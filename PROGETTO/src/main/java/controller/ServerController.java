package controller;

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

    public void startLobby(Player creator){
        //Creating the specific GameController
        GameController gameController= new GameController();
        //adding the first player
        gameController.addPlayer(creator);
    }

    /**
     * This method is used to add a single player to a lobby
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws IllegalArgumentException if the lobby does not exist or it is already started
     */
    public void addPlayerToLobby(Player player, int gameId) throws IllegalArgumentException {
        if (!allGameControllers.containsKey(gameId)) { //if the game doesn't exist
            throw new IllegalArgumentException("This game does not exist!");
        } else if(!allGameControllers.get(gameId).getGame().getState().equals(Game.GameState.WAITING_FOR_START)) {//if the game is already started
            throw new IllegalArgumentException(("This game is already started!"));
        }else{
            GameController temp = allGameControllers.get(gameId);
            try {
                temp.addPlayer(player);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Choose another lobby or create a new one!");
            }
        }
    }

    public static int getFirstAvailableId(){
        firstAvailableId++;
        return firstAvailableId;
    }

    public boolean isNicknameAvailable(String nickname){
        Player temp=null;
        for (Player player : allPlayers) {
            if (player.getNickname().equals(nickname)) {
                return false;
            }
        }
        return true;
    }
}
