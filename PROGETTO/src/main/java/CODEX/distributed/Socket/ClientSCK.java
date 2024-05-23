package CODEX.distributed.Socket;


import CODEX.Exceptions.*;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.*;
import CODEX.utils.Event;
import CODEX.view.GUI.InterfaceGUI;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
    private boolean errorState = false;
    private HashSet<Integer> lobbyId;
    private final Socket socket;
    private Player personalPlayer;
    private int selectedView;
    private InterfaceTUI tuiView;
    //private InterfaceGUI guiView;
    private Board board;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    //private final Thread threadCheckConnection;
    private Player player; //the nickname is saved somewhere
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;
    public Integer gameID;

    private Boolean running; //it is initialized true, when becomes false threadCheckConnection has to terminate.
    private Boolean responseReceived;
    private final Object actionLock;
    private final Object inputLock;
    private boolean isPlaying;
    private boolean inGame;
    private boolean firstPongReceived; //to check the connection
    private boolean secondPongReceived; //to check the connection
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

        //in this way the stream is converted into objects
        //forse però dovrei usare dei buffer per non perdere nessun messaggio
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //what ClientHandlerThread writes in its socket's output stream ends up here

        this.running = true;
        this.responseReceived = false; //initialized false to enter the while inside every ClientGeneralInterface method
        this.actionLock = new Object();
        this.outputLock = new Object();

        /*
        //da rivedere più avanti
        //to control the status of the connection (a player can leave the game without any advice)
        threadCheckConnection= new Thread(()-> { //ci serve qualcosa su cui fare la syn?
            while (running) {
                synchronized (inputLock) {
                    PingMessage pingMessage = (PingMessage) this.inputStream.getObjectInputFilter(); //we receive 'ARE_YOU_STILL_CONNECTED'
                    try {
                        outputStream.writeObject(new PingMessage());
                        outputStream.flush();
                        outputStream.reset();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        },"CheckConnection"); //to be started when a Game is created (when we receive the msg ALL_CONNNECTED)


         */

        new Thread(() -> {
            while (running) {
                synchronized (inputLock) {
                    try {
                        SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                        if (sckMessage != null) {
                            System.out.println("the message isn't null");
                            //System.out.println("messaggio ricevuto da ClienSCK non nullo");
                            //il fatto che sia BLOCCANTE è POSITIVO: gli update vengono fatti in ordine di arrivo e quindi quando riceviamo SETUP_PHASE_2 siamo sicuri di aver veramente ricevuto già tutto
                            try {
                                modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta
                            } catch (Exception e) {
                                System.out.println("sono nel catch di modifyClientSide(sckMessage)");
                                System.out.println(sckMessage.getMessageEvent().toString());
                                System.err.println(e.getMessage());
                                System.err.println(e.getCause().getMessage());
                                try { //devo fermare i thread lanciati all'interno di questo thread
                                    inputStream.close();
                                    outputStream.close();
                                    socket.close();
                                    running = false;
                                    inGame=false;
                                } catch (IOException ex) { //this catch is needed for the close statements
                                    throw new RuntimeException(ex);
                                }
                                if(this.timer!=null){ //this is null if the game is not already started
                                    this.timer.cancel(); //we don't need to check the connection anymore
                                }
                                break;
                            }
                        } else {
                            System.out.println("the message is null");
                        }
                    } catch (Exception e) { //se il server si disconnette
                        System.err.println(e.getMessage());
                        System.out.println("sono nel catch di this.inputStream.readObject()");
                        try { //devo fermare i thread lanciati all'interno di questo thread
                            inputStream.close();
                            outputStream.close();
                            socket.close();
                            running = false;
                            inGame=false;
                        } catch (IOException ex) { //this catch is needed for the close statements
                            throw new RuntimeException(ex);
                        }
                        if(this.timer!=null){ //this is null if the game is not already started
                            this.timer.cancel(); //we don't need to check the connection anymore
                        }
                        break; //se per esempio il flusso viene interrotto (dovrebbe venire lanciata un eccezione di Input/Output)
                    }
                }

            }
        }).start();
    }

    //we have to read this stream every time there is a server update to the client (->in the Thread local to server we modify this stream)
    //public SCKMessage receivedMessage () throws IOException, ClassNotFoundException {
    //    //the socket stream has been changed in the Thread (locally to the server)
    //    return (SCKMessage) inputStream.readObject(); //we are reading the object written in the inputStream
    //}


    public boolean setNickname(String nickname) {
        try {
            chooseNickname(nickname);
            this.personalPlayer.setNickname(nickname);
            this.nicknameSet = true;
            //} catch (NicknameAlreadyTakenException e) {  //DA RISOLVERE!! ALTRIMENTI NON POSSIAMO COMUNICARE QUANDO IL NCKNM è SBAGLIATO
            //this.nicknameSet = false;
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
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
            try {
                responseReceived = false;
                outputStream.writeObject(sckMessage);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                try { //devo fermare i thread lanciati all'interno di questo thread
                    inputStream.close();
                    outputStream.close();
                    socket.close(); //the ClientSCK will receive an exception
                    running = false;
                } catch (IOException ex) { //this catch is needed for the close statements
                    throw new RuntimeException(ex);
                }
                System.err.println("Server not available!");
                System.exit(-1); //this is a shutdown of the VirtualMachine
            }
        }
    }


    //leggiamo l'evento per capire di che update si tratta e poi aggiorniamo quello che ci dice di aggiornare l'evento e chiamiamo infine sendMessage
    //non è l'update dei listeners (quello è in ClientHandlerThread: scriverà sull'input della socket)
    //con questo update andiamo a modificare le cose locali al client
    public void modifyClientSide(SCKMessage sckMessage) throws IOException {
        switch (sckMessage.getMessageEvent()) {
            case PONG -> { //sent by the server to say 'yes, I'm still connected' (in response to a ping message)
                if(this.firstPongReceived){
                    this.secondPongReceived=true; //abbiamo davvero bisogno di due booleani?
                }else{
                    this.firstPongReceived=true;
                }
            }
            case PING -> { //sent by the server to say 'are you still connected?'
                sendMessage(new SCKMessage(null,Event.PONG)); //to say to the server 'yes, I'm still connected'
            }
            case UPDATED_BOARD -> { //viene chiamato in playBaseCard per la prima volta
                //we have to change the view and the local model
                updateBoard((String) sckMessage.getObj().get(0), (Board) sckMessage.getObj().get(1));
            }
            case UPDATED_RESOURCE_DECK -> {
                //we have to change the view and the local model
                updateResourceDeck((PlayableDeck) sckMessage.getObj().get(0));
            }
            case UPDATED_GOLD_DECK->{
                //we have to change the view and the local model
                updateGoldDeck((PlayableDeck) sckMessage.getObj().get(0));
            }
            case UPDATED_PLAYER_DECK->{
                //we have to change the view and the local model
                newUpdatePlayerDeck((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1),(PlayableCard) sckMessage.getObj().get(2),(PlayableCard) sckMessage.getObj().get(3));
            }
            case UPDATED_RESOURCE_CARD_1->{
                //we have to change the view and the local model
                updateResourceCard1((PlayableCard) sckMessage.getObj().get(0));
            }
            case UPDATED_RESOURCE_CARD_2->{
                //we have to change the view and the local model
                updateResourceCard2((PlayableCard) sckMessage.getObj().get(0));
            }
            case UPDATED_GOLD_CARD_1->{
                //we have to change the view and the local model
                updateGoldCard1((PlayableCard) sckMessage.getObj().get(0));
            }
            case UPDATED_GOLD_CARD_2->{
                //we have to change the view and the local model
                updateGoldCard2((PlayableCard) sckMessage.getObj().get(0));
            }
            case UPDATED_CHAT->{
                //we have to change the view and the local model
                updateChat((Chat) sckMessage.getObj().get(0));
            }
            case UPDATED_PAWNS->{
                //we have to change the view and the local model
                updatePawns((Player) sckMessage.getObj().get(0), (Pawn) sckMessage.getObj().get(1));
            }
            case UPDATED_NICKNAME->{
                //we have to change the view and the local model
                updateNickname((Player) sckMessage.getObj().get(0), (String) sckMessage.getObj().get(1));
            }
            case UPDATED_ROUND,NEW_TURN->{ //equivalenti
                //we have to change the view and the local model
                updateRound((List<Player>) sckMessage.getObj().get(0)); //we have to call it three times before the game can start and the menu be printed
            }
            case UPDATED_COMMON_OBJECTIVES->{
                updateCommonObjectives((ObjectiveCard)sckMessage.getObj().get(0), (ObjectiveCard)sckMessage.getObj().get(1));
            }
            case UPDATED_PERSONAL_OBJECTIVE->{
                System.out.println("sono in case UPDATED_PERSONAL_OBJECTIVE");
                updatePersonalObjective((ObjectiveCard) sckMessage.getObj().get(0), (String) sckMessage.getObj().get(1));
            }
            case GAME_STATE_CHANGED->{ //subito dopo questo update c'è NEW_TURN ( quando si chiama game.startGame() )
                //we have to change the view and the local model
                System.out.println("game state changed"); //per il test
                //sempre in game.startGame (nel model) vengono chiamati poi tutti gli altri update per permermettere al client di avere una copia locale di quello che c'è sul server
                updateGameState((Game.GameState) sckMessage.getObj().get(0)); //threadCheckConnection.start() in updateRound
            }
            //va nel default dove ci sono i messaggi di errore
            /*
            case UNABLE_TO_PLAY_CARD->{
                showError(Event.UNABLE_TO_PLAY_CARD);
            }

             */
            case SETUP_PHASE_2->{
                //when we receive this update the player has all he needs to start the game (base card and objective card already chosen)
                finishedSetupPhase2(); //here we call updateRound for the third time (from now on we can print the menu)

            }
            case GAME_LEFT->{
                //when someone leaves the game the other players receive this update that makes them close their socket
                gameLeft();
            }
            case AVAILABLE_LOBBY -> {
                synchronized (actionLock) {
                    for(Object o: sckMessage.getObj()){
                        lobbyId.add((Integer) o);
                    }
                    responseReceived = true;
                    actionLock.notify();
                }
            }
            case OK -> { //...potremmo stampare anche il messaggio di ok....
                System.out.println("sono in case OK di ClientSCK");
                //questo if mi serve per i test per memorizzare il gameID
                synchronized (actionLock) {
                    if (sckMessage.getObj() != null) { //per usarlo nel test
                        this.gameID = (Integer) sckMessage.getObj().get(0);
                    }
                    this.responseReceived = true; //in this way the client is free to do the next action
                    actionLock.notify(); //per fermare la wait nei metodi della ClientGeneralInterface
                }
            }
            default -> { //qui ci finiscono tutti i messaggi di errore
                //stampiamo l'errore e poi permettiamo al client di proseguire
                synchronized (actionLock) {
                    errorState=true;
                    System.out.println(sckMessage.getMessageEvent().toString());
                    this.responseReceived = true;
                    actionLock.notify();
                }
            }
        }
    }

    private void newUpdatePlayerDeck(String s, PlayableCard playableCard, PlayableCard playableCard1, PlayableCard playableCard2) {
        PlayableCard[] playerDeck=new PlayableCard[3];
        playerDeck[0]=playableCard;
        playerDeck[1]=playableCard1;
        playerDeck[2]=playableCard2;
        if(s.equals(personalPlayer.getNickname())){
            personalPlayer.setPlayerDeck(playerDeck);
        }else {
            for (Player p : playersInTheGame) {
                if (s.equals(p.getNickname())) {
                    p.setPlayerDeck(playerDeck);
                }
            }
        }
        if (selectedView == 1) {
            System.out.println("I received the update newUpdatePlayerDeck.");
        } else if (selectedView == 2) {
            //guiView.updatePlayerDeck(player, playerDeck)
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
            System.out.println("Type -1 if you want to create a new lobby, or the lobby id if you want to join it (if there are any available)");
            ok=false;
            int gameSelection=0;
            while(!ok) {
                try {
                    sc=new Scanner(System.in);
                    gameSelection = sc.nextInt();
                    ok=true;
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
            String[] network = new String[1];
            network[0] = "TCP";
            InterfaceGUI.main(network);
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
            try {
                sendMessage(new SCKMessage(null,Event.AVAILABLE_LOBBY));
            }catch (Exception e){
                e.printStackTrace();
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
            try {
                sendMessage(new SCKMessage(null, Event.CHECK_N_PLAYERS));
            } catch (Exception e) {
                e.printStackTrace();
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
        System.out.println("sono in addPlayerToLobby");
        synchronized (actionLock) {
                List<Object> list = new ArrayList<>();
                list.add(playerNickname);
                list.add(gameId);
                try {
                    sendMessage(new SCKMessage(list, Event.ADD_PLAYER_TO_LOBBY));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            while (!responseReceived){
                try {
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("risposta ricevuta in addPlayerToLobby");
        }
    }

    @Override
    public void chooseNickname(String nickname) throws RemoteException, NotBoundException {
        synchronized (actionLock) {
            List<Object> list=new ArrayList<>();
            list.add(nickname);
            try {
                sendMessage(new SCKMessage(list,Event.CHOOSE_NICKNAME));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list=new ArrayList<>();
            list.add(creatorNickname);
            list.add(numOfPlayers);
            try {
                sendMessage(new SCKMessage(list,Event.CREATE_LOBBY));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list=new ArrayList<>();
            list.add(nickname);
            list.add(selectedCard);
            list.add(position);
            list.add(orientation);
            try {
                sendMessage(new SCKMessage(list,Event.PLAY_CARD));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list = new ArrayList<>();
            list.add(nickname);
            list.add(baseCard);
            list.add(orientation);
            try {
                System.out.println("sto per inviare il messaggio in playBaseCard");
                sendMessage(new SCKMessage(list, Event.PLAY_BASE_CARD));
                System.out.println("messaggio in playBaseCard inviato");
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            while (!responseReceived) {
                try {
                    System.out.println("aspetto una risposta");
                    actionLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

             */
        }
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        synchronized (actionLock) {
            List<Object> list=new ArrayList<>();
            list.add(nickname);
            list.add(selectedCard);
            try {
                sendMessage(new SCKMessage(list,Event.DRAW_CARD));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list=new ArrayList<>();
            list.add(chooserNickname);
            list.add(selectedCard);
            try {
                sendMessage(new SCKMessage(list,Event.CHOOSE_OBJECTIVE_CARD));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list=new ArrayList<>();
            list.add(chooserNickname);
            list.add(selectedColor);
            try {
                sendMessage(new SCKMessage(list,Event.CHOOSE_PAWN_COLOR));
            }catch (Exception e){
                e.printStackTrace();
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
            List<Object> list=new ArrayList<>();
            list.add(senderNickname);
            list.add(receiversNickname);
            list.add(message);
            try {
                sendMessage(new SCKMessage(list,Event.SEND_MESSAGE));
            }catch (Exception e){
                e.printStackTrace();
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
        synchronized (actionLock) {
            List<Object> list=new ArrayList<>();
            list.add(nickname);
            try {
                sendMessage(new SCKMessage(list,Event.LEAVE_GAME));
            }catch (Exception e){
                e.printStackTrace();
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

 //update

    //taken from RMIClient
    public void finishedSetupPhase2() throws RemoteException{
        System.out.println("I received the board finishedSetupPhase2.");
        updateRound(playersInTheGame);
    }


    @Override
    public void updateBoard(String boardOwner, Board board) throws RemoteException {
        //we have to change the view and the local model
        if (boardOwner.equals(personalPlayer.getNickname())) {
            System.out.println("I received the board updatePlayerDeck.");
            personalPlayer.setBoard(board);
        } else {
            for (Player p : playersInTheGame) {
                if (boardOwner.equals(p.getNickname())) {
                    p.setBoard(board);
                }
            }
            if (selectedView == 1) {
                System.out.println("I received the board updatePlayerDeck di un altro.");
            } else if (selectedView == 2) {
                //guiView.showBoard(board)
            }
        }


    }

    @Override
    public void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException {
        //we have to change the view and the local model
        this.resourceDeck=resourceDeck;
        if (selectedView == 1) {
            System.out.println("I received the updatePlayerDeck.");
        } else if (selectedView == 2) {
            //guiView.showUpdatedResourceDeck(this.resourceDeck)
        }
    }

    @Override
    public void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException {
        //we have to change the view and the local model
        this.goldDeck=goldDeck;
        if (selectedView == 1) {
            System.out.println("I received the updatePlayerDeck.");
        } else if (selectedView == 2) {
            //guiView.updateGoldDeck(goldDeck)
        }
    }

    @Override
    public void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException {
        //we have to change the view and the local model
        if(playerNickname.equals(personalPlayer.getNickname())){
            personalPlayer.setPlayerDeck(playerDeck);
        }else {
            for (Player p : playersInTheGame) {
                if (playerNickname.equals(p.getNickname())) {
                    p.setPlayerDeck(playerDeck);
                }
            }
        }
        if (selectedView == 1) {
            System.out.println("I received the updatePlayerDeck.");
        } else if (selectedView == 2) {
            //guiView.updatePlayerDeck(player, playerDeck)
        }
    }

    //taken from RMIClient
    public void updatePersonalObjective(ObjectiveCard card, String nickname) throws RemoteException {
        if (personalPlayer.getNickname().equals(nickname)) {
            personalPlayer.addPersonalObjective(card);
            if (personalPlayer.getPersonalObjectives().size() == 2) {
                    if (selectedView == 1) {
                        System.out.println("I received the updatePersonalObjective.");
                        boolean ok = false;
                        while (!ok) {
                            System.out.println("sto chiedendo alla tui di stamparmi il player deck");
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
                            }catch (CardNotOwnedException e){ //questa eccezione però non viene lanciata da nessuno (nè dal controller nè dalla tui)
                                System.out.println("You don't own this card.");
                            }
                        }
                        /*
                        new Thread(()->{
                            boolean ok = false;
                            while (!ok) {
                                System.out.println("sto chiedendo alla tui di stamparmi il player deck");
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
                                }catch (CardNotOwnedException e){ //questa eccezione però non viene lanciata da nessuno (nè dal controller nè dalla tui)
                                    System.out.println("You don't own this card.");
                                }
                            }

                        }).start();;

                         */


                    } else if (selectedView == 2) {
                        //gui
                    }

            }
        }
    }

    @Override
    public void finishedSetUpPhase() throws RemoteException {

    }


    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.resourceCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateResourceCard1.");
        } else if (selectedView == 2) {
            //guiView.updateResourceCard1(card)
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
        }
    }

    @Override
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
        this.goldCard1=card;
        if (selectedView == 1) {
            System.out.println("I received the updateGoldCard1.");
        } else if (selectedView == 2) {
            //guiView.updateGoldCard2(card)
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
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {
        //we have to change the view and the local model
        if (selectedView == 1) {
            System.out.println("I received the updatePawns.");
        } else if (selectedView == 2) {
            //guiView.updatePawns(player, pawn)
        }
    }

    @Override
    public void updateNickname(Player player, String nickname) throws RemoteException {
        //we have to change the view and the local model
        if (selectedView == 1) {
            System.out.println("I received the updateNickname.");
        } else if (selectedView == 2) {
            //guiView.updateNickname(player, nickname)
        }
    }

    public void updateRound(Player player) throws RemoteException {}
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException { //taken from RMIClient
        //we have to change the view and the local model @TODO differenziare TUI e GUI
        System.out.println("I received the updateRound.");
        playersInTheGame = newPlayingOrder; //when turnCounter==-1 we have to initialize this list
        if(this.turnCounter==0){ //we enter here only one time: the second time that updateRound is called
            //the second time that updateRound is called we have all that is need to call playBaseCard (see the model server side)
            try {
                System.out.println("la tui mi chiede il lato della base card");
                boolean choice=tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]);
                System.out.println("chiamo playBaseCard");
                playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0],choice);
            } catch (NotBoundException e) { //non si verifica @TODO non lanciamo altre eccezioni
                throw new RuntimeException(e);
            } catch (RemoteException e) { //non si verifica
                throw new RuntimeException(e);
            }
           /* new Thread(()->{
                try {
                    System.out.println("la tui mi chiede il lato della base card");
                    boolean choice=tuiView.askPlayBaseCard(sc, personalPlayer.getPlayerDeck()[0]);
                    System.out.println("chiamo playBaseCard");
                    playBaseCard(personalPlayer.getNickname(), personalPlayer.getPlayerDeck()[0],choice);
                } catch (NotBoundException e) { //non si verifica
                    throw new RuntimeException(e);
                } catch (RemoteException e) { //non si verifica
                    throw new RuntimeException(e);
                }
            }).start();

            */

        }
        if(this.turnCounter>=1){ //we enter here from the third time included that updateRound is called
            //before starting the thread that prints the menu we communicate which is the player that is playing
            if(playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())){
                setIsPlaying(true);
                System.out.println("You are playing");
            }else{
                setIsPlaying(false);
                System.out.println("You are not playing");
            }
            if(this.turnCounter==1){ //we enter here the third time (finishedSetupPhase2())
                //we have to start the thread that prints the menu
                new Thread(()->{
                    while (inGame) { //quando la connessione viene persa/il Game termina inGame deve venire settato a false
                        //il player può usare il menù completo solo se isPlaying==true se no usa quello di base
                        showMenuAndWaitForSelection();

                    }
                }).start();
            }
        }
        turnCounter++; //first time: -1 -> 0, second time 0 -> 1  so from the second time on we enter if(turnCounter>=1)
    }


    //taken from RMIClient
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
                this.firstPongReceived=true; //initialization
                this.secondPongReceived=true; //initialization
                this.timer = new Timer(true); //isDaemon==true -> maintenance activities performed as long as the application is running
                //we need to use ping-pong messages because sometimes the connection seems to be open (we do not receive any I/O exception) but it is not.
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(firstPongReceived||secondPongReceived) {
                            firstPongReceived=false;
                            secondPongReceived=false;
                            try {
                                sendMessage(new SCKMessage(null,Event.PING)); //first ping
                                sendMessage(new SCKMessage(null,Event.PING)); //second ping
                            } catch (IOException e) { //the connection doesn't is open (and it doesn't seem to be open)
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
                            }
                        }else{ //there are no pongs received
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
                        }
                    }
                }, 0, 10000); // Esegui ogni 10 secondi

            } else if (gameState.equals(Game.GameState.ENDING)) {

            }else if(gameState.equals(Game.GameState.ENDED)){ //we do not return to the lobby -> we have to close the connection and stop the threads
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
        }
    }

    @Override
    public void handleDisconnection() throws RemoteException {

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

    //taken from RMIClient
    public void showError(Event event){ //dovrebbe mostrarmi UNABLE_TO_PLAY_CARD...dovrebbe essere già inclusa nelle eccezioni
        System.out.println(event.toString());
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
                        case 0:
                            inGame = false;
                            leaveGame(personalPlayer.getNickname());
                            System.out.println("You left the game.");
                            this.resetAttributes();
                            this.waitingRoom();
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
                                orientation = tuiView.askCardOrientation(sc, card);
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

    private void resetAttributes() {
        String tmp= personalPlayer.getNickname();
        this.personalPlayer=new Player();
        this.personalPlayer.setNickname(tmp);
        this.goldDeck=null;
        this.resourceDeck=null;
        this.resourceCard1=null;
        this.resourceCard2=null;
        this.goldCard1=null;
        this.goldCard2=null;
        this.turnCounter=-1;  //credo sia per capire se è il primo turno (e quindi bisogna scegliere tra le carte base
        this.playersInTheGame=null;
        //this.gameController=null; //dovrebbe servire solo per RMI
    }


    public static class Settings { //this is an attribute. (qui ci sono indirizzo e porta del server locale
        static int PORT = 50000; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }
    public void setIsPlaying(boolean isPlaying){
        this.isPlaying=isPlaying;
    }
    public boolean getIsPlaying(){ // c'è la syn nel metodo che lo chiama (showMenuAndWaitForSelection)
        return this.isPlaying;
    }

}




