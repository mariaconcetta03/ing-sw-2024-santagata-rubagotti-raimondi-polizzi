package controller;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import org.model.Game;
import org.model.Player;
import utils.ClientChat;
import utils.Event;

import java.util.*;
public class ServerController {
    private static int firstAvailableId = -1;
    private static Map<Integer, GameController> allGameControllers;
    private static List<Player> allPlayers;

    /**
     * Class constructor
     */
    public ServerController() {
        allPlayers=new ArrayList();
        allGameControllers=new HashMap<>();
    }

    /**
     * This method creates a new lobby adding the first player to it.
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @return gameController is the game controller of this match, we need to pass this to the client
     */
    public GameController startLobby(Player creator, int numOfPlayers) throws IllegalArgumentException{
        //Creating the specific GameController
        GameController gameController= new GameController();
            gameController.setNumberOfPlayers(numOfPlayers);
        //inserting the new gameController in the Map
        int tempId=getFirstAvailableId();
        allGameControllers.put(tempId,gameController);
        gameController.setId(tempId);
        //adding the first player
        gameController.addPlayer(creator);
        //we will have to check in the VIEW if the numOfPlayers is between 2 and 4
        return gameController;
        }



    /**
     * This method is used to add a single player to an already created lobby
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws GameNotExistsException if the Game the player wants to join doesn't exist
     * @throws GameAlreadyStartedException if the Game is not in WAITING_FOR_START condition
     * @throws FullLobbyException if the lobby has already reached the maximum number of players
     * @return allGameControllers.get(gameId) is the game controller of this match, we need to pass this to the client
     */
    public GameController addPlayerToLobby(Player player, int gameId) throws GameNotExistsException, GameAlreadyStartedException, FullLobbyException {
        if (!allGameControllers.containsKey(gameId)) { //if the game doesn't exist
            throw new GameNotExistsException("The game doesn't exist");
        } else if((allGameControllers.get(gameId).getGame()!=null)&&(!allGameControllers.get(gameId).getGame().getState().equals(Game.GameState.WAITING_FOR_START))) {//if the game is already started
            throw new GameAlreadyStartedException("Game already started!");
        }else {
            GameController temp = allGameControllers.get(gameId);
            try {
                temp.addPlayer(player);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new FullLobbyException("Choose another lobby or create a new one!");
            }
        }
        return allGameControllers.get(gameId);
    }


    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     * @throws NicknameAlreadyTakenException if the nickname is already in use
     */
    public void chooseNickname(Player chooser, String nickname) throws NicknameAlreadyTakenException {
        if(isNicknameAvailable(nickname)){
            chooser.setNickname(nickname);
        }else{
            throw new NicknameAlreadyTakenException("The selected nickname is not available");
        }
    }


    /**
     * Getter method
     * @return the first available id
     * INTERNAL USE METHOD
     */
    public static int getFirstAvailableId() {
        firstAvailableId++;
        return firstAvailableId;
    }


    /**
     * This method tells if a nickname is available to be selected by the players
     * @param nickname is the String we want to check
     * @return true if it is available, false instead
     * INTERNAL USE METHOD
     */
    public boolean isNicknameAvailable(String nickname){
        Player temp=null;
        for (Player player : allPlayers) {
            if ((player.getNickname()!=null)&&(player.getNickname().equals(nickname))) {
                return false;
            }
        }
        return true;
    }


    /**
     * This method returns the player given his nickname
     * @param Nickname is the nickname we are using to search for a player
     * @return the player if found, null otherwise
     */
    public static Player getPlayerByNickname(String Nickname){
        for(Player player : allPlayers){
            if(player.getNickname().equals(Nickname)){
                return player;
            }
        }
        return null;
    }


    /**
     * Getter method
     * @return allGameControllers which is a map that associate the controller to a specific game
     */
    public static Map<Integer, GameController> getAllGameControllers() {
        return allGameControllers;
    }


    /**
     * Getter method
     * @return allPlayers of the game
     */
    public List<Player> getAllPlayers() {
        return allPlayers;
    }
}
