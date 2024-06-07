package CODEX.org.model;
import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.updateChatEvent;
import CODEX.utils.executableMessages.events.updateChatMessageEvent;
//import CODEX.utils.executableMessages.events.updateChatEvent;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chat instance of the Game
 */


public class Chat extends Observable implements Serializable {
    private List<String> usersNickname; //these are the players which can communicate in the chat
    private List<ChatMessage> messages; // all the messages which have been sent in this chat
    private final int id; // every chat has a different ID. That means that there is a way to select the players we're chatting with?



    /**
     * Class constructor
     * @param usersNickname is the List containing all the players taking part to the chat instance
     * @param id is the chat id
     */
    public Chat(List<String> usersNickname, int id) {
        this.usersNickname = usersNickname;
        this.id = id;
        this.messages = new ArrayList<>();
    }



    /**
     * This method add a new message to the chat ("sending" it)
     * @param mess It's the message sent by the player
     */
    public void sendMessage (ChatMessage mess) throws RemoteException {
        if(messages.isEmpty()){
            notifyObservers(new updateChatEvent(this));
        }
        this.messages.add(mess);
        //notifyObservers(new updateChatMessageEvent(mess)); //inviamo il solo nuovo messaggio, se la chat diventa enorme è uno
        //spreco assurdo inviarla tutta tramite la rete ogni volta
    }



    /**
     * This method returns all the messages received by a player
     * @param playerNickname is the player we're interested in
     * @return an ArrayList containing all the messages received from p
     */
    public List<ChatMessage> messagesReceivedByPlayer(String playerNickname){ //@TODO non mi serve
        List<ChatMessage> messageReceived= new ArrayList<>();
        for(ChatMessage m : this.messages){
            if(m.getReceiver().contains(playerNickname)){
                messageReceived.add(m);
            }
        }
        return messageReceived;
    }



    /**
     * Getter method
     * @return the entire list of messages
     */
    public List<ChatMessage> getMessages() {
        return this.messages;
    }



    /**
     * Getter method
     * @return the list of players' nicknames that are participating into  the Chat
     */
    public List<String> getUsers() {
        return this.usersNickname;
    }



    /**
     * Getter method
     * @return the chat id
     */
    public int getId() {
        return this.id;
    }
}
