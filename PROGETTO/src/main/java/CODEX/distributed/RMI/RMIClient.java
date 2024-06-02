package CODEX.distributed.RMI;

import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.*;

import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;


// ----------------------------------- H O W   I T   W O R K S ----------------------------------------
// RMI CLIENT CALLS THE REMOTE METHODS THROUGH THE RMISERVER INTERFACE (WHICH RMI CLIENT HAS INSIDE)
// THE RMI SERVER GOES TO MODIFY THE MODEL (UPON CLIENT REQUEST) THROUGH THE CONTROLLER.
// TO UNDERSTAND WHETHER AN EVENT WAS SUCCESSFUL OR NOT, YOU NEED TO CONSULT THE ATTRIBUTE
// "lastEvent" PRESENT IN THE GAME CLASS.
// FOR THE METHODS OF THE GAMECONTROLLER, THE CLIENT INVOKES THEM DIRECTLY BY PASSING THROUGH THE
// GAMECONTROLLER (WITHOUT INVOKING THE SERVER). THE GAME CONTROLLER IS PASSED TO THE CLIENT BY THE
// METHODS "startLobby" AND "addPlayerToLobby"
// ----------------------------------------------------------------------------------------------------



public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface{
    private final Object guiLock;
    private final Object printLock;
    private boolean finishedSetup=false;
    private static final int HEARTBEAT_INTERVAL = 5; // seconds
    private static final int TIMEOUT = 10; // seconds
    private ScheduledExecutorService schedulerToSendHeartbeat;
    ScheduledExecutorService schedulerToCheckReceivedHeartBeat;
    private long lastHeartbeatTime;
    private ServerRMIInterface SRMIInterface; //following the slides' instructions

    ExecutorService executor;
    private BufferedReader console;

    Scanner sc;
    int turnCounter=-1;
    private GameControllerInterface gameController = null;
    // given to the client when the game is started (lobby created or player joined to a lobby))

    private int selectedView; // 1==TUI, 2==GUI
    private InterfaceTUI tuiView;
    //private GUI; :O INTERFACCIA PERò...
    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;

    public Player getPersonalPlayer() {
        return personalPlayer;
    }

    private Player personalPlayer;



    private List<Player> playersInTheGame;

    public ObjectiveCard getCommonObjective1() {
        return commonObjective1;
    }

    public ObjectiveCard getCommonObjective2() {
        return commonObjective2;
    }

    private ObjectiveCard commonObjective1, commonObjective2;
    private boolean inGame=false;
    private boolean isPlaying= false;
    private boolean baseCard= false;
    private boolean nicknameSet = false;
    private List<ChatMessage> messages;


    public GameControllerInterface getGameController() {
        return gameController;
    }

    public boolean setNickname(String nickname) {
        try {
            chooseNickname(nickname);
            this.personalPlayer.setNickname(nickname);
            this.nicknameSet = true;
        } catch (NicknameAlreadyTakenException e) {
            this.nicknameSet = false;
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("il nickname è stato settato a: " + this.personalPlayer.getNickname());
        return this.nicknameSet;
    }



    /**
     * Class constructor
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public RMIClient() throws RemoteException {
        this.printLock = new Object();
        this.guiLock=new Object();
        personalPlayer= new Player();
        messages=new ArrayList<>();
    }


    /**
     * Settings class
     * It is about port and ip address of the server which the client needs to communicate with
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 standard port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }




    // ---- M E T H O D S   F R O M   C L I E N T   T O   S E R V E R ----

    /**
     * This method gets the SRMIInterface (Server Interface) from the registryServer
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    public void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException {
        Registry registryServer = null;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT); // getting the registry
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
    }




    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException { //exceptions added automatically
        this.gameController = this.SRMIInterface.createLobby(creatorNickname, numOfPlayers);

        ClientGeneralInterface client = this;
        gameController.addRMIClient(this.personalPlayer.getNickname(), client);
    }



    /**
     * This method is used to add a single player to an already created lobby
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     * @throws GameAlreadyStartedException if the game has already started
     * @throws FullLobbyException if the lobby is full
     * @throws GameNotExistsException if the game you're trying to access doesn't exist
     */
    @Override
    public void addPlayerToLobby (String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        this.gameController = this.SRMIInterface.addPlayerToLobby(playerNickname, gameId);
        ClientGeneralInterface client = this;
        gameController.addRMIClient(this.personalPlayer.getNickname(), client);
    }




    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void chooseNickname (String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
        this.SRMIInterface.chooseNickname(nickname);
    }




    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException, IllegalArgumentException {
        this.gameController.playCard(nickname, selectedCard, position, orientation);
    }




    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     * @param nickname is the nickname of the Player that wants to play a card
     * @param baseCard is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        this.gameController.playBaseCard(nickname, baseCard, orientation);
    }




    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        this.gameController.drawCard(nickname, selectedCard);
    }




    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
        this.gameController.choosePawnColor(chooserNickname, selectedColor);
    }



    /**
     * This method sends a message from the sender to the receiver(s)
     * @param senderNickname is the nickname of the player sending the message
     * @param receiversNicknames is the list of nicknames of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNicknames, String message) throws RemoteException, NotBoundException {
        this.gameController.sendMessage(senderNickname, receiversNicknames, message);
    }



    /**
     * This method lets a player end the game (volontary action or involontary action - connection loss)
     * @param nickname of the player who is going leave the game
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException
     * @throws IllegalArgumentException
     */
    @Override
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException{
        this.gameController.leaveGame(nickname);
    }

    /**
     * This method is used to get the available lobbies.
     * @return a List containing the available lobbies
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public List<Integer> getAvailableLobbies() throws RemoteException {
        List<Integer> lobbies = new ArrayList<>();
        lobbies.addAll(SRMIInterface.getAvailableGameControllersId());
        return lobbies;
    }

    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() {
        sc=new Scanner(System.in);
        this.console = new BufferedReader(new InputStreamReader(System.in));
        boolean ok=false;
        if(selectedView==1){
            try {
                tuiView = new InterfaceTUI();
                tuiView.printWelcome();
                if (personalPlayer.getNickname() == null) {
                    while (!ok) {
                        String nickname = tuiView.askNickname(sc);
                        try {
                            this.chooseNickname(nickname);
                            personalPlayer.setNickname(nickname);
                            ok = true;
                        } catch (NicknameAlreadyTakenException ex) {
                            System.out.println("Nickname is already taken! Please try again.");
                        }
                    }
                    System.out.println("Nickname correctly selected!");
                }
                    if (!SRMIInterface.getAvailableGameControllersId().isEmpty()) {
                        System.out.println("If you want you can join an already created lobby. These are the ones available:");
                        for (Integer i : SRMIInterface.getAvailableGameControllersId()) {
                            System.out.println("ID: " + i);
                        }
                    } else {
                        System.out.println("There are no lobby available");
                    }
                ok = false;
                int gameSelection = 0;
                while (!ok) {
                    System.out.println("Type -1 if you want to create a new lobby, or the lobby id if you want to join it (if there are any available).");
                    System.out.println("Type -2  to refresh the available lobbies.");
                    try {
                        gameSelection = sc.nextInt();
                        if (gameSelection == -2) {
                                if (!SRMIInterface.getAvailableGameControllersId().isEmpty()) {
                                    System.out.println("If you want you can join an already created lobby. These are the ones available:");
                                    for (Integer i : SRMIInterface.getAvailableGameControllersId()) {
                                        System.out.println("ID: " + i);
                                    }
                                } else {
                                    System.out.println("There are no lobby available");
                                }
                        } else if ((gameSelection != -1) && (!SRMIInterface.getAvailableGameControllersId().contains(gameSelection))) {
                            System.out.println("You wrote a wrong ID, try again.");
                        } else {
                            ok = true;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                        sc.next();
                    }
                }
                    ok = false;
                    while (!ok) {
                        if (gameSelection == -1) {
                            System.out.println("How many players would you like to partecipate in this game?");
                            while (!ok) {
                                try {
                                    gameSelection = sc.nextInt();
                                    ok = true;
                                } catch (InputMismatchException e) {
                                    System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                                    sc.next();
                                }
                            }
                            createLobby(personalPlayer.getNickname(), gameSelection);
                            System.out.println("Successfully created a new lobby with id: " + gameController.getId());
                        } else if (SRMIInterface.getAllGameControllers().containsKey(gameSelection)) {
                            try {
                                System.out.println("Joining the " + gameSelection + " lobby...");
                                addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                                System.out.println("Successfully joined the lobby with id: " + gameController.getId());
                                ok = true;
                                gameController.checkNPlayers();
                            } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException e) {
                                System.out.println(ANSIFormatter.ANSI_RED + "The lobby you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                            } //counter
                        }
                    }
                } catch ( RemoteException | NotBoundException e) {
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    System.exit(-1);
                }
        }else{ //GUI
            // NOTHING
//            String[] network = new String[1];
//            network[0] = "RMI";
//            InterfaceGUI.main(network);
            // facendo un'interfaccia RMIGUI e un'altra interfaccia SCKGUI
            // leggo lo username da system out che viene stampato (scanner su system out) attenzione devo farlo stampare SOLO una volta
            // viene stampato tutte le volte che premo il tasto done. a questo punto viene comunicato l'esito al client
        }
    }

    /**
     * This method is used to execute the player's choice of action during his turn.
     * @throws InterruptedException if the thread executing this gets interrupted
     */
    private void gameTurn() throws InterruptedException{
        boolean ok=false;
        boolean read= false;
        int choice;
        if(selectedView==1) {
            choice=tuiView.showMenuAndWaitForSelection(isPlaying, console);
            if(choice!=-1) {
                try {
                    switch (choice) {
                        case 0:
                            System.out.println("Are you sure to LEAVE the game? Type 1 if you want to leave any other character to return to the game.");
                            try {
                                if (sc.nextInt() == 1) {
                                    inGame = false;
                                    leaveGame(personalPlayer.getNickname());
                                    System.out.println("You left the game.");
                                    System.exit(-1);
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
                            tuiView.printScoreBoard(playersInTheGame);
                            break;
                        case 6:
                            System.out.println("Do you want to send a message to everybody (type 1) or a private message (type the single nickname)?");
                            String answer = sc.next();
                            sc.nextLine();
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
                                    System.out.println("Message correctly sent!");
                                } else {
                                    System.out.println("There is no such player in this lobby! Try again.");
                                }
                            }
                            break;
                        case 7:
                            boolean orientation = true;
                            PlayableCard card = null;
                            Coordinates coordinates;
                            card = tuiView.askPlayCard(sc, personalPlayer);
                            if (card != null) {
                                orientation = tuiView.askCardOrientation(sc);
                            coordinates = tuiView.askCoordinates(sc, card, personalPlayer.getBoard());
                            if (coordinates != null) {
                                try {
                                    this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                    tmp = new ArrayList<>();
                                    tmp.add(resourceCard1);
                                    tmp.add(resourceCard2);
                                    tmp.add(goldCard1);
                                    tmp.add(goldCard2);
                                    card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                    this.drawCard(personalPlayer.getNickname(), card);
                                }catch (IllegalArgumentException e ){
                                    System.out.println("You can't play this card! Returning to menu..."); //@TODO differenziare eccezioni per non giocabilità e non abbastanza risorse?
                                }
                            } else {
                                System.out.println("you can't play this card...returning to menu");
                            }
                            }
                            break;
                        default:
                            System.out.println("not yet implemented");
                    }
                } catch (RemoteException | NotBoundException e) {
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }else{ 
            //gui
        }
    }



    // ------- M E T H O D S   F O R   U P D A T E -------
    // ---- F R O M   S E R V E R   T O   C L I E N T ----

    /**
     *  This method is called when all the player chose their personal objective card to let the
     *  client know the set-up fase has finished.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void finishedSetUpPhase() throws RemoteException{
        updateRound(playersInTheGame);
    }
    /**
     * This is an update method
     * @param board the new board we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateBoard (String boardOwner, Board board) throws RemoteException {
        if (boardOwner.equals(personalPlayer.getNickname())) {
            personalPlayer.setBoard(board);
        }
            for (Player p : playersInTheGame) {
                if (boardOwner.equals(p.getNickname())) {
                    p.setBoard(board);
                }
            }
            if (selectedView == 1) {
                //System.out.println("I received the board update.");
            } else if (selectedView == 2) {
                //guiView.showBoard(board)
            }
        }

    /**
     * This is an update method
     * @param resourceDeck the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateResourceDeck (PlayableDeck resourceDeck) throws RemoteException {
        this.resourceDeck=resourceDeck;
        if (selectedView == 1) {
            //System.out.println("I received the resourceDeck.");
        } else if (selectedView == 2) {
            //guiView.showUpdatedResourceDeck(this.resourceDeck)
        }
    }



    /**
     * This is an update method
     * @param goldDeck the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateGoldDeck (PlayableDeck goldDeck) throws RemoteException {
        this.goldDeck=goldDeck;
        if (selectedView == 1) {
            //System.out.println("I received the goldDeck.");
        } else if (selectedView == 2) {
            //guiView.updateGoldDeck(goldDeck)
        }
    }



    /**
     * This is an update method
     * @param playerNickname the player which deck is updated
     * @param playerDeck the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePlayerDeck (String playerNickname, PlayableCard[] playerDeck) throws RemoteException {
        System.out.println("I received the updated "+playerNickname+"'s deck.");
        if(playerNickname.equals(personalPlayer.getNickname())) {
            personalPlayer.setPlayerDeck(playerDeck);
        }
        for (Player p : playersInTheGame) {
            if (playerNickname.equals(p.getNickname())) {
                p.setPlayerDeck(playerDeck);
            }
        }

        if (selectedView == 1) {
            //System.out.println("I received the updated "+playerNickname+"'s deck.");
        } else if (selectedView == 2) {
            //guiView.updatePlayerDeck(player, playerDeck)
        }
    }

    /**
     * This is an update method
     * @param card is the personal objective card
     * @param nickname is the owner of the personal objective card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePersonalObjective(ObjectiveCard card, String nickname) throws RemoteException {
        if (personalPlayer.getNickname().equals(nickname)) {
            personalPlayer.addPersonalObjective(card);
            if (personalPlayer.getPersonalObjectives().size() == 2) {
                    if (selectedView == 1) {
                        executor.execute(()-> {
                            boolean ok = false;
                            while (!ok) {
                                tuiView.printHand(personalPlayer.getPlayerDeck());
                                try {
                                    ObjectiveCard tmp=tuiView.askChoosePersonalObjective(sc, personalPlayer.getPersonalObjectives());
                                    chooseObjectiveCard(personalPlayer.getNickname(),tmp);
                                    ok = true;
                                    personalPlayer.setPersonalObjective(tmp);
                                    System.out.println("You've correctly chosen your objective card!");
                                }catch (RemoteException |NotBoundException e){
                                    System.out.println("Unable to communicate with the server! Shutting down.");
                                    System.exit(-1);
                                }catch (CardNotOwnedException e){
                                    System.out.println("You don't own this card.");
                                }
                            }
                        });
                    } else if (selectedView == 2) {
                        //gui
                        synchronized (guiLock){
                            guiLock.notify();
                        }
                    }

            }
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        this.resourceCard1=card;
        if (selectedView == 1) {
            //System.out.println("I received the resource Card 1.");
        } else if (selectedView == 2) {
            //guiView.updateResourceCard1(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
   public void updateResourceCard2(PlayableCard card) throws RemoteException {
       this.resourceCard2=card;
        if (selectedView == 1) {
            //System.out.println("I received the resource Card2.");
        } else if (selectedView == 2) {
            //guiView.updateResourceCard2(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        this.goldCard1=card;
        if (selectedView == 1) {
                //System.out.println("I received the goldCard 1");
        } else if (selectedView == 2) {
            //guiView.updateGoldCard1(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateGoldCard2(PlayableCard card) throws RemoteException {
        this.goldCard2=card;
        if (selectedView == 1) {
            //System.out.println("I received the goldCard2.");
        } else if (selectedView == 2) {
            //guiView.updateGoldCard2(card)
        }
    }

    @Override
    public void updateChat(Chat chat) throws RemoteException {

    }


    /**
     * This is an update method
     * @param message which needs to be updated MEGLIO AGGIUNGERE UN SOLO MESSAGGIO MAGARI
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateChat(ChatMessage message) throws RemoteException {//ha senso creare un chatHandler nel model
        messages.add(message);
        if (selectedView == 1) {
            if(!message.getSender().getNickname().equals(personalPlayer.getNickname())) {
                System.out.println(ANSIFormatter.ANSI_YELLOW+"You received a message from "+message.getSender().getNickname()+"!"+ANSIFormatter.ANSI_RESET);
            }
            tuiView.printChat(messages, message.getSender().getNickname(), personalPlayer.getNickname());
        } else if (selectedView == 2) {
            //guiView.updateChat(chat)
        }
    }



    /**
     * This is an update method
     * @param player who selected a new pawn color
     * @param pawn is the selected color
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {

        if (selectedView == 1) {
            //System.out.println("I received the pawns.");
        } else if (selectedView == 2) {
            //guiView.updatePawns(player, pawn)
        }
    }

    /**
     * This is an update method
     * @param newPlayingOrder are the players of the game ordered
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException {
        playersInTheGame = newPlayingOrder;
        if (selectedView == 1) {
            if (turnCounter != -1) {
                if (turnCounter != 0) {
                        if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                            isPlaying = true;
                            System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                        } else {
                            isPlaying = false;
                            System.out.println(playersInTheGame.get(0).getNickname() + " is playing!");
                        }
                        if (turnCounter == 1) {
                            executor.execute(() -> {
                                try {
                                    while (inGame) {
                                        gameTurn();
                                    }
                                } catch (InterruptedException e) {
                                    System.out.println("Issues while executing the app. Closing the program.");
                                    e.printStackTrace();
                                    System.exit(-1);
                                }
                            });
                        }
                } else if (turnCounter == 0) {
                    baseCard = true;
                    executor.execute(() -> {
                        try {
                            playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0], tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]));
                        } catch (NotBoundException | RemoteException ignored) {}
                    });
                }
            }
            turnCounter++;
        } else if (selectedView==2) {
            //gui
            System.out.println("I received the updateRound.");
            //playersInTheGame = newPlayingOrder;
            if (this.turnCounter == 0){
                // MAI FATTO
                //chiamo playBaseCard : se uso un thread per farlo posso continuare a ricevere e a rispondere a ping
            }
            if (this.turnCounter >= 1){
                // MAI FATTO
                //dico ai giocatori chi sta giocando e chi no
                if (this.turnCounter == 1){ //questo è il terzo turno
                    // IN GUI NON VI è ALCUN MENU
                    //dal terzo turno è possibile vedere il menù e selezionarne i punti del menù, la TUI qui lancia un thread che va per tutta la partita
                    synchronized (guiLock){
                        finishedSetup=true;
                        guiLock.notify();
                    }
                }
            }
            turnCounter++; //quando il model fa un updateRound per la terza volta siamo in turnCounter==1 e si può iniziare a selezionare il menù
        }
    }

    /**
     * This is an update method
     * @param card1 is the first common Objective
     * @param card2 is the second common Objective
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateCommonObjectives(ObjectiveCard card1, ObjectiveCard card2) throws RemoteException{
        this.commonObjective1=card1;
        this.commonObjective2=card2;
    }

    /**
     * This is an update method
     * @param gameState is the new Game state (WAITING_FOR_START -> STARTED -> ENDING -> ENDED)
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateGameState(Game.GameState gameState) throws RemoteException {
        if(gameState.equals(Game.GameState.STARTED)){
            inGame=true;
            this.gameController.startHeartbeat(this.personalPlayer.getNickname()); //il gameController inizia a segnarsi se gli arrivano heartBeat
            this.schedulerToSendHeartbeat = Executors.newScheduledThreadPool(1); //bisogna fare lo shutdown quando il gioco termina (con ENDED o con una disconnessione)
            this.schedulerToSendHeartbeat.scheduleAtFixedRate(() -> {
                try {
                    gameController.heartbeat(this.personalPlayer.getNickname()); //in gameController però la prima volta che viene scritta la variabile lastHeartbeatTime è in startHeartbeat
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("Sent heartbeat");
            }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
        }
        if (selectedView == 1) {
            if(gameState.equals(Game.GameState.STARTED)) {
                System.out.println(ANSIFormatter.ANSI_RED+"The game has started!"+ANSIFormatter.ANSI_RESET);
                executor=Executors.newCachedThreadPool();

            } else if (gameState.equals(Game.GameState.ENDING)) {
                System.out.println(ANSIFormatter.ANSI_RED+"Ending condition triggered: someone reached 20 points or both the deck are finished."+ANSIFormatter.ANSI_RESET);
            }else if(gameState.equals(Game.GameState.ENDED)){
                System.out.println(ANSIFormatter.ANSI_RED+"The game has ended."+ANSIFormatter.ANSI_RESET);
                this.schedulerToSendHeartbeat.shutdownNow(); //va fermato subito l'heartBeat? quando il GameController cessa di esistere?
                tuiView.printWinner(playersInTheGame, personalPlayer.getNickname());
                System.exit(-1);
            }
        } else if (selectedView == 2) {

        }
    }

    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void handleDisconnection() throws RemoteException {
        if(selectedView==1) {
            System.out.println("Oh no! Someone disconnected!");
            try {
                this.executor.shutdown();
                this.schedulerToCheckReceivedHeartBeat.shutdown();
                this.schedulerToSendHeartbeat.shutdown(); //va prima chiuso l'heartbeat receiver lato server?

            } catch (SecurityException e) {}

            System.out.println("A disconnection happened. Closing the game.");
            new Thread(()->{System.exit(-1);}); //uso un thread se no questa chiamata non ritorna

            /*
            Timer timer = new Timer();
            try {
                timer.wait(5000);
            } catch (InterruptedException e) {
            }

             */
        }else if(selectedView==2) {
        }
    }


    //GETTER & SETTER

    public int getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }


    public Game.GameState getGameState (){
        try {
            return gameController.getGame().getState();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean getInGame() {
        return inGame;
    }

    @Override
    public void heartbeat(){
        lastHeartbeatTime = System.currentTimeMillis();
        //System.out.println("Received heartbeat at " + lastHeartbeatTime);
    }

    @Override
    public void startHeartbeat() throws RemoteException {
        lastHeartbeatTime = System.currentTimeMillis();
        startHeartbeatMonitor();
    }
    private void startHeartbeatMonitor() { //scheduler.shutdownNow(); in caso di connection lost o Game ENDED
        this.schedulerToCheckReceivedHeartBeat = Executors.newScheduledThreadPool(1);
        var lambdaContext = new Object() {
            ScheduledFuture<?> heartbeatTask;
        };
        lambdaContext.heartbeatTask= this.schedulerToCheckReceivedHeartBeat.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastHeartbeatTime) / 1000 > TIMEOUT) {
                System.out.println("Server connection lost");
                //caso in cui il server risulta irragiungibile
                if (lambdaContext.heartbeatTask != null && !lambdaContext.heartbeatTask.isCancelled()) {
                    lambdaContext.heartbeatTask.cancel(true);
                }
                // taken from handleDisconnection()
                try {
                    this.executor.shutdown();
                    this.schedulerToSendHeartbeat.shutdown(); //va prima chiuso l'heartbeat receiver lato server?

                } catch (Exception e) {
                }
                //System.out.println("A disconnection happened.");
                Timer timer = new Timer();
                try {
                    timer.wait(5000);
                } catch (InterruptedException e) {
                }

                this.schedulerToCheckReceivedHeartBeat.shutdown();
            }
        }, 0, TIMEOUT, TimeUnit.SECONDS);
    }


    public Object getGuiLock(){
        return this.guiLock;
    }

    public boolean getFinishedSetup(){
        return this.finishedSetup;
    }



    public List<Player> getPlayersInTheGame() {
        return playersInTheGame;
    }

    public PlayableCard getResourceCard1() {
        return resourceCard1;
    }

    public PlayableCard getResourceCard2() {
        return resourceCard2;
    }

    public PlayableCard getGoldCard1() {
        return goldCard1;
    }

    public PlayableCard getGoldCard2() {
        return goldCard2;
    }

    public PlayableDeck getGoldDeck() {
        return goldDeck;
    }

    public PlayableDeck getResourceDeck() {
        return resourceDeck;
    }

}













// OLD COMMENTS

//per creare il riferimento in WrappedObserver tramite RMIServer che potrebbe implementare un Observable come da pdf
    /*
    public class RemoteObserverImpl implements RemoteObserver {
        public void remoteUpdate(Object observable, Event updateMsg) {
            System.out.println("Got message: " + updateMsg);
        }
        public static void main(String[] args) throws Exception {
            Registry registry = LocateRegistry.getRegistry();
            RMIObservableService remoteService = (RMIObservableService)    //remoteService è il nostro server
                    registry.lookup("Observable");
            RemoteObserver client = new RemoteObserverImpl();
            RemoteObserver clientStub = (RemoteObserver)
                    UnicastRemoteObject.exportObject(client, 3949);
            remoteService.addObserver(clientStub);    //il server è un Observable e quindi ha la lista di listeners (che sono i Wrapped)
        }
    }

    //il wrapped inizia ad esistere solo nel momento in cui si crea un istanza di Game e quindi si ha bisogno di notificare i players
    //quindi non bisogna fare addObserver in questo momento perchè ancora quel client non è diventato un player (e quindi non ha ancora un Game)
    // diventerà un addToList (per memorizzare i client in attesa)
    // quando parte tutto abbiamo un Game i wrapped

     */

//    public void update(Event e, Observable obs) {
//        switch (e) {
//            case UPDATED_BOARD: // LIST: PLAYER PLAYER - BOARD NEWBOARD
////                for(int i = 0; i < get
////                if((gameController.getGame().getPlayers().get(i)).equals.obs))
////               gameController.getGame()
//
//                for (int i = 0; i<gameController.getGamePlayers().size(); i++) {
//                    if ((obs.getPlayer()).equals(gameController.getGamePlayers().get(i).getBoard())) {
//                        gameController.getGamePlayers().get(i).setBoard(obs);
//                    }
//                }
//                break;
//
//            case UPDATED_RESOURCE_DECK:
//                break;
//
//            case UPDATED_GOLD_DECK:
//                break;
//
//            case UPDATED_PLAYER_DECK:
//                break;
//
//            case UPDATED_RESOURCE_CARD_1:
//                break;
//
//            case UPDATED_RESOURCE_CARD_2:
//                break;
//
//            case UPDATED_GOLD_CARD_1:
//                break;
//
//            case UPDATED_GOLD_CARD_2:
//                break;
//
//            case UPDATED_CHAT:
//                break;
//
//            case UPDATED_PAWNS:
//                break;
//
//            case UPDATED_NICKNAME:
//                break;
//
//            case UPDATED_ROUND:
//                break;
//
//            default:
//                break;
//
//        }
//    }
/*
public void gameLeft() throws RemoteException{
    if(inGame){
        inGame=false;
        try {
            menuThread.join();
        }catch (InterruptedException ignored){} //will never be interrupted before
        System.out.println(ANSIFormatter.ANSI_RED+"Someone left the game."+ANSIFormatter.ANSI_RESET);
        this.resetAttributes();
        System.out.println("Returning to lobby.\n\n\n\n\n\n\n");
        try {
            this.waitingRoom();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
public void resetAttributes(){
    String tmp= personalPlayer.getNickname();
    this.personalPlayer=new Player();
    this.personalPlayer.setNickname(tmp);
    this.goldDeck=null;
    this.resourceDeck=null;
    this.resourceCard1=null;
    this.resourceCard2=null;
    this.goldCard1=null;
    this.goldCard2=null;
    this.turnCounter=-1;
    this.playersInTheGame=null;
    this.gameController=null;
}
 */