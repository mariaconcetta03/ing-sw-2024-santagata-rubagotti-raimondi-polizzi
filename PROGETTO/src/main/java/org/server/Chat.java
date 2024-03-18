package org.server;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Player> users; //these are the players which can communicate in the chat
    private List<Message> messages; // all the messages which have been sent in this chat
    private int id; // every chat has a different ID

    public Chat(List<Player> users, int id) {
        this.users = users;
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public void sendMessage (Message mess) { // adds a message to the message list "messages"
        this.messages.add(mess);
    }

    public List<Message> getMessages() { // returns the entire list of messages
        return this.messages;
    }

    public List<Player> getUsers() {
        return this.users;
    }

    public int getId() {
        return this.id;
    }
}
