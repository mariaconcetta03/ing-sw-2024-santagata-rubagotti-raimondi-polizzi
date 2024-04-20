package utils;

import java.security.Timestamp;
import java.util.ArrayList;

public class ClientChat { // one for each chatroom
    private final int id;
    private final ArrayList<ClientMessage> chat = new ArrayList<>();
    // serve per comunicare con l'esterno e per aggiornare il model (chiama il controller che aggiornetà il model)
    private final ArrayList<String> playerList = new ArrayList<>(); // to be deleted bcz already saved into the model
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
