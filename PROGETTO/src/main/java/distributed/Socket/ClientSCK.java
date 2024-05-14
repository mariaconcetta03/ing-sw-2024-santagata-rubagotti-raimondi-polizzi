package distributed.Socket;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import distributed.ClientGeneralInterface;
import distributed.messages.*;
import org.model.*;
import utils.Event;
import view.TUI.InterfaceTUI;


/**
 * This class represents the Client who chose TCP as network protocol.
 * It listens to the SCKMessage sent by the ClientHandlerThread through the socket
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK implements ClientGeneralInterface{
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
                            System.out.println("messaggio ricevuto da ClienSCK non nullo");
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
                updateBoard((Board) sckMessage.getObj().get(0));
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
                updatePlayerDeck((Player) sckMessage.getObj().get(0), (PlayableCard[]) sckMessage.getObj().get(1));
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
            case UPDATED_ROUND->{
                //we have to change the view and the local model
                updateRound((Player) sckMessage.getObj().get(0));
            }
            case GAME_STATE_CHANGED->{
                System.out.println("game state changed");
                //System.out.println(((Game) sckMessage.getObj().get(0)).getState().toString());
                //we have to change the view and the local model
                //quello che era prima il case ALL_CONNECTED diventa questo controllo
                if(((Game) sckMessage.getObj().get(0)).getState().equals(Game.GameState.STARTED)){
                    //per il test
                    System.out.println("partita iniziata");
                    //getModel(); //to initialize the local copy of the model
                    //sendMessage(new SCKMessage(null, Event.START)); //null is referred to the objects sent. 'START' to tell the server that this client is ready
                    //threadCheckConnection.start(); //now if a player doesn't reply to a ping message the Game ends
                }
                else if(((Game) sckMessage.getObj().get(0)).getState().equals(Game.GameState.ENDED)){
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    running=false;
                }
                updateGameState((Game) sckMessage.getObj().get(0));
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
                    System.out.println(sckMessage.getMessageEvent().toString());
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
        Scanner sc= new Scanner(System.in);
        boolean ok=false;
        int errorCounter=0;
        if (selectedView == 1) {
            tuiView = new InterfaceTUI();
            while(!ok){
                if(errorCounter==3){
                    System.out.println("Unable to communicate with the server! Shutting down.");
                    System.exit(-1);
                }
                String nickname = tuiView.askNickname();
                try {
                   this.chooseNickname(nickname);
                    personalPlayer.setNickname(nickname);
                    ok=true;
                } catch (RemoteException | NotBoundException e) {
                    errorCounter++;
                    System.out.println();
                } catch (NicknameAlreadyTakenException ex) {
                    System.out.println("Nickname is already taken! Please try again.");
                }
            }
            System.out.println("Nickname correctly selected!");
        } else {
            //guiView= new InterfaceGUI();
        }
        /*
        String nickname = tuiView.askNickname();
        try {
            List<Object> list=new ArrayList<>();
            list.add(nickname);
            this.sendMessage(new SCKMessage(list, Event.NICKNAME_SELECTION));
        } catch (IOException ignored) {
        }

         */
    }




    //GETTER & SETTER
    public int getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }


    /*
    //un modo per aspettare che un booleano cambi


    private Boolean bool = true;
    private final Object lock = new Object();

    private Boolean getChange(){
        synchronized(lock){
            while (bool) {
                bool.wait();
            }
        }
        return bool;
    }
    public void setChange(){
        synchronized(lock){
            bool = false;
            bool.notify();
        }
    }

     */


    //these classes implement clientGeneralInterface
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        System.out.println("sono in addPlayerToLobby");
        synchronized (actionLock) {
                responseReceived = false;
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
    public void chooseNickname(String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
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
    public void updateBoard(Board board) throws RemoteException {
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
    public void updatePlayerDeck(Player player, PlayableCard[] playerDeck) throws RemoteException {
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
    public void updateRound(Player newCurrentPlayer) throws RemoteException {
        //we have to change the view and the local model
    }

    @Override
    public void updateGameState(Game game) throws RemoteException {
        //we have to change the view and the local model
    }
    //fine update

    //end of implementation of ClientGeneralInterface

    public static class Settings { //this is an attribute. (qui ci sono indirizzo e porta del server locale
        static int PORT = 50000; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }
}




