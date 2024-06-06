package CODEX.distributed.Socket;


import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.*;

import CODEX.utils.executableMessages.clientMessages.*;
import CODEX.view.GUI.GUIBaseCardController;
import CODEX.view.GUI.GUIGameController;
import CODEX.view.GUI.GUIObjectiveController;
import CODEX.view.GUI.InterfaceGUI;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

//PER LA PROSSIMA SETTIMANA: CHIEDIAMO SE DOBBIAMO GESTIRE CASI DI PERDITA DI MESSAGGI IN RETE
//se vogliamo gestire il caso di perdita di messaggi nella trasmissione in rete li numeriamo (anche i messaggi di ping)
//e quando leggiamo un messaggio andiamo a controllare che abbiamo il numero successivo all'ultimo messaggio arrivato,
//se si tratta del secondo ping (perchè per sicurezza ne mandiamo due) non richiediamo il ping precedente se invece si
//tratta di update: richiediamo l'update precedente. Questo implica di tenere lato client e lato server una lista di
//ultimi messaggi inviati da cui pescare il messaggio mancante quando viene richiesto

/**
 * This class represents the Client who chose TCP as network protocol.
 * It listens to the SCKMessage sent by the ClientHandlerThread through the socket
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK implements ClientGeneralInterface {
    private boolean guiClosed=false;
    private boolean aDisconnectionHappened=false;
    private final Object disconnectionLock=new Object();
    private boolean errorState = false;
    private boolean finishedSetup=false;
    private HashSet<Integer> lobbyId;
    private final Socket socket;
    private GUIGameController guiGameController=null;

    public Player getPersonalPlayer() {
        return personalPlayer;
    }

    private Player personalPlayer;
    private int selectedView;
    private InterfaceTUI tuiView;
    //private InterfaceGUI guiView;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    //private final Thread threadCheckConnection;
    private Player player; //the nickname is saved somewhere
    private List<Player> playersInTheGame;

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

    //ATTENZIONE: se si chiama un metodo della ClientActionsInterface all'interno di un metodo di update bisogna per forza
    //usare un thread perchè i metodi della ClientActionsInterface aspettano l'OK di ritorno che non può venire letto
    //dal ClientSCK se si è ancora fermi sull'update che ha chiamato un metodo della ClientActionsInterface.
    //Questo accade perchè per fare gli update in ordine vengono letti uno alla volta.

    /**
     * Constructor method
     * @throws IOException
     */
    public ClientSCK() throws IOException { //we call this constructor after we ask the IP address and the port of the server
        this.socket = new Socket();
        // this.socket.connect(new InetSocketAddress(Settings.SERVER_NAME, Settings.PORT), 1000); //the address and the port of the server
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 1085; // Porta del server
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
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
                    if (!aDisconnectionHappened) { //(l'evento di disconnessione viene notificato tramite update e gli update vengono fatti in ordine)
                        try {
                            SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                            if (sckMessage != null) {
                                //System.out.println("messaggio ricevuto da ClienSCK non nullo");
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
                                /*
                                System.out.println("lost connection...Bye, bye");
                                try { //devo fermare i thread lanciati all'interno di questo thread
                                    inputStream.close();
                                    outputStream.close();
                                    socket.close();
                                    running = false;
                                    inGame = false;
                                } catch (IOException ex) { //this catch is needed for the close statements
                                    //throw new RuntimeException(ex);
                                }
                                if (this.timer != null) { //this is null if the game is not already started
                                    this.timer.cancel(); //we don't need to check the connection anymore
                                }
                                break; //se per esempio il flusso viene interrotto (dovrebbe venire lanciata un eccezione di Input/Output)

                                 */
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

    //we have to read this stream every time there is a server update to the client (->in the Thread local to server we modify this stream)
    //public SCKMessage receivedMessage () throws IOException, ClassNotFoundException {
    //    //the socket stream has been changed in the Thread (locally to the server)
    //    return (SCKMessage) inputStream.readObject(); //we are reading the object written in the inputStream
    //}


    public HashSet<Integer> getAvailableLobbies() throws RemoteException {
        checkAvailableLobby(); // update in the clientsck
       return lobbyId;
    }


    public boolean setNickname(String nickname) {
        this.personalPlayer.setNickname(nickname);
        this.nicknameSet = true;
        if (errorState){
            this.nicknameSet = false;
        }
        //} catch (NicknameAlreadyTakenException e) {  //DA RISOLVERE!! ALTRIMENTI NON POSSIAMO COMUNICARE QUANDO IL NCKNM è SBAGLIATO
        //this.nicknameSet = false;
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
                    handleDisconnection();
                    /*
                    System.out.println("lost connection....Bye, bye");
                    try { //devo fermare i thread lanciati all'interno di questo thread
                        inputStream.close();
                        outputStream.close();
                        socket.close(); //the ClientSCK will receive an exception
                        running = false;
                    } catch (IOException ex) { //this catch is needed for the close statements
                        //throw new RuntimeException(ex);
                    }

                     */
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
        //al posto dello switch se l'attributo Event del sckMessage è diverso da null si tratta di un update, altrimenti è un ServerMessage (attributo ServerMessage del sckMessage)

        if (sckMessage.getEvent() != null) { //we have an update
            sckMessage.getEvent().executeSCK(this);
        }else{ //we have a ServerMessage
            sckMessage.getServerMessage().execute(this);
        }

       /*
        switch (sckMessage.getMessageEvent()) { //lasciati commentati gli update per cui non è stato creato un evento del package events

            case UPDATED_CHAT->{  //non è stato creato un nuovo Event del package events per questo
                //we have to change the view and the local model
                updateChat((Chat) sckMessage.getObj().get(0));
            }

            case UPDATED_NICKNAME->{ //non è stato creato un nuovo Event del package events per questo
                //we have to change the view and the local model
                updateNickname((Player) sckMessage.getObj().get(0), (String) sckMessage.getObj().get(1));
            }


        */
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
                if(errorState){
                    System.out.println("Nickname is already taken! Please try again.");
                    errorState=false;
                    ok=false;
                }
            }
            personalPlayer.setNickname(nickname);
            System.out.println("Nickname correctly selected!");
            this.checkAvailableLobby();
            printLobby(lobbyId);
            ok=false;
            int gameSelection=0;
            while(!ok) {
                System.out.println("Type -1 if you want to create a new lobby, or the lobby id if you want to join it (if there are any available)");
                System.out.println("Type -2  to refresh the available lobbies.");
                try {
                    sc=new Scanner(System.in);
                    gameSelection = sc.nextInt();
                    if(gameSelection==-2){
                        this.checkAvailableLobby();
                        if(!(lobbyId.isEmpty())) {
                            System.out.println("If you want you can join an already created lobby. These are the ones available:");
                            printLobby(lobbyId);
                        }else {
                            System.out.println("There are no lobby available");
                        }
                    }
                    else if((gameSelection!=-1)&&(!lobbyId.contains(gameSelection))){
                        System.out.println("You wrote a wrong ID, try again.");
                    }else{
                        ok = true;
                    }
                } catch (InputMismatchException e) {
                    System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                }
            }
            try {
                ok=false;
                while(!ok) {
                    sc=new Scanner(System.in);
                    if (gameSelection == -1) {
                        System.out.println("How many players would you like to join you in this game?");
                        while(!ok) {
                            try {
                                sc=new Scanner(System.in);
                                gameSelection = sc.nextInt();
                                ok=true;
                            } catch (InputMismatchException e) {
                                System.out.println(ANSIFormatter.ANSI_RED + "Please write a number." + ANSIFormatter.ANSI_RESET);
                            }
                        }
                        createLobby(personalPlayer.getNickname(), gameSelection); //the controller (server side) doesn't have other exceptions (so we can't have an errorState here)
                        System.out.println("Successfully created a new lobby with id: " + this.gameID);
                    } else if (lobbyId.contains(gameSelection)) {
                        try {
                            addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                            if(errorState){
                                System.out.println(ANSIFormatter.ANSI_RED + "The game you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                                errorState=false;
                            }else{
                                System.out.println("Successfully joined the lobby with id: " + this.gameID);
                                checkNPlayers(); //this method in the server side makes the game start
                                if(errorState){ //l'eccezione lato server in checkNPlayers però non è ancora stata aggiunta
                                    System.out.println("The game is already started!");
                                    errorState=false;
                                }else {
                                    ok=true;
                                }
                            }
                        } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException e) { //da togliere
                            //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                        } //counter
                    } else {
                        System.out.println("You wrote a wrong id, try again!"); //nel caso non ci siano lobby non si esce più da questo ciclo (perchè la gameSelection è scritta prima del ciclo)
                    }
                }
            }catch (RemoteException |NotBoundException e){ //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                System.out.println("Unable to communicate with the server! Shutting down.");
                System.exit(-1);
            }
        } else {
            // NOTHING
//            String[] network = new String[1];
//            network[0] = "TCP";
//            InterfaceGUI.main(network);
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
    public int getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }


    public void checkAvailableLobby(){
        synchronized (actionLock) {
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
    public void checkNPlayers(){
        synchronized (actionLock) {
            ClientMessage clientMessage=new CheckNPlayers();
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
            /*
            while (!responseReceived) {
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

             */
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




        /*
        synchronized (actionLock) {
            ClientMessage clientMessage=new LeaveGame(nickname);
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

         */
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

        if (playersInTheGame != null) {
            for (Player p : playersInTheGame) {
                if (boardOwner.equals(p.getNickname())) {
                    p.setBoard(board);
                }
            }
        }

        if (selectedView == 1) {
            System.out.println("I received the board di un altro.");
        } else if (selectedView == 2) {
        if (guiGameController != null) {
            System.out.println("AAAAAAAAAAAAA!!! I received the board di un altro!!! AAAAAAAAAAAAAAA");
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
            //guiView.showUpdatedResourceDeck(this.resourceDeck)
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
            //guiView.updateGoldDeck(goldDeck)
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
        if (playersInTheGame!=null) {
            for (Player p : playersInTheGame) {
                if (playerNickname.equals(p.getNickname())) {
                    p.setPlayerDeck(playerDeck);
                }
            }
        }else {
            System.out.println("null in updatePlayerDeck");
        }
        if (selectedView == 1) {

        } else if (selectedView == 2) {
            //guiView.updatePlayerDeck(player, playerDeck)
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
                                }catch (RemoteException |NotBoundException e){ //sarebbe 'ignored'
                                    System.out.println("Unable to communicate with the server! Shutting down.");
                                    System.exit(-1);
                                }catch (CardNotOwnedException e){ //questa eccezione però non viene lanciata da nessuno (nè dal controller nè dalla tui)
                                    System.out.println("You don't own this card.");
                                }
                            }

                        }).start();;

                    } else if (selectedView == 2) {
                        //gui
                        synchronized (guiLock){
                            guiLock.notify();
                        }
                    }

            }
        }
    }

    @Override
    public void finishedSetUpPhase() throws RemoteException {
        updateRound(playersInTheGame);
    }

    @Override
    public void showWinner(List<Player> winners) throws RemoteException {

    }

    @Override
    public void updateLastMoves(int lastMoves) throws RemoteException {

    }


    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.resourceCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard1.");
        } else if (selectedView == 2) {
            //guiView.updateResourceCard1(card)
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
            //guiView.updateResourceCard2(card)
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
            //guiView.updateGoldCard1(card)
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
            //guiView.updateGoldCard2(card)
            if(guiGameController!=null){
                guiGameController.updateGoldCard2(card);
            }
        }
    }

    @Override
    public void updateChat(Chat chat) throws RemoteException {
        //we have to change the view and the local model

        if (selectedView == 1) {
            System.out.println("You received a message (updateGoldCard2).");
        } else if (selectedView == 2) {
            //guiView.updateChat(chat)
        }
    }

    @Override
    public void updateChat(ChatMessage message) throws RemoteException {

    }

    @Override
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {
        //we have to change the view and the local model
        if (selectedView == 1) {
            System.out.println("I received the updatePawns.");
        } else if (selectedView == 2) {
            //guiView.updatePawns(player, pawn)
        }
    }

    @Override
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException { //taken from RMIClient
        if (selectedView == 1) { //TUI

            System.out.println("I received the updateRound.");
            playersInTheGame = newPlayingOrder; //when turnCounter==-1 we have to initialize this list
            if (this.turnCounter == 0) { //we enter here only one time: the second time that updateRound is called
                //the second time that updateRound is called we have all that is need to call playBaseCard (see the model server side)

                new Thread(() -> { //per riuscire a ricevere i ping (e rispondere con un pong)
                    try {
                        boolean choice = tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]);
                        playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0], choice);
                    } catch (NotBoundException|RemoteException ignored) { //non si verifica
                    }
                }).start();
            }
            if (this.turnCounter >= 1) { //we enter here from the third time included that updateRound is called
                //before starting the thread that prints the menu we communicate which is the player that is playing
                if (playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())) {
                    setIsPlaying(true);
                    System.out.println(ANSIFormatter.ANSI_GREEN + "It's your turn!" + ANSIFormatter.ANSI_RESET);
                } else {
                    setIsPlaying(false);
                    System.out.println(playersInTheGame.get(0).getNickname() + " is playing!");
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
            turnCounter++; //first time: -1 -> 0, second time 0 -> 1  so from the second time on we enter if(turnCounter>=1)
        }
        else if (selectedView == 2) { //GUI
            System.out.println("I received the updateRound.");
            playersInTheGame = newPlayingOrder;
            if (this.turnCounter == 0){
                //chiamo playBaseCard : se uso un thread per farlo posso continuare a ricevere e a rispondere a ping
            }
            if (this.turnCounter >= 1){
                //dico ai giocatori chi sta giocando e chi no
                if(guiGameController!=null){
                    guiGameController.updatePoints();
                    guiGameController.updateRound();
                }

                if (this.turnCounter == 1){ //questo è il terzo turno
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
                                /*
                                System.out.println("the connection has been interrupted....Bye bye");
                                try { //we close all we have to close
                                    running=false;
                                    inGame=false;
                                    inputStream.close();
                                    outputStream.close();
                                    socket.close();
                                } catch (IOException ex) { //needed for the close clause
                                    throw new RuntimeException(ex);
                                }
                                timer.cancel(); // Ferma il timer

                                 */
                            }
                        }else{ //there are no pongs received

                            try {
                                handleDisconnection();
                            } catch (RemoteException ignored) {

                            }
                            /*
                            System.out.println("the connection has been interrupted...Bye bye");
                            try {
                                running=false;
                                inGame=false;
                                inputStream.close();
                                outputStream.close();
                                socket.close();
                            } catch (IOException e) { //needed for the close clause
                                throw new RuntimeException(e);
                            }
                            timer.cancel(); // Ferma il timer

                             */
                        }
                    }
                }, 0, 10000); // Esegui ogni 10 secondi



            } else if (gameState.equals(Game.GameState.ENDING)) {

            }else if(gameState.equals(Game.GameState.ENDED)){ //we do not return to the lobby -> we have to close the connection and stop the threads
                System.out.println("game state: ENDED");
                running=false;
                inGame=false;
                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) { //needed for the close clause
                    throw new RuntimeException(e);
                }
                //the TimerTask that checks the connection should end by itself when the application ends
                this.timer.cancel(); //to be sure

            }
        } else if (selectedView == 2) {
            //guiView.updateGameState(game)
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame = true;
                System.out.println("The game has started!");
            }
        }
    }

    @Override
    public void handleDisconnection() throws RemoteException { //arriva prima l'update che mette inGame=true
        System.out.println("evento disconnessione arrivato");

        //chiudere stream, socket, timer e thread
        if(selectedView==1){ //TUI
            handleDisconnectionFunction();
        }
        else if(selectedView==2) { //GUI -> da migliorare perchè blocca la scelta della carta
            synchronized (guiLock){
                aDisconnectionHappened=true;
                guiLock.notify(); //nel caso in cui la gui sta facendo la wait di un evento (che dopo questa discconnessione non si verificherà mai)
            }


            /*
            synchronized (disconnectionLock) {
                aDisconnectionHappened=true;
                disconnectionLock.notify();
            }
            synchronized (disconnectionLock){
                while(!guiClosed){
                    try {
                        disconnectionLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

             */


            //handleDisconnectionFunction(); viene chiamata direttamente dalla gui

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


    //taken from RMIClient
    //caso in cui qualcuno lascia il gioco e la partita deve finire
    public void gameLeft() throws RemoteException{
        System.out.println("I received the update gameLeft.");
        if(inGame){ //solo se il gioco è iniziato qualcuno se ne può andare volontariamente....verrà prima settato lo stato del gioco ad ENDED oppre chiamato questo update??
            inGame=false;
            System.out.println(ANSIFormatter.ANSI_RED+"Someone left the game."+ANSIFormatter.ANSI_RESET);
            System.out.println("the game has to end...Bye bye");
            /*
            this.resetAttributes();
            System.out.println("Returning to lobby.\n\n\n\n\n\n\n");
            this.waitingRoom();

             */
        }
        //we do not return to the lobby -> we have to terminate everything
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) { //needed for the close clause
            throw new RuntimeException(e);
        }
        //the TimerTask that checks the connection should end by itself when the application ends
        this.timer.cancel(); //to be sure

    }



    //fine update

    //end of implementation of ClientGeneralInterface

    private void showMenuAndWaitForSelection(){ //syn perchè così legge il valore corrente di isPlaying
        Integer choice;
        if(selectedView==1) {
            choice=tuiView.showMenuAndWaitForSelection(this.getIsPlaying(),this.console);
            int intChoice=choice;
            boolean ok;
            if(intChoice!=-1) { //dopo ogni azione della tui si può ristampare il menù (possiamo farlo da qui o direttamente nella tui)
                try {
                    switch (intChoice) {
                        case 0:  // if (!aDisconnectionHappened)
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
                        case 6:
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
                        case 7:
                            boolean orientation = true;
                            PlayableCard card = null;
                            Coordinates coordinates;
                            card = tuiView.askPlayCard(sc, personalPlayer);
                            if (card != null) {
                                orientation = tuiView.askCardOrientation(sc);
                                coordinates = tuiView.askCoordinates(sc, card, personalPlayer.getBoard());
                                if (coordinates != null) {
                                    this.playCard(personalPlayer.getNickname(), card, coordinates, orientation);
                                    if(!errorState) {
                                        tmp = new ArrayList<>();
                                        tmp.add(resourceCard1);
                                        tmp.add(resourceCard2);
                                        tmp.add(goldCard1);
                                        tmp.add(goldCard2);
                                        card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                        this.drawCard(personalPlayer.getNickname(), card);
                                    }else{
                                        System.out.println("you can't play this card...returning to menu");
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
                            System.out.println("not yet implemented");
                    }
                } catch (RemoteException | NotBoundException e) {
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    System.exit(-1);
                }
            }
        }else{
            //gui
        }
    } //usciti da qui restituiamo il lock della syn e quindi se c'è stato un update su isPlaying la prossima chiamata ne terrà conto

    public boolean getInGame() {
        return this.inGame;
    }

    public Object getDisconnectionLock() {
        return disconnectionLock;
    }

    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
    }

    public void handleDisconnectionFunction() throws RemoteException{
        // TUI + GUI
        running=false;
        inGame=false;
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
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

    public void setGuiClosed(boolean guiClosed) {
        this.guiClosed = guiClosed;
    }

    public void setGuiGameController(GUIGameController guiGameController) {
        this.guiGameController=guiGameController;
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

    public Game.GameState getGameState () {
        return player.getGame().getState();
    }

    public Object getGuiLock(){
        return this.guiLock;
    }
    public boolean getFinishedSetup(){
        return this.finishedSetup;
    }
    public boolean getErrorState(){
        return this.errorState;
    }
}




