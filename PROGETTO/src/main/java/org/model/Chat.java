package org.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chat instance of the Game
 */


public class Chat implements Serializable { //implements Serializable because has to use the net
    private List<Player> users; //these are the players which can communicate in the chat
    private List<Message> messages; // all the messages which have been sent in this chat
    private final int id; // every chat has a different ID. That means that there is a way to select the players we're chatting with?



    /**
     * Class constructor
     * @param users is the List containing all the players taking part to the chat instance
     * @param id is the chat id
     */
    public Chat(List<Player> users, int id) {
        this.users = users;
        this.id = id;
        this.messages = new ArrayList<>();
    }



    /**
     * This method add a new message to the chat ("sending" it)
     * @param mess It's the message sent by the player
     */
    public void sendMessage (Message mess) {
        this.messages.add(mess);
    }



    /**
     * This method returns all the messages received by a player
     * @param p is the player we're interested in
     * @return an ArrayList containing all the messages received from p
     */
    public List<Message> messagesReceivedByPlayer(Player p){
        List<Message> messageReceived= new ArrayList<>();
        for(Message m : this.messages){
            if(m.getReceiver().contains(p)){
                messageReceived.add(m);
            }
        }
        return messageReceived;
    }



    /**
     * Getter method
     * @return the entire list of messages
     */
    public List<Message> getMessages() {
        return this.messages;
    }



    /**
     * Getter method
     * @return the list of players using the Chat
     */
    public List<Player> getUsers() {
        return this.users;
    }



    /**
     * Getter method
     * @return the chat id
     */
    public int getId() {
        return this.id;
    }
}
