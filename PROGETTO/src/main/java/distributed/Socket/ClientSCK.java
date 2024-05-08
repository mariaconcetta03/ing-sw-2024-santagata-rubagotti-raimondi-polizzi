package distributed.Socket;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
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
    private int selectedView;
    private InterfaceTUI tuiView;
    //private InterfaceGUI guiView;
    private Board board;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final Thread threadCheckConnection;
    private Player player;
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;

    //ho rimosso eccezione ClassNotFound, non veniva mai lanciata secondo l'IDE
    /**
     * Constructor method
     * @param address
     * @param port
     * @throws IOException
     */
    public ClientSCK(String address, int port) throws IOException { //we call this constructor after we ask the IP address and the port of the server
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), 1000); //the address and the port of the server

        //in this way the stream is converted into objects
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //what ClientHandlerThread writes in its socket's output stream ends up here

        //to control the status of the connection (a player can leave the game without any advice)
        threadCheckConnection= new Thread(()-> { //ci serve qualcosa su cui fare la syn?
            try { //dobbiamo usare il filter?? per leggere il PingMessage
                PingMessage pingMessage = (PingMessage) this.inputStream.readObject(); //we receive 'ARE_YOU_STILL_CONNECTED'
                outputStream.writeObject(new PingMessage());
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        },"CheckConnection"); //to be started when a Game is created (when we receive the msg ALL_CONNNECTED)



        while (true) {
            try {
                SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta
            } catch (Exception e) {
                break; //se per esempio il flusso viene interrotto (dovrebbe venire lanciata un eccezione di Input/Output)
            }

        }

        //we have to read this stream every time there is a server update to the client (->in the Thread local to server we modify this stream)
        //public SCKMessage receivedMessage () throws IOException, ClassNotFoundException {
        //    //the socket stream has been changed in the Thread (locally to the server)
        //    return (SCKMessage) inputStream.readObject(); //we are reading the object written in the inputStream
        //}

    }

    /**
     * This method allows the Client to send, through the socket, a message to be read (using its input stream) by the ClientHandlerThread.
     * @param sckMessage is the message containing objects and Event relative to the action to perform
     * @throws IOException in case the Server is unreachable we shut down the Client.
     */
    public void sendMessage(SCKMessage sckMessage) throws IOException {
        try {
            outputStream.writeObject(sckMessage);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            System.err.println("Server not available!");
            System.exit(-1); //this is a shutdown of the VirtualMachine
        }
    }


    //leggiamo l'evento per capire di che update si tratta e poi aggiorniamo quello che ci dice di aggiornare l'evento e chiamiamo infine sendMessage
    //non è l'update dei listeners (quello è in ClientHandlerThread: scriverà sull'input della socket)
    //con questo update andiamo a modificare le cose locali al client
    public void modifyClientSide(SCKMessage sckMessage) throws IOException, ClassNotFoundException {
        switch (sckMessage.getMessageEvent()) {
            case ALL_CONNECTED -> {
                getModel(); //local copy of the model
                sendMessage(new SCKMessage(null, Event.START)); //null is referred to the objects sent. 'START' to tell the server that this client is ready
                threadCheckConnection.start(); //now if a player doesn't reply to a ping message the Game ends
            }
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
                //we have to change the view and the local model
                updateGameState((Game) sckMessage.getObj().get(0));
            }
        }
    }

    public void getModel() throws IOException, ClassNotFoundException {
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_BOARD)); il 1 parametro è una List di Object non evento!
        this.board = (Board) this.inputStream.getObjectInputFilter(); // we need a filter because we may obtain a SCKMessage instead of a Board
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_PLAYERS));
        this.playersInTheGame = (List<Player>) this.inputStream.getObjectInputFilter(); //da riguardare bene filter (è da usare anche per l'estrazione di SCKMessage?)
        // e altro.... come i goal comuni
    }

    /**
     * This method is called when the client is created. Absolves the function of helping the player to select
     * his nickname and to choose if he wants to join an already started Game or create a new one.
     */
    public void waitingRoom() {
        if (selectedView == 1) {
            tuiView = new InterfaceTUI();
        } else {
            //guiView= new InterfaceGUI();
        }
        String nickname = tuiView.askNickname();
        try {
            this.sendMessage(new SCKMessage(nickname, Event.NICKNAME_SELECTION));
        } catch (IOException ignored) {
        }
    }




    //GETTER & SETTER
    public int getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }


    //this class implements clientGeneralInterface
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        List<Object> list=new ArrayList<>();
        list.add(playerNickname);
        list.add(gameId);
        try {
            sendMessage(new SCKMessage(list,Event.ADD_PLAYER_TO_LOBBY));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void chooseNickname(String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
        List<Object> list=new ArrayList<>();
        list.add(nickname);
        try {
            sendMessage(new SCKMessage(list,Event.CHOOSE_NICKNAME));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(creatorNickname);
        list.add(numOfPlayers);
        try {
            sendMessage(new SCKMessage(list,Event.CREATE_LOBBY));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
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
    }

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(nickname);
        list.add(baseCard);
        list.add(orientation);
        try {
            sendMessage(new SCKMessage(list,Event.PLAY_BASE_CARD));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(nickname);
        list.add(selectedCard);
        try {
            sendMessage(new SCKMessage(list,Event.DRAW_CARD));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(chooserNickname);
        list.add(selectedCard);
        try {
            sendMessage(new SCKMessage(list,Event.CHOOSE_OBJECTIVE_CARD));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(chooserNickname);
        list.add(selectedColor);
        try {
            sendMessage(new SCKMessage(list,Event.CHOOSE_PAWN_COLOR));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException, NotBoundException {
        List<Object> list=new ArrayList<>();
        list.add(senderNickname);
        list.add(receiversNickname);
        list.add(message);
        try {
            sendMessage(new SCKMessage(list,Event.SEND_MESSAGE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException {
        List<Object> list=new ArrayList<>();
        list.add(nickname);
        try {
            sendMessage(new SCKMessage(list,Event.LEAVE_GAME));
        }catch (Exception e){
            e.printStackTrace();
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
}




