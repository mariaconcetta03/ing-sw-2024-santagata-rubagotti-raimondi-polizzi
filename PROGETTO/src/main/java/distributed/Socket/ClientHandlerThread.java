package distributed.Socket;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import controller.ServerController;
import distributed.ClientGeneralInterface;
import distributed.messages.Message;
import distributed.messages.PingMessage;
import distributed.messages.SCKMessage;
import org.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;

import utils.Event;
import utils.Observable;
import utils.Observer;

//the server communicates with the clients calling their method update, the clients communicate with the server (better->the model) using
//first the attribute serverController and then the attribute gameController (when the game is created)

/**
 * This class represents the Client (server-side) who chose TCP as network protocol
 * It is notified of all the changes in the model and forwards them, using its socket attribute,
 * to ClientSCK. It is a thread, so we will not have problems related to congestion (as we could have had in RMI).
 */
public class ClientHandlerThread implements Runnable, Observer, ClientGeneralInterface { //this is a Thread
    private final Socket socket;
    private GameController associatedGameController; //returned by ServerController' startlobby()/addPlayerToLobby()

    // private boolean hasAlreadyAGame=false; //this flag is necessary if we don't want to generate some avoidable errors (see the explanation above)
    private String nickname = null;
    private Player personalPlayer= new Player(); //it could be properly initialized with the method addPlayerToGame or addPlayerToLobby or createLobby
    private ServerController serverController; //to be passed as parameter in the constructor method

    private ClientSCK clientSCK= null; //this class writes what the client wants in the stream and it's local to the client

    private final Thread threadCheckMSG;

    private final Thread threadCheckConnection;
    private final Object inputLock;

    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private GameController gameController;

    /**
     * Constructor method
     * @param client
     * @param serverController
     * @throws IOException
     */
    public ClientHandlerThread(Socket client,ServerController serverController) throws IOException {
        this.serverController=serverController;
        this.socket = client; //the client can communicate with the server by this thread using this socket

        this.output = new ObjectOutputStream(client.getOutputStream());
        this.input = new ObjectInputStream(client.getInputStream());


        this.inputLock = new Object();

        //this Thread is needed because some actions of the client can't be predicted and isolated in the question-response pattern
        threadCheckMSG = new Thread(()-> {
            synchronized (inputLock) {
                while (!Thread.currentThread().isInterrupted()) {
                        SCKMessage sckMessage = (SCKMessage) this.input.getObjectInputFilter(); //messaggi scritti dal Client vero
                        react(sckMessage);
                }
            }
        },"CheckMSG"); //to be started when all the players are connected

        //to control the status of the connection (a player can leave the game without any advice)
        threadCheckConnection= new Thread(()-> {
                while (!Thread.currentThread().isInterrupted()) { //we enter here every time this ClientHandlerThread is not interrupted by other ClientHandlerThread
                    try {
                        output.writeObject(new PingMessage());
                        output.flush();
                        output.reset();
                    } catch (IOException e) {
                    }
                    // here we set a timeout and then we check the response, if there is no response we declared the connection dead
                    // we set the timeout
                    // we check the response
                    try {
                        Timer t = new Timer();
                        synchronized (inputLock) {
                            t.wait(2000);
                            PingMessage pingMessage = (PingMessage) this.input.getObjectInputFilter();
                        }
                        } catch (InterruptedException e) { //no response-> we free the thread and we end the game
                        //Game.GameState.ENDED
                        throw new RuntimeException(e);
                        }
                }

        },"CheckConnection"); //to be started when a Game is created (when we receive START from ClientSCK)
    }


    //il medoto run va rifatto basandosi sui Message.
    /*
    public void run() { //that's not the only method we can have in this thread
        try { //here we can match the user requests with the ClientSCK methods
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("Select the number:\n 1 to addPlayerToLobby\n 2 to chooseNickname\n 3 to createLobby\n 4 to createGame\n 5 to addPlayerToGame\n " +
                    "6 to startGame\n 7 to playCard\n 8 to playBaseCard\n 9 to drawCard\n 10 to chooseObjectiveCard\n 11 to choosePawnColor\n " +
                    "12 to sendMessage\n 13 to nextRound\n 14 to endGame\n 15 to leaveGame\n");
            // Leggo e scrivo nella connessione finche' non ricevo "quit"
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                } else {
                    out.println("Received: " + line);
                    out.flush();
                    if(line.equals("1")){
                        if(!hasAlreadyAGame) {
                            try {
                                int gameId = 1;
                                //we should do personalPlayer.getGame() but it return a Game not its id....
                                addPlayerToLobby(personalPlayer.getNickname(), gameId); //what we want the client decide about his player in this stage
                                hasAlreadyAGame = true;
                            } catch (Exception e) {
                                String message = e.getMessage();
                                out.println(message); //in this way the user can understand why there is an error
                            }
                        }else {
                            out.println("You have already a game");
                        }
                    } //this has to be done with all the methods and we have to ask the client to insert the parameters
                    if(line.equals("2")){
                        out.println("Insert your nickname: ");
                        out.flush();
                        line = in.nextLine();
                        try{
                            chooseNickname (line);
                            //if there are no errors we can set our personPlayer nickname to the one contains in 'line'
                        }catch (Exception e){
                            String message = e.getMessage();
                            out.println(message);
                        }
                    }
                    if(line.equals("3")){
                        if(!hasAlreadyAGame) {
                            try {
                                out.println("Insert the number of players: ");
                                out.flush();
                                line = in.nextLine();
                                createLobby(personalPlayer.getNickname(), Integer.parseInt(line));
                            } catch (Exception e) {
                                String message = e.getMessage();
                                out.println(message);
                            }
                        }else {
                            out.println("You have already a game");
                        }
                    }
                    if(line.equals("11")){
                        out.println("Select the number:\n 1 to have red\n 2 to have green\n 3 to have yellow\n 4 to have blue");
                        out.flush();
                        line = in.nextLine();
                        if(line.equals("1")||line.equals("2")||line.equals("3")||line.equals("4")) {
                            Pawn selectedColor= Pawn.RED; //to have it initialized
                            if (line.equals("1")) {
                                selectedColor = Pawn.RED;
                            }
                            if (line.equals("2")) {
                                selectedColor = Pawn.GREEN;
                            }
                            if (line.equals("3")) {
                                selectedColor = Pawn.YELLOW;
                            }
                            if (line.equals("4")) {
                                selectedColor = Pawn.BLUE;
                            }
                            try{
                                choosePawnColor(personalPlayer.getNickname(), selectedColor);
                            }catch (Exception e){
                                String message = e.getMessage();
                                out.println(message);
                            }
                        }else{
                            out.println("the selected number is invalid");
                        }
                    }
                }
            }
            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

     */


    //when a Thread starts, run is automatically called
    public void run() { //ci sono altre cose da aggiungere tipo chiamare i metodi della ClientGeneralInterface in base al messaggio ricevuto
        try {
            while (!Thread.currentThread().isInterrupted()) { //questo while dovrebbe servere nel caso in cui non vogliamo che il Thread venga interrotto mentre fa quello dentro il while
                synchronized (inputLock) { //we use the inputLock to write the stream not simultaneously
                    SCKMessage message = (SCKMessage) this.input.readObject(); //here we write the messages received before START (when we meet START, ThreadCheckMSG would continue to read the other messages)
                    Object obj = null;
                    if (message.getMessageEvent() == Event.START) { //from this moment the client can start sending msg to the server (they would be read by ThreadCheckMSG)
                            threadCheckMSG.start(); // we start to read the msg sent by ClientSCK
                            threadCheckConnection.start(); // we start to check if the ClientSCK is still connected
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void react(SCKMessage sckMessage){ //qui in base al messaggio letto chiamiamo il giusto metodo della ClientGeneralInterface
        //legge il messaggio e fa qualcosa (si può prendere spunto dalla run commentata che leggeva testo dagli stream)
        switch (sckMessage.getMessageEvent()) {
            case ADD_PLAYER_TO_LOBBY->{
                try {
                    addPlayerToLobby((String) sckMessage.getObj().get(0), (Integer) sckMessage.getObj().get(1));
                }catch (Exception e){

                }
            }
            case CHOOSE_NICKNAME->{
                try {
                    chooseNickname((String)sckMessage.getObj().get(0));
                }catch (Exception e){

                }
            }
            case CREATE_LOBBY->{
                try {
                    createLobby((String)sckMessage.getObj().get(0),(int) sckMessage.getObj().get(1));
                }catch (Exception e){

                }
            }
            case PLAY_CARD->{
                try {
                    playCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1), (Coordinates) sckMessage.getObj().get(2), (boolean) sckMessage.getObj().get(3));
                 }catch (Exception e){

                 }
            }
            case PLAY_BASE_CARD->{
                try {
                    playBaseCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1), (boolean) sckMessage.getObj().get(2));
                }catch (Exception e){

                }
            }
            case DRAW_CARD->{
                try {
                    drawCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1));
                }catch (Exception e){

                }
            }
            case CHOOSE_OBJECTIVE_CARD->{
                try {
                    chooseObjectiveCard((String) sckMessage.getObj().get(0), (ObjectiveCard) sckMessage.getObj().get(1));
                }catch (Exception e){

                }
            }
            case CHOOSE_PAWN_COLOR->{
                try {
                    choosePawnColor((String) sckMessage.getObj().get(0), (Pawn) sckMessage.getObj().get(1));
                }catch (Exception e){

                }
            }
            case SEND_MESSAGE->{
                try {
                    sendMessage((String) sckMessage.getObj().get(0), (List<String>) sckMessage.getObj().get(0), (String) sckMessage.getObj().get(0));
                }catch (Exception e){

                }
            }
            case LEAVE_GAME->{
                try {
                    leaveGame((String) sckMessage.getObj().get(0));
                }catch (Exception e){

                }
            }
        }

    }


    //methods to be implemented to have a class that implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)

    @Override
    public void update(Observable obs, Message arg) { //here we call writeTheStream (the switch case is already in ClientSCK)
        //writeTheStream(message);
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

//end of methods to be implemented taken from Observer interface

    //writeTheStream will be called in the method react (maybe inside the methods of the ClientControllerInterface) to tell the client what the controller has effectively done
    //writeTheStream will also be called in the method update (we have it because ClientHandlerThread is a listener) to tell the client what is changed in the model
    public void writeTheStream(SCKMessage sckMessage){ //this is the stream the real client can read
        try {
            output.writeObject(sckMessage);
            output.flush();
            output.reset();
        } catch (IOException e) {
        }
    }

    //methods to be implemented to have a class that implements ClientGeneralInterface

    //these methods should be private because they will never be called from the outside

    /**
     * This method, will be used to call the ServerController method
     * @param playerNickname
     * @param gameId
     * @throws RemoteException
     * @throws NotBoundException
     * @throws GameAlreadyStartedException
     * @throws FullLobbyException
     * @throws GameNotExistsException
     */
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController=serverController.addPlayerToLobby(playerNickname, gameId);
        writeTheStream(sckMessage);
    }


    /**
     * This method, will be used to call the ServerController method
     * @param nickname
     * @throws RemoteException
     * @throws NotBoundException
     * @throws NicknameAlreadyTakenException
     */
    @Override
    public void chooseNickname(String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        serverController.chooseNickname(nickname);
        writeTheStream(sckMessage);
    }


    /**
     * This method, will be used to call the ServerController method
     * @param creatorNickname
     * @param numOfPlayers
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController=serverController.startLobby(creatorNickname, numOfPlayers);
        writeTheStream(sckMessage);
    }

    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.playCard(nickname, selectedCard, position, orientation);
        writeTheStream(sckMessage);
    }

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.playBaseCard(nickname, baseCard, orientation);
        writeTheStream(sckMessage);
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.drawCard(nickname, selectedCard);
        writeTheStream(sckMessage);
    }

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
        writeTheStream(sckMessage);
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.choosePawnColor(chooserNickname, selectedColor);
        writeTheStream(sckMessage);
    }


    //that's a method to be used to communicate between the players
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.sendMessage(senderNickname, receiversNickname, message);
        writeTheStream(sckMessage);
    }

    @Override
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.leaveGame(nickname);
        writeTheStream(sckMessage);
    }


    //update function (taken from ClientGeneralInterface) (da cancellare perchè ci sono già in ClientSCK...quindi ClientHandlerThread non dovrà più implementare ClientGeneralInterface)

    //update da cancellare
    @Override
    public void updateBoard(Board board) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updatePlayerDeck(Player player, PlayableCard[] playerDeck) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateResourceCard2(PlayableCard card) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateGoldCard2(PlayableCard card) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateChat(Chat chat) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateNickname(Player player, String nickname) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateRound(Player newCurrentPlayer) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }

    @Override
    public void updateGameState(Game game) throws RemoteException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        writeTheStream(sckMessage);
    }
    //fine di update da cancellare

    //end of methods to be implemented taken from ClientGeneralInterface
}

