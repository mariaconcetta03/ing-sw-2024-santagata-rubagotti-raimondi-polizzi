package distributed.Socket;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import controller.ServerController;
import distributed.ClientGeneralInterface;
import org.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import utils.Observer;
import java.util.Scanner;

public class ClientHandlerThread implements Runnable, Observer { //this is a Thread (it isn't blocking)

    //if we memorize the nickname in our personal player we should obtain some errors if we call together addPlayerToGame, addPlayerToLobby, createLobby
    //because we use the same personalPlayer that has a unique nickname.
    boolean hasAlreadyAGame=false; //this flag is necessary if we don't want to generate some avoidable errors (see the explanation above)

    Player personalPlayer= new Player(); //it could be properly initialized with the method addPlayerToGame or addPlayerToLobby or createLobby
    ServerController serverController;
    GameController gameController; //this has to be set through the help of serverController (or even better we can use only serverController and this one calls the associated gameController)
    private final Socket socket;
    //what is the difference with Scanner and Printer? (can we use the print function with these below?)
    //private final ObjectOutputStream output; //used to send
    //private final ObjectInputStream input; //used to receive
    ClientSCK clientSCK= null; //that's not a real socket but contains all the implemented method of the ClientGeneralInterface

    public ClientHandlerThread(Socket client) {
        this.socket = client; //the client can communicate with the server by this thread using this socket
        //here we would have to initialize
        //this.output = new ObjectOutputStream(client.getOutputStream());
        //this.input = new ObjectInputStream(client.getInputStream());
    }
    //to decide what to do we can ask the client controller (which is the one who have to initialize ClientSCK)
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
                                addPlayerToLobby(personalPlayer, gameId); //what we want the client decide about his player in this stage
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
                            chooseNickname (personalPlayer, line);
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
                                createLobby(personalPlayer, Integer.parseInt(line));
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
                                choosePawnColor(personalPlayer, selectedColor);
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
    //we can add method in which we can control the status of the connection (asking it to the controller)

 //funzioni che può chiamare il client (tramite la run()) si potrebbero mettere come private perchè non verranno chiamate da fuori

    //if this method doesn't give errors we can save the gameId (if we want to use it)
    private void addPlayerToLobby (Player p, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException{
       serverController.addPlayerToLobby(p, gameId);
   }
    private void chooseNickname (Player chooser, String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException{
        serverController.chooseNickname(chooser, nickname);
    }

    //here the ServerController creates a new instance of GameController
    private void createLobby (Player creator, int numOfPlayers) throws RemoteException, NotBoundException{
        serverController.startLobby(creator, numOfPlayers);
    }

    //internal use? this method should create the association between the client and the game (memorized in a Map in the ServerController)
    private void createGame (List<Player> gamePlayers) throws RemoteException, NotBoundException{}

    // if we call this method we should already have the attribute gameController memorized in this thread...because it use gameController (but the game id is set in this function)-> IMPOSSIBLE
    // solution: we always call the serverController that recognise the client and calls the associated came controller
    private void addPlayerToGame (Player player) throws RemoteException, NotBoundException, ArrayIndexOutOfBoundsException{
        //gameController.addPlayer(player); //that's the code in RMIServer but I don't know how to make it work
    }

    //internal use?
    private void startGame() throws RemoteException, NotBoundException, IllegalStateException{}

    //how do we select the card without the view?
    private void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException{}

    //how do we select the card without the view?
    private void playBaseCard (String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException{}

    //how do we select the card without the view?
    private void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException{}

    //how do we select the card without the view?
    private void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException, NotBoundException{}

    private void choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException, NotBoundException{
        gameController.choosePawnColor(chooser, selectedColor);
    }

    //how the client know the receivers?
    private void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException, NotBoundException{}

    //internal use?
    private void nextRound() throws RemoteException, NotBoundException{}

    //internal use?
    private void endGame() throws RemoteException, NotBoundException{}

    //we have the nickname saved in personalPlayer but we don't have the game controller used in thi method (as suggested
    // we should call the server controller which has tha Map with all game controllers saved and can call it by himself)
    private void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException{}

    //methods to be implemented to have a class tha implements Observer: (invece in RMI avremo una classe intermedia la WrappedObserver che implementerà Observer)
    public void updateBoard(Board board){

    }
    public void updateResourceDeck(){}
    public void updateGoldDeck(){}
    public void updatePlayerDeck(Player player){}
    public void updateResourceCard1(){}
    public void updateResourceCard2(){}
    public void updateGoldCard2(){}
    public void updateGoldCard1(){}
    public void updateChat(int chatID){}
    public void updatePawns(){}
    public void updateNickname(){}
    public void updateRound(){}
}

