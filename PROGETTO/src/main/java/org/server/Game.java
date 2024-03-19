package org.server;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private int id; // each Game has a different id
    private int nPlayers; // number of players in this game. It's decided by the lobby-creator
    private List<Player> players; // all the players in the game
    private List<Board> boards; /* each player has his own board. The player and the boards are linked thanks to their
                                   position: for example the player in the 2nd position, has the board in the 2nd position,
                                   in the first position of the list "boards", there will be the board of the player
                                   in the first position of the list "players" */
    public enum GameState {
        STARTED,
        ENDED,
        WAITING_FOR_START
    }
    private GameState state;
    private Player currentPlayer; // player who needs to play at this moment (it's his turn)
    private Deck resourceDeck; // contains all the resource cards
    private Deck goldDeck; // contains all the gold cards
    private Deck baseDeck; // contains all the base cards, which are the cards that players use to start the game
    private Deck objectiveDeck; // contains all the objective cards

    /* these ones are the 4 cards which are at the table center: players can see them and can decide to draw from the
       deck, or to draw one of these cards */
    private int resourceCard1;
    private int resourceCard2;
    private int goldCard1;
    private int goldCard2;

    // this 2 cards represent the 2 common goals (objectives)
    private int objectiveCard1;
    private int objectiveCard2;

    private List<Chat> chats; // contains all the chats started during the game

    public Game (Player player, int id, Deck resourceDeck, Deck goldDeck, Deck baseDeck, Deck objectiveDeck) {
        this.id = id;
        this.players = new ArrayList<>();
        this.players.add(player);
        this.boards = null;
        this.chats = new ArrayList<>();
        this.state = GameState.WAITING_FOR_START;
        this.currentPlayer = null;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
        this.baseDeck = baseDeck;
        this.objectiveDeck = objectiveDeck;
        this.resourceCard1 = 0;
        this.resourceCard2 = 0;
        this.goldCard1 = 0;
        this.goldCard2 = 0;
        this.objectiveCard1 = 0;
        this.objectiveCard2 = 0;
    }

    // only the first player is added to the list by the constructor, the others will be added thanks to this function
    public void addPlayer (Player p) {
        this.players.add(p);
    }

    public void startGame () {
        // setting the state of the game to STARTED
        // this.state = GameState.STARTED; done in Codex

        // creating a board for each player in the game
        int numPlayers = players.size();
        this.boards = new ArrayList<>();
        for (int i=0; i<numPlayers; i++) {
            this.boards.add(new Board(players.get(i),numPlayers));
        }

        // shuffling the resource deck and giving 2 cards to the market
        this.resourceDeck.shuffleDeck();
        this.resourceCard1 = this.resourceDeck.getFirstCard();
        this.resourceCard2 = this.resourceDeck.getFirstCard();

        // shuffling the gold deck and giving 2 cards to the market
        this.goldDeck.shuffleDeck();
        this.goldCard1 = this.goldDeck.getFirstCard();
        this.goldCard2 = this.goldDeck.getFirstCard();

        // shuffling the base deck and each player draws a starter card (base card)
        for (int i=0; i<this.players.size(); i++) {
            this.baseDeck.shuffleDeck();
            this.players.get(i).drawCard(this.baseDeck.getFirstCard());
        }

        // setting the colour of the pawn of the players
        for (int i=0; i<this.players.size(); i++) {
            if(i==0) {
                this.players.get(i).setColor(Player.Pawn.RED);
            } else if (i==1) {
                this.players.get(i).setColor(Player.Pawn.BLUE);
            } else if (i==2) {
                this.players.get(i).setColor(Player.Pawn.GREEN);
            } else if (i==3) {
                this.players.get(i).setColor(Player.Pawn.YELLOW);
            }
        }

        // shuffling the objective deck and giving 2 cards to the market as common objective
        this.objectiveDeck.shuffleDeck();
        this.objectiveCard1 = this.objectiveDeck.getFirstCard();
        this.objectiveCard2 = this.objectiveDeck.getFirstCard();

        // giving each player 2 objective cards, he will decide which one to choose
        for (int i=0; i<this.players.size(); i++) {
            this.players.get(i).chooseObjectiveCard (this.objectiveDeck.getFirstCard(), this.objectiveDeck.getFirstCard());
        }

        // setting the game-order of the players
        Random random = new Random();
        int randomFirstPlayer = random.nextInt(nPlayers); // sorting a random number between 0 and nPlayers -1
        this.players.get(randomFirstPlayer).setIsFirst(true); // he is the first player
        this.currentPlayer = this.players.get(randomFirstPlayer); // he is the first player and the current player
        this.players.get(randomFirstPlayer).setPlayOrder(1); // the game-order positions start from 1. Example: 1,2,3,4

        int not_assigned = 4;
        int order = 2;
        for (int i=randomFirstPlayer; i<this.players.size(); i++) { //assigning the game-order position to the players
            // that follow the first player in the list
            this.players.get(i).setPlayOrder(order); // the game-order positions start from 1. Example: 1,2,3,4
            order++;
            not_assigned--;
        }
        for (int i = 0; not_assigned!=0 && i<this.players.size(); i++) { //assigning the game-order position to the
            // players that comes before the first player in the list
            this.players.get(i).setPlayOrder(order);
            order++;
            not_assigned--;
        }
    }


    // after the player has played on the board the base card, this function is invoked
    // this function gives to the player 2 resource cards + 1 base card
    public void giveInitialCards () {
        for (int i=0; i<nPlayers; i++) {
            players.get(i).drawCard(resourceDeck.getFirstCard()); // resource card #1
            players.get(i).drawCard(resourceDeck.getFirstCard()); // resource card #2
            players.get(i).drawCard(goldDeck.getFirstCard()); // gold card #1
        }
    }


    public void endGame () {
        state = GameState.ENDED;
    }

    // returns the player with the higher number of points
    public Player winner () {
        Player winner = this.players.get(0);
        for (int i=0; i<this.players.size(); i++) { //looking for the player with the higher points
            if (winner.getPoints() < this.players.get(i).getPoints()){
                winner = this.players.get(i);
            }
        }
    return winner;
    }

    // adds a new chat to the List chat in this game. P1 and P2 are the 2 players the chat is composed by
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

    // adds a new chat to the List chat in this game. The new chat is composed by all the players in this game
    public void startGeneralChat() {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...

        // the General Chat includes all the players in the game
        Chat newChat = new Chat(this.players, index);
        chats.add(newChat);
    }

    // returns the player who will need to play soon, at the next round
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
        return null;
    }

    // returns the board of the player p
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

    public void setState (GameState state) {
        this.state = state;
    }

    public Deck getBaseDeck() {
        return baseDeck;
    }

    public Deck getObjectiveDeckDeck() {
        return objectiveDeck;
    }

    public void resetGoldCard1 () {
        this.goldCard1 = this.goldDeck.getFirstCard();
    }

    public void resetGoldCard2 () {
        this.goldCard2 = this.goldDeck.getFirstCard();
    }

    public void resetResourceCard1 () {
        this.resourceCard1 = this.resourceDeck.getFirstCard();
    }

    public void resetResourceCard2 () {
        this.resourceCard2 = this.resourceDeck.getFirstCard();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public List<Player> getPlayers () {
        return this.players;
    }

}
