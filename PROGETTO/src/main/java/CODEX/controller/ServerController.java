package CODEX.controller;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.org.model.Game;
import CODEX.org.model.Player;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;


/**
 * This class manages all the GameControllers and administrates nickname' selection.
 */
public class ServerController implements Serializable {
    private static int firstAvailableId = -1;
    private Map<Integer, GameController> allGameControllers;
    private List<String> allNicknames;

    /**
     * Class constructor
     */
    public ServerController() {
        allNicknames = new ArrayList<>();
        allGameControllers = new HashMap<>();
    }


    /**
     * This method creates a new lobby adding the first player to it.
     *
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers    is the number of player the creator decided can play in the lobby
     * @return gameController is the game controller of this match, we need to pass this to the client
     * @throws IllegalArgumentException when the number of players is wrong
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public synchronized GameController startLobby(String creatorNickname, int numOfPlayers) throws IllegalArgumentException, RemoteException {
        // Creating the specific GameController
        GameController gameController = new GameController();
        gameController.setServerController(this);
        gameController.setNumberOfPlayers(numOfPlayers);
        int tempId = getFirstAvailableId();
        allGameControllers.put(tempId, gameController);
        gameController.setId(tempId);
        Player creator = new Player();
        creator.setNickname(creatorNickname);
        gameController.addPlayer(creator);
        return gameController;
    }


    /**
     * This method is used to add a single player to an already created lobby
     *
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId         is the lobby the player wants to join
     * @return allGameControllers.get(gameId) is the game controller of this match, we need to pass this to the client
     * @throws GameNotExistsException      if the Game the player wants to join doesn't exist
     * @throws GameAlreadyStartedException if the Game is not in WAITING_FOR_START condition
     * @throws FullLobbyException          if the lobby has already reached the maximum number of players
     */
    public synchronized GameController addPlayerToLobby(String playerNickname, int gameId) throws GameNotExistsException, GameAlreadyStartedException, FullLobbyException {
        if (!allGameControllers.containsKey(gameId)) {
            throw new GameNotExistsException("The game doesn't exist");
        } else if ((allGameControllers.get(gameId).getGame() != null) && (!allGameControllers.get(gameId).getGame().getState().equals(Game.GameState.WAITING_FOR_START))) {
            throw new GameAlreadyStartedException("Game already started!");
        } else {
            GameController temp = allGameControllers.get(gameId);
            Player player = new Player();
            player.setNickname(playerNickname);
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
     *
     * @param nickname is the String he wants to put as his nickname
     * @throws NicknameAlreadyTakenException if the nickname is already in use
     */
    public synchronized void chooseNickname(String nickname) throws NicknameAlreadyTakenException {
        if (isNicknameAvailable(nickname)) {
            allNicknames.add(nickname);
        } else {
            throw new NicknameAlreadyTakenException("The selected nickname is not available");
        }
    }


    /**
     * This method tells if a nickname is available to be selected by the players
     *
     * @param nickname is the String we want to check
     * @return true if it is available, false instead
     * INTERNAL USE METHOD
     */
    public boolean isNicknameAvailable(String nickname) {
        for (String nick : allNicknames) {
            if (nick.equals(nickname)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Getter method
     *
     * @return the first available id
     * INTERNAL USE METHOD
     */
    public static int getFirstAvailableId() {
        firstAvailableId++;
        return firstAvailableId;
    }


    /**
     * Getter method
     *
     * @return allGameControllers which is a map that associate the controller to a specific game
     */
    public Map<Integer, GameController> getAllGameControllers() {
        return allGameControllers;
    }


    /**
     * Getter method
     *
     * @return available ids of the different game controllers
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public Set<Integer> getAvailableGameControllersId() throws RemoteException {
        Set<Integer> tmp = new HashSet<>();
        for (Integer i : allGameControllers.keySet()) {
            if (allGameControllers.get(i).getGame() == null) {
                tmp.add(i);
            }
        }
        return tmp;
    }


    /**
     * Getter method
     *
     * @return allNicknames of the game
     */
    public List<String> getAllNicknames() {
        return allNicknames;
    }

}
