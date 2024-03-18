package org.server;
import java.sql.Timestamp;
import java.util.List;


public class Message {
    private String message; // in the string it's represented the text of the message
    private Player sender; // who sent the message
    private List<Player> receiver; // it can be a single receiver, or the whole group of players
    private Timestamp timestamp; // it also works as an ID for the message, combined with his sender
                                 // a sender can't send 2 messages at the same time!

    public Message (String message, Player sender, List<Player> receiver, Timestamp timestamp) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public String getMessage() { // returns the text of the message as a String object
        return this.message;
    }

    public Player getSender() { // returns the player who sent the message
        return this.sender;
    }

    public List<Player> getReceiver() { // returns the list of the receivers: it can be a single player or the whole group
        return this.receiver;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}
