package CODEX.utils;

import java.security.Timestamp;
import java.util.ArrayList;

public class ClientMessage {
    private final String message;
    private final String sender;
    private final ArrayList<String> receivers = new ArrayList<>();
    private final Timestamp timestamp;
    public ClientMessage (String message, String sender, ArrayList<String> receivers, Timestamp timestamp ) {

        this.message = message;
        this.sender = sender;
        this.receivers.addAll(receivers);
        this.timestamp = timestamp;
    }
}
