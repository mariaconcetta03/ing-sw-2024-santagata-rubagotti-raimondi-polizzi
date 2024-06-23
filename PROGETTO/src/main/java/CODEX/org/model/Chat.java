package CODEX.org.model;

import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.UpdateChatEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chat instance of the Game
 */
public class Chat extends Observable implements Serializable {
    private List<String> usersNickname;
    private List<ChatMessage> messages;
    private final int id;


    /**
     * Class constructor
     *
     * @param usersNickname is the List containing all the players taking part to the chat instance
     * @param id            is the chat id
     */
    public Chat(List<String> usersNickname, int id) {
        this.usersNickname = usersNickname;
        this.id = id;
        this.messages = new ArrayList<>();
    }


    /**
     * This method add a new message to the chat ("sending" it)
     *
     * @param mess It's the message sent by the player
     */
    public void sendMessage(ChatMessage mess) {
        if (messages.isEmpty()) {
            notifyObservers(new UpdateChatEvent(this));
        }
        this.messages.add(mess);
    }


    /**
     * This method returns all the messages received by a player
     *
     * @param playerNickname is the player we're interested in
     * @return an ArrayList containing all the messages received from p
     */
    public List<ChatMessage> messagesReceivedByPlayer(String playerNickname) {
        List<ChatMessage> messageReceived = new ArrayList<>();
        for (ChatMessage m : this.messages) {
            if (m.getReceiver().contains(playerNickname)) {
                messageReceived.add(m);
            }
        }
        return messageReceived;
    }


    /**
     * Getter method
     *
     * @return the entire list of messages
     */
    public List<ChatMessage> getMessages() {
        return this.messages;
    }


    /**
     * Getter method
     *
     * @return the list of players' nicknames that are participating into  the Chat
     */
    public List<String> getUsers() {
        return this.usersNickname;
    }


    /**
     * Getter method
     *
     * @return the chat id
     */
    public int getId() {
        return this.id;
    }
}
