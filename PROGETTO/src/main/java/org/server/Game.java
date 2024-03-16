package org.server;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private List<Board> boards;


    // in the first position of the list "boards", there will be the board of
    // the player in the first position of the list "players"
    public enum GameState {
        STARTED,
        ENDED,
        WAITING_FOR_START
    }
    private GameState state;
    private Player currentPlayer;
    private Deck resourceDeck;
    private Deck goldDeck;
    private int resourceCard1;
    private int resourceCard2;
    private int goldCard1;
    private int goldCard2;
    private int objectiveCard1;
    private int objectiveCard2;
    private List<Chat> chats;

    public Game (List<Player> players, List<Board> boards, Deck resourceDeck, Deck goldDeck, int resourceCard1, int resourceCard2, int goldCard1, int goldCard2, int objectiveCard1, int objectiveCard2) {
        this.players = players;
        this.boards = boards;
        this.chats = new ArrayList<>();
        this.state = GameState.WAITING_FOR_START;
        this.currentPlayer = null;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
        this.resourceCard1 = resourceCard1;
        this.resourceCard2 = resourceCard2;
        this.goldCard1 = goldCard1;
        this.goldCard2 = goldCard2;
        this.objectiveCard1 = objectiveCard1;
        this.objectiveCard2 = objectiveCard2;
    }

    public void startGame () {
        this.state = GameState.STARTED;
    }

    public void endGame () {
        state = GameState.ENDED;
    }

    public Player winner () {
        Player winner = this.players.get(0);
        for (int i=0; i<this.players.size(); i++) { //looking for the player with the higher points
            if (winner.getPoints() < this.players.get(i).getPoints()){
                winner = this.players.get(i);
            }
        }
    return winner;
    }

    public void startChat (Player p1, Player p2) {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...
        List<Player> playersInChat = new ArrayList<>();
        playersInChat.add(p1);
        playersInChat.add(p2);
        // creating a list of the players in the chat

        Chat newChat = new Chat(playersInChat, index);
        chats.add(newChat);
    }

    public void startGeneralChat() {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...

        // the General Chat includes all the players in the game
        Chat newChat = new Chat(this.players, index);
        chats.add(newChat);
    }

    public Player nextRound() {
        boolean trovato = false;
        int numPlayers = this.players.size();

        //if current player is the last player
        if (this.currentPlayer.getPlayOrder() == numPlayers) {
            for (int i = 0; i < this.players.size() && !trovato; i++) {
                if (this.players.get(i).isFirst()) { //i need to return the first player
                    trovato = true;
                    return this.players.get(i);
                }
            }
        }
        else { //if current player is not the last player
            for (int i=0; i<this.players.size() && !trovato; i++) {
                if (this.currentPlayer.getPlayOrder()+1 == this.players.get(i).getPlayOrder()){ //i need to return the next player
                    trovato = true;
                    return this.players.get(i);
                }
            }
        }
    }

    public Board getBoard (Player p) {
        int i=0;
        while (i<this.players.size() && !p.equals(this.players.get(i))) {
            i++; //finding the index of the player p
        }

        return this.boards.get(i); //remember: the player position is the same of his board's position
    }

    public int getGoldCard1() {
        return this.goldCard1;
    }

    public int getGoldCard2() {
        return this.goldCard2;
    }

    public int getObjectiveCard1() {
        return this.objectiveCard1;
    }

    public int getObjectiveCard2() {
        return this.objectiveCard2;
    }

    public int getResourceCard1() {
        return this.resourceCard1;
    }

    public int getResourceCard2() {
        return this.resourceCard2;
    }

    public Deck getResourceDeck() {
        return this.resourceDeck;
    }

    public Deck getGoldDeck() {
        return this.goldDeck;
    }

    public GameState getState() {
        return this.state;
    }

}
