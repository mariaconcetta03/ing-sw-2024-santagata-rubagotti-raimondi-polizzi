package CODEX.utils;

import java.security.Timestamp;
import java.util.ArrayList;


/**
 * This class represents the chat itself
 */
public class ClientChat {
    private final int id;
    private final ArrayList<ClientMessage> chat = new ArrayList<>();
    private final ArrayList<String> playerList = new ArrayList<>();



    /**
     * Class constructor
     * @param id of the chat
     * @param playerList represents the player who are joining the chat
     */
    public ClientChat(int id, ArrayList<String> playerList) {
        this.id = id;
        this.playerList.addAll(playerList);
    }


    /**
     * This method adds the player to a chat
     * @param player who wants to join the chat
     */
    public void addPlayerToChat(String player) {
        this.playerList.add(player);
    }


    /**
     * This method send a message
     * @param message sent by the player
     * @param sender who is sending the message
     * @param receivers represents the ones who are receiving it
     * @param timestamp represents when the message arrives
     */
    public void addMessage(String message, String sender, ArrayList<String> receivers, Timestamp timestamp) {
        this.chat.add(new ClientMessage(message, sender, receivers, timestamp));
    }
}
