package distributed.Socket;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import distributed.ClientGeneralInterface;
import distributed.messages.*;
import org.model.*;
import utils.Event;
import view.TUI.ANSIFormatter;
import view.TUI.InterfaceTUI;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * This class represents the Client who chose TCP as network protocol.
 * It listens to the SCKMessage sent by the ClientHandlerThread through the socket
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK implements ClientGeneralInterface{
    private boolean errorState= false;
    private HashSet<Integer> lobbyId;
    private final Socket socket;
    private Player personalPlayer;
    private int selectedView;
    private InterfaceTUI tuiView;
    //private InterfaceGUI guiView;
    private Board board;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
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
    private Scanner sc;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;
    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private BufferedReader console;

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

        lobbyId=new HashSet<>();

        personalPlayer=new Player();
        this.inputLock=new Object();

        //in this way the stream is converted into objects
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //what ClientHandlerThread writes in its socket's output stream ends up here

        this.running=true;
        this.responseReceived =false; //initialized false to enter the while inside every ClientGeneralInterface method
        this.actionLock=new Object();

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

        new Thread(()-> {
            while (running) {
                synchronized (inputLock) {
                    try {
                        SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                        if (sckMessage != null) {
                            //System.out.println("messaggio ricevuto da ClienSCK non nullo");
                            modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta
                        }
                    } catch (Exception e) { //se il server si disconnette
                        try { //devo fermare i thread lanciati all'interno di questo thread
                            inputStream.close();
                            outputStream.close();
                            socket.close();
                            running = false;
                        } catch (IOException ex) { //this catch is needed for the close statements
                            throw new RuntimeException(ex);
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



    /**
     * This method allows the Client to send, through the socket, a message to be read (using its input stream) by the ClientHandlerThread.
     * @param sckMessage is the message containing objects and Event relative to the action to perform
     * @throws IOException in case the Server is unreachable we shut down the Client.
     */
    public void sendMessage(SCKMessage sckMessage) throws IOException { //ATTENTION: this method is called ONLY inside ClientGeneralInterface methods
        try {
            responseReceived=false;
            outputStream.writeObject(sckMessage);
            outputStream.flush();
        } catch (IOException e) {
            try { //devo fermare i thread lanciati all'interno di questo thread
                inputStream.close();
                outputStream.close();
                socket.close(); //the ClientSCK will receive an exception
                running=false;
            } catch (IOException ex) { //this catch is needed for the close statements
                throw new RuntimeException(ex);
            }
            System.err.println("Server not available!");
            System.exit(-1); //this is a shutdown of the VirtualMachine
        }
    }


    //leggiamo l'evento per capire di che update si tratta e poi aggiorniamo quello che ci dice di aggiornare l'evento e chiamiamo infine sendMessage
    //non è l'update dei listeners (quello è in ClientHandlerThread: scriverà sull'input della socket)
    //con questo update andiamo a modificare le cose locali al client
    public void modifyClientSide(SCKMessage sckMessage) throws IOException, ClassNotFoundException {
        switch (sckMessage.getMessageEvent()) {
            case UPDATED_BOARD -> {
                //we have to change the view and the local model
                updateBoard((String) sckMessage.getObj().get(0), (Board) sckMessage.getObj().get(0));
            }
            case UPDATED_RESOURCE_DECK -> {
                //we have to change the view and the local model
                updateResourceDeck((PlayableDeck) sckMessage.getObj().get(0));
            }
            case UPDATED_GOLD_DECK->{
                //we have to change the view and the local model
                updateGoldDeck((PlayableDeck) sckMessage.getObj().get(0));
            }
            case UPDATED_PLAYER_DECK,SETUP_PHASE_1,SETUP_PHASE_2->{
                //we have to change the view and the local model
                updatePlayerDeck((String) sckMessage.getObj().get(0), (PlayableCard[]) sckMessage.getObj().get(1));
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
            case UPDATED_ROUND,NEW_TURN->{ //perchè avere due casi equivalenti?
                //we have to change the view and the local model
                updateRound((List<Player>) sckMessage.getObj().get(0));
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
            case GAME_STATE_CHANGED->{ //subito dopo questo update c'è NEW_TURN ( quando si chiama game.startGame() )
                //we have to change the view and the local model
                System.out.println("game state changed"); //per il test
                //sempre in game.startGame vengono chiamati poi tutti gli altri update per permermettere al client di avere una copia locale di quello che c'è sul server
                updateGameState((Game.GameState) sckMessage.getObj().get(0)); //se la partita inizia: threadCheckConnection.start();
                // in caso di Game ENDED dobbiamo chiudere la connessione? potremmo riutilizzarla per un'altra partita e rimettere il client in una lobby
                //per chiudere la connessione:
                // inputStream.close();
                // outputStream.close();
                // socket.close();
                // running=false; //se decidiamo di non chiudere la connessione e di rimettere il client in una lobby il threadCheckConnection si deve fermare?
            }
            case OK -> { //...potremmo stampare anche il messaggio di ok....
                //System.out.println("sono in case OK di ClientSCK");
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
                    //System.out.println(sckMessage.getMessageEvent().toString());
                    this.responseReceived = true;
                    actionLock.notify();
                }
            }
        }
    }

    /*

    //da rivedere quando si fa la view:
    //chiamando questo metodo otteniamo una prima inizializzazione degli attributi locali al client che devono rispettare la struttura nel model lato server
    public void getModel() throws IOException, ClassNotFoundException {
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_BOARD)); il 1 parametro è una List di Object non evento!
        this.board = (Board) this.inputStream.getObjectInputFilter(); // we need a filter because we may obtain a SCKMessage instead of a Board
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_PLAYERS));
        this.playersInTheGame = (List<Player>) this.inputStream.getObjectInputFilter(); //da riguardare bene filter (è da usare anche per l'estrazione di SCKMessage?)
        // e altro.... come i goal comuni
    }

     */

    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() {
        this.isPlaying=false;
        sc=new Scanner(System.in);
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
                        createLobby(personalPlayer.getNickname(), gameSelection); //the controller server side doesn't have other exceptions
                        System.out.println("Successfully created a new lobby with id: " + gameID);
                    } else if (lobbyId.contains(gameSelection)) {
                        try {
                            addPlayerToLobby(personalPlayer.getNickname(), gameSelection);
                            if(errorState){
                                System.out.println(ANSIFormatter.ANSI_RED + "The game you want to join is inaccessible, try again" + ANSIFormatter.ANSI_RESET);
                                errorState=false;
                            }else{
                                System.out.println("Successfully joined the lobby with id: " + gameID);
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
                        System.out.println("You wrote a wrong id, try again!");
                    }
                }
            }catch (RemoteException |NotBoundException e){ //queste sono eccezioni da togliere dalla signature dei metodi in comune tra rmi e tcp
                System.out.println("Unable to communicate with the server! Shutting down.");
                System.exit(-1);
            }
        } else {
            //guiView= new InterfaceGUI();
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
                sendMessage(new SCKMessage(list, Event.PLAY_BASE_CARD));
            } catch (Exception e) {
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
    @Override
    public void updateBoard(String boardOwner, Board board) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateResourceCard2(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateGoldCard2(PlayableCard card) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateChat(Chat chat) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateNickname(Player player, String nickname) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateRound(Player player) throws RemoteException {}
    public void updateRound(List<Player> newPlayingOrder) throws RemoteException { //taken from RMIClient
        //we have to change the view and the local model

        playersInTheGame = newPlayingOrder;
        if(playersInTheGame.get(0).getNickname().equals(personalPlayer.getNickname())){
            System.out.println("You are playing");
            setIsPlaying(true);
            //Qui posso lanciare un thread che fintanto che isPlaying==true mostra le azioni che il Player può fare.
            //Queste nuove azioni vanno sommate a quelle di base (es. guarda la board di un altro giocatore) quindi
            //un thread di base deve sempre andare e a questo thread quando isPlaying==true si possono aggiungere
            //funzionalità (al posto di usare un secondo thread)
        }else{
            System.out.println("You are not playing");
            setIsPlaying(false);
        }
    }

    @Override
    public void updateGameState(Game.GameState gameState) throws RemoteException {
        //we have to change the view and the local model

        if (selectedView == 1) {
            if(gameState.equals(Game.GameState.STARTED)) {
                inGame=true;
                System.out.println("The game has started!");
                // qui lancio il thread base che mi legge l'input di un Player
                new Thread(()->{
                    while (inGame) { //quando la connessione viene persa/il Game termina inGame deve venire settato a false
                        //il player può usare il menù completo solo se isPlaying==true se no usa quello di base
                        showMenuAndWaitForSelection();

                    }
                }).start();
            } else if (gameState.equals(Game.GameState.ENDING)) {

            }else if(gameState.equals(Game.GameState.ENDED)){
                //inGame=false;
            }
        } else if (selectedView == 2) {
            //guiView.updateGameState(game)
        }
    }


    //fine update


    synchronized private void showMenuAndWaitForSelection(){ //syn perchè così legge il valore corrente di isPlaying
        Integer choice;
        if(selectedView==1) {
            choice=tuiView.showMenuAndWaitForSelection(this.getIsPlaying(),this.console);
            boolean ok;
            if(choice!=-1) {
                try {
                    switch (choice) {
                        case 0:
                            inGame = false;
                            leaveGame(personalPlayer.getNickname());
                            System.out.println("You left the game.");
                            this.resetAttributes();
                            this.waitingRoom();
                        case 1:
                            tuiView.printHand(personalPlayer.getPlayerDeck());
                        case 2:
                            List<ObjectiveCard> list = new ArrayList<>();
                            list.add(commonObjective1);
                            list.add(commonObjective2);
                            list.add(personalPlayer.getPersonalObjective());
                            tuiView.printObjectiveCard(list);
                        case 3:
                            List<PlayableCard> tmp = new ArrayList<>();
                            tmp.add(resourceCard1);
                            tmp.add(resourceCard2);
                            tmp.add(goldCard1);
                            tmp.add(goldCard2);
                            tuiView.printDrawableCards(goldDeck, resourceDeck, tmp);
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
                        case 5:
                            tuiView.printScoreBoard(playersInTheGame);
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
                                    tmp = new ArrayList<>();
                                    tmp.add(resourceCard1);
                                    tmp.add(resourceCard2);
                                    tmp.add(goldCard1);
                                    tmp.add(goldCard2);
                                    card = tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                                    this.drawCard(personalPlayer.getNickname(), card);
                                } else {
                                    System.out.println("The coordinates are null");
                                }
                            } else {
                                System.out.println("The card is null");
                            }
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
    }
/*
    public void gameTurn(boolean isPlaying) throws InterruptedException{
        int choice= -1;
        boolean ok=false;
        if(selectedView==1) {
            tuiView.gameTurn(isPlaying); //da modificare InterfaceTUI: in base al boolean passato deve far vedere cose diverse
            choice=tuiView.askAction(sc, isPlaying);
            try {
                switch (choice) {
                    case 0:
                        System.out.println("Are you sure to LEAVE the game? Type 1 if you want to leave.");
                        try{
                            if(sc.nextInt()==1){
                                inGame=false;
                                menuThread.join(); //letting the thread finish
                                leaveGame(personalPlayer.getNickname());
                                System.out.println("You left the game.");
                                this.resetAttributes();
                                this.waitingRoom();
                            }
                        }catch (InputMismatchException ignored){} //@TODO da gestire
                        break;
                    case 1: tuiView.printHand(personalPlayer.getPlayerDeck());
                        gameTurn(isPlaying);
                        break;
                    case 2: List<ObjectiveCard> list= new ArrayList<>();
                        list.add(commonObjective1);
                        list.add(commonObjective2);
                        list.add(personalPlayer.getPersonalObjective());
                        tuiView.printObjectiveCard(list);
                        gameTurn(isPlaying);
                        break;
                    case 3:List<PlayableCard> tmp=new ArrayList<>();
                        tmp.add(resourceCard1);
                        tmp.add(resourceCard2);
                        tmp.add(goldCard1);
                        tmp.add(goldCard2);
                        tuiView.printDrawableCards(goldDeck, resourceDeck, tmp);
                        gameTurn(isPlaying);
                        break;
                    case 4:
                        ok=false;
                        System.out.println("Which player's board do you want to see?");
                        String nickname= sc.nextLine();
                        for(Player player: playersInTheGame){
                            if(player.getNickname().equals(nickname)){
                                ok=true;
                                tuiView.printTable(player.getBoard()); //TODO non sto aggiornando playerInTheGame quando arriva update
                            }
                        }
                        if(!ok){
                            System.out.println("There is no such player in this lobby! Try again.");
                        }
                        gameTurn(isPlaying);
                        break;
                    case 5: tuiView.printScoreBoard(playersInTheGame);
                        gameTurn(isPlaying);
                        break;
                    case 6: System.out.println("Do you want to send a message to everybody (type 1) or a private message (type the single nickname)?");
                        String answer= sc.nextLine();
                        if(answer.equals("1")){
                            List<String> receivers = new ArrayList<>();
                            for(Player p: playersInTheGame){
                                if(!p.getNickname().equals(personalPlayer.getNickname())){
                                    receivers.add(p.getNickname());
                                }
                            }
                            System.out.println("Now type the message you want to send: ");
                            answer=sc.nextLine();
                            sendMessage(personalPlayer.getNickname(), receivers, answer);
                        }else{
                            ok=false;
                            for(Player p: playersInTheGame){
                                if(p.getNickname().equals(answer)){
                                    ok=true;
                                }
                            }
                            if(ok){
                                List<String> receivers= new ArrayList<>();
                                receivers.add(answer);
                                System.out.println("Now type the message you want to send: ");
                                answer=sc.nextLine();
                                sendMessage(personalPlayer.getNickname(), receivers, answer);
                            }else{
                                System.out.println("There is no such player in this lobby! Try again.");
                            }
                        }
                        gameTurn(isPlaying);
                        break;
                    case 7: boolean orientation=true;
                        PlayableCard card= null;
                        Coordinates coordinates;
                        card= tuiView.askPlayCard(sc, personalPlayer);
                        if(card!=null){
                            orientation=tuiView.askCardOrientation(sc, card);
                        }else{
                            gameTurn(isPlaying);
                        }
                        coordinates=tuiView.askCoordinates(sc, card, personalPlayer.getBoard());
                        if(coordinates!=null){
                            this.playCard(personalPlayer.getNickname(), card, coordinates,orientation);
                            tmp=new ArrayList<>();
                            tmp.add(resourceCard1);
                            tmp.add(resourceCard2);
                            tmp.add(goldCard1);
                            tmp.add(goldCard2);
                            card= tuiView.askCardToDraw(goldDeck, resourceDeck, tmp, sc);
                            this.drawCard(personalPlayer.getNickname(), card);
                        }else{
                            gameTurn(isPlaying);
                        }
                        break;
                    default:
                        System.out.println("not yet implemented");
                }
            }catch (RemoteException|NotBoundException e){
                System.out.println("Unable to communicate with the server! Shutting down.");
                System.exit(-1);
            }
        }else{
            //gui
        }
    }

 */

    //end of implementation of ClientGeneralInterface

    public static class Settings { //this is an attribute. (qui ci sono indirizzo e porta del server locale
        static int PORT = 50000; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }
    synchronized public void setIsPlaying(boolean isPlaying){
        this.isPlaying=isPlaying;
    }
    public boolean getIsPlaying(){ // c'è la syn nel metodo che lo chiama (showMenuAndWaitForSelection)
        return this.isPlaying;
    }


    public class NonBlockingInputExample {
        public static void main(String[] args) {
            try {
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                String line = console.readLine();
                System.out.println("You entered: " + line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}




