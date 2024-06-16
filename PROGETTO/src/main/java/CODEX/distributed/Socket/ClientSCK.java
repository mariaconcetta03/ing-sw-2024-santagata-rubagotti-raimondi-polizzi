package CODEX.distributed.Socket;

import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.*;
import CODEX.utils.executableMessages.clientMessages.*;
import CODEX.view.GUI.*;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This class represents the Client who choose TCP as network protocol.
 * It receives SCKMessages sent by the ClientHandlerThread through the socket stream
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK implements ClientGeneralInterface {
    private List<Pawn> availableColors;
    private boolean aDisconnectionHappened=false;
    private final Object disconnectionLock=new Object();
    private boolean errorState = false;
    private HashSet<Integer> lobbyId;
    private final Socket socket;
    private GUIGameController guiGameController=null;
    private int lastMoves=10;
    private final Object guiGamestateLock=new Object();
    private final Object guiPawnsControllerLock=new Object();
    private boolean done=false;
    private GUIPawnsController GUIPawnsController=null;
    private final Object guiBaseCardControllerLock=new Object();
    private GUIBaseCardController guiBaseCardController=null;
    private final Object guiObjectiveControllerLock=new Object();
    private GUIObjectiveController guiObjectiveController=null;
    private Player personalPlayer;
    private int selectedView;
    private InterfaceTUI tuiView;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;
    private Integer gameID;
    private Boolean running;
    private Boolean responseReceived;
    public final Object actionLock;
    private final Object inputLock;
    private boolean isPlaying;
    private boolean inGame;
    private boolean pongReceived; // to check the connection
    private Timer timer;
    private Scanner sc;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;
    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private BufferedReader console;
    private int turnCounter = -1;
    private final Object outputLock;
    private boolean nicknameSet = false;
    private final Object guiLock;
    private GUILobbyController guiLobbyController;


    /*
    WARNING: if you call a ClientActionsInterface method inside an update method you must necessarily
    use a thread because the ClientActionsInterface methods wait for the return OK that cannot be read
    by the ClientSCK if you are still stuck on the update that called a ClientActionsInterface method.
    This happens because to do the updates in order they are read one at a time.
    */


    /**
     * Class constructor
     * we call this constructor after we ask the IP address and the port of the server
     * @param serverAddress is the IP address of the server
     * @throws IOException if there is a problem with input stream and output stream
     */
    public ClientSCK(String serverAddress) throws IOException {
        this.socket = new Socket();
        int port = 1085; // server's port
        SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);
        socket.connect(socketAddress);
        Scanner sc= new Scanner(System.in);

        lobbyId = new HashSet<>();

        personalPlayer = new Player();
        this.inputLock = new Object();

        this.guiLock=new Object();


        // in this way the stream is converted into objects
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());

        this.running = true;
        this.inGame=false; // this will become true when the state of the Game will change in STARTED

        // initialized false to enter the while inside every ClientGeneralInterface method
        this.responseReceived = false;

        this.actionLock = new Object();
        this.outputLock = new Object();
        new Thread(() -> {
            while (running) {
                synchronized (inputLock) {
                    if (!aDisconnectionHappened) {
                        try {
                            SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject();
                            if (sckMessage != null) {
                                // we are not using a thread because the updates are executed in order
                                modifyClientSide(sckMessage);
                            }

                        } catch (Exception e) { // if the server disconnects @TODO non Exception generica
                            if(running) {

                                    handleDisconnection();

                            }
                        }
                    }
                }
            }
        }).start();
    }



    /**
     * Settings class
     */
    public static class Settings {
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST


        /**
         * Default constructor
         */
        public Settings (){}



        /**
         * Setter method
         * @param serverName server address
         */
        public static void setServerName(String serverName) {
            SERVER_NAME = serverName;
        }
    }





    /**
     * This method is useful for RMI
     * @param nickname player's nickname
     */
    @Override
    public void okEventExecute(String nickname) {
    }



    /**
     * This method allows the Client to send, through the socket, a message to be read (using its input stream)
     * by the ClientHandlerThread
     * @param sckMessage is the message containing objects and Event relative to the action to perform
     * @throws IOException in case the Server is unreachable we shut down the Client.
     */
    public void sendMessage(SCKMessage sckMessage) throws IOException {
        // this method is called ONLY inside ClientGeneralInterface methods
        synchronized (outputLock) {
            if (!aDisconnectionHappened) {
                try {
                    responseReceived = false;
                    errorState = false;
                    outputStream.writeObject(sckMessage);
                    outputStream.flush();
                    outputStream.reset();
                } catch (IOException e) {
                    aDisconnectionHappened=true;
                    responseReceived = true; // not to start a wait for other answers
                    handleDisconnection();
                }
            }
        }
    }



    /**
     * If the Event attribute of the sckMessage is not null it is an update,
     * otherwise it is a ServerMessage (ServerMessage attribute of the sckMessage)
     * If the Event attribute of the sckMessage is not null the execute method will modify the local
     * attributes of the client.
     * @param sckMessage represents an event that has to be executed
     */
    public void modifyClientSide(SCKMessage sckMessage) {
        if (sckMessage.getEvent() != null) { // we have an update
            sckMessage.getEvent().executeSCK(this);
        }else{ // we have a ServerMessage
            // here we unlock the client that has been waiting for a server response
            sckMessage.getServerMessage().execute(this);
        }

    }



    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() {
        this.isPlaying=false;
        this.sc=new Scanner(System.in);
        this.console = new BufferedReader(new InputStreamReader(System.in));
        boolean ok=false;
        if (selectedView == 1) {
            tuiView = new InterfaceTUI();
            tuiView.printWelcome();
            String nickname=null;
            while(!ok){
                nickname = tuiView.askNickname(sc);
                this.chooseNickname(nickname);
                ok=true;
                if(errorState&&!aDisconnectionHappened){
                    System.out.println("Nickname is already taken! Please try again.");
                    errorState=false;
                    ok=false;
                }else if(aDisconnectionHappened){
                    handleDisconnection();
                }
            }
            personalPlayer.setNickname(nickname);
            System.out.println("Nickname correctly selected!");

            this.checkAvailableLobby();
            printLobby(lobbyId);
            ok=false;

            int gameSelection=0;
           // while(!ok) {
           //     boolean secondOk=false;
                while (!ok) {
                    System.out.println("Type -1 if you want to create a new lobby, or the lobby id if you want to join it (if there are any available)");
                    System.out.println("Type -2  to refresh the available lobbies.");
                    try {
                        gameSelection = sc.nextInt();
                        if (gameSelection == -2) {
                            this.checkAvailableLobby();
                            if (!(lobbyId.isEmpty())) {
                                System.out.println("If you want you can join an already created lobby. These are the ones available:");
                                printLobby(lobbyId);
                            } else {
                                System.out.println("There are no lobby available");
                            }
                        } else if ((gameSelection != -1) && (!lobbyId.contains(gameSelection))) {
                            System.out.println("You wrote a wrong ID, try again.");
                        } else {
                            ok = true;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                        sc.next();
                    }
                }

                ok=false;

                    //boolean thirdOk=false;
            while(!ok){
                    if (gameSelection == -1) {
                        System.out.println("How many players would you like to join you in this game?");
                        while (!ok) {
                            try {
                                gameSelection = sc.nextInt();
                                if((gameSelection<=4)&&(gameSelection>=2)) {
                                    ok = true;
                                }else{
                                    System.out.println("Invalid number of players. Type a number between 2-4.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                                sc.next();
                            }
                        }
                        createLobby(personalPlayer.getNickname(), gameSelection);
                        System.out.println("Successfully created a new lobby with id: " + this.gameID);
                        //ok=true;
                    } else if (lobbyId.contains(gameSelection)) {
                        //try {
                            System.out.println("Joining the " + gameSelection + " lobby...");
                            addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                            if (errorState&&!aDisconnectionHappened) {
                                System.out.println(ANSIFormatter.ANSI_RED + "The game you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                                errorState = false;
                            }else if(aDisconnectionHappened){
                                handleDisconnection();
                            } else {
                                System.out.println("Successfully joined the lobby with id: " + this.gameID);
                                ok=true;
                                checkNPlayers(); // this method in the server side makes the game start
                            }
                    } else {
                        System.out.println("You wrote a wrong id, try again!");
                    }

            }
        }
    }



    /**
     * This method let the client know which are the lobbies availables and
     * it is used in the TUI
     * @param ids are the indexes of the available lobbies
     */
    public void printLobby(HashSet<Integer> ids){
        if(!ids.isEmpty()) {
            for (Integer i : ids) {
                System.out.println("ID: " + i);
            }
        }else{
            System.out.println("There are no lobby available.");
        }
    }



    /**
     * This method checks the available lobbies at this moment, sending a message to the server
     */
    public void checkAvailableLobby(){
        synchronized (actionLock) {
            lobbyId=new HashSet<>();
            ClientMessage clientMessage= new ClientAvailableLobbies();
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method checks the available colors (pawns) at this moment, sending a message to the server
     */
    public void checkAvailableColors(){
        synchronized (actionLock) {
            ClientMessage clientMessage= new ClientAvailableColors();
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method checks the number of players in a lobby (serverside)
     * if that's complete, then it starts the game.
     */
    public void checkNPlayers(){
        synchronized (actionLock) {
            ClientMessage clientMessage=new CheckNPlayers();
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived){ //in realtà qua non avremmo bisogno di aspettare la risposta
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }






    // METHODS OVERRIDDEN FROM clientGeneralInterface
    /**
     * This method is used to add a single player to an already created lobby
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
    */
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new AddPlayerToLobby(playerNickname,gameId);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived){
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param nickname is the String he wants to put as his nickname
     */
    @Override
    public void chooseNickname(String nickname)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new ChooseNickname(nickname);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new CreateLobby(creatorNickname,numOfPlayers);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position     the position where the Player wants to play the Card
     * @param orientation  the side on which the Player wants to play the Card
     */
    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new PlayCard(nickname,selectedCard,position,orientation);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("usciti dalla wait di playCard");
        }
    }



    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     * @param nickname    is the nickname of the Player that wants to play a card
     * @param baseCard    is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
      */
    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new PlayBaseCard(nickname,baseCard,orientation);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname     is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     */
    @Override
    public void drawCard(String nickname, PlayableCard selectedCard)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new DrawCard(nickname,selectedCard);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard    is the ObjectiveCard the player selected
     */
    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new ChooseObjectiveCard(chooserNickname,selectedCard);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }



    /**
     * This method allows a player to choose the color of his pawn
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor   is the color chosen by the player
      */
    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) {
        synchronized (actionLock) {
            ClientMessage clientMessage=new ChoosePawnColor(chooserNickname,selectedColor);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method sends a message from the sender to the receiver(s)
     * @param senderNickname     is the nickname of the player sending the message
     * @param receiversNickname is the list of nicknames of the players who need to receive this message
     * @param message            is the string sent by the sender to the receivers
      */
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message)  {
        synchronized (actionLock) {
            ClientMessage clientMessage=new SendMessage(senderNickname,receiversNickname,message);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * This method lets a player end the game (volontary action or involontary action - connection loss)
     * @param nickname of the player who is going leave the game
     */
    @Override
    public void leaveGame(String nickname) {
     synchronized (disconnectionLock) {
            if (!aDisconnectionHappened) {
                System.out.println("game left.");
                try { //we close all we have to close
                    running = false;
                    inGame = false;
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException ex) { //needed for the close clause
                    throw new RuntimeException(ex);
                }
                if (timer != null) {
                    timer.cancel(); // Ferma il timer
                }
                System.exit(0); //status 0 -> no errors
            }

        }
    }





    // METHODS OVERRIDDEN FROM CLIENTGENERALINTERFACE

    /**
     * This is an update method
     * @param board the new board we want to update
     * @param boardOwner is the player which possesses the board
     * @param newCard the last card placed
     */
    @Override
    public void updateBoard(String boardOwner, Board board, PlayableCard newCard)  {
        // we have to change the view and the local model
        if (boardOwner.equals(personalPlayer.getNickname())) {
            System.out.println("I received the board.");
            personalPlayer.setBoard(board);
            InterfaceTUI t = new InterfaceTUI();
            t.printTable(board);
        }

        for (Player p : playersInTheGame) {
            if (boardOwner.equals(p.getNickname())) {
                p.setBoard(board);
                System.out.println("I received " + p.getNickname() + "'s board.");
            }
        }

        if (selectedView == 2) {
            if (guiGameController != null) {

                guiGameController.updateBoard(boardOwner, newCard);
            }
        }
    }



    /**
     * This is an update method
     * @param resourceDeck the new deck we want to update
     */
    @Override
    public void updateResourceDeck(PlayableDeck resourceDeck) {
        // we have to change the view and the local model
        this.resourceDeck=resourceDeck;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceDeck.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceDeck();
            }
        }
    }



    /**
     * This is an update method
     * @param goldDeck the new deck we want to update
     */
    @Override
    public void updateGoldDeck(PlayableDeck goldDeck)  {
        // we have to change the view and the local model
        this.goldDeck=goldDeck;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldDeck.");
        } else if (selectedView == 2) {
            if(guiGameController!=null){
                guiGameController.updateGoldDeck();
            }
        }
    }



    /**
     * This is an update method
     * @param playerNickname the player which deck is updated
     * @param playerDeck     the new deck we want to update
     */
    @Override
    public void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck)  {
        //we have to change the view and the local model
        System.out.println("I received the updated "+playerNickname+"'s deck.");
        if(playerNickname.equals(personalPlayer.getNickname())){
            personalPlayer.setPlayerDeck(playerDeck);
        }

        for (Player p : playersInTheGame) {
            if (playerNickname.equals(p.getNickname())) {
                p.setPlayerDeck(playerDeck);
            }
        }

       if (selectedView == 2) {

            if(!(playerNickname.equals(personalPlayer.getNickname()))){
                if(guiGameController!=null){
                    guiGameController.updatePlayerDeck(playerNickname,playerDeck);
                }
            }
        }
    }



    /**
     * This is an update method
     * @param card     is the personal objective card
     * @param nickname is the owner of the personal objective card
     */
    @Override
    public void updatePersonalObjective(ObjectiveCard card, String nickname)  {
        if (personalPlayer.getNickname().equals(nickname)) {
            personalPlayer.addPersonalObjective(card);
            if (personalPlayer.getPersonalObjectives().size() == 2) {
                    if (selectedView == 1) {
                        System.out.println("I received the updatePersonalObjective.");

                        new Thread(()->{
                            boolean ok = false;
                            while (!ok) {
                                tuiView.printHand(personalPlayer.getPlayerDeck());
                                try {
                                    ObjectiveCard tmp=tuiView.askChoosePersonalObjective(sc, personalPlayer.getPersonalObjectives());
                                    chooseObjectiveCard(personalPlayer.getNickname(),tmp);
                                    ok = true;
                                    personalPlayer.setPersonalObjective(tmp);
                                    System.out.println("You've correctly chosen your objective card!");
                                    checkObjectiveCardChosen();
                                }catch (CardNotOwnedException e){
                                    System.out.println("You don't own this card.");
                                }
                            }

                        }).start();;

                    } else if (selectedView == 2) {
                        guiBaseCardController.updateGameState();
                    }
            }
        }
    }



    /**
     * This method is called when all the player chose their personal objective card to let the
     * client know the set-up fase has finished.
     */
    @Override
    public void finishedSetUpPhase() {
        updateRound(playersInTheGame);
    }



    /**
     * This method prints the winners of the game in the TUI
     * @param finalScoreBoard is a Map containing all the players' nicknames as values and as keys their positions
     */
    @Override
    public void showWinner(Map<Integer, List<String>> finalScoreBoard)  {
        errorState=false;
        synchronized (actionLock){
            actionLock.notify(); //to stop the waiting of something that will never arrive
        }
        if(selectedView==1) { //TUI
            Map<String, Player> players=new HashMap<>();
            for(Player p: playersInTheGame){
                players.put(p.getNickname(), p);
            }
            boolean printed=false;

            for(String s: finalScoreBoard.get(1)){
                if(s.equals(personalPlayer.getNickname())){
                    tuiView.printWinner(true);
                    printed=true;
                }
            }
            if(!printed){
                tuiView.printWinner(false);
            }
            System.out.println();
            System.out.println(ANSIFormatter.ANSI_WHITE_BACKGROUND+ANSIFormatter.ANSI_BLACK+"----- This is the final scoreboard -----"+ANSIFormatter.ANSI_RESET);

            for(Integer i: finalScoreBoard.keySet()) {
                for (String s : finalScoreBoard.get(i)) {
                    System.out.println(ANSIFormatter.ANSI_RED+i + "_ "+ANSIFormatter.ANSI_RESET + s+" with "+players.get(s).getPoints()+" points and "+players.get(s).getNumObjectivesReached()+" objectives reached.");
                }
            }
            Executor executor= Executors.newSingleThreadExecutor();
            executor.execute(()->{System.exit(-1);});

        }
        if(selectedView==2){ //GUI
            if(guiGameController!=null){
                Map<String, Player> players=new HashMap<>();
                for(Player p: playersInTheGame){
                    players.put(p.getNickname(), p);
                }
                List<Player> winners=new ArrayList<>();

                for (String s : finalScoreBoard.get(1)) {
                    winners.add(players.get(s));
                }
                guiGameController.updateWinners(winners);
            }
        }
    }



    /**
     * This is an update method
     * @param lastMoves is the number of turns remaining before the game ends.
     */
    @Override
    public void updateLastMoves(int lastMoves)  {
        this.lastMoves=lastMoves;
        System.out.println("LAST MOVES "+this.lastMoves);
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    @Override
    public void updateResourceCard1(PlayableCard card)  {
        // we have to change the view and the local model
        this.resourceCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard1.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceCard1(card);
            }
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    @Override
    public void updateResourceCard2(PlayableCard card)  {
        // we have to change the view and the local model
        this.resourceCard2=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard2.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceCard2(card);
            }
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    @Override
    public void updateGoldCard1(PlayableCard card)  {
        // we have to change the view and the local model
        this.goldCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldCard1.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateGoldCard1(card);
            }
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    @Override
    public void updateGoldCard2(PlayableCard card) {
        // we have to change the view and the local model
        this.goldCard2=card;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldCard2.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateGoldCard2(card);
            }
        }
    }



    /**
     * This is an update method
     * @param chatIdentifier ID
     * @param chat which needs to be updated
     */
    @Override
    public void updateChat(Integer chatIdentifier, Chat chat) {
        // we have to change the view and the local model
        if (selectedView == 1) {
            System.out.println("You received a message (updateGoldCard2).");
        }
    }



    /**
     * This is an update method
     * @param nickname is the nickname of the player who selected a new pawn color
     * @param pawn is the selected color
     */
    @Override
    public void updatePawns(String nickname, Pawn pawn) {
        if(nickname.equals(personalPlayer.getNickname())){
            personalPlayer.setColor(pawn);
        }
        for(Player p: playersInTheGame){
            if(p.getNickname().equals(nickname)){
                p.setColor(pawn);
            }
        }
        if (selectedView == 1) {
            System.out.println("I received the updatePawns.");
        } else if (selectedView == 2) {


            synchronized (guiPawnsControllerLock) {

                while (GUIPawnsController == null) {

                    try {
                        guiPawnsControllerLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            GUIPawnsController.updatePawns(pawn);
        }
    }



    /**
     * This is an update method
     * @param newPlayingOrder are the players of the game ordered
     */
    @Override
    public void updateRound(List<Player> newPlayingOrder)  { //taken from RMIClient
        System.out.println("I received the updateRound.");
        playersInTheGame = newPlayingOrder;
        if (selectedView == 1) { //TUI
            // when turnCounter==-1 we have to initialize this list
            if (this.turnCounter == 0) { // we enter here only one time: the second time that updateRound is called
                // the second time that updateRound is called we have all that is need to
                // call playBaseCard (see the model server side)
                new Thread(() -> {

                        boolean choice = tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]);
                        playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0], choice);
                        checkBaseCardPlayed();

                }).start();
            }
            if (this.turnCounter >= 1) { // we enter here from the third time included that updateRound is called
                // before starting the thread that prints the menu we communicate which is the player that is playing
                if(lastMoves>0){
                    if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                        setIsPlaying(true);
                        System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                        if(lastMoves<=playersInTheGame.size()){
                            System.out.println("This is your last turn! You will not draw.");
                        }
                    } else {
                        setIsPlaying(false);
                        System.out.println(ANSIFormatter.ANSI_BLUE+playersInTheGame.get(0).getNickname() + " is playing!"+ANSIFormatter.ANSI_RESET);
                    }
                }else{
                    inGame=false;
                }
                if (this.turnCounter == 1) { // we enter here the third time (finishedSetupPhase2())
                    // we have to start the thread that prints the menu
                    new Thread(() -> {
                        while (inGame) {
                            showMenuAndWaitForSelection();
                        }
                    }).start();
                }
            }
            if(this.turnCounter==-1){
                Executor executor= Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    boolean ok=false;

                        while (!ok) {
                            Pawn selection = tuiView.askPawnSelection(getAvailableColors(),sc);
                            if (selection != null) {
                                this.choosePawnColor(personalPlayer.getNickname(), selection);
                                if(errorState&&!aDisconnectionHappened){
                                    System.out.println("This color is already taken! Please try again.");
                                    errorState=false;
                                }else if(aDisconnectionHappened){
                                    handleDisconnection();
                                }
                                else {
                                    ok = true;
                                    System.out.println("Pawn color correctly selected!");
                                    checkChosenPawnColor();
                                }
                            } else {
                                System.out.println("Please insert one of the possible colors!");
                            }
                        }

                });


            }
            turnCounter++;
            //first time: -1 -> 0, second time 0 -> 1  so from the second time on we enter if(turnCounter>=1)
        }
        else if (selectedView == 2) { //GUI
            System.out.println("I received the updateRound.");
            //playersInTheGame = newPlayingOrder;
            if(this.turnCounter == -1){
                synchronized (guiGamestateLock) {
                    while (guiLobbyController == null) {
                        try {
                            guiGamestateLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                guiLobbyController.updateGameState();
            }
            if (this.turnCounter == 0){
                GUIPawnsController.updateGameState();
            }
            if (this.turnCounter >= 1){

                if(lastMoves>0) {

                    if (lastMoves <= playersInTheGame.size()) {
                        System.out.println("This is your last turn! You will not draw.");
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(true);
                        }
                    }else {
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(false);
                        }
                    }
                }else{
                    inGame=false;
                }
                if (this.turnCounter == 1){ // 3rd turn
                    guiObjectiveController.updateGameState();
                }
            }
            turnCounter++;
        }
    }



    /**
     * This is an update method
     * @param card1 is the first common Objective
     * @param card2 is the second common Objective
     */
    @Override
    public void updateCommonObjectives(ObjectiveCard card1, ObjectiveCard card2){
        System.out.println("I received the updateCommonObjectives.");
        this.commonObjective1=card1;
        this.commonObjective2=card2;
    }



    /**
     * This is an update method
     * @param gameState is the new Game state (WAITING_FOR_START -> STARTED -> ENDING -> ENDED)
     */
    @Override
    public void updateGameState(Game.GameState gameState)  {
        // we have to change the view and the local model
        System.out.println("I received the updateGameState.");
        if (selectedView == 1) {
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame=true;
                System.out.println("The game has started!");


                //to check the connection
                this.pongReceived=true; // initialization
                this.timer = new Timer(true); // isDaemon==true -> maintenance activities performed
                // as long as the application is running
                // we need to use ping-pong messages because sometimes the connection seems to
                // be open (we do not receive any I/O exception) but it is not.
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(pongReceived) {
                            pongReceived=false;
                            try {
                                ClientMessage clientMessage=new ClientPing();
                                sendMessage(new SCKMessage(clientMessage));
                            } catch (IOException e) { //
                                // the connection doesn't is open (and it doesn't seem to be open)

                                    handleDisconnection();

                            }
                        }else{
                            //there are no pongs received


                                handleDisconnection();

                        }
                    }
                }, 0, 10000);

            }
        } else if (selectedView == 2) {
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame = true;
                System.out.println("The game has started!");
            }
        }
    }



    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     */
    @Override
    public void handleDisconnection()  {
        System.out.println("evento disconnessione arrivato");

        synchronized (disconnectionLock) {
            if (selectedView == 1) { //TUI
                if(lastMoves!=0) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
            } else if (selectedView == 2) {
                aDisconnectionHappened = true;
                synchronized (actionLock){
                    actionLock.notify(); //to stop the waiting of something that will never arrive
                }
                if(GUIPawnsController==null){ //the game has not started yet
                    handleDisconnectionFunction();
                }
            }
        }

    }



    /**
     * This method is used in RMI Client
     * @throws RemoteException when there's a problem in the communication
     */
    @Override
    public void heartbeat() throws RemoteException {
    }



    /**
     * This method is used in RMI Client
     * @throws RemoteException when there's a problem in the communication
     */
    @Override
    public void startHeartbeat() throws RemoteException {
    }



    /**
     * This method is used in the TUI: it shows the menu and waits for the user to select
     * an option of the actions that he can do (the waiting isn't blocking)
     */
    private void showMenuAndWaitForSelection(){
        if(selectedView==1) {
            int intChoice=tuiView.showMenuAndWaitForSelection(this.getIsPlaying(),this.console);
            boolean ok;
            if(intChoice!=-1) {
                    switch (intChoice) {
                        case 0:
                            System.out.println("Are you sure to LEAVE the game? Type 1 if you want to leave any other character to return to the game.");
                            try {
                                if (sc.nextInt() == 1) {
                                    inGame = false;
                                    leaveGame(personalPlayer.getNickname());
                                }
                            } catch (InputMismatchException ignored) {
                            }
                            break;
                        case 1:
                            tuiView.printHand(personalPlayer.getPlayerDeck());
                            break;
                        case 2:
                            List<ObjectiveCard> list = new ArrayList<>();
                            list.add(commonObjective1);
                            list.add(commonObjective2);
                            list.add(personalPlayer.getPersonalObjective());
                            tuiView.printObjectiveCard(list);
                            break;
                        case 3:
                            List<PlayableCard> tmp = new ArrayList<>();
                            tmp.add(resourceCard1);
                            tmp.add(resourceCard2);
                            tmp.add(goldCard1);
                            tmp.add(goldCard2);
                            tuiView.printDrawableCards(goldDeck, resourceDeck, tmp);
                            break;
                        case 4:
                            ok = false;
                            System.out.println("Which player's board do you want to see?");
                            String nickname = sc.next();
                            for (Player player : playersInTheGame) {
                                if (player.getNickname().equals(nickname)) {
                                    ok = true;
                                    tuiView.printTable(player.getBoard());
                                }
                            }
                            if (!ok) {
                                System.out.println("There is no such player in this lobby! Try again.");
                            }
                            break;
                        case 5:
                            List<Player> tmpList = new ArrayList<>(playersInTheGame);
                            tuiView.printScoreBoard(tmpList);
                            break;
                        case 6: tuiView.printLegend();
                            break;
                        case 7: System.out.println(ANSIFormatter.ANSI_BLUE+"It's "+playersInTheGame.get(0).getNickname()+"'s turn!"+ANSIFormatter.ANSI_RESET);
                            break;
                        case 8:
                            System.out.println("Do you want to send a message to everybody (type 1) or a private message (type the single nickname)?");
                            String answer = sc.next();
                            if (answer.equals("1")) {
                                List<String> receivers = new ArrayList<>();
                                for (Player p : playersInTheGame) {
                                    if (!p.getNickname().equals(personalPlayer.getNickname())) {
                                        receivers.add(p.getNickname());
                                    }
                                }
                                System.out.println("Now type the message you want to send: ");
                                answer = sc.nextLine();
                                sendMessage(personalPlayer.getNickname(), receivers, answer);
                            } else {
                                ok = false;
                                for (Player p : playersInTheGame) {
                                    if (p.getNickname().equals(answer)) {
                                        ok = true;
                                    }
                                }
                                if (ok) {
                                    List<String> receivers = new ArrayList<>();
                                    receivers.add(answer);
                                    System.out.println("Now type the message you want to send: ");
                                    answer = sc.nextLine();
                                    sendMessage(personalPlayer.getNickname(), receivers, answer);
                                } else {
                                    System.out.println("There is no such player in this lobby!");
                                }
                            }
                            break;
                        case 9:
                            break;
                        case 10:
                            boolean orientation = true;
                            PlayableCard card = null;
                            Coordinates coordinates;
                            card = tuiView.askPlayCard(sc, personalPlayer);
                            if (card != null) {
                                orientation = tuiView.askCardOrientation(sc);
                                coordinates = tuiView.askCoordinates(sc, card, personalPlayer.getBoard());
                                if (coordinates != null) {
                                    if(coordinates.getX()!=-1) {
                                        this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                        if ((!errorState) && (lastMoves > playersInTheGame.size()) && !aDisconnectionHappened) {
                                            tmp = new ArrayList<>();
                                            tmp.add(resourceCard1);
                                            tmp.add(resourceCard2);
                                            tmp.add(goldCard1);
                                            tmp.add(goldCard2);
                                            card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                            this.drawCard(personalPlayer.getNickname(), card);
                                        } else if (aDisconnectionHappened && lastMoves!=0) {
                                            handleDisconnection();
                                        }else{
                                            System.out.println("You can't play this card! Returning to menu...");
                                            errorState=false; //to be used the next time
                                    }
                                    }
                                } else {
                                    System.out.println("The coordinates are null");
                                }
                            } else {
                                System.out.println("The card is null");
                            }
                            break;
                        default:
                            System.out.println("Functionality not yet implemented");
                    }

            }
        }
    }



    /**
     * This method manages disconnections
     */
    public void handleDisconnectionFunction(){
        // TUI + GUI
        running=false;
        inGame=false;
        try {
            if(inputStream!=null) {
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
            if(socket!=null&&!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) { // needed for the close clause
            throw new RuntimeException(e);
        }
        // the TimerTask that checks the connection should end by itself when the application ends
        if(this.timer!=null) {
            this.timer.cancel(); //to be sure
        }
        System.out.println("A disconnection happened.");

        System.exit(0);

    }





    // STANDARD METHODS OF THE CLASS

    /**
     * This method checks if everybody has chosen his pawn
     */
    public void checkChosenPawnColor(){
        ClientMessage clientMessage=new CheckChosenPawnColor();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * This method checks if everybody has chosen his objective
     */
    public void checkObjectiveCardChosen(){
        ClientMessage clientMessage=new CheckObjectiveCardChosen();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * This method checks if everybody has chosen his base card
     */
    public void checkBaseCardPlayed(){
        ClientMessage clientMessage=new CheckBaseCardPlayed();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * Getter method
     * @return guiGamestateLock
     */
    public Object getGuiGamestateLock() {
        return guiGamestateLock;
    }



    /**
     * Getter method
     * @return true if a disconnection happened, false otherwise
     */
    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
    }



    /**
     * Getter method
     * @return guiPawnsControllerLock
     */
    public Object getGuiPawnsControllerLock() {
        return this.guiPawnsControllerLock;
    }



    /**
     * Getter method
     * @return guiBaseCardControllerLock
     */
    public Object getGuiBaseCardControllerLock() {
        return this.guiBaseCardControllerLock;
    }



    /**
     * Getter method
     * @return the gui objective controller lock
     */
    public Object getGuiObjectiveControllerLock() {
        return this.guiObjectiveControllerLock;
    }



    /**
     * Getter method
     * @return a list of available colors
     */
    public List<Pawn> getAvailableColors(){
        checkAvailableColors();
        return this.availableColors;
    }



    /**
     * Getter method
     * @return true or false if the player is playing or not
     */
    public boolean getIsPlaying(){
        return this.isPlaying;
    }



    /**
     * Getter method
     * @return error state
     */
    public boolean getErrorState(){
        return this.errorState;
    }



    /**
     * Getter method
     * @return CommonObjective1
     */
    public ObjectiveCard getCommonObjective1() {
        return commonObjective1;
    }



    /**
     * Getter method
     * @return CommonObjective2
     */
    public ObjectiveCard getCommonObjective2() {
        return commonObjective2;
    }



    /**
     * Getter method
     * @return PersonalPlayer
     */
    public Player getPersonalPlayer() {
        return personalPlayer;
    }



    /**
     * Getter method
     * @return ResourceCard1
     */
    public PlayableCard getResourceCard1() {
        return resourceCard1;
    }



    /**
     * Getter method
     * @return ResourceCard2
     */
    public PlayableCard getResourceCard2() {
        return resourceCard2;
    }



    /**
     * Getter method
     * @return goldCard1
     */
    public PlayableCard getGoldCard1() {
        return goldCard1;
    }



    /**
     * Getter method
     * @return goldCard2
     */
    public PlayableCard getGoldCard2() {
        return goldCard2;
    }



    /**
     * Getter method
     * @return goldDeck
     */
    public PlayableDeck getGoldDeck() {
        return goldDeck;
    }



    /**
     * Getter method
     * @return resourceDeck
     */
    public PlayableDeck getResourceDeck() {
        return resourceDeck;
    }



    /**
     * Getter method
     * @return playersInTheGame
     */
    public List<Player> getPlayersInTheGame(){
        return this.playersInTheGame;
    }



    /**
     * Getter method
     * @return the lobby ids of the available lobbies
     */
    public HashSet<Integer> getAvailableLobbies() {
        checkAvailableLobby(); // update in the clientsck
        return lobbyId;
    }



    /**
     * Setter method
     * @param nickname is the nickname we want to set
     * @return true if the nickname is set correctly
     */
    public boolean setNickname(String nickname) {
        this.personalPlayer.setNickname(nickname);
        this.nicknameSet = true;
        if (errorState&&!aDisconnectionHappened){
            this.nicknameSet = false;
        }else if(aDisconnectionHappened){

                handleDisconnection();

        }

        System.out.println("il nickname è stato settato a: " + this.personalPlayer.getNickname());
        return this.nicknameSet;
    }



    /**
     * Setter method
     * @param received true if it is received, false otherwise
     */
    public void setPongReceived(Boolean received){ //to be used in ServerPong
        this.pongReceived=received;
    }



    /**
     * Setter method
     * @param list of available id
     */
    public void setLobbyId(List<Integer>list){
        lobbyId.addAll(list);
    }



    /**
     * Setter method
     * @param responseReceived true if it is received, false otherwise
     */
    public void setResponseReceived(boolean responseReceived){
        this.responseReceived=responseReceived;
    }



    /**
     * Setter method
     * @param guiGameController is the GUI game controller (for the main match window)
     */
    public void setGuiGameController(GUIGameController guiGameController) {
        this.guiGameController=guiGameController;
    }



    /**
     * Setter method
     * @param guiLobbyController is the lobby controller of the GUI
     */
    public void setGuiLobbyController(GUILobbyController guiLobbyController) {
        this.guiLobbyController = guiLobbyController;
    }



    /**
     * Setter method
     * @param done true if it is received, false otherwise
     */
    public void setDone(boolean done) {
        this.done = done;
    }



    /**
     * Setter method
     * @param ctr controller of the GUI's window of the pawns
     */
    public void setGuiPawnsController(GUIPawnsController ctr) {
        this.GUIPawnsController=ctr;
    }



    /**
     * Setter method
     * @param ctr is the GUI controller
     */
    public void setGuiBaseCardController(GUIBaseCardController ctr) {
        this.guiBaseCardController=ctr;
    }



    /**
     * Setter method
     * @param ctr is the GUI controller
     */
    public void setGuiObjectiveController(GUIObjectiveController ctr) {
        this.guiObjectiveController=ctr;
    }



    /**
     * Setter method
     * @param selectedView the index related to the selected view
     */
    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }



    /**
     * Setter method
     * @param availableColors tells what colors a player can choose
     */
    public void setAvailableColors(List<Pawn> availableColors){
        this.availableColors=availableColors;
    }



    /**
     * Setter method
     * @param isPlaying tells about the status of a player
     */
    public void setIsPlaying(boolean isPlaying){
        this.isPlaying=isPlaying;
    }



    /**
     * Setter method
     * @param errorState if there's an error, then it is true, false otherwise
     */
    public void setErrorState (boolean errorState) {
        this.errorState = errorState;
    }



    /**
     * Setter method
     * @param gameID int ID of the game
     */
    public void setGameID(Integer gameID){
        this.gameID=gameID;
    }

}