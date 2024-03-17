package org.server;
import java.util.*;

public class Codex {
    //we can use an int code to unically identify games (not a string as previously said)
    private static int GameID=1;
    private Codex(){}
    private static final Codex instance= new Codex();
    public List<Player> allPlayers;
    public Map<Integer, Game> allGames;

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
        //setting the game ID
        newGame.setId(GameID);
        GameID++;
        //deciding the numbers of players
        System.out.println("How many players to play this game? ");
        int nPlayers= sc.nextInt();
        //mi serve un setter per settare il numero di giocatori
        newGame.setPlayers(nPlayers);
        //set the game state as waiting for start
        newGame.setGameState(Game.GameState.WAITING_FOR_START);
        //adding the creator as the first player in the new game
        newGame.getPlayers().add(creator);
    }

    //a lobby ends if we have a winner or if a player disconnects
    //deciding the winner is duty of the Game class
    //
    public void endLobby(int id){
        allGames.remove(id);
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
                if (allGames.get(i).getState()== Game.GameState.WAITING_FOR_START) {
                    System.out.print(allGames.get(i) + "   ");
                }
            }
        }else{
            startLobby(newPlayer);
            return;
        }
        //hp the user gives me good answer, not stupid ones (we could later implement a check-system)
        System.out.println("Type 0 if you want to create a new game or the code of an already started" +
                "game if you want to join it: ");
        int decision=sc.nextInt();
        if(decision==0){
            startLobby(newPlayer);
        }else{
            Game tmp= new Game();
            for(int i=0; i<allGames.size();i++){
                if(allGames.get(i).getId()==decision){
                    tmp=allGames.get(i);
                    break;
                }
            }
            //mi serve un getter da Game per la lista Players
            tmp.getPlayers().add(newPlayer);
            //it will be duty of the Game object to understand if the lobby is full and so
            // change its state to STARTED
        }

    }
    public void disconnectPlayer(String nickname){
        Player tmp=new Player();
        for(int i=0;i<allPlayers.size();i++){
            if(nickname.equals(allPlayers.get(i).getNickname())){
                tmp=allPlayers.get(i);
                break;
            }
        }
        allPlayers.remove(tmp);
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
