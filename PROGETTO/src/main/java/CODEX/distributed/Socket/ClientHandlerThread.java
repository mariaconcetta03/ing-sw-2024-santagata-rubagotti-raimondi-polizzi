package CODEX.distributed.Socket;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.controller.GameController;
import CODEX.controller.ServerController;
import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.messages.Message;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.Coordinates;
import CODEX.org.model.ObjectiveCard;
import CODEX.org.model.Pawn;
import CODEX.org.model.PlayableCard;
import CODEX.utils.Event;
import CODEX.utils.Observable;
import CODEX.utils.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
    private ServerController serverController; //to be passed as parameter in the constructor method
    //private final Thread threadCheckConnection;
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
        this.serverController = serverController;
        this.socket = client; //the client can communicate with the server by this thread using this socket

        this.output = new ObjectOutputStream(client.getOutputStream());
        this.input = new ObjectInputStream(client.getInputStream());


        this.inputLock = new Object();

        this.running = true;

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

        /*
        //da riguardare più avanti
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


         */

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
        System.out.println("sono nel run");
        try {
            System.out.println("sono nel try");
            while (!Thread.currentThread().isInterrupted()&&running) { //questo while dovrebbe servere nel caso in cui non vogliamo che il Thread venga interrotto mentre fa quello dentro il while
                System.out.println("sono nel while");
                synchronized (inputLock) { //we use the inputLock to write the stream not simultaneously
                    System.out.println("sono nel syn");
                    SCKMessage sckMessage = (SCKMessage) this.input.readObject(); //messaggi scritti dal Client vero
                    if(sckMessage!=null) {
                        System.out.println("messaggio non nullo");
                        react(sckMessage);
                    }else {
                        System.out.println("messaggio nullo");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void react(SCKMessage sckMessage){ //qui in base al messaggio letto chiamiamo il giusto metodo della ClientActionsInterface
        //legge il messaggio e fa qualcosa
        switch (sckMessage.getMessageEvent()) {
            /*
            case START -> { //this message is sent by ClientSCK in response to ALL_CONNECTED (sent by the server)
                threadCheckConnection.start(); // we start to check if the ClientSCK is still connected
            }

             */
            case AVAILABLE_LOBBY -> {
                try {
                    System.out.println("sono in available lobby");
                    List<Object> list = new ArrayList<>();
                    list.addAll(serverController.getAllGameControllers().keySet());
                    writeTheStream(new SCKMessage(list, Event.AVAILABLE_LOBBY));
                }catch (RemoteException ignored){}
            }
            case ADD_PLAYER_TO_LOBBY->{
                System.out.println("sono in ADD_PLAYER_TO_LOBBY");
                try {
                    addPlayerToLobby((String) sckMessage.getObj().get(0), (Integer) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case CHOOSE_NICKNAME->{
                try {
                    chooseNickname((String)sckMessage.getObj().get(0));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case CREATE_LOBBY->{
                System.out.println("sono in CREATE_LOBBY");
                try {
                    createLobby((String)sckMessage.getObj().get(0),(int) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case PLAY_CARD->{
                try {
                    playCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1), (Coordinates) sckMessage.getObj().get(2), (boolean) sckMessage.getObj().get(3));
                 }catch (Exception e){
                    System.err.println(e.getMessage());
                 }
            }
            case PLAY_BASE_CARD->{
                try {
                    System.out.println("sono in PLAY_BASE_CARD");
                    playBaseCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1), (boolean) sckMessage.getObj().get(2));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case DRAW_CARD->{
                try {
                    drawCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case CHOOSE_OBJECTIVE_CARD->{
                try {
                    chooseObjectiveCard((String) sckMessage.getObj().get(0), (ObjectiveCard) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case CHOOSE_PAWN_COLOR->{
                try {
                    choosePawnColor((String) sckMessage.getObj().get(0), (Pawn) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case SEND_MESSAGE->{
                try {
                    sendMessage((String) sckMessage.getObj().get(0), (List<String>) sckMessage.getObj().get(1), (String) sckMessage.getObj().get(2));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case LEAVE_GAME->{
                try {
                    leaveGame((String) sckMessage.getObj().get(0));
                    //running=false;
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
            case CHECK_N_PLAYERS -> { //farà partire gli update che dicono che il gioco è iniziato
                try {
                    gameController.checkNPlayers(); //bisogna aggiungere qui dentro un'eccezione per dire 'game already started'
                    writeTheStream(new SCKMessage(null, Event.OK));
                } catch (RemoteException e) { //da cancellare perchè serve solo ad rmi
                    throw new RuntimeException(e);
                }
            }
        }

    }


    //methods to be implemented to have a class that implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)


    // quando il Game viene creato sarebbe meglio avere un update specifico nella classe Observer per non dover aspettare il messaggio di START del
    // client per far partire il thread che controlla la connessione (il Client nel frattempo potrebbe essersi sconnesso), in questo modo iniziamo
    // subito a controllare la connessione e se il client in fase di lobby si è disconnesso ce ne accorgiamo.
    // questo update specifico andrebbe a far partire il thread check connection poco prima di avvisare il client che la partita è iniziata (con un messaggio)
    @Override
    public void update(Observable obs, Message arg) { //here we call writeTheStream (the switch case is already in ClientSCK)
        if(arg.getMessageEvent().equals(Event.UPDATED_PLAYER_DECK)){
            List<Object> list=new ArrayList<>();
            list.add(arg.getObj().get(0));
            PlayableCard[] playableCards=(PlayableCard[]) arg.getObj().get(1);
            for (PlayableCard c:playableCards){
                list.add(c);
            }
            System.out.println("sono in update");
            System.out.println(arg.getMessageEvent());
            //writeTheStream(message);
            writeTheStream(new SCKMessage(list,arg.getMessageEvent()));
            return;
        }
        if(!(arg.getMessageEvent().equals(Event.OK))){ //non mi servono i messaggi di update ok dal controller
            System.out.println("sono in update");
            System.out.println(arg.getMessageEvent());
            //writeTheStream(message);
            writeTheStream(new SCKMessage(arg.getObj(),arg.getMessageEvent())); //qui dobbiamo vedere se far diventare Message e SCKMessage la stessa cosa
        }
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
            System.out.println("sono nel try di writeTheStream");
            output.writeObject(sckMessage);
            output.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
        System.out.println("sono in addPlayerToLobby");
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        //probabilmente questo messaggio ci serve per rendere TCP sincrono come RMI (il client aspetta che gli venga detto è andato tutto a buon fine)
        try {
            System.out.println("sono nel try di addPlayerToLobby");
            this.gameController=serverController.addPlayerToLobby(playerNickname, gameId);
            setNickname(playerNickname); //we have an attribute nickname in this class (we are implementing Observer)
            //mi serve per i test che il ClientSCK abbia il gameid

            //IMPORTANTE per ricevere le notify e gli update
            this.gameController.addClient(this);

            writeTheStream(new SCKMessage(null,Event.OK)); //ci serve il messaggio per dire al ClientSCK il server ha fatto quello che hai chiesto (lo blocchiamo fino a quel momento)
        }catch (RemoteException e){//non verrà mai lanciata
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        } catch (GameAlreadyStartedException | FullLobbyException | GameNotExistsException ex) {
            writeTheStream(new SCKMessage(null, ex.getAssociatedEvent()));
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
            writeTheStream(new SCKMessage(null,Event.OK)); //ci serve il messaggio per dire al ClientSCK il server ha fatto quello che hai chiesto (lo blocchiamo fino a quel momento)
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        } catch (NicknameAlreadyTakenException ex) {
            writeTheStream(new SCKMessage(null,ex.getAssociatedEvent()));
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
        System.out.println("sono nel createLobby");
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            System.out.println("sono nel try di createLobby");
            this.gameController=serverController.startLobby(creatorNickname, numOfPlayers);
            setNickname(creatorNickname); //we have an attribute nickname in this class (we are implementing Observer)

            //IMPORTANTE per ricevere le notify e gli update
            this.gameController.addClient(this);

            //per il test
            List<Object>list=new ArrayList<>();
            list.add(this.gameController.getId());

            writeTheStream(new SCKMessage(list,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }


    //quando potrà venir chiamato questo metodo il nickname sarà già settato in questa classe (al posto di riceverlo possiamo prendere quello già salvato qui)
    @Override
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try { //possiamo prendere nickname dagli attributi della classe al posto che dai parametri
            this.gameController.playCard(nickname, selectedCard, position, orientation);
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }

    @Override
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            System.out.println("sono nel try di playBaseCard");
            this.gameController.playBaseCard(nickname, baseCard, orientation);
            System.out.println("scrivo il messaggio di OK di playBaseCard");
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }

    @Override
    public void drawCard(String nickname, PlayableCard selectedCard) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.drawCard(nickname, selectedCard);
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }

    @Override
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) {
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }

    @Override
    public void choosePawnColor(String chooserNickname, Pawn selectedColor){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.choosePawnColor(chooserNickname, selectedColor);
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }


    //that's a method to be used to communicate between the players
    @Override
    public void sendMessage(String senderNickname, List<String> receiversNickname, String message){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.sendMessage(senderNickname, receiversNickname, message);
            writeTheStream(new SCKMessage(null,Event.OK));
        }catch (RemoteException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }

    @Override
    public void leaveGame(String nickname){
        //here we call the controller and we save the response in message (so that the real client ClientSCK can read it)
        try {
            this.gameController.leaveGame(nickname);  //questo metodo prima di aggiornare gli altri giocatori dovrebbe togliere dalla lista di listeners quello che l'ha chiamato
            writeTheStream(new SCKMessage(null,Event.OK));
            running=false; //così mi si fermano i thread interni
            try {
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }catch (RemoteException | IllegalArgumentException e){
            System.err.println(e.getMessage()); //cosa ci faccio con questa eccezione? (viene lanciata nell'update di WrappedObserver->va gestita in modo diverso)
        }
    }


    //end of methods to be implemented taken from ClientActionsInterface
}

