package org.model;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * This class represents the instance of a single game started by the Server
 */
public class Game {
    private int id; // each Game has a different id
    private int nPlayers; // number of players in this game. It's decided by the lobby-creator
    private List<Player> players; // all the players in the game
    public enum GameState {
        STARTED,
        ENDING,
        ENDED,
        WAITING_FOR_START
    }
    private GameState state;
    private Player currentPlayer; // player who needs to play at this moment (it's his turn)
    private Deck resourceDeck; // contains all the resource cards
    private Deck goldDeck; // contains all the gold cards
    private Deck baseDeck; // contains all the base cards, which are the cards that players use to start the game
    private ObjectiveCard[] objectiveDeck; // contains all the objective cards

    /* these ones are the 4 cards which are at the table center: players can see them and can decide to draw from the
       deck, or to draw one of these cards */
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;

    // this 2 cards represent the 2 common goals (objectives)
    private ObjectiveCard objectiveCard1;
    private ObjectiveCard objectiveCard2;
    private Scanner sc= new Scanner(System.in);

    private List<Chat> chats; // contains all the chats started during the game

    public Game (Player player, int id, Deck resourceDeck, Deck goldDeck, Deck baseDeck, ObjectiveCard[] objectiveDeck) {
        this.id = id;
        this.players = new ArrayList<>();
        this.players.add(player);
        this.chats = new ArrayList<>();
        this.state = GameState.WAITING_FOR_START;
        this.currentPlayer = null;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
        this.baseDeck = baseDeck;
        this.objectiveDeck = objectiveDeck;
        this.resourceCard1 = null;
        this.resourceCard2 = null;
        this.goldCard1 = null;
        this.goldCard2 = null;
        this.objectiveCard1 = null;
        this.objectiveCard2 = null;
    }



    /**
     * only the first player is added to the list by the constructor, the others will be added thanks to this function
     * @param p
     * @throws InvalidDnDOperationException
     */
    public void addPlayer (Player p) throws ArrayIndexOutOfBoundsException {
        if (players.size() < nPlayers) {
            this.players.add(p);
        } else throw new ArrayIndexOutOfBoundsException();
    }


    public void startGame () throws IllegalArgumentException {
        if((players.size()<2)||(players.size()>4)){
            throw new IllegalArgumentException("Incorrect number of players");}

        // setting the state of the game to STARTED
        this.state = GameState.STARTED;

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

        //rivedere completamente
        // setting the colour of the pawn of the players
        for (int i=0; i<this.players.size(); i++) {
            List <Pawn> colors = new ArrayList<>();
            colors.add(Pawn.RED);
            colors.add(Pawn.BLUE);
            colors.add(Pawn.YELLOW);
            colors.add(Pawn.GREEN);
            Scanner sc;
            if(i==0) {
                this.players.get(i).setColor(colors.get(0));
                colors.remove(this.players.get(i).getChosenColor());
            } else if (i==1) {
                this.players.get(i).getAvailableColors(colors);
                colors.remove(this.players.get(i).getChosenColour());
            } else if (i==2) {
                this.players.get(i).getAvailableColors(colors);
                colors.remove(this.players.get(i).getChosenColour());
            } else if (i==3) {
                this.players.get(i).getAvailableColors(colors);
                colors.remove(this.players.get(i).getChosenColour());
            }
        }

        // giving 2 cards to the market as common objective
        List<Integer> usedIndexes = new ArrayList<>();
        Random rand = new Random();

        int index = rand.nextInt(objectiveDeck.length);
        this.objectiveCard1 = objectiveDeck[index];
        usedIndexes.add(index);
        while (usedIndexes.contains(index)) {
            index = rand.nextInt(objectiveDeck.length);
		}
        usedIndexes.add(index);
        this.objectiveCard2 = objectiveDeck[index];


        // giving each player 2 objective cards, he will decide which one to choose
        for (int i=0; i<this.players.size(); i++) {
            while (usedIndexes.contains(index)) {
                index = rand.nextInt(objectiveDeck.length);
            }
            usedIndexes.add(index);

            int index2 = index;
            while (usedIndexes.contains(index2)) {
                index2 = rand.nextInt(objectiveDeck.length);
            }
            usedIndexes.add(index2);
            this.players.get(i).obtainObjectiveCards (this.objectiveDeck[index], this.objectiveDeck[index2]);
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

    /**
     * after the player has played on the board the base card, this function is invoked
     * this function gives to the player 2 resource cards + 1 base card
     */
    public void giveInitialCards () {
        for (int i=0; i<nPlayers; i++) {
            players.get(i).drawCard(resourceDeck.getFirstCard()); // resource card #1
            players.get(i).drawCard(resourceDeck.getFirstCard()); // resource card #2
            players.get(i).drawCard(goldDeck.getFirstCard()); // gold card #1
        }
    }


    /**
     * This method ends the game
     */
    public void endGame () {
        state = GameState.ENDED;
    }

    /**
     * returns the player with the higher number of points
     * @return winner
     */
    public Player winner () {
        Player winner = this.players.get(0);
        for (int i=0; i<this.players.size(); i++) { //looking for the player with the higher points
            if (winner.getPoints() < this.players.get(i).getPoints()){
                winner = this.players.get(i);
            }
        }
    return winner;
    }


    /**
     * adds a new chat to the List chat in this game. P1 and P2 are the 2 players the chat is composed by
     * @param p1
     * @param p2
     */
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


    /**
     * adds a new chat to the List chat in this game. The new chat is composed by all the players in this game
     */
    public void startGeneralChat() {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...

        // the General Chat includes all the players in the game
        Chat newChat = new Chat(this.players, index);
        chats.add(newChat);
    }


    /**
     * returns the player who will need to play soon, at the next round
     */
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

    public PlayableCard getGoldCard1() {
        return this.goldCard1;
    }

    public PlayableCard getGoldCard2() {
        return this.goldCard2;
    }

    public ObjectiveCard getObjectiveCard1() {
        return this.objectiveCard1;
    }

    public ObjectiveCard getObjectiveCard2() {
        return this.objectiveCard2;
    }

    public PlayableCard getResourceCard1() {
        return this.resourceCard1;
    }

    public PlayableCard getResourceCard2() {
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

    public ObjectiveCard[] getObjectiveDeckDeck() {
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
