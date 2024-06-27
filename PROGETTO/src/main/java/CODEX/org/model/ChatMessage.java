package CODEX.org.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class represents a message in a Chat
 */


public class ChatMessage implements Serializable {
    private String message;
    private String senderNickname;
    private List<String> receiversNicknames; // it can be a single receiver, or the whole group of players
    private Timestamp timestamp;


    /**
     * Class constructor
     *
     * @param message            is a text message
     * @param senderNickname     is who is sending a message
     * @param receiversNicknames is who is receiving a message
     * @param timestamp          date, hour
     */
    public ChatMessage(String message, String senderNickname, List<String> receiversNicknames, Timestamp timestamp) {
        this.message = message;
        this.senderNickname = senderNickname;
        this.receiversNicknames = receiversNicknames;
        this.timestamp = timestamp;
    }


    /**
     * Getter method
     *
     * @return the message String
     */
    public String getMessage() { // returns the text of the message as a String object
        return this.message;
    }


    /**
     * Getter method
     *
     * @return the player that sent this message
     */
    public String getSender() { // returns the player who sent the message
        return this.senderNickname;
    }


    /**
     * Getter method
     *
     * @return a list containing the receivers of the message
     */
    public List<String> getReceiver() { // returns the list of the receivers: it can be a single player or the whole group
        return this.receiversNicknames;
    }


    /**
     * Getter method
     *
     * @return the timestamp of the message
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

}
