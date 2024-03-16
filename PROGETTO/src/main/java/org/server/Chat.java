package org.server;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Player> users;
    private List<Message> messages;
    private int id;

    public Chat(List<Player> users, int id) {
        this.users = users;
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public void sendMessage (Message mess) {
        this.messages.add(mess);
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public List<Player> getUsers() {
        return this.users;
    }

    public int getId() {
        return this.id;
    }
}
