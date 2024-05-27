package CODEX.distributed.Socket;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.controller.GameController;
import CODEX.controller.ServerController;
import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.*;
import CODEX.utils.ErrorsAssociatedWithExceptions;
import CODEX.utils.Observable;
import CODEX.utils.Observer;
import CODEX.utils.executableMessages.serverMessages.ServerError;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import CODEX.utils.executableMessages.serverMessages.ServerOk;
import CODEX.utils.executableMessages.serverMessages.ServerPing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//the server communicates with the clients calling their method update, the clients communicate with the server (better->the model) using
//first the attribute serverController and then the attribute gameController (when the game is created)

/**
 * This class represents the Client (server-side) who chose TCP as network protocol
 * It is notified of all the changes in the model and forwards them, using its socket attribute,
 * to ClientSCK. It is a thread, so we will not have problems related to congestion (as we could have had in RMI).
 */
public class ClientHandlerThread implements Runnable, Observer, ClientActionsInterface { //this is a Thread
    private final Socket socket;
    private String nickname = null;
    private final ServerController serverController; //to be passed as parameter in the constructor method
    //private final Thread threadCheckConnection;
    private final Object inputLock;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private GameController gameController;
    private Boolean running; //it is initialized true, when becomes false the while in run and threadCheckConnection terminate.
    private boolean pongReceived;
    private Timer timer;


    /**
     * Constructor method
     * @param client
     * @param serverController
     * @throws IOException
     */
    public ClientHandlerThread(Socket client,ServerController serverController) throws IOException {
        this.serverController = serverController;
        this.socket = client; //the client can communicate with the server by this thread using this socket

        this.output = new ObjectOutputStream(client.getOutputStream());
        this.input = new ObjectInputStream(client.getInputStream());


        this.inputLock = new Object();

        this.running = true;


    }



    //when a Thread starts, run is automatically called
    public void run() { //questo metodo viene chiamato in automatico quando si fa submit alla thread pool. Lo uso per fare start dei thread interni a questo ClientHandlerThread
            while (!Thread.currentThread().isInterrupted()&&running) { //questo while dovrebbe servere nel caso in cui non vogliamo che il Thread venga interrotto mentre fa quello dentro il while
                synchronized (inputLock) { //we use the inputLock to write the stream not simultaneously
                    SCKMessage sckMessage = null; //messaggi scritti dal Client vero
                    try {
                        sckMessage = (SCKMessage) this.input.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("lost the connection...Bye, bye");
                        System.err.println(e.getMessage());
                        try {
                            running=false;
                            input.close();
                            output.close();
                            socket.close();
                        } catch (IOException ex) { //needed for the close clause
                            throw new RuntimeException(ex);
                        }
                        timer.cancel(); // Ferma il timer
                    }
                    if(sckMessage!=null) {
                        react(sckMessage);
                    }
                }
            }

    }
    public void setPongReceived(Boolean received){ //to be used in ClientPong
        this.pongReceived=received;
    }
    public ServerController getServerController(){ //to be used in ClientAvailableLobbies
        return this.serverController;
    }
    public GameController getGameController(){ //to be used in ClientAvailableLobbies
        return this.gameController;
    }
    private void react(SCKMessage sckMessage) { //qui in base al messaggio letto chiamiamo il giusto metodo della ClientActionsInterface
        //al posto dello switch qui userò sempre l'attributo ClientMessage della classe SCKMessage

        sckMessage.getClientMessage().execute(this);

    }



    //methods to be implemented to have a class that implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)



    @Override
    public void update(Observable obs, CODEX.utils.executableMessages.events.Event e) throws RemoteException {
        boolean check=e.executeSCKServerSide();
        if(check){ //true if the game state has changed to 'STARTED'


            this.pongReceived=true; //initialization
            this.timer = new Timer(true); //isDaemon==true -> maintenance activities performed as long as the application is running
            //we need to use ping-pong messages because sometimes the connection seems to be open (we do not receive any I/O exception) but it is not.
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(pongReceived) {
                        pongReceived=false;
                        CODEX.utils.executableMessages.serverMessages.ServerMessage serverMessage= new ServerPing();
                        writeTheStream(new SCKMessage(serverMessage));
                    }else{ //there are no pongs received
                        System.out.println("the connection has been interrupted...Bye bye");
                        try {
                            running=false;
                            input.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) { //needed for the close clause
                            throw new RuntimeException(e);
                        }
                        timer.cancel(); // Ferma il timer
                        if(gameController.getFirstDisconnection()){
                            gameController.setFirstDisconnection(false); //chiamo solo una volta disconnection() anche se sono più client a disconnettersi
                            gameController.disconnection();; //bisogna settare qualche parametro in caso di più disconnection() in contemporanea per non mandare troppi disconnectionEvent
                        }
                    }
                }
            }, 0, 10000); // Esegui ogni 10 secondi


        }

        writeTheStream(new SCKMessage(e));

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

    //writeTheStream will be called in the method react (maybe inside the methods of the ClientActionsInterface) to tell the client what the controller has effectively done
    //writeTheStream will also be called in the method update (we have it because ClientHandlerThread is a listener) to tell the client what is changed in the model
    synchronized public void writeTheStream(SCKMessage sckMessage){ //this is the stream the real client can read
        try {
            output.writeObject(sckMessage);
            output.flush();
            output.reset();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("the connection has been interrupted....Bye bye");
            try { //we close all we have to close
                running=false;
                input.close();
                output.close();
                socket.close();
            } catch (IOException ex) { //needed for the close clause
                throw new RuntimeException(ex);
            }
            timer.cancel(); // Ferma il timer
            gameController.disconnection();
        }
    }

    //methods to be implemented to have a class that implements ClientActionsInterface

    //these methods should be private because they will never be called from the outside

    //ATTENZIONE: vanno riviste le RemoteException perchè vengono chiamate solo nell'update
    // del WrappedObserver però al posto di mettere un try/catch sono state messe nella
    // signature e quindi qui mi da errore se non le inserisco anche se l'update TCP non ne ha bisogno

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
    public void addPlayerToLobby(String playerNickname, int gameId)  {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        //probabilmente questo messaggio ci serve per rendere TCP sincrono come RMI (il client aspetta che gli venga detto è andato tutto a buon fine)
        try {
            this.gameController=serverController.addPlayerToLobby(playerNickname, gameId);
            setNickname(playerNickname); //we have an attribute nickname in this class (we are implementing Observer)
            //mi serve per i test che il ClientSCK abbia il gameid

            //IMPORTANTE per ricevere le notify e gli update
            this.gameController.addClient(this.nickname, this);
            ServerMessage serverMessage=new ServerOk(this.gameController.getId());
            writeTheStream(new SCKMessage(serverMessage)); //ci serve il messaggio per dire al ClientSCK il server ha fatto quello che hai chiesto (lo blocchiamo fino a quel momento)
        }catch (RemoteException ignored){//non verrà mai lanciata

        } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException ex) {
            ServerMessage serverMessage= new ServerError(ex.getAssociatedEvent());
            writeTheStream(new SCKMessage(serverMessage));
        }
    }


    /**
     * This method, will be used to call the ServerController method
     * @param nickname
     * @throws RemoteException
     * @throws NotBoundException
     * @throws NicknameAlreadyTakenException
     */
    @Override
    public void chooseNickname(String nickname) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            serverController.chooseNickname(nickname);
            setNickname(nickname); //we have an attribute nickname in this class (we are implementing Observer)
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage)); //ci serve il messaggio per dire al ClientSCK il server ha fatto quello che hai chiesto (lo blocchiamo fino a quel momento)
        }catch (RemoteException ignored){

        } catch (NicknameAlreadyTakenException ex) {
            ServerMessage serverMessage= new ServerError(ex.getAssociatedEvent());
            writeTheStream(new SCKMessage(serverMessage));
        }
    }


    /**
     * This method, will be used to call the ServerController method
     * @param creatorNickname
     * @param numOfPlayers
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController=serverController.startLobby(creatorNickname, numOfPlayers);
            setNickname(creatorNickname); //we have an attribute nickname in this class (we are implementing Observer)

            //IMPORTANTE per ricevere le notify e gli update
            this.gameController.addClient(this.nickname,this);

            ServerMessage serverMessage=new ServerOk(this.gameController.getId());
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){
        }
    }


    //quando potrà venir chiamato questo metodo il nickname sarà già settato in questa classe (al posto di riceverlo possiamo prendere quello già salvato qui)
    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try { //possiamo prendere nickname dagli attributi della classe al posto che dai parametri
            this.gameController.playCard(nickname, selectedCard, position, orientation);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (IllegalArgumentException e){
            ServerMessage serverMessage= new ServerError(ErrorsAssociatedWithExceptions.UNABLE_TO_PLAY_CARD);
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){

        }
    }

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.playBaseCard(nickname, baseCard, orientation);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){

        }
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.drawCard(nickname, selectedCard);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){
        }
    }

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){
        }
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.choosePawnColor(chooserNickname, selectedColor);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){

        }
    }


    //that's a method to be used to communicate between the players
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.sendMessage(senderNickname, receiversNickname, message);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored) {
        }
    }

    @Override
    public void leaveGame(String nickname){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.leaveGame(nickname);  //questo metodo prima di aggiornare gli altri giocatori dovrebbe togliere dalla lista di listeners quello che l'ha chiamato
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
            running=false; //così mi si fermano i thread interni
            try { //we close all we have to close
                running=false;
                input.close();
                output.close();
                socket.close();
            } catch (IOException ex) { //needed for the close clause
                throw new RuntimeException(ex);
            }
            timer.cancel(); // Ferma il timer
        }catch (RemoteException | IllegalArgumentException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }


    //end of methods to be implemented taken from ClientActionsInterface
}

