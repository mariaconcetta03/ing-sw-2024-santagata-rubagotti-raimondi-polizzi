package distributed.Socket;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import controller.ServerController;
import distributed.ClientActionsInterface;
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
public class ClientHandlerThread implements Runnable, Observer, ClientActionsInterface { //this is a Thread
    private final Socket socket;
    private GameController associatedGameController; //returned by ServerController' startlobby()/addPlayerToLobby()

    // private boolean hasAlreadyAGame=false; //this flag is necessary if we don't want to generate some avoidable errors (see the explanation above)
    private String nickname = null;
    private Player personalPlayer= new Player(); //it could be properly initialized with the method addPlayerToGame or addPlayerToLobby or createLobby
    private ServerController serverController; //to be passed as parameter in the constructor method

    private ClientSCK clientSCK= null; //this class writes what the client wants in the stream and it's local to the client



    private final Thread threadCheckConnection;
    private final Object inputLock;

    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private GameController gameController;

    private Boolean running; //it is initialized true, when becomes false the while in run and threadCheckConnection terminate.


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

        this.running=true;

        /*

        //potremmo far fare questo lavoro al Thread principale (la run del thread principale se no si interrompe dopo il messaggio di START

        //this Thread is needed because some actions of the client can't be predicted and isolated in the question-response pattern
        threadCheckMSG = new Thread(()-> { //dopo il messaggio di START ricevuto dal client è questo thread che si occupa di leggere i messaggi
            synchronized (inputLock) {
                while (!Thread.currentThread().isInterrupted()&&running) {
                        SCKMessage sckMessage = (SCKMessage) this.input.getObjectInputFilter(); //messaggi scritti dal Client vero
                        react(sckMessage);
                }
            }
        },"CheckMSG"); //to be started when all the players are connected

         */

        //to control the status of the connection (a player can leave the game without any advice)
        threadCheckConnection= new Thread(()-> {
                while (!Thread.currentThread().isInterrupted()&&running) { //we enter here every time this ClientHandlerThread is not interrupted by other ClientHandlerThread
                    try {
                        output.writeObject(new PingMessage());
                        output.flush();
                        output.reset();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    // here we set a timeout and then we check the response, if there is no response we declared the connection dead
                    // we set the timeout
                    // we check the response
                    try {
                        Timer t = new Timer();
                        synchronized (inputLock) { //non ci deve essere lo stesso lock anche a lato ClientSCK perchè ClientSCK scrive questo stream, ClientHandlerThread lo legge solo
                            t.wait(2000);
                            PingMessage pingMessage = (PingMessage) this.input.getObjectInputFilter();
                        }
                        } catch (InterruptedException e) { //no response-> we free the thread and we end the game
                        //Game.GameState.ENDED
                        System.out.println(e.getMessage()); //per adesso lasciamo così poi va sostituito con qualcosa che avvisa tutti i giocatori che il gioco è terminato
                        try { //devo fermare i thread lanciati all'interno di questo thread
                            input.close();
                            output.close();
                            socket.close(); //the ClientSCK will receive an exception
                            running=false;
                            this.gameController.leaveGame(nickname); //questo metodo prima di aggiornare gli altri giocatori dovrebbe togliere dalla lista di listeners quello che l'ha chiamato
                        } catch (IOException ex) { //this catch is needed for the close statements
                            throw new RuntimeException(ex);
                        }
                    }
                }

        },"CheckConnection"); //to be started when a Game is created (when we receive START from ClientSCK)
    }




      /*
            // alla fine devo chiudere gli stream e il socket
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {  //bisogna notificare se è andato tutto a buon fine?
            System.err.println(e.getMessage());
        }


     */


    //when a Thread starts, run is automatically called
    public void run() { //questo metodo viene chiamato in automatico quando si fa submit alla thread pool. Lo uso per fare start dei thread interni a questo ClientHandlerThread
        try {
            while (!Thread.currentThread().isInterrupted()&&running) { //questo while dovrebbe servere nel caso in cui non vogliamo che il Thread venga interrotto mentre fa quello dentro il while
                synchronized (inputLock) { //we use the inputLock to write the stream not simultaneously
                    SCKMessage sckMessage = (SCKMessage) this.input.getObjectInputFilter(); //messaggi scritti dal Client vero
                    react(sckMessage);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void react(SCKMessage sckMessage){ //qui in base al messaggio letto chiamiamo il giusto metodo della ClientGeneralInterface
        //legge il messaggio e fa qualcosa (si può prendere spunto dalla run commentata che leggeva testo dagli stream)
        switch (sckMessage.getMessageEvent()) {
            case START -> { //this message is sent by ClientSCK in response to ALL_CONNECTED (sent by the server)
                threadCheckConnection.start(); // we start to check if the ClientSCK is still connected
            }
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
                    //running=false;
                }catch (Exception e){

                }
            }
        }

    }


    //methods to be implemented to have a class that implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)

    @Override
    public void update(Observable obs, Message arg) { //here we call writeTheStream (the switch case is already in ClientSCK)
        //writeTheStream(message);
        writeTheStream(new SCKMessage(arg.getObj(),arg.getMessageEvent())); //qui dobbiamo vedere se far diventare Message e SCKMessage la stessa cosa
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
    public void writeTheStream(SCKMessage sckMessage){ //this is the stream the real client can read
        try {
            output.writeObject(sckMessage);
            output.flush();
            output.reset();
        } catch (IOException e) {
        }
    }

    //methods to be implemented to have a class that implements ClientActionsInterface

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
        writeTheStream(sckMessage); //ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
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
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
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
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
        //probabilmente questo messaggio ci serve per rendere TCP sincrono come RMI (il client aspetta che gli venga detto è andato tutto a buon fine)
    }

    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.playCard(nickname, selectedCard, position, orientation);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.playBaseCard(nickname, baseCard, orientation);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.drawCard(nickname, selectedCard);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.choosePawnColor(chooserNickname, selectedColor);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }


    //that's a method to be used to communicate between the players
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException, NotBoundException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.sendMessage(senderNickname, receiversNickname, message);
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
    }

    @Override
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException {
        SCKMessage sckMessage=null;
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        this.gameController.leaveGame(nickname);  //questo metodo prima di aggiornare gli altri giocatori dovrebbe togliere dalla lista di listeners quello che l'ha chiamato
        writeTheStream(sckMessage);//ci serve il messaggio mer dire al ClientSCK il server ha fatto quello che hai chiesto?
        running=false; //così mi si fermano i thread interni
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //end of methods to be implemented taken from ClientActionsInterface
}

