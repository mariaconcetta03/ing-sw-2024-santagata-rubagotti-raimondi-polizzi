package distributed.RMI;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import distributed.ClientGeneralInterface;
import org.model.*;
import utils.Event;
import utils.Observable;
import view.TUI.InterfaceTUI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


// ----------------------------------- H O W   I T   W O R K S ----------------------------------------
// RMI CLIENT CALLS THE REMOTE METHODS THROUGH THE RMISERVER INTERFACE (WHICH RMI CLIENT HAS INSIDE)
// THE RMI SERVER GOES TO MODIFY THE MODEL (UPON CLIENT REQUEST) THROUGH THE CONTROLLER.
// TO UNDERSTAND WHETHER AN EVENT WAS SUCCESSFUL OR NOT, YOU NEED TO CONSULT THE ATTRIBUTE
// "lastEvent" PRESENT IN THE GAME CLASS.
// FOR THE METHODS OF THE GAMECONTROLLER, THE CLIENT INVOKES THEM DIRECTLY BY PASSING THROUGH THE
// GAMECONTROLLER (WITHOUT INVOKING THE SERVER). THE GAME CONTROLLER IS PASSED TO THE CLIENT BY THE
// METHODS "startLobby" AND "addPlayerToLobby"
// ----------------------------------------------------------------------------------------------------



public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface {
    private ServerRMIInterface SRMIInterface; //following the slides' instructions

    private GameController gameController = null;
    // given to the client when the game is started (lobby created or player joined to a lobby))

    private int selectedView; // 1==TUI, 2==GUI
    private InterfaceTUI tuiView;
    //private GUI; :O INTERFACCIA PERò...
    private Board personalBoard;
    private PlayableCard playerDeck[];
    private Player player;
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;


    /**
     * Class constructor
     * @throws RemoteException
     */
    protected RMIClient() throws RemoteException {
    }




    /**
     * Main method
     * This method calls the method "startConnectionWithServer()". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args from CLI
     */
    public static void main( String[] args ) {

        try {
            RMIClient rmiC = new RMIClient();
            GameController gc;
            List <String> rc = new ArrayList<String>();
            rc.add("Minnie");

            // OK
            rmiC.createLobby("Pippo", 4); // OK
            rmiC.addPlayerToLobby("Papero", rmiC.gameController.getId());
            rmiC.addPlayerToLobby("Minnie", rmiC.gameController.getId());
            rmiC.addPlayerToLobby("Topolino", rmiC.gameController.getId());
            rmiC.sendMessage("Pippo", rc, "Hello!");

            // OK, BUT WE NEED A TUI OR A GUI TO COMPLETE THE WHOLE TEST. NOW IT ONLY JUMPS INTO THE METHOD "updateChat"
            rmiC.updateChat(rmiC.gameController.getGame().getChats().get(0));

            // KO
            // rmiC.createLobby("Pluto", 9); // KO [java.lang.IllegalArgumentException: Wrong number of players!]

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * Settings class
     * It is about port and ip address of the server which the client needs to communicate with
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 standard port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }






    // ---- M E T H O D S   F R O M   C L I E N T   T O   S E R V E R ----

    /**
     * This method gets the SRMIInterface from the registryServer
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException {
        Registry registryServer = null;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT); // getting the registry
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
    }




    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException { //exceptions added automatically
        SRMIInterfaceFromRegistry();
        this.gameController = this.SRMIInterface.createLobby(creatorNickname, numOfPlayers);
    }



    /**
     * This method is used to add a single player to an already created lobby
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws RemoteException
     * @throws NotBoundException
     * @throws GameAlreadyStartedException
     * @throws FullLobbyException
     * @throws GameNotExistsException
     */
    public void addPlayerToLobby (String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        SRMIInterfaceFromRegistry();
        this.gameController = this.SRMIInterface.addPlayerToLobby(playerNickname, gameId);
    }




    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void chooseNickname (String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.chooseNickname(nickname);
    }




    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
        this.gameController.playCard(nickname, selectedCard, position, orientation);
    }




    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     * @param nickname is the nickname of the Player that wants to play a card
     * @param baseCard is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        this.gameController.playBaseCard(nickname, baseCard, orientation);
    }




    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        this.gameController.drawCard(nickname, selectedCard);
    }




    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        this.gameController.chooseObjectiveCard(chooserNickname, selectedCard);
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException {
        this.gameController.choosePawnColor(chooserNickname, selectedColor);
    }



    /**
     * This method sends a message from the sender to the receiver
     * @param senderNickname is the nickname of the player sending the message
     * @param receiversNicknames is the list of nicknames of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void sendMessage(String senderNickname, List<String> receiversNicknames, String message) throws RemoteException, NotBoundException {
        this.gameController.sendMessage(senderNickname, receiversNicknames, message);
    }




    /**
     * This method lets a player end the game (volontary action or involontary action - connection loss)
     * @param nickname of the player who is gonna leave the game
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IllegalArgumentException
     */
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException{
        this.gameController.leaveGame(nickname);
    }





    // ------- M E T H O D S   F O R   U P D A T E -------
    // ---- F R O M   S E R V E R   T O   C L I E N T ----

    /**
     * This is an update method
     * @param board the new board we want to update
     */
    public void updateBoard (Board board) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateBoard(board)
        } else if (selectedView == 2) {
            //guiView.updateBoard(board)
        }
    }



    /**
     * This is an update method
     * @param resourceDeck the new deck we want to update
     */
    public void updateResourceDeck (PlayableDeck resourceDeck) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateResourceDeck (resourceDeck)
        } else if (selectedView == 2) {
            //guiView.updateBoard(board)
        }
    }



    /**
     * This is an update method
     * @param goldDeck the new deck we want to update
     */
    public void updateGoldDeck (PlayableDeck goldDeck) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateGoldDeck(goldDeck)
        } else if (selectedView == 2) {
            //guiView.updateGoldDeck(goldDeck)
        }
    }



    /**
     * This is an update method
     * @param player the player which deck is updated
     * @param playerDeck the new deck we want to update
     */
    public void updatePlayerDeck (Player player, PlayableCard[] playerDeck) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updatePlayerDeck(player, playerDeck)
        } else if (selectedView == 2) {
            //guiView.updatePlayerDeck(player, playerDeck)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    public void updateResourceCard1(PlayableCard card) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateResourceCard1(card)
        } else if (selectedView == 2) {
            //guiView.updateResourceCard1(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
   public void updateResourceCard2(PlayableCard card) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateResourceCard2(card)
        } else if (selectedView == 2) {
            //guiView.updateResourceCard2(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    public void updateGoldCard1(PlayableCard card) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateGoldCard1(card)
        } else if (selectedView == 2) {
            //guiView.updateGoldCard1(card)
        }
    }



    /**
     * This is an update method
     * @param card which needs to be updated
     */
    public void updateGoldCard2(PlayableCard card) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateGoldCard2(card)
        } else if (selectedView == 2) {
            //guiView.updateGoldCard2(card)
        }
    }



    /**
     * This is an update method
     * @param chat which needs to be updated
     */
    public void updateChat(Chat chat) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateChat(chat)
        } else if (selectedView == 2) {
            //guiView.updateChat(chat)
        }
    }



    /**
     * This is an update method
     * @param player which selected a new pawn color
     * @param pawn selected color
     */
    public void updatePawns(Player player, Pawn pawn) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updatePawns(player, pawn)
        } else if (selectedView == 2) {
            //guiView.updatePawns(player, pawn)
        }
    }



    /**
     * This is an update method
     * @param player which selected a new nickname
     * @param nickname chosen nickname string
     */
    public void updateNickname(Player player, String nickname) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateNickname(player, nickname)
        } else if (selectedView == 2) {
            //guiView.updateNickname(player, nickname)
        }
    }



    /**
     * This is an update method
     * @param newCurrentPlayer which needs to play now
     */
    public void updateRound(Player newCurrentPlayer) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateRound(newCurrentPlayer)
        } else if (selectedView == 2) {
            //guiView.updateRound(newCurrentPlayer)
        }
    }



    /**
     * This is an update method
     * @param game which needs to update his state (WAITING_FOR_START -> STARTED -> ENDING -> ENDED)
     */
    public void updateGameState(Game game) throws RemoteException {
        if (selectedView == 1) {
            //tuiView.updateGameState(game)
        } else if (selectedView == 2) {
            //guiView.updateGameState(game)
        }
    }
}









// OLD COMMENTS

//per creare il riferimento in WrappedObserver tramite RMIServer che potrebbe implementare un Observable come da pdf
    /*
    public class RemoteObserverImpl implements RemoteObserver {
        public void remoteUpdate(Object observable, Event updateMsg) {
            System.out.println("Got message: " + updateMsg);
        }
        public static void main(String[] args) throws Exception {
            Registry registry = LocateRegistry.getRegistry();
            RMIObservableService remoteService = (RMIObservableService)    //remoteService è il nostro server
                    registry.lookup("Observable");
            RemoteObserver client = new RemoteObserverImpl();
            RemoteObserver clientStub = (RemoteObserver)
                    UnicastRemoteObject.exportObject(client, 3949);
            remoteService.addObserver(clientStub);    //il server è un Observable e quindi ha la lista di listeners (che sono i Wrapped)
        }
    }

    //il wrapped inizia ad esistere solo nel momento in cui si crea un istanza di Game e quindi si ha bisogno di notificare i players
    //quindi non bisogna fare addObserver in questo momento perchè ancora quel client non è diventato un player (e quindi non ha ancora un Game)
    // diventerà un addToList (per memorizzare i client in attesa)
    // quando parte tutto abbiamo un Game i wrapped

     */

//    public void update(Event e, Observable obs) {
//        switch (e) {
//            case UPDATED_BOARD: // LIST: PLAYER PLAYER - BOARD NEWBOARD
////                for(int i = 0; i < get
////                if((gameController.getGame().getPlayers().get(i)).equals.obs))
////               gameController.getGame()
//
//                for (int i = 0; i<gameController.getGamePlayers().size(); i++) {
//                    if ((obs.getPlayer()).equals(gameController.getGamePlayers().get(i).getBoard())) {
//                        gameController.getGamePlayers().get(i).setBoard(obs);
//                    }
//                }
//                break;
//
//            case UPDATED_RESOURCE_DECK:
//                break;
//
//            case UPDATED_GOLD_DECK:
//                break;
//
//            case UPDATED_PLAYER_DECK:
//                break;
//
//            case UPDATED_RESOURCE_CARD_1:
//                break;
//
//            case UPDATED_RESOURCE_CARD_2:
//                break;
//
//            case UPDATED_GOLD_CARD_1:
//                break;
//
//            case UPDATED_GOLD_CARD_2:
//                break;
//
//            case UPDATED_CHAT:
//                break;
//
//            case UPDATED_PAWNS:
//                break;
//
//            case UPDATED_NICKNAME:
//                break;
//
//            case UPDATED_ROUND:
//                break;
//
//            default:
//                break;
//
//        }
//    }