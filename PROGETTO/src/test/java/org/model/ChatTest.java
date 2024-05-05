package org.model;

import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChatTest extends TestCase {
    List<Player> users=new ArrayList<>();
    public void testSendMessage() {

        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p2.setNickname("Paperino");
        users.add(p1);
        users.add(p2);
        users.add(p3);
        Chat c1=new Chat(users, 0);
        List<Player> receiver= new ArrayList<>();
        receiver.add(p2);
        receiver.add(p3);
        Timestamp t1= new Timestamp(System.currentTimeMillis());
        ChatMessage m=new ChatMessage("Ciao", p1, receiver, t1);
        c1.sendMessage(m);
    }

    public void testMessagesReceivedByPlayer() {
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p2.setNickname("Paperino");
        users.add(p1);
        users.add(p2);
        users.add(p3);
        Chat c1=new Chat(users, 0);
        Timestamp t1= new Timestamp(System.currentTimeMillis());
        List<Player> receiver= new ArrayList<>();
        receiver.add(p2);
        receiver.add(p3);
        for(int i=0;i<200; i++) {
            ChatMessage m = new ChatMessage("Ciao sono il messaggio "+i, p1, receiver, t1);
            c1.sendMessage(m);
        }
        for(int i=0; i< c1.messagesReceivedByPlayer(p2).size(); i++){
            System.out.println(c1.messagesReceivedByPlayer(p2).get(i).getMessage());
        }
        assertThrows(IndexOutOfBoundsException.class, ()->{System.out.println(c1.messagesReceivedByPlayer(p1).get(0).getMessage());}); //no message here!
    }
}