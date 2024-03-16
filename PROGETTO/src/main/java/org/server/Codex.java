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
    public static Codex getInstance(){
        return instance;
    }
    //I believe that method startLobby() should get as a parameter the first player joining
    //that is also the one deciding how many players the game will have (2-3-4)
    public void startLobby(Player creator){
        Game newGame= new Game();


    }
    public void endLobby(){

    }
    public void connectPlayer() {
        Player newPlayer = new Player();
        String tempName = null;
        //we can make it a bit better saying the nickname is already taken
        do {
            System.out.println("Insert nickname: ");
            tempName = sc.nextLine();
        }while(!isNicknameAvailable(tempName));
        newPlayer.setNickname(tempName);
        allPlayers.add(newPlayer);
        //we manage the decision related to multiple games by using a scanner
        //obviously when we'll implement the view it'll be different
        if(!allGames.isEmpty()) {
            System.out.println("Already started games are:");
            for (int i = 0; i < allGames.size(); i++) {
                System.out.print(allGames.get(i) + "   ");
            }
        }else{
            startLobby(newPlayer);
        }
        System.out.println("Would you like to join an ");
        for(int i=0; i<allGames.size();i++){

        }
        //have to understand how to connect to games
    }
    public void disconnectPlayer(){

    }
    public boolean isNicknameAvailable(String nickname){
        Player temp=null;
        for(int i=0; i<allPlayers.size();i++){
            temp=allPlayers.get(i);
            if(temp.getNickname().equals(nickname)){
                return false;
            }
        }
        return true;
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
