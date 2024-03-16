package org.server;
import java.util.*;

public class Codex {
    private Codex(){}
    private static final Codex instance= new Codex();
    public List<Player> allPlayers;
    public List<Game> allGames;

    //is the cards Map necessary? maybe use Json but didn't understand a lot
    //I created a card LIST because the id attribute is already part of the Card object so
    // we don't need to map them
    public List<Card> cards ;
    //Scanner that read everything the user inserts by command line
    Scanner sc= new Scanner(System.in);
    public static Codex getIstance(){
        return instance;
    }
    public void startLobby(){

    }
    public void endLobby(){

    }
    public void connectPlayer(){


    }
    public void disconnectPlayer(){

    }
    public boolean isNicknameAvailable(String nickname){
        Player temp=null;
        for(int i=0; i<allPlayers.size();i++){
            temp=allPlayers.get(i);
            if(temp.getNickname().equals(nickname)){
                return true;
            }
        }
        return false;
    }
    //returns the Card given the Id, may be useful in some methods
    public Card getCardById(int id){
        Card temp=null;
        for(int i=0;i<cards.size();i++){
            temp=cards.get(i);
            if(temp.getId()==id){
                return temp;
            }
        }
        return null;
    }

}
