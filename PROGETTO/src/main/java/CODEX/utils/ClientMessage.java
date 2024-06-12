package CODEX.utils;

import java.security.Timestamp;
import java.util.ArrayList;

/**
 * This class represents the message sent by a client
 */
public class ClientMessage {
    private final String message;
    private final String sender;
    private final ArrayList<String> receivers = new ArrayList<>();
    private final Timestamp timestamp;


    /**
     * Class constructor
     * @param message sent by the player
     * @param sender is the player
     * @param receivers are the other ones
     * @param timestamp is the time when the message is sent
     */
    public ClientMessage (String message, String sender, ArrayList<String> receivers, Timestamp timestamp ) {
        this.message = message;
        this.sender = sender;
        this.receivers.addAll(receivers);
        this.timestamp = timestamp;
    }
}
