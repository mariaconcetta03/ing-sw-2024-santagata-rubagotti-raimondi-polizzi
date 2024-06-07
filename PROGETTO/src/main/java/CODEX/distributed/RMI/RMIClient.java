package CODEX.distributed.RMI;

import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.*;

import CODEX.view.GUI.GUIGameController;
import CODEX.view.GUI.GUILobbyController;
import CODEX.view.GUI.GUIPawnsController;
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
    private static final int TIMEOUT = 7; // seconds
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
    private GUIGameController guiGameController=null;
    private boolean aDisconnectionHappened=false;
    private final Object guiGamestateLock=new Object();
    private final Object guiPawnsControllerLock=new Object();
    private GUIPawnsController GUIPawnsController=null;
    private boolean secondUpdateRoundArrived=false;

    public Player getPersonalPlayer() {
        return personalPlayer;
    }

    private Player personalPlayer;

    private int lastMoves=10;

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
    private Map<Integer, Chat> chats;
    private Settings networkSettings;
    private GUILobbyController guiLobbyController=null;

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
        chats=new HashMap<>();
        networkSettings=new Settings();
    }

    public void setGuiGameController(GUIGameController guiGameController) {
        this.guiGameController=guiGameController;
    }

    public GUILobbyController getGuiLobbyController() {
        return guiLobbyController;
    }

    public void setGuiLobbyController(GUILobbyController guiLobbyController) {
        this.guiLobbyController = guiLobbyController;
    }

    public void setGuiPawnsController(GUIPawnsController ctr) {
        this.GUIPawnsController = ctr;
    }

    public Object getGuiPawnsControllerLock() {
        return this.guiPawnsControllerLock;
    }

    public boolean getSecondUpdateRoundArrived() {
        return secondUpdateRoundArrived;
    }

    public void setSecondUpdateRoundArrived(boolean secondUpdateRoundArrived) {
        this.secondUpdateRoundArrived = secondUpdateRoundArrived;
    }


    /**
     * Settings class
     * It is about port and ip address of the server which the client needs to communicate with
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 standard port for RMI registry
        String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

        public void setSERVER_NAME(String SERVER_NAME) {
            this.SERVER_NAME = SERVER_NAME;
        }
        public String getSERVER_NAME(){
            return this.SERVER_NAME;
        }
    }




    // ---- M E T H O D S   F R O M   C L I E N T   T O   S E R V E R ----

    /**
     * This method gets the SRMIInterface (Server Interface) from the registryServer
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    public void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException {
        Registry registryServer = null;
        registryServer = LocateRegistry.getRegistry(networkSettings.getSERVER_NAME(),
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
        if(!aDisconnectionHappened) {
            this.gameController.playCard(nickname, selectedCard, position, orientation);
        }
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
        if(!aDisconnectionHappened) {
            this.gameController.playBaseCard(nickname, baseCard, orientation);
        }
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
        if(!aDisconnectionHappened) {
            this.gameController.drawCard(nickname, selectedCard);
        }
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
        if(!aDisconnectionHappened) {
            this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
        }
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException, ColorAlreadyTakenException{
        if(!aDisconnectionHappened) {
            this.gameController.choosePawnColor(chooserNickname, selectedColor);
        }
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
        if(!aDisconnectionHappened) {
            this.gameController.sendMessage(senderNickname, receiversNicknames, message);
        }
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
        /*
        this.gameController.leaveGame(nickname);

         */
        //if (!aDisconnectionHappened) non lo controllo perchè se viene rilevata sulla gui la disconnessione non viene premuto il tasto che porta a questa funzione

        if(this.executor!=null) {
            this.executor.shutdown();
        }
        this.schedulerToCheckReceivedHeartBeat.shutdown();
        this.schedulerToSendHeartbeat.shutdown(); //l'heartbeat receiver lato server rileverà la disconnessione
        System.out.println("You left the game.");
        System.exit(0); //status 0 -> no errors

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
                            List<Player> tmpList= new ArrayList<>(playersInTheGame);
                            tuiView.printScoreBoard(tmpList);
                            break;
                        case 6: tuiView.printLegend();
                            break;
                        case 7: System.out.println(ANSIFormatter.ANSI_BLUE+"It's "+playersInTheGame.get(0).getNickname()+"'s turn!"+ANSIFormatter.ANSI_RESET);
                            break;
                        case 8:
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
                                    try {
                                        this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                        if(lastMoves>playersInTheGame.size()) {
                                            tmp = new ArrayList<>();
                                            tmp.add(resourceCard1);
                                            tmp.add(resourceCard2);
                                            tmp.add(goldCard1);
                                            tmp.add(goldCard2);
                                            card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                            this.drawCard(personalPlayer.getNickname(), card);
                                        }
                                    }catch (IllegalArgumentException e ){
                                        System.out.println("You can't play this card! Returning to menu..."); //@TODO differenziare eccezioni per non giocabilità e non abbastanza risorse?
                                    }
                                } else {
                                    System.out.println("You can't play this card! Returning to menu..."); //@TODO differenziare eccezioni per non giocabilità e non abbastanza risorse?
                                }
                            }
                            break;
                        default:
                            System.out.println("Functionality not yet implemented");
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
    public void updateBoard (String boardOwner, Board board, PlayableCard newCard) throws RemoteException {
        if (boardOwner.equals(personalPlayer.getNickname())) {
            personalPlayer.setBoard(board);
        }
            for (Player p : playersInTheGame) {
                if (boardOwner.equals(p.getNickname())) {
                    p.setBoard(board);
                }
            }
            if (selectedView == 1) {
                System.out.println("I received the board update.");
            } else if (selectedView == 2) {
                if (guiGameController != null) {
                    guiGameController.updateBoard(boardOwner, newCard);
                }
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
            if(guiGameController!=null){
                guiGameController.updateResourceDeck();
            }
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
            if(guiGameController!=null){
                guiGameController.updateGoldDeck();
            }
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
            if(!(playerNickname.equals(personalPlayer.getNickname()))){
                if(guiGameController!=null){
                    guiGameController.updatePlayerDeck(playerNickname, playerDeck);
                }
            }
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
                                    gameController.checkObjectiveCardChosen(); //just added
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
            if(guiGameController!=null){
                guiGameController.updateResourceCard1(card);
            }
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
            if(guiGameController!=null){
                guiGameController.updateResourceCard2(card);
            }
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
            if(guiGameController!=null){
                guiGameController.updateGoldCard1(card);
            }
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
            if(guiGameController!=null){
                guiGameController.updateGoldCard2(card);
            }
        }
    }

    @Override
    public void updateChat(Integer chatIdentifier,Chat chat) throws RemoteException {
        //chats.put()
    }


    /**
     * This is an update method
     * @param message which needs to be updated MEGLIO AGGIUNGERE UN SOLO MESSAGGIO MAGARI
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateChatMessage(ChatMessage message) throws RemoteException {
        //messages.add(message);
        if (selectedView == 1) {
            if(!message.getSender().equals(personalPlayer.getNickname())) {
                System.out.println(ANSIFormatter.ANSI_YELLOW+"You received a message from "+message.getSender()+"!"+ANSIFormatter.ANSI_RESET);
            }
            //tuiView.printChat(messages, message.getSender(), personalPlayer.getNickname());



        } else if (selectedView == 2) {
            //guiView.updateChat(chat)
        }
    }



    /**
     * This is an update method
     * @param nickname is the nickname of the player who selected a new pawn color
     * @param pawn is the selected color
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePawns(String nickname, Pawn pawn) throws RemoteException {
        //System.out.println("I received "+nickname+"'s pawn color");
        if(nickname.equals(personalPlayer.getNickname())){
            personalPlayer.setColor(pawn);
        }
        for(Player p: playersInTheGame){
            if(p.getNickname().equals(nickname)){
                p.setColor(pawn);
            }
        }
        if (selectedView == 1) {

        } else if (selectedView == 2) { //arriva la scelta degli altri, ma se io ho già scelto ignoro questo update
            //guiView.updatePawns(player, pawn)
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
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException {
        playersInTheGame = newPlayingOrder;
        System.out.println("update ricevuto, tocca a "+playersInTheGame.get(0).getNickname());
        if (selectedView == 1) {
            if (turnCounter != -1) {
                if (turnCounter != 0) {
                    if(lastMoves>0) {
                        if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                            isPlaying = true;
                            System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                            if (lastMoves <= playersInTheGame.size()) {
                                System.out.println("This is your last turn! You will not draw.");
                            }

                        } else {
                            isPlaying = false;
                            System.out.println(playersInTheGame.get(0).getNickname() + " is playing!");
                        }
                    }else{
                        inGame=false;
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
                            gameController.checkBaseCardPlayed();
                        } catch (NotBoundException | RemoteException ignored) {}
                    });
                }
            }else { //ask Pawn selection
                executor.execute(() -> {
                    boolean ok=false;
                    try {
                        while (!ok) {
                            Pawn selection = tuiView.askPawnSelection(gameController.getGame().getAvailableColors(),sc);
                            if (selection != null) {
                                try {
                                    this.choosePawnColor(personalPlayer.getNickname(), selection);
                                    ok = true;
                                    System.out.println("Pawn color correctly selected!");
                                    gameController.checkChosenPawnColor();
                                } catch (ColorAlreadyTakenException e) {
                                    System.out.println("This color is already taken! Please try again.");
                                }
                            } else {
                                System.out.println("Please insert one of the possible colors!");
                            }
                        }
                    } catch (NotBoundException |RemoteException e) {
                        System.out.println("Unable to communicate with the Server. Shutting down.");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                });
            }
            turnCounter++;
        } else if (selectedView==2) {
            //gui
            System.out.println("I received the updateRound.");
            //playersInTheGame = newPlayingOrder;
            if (this.turnCounter == 0){
                //chiamo playBaseCard : se uso un thread per farlo posso continuare a ricevere e a rispondere a ping

                synchronized (guiPawnsControllerLock){
                    secondUpdateRoundArrived=true;
                    guiPawnsControllerLock.notify();
                }

            }
            if (this.turnCounter >= 1){

                if(lastMoves>0) { //viene inizializzato a 10 e viene cambiato con un altro valore solo quando arriva il primo updateLastMovesEvent (dai successivi updateLastMovesEvent viene decrementato)
                    if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                        if (lastMoves <= playersInTheGame.size()) {
                            System.out.println("This is your last turn! You will not draw.");
                            //dobbiamo impedire al giocatore di pescare le carte settando un booleano
                            if (guiGameController != null) {
                                guiGameController.updatePoints();
                                guiGameController.updateRound(true); //settiamo lastTurn a true
                            }
                        }
                    }else {
                        if(guiGameController!=null){
                            guiGameController.updatePoints();
                            guiGameController.updateRound(false);
                        }

                    }

                    /*if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                        isPlaying = true;
                        System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                        if (lastMoves <= playersInTheGame.size()) {
                            System.out.println("This is your last turn! You will not draw.");
                        }

                    } else {
                        isPlaying = false;
                        System.out.println(playersInTheGame.get(0).getNickname() + " is playing!");
                    }

                     */
                }else{
                    inGame=false;
                }
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
                //this.schedulerToSendHeartbeat.shutdownNow(); //va fermato subito l'heartBeat? quando il GameController cessa di esistere?
                tuiView.printWinner(playersInTheGame, personalPlayer.getNickname());
                executor.execute(()->{System.exit(-1);});
            }
        } else if (selectedView == 2) {
            //guiView.updateGameState(game)
            if(gameState.equals(Game.GameState.STARTED)) {
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

        }
    }

    public Object getGuiGamestateLock() {
        return guiGamestateLock;
    }

    /**
     *
     * @param winners
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void showWinner(List<Player> winners) throws RemoteException{
        if(selectedView==1) { //TUI
            if (winners.size() == 1) {
                System.out.println(winners.get(0).getNickname() + " WON!!!");
            } else if (winners.size() > 1) {
                for (Player p : winners) {
                    System.out.print(p.getNickname() + ", ");
                }
                System.out.println("tied!");
            }
            //tuiView.printWinner(playersInTheGame, personalPlayer.getNickname());
        }
        if(selectedView==2){ //GUI
            // ci sarà un update notificato al GUIGameController. Quando arriva questa notifica allora cambio la schermata
            if(guiGameController!=null){
                guiGameController.updateWinners(winners);
                }
        }

    }

    /**
     * This is an update method
     * @param lastMoves is the number of turns remaining before the game ends.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateLastMoves(int lastMoves) throws RemoteException {
        this.lastMoves=lastMoves;
        System.out.println("LAST MOVES "+this.lastMoves);
    }

    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void handleDisconnection() throws RemoteException {
        System.out.println("A disconnection happened. Closing the game.");
        if(selectedView==1) {
            handleDisconnectionFunction();
        }
        if(selectedView==2) {
            synchronized (guiLock){
                aDisconnectionHappened = true; //serve per bloccare altre cose appena arriva una disconnessione
                guiLock.notify(); //nel caso in cui la gui sta facendo la wait di un evento (che dopo questa discconnessione non si verificherà mai)
            }
        }
    }

    public void handleDisconnectionFunction() throws RemoteException{ //viene chiamata direttamente dalla gui

        inGame=false;
        if(this.executor!=null) {
            this.executor.shutdown();
        }
        this.schedulerToCheckReceivedHeartBeat.shutdown();
        this.schedulerToSendHeartbeat.shutdown(); //va prima chiuso l'heartbeat receiver lato server -> lo facciamo la prima volta che il controller chiama disconnect()

        Timer timer=new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        //uso un thread se no questa chiamata non ritorna
                        System.exit(0); //status 0 -> no errors
                    }

                },2000);
    }


    //GETTER & SETTER

    /**
     * Getter method
     * @return the index related to the selected view
     */
    public int getSelectedView() {
        return selectedView;
    }

    /**
     * Setter method
     * @param selectedView the index related to the selected view
     */
    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }

    /**
     * Getter method
     * @return the state the Game is in
     */
    public Game.GameState getGameState (){
        try {
            return gameController.getGame().getState();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter method
     * @return true if the player is effectively in a Game, false otherwise
     */
    public boolean getInGame() {
        return inGame;
    }

    /**
     * This method is used to check the disconnections through RMI protocol
     */
    @Override
    public void heartbeat(){
        lastHeartbeatTime = System.currentTimeMillis();
        //System.out.println("Received heartbeat at " + lastHeartbeatTime);
    }

    /**
     * This method lets the heartbeat mechanism start
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void startHeartbeat() throws RemoteException {
        lastHeartbeatTime = System.currentTimeMillis();
        startHeartbeatMonitor();
    }

    /**
     * This method use a scheduler to administrate the heartbeat mechanism.
     */
    private void startHeartbeatMonitor() { //scheduler.shutdownNow(); in caso di connection lost o Game ENDED
        this.schedulerToCheckReceivedHeartBeat = Executors.newScheduledThreadPool(1);
        var lambdaContext = new Object() {
            ScheduledFuture<?> heartbeatTask;
        };
        lambdaContext.heartbeatTask= this.schedulerToCheckReceivedHeartBeat.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastHeartbeatTime) / 1000 > TIMEOUT) {
                //caso in cui il server risulta irragiungibile/è avvenuta una disconnessione
                if (lambdaContext.heartbeatTask != null && !lambdaContext.heartbeatTask.isCancelled()) {
                    lambdaContext.heartbeatTask.cancel(true); //chiude schedulerToCheckReceivedHeartBeat
                }
                // chiudiamo tutto quello che c'è da chiudere
                try {
                    handleDisconnection();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * Getter method
     * @return the lock related to GUI
     */
    public Object getGuiLock(){
        return this.guiLock;
    }

    /**
     * Getter method
     * @return true if the setup phase is finished, false otherwise
     */
    public boolean getFinishedSetup(){
        return this.finishedSetup;
    }

    /**
     * Getter method
     * @return a List containing all the Players playing the specific Game
     */
    public List<Player> getPlayersInTheGame() {
        return playersInTheGame;
    }

    /**
     * Getter method
     * @return the resource Card 1
     */
    public PlayableCard getResourceCard1() {
        return resourceCard1;
    }

    /**
     * Getter method
     * @return the resource Card 2
     */
    public PlayableCard getResourceCard2() {
        return resourceCard2;
    }

    /**
     * Getter method
     * @return the gold Card 1
     */
    public PlayableCard getGoldCard1() {
        return goldCard1;
    }

    /**
     * Getter method
     * @return the gold Card 2
     */
    public PlayableCard getGoldCard2() {
        return goldCard2;
    }

    /**
     * Getter method
     * @return the gold Deck
     */
    public PlayableDeck getGoldDeck() {
        return goldDeck;
    }

    /**
     * Getter method
     * @return the resource Deck
     */
    public PlayableDeck getResourceDeck() {
        return resourceDeck;
    }

    public Settings getNetworkSettings() {
        return networkSettings;
    }
    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
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