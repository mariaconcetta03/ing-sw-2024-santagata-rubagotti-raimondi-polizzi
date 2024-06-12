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
 * This class represents the Client who chose TCP as network protocol.
 * It listens to the SCKMessage sent by the ClientHandlerThread through the socket
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK implements ClientGeneralInterface {
    List<Pawn> availableColors;

    private boolean aDisconnectionHappened=false;
    private final Object disconnectionLock=new Object();
    private boolean errorState = false;

    private HashSet<Integer> lobbyId;
    private final Socket socket;
    private GUIGameController guiGameController=null;
    private int lastMoves=10;
    private final Object guiGamestateLock=new Object();
    private final Object guiPawnsControllerLock=new Object();
    private boolean secondUpdateRoundArrived=false;
    private boolean done=false;
    private GUIPawnsController GUIPawnsController=null;
    private final Object guiBaseCardControllerLock=new Object();
    private GUIBaseCardController guiBaseCardController=null;
    private final Object guiObjectiveControllerLock=new Object();
    private GUIObjectiveController guiObjectiveController=null;

    public Player getPersonalPlayer() {
        return personalPlayer;
    }

    private Player personalPlayer;
    private int selectedView;
    private InterfaceTUI tuiView;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    private List<Player> playersInTheGame;


    public void setDone(boolean done) {
        this.done = done;
    }

    public ObjectiveCard getCommonObjective1() {
        return commonObjective1;
    }

    public ObjectiveCard getCommonObjective2() {
        return commonObjective2;
    }

    private ObjectiveCard commonObjective1, commonObjective2;
    private Integer gameID;

    private Boolean running; //it is initialized true, when becomes false threadCheckConnection has to terminate.
    private Boolean responseReceived;
    public final Object actionLock;
    private final Object inputLock;
    private boolean isPlaying;
    private boolean inGame;
    private boolean pongReceived; //to check the connection
    private Timer timer;
    private Scanner sc;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;

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

    //ATTENZIONE: se si chiama un metodo della ClientActionsInterface all'interno di un metodo di update bisogna per forza
    //usare un thread perchè i metodi della ClientActionsInterface aspettano l'OK di ritorno che non può venire letto
    //dal ClientSCK se si è ancora fermi sull'update che ha chiamato un metodo della ClientActionsInterface.
    //Questo accade perchè per fare gli update in ordine vengono letti uno alla volta.

    /**
     * Constructor method
     * @throws IOException
     */
    public ClientSCK(String serverAddress) throws IOException { //we call this constructor after we ask the IP address and the port of the server
        this.socket = new Socket();
        // this.socket.connect(new InetSocketAddress(Settings.SERVER_NAME, Settings.PORT), 1000); //the address and the port of the server
        int port = 1085; // Porta del server
        SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);
        socket.connect(socketAddress);

        lobbyId = new HashSet<>();

        personalPlayer = new Player();
        this.inputLock = new Object();

        this.guiLock=new Object();


        //in this way the stream is converted into objects
        //forse però dovrei usare dei buffer per non perdere nessun messaggio
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //what ClientHandlerThread writes in its socket's output stream ends up here

        this.running = true;
        this.inGame=false; //this will become true when the state of the Game will change in STARTED
        this.responseReceived = false; //initialized false to enter the while inside every ClientGeneralInterface method
        this.actionLock = new Object();
        this.outputLock = new Object();
        new Thread(() -> {
            while (running) {
                synchronized (inputLock) {
                    if (!aDisconnectionHappened) {
                        try {
                            SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                            if (sckMessage != null) {

                                //il fatto che sia BLOCCANTE è POSITIVO: gli update vengono fatti in ordine di arrivo e quindi quando riceviamo SETUP_PHASE_2 siamo sicuri di aver veramente ricevuto già tutto

                                modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta

                            }

                        } catch (Exception e) { //se il server si disconnette
                            if(running) {
                                try {
                                    handleDisconnection();
                                } catch (RemoteException ex) {
                                    throw new RuntimeException(ex);
                                }

                            }
                        }


                    }

                }
            }
        }).start();
    }

    public List<Player> getPlayersInTheGame(){
        return this.playersInTheGame;
    }
    public void setLobbyId(List<Integer>list){
        lobbyId.addAll(list);
    }
    public void setResponseReceived(boolean responseReceived){
        this.responseReceived=responseReceived;
    }

    @Override
    public void okEventExecute(String nickname) {

    }



    public HashSet<Integer> getAvailableLobbies() throws RemoteException {
        checkAvailableLobby(); // update in the clientsck
       return lobbyId;
    }


    public boolean setNickname(String nickname) {
        this.personalPlayer.setNickname(nickname);
        this.nicknameSet = true;
        if (errorState&&!aDisconnectionHappened){
            this.nicknameSet = false;
        }else if(aDisconnectionHappened){
            try {
                handleDisconnection();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("il nickname è stato settato a: " + this.personalPlayer.getNickname());
        return this.nicknameSet;
    }



    /**
     * This method allows the Client to send, through the socket, a message to be read (using its input stream) by the ClientHandlerThread.
     * @param sckMessage is the message containing objects and Event relative to the action to perform
     * @throws IOException in case the Server is unreachable we shut down the Client.
     */
    public void sendMessage(SCKMessage sckMessage) throws IOException { //ATTENTION: this method is called ONLY inside ClientGeneralInterface methods
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
                    responseReceived = true; //per non fare iniziare la wait di qualche risposta
                    handleDisconnection();

                }
            }
        }
    }


    public void setPongReceived(Boolean received){ //to be used in ServerPong
        this.pongReceived=received;
    }

    //leggiamo l'evento per capire di che update si tratta e poi aggiorniamo quello che ci dice di aggiornare l'evento e chiamiamo infine sendMessage
    //non è l'update dei listeners (quello è in ClientHandlerThread: scriverà sull'input della socket)
    //con questo update andiamo a modificare le cose locali al client
    public void modifyClientSide(SCKMessage sckMessage) throws IOException {
        // se l'attributo Event del sckMessage è diverso da null si tratta di un update, altrimenti è un ServerMessage (attributo ServerMessage del sckMessage)

        if (sckMessage.getEvent() != null) { //we have an update
            sckMessage.getEvent().executeSCK(this);
        }else{ //we have a ServerMessage
            sckMessage.getServerMessage().execute(this);
        }


    }



    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() throws RemoteException {
        this.isPlaying=false;
        this.sc=new Scanner(System.in);
        this.console = new BufferedReader(new InputStreamReader(System.in));
        boolean ok=false;
        int errorCounter=0;
        if (selectedView == 1) {
            tuiView = new InterfaceTUI();
            tuiView.printWelcome();
            String nickname=null;
            while(!ok){
                if(errorCounter==3){
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    System.exit(-1);
                }
                nickname = tuiView.askNickname(sc);
                try {
                   this.chooseNickname(nickname);
                    ok=true;
                } catch (RemoteException | NotBoundException e) { //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                    errorCounter++;
                    System.out.println();
                }
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

            while(!ok) {
                boolean secondOk=false;
                while ((!secondOk)) {
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
                            secondOk = true;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                    }
                }
                try {

                    boolean thirdOk=false;
                    if (gameSelection == -1) {
                        System.out.println("How many players would you like to join you in this game?");
                        while (!thirdOk) {
                            try {
                                sc = new Scanner(System.in);
                                gameSelection = sc.nextInt();
                                thirdOk = true;
                            } catch (InputMismatchException e) {
                                System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                            }
                        }
                        createLobby(personalPlayer.getNickname(), gameSelection); //the controller (server side) doesn't have other exceptions (so we can't have an errorState here)
                        System.out.println("Successfully created a new lobby with id: " + this.gameID);
                        ok=true;
                    } else if (lobbyId.contains(gameSelection)) {
                        try {
                            addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                            if (errorState&&!aDisconnectionHappened) {
                                System.out.println(ANSIFormatter.ANSI_RED + "The game you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                                errorState = false;
                            }else if(aDisconnectionHappened){
                                handleDisconnection();
                            } else {
                                System.out.println("Successfully joined the lobby with id: " + this.gameID);
                                checkNPlayers(); //this method in the server side makes the game start
                                /*
                                if (errorState) { //l'eccezione lato server in checkNPlayers però non è ancora stata aggiunta
                                    System.out.println("The game is already started!");
                                    errorState = false;
                                } else {
                                ok = true;

                                 */
                                ok=true;
                            }
                        } catch (Exception ignored) { //da togliere
                            //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                        } //counter
                    } else {
                        System.out.println("You wrote a wrong id, try again!"); //nel caso non ci siano lobby non si esce più da questo ciclo (perchè la gameSelection è scritta prima del ciclo)
                    }


                } catch (RemoteException | NotBoundException e) { //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    System.exit(-1);
                }
            }
        } else { //GUI

        }

    }

    public void printLobby(HashSet<Integer> ids){
        if(!ids.isEmpty()) {
            for (Integer i : ids) {
                System.out.println(i + " ");
            }
        }else{
            System.out.println("There are no lobby available.");
        }
    }

    //GETTER & SETTER


    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }


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
    public List<Pawn> getAvailableColors(){
        checkAvailableColors();
        return this.availableColors;
    }
    public void setAvailableColors(List<Pawn> availableColors){
        this.availableColors=availableColors;
    }
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


    //these classes implement clientGeneralInterface
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
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

    @Override
    public void chooseNickname(String nickname) throws RemoteException, NotBoundException {
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

    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException {
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

    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
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

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        synchronized (actionLock) {
            ClientMessage clientMessage=new PlayBaseCard(nickname,baseCard,orientation);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
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

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        synchronized (actionLock) {
            ClientMessage clientMessage=new ChooseObjectiveCard(chooserNickname,selectedCard);
            try {
                sendMessage(new SCKMessage(clientMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
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

    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException, NotBoundException {
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

    @Override
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException {
        //abbiamo deciso che quando un giocatore vuole lasciare il gioco il server riceve una disconnessione
        //if (!aDisconnectionHappened) non lo controllo perchè se viene rilevata sulla gui la disconnessione non viene premuto il tasto che porta a questa funzione
        synchronized (disconnectionLock) {
            if (!aDisconnectionHappened) { //per sicurezza lo controllo
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





    //update




    @Override
    public void updateBoard(String boardOwner, Board board, PlayableCard newCard) throws RemoteException {
        //we have to change the view and the local model
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


        if (selectedView == 1) {

        } else if (selectedView == 2) {
            if (guiGameController != null) {

                guiGameController.updateBoard(boardOwner, newCard);
            }
        }
    }

    @Override
    public void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException {
        //we have to change the view and the local model
        this.resourceDeck=resourceDeck;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceDeck.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceDeck();
            }
        }
    }

    @Override
    public void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException {
        //we have to change the view and the local model
        this.goldDeck=goldDeck;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldDeck.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateGoldDeck();
            }
        }
    }

    @Override
    public void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException {
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

        if (selectedView == 1) {

        } else if (selectedView == 2) {

            if(!(playerNickname.equals(personalPlayer.getNickname()))){
                if(guiGameController!=null){
                    guiGameController.updatePlayerDeck(playerNickname,playerDeck);
                }
            }
        }
    }

    //taken from RMIClient
    @Override
    public void updatePersonalObjective(ObjectiveCard card, String nickname) throws RemoteException {
        if (personalPlayer.getNickname().equals(nickname)) {
            personalPlayer.addPersonalObjective(card);
            if (personalPlayer.getPersonalObjectives().size() == 2) {
                    if (selectedView == 1) {
                        System.out.println("I received the updatePersonalObjective.");

                        new Thread(()->{ //per ricevere i ping
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
                                }catch (RemoteException |NotBoundException e){ //sarebbe 'ignored'
                                    System.out.println("Unable to communicate with the server! Shutting down.");
                                    System.exit(-1);
                                }catch (CardNotOwnedException e){ //questa eccezione però non viene lanciata da nessuno (nè dal controller nè dalla tui)
                                    System.out.println("You don't own this card.");
                                }
                            }

                        }).start();;

                    } else if (selectedView == 2) {

                        //se ho due objective card sicuramente ho anche guiBaseCardController
                        guiBaseCardController.updateGameState();
                    }

            }
        }
    }

    @Override
    public void finishedSetUpPhase() throws RemoteException {
        updateRound(playersInTheGame);
    }

    @Override
    public void showWinner(Map<Integer, List<String>> finalScoreBoard) throws RemoteException {
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
            // ci sarà un update notificato al GUIGameController. Quando arriva questa notifica allora cambio la schermata
            if(guiGameController!=null){
                Map<String, Player> players=new HashMap<>();
                for(Player p: playersInTheGame){
                    players.put(p.getNickname(), p);
                }
                List<Player> winners=new ArrayList<>();

                for (String s : finalScoreBoard.get(1)) { //passo alla gui solo i giocatori in prima posizione
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
        this.lastMoves=lastMoves;
        System.out.println("LAST MOVES "+this.lastMoves);
    }


    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.resourceCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard1.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceCard1(card);
            }
        }
    }

    @Override
    public void updateResourceCard2(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.resourceCard2=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard2.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateResourceCard2(card);
            }
        }
    }

    @Override
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.goldCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldCard1.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateGoldCard1(card);
            }
        }
    }

    @Override
    public void updateGoldCard2(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.goldCard2=card;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldCard2.");
        } else if (selectedView == 2) {

            if(guiGameController!=null){
                guiGameController.updateGoldCard2(card);
            }
        }
    }

    @Override
    public void updateChat(Integer chatIdentifier, Chat chat) throws RemoteException {
        //we have to change the view and the local model

        if (selectedView == 1) {
            System.out.println("You received a message (updateGoldCard2).");
        } else if (selectedView == 2) {

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
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    @Override
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException { //taken from RMIClient
        System.out.println("I received the updateRound.");
        playersInTheGame = newPlayingOrder;
        if (selectedView == 1) { //TUI
            //when turnCounter==-1 we have to initialize this list
            if (this.turnCounter == 0) { //we enter here only one time: the second time that updateRound is called
                //the second time that updateRound is called we have all that is need to call playBaseCard (see the model server side)

                new Thread(() -> { //per riuscire a ricevere i ping (e rispondere con un pong)
                    try {
                        boolean choice = tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]);
                        playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0], choice);
                        checkBaseCardPlayed();
                    } catch (NotBoundException|RemoteException ignored) { //non si verifica
                    }
                }).start();
            }
            if (this.turnCounter >= 1) { //we enter here from the third time included that updateRound is called
                //before starting the thread that prints the menu we communicate which is the player that is playing
                if(lastMoves>0){
                    if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                        setIsPlaying(true);
                        System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                        if(lastMoves<=playersInTheGame.size()){
                            System.out.println("This is your last turn! You will not draw.");
                        }
                    } else {
                        setIsPlaying(false);
                        System.out.println(playersInTheGame.get(0).getNickname() + " is playing!");
                    }
                }else{
                    inGame=false;
                }
                if (this.turnCounter == 1) { //we enter here the third time (finishedSetupPhase2())
                    //we have to start the thread that prints the menu
                    new Thread(() -> {
                        while (inGame) { //quando la connessione viene persa/il Game termina inGame deve venire settato a false
                            //il player può usare il menù completo solo se isPlaying==true se no usa quello di base
                            showMenuAndWaitForSelection();
                        }
                    }).start();
                }
            }
            if(this.turnCounter==-1){
                Executor executor= Executors.newSingleThreadExecutor();
                //taken from RMI
                executor.execute(() -> {
                    boolean ok=false;
                    try {
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
                    } catch (NotBoundException |RemoteException e) { //ignored
                        System.out.println("Unable to communicate with the Server. Shutting down.");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                });


            }
            turnCounter++; //first time: -1 -> 0, second time 0 -> 1  so from the second time on we enter if(turnCounter>=1)
        }
        else if (selectedView == 2) { //GUI
            System.out.println("I received the updateRound.");
            //playersInTheGame = newPlayingOrder;
            if(this.turnCounter == -1){ //chiedo le pawn
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
                //chiamo playBaseCard : se uso un thread per farlo posso continuare a ricevere e a rispondere a ping



                //se arriva il secondo updateRound: ho già fatto la scelta della pawn e quindi ho il GUIPawnsController
                GUIPawnsController.updateGameState();
            }
            if (this.turnCounter >= 1){

                if(lastMoves>0) { //viene inizializzato a 10 e viene cambiato con un altro valore solo quando arriva il primo updateLastMovesEvent (dai successivi updateLastMovesEvent viene decrementato)

                    if (lastMoves <= playersInTheGame.size()) {
                        System.out.println("This is your last turn! You will not draw.");
                        //dobbiamo impedire al giocatore di pescare le carte settando un booleano
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(true); //settiamo lastTurn a true
                        }
                    }else {
                        if (guiGameController != null) {
                            guiGameController.updatePoints();
                            guiGameController.updateRound(false); //settiamo lastTurn a true

                        }
                    }

                }else{
                    inGame=false;
                }
                if (this.turnCounter == 1){ //questo è il terzo turno
                    //dal terzo turno è possibile vedere il menù e selezionarne i punti del menù, la TUI qui lancia un thread che va per tutta la partita

                    guiObjectiveController.updateGameState();
                }
            }
            turnCounter++; //quando il model fa un updateRound per la terza volta siamo in turnCounter==1 e si può iniziare a selezionare il menù
        }
    }


    //taken from RMIClient
    @Override
    public void updateCommonObjectives(ObjectiveCard card1, ObjectiveCard card2) throws RemoteException{
        System.out.println("I received the updateCommonObjectives.");
        this.commonObjective1=card1;
        this.commonObjective2=card2;
    }

    @Override
    public void updateGameState(Game.GameState gameState) throws RemoteException {
        //we have to change the view and the local model
        System.out.println("I received the updateGameState.");
        if (selectedView == 1) {
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame=true;
                System.out.println("The game has started!");


                //to check the connection
                this.pongReceived=true; //initialization
                this.timer = new Timer(true); //isDaemon==true -> maintenance activities performed as long as the application is running
                //we need to use ping-pong messages because sometimes the connection seems to be open (we do not receive any I/O exception) but it is not.
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(pongReceived) {
                            pongReceived=false;
                            try {
                                ClientMessage clientMessage=new ClientPing();
                                sendMessage(new SCKMessage(clientMessage));
                            } catch (IOException e) { //the connection doesn't is open (and it doesn't seem to be open)
                                try {
                                    handleDisconnection();
                                } catch (RemoteException ignored) {

                                }

                            }
                        }else{ //there are no pongs received

                            try {
                                handleDisconnection();
                            } catch (RemoteException ignored) {

                            }

                        }
                    }
                }, 0, 10000); // Esegui ogni 10 secondi



            } else if (gameState.equals(Game.GameState.ENDING)) {

            }else if(gameState.equals(Game.GameState.ENDED)){ //we do not return to the lobby -> we have to close the connection and stop the threads


            }
        } else if (selectedView == 2) {
            //guiView.updateGameState(game)
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame = true;
                System.out.println("The game has started!");

            }
        }
    }
    public Object getGuiGamestateLock() {
        return guiGamestateLock;
    }

    @Override
    public void handleDisconnection() throws RemoteException { //arriva prima l'update che mette inGame=true
        System.out.println("evento disconnessione arrivato");

        synchronized (disconnectionLock) {
            //chiudere stream, socket, timer e thread
            if (selectedView == 1) { //TUI
                handleDisconnectionFunction();
            } else if (selectedView == 2) {
                synchronized (guiLock) {
                    aDisconnectionHappened = true;
                    guiLock.notify(); //nel caso in cui la gui sta facendo la wait di un evento (che dopo questa discconnessione non si verificherà mai)
                }

                //handleDisconnectionFunction(); viene chiamata direttamente dalla gui

            }
        }

    }

    @Override
    public void heartbeat() throws RemoteException {
        //c'è solo in RMI...
    }

    @Override
    public void startHeartbeat() throws RemoteException {
        //c'è solo in RMI...
    }






    //fine update

    //end of implementation of ClientGeneralInterface

    private void showMenuAndWaitForSelection(){

        if(selectedView==1) {
            int intChoice=tuiView.showMenuAndWaitForSelection(this.getIsPlaying(),this.console);
            boolean ok;
            if(intChoice!=-1) { //dopo ogni azione della tui si può ristampare il menù (possiamo farlo da qui o direttamente nella tui)
                try {
                    switch (intChoice) {
                        case 0:
                            inGame = false;
                            leaveGame(personalPlayer.getNickname());
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
                            String nickname = sc.nextLine();
                            for (Player player : playersInTheGame) {
                                if (player.getNickname().equals(nickname)) {
                                    ok = true;
                                    tuiView.printTable(player.getBoard()); //TODO non sto aggiornando playerInTheGame quando arriva update
                                }
                            }
                            if (!ok) {
                                System.out.println("There is no such player in this lobby! Try again.");
                            }
                            break;
                        case 5:
                            tuiView.printScoreBoard(playersInTheGame);
                            break;
                        case 6: tuiView.printLegend();
                            break;
                        case 7: System.out.println(ANSIFormatter.ANSI_BLUE+"It's "+playersInTheGame.get(0).getNickname()+"'s turn!"+ANSIFormatter.ANSI_RESET);
                            break;
                        case 8:
                            System.out.println("Do you want to send a message to everybody (type 1) or a private message (type the single nickname)?");
                            String answer = sc.nextLine();
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
                                    this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                    if((!errorState)&&(lastMoves>playersInTheGame.size())&&!aDisconnectionHappened) {
                                        tmp = new ArrayList<>();
                                        tmp.add(resourceCard1);
                                        tmp.add(resourceCard2);
                                        tmp.add(goldCard1);
                                        tmp.add(goldCard2);
                                        card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                        this.drawCard(personalPlayer.getNickname(), card);
                                    }else if(aDisconnectionHappened){
                                        handleDisconnection();
                                    }
                                    else{
                                        System.out.println("You can't play this card! Returning to menu..."); //@TODO differenziare eccezioni per non giocabilità e non abbastanza risorse?
                                        errorState=false; //to be used the next time
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




    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
    }

    public void handleDisconnectionFunction() throws RemoteException{
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
        } catch (IOException e) { //needed for the close clause
            throw new RuntimeException(e);
        }
        //the TimerTask that checks the connection should end by itself when the application ends
        if(this.timer!=null) {
            this.timer.cancel(); //to be sure
        }
        System.out.println("A disconnection happened.");

        System.exit(0);

    }





    public void setGuiGameController(GUIGameController guiGameController) {
        this.guiGameController=guiGameController;
    }



    public void setGuiLobbyController(GUILobbyController guiLobbyController) {
        this.guiLobbyController = guiLobbyController;
    }

    public Object getGuiPawnsControllerLock() {
        return this.guiPawnsControllerLock;
    }

    public void setGuiPawnsController(GUIPawnsController ctr) {
        this.GUIPawnsController=ctr;
    }

    public Object getGuiBaseCardControllerLock() {
        return this.guiBaseCardControllerLock;
    }

    public void setGuiBaseCardController(GUIBaseCardController ctr) {
        this.guiBaseCardController=ctr;
    }

    public Object getGuiObjectiveControllerLock() {
        return this.guiObjectiveControllerLock;
    }

    public void setGuiObjectiveController(GUIObjectiveController ctr) {
        this.guiObjectiveController=ctr;
    }


    public static class Settings { //this is an attribute. (qui ci sono indirizzo e porta del server locale
        static int PORT = 9090; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

        public static String getServerName() {
            return SERVER_NAME;
        }

        public static void setServerName(String serverName) {
            SERVER_NAME = serverName;
        }

    }


    public void setIsPlaying(boolean isPlaying){
        this.isPlaying=isPlaying;
    }

    public void setErrorState (boolean errorState) {
        this.errorState = errorState;
    }
    public boolean getIsPlaying(){ // c'è la syn nel metodo che lo chiama (showMenuAndWaitForSelection)
        return this.isPlaying;
    }
    public void setGameID(Integer gameID){
        this.gameID=gameID;
    }


    public boolean getErrorState(){
        return this.errorState;
    }
    public void checkChosenPawnColor(){
        ClientMessage clientMessage=new CheckChosenPawnColor();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void checkObjectiveCardChosen(){
        ClientMessage clientMessage=new CheckObjectiveCardChosen();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void checkBaseCardPlayed(){
        ClientMessage clientMessage=new CheckBaseCardPlayed();
        try {
            sendMessage(new SCKMessage(clientMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}