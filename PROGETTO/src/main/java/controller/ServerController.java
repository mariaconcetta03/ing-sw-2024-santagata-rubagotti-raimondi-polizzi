package controller;

import Exceptions.NicknameAlreadyTakenException;
import org.model.Game;
import org.model.Player;

import java.util.*;
public class ServerController {
    private static int firstAvailableId = -1;
    private Map<Integer, GameController> allGameControllers;
    private List<Player> allPlayers;

    public ServerController() {
        allPlayers=new ArrayList();
        allGameControllers=new HashMap<>();
    }

    /**
     * This method creates a new lobby
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     */
    public void startLobby(Player creator, int numOfPlayers){
        //Creating the specific GameController
        GameController gameController= new GameController();
        //adding the first player
        gameController.addPlayer(creator);
        //we will have to check in the VIEW if the numOfPlayers is between 2 and 4
        gameController.setNumberOfPlayers(numOfPlayers);
    }

    /**
     * This method is used to add a single player to an already created lobby
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @return whether it was possible or not to add the player
     */
    public boolean addPlayerToLobby(Player player, int gameId) {
        if (!allGameControllers.containsKey(gameId)) { //if the game doesn't exist
            return false;
        } else if(!allGameControllers.get(gameId).getGame().getState().equals(Game.GameState.WAITING_FOR_START)) {//if the game is already started
            return false;
        }else{
            GameController temp = allGameControllers.get(gameId);
            try {
                temp.addPlayer(player);
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Choose another lobby or create a new one!");
                return false;
            }
        }
    }

    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * I can't throw exceptions through internet connection
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     */
    public void chooseNickname(Player chooser, String nickname) throws NicknameAlreadyTakenException {
        if(isNicknameAvailable(nickname)){
            chooser.setNickname(nickname);
        }else{
            throw new NicknameAlreadyTakenException("The selected nickname is not available");
        }
    }

    /**
     * This method returns the first available id used to identify games
     * @return the first available id
     */
    public static int getFirstAvailableId(){
        firstAvailableId++;
        return firstAvailableId;
    }

    /**
     * This method tells if a nickname is available to be selected by the players
     * @param nickname is the String we want to check
     * @return if it is or not available
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

    public Map<Integer, GameController> getAllGameControllers() {
        return allGameControllers;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }
}
