package utils;

import java.security.Timestamp;
import java.util.ArrayList;

public class ClientChat {
    private final int id;
    private final ArrayList<ClientMessage> chat = new ArrayList<>();
    private final ArrayList<String> playerList = new ArrayList<>();
    public ClientChat(int id, ArrayList<String> playerList) {
        this.id = id;
        this.playerList.addAll(playerList);
    }
    public void addPlayerToChat(String player) {
        this.playerList.add(player);
    }
    public void addMessage(String message, String sender, ArrayList<String> receivers, Timestamp timestamp) {
        this.chat.add(new ClientMessage(message, sender, receivers, timestamp));
    }

}
