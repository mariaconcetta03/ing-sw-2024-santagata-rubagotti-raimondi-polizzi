package distributed.Socket;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import controller.ServerController;
import distributed.messages.Message;
import distributed.messages.SCKMessage;
import org.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

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
public class ClientHandlerThread implements Runnable, Observer { //this is a Thread
    private final Socket socket;
    private GameController associatedGameController; //returned by ServerController' startlobby()/addPlayerToLobby()
    private boolean hasAlreadyAGame=false; //this flag is necessary if we don't want to generate some avoidable errors (see the explanation above)
    private String nickname = null;
    private Player personalPlayer= new Player(); //it could be properly initialized with the method addPlayerToGame or addPlayerToLobby or createLobby
    private ServerController serverController; //to be passed as parameter in the constructor method

    private ClientSCK clientSCK= null; //that's not a real socket but contains all the implemented method of the ClientGeneralInterface

    private final Thread threadCheckMSG;
    private final Object inputLock;

    private final ObjectInputStream input;
    private final ObjectOutputStream output;

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

        //qui dovremmo far partire il thread (non bloccante) che continua a fare update
        //siamo noi che dobbiamo chiedere al server gli aggiornamenti o è il server che chiama noi?
        threadCheckMSG = new Thread(()-> {
            synchronized (inputLock) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        SCKMessage sckMessage = (SCKMessage) this.input.readObject(); //messaggi scritti dal Client vero
                        react(sckMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        },"CheckUpdateBoard"); //to be started when all the players are connected


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


    public void run() { //ci sono altre cose da aggiungere tipo chiamare i metodi della ClientGeneralInterface in base al messaggio ricevuto
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inputLock) {
                    SCKMessage message = (SCKMessage) this.input.readObject();
                    Object obj = null;
                    if (message.getMessageEvent() == Event.START) {
                        if (!threadCheckMSG.isAlive()) {
                            threadCheckMSG.start();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void react(SCKMessage sckMessage){
        //legge il messaggio e fa qualcosa
    }


    //we can add method in which we can control the status of the connection (asking it to the controller)

 //funzioni che può chiamare il client (tramite la run()) si potrebbero mettere come private perchè non verranno chiamate da fuori

    //if this method doesn't give errors we can save the gameId (if we want to use it)

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
    private void addPlayerToLobby (String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException{
       serverController.addPlayerToLobby(playerNickname, gameId);
   }

    /**
     * This method, will be used to call the ServerController method
     * @param nickname
     * @throws RemoteException
     * @throws NotBoundException
     * @throws NicknameAlreadyTakenException
     */
    private void chooseNickname (String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException{
        serverController.chooseNickname(nickname);
    }

    /**
     * This method, will be used to call the ServerController method
     * @param creatorNickname
     * @param numOfPlayers
     * @throws RemoteException
     * @throws NotBoundException
     */
    private void createLobby (String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException{
        serverController.startLobby(creatorNickname, numOfPlayers);
    }

    private void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException{}

    private void playBaseCard (String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException{}

    private void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException{}

    private void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException, NotBoundException{}

    private void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException{
        associatedGameController.choosePawnColor(chooserNickname, selectedColor);
    }

    private void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException, NotBoundException{}

    //we have the nickname saved in personalPlayer but we don't have the game controller used in thi method (as suggested
    // we should call the server controller which has tha Map with all game controllers saved and can call it by himself)
    private void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException{}

    //methods to be implemented to have a class that implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)
    public void updateBoard(Board board){

    }

    @Override
    public void update(Observable obs, Message arg) { //here we call writeTheStream
        //switch(arg.get)
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return null;
    }


    public void writeTheStream(SCKMessage message){ //this is the stream the real client can read
        try {
            output.writeObject(message);
            output.flush();
            output.reset();
        } catch (IOException e) {
        }
    }
}

