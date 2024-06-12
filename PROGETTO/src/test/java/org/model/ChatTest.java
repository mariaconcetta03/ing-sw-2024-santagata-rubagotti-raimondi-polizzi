package org.model;

import CODEX.org.model.Chat;
import CODEX.org.model.ChatMessage;
import CODEX.org.model.Player;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChatTest extends TestCase {
    List<String> users=new ArrayList<>();
    @Test
    public void testSendMessage() throws RemoteException {
        String s1="Pippo";
        String s2= "Pluto";
        String s3="Paperino";
        users.add(s1);
        users.add(s2);
        users.add(s3);
        Chat c1=new Chat(users, 0);
        List<String> receiver= new ArrayList<>();
        receiver.add(s2);
        receiver.add(s3);
        Timestamp t1= new Timestamp(System.currentTimeMillis());
        ChatMessage m=new ChatMessage("Ciao", s1, receiver, t1);
        c1.sendMessage(m);
        System.out.println("Chat "+c1.getId()+", users "+c1.getUsers().get(0)+", "+c1.getUsers().get(1)+": "+c1.getMessages().get(0).getMessage()+" sent by "+c1.getMessages().get(0).getSender()+" at "+c1.getMessages().get(0).getTimestamp());
    }
    @Test
    public void testMessagesReceivedByPlayer() throws RemoteException {
        String s1="Pippo";
        String s2= "Pluto";
        String s3="Paperino";
        users.add(s1);
        users.add(s2);
        users.add(s3);
        Chat c1=new Chat(users, 0);
        Timestamp t1= new Timestamp(System.currentTimeMillis());
        List<String> receiver= new ArrayList<>();
        receiver.add(s2);
        receiver.add(s3);
        for(int i=0;i<200; i++) {
            ChatMessage m = new ChatMessage("Ciao sono il messaggio "+i, s1, receiver, t1);
            c1.sendMessage(m);
        }
        for(int i=0; i< c1.messagesReceivedByPlayer(s2).size(); i++){
            System.out.println(c1.messagesReceivedByPlayer(s2).get(i).getMessage());
        }
        assertThrows(IndexOutOfBoundsException.class, ()->{System.out.println(c1.messagesReceivedByPlayer(s1).get(0).getMessage());}); //no message here!
    }
}