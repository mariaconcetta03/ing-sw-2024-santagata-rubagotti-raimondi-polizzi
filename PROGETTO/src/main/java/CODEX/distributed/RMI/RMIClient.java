package CODEX.distributed.RMI;

import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.*;
import CODEX.view.GUI.*;
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


/**
 * This class represents a client RMI
 *  ----------------------------------- H O W   I T   W O R K S ----------------------------------------
 *  RMI CLIENT CALLS THE REMOTE METHODS THROUGH THE RMISERVER INTERFACE (WHICH RMI CLIENT HAS INSIDE)
 *  THE RMI SERVER GOES TO MODIFY THE MODEL (UPON CLIENT REQUEST) THROUGH THE CONTROLLER.
 *  FOR THE METHODS OF THE GAMECONTROLLER, THE CLIENT INVOKES THEM DIRECTLY BY PASSING THROUGH THE
 *  GAMECONTROLLER. THE GAME CONTROLLER IS PASSED TO THE CLIENT BY THE METHODS "startLobby"
 *  AND "addPlayerToLobby"
 *  ----------------------------------------------------------------------------------------------------
 */
public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface {
    private final Object guiLock;
    private static final int HEARTBEAT_INTERVAL = 2; // seconds
    private static final int TIMEOUT = 4; // seconds
    private ScheduledExecutorService schedulerToSendHeartbeat;
    ScheduledExecutorService schedulerToCheckReceivedHeartBeat;
    private long lastHeartbeatTime;
    private boolean done = false;
    private ServerRMIInterface SRMIInterface;
    ExecutorService executor;
    private BufferedReader console;
    Scanner sc;
    int turnCounter = -1;

    private GameControllerInterface gameController = null;
    // given to the client when the game is started (lobby created or player joined to a lobby)

    private int selectedView; // 1==TUI, 2==GUI
    private InterfaceTUI tuiView;
    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private GUIGameController guiGameController = null;
    private boolean aDisconnectionHappened = false;
    private final Object guiGamestateLock = new Object();
    private final Object guiPawnsControllerLock = new Object();
    private GUIPawnsController GUIPawnsController = null;
    private final Object actionLock = new Object();
    private boolean responseReceived = false;
    private final Object guiBaseCardControllerLock = new Object();
    private GUIBaseCardController guiBaseCardController = null;
    private final Object guiObjectiveControllerLock = new Object();
    private GUIObjectiveController guiObjectiveController = null;
    private final Object disconnectionLock = new Object();
    private Player personalPlayer;
    private int lastMoves = 10;
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;
    private boolean inGame = false;
    private boolean isPlaying = false;
    private boolean baseCard = false;
    private boolean nicknameSet = false;
    private Map<Integer, Chat> chats;
    private Settings networkSettings;
    private GUILobbyController guiLobbyController = null;
    private boolean showWinnerArrived=false;


    @Override
    public void setResponseReceived(boolean b) {
        this.responseReceived = b;
    }



    @Override
    public void okEventExecute(String nickname) {
        if (nickname.equals(personalPlayer.getNickname())) {
            synchronized (actionLock) {
                System.out.println("dentro la syn di ok");
                responseReceived = true;
                actionLock.notify();
            }
        }
    }



    /**
     * Class constructor
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public RMIClient() throws RemoteException {
        this.guiLock = new Object();
        personalPlayer = new Player();
        chats = new HashMap<>();
        networkSettings = new Settings();
    }




    /**
     * Settings class
     * It is about port and ip address of the server which the client needs to communicate with
     */
    public static class Settings {
        static int PORT = 1099; // 1099 standard port for RMI registry
        String SERVER_NAME = "127.0.0.1"; // LOCALHOST

        /**
         * Getter method
         * @return the IP of the server
         */
        public String getSERVER_NAME() {
            return this.SERVER_NAME;
        }

        /**
         * Setter method
         * @param SERVER_NAME IP address of the server
         */
        public void setSERVER_NAME(String SERVER_NAME) {
            if (SERVER_NAME.equals("")) { // empty string --> localhost
                Settings.this.SERVER_NAME = "127.0.0.1";
                System.out.println("hai lasciato la stringa vuota! quindi ho settato il localhost: 127.0.0.1");
            } else {
                this.SERVER_NAME = SERVER_NAME;
                System.out.println("hai inserito l'IP, quindi ho settato il server name a: " + SERVER_NAME);
            }
        }
    }



    /**
     * This method gets the SRMIInterface (Server Interface) from the registryServer
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NotBoundException if an exception happens while communicating with the remote
     */
    public void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException {
        Registry registryServer = null;
        // getting the registry
        registryServer = LocateRegistry.getRegistry(networkSettings.getSERVER_NAME(),
                Settings.PORT);
        // looking up the registry to search for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
    }



    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers)  {
        try {this.gameController = this.SRMIInterface.createLobby(creatorNickname, numOfPlayers);
        ClientGeneralInterface client = this;
        gameController.addRMIClient(this.personalPlayer.getNickname(), client);
        } catch (RemoteException exceptionBeforeTheGameHasStarted) {
            handleDisconnectionFunction();
        }
    }



    /**
     * This method is used to add a single player to an already created lobby
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws GameAlreadyStartedException if the game has already started
     * @throws FullLobbyException if the lobby is full
     * @throws GameNotExistsException if the game you're trying to access doesn't exist
     */
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) throws GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        try {
        this.gameController = this.SRMIInterface.addPlayerToLobby(playerNickname, gameId);
        ClientGeneralInterface client = this;
        gameController.addRMIClient(this.personalPlayer.getNickname(), client);
        } catch (RemoteException exceptionBeforeTheGameHasStarted) {
            handleDisconnectionFunction();
        }
    }



    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param nickname is the String he wants to put as his nickname
     * @throws NicknameAlreadyTakenException if the nickname has already been chosen by another user
     */
    @Override
    public void chooseNickname(String nickname) throws NicknameAlreadyTakenException {
        try {
            this.SRMIInterface.chooseNickname(nickname);
        } catch (RemoteException exceptionBeforeTheGameHasStarted) {
            handleDisconnectionFunction();
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
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    responseReceived = false;
                    this.gameController.playCard(nickname, selectedCard, position, orientation);
                    while (!responseReceived) {
                        try {
                            actionLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
                }
            }
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
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    this.gameController.playBaseCard(nickname, baseCard, orientation);
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
                }
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
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    responseReceived = false;
                    this.gameController.drawCard(nickname, selectedCard);
                    while (!responseReceived) {
                        try {
                            actionLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
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
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
                }
            }
        }
    }



    /**
     * This method allows a player to choose the color of his pawn
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor   is the color chosen by the player
     * @throws ColorAlreadyTakenException if someone chose the color before you
     */
    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws  ColorAlreadyTakenException {
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    responseReceived = false;
                    this.gameController.choosePawnColor(chooserNickname, selectedColor);
                    while (!responseReceived) {
                        try {
                            actionLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
                }
            }
        }
    }



    /**
     * This method sends a message from the sender to the receiver(s)
     * @param senderNickname     is the nickname of the player sending the message
     * @param receiversNicknames is the list of nicknames of the players who need to receive this message
     * @param message            is the string sent by the sender to the receivers
    */
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNicknames, String message)  {
        try {
            if (!aDisconnectionHappened) {
                synchronized (actionLock) {
                    responseReceived = false;
                    this.gameController.sendMessage(senderNickname, receiversNicknames, message);
                    while (!responseReceived) {
                        try {
                            actionLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            synchronized (disconnectionLock) {
                if (selectedView == 1) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
                if (selectedView == 2) {
                    aDisconnectionHappened = true;
                }
            }
        }
    }



    /**
     * This method lets a player end the game (volontary action or involontary action - connection loss)
     * @param nickname of the player who is going leave the game
     */
    @Override
    public void leaveGame(String nickname)  {
        synchronized (disconnectionLock) {
            if (!aDisconnectionHappened) {
                if (this.executor != null) {
                    this.executor.shutdown();
                }
                this.schedulerToCheckReceivedHeartBeat.shutdown();
                this.schedulerToSendHeartbeat.shutdown();
                System.out.println("You left the game.");
                System.exit(0); //status 0 -> no errors
            }
        }

    }



    /**
     * This method is used to get the available lobbies.
     * @return a List containing the available lobbies
     */
    public List<Integer> getAvailableLobbies()  {
        List<Integer> lobbies = new ArrayList<>();
        try {
            lobbies.addAll(SRMIInterface.getAvailableGameControllersId());
        } catch (RemoteException exceptionBeforeTheGameHasStarted) {
            handleDisconnectionFunction();
        }
        return lobbies;
    }



    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() {
        try {
            sc = new Scanner(System.in);
            this.console = new BufferedReader(new InputStreamReader(System.in));
            boolean ok = false;
            if (selectedView == 1) {
                tuiView = new InterfaceTUI();
                tuiView.printWelcome();
                if (personalPlayer.getNickname() == null) {
                    String nickname;
                    while (!ok) {
                        nickname = tuiView.askNickname(sc);
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
                        } else {
                            if ((gameSelection != -1) && (!SRMIInterface.getAvailableGameControllersId().contains(gameSelection))) {
                                System.out.println("You wrote a wrong ID, try again.");
                            } else {
                                ok = true;
                            }
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
                        try {
                            createLobby(personalPlayer.getNickname(), gameSelection);
                            System.out.println("Successfully created a new lobby with id: " + gameController.getId());
                        }catch (IllegalArgumentException e){
                            ok=false;
                            gameSelection=-1;
                            System.out.println("Invalid number of players. Type a number between 2-4.");
                        }
                    } else {
                        if (SRMIInterface.getAllGameControllers().containsKey(gameSelection)) {
                            try {
                                System.out.println("Joining the " + gameSelection + " lobby...");
                                addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                                System.out.println("Successfully joined the lobby with id: " + gameController.getId());
                                ok = true;
                                gameController.checkNPlayers();
                            } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException e) {
                                System.out.println(ANSIFormatter.ANSI_RED + "The lobby you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                            }
                        }
                    }
                }
            }
        } catch (RemoteException exceptionBeforeTheGameHasStarted){
            handleDisconnectionFunction();
        }
    }



    /**
     * This method is used to execute the player's choice of action during his turn.
     * @throws InterruptedException if the thread executing this gets interrupted
     */
    private void gameTurn() throws InterruptedException {
        boolean ok = false;
        boolean read = false;
        int choice;
        if (selectedView == 1) {
            choice = tuiView.showMenuAndWaitForSelection(isPlaying, console);
            if (choice != -1) {
                    switch (choice) {
                        case 0:
                            System.out.println("Are you sure to LEAVE the game? Type 1 if you want to leave any other character to return to the game.");
                            try {
                                if (sc.nextInt() == 1) {
                                    inGame = false;
                                    leaveGame(personalPlayer.getNickname());
                                }
                            } catch (InputMismatchException ignored) {}
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
                        case 6:
                            tuiView.printLegend();
                            break;
                        case 7:
                            System.out.println(ANSIFormatter.ANSI_BLUE + "It's " + playersInTheGame.get(0).getNickname() + "'s turn!" + ANSIFormatter.ANSI_RESET);
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
                                    if(coordinates.getX()!=-1) {
                                        try {
                                            System.out.println("chiamo playCard");
                                            this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                            if (lastMoves > playersInTheGame.size()) {
                                                tmp = new ArrayList<>();
                                                tmp.add(resourceCard1);
                                                tmp.add(resourceCard2);
                                                tmp.add(goldCard1);
                                                tmp.add(goldCard2);
                                                card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                                this.drawCard(personalPlayer.getNickname(), card);
                                            }
                                        } catch (IllegalArgumentException e) {
                                            System.out.println("You can't play this card! Returning to menu...");
                                        }
                                    }
                                } else {
                                    System.out.println("You can't play this card! Returning to menu...");
                                }
                            }
                            break;
                        default:
                            System.out.println("Functionality not yet implemented");
                    }

            }
        }
    }



    /**
     * This method is called when all the player chose their personal objective card to let the
     * client know the set-up fase has finished.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void finishedSetUpPhase() throws RemoteException {
        updateRound(playersInTheGame);
    }



    /**
     * This is an update method
     * @param board the new board we want to update
     * @param boardOwner is the player which possesses the board
     * @param newCard the last card placed
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateBoard(String boardOwner, Board board, PlayableCard newCard) throws RemoteException {
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
    public void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException {
        this.resourceDeck = resourceDeck;
        if (selectedView == 2) {
            if (guiGameController != null) {
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
    public void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException {
        this.goldDeck = goldDeck;
        if (selectedView == 2) {
            if (guiGameController != null) {
                guiGameController.updateGoldDeck();
            }
        }
    }



    /**
     * This is an update method
     * @param playerNickname the player which deck is updated
     * @param playerDeck     the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException {
        System.out.println("I received the updated " + playerNickname + "'s deck.");
        if (playerNickname.equals(personalPlayer.getNickname())) {
            personalPlayer.setPlayerDeck(playerDeck);
        }
        for (Player p : playersInTheGame) {
            if (playerNickname.equals(p.getNickname())) {
                p.setPlayerDeck(playerDeck);
            }
        }

        if (selectedView == 2) {
            if (!(playerNickname.equals(personalPlayer.getNickname()))) {
                if (guiGameController != null) {
                    guiGameController.updatePlayerDeck(playerNickname, playerDeck);
                }
            }
        }
    }



    /**
     * This is an update method
     * @param card     is the personal objective card
     * @param nickname is the owner of the personal objective card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePersonalObjective(ObjectiveCard card, String nickname) throws RemoteException {
        if (personalPlayer.getNickname().equals(nickname)) {
            personalPlayer.addPersonalObjective(card);
            if (personalPlayer.getPersonalObjectives().size() == 2) {
                if (selectedView == 1) {
                    executor.execute(() -> {
                        boolean ok = false;
                        while (!ok) {
                            tuiView.printHand(personalPlayer.getPlayerDeck());
                            try {
                                ObjectiveCard tmp = tuiView.askChoosePersonalObjective(sc, personalPlayer.getPersonalObjectives());
                                chooseObjectiveCard(personalPlayer.getNickname(), tmp);
                                ok = true;
                                personalPlayer.setPersonalObjective(tmp);
                                System.out.println("You've correctly chosen your objective card!");
                                gameController.checkObjectiveCardChosen(); //just added
                            } catch (RemoteException  e) {
                                System.out.println("Unable to communicate with the server! Shutting down.");
                                System.exit(-1);
                            } catch (CardNotOwnedException e) {
                                System.out.println("You don't own this card.");
                            }
                        }
                    });
                } else if (selectedView == 2) {
                    guiBaseCardController.updateGameState();
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
        this.resourceCard1 = card;
        if (selectedView == 2) {
            if (guiGameController != null) {
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
        this.resourceCard2 = card;
        if (selectedView == 2) {
            if (guiGameController != null) {
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
        this.goldCard1 = card;
        if (selectedView == 2) {
            if (guiGameController != null) {
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
        this.goldCard2 = card;
       if (selectedView == 2) {
            if (guiGameController != null) {
                guiGameController.updateGoldCard2(card);
            }
        }
    }



    /**
     * This is an update method
     * @param chatIdentifier ID
     * @param chat which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateChat(Integer chatIdentifier, Chat chat) throws RemoteException {
    }



    /**
     * This is an update method
     * @param nickname is the nickname of the player who selected a new pawn color
     * @param pawn     is the selected color
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updatePawns(String nickname, Pawn pawn) throws RemoteException {
        if (nickname.equals(personalPlayer.getNickname())) {
            personalPlayer.setColor(pawn);
        }
        for (Player p : playersInTheGame) {
            if (p.getNickname().equals(nickname)) {
                p.setColor(pawn);
            }
        }

        if (selectedView == 2) {
            System.out.println("RMI: sto per entrare nel syn");
            synchronized (guiPawnsControllerLock) {
                System.out.println("RMI: nel syn");
                while (GUIPawnsController == null) {
                    System.out.println("RMI: adesso faccio la wait()");
                    try {
                        guiPawnsControllerLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("RMI: uscito dal syn");
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
        playersInTheGame = newPlayingOrder; //the first time we called updateRound we need to initialize playerInTheGame (even if they are not still ordered)
        if (selectedView == 1) {
            if (turnCounter != -1) {
                if (turnCounter != 0) {
                    if (lastMoves > 0) {
                        if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                            System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                            if (lastMoves <= playersInTheGame.size()) {
                                System.out.println("This is your last turn! You will not draw.");
                            }
                            isPlaying = true;
                        } else {
                            System.out.println(ANSIFormatter.ANSI_BLUE+playersInTheGame.get(0).getNickname() + " is playing!"+ANSIFormatter.ANSI_RESET);
                            isPlaying = false;
                        }
                    } else {
                        inGame = false;
                    }
                    if (turnCounter == 1) { // when the model does an updateRound for the third time we are in turnCounter==1 and we can start showing the menu
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
                        } catch (RemoteException ignored) {
                        }
                    });
                }
            } else { // ask Pawn selection
                executor.execute(() -> {
                    boolean ok = false;
                    try {
                        while (!ok) {
                            Pawn selection = tuiView.askPawnSelection(gameController.getGame().getAvailableColors(), sc);
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
                    } catch (RemoteException e) {
                        System.out.println("Unable to communicate with the Server. Shutting down.");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                });
            }
            turnCounter++;

        } else if (selectedView == 2) {
            System.out.println("I received the updateRound.");
            if (this.turnCounter == -1) { // first time that updateRound is called
                synchronized (guiGamestateLock) {
                    while (guiLobbyController == null) {
                        try {
                            guiGamestateLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                guiLobbyController.updateGameState(); //telling the guiLobbyController that the pawns selection can start
            }
            if (this.turnCounter == 0) { // second time that updateRound is called
                GUIPawnsController.updateGameState();
            }
            if (this.turnCounter >= 1) {

                if (lastMoves > 0) { // initialized to 10 (high value) and then it is set to an int with the first UpdateLastMovesEvent received

                    if (lastMoves <= playersInTheGame.size()) {
                        System.out.println("This is your last turn! You will not draw.");
                        // player can't draw cards --> passing the boolean true to guiGameController.updateRound(boolean lastRound)
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(true); // lastTurn = true
                        }
                    } else {
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(false); // lastTurn = true
                        }
                    }

                } else {
                    inGame = false;
                }

                if (this.turnCounter == 1) { // third time that updateRound is called
                    guiObjectiveController.updateGameState();
                }
            }
            turnCounter++;
            // when the model does an updateRound for the third time we are in turnCounter==1 and we can show the GUIGameController scene
        }
    }



    /**
     * This is an update method
     * @param card1 is the first common Objective
     * @param card2 is the second common Objective
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateCommonObjectives(ObjectiveCard card1, ObjectiveCard card2) throws RemoteException {
        this.commonObjective1 = card1;
        this.commonObjective2 = card2;
    }



    /**
     * This is an update method
     * @param gameState is the new Game state (WAITING_FOR_START -> STARTED -> ENDING -> ENDED)
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateGameState(Game.GameState gameState) throws RemoteException {
        if (gameState.equals(Game.GameState.STARTED)) {
            inGame = true;
            this.gameController.startHeartbeat(this.personalPlayer.getNickname());
            this.schedulerToSendHeartbeat = Executors.newScheduledThreadPool(1);
            this.schedulerToSendHeartbeat.scheduleAtFixedRate(() -> {
                if(!aDisconnectionHappened) {
                    try {
                        gameController.heartbeat(this.personalPlayer.getNickname());
                    } catch (RemoteException e) {
                        aDisconnectionHappened = true;
                        if (selectedView == 1) {
                            handleDisconnectionFunction();
                        }
                    }
                }else {
                    if(schedulerToSendHeartbeat!=null){
                        schedulerToSendHeartbeat.shutdown();
                    }
                }
            }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
        }
        if (selectedView == 1) {
            if (gameState.equals(Game.GameState.STARTED)) {
                System.out.println(ANSIFormatter.ANSI_RED + "The game has started!" + ANSIFormatter.ANSI_RESET);
                executor = Executors.newCachedThreadPool();
            } else if (gameState.equals(Game.GameState.ENDING)) {
                System.out.println(ANSIFormatter.ANSI_RED + "Ending condition triggered: someone reached 20 points or both the deck are finished." + ANSIFormatter.ANSI_RESET);
            } else if (gameState.equals(Game.GameState.ENDED)) {
                System.out.println(ANSIFormatter.ANSI_RED + "\nThe game has ended.\n" + ANSIFormatter.ANSI_RESET);
            }
        }
    }



    /**
     * This method prints the winners of the game in the TUI
     * @param finalScoreBoard is a Map containing all the players' nicknames as values and as keys their positions
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void showWinner(Map<Integer, List<String>> finalScoreBoard) throws RemoteException {
        showWinnerArrived=true;
        synchronized (actionLock){
            actionLock.notify(); //to stop the waiting of something that will never arrive
        }

        if (selectedView == 1) { //TUI
            Map<String, Player> players = new HashMap<>();
            for (Player p : playersInTheGame) {
                players.put(p.getNickname(), p);
            }
            boolean printed = false;

            for (String s : finalScoreBoard.get(1)) {
                if (s.equals(personalPlayer.getNickname())) {
                    tuiView.printWinner(true);
                    printed = true;
                }
            }
            if (!printed) {
                tuiView.printWinner(false);
            }
            System.out.println();
            System.out.println(ANSIFormatter.ANSI_WHITE_BACKGROUND + ANSIFormatter.ANSI_BLACK + "----- This is the final scoreboard -----" + ANSIFormatter.ANSI_RESET);

            for (Integer i : finalScoreBoard.keySet()) {
                for (String s : finalScoreBoard.get(i)) {
                    System.out.println(ANSIFormatter.ANSI_RED + i + "_ " + ANSIFormatter.ANSI_RESET + s + " with " + players.get(s).getPoints() + " points and " + players.get(s).getNumObjectivesReached() + " objectives reached.");
                }
            }

            Timer finalTimer = new Timer(true);
            finalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0); //status 0 -> no errors
                }
            },6000); //6 seconds

        }
        if (selectedView == 2) { //GUI
            // there will be an update notified to GUIGameController. When this notification arrives then I change the screen
            if (guiGameController != null) {
                Map<String, Player> players = new HashMap<>();
                for (Player p : playersInTheGame) {
                    players.put(p.getNickname(), p);
                }
                List<Player> winners = new ArrayList<>();

                for (String s : finalScoreBoard.get(1)) { // I only pass the players in first position to the gui
                    winners.add(players.get(s));
                }
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
        this.lastMoves = lastMoves;
        System.out.println("LAST MOVES " + this.lastMoves);
    }



    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     */
    @Override
    public void handleDisconnection() {
        System.out.println("A disconnection happened. Closing the game.");
        synchronized (disconnectionLock) {
            if (selectedView == 1) {
                if(!showWinnerArrived) {
                    aDisconnectionHappened = true;
                    handleDisconnectionFunction();
                }
            }
            if (selectedView == 2) {
                aDisconnectionHappened = true; // It is used to block other things as soon as a disconnection occurs
                synchronized (actionLock){
                    actionLock.notify(); //to stop the waiting of something that will never arrive
                }
            }
        }
    }



    /**
     * This method manages disconnections
     */
    public void handleDisconnectionFunction() {
        inGame = false;
        if (this.executor != null) {
            this.executor.shutdown();
        }
        if (this.schedulerToCheckReceivedHeartBeat != null) {
            this.schedulerToCheckReceivedHeartBeat.shutdown();
        }
        if (this.schedulerToSendHeartbeat != null) {
            this.schedulerToSendHeartbeat.shutdown();
            // firstly closing the heart beat
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0); //status 0 -> no errors
            }

        }, 2000);
    }



    /**
     * This method is used to check the disconnections through RMI protocol
     */
    @Override
    public void heartbeat() {
        lastHeartbeatTime = System.currentTimeMillis();

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
     * This method uses a scheduler to administrate the heartbeat mechanism.
     */
    private void startHeartbeatMonitor() {
        this.schedulerToCheckReceivedHeartBeat = Executors.newScheduledThreadPool(1);
        var lambdaContext = new Object() {
            ScheduledFuture<?> heartbeatTask;
        };
        lambdaContext.heartbeatTask = this.schedulerToCheckReceivedHeartBeat.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastHeartbeatTime) / 1000 > TIMEOUT) {
                //when server isn't reached / a disconnection happened
                if (lambdaContext.heartbeatTask != null && !lambdaContext.heartbeatTask.isCancelled()) {
                    lambdaContext.heartbeatTask.cancel(true); //closes schedulerToCheckReceivedHeartBeat
                }


                    handleDisconnection();

            }
        }, 0, TIMEOUT, TimeUnit.SECONDS);
    }



    /**
     * Getter method
     * @return playersInTheGame is a list containing all the Players playing the specific Game
     */
    public List<Player> getPlayersInTheGame() {
        return playersInTheGame;
    }



    /**
     * Getter method
     * @return resourceCard1 of the market
     */
    public PlayableCard getResourceCard1() {
        return resourceCard1;
    }



    /**
     * Getter method
     * @return resourceCard2 of the market
     */
    public PlayableCard getResourceCard2() {
        return resourceCard2;
    }



    /**
     * Getter method
     * @return goldCard1 of the market
     */
    public PlayableCard getGoldCard1() {
        return goldCard1;
    }



    /**
     * Getter method
     * @return goldCard2 of the market
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



    /**
     * Getter method
     * @return the network settings
     */
    public Settings getNetworkSettings() {
        return networkSettings;
    }



    /**
     * Getter method
     * @return personalPlayer
     */
    public Player getPersonalPlayer() {
        return personalPlayer;
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
     * @return commonObjective1 in the game
     */
    public ObjectiveCard getCommonObjective1() {
        return commonObjective1;
    }



    /**
     * Getter method
     * @return common objective 2
     */
    public ObjectiveCard getCommonObjective2() {
        return commonObjective2;
    }



    /**
     * Getter method
     * @return the gameController
     */
    public GameControllerInterface getGameController() {
        return gameController;
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
     * @return guiGamestateLock
     */
    public Object getGuiGamestateLock() {
        return guiGamestateLock;
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
     * @param b (true if a disconnection happened, false otherwise)
     */
    public void setADisconnectionHappened(boolean b) {
        this.aDisconnectionHappened=b;
    }



    /**
     * Setter method
     * @param nickname is the nickname we want to set
     * @return true if the nickname is set correctly
     */
    public boolean setNickname(String nickname) {
        try {
            chooseNickname(nickname);
            this.personalPlayer.setNickname(nickname);
            this.nicknameSet = true;
        } catch (NicknameAlreadyTakenException e) {
            this.nicknameSet = false;
        }
        System.out.println("il nickname è stato settato a: " + this.personalPlayer.getNickname());
        return this.nicknameSet;
    }



    /**
     * Setter method
     * @param guiGameController
     */
    public void setGuiGameController(GUIGameController guiGameController) {
        this.guiGameController = guiGameController;
    }



    /**
     * Setter method
     * @param guiLobbyController
     */
    public void setGuiLobbyController(GUILobbyController guiLobbyController) {
        this.guiLobbyController = guiLobbyController;
    }



    /**
     * Setter method
     * @param ctr
     */
    public void setGuiPawnsController(GUIPawnsController ctr) {
        this.GUIPawnsController = ctr;
    }



    /**
     * Setter method
     * @param ctr
     */
    public void setGuiBaseCardController(GUIBaseCardController ctr) {
        this.guiBaseCardController = ctr;
    }



    /**
     * Setter method
     * @param done
     */
    public void setDone(boolean done) {
        this.done = done;
    }



    /**
     * Setter method
     * @param ctr
     */
    public void setGuiObjectiveController(GUIObjectiveController ctr) {
        this.guiObjectiveController = ctr;
    }

}

