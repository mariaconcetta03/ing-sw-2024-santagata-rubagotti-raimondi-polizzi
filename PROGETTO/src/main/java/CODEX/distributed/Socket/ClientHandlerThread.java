package CODEX.distributed.Socket;

import CODEX.Exceptions.*;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * This class represents the Client (server-side) who chooses TCP as network protocol
 * It is notified of all the changes in the model and forwards them, using its socket attribute,
 * to ClientSCK. It is a thread, so we will not have problems related to congestion (as we could have had in RMI).
 * ---------------------------------------------------------------------------------------------------------------
 * The server communicates with the clients calling this class' methods of update, the clients communicate
 * with the server using first the attribute serverController and then the attribute gameController (when the game is created)
 */
public class ClientHandlerThread implements Runnable, Observer, ClientActionsInterface {
    private final Socket socket;
    private String nickname = null;
    private final ServerController serverController; // to be passed as parameter in the constructor method
    private final Object inputLock;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private GameController gameController;
    private Boolean running;
    private boolean pongReceived;
    private Timer timer;
    private boolean aDisconnectionHappened=false;



    /**
     * Class constructor
     * @param client
     * @param serverController
     * @throws IOException
     */
    public ClientHandlerThread(Socket client,ServerController serverController) throws IOException {
        this.serverController = serverController;
        this.socket = client; // the client can communicate with the server by this thread using this socket

        this.output = new ObjectOutputStream(client.getOutputStream());
        this.input = new ObjectInputStream(client.getInputStream());

        this.inputLock = new Object();

        this.running = true;

    }






    // METHOD OVERRIDDEN FROM RUNNABLE

    /**
     * Run method of the Runnable interface [thread]
     */
    @Override
    public void run() {
            while (!Thread.currentThread().isInterrupted()&&running) {
                synchronized (inputLock) { // we use the inputLock to write the stream not simultaneously
                    SCKMessage sckMessage = null; // messages written by ClientSCK
                    try {
                        sckMessage = (SCKMessage) this.input.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("lost the connection...Bye, bye");
                        running=false;
                        try {
                            input.close();
                            output.close();
                            socket.close();
                        } catch (IOException ignored) { // needed for the close clauses
                        }
                        timer.cancel(); // stops the timer

                        gameController.disconnection();

                        Thread.currentThread().interrupt(); // stopping the ClientHandlerThread
                        System.out.println("appena chiamato interrupt");
                    }
                    if(sckMessage!=null&&running) {
                        react(sckMessage);
                    }
                }
            }
    }






    // METHODS OVERRIDDEN FROM OBSERVER

    /**
     * This method is called by the classes that extend observable and forwards the update received
     * to ClientSCK through the socket attributes of this class (the function writeTheStream does the forwarding)
     * @param obs observable
     * @param e event
     * @throws RemoteException
     */
    @Override
    public void update(Observable obs, CODEX.utils.executableMessages.events.Event e) {
        boolean check=e.executeSCKServerSide();
        if(check){ // true if the gameState has changed to 'STARTED'
            this.pongReceived=true; // initialization
            // isDaemon==true -> maintenance activities performed as long as the application is running
            this.timer = new Timer(true);
            // we need to use ping-pong messages because sometimes the connection seems
            // to be open (we do not receive any I/O exception) but it is not.
            timer.scheduleAtFixedRate(new TimerTask() {

                /**
                 * Run method of the Runnable interface [thread]
                 * When we receive an updateGameStateEvent and the GameState has changed to STARTED, we
                 * start the thread that checks the connection
                 */
                @Override
                public void run() {
                    if(pongReceived&& !aDisconnectionHappened) {
                        pongReceived=false;
                        CODEX.utils.executableMessages.serverMessages.ServerMessage serverMessage= new ServerPing();
                        writeTheStream(new SCKMessage(serverMessage));

                    }else{ // there are no pongs received
                        System.out.println("the connection has been interrupted...Bye bye");
                        try {
                            running=false;
                            input.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) { // needed for the close clause
                        }
                        timer.cancel(); // stops the timer

                         gameController.disconnection();
                    }
                }
            }, 0, 10000); // every 10 seconds
        }
        writeTheStream(new SCKMessage(e));
    }



    /**
     * Setter method
     * @param nickname of the player
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



    /**
     * Setter method
     * @param aDisconnectionHappened true if a disconnection happened, false otherwise
     */
    @Override
    public void setADisconnectionHappened(boolean aDisconnectionHappened) {
        this.aDisconnectionHappened=aDisconnectionHappened;
    }






// METHODS OVERRIDDEN FROM ClientActionsInterface

    /**
     * This method, will be used to call the ServerController method
     * @param playerNickname to add to the lobby
     * @param gameId game where we need to add the player
     */
    @Override
    public void addPlayerToLobby(String playerNickname, int gameId) {
        // here we call the serverController and we save the response in a message (so that the
        // real client ClientSCK can read it) we need this message to make TCP synchronous like RMI
        try {
            this.gameController=serverController.addPlayerToLobby(playerNickname, gameId);
            setNickname(playerNickname); // we have an attribute nickname in this class [because of Observer]

            // we add the client to the GameController's list of observers to receive the updates
            this.gameController.addClient(this.nickname, this);

            ServerMessage serverMessage=new ServerOk(this.gameController.getId());
            // we write the return value of the GameController method to unlock the client that is waiting it
            writeTheStream(new SCKMessage(serverMessage));

        }catch (RemoteException ignored){ // never thrown
        } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException ex) {
            ServerMessage serverMessage= new ServerError(ex.getAssociatedEvent());
            writeTheStream(new SCKMessage(serverMessage));
        }
    }



    /**
     * This method, will be used to call the ServerController method
     * @param nickname of the player
     */
    @Override
    public void chooseNickname(String nickname) {
        try {
            serverController.chooseNickname(nickname);
            setNickname(nickname); // we have an attribute nickname in this class (we are implementing Observer)
            ServerMessage serverMessage=new ServerOk();
            // we write the return value of the serverController method to unlock the client that is waiting it
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){
        } catch (NicknameAlreadyTakenException ex) {
            ServerMessage serverMessage= new ServerError(ex.getAssociatedEvent());
            writeTheStream(new SCKMessage(serverMessage));
        }
    }



    /**
     * This method, will be used to call the ServerController method
     * @param creatorNickname player who created the lobby
     * @param numOfPlayers number of players
     */
    @Override
    public void createLobby(String creatorNickname, int numOfPlayers) {
        // here we call the serverController and we save the gamaController returned
        try {
            this.gameController=serverController.startLobby(creatorNickname, numOfPlayers);
            setNickname(creatorNickname); // we have an attribute nickname in this class (we are implementing Observer)

            // we add the client to the GameController's list of observers to receive the updates
            this.gameController.addClient(this.nickname,this);

            ServerMessage serverMessage=new ServerOk(this.gameController.getId());
            writeTheStream(new SCKMessage(serverMessage));
        }catch (RemoteException ignored){
        }
    }


    /**
     * This method is invoked when a player wants to play a card
     * @param nickname of the player who is playing
     * @param selectedCard card played
     * @param position of the card
     * @param orientation of the card (true = front / false = back)
     */
    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) {
        // we write the return value of the gameController method to unlock the client that is waiting it
        try {
            this.gameController.playCard(nickname, selectedCard, position, orientation);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (IllegalArgumentException e){
            ServerMessage serverMessage= new ServerError(ErrorsAssociatedWithExceptions.UNABLE_TO_PLAY_CARD);
            writeTheStream(new SCKMessage(serverMessage));
        }
    }



    /**
     * This method is invoked when a player wants to play a baseCard
     * @param nickname of the player who is playing
     * @param baseCard card played
     * @param orientation of the card (true = front / false = back)
     */
    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) {
        // we write the return value of the GameController method to unlock the client that is waiting it
        this.gameController.playBaseCard(nickname, baseCard, orientation);
        ServerMessage serverMessage=new ServerOk();
        writeTheStream(new SCKMessage(serverMessage));
    }



    /**
     * This method allows a player to draw a card
     * @param nickname player's nickname
     * @param selectedCard card to draw
     */
    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) {
        // we write the return value of the GameController method to unlock the client that is waiting it

            this.gameController.drawCard(nickname, selectedCard);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));

    }



    /**
     * This method allows a player to choose his personal objective
     * @param chooserNickname player's nickname
     * @param selectedCard selected objective
     */
    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) {
        // we write the return value of the GameController method to unlock the client that is waiting it

            this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));

    }



    /**
     * This method allows a player to choose his pawn color
     * @param chooserNickname player's nickname
     * @param selectedColor selected pawn
     */
    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor){
    // we write the return value of the GameController method to unlock the client that is waiting it
        try {
            this.gameController.choosePawnColor(chooserNickname, selectedColor);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));
        }catch (ColorAlreadyTakenException e){
            ServerMessage serverMessage= new ServerError(ErrorsAssociatedWithExceptions.COLOR_ALREADY_TAKEN);
            writeTheStream(new SCKMessage(serverMessage));
        }
    }



    /**
     * This method is used to send a message between two or more players
     * @param senderNickname nickname of the sender
     * @param receiversNickname nicknames of the receivers
     * @param message text message
     */
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message){
        // we write the return value of the GameController method to unlock the client that is waiting it

            this.gameController.sendMessage(senderNickname, receiversNickname, message);
            ServerMessage serverMessage=new ServerOk();
            writeTheStream(new SCKMessage(serverMessage));

    }



    /**
     * When a player wants to leave the game the server detects a
     * disconnection so this function will never be called
     * @param nickname of the player who is leaving
     */
    @Override
    public void leaveGame(String nickname){
    }




    // STANDARD METHODS OF THE CLASS [NO OVERRIDING]

    /**
     * This method is called when the socket stream is read and calls the execute method of
     * the client message received. The execute method would call the methods of the clientActionInterface
     * @param sckMessage message
     */
    private void react(SCKMessage sckMessage) {
        // here I will always use the ClientMessage attribute of the SCKMessage class
        sckMessage.getClientMessage().execute(this);
    }



    /**
     * This method will be called inside the method execute of the clientMessage received after a
     * gameController/serverController methods invocation to tell the client what the controller has
     * effectively done (in this case we write a serverMessage)
     *
     * WriteTheStream will also be called in the method update (we have it because ClientHandlerThread
     * is an observer) to tell the ClientSCK what is changed in the model
     */
    synchronized public void writeTheStream(SCKMessage sckMessage){
        if(running) {
            try {
                output.writeObject(sckMessage);
                output.flush();
                output.reset();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.out.println("the connection has been interrupted....Bye bye");
                try { // we close all we have to close
                    running = false;
                    input.close();
                    output.close();
                    socket.close();
                } catch (IOException ex) { // needed for the close clause

                }
                timer.cancel(); // stops the timer

                gameController.disconnection();

                Thread.currentThread().interrupt();
            }
        }else {
            Thread.currentThread().interrupt();
        }
    }



    /**
     * Getter method
     * @return server controller
     */
    public ServerController getServerController(){
        return this.serverController;
    }



    /**
     * Getter method
     * @return game controller
     */
    public GameController getGameController(){
        return this.gameController;
    }



    /**
     * Setter method
     * @param received pong received (true or false to check if the pong has arrived)
     */
    public void setPongReceived(Boolean received){
        this.pongReceived=received;
    }

}

