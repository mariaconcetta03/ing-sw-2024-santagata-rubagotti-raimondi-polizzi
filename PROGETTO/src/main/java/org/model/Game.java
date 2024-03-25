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

    /** these ones are the 4 cards which are at the table center: players can see them and can decide to draw from the
       deck, or to draw one of these cards */
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;

    /**
     *  this 2 cards represent the 2 common goals (objectives)
     */
    private ObjectiveCard objectiveCard1;
    private ObjectiveCard objectiveCard2;

    private List<Chat> chats; // contains all the chats started during the game





    /**
     * Class constructor
     * @param player is the first player who connected to the server
     * @param id each game has a different id
     * @param resourceDeck all the resource cards
     * @param goldDeck all the gold cards
     * @param baseDeck all the base cards
     * @param objectiveDeck all the objective cards
     */
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
     * Only the first player is added to the list by the constructor, the others will be added thanks to this function
     * @param p a player that has to be added
     * @throws ArrayIndexOutOfBoundsException if max number of player is reached
     */
    public void addPlayer (Player p) throws ArrayIndexOutOfBoundsException {
        if (players.size() < nPlayers) {
            this.players.add(p);
        } else throw new ArrayIndexOutOfBoundsException();
    }






    /**
     * This method sets the state of the game to STARTED,
     * it shuffles the resource deck and the gold one, giving for each type of deck 2 cards to the market,
     * then it shuffles the base deck and each player draws a starter card (base card)
     * the method lets the players decide the color of their pawn, the order of choosing is the order in which the player connected to the server
     * it gives 2 cards to the market as common objective
     * it gives each player 2 objective cards, he will decide which one to choose
     * it sets the game-order of the players
     * @throws IllegalArgumentException if players are less than 2 or more than 4
     */
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

        // letting the players decide the color of their pawn
        // the order of choosing is the order in which the player connected to the server
        List <Pawn> colors = new ArrayList<>();
        colors.add(Pawn.RED);
        colors.add(Pawn.BLUE);
        colors.add(Pawn.YELLOW);
        colors.add(Pawn.GREEN);
        Scanner sc = new Scanner(System.in);
        String chosenColour;

        for (int i=0; i<this.players.size(); i++) {
            chosenColour = sc.nextLine();
            if (chosenColour.equals("RED") && colors.contains(Pawn.RED)) {
                colors.remove(Pawn.RED);
                this.players.get(i).setColor(Pawn.RED);
            } else if (chosenColour.equals("BLUE") && colors.contains(Pawn.BLUE)) {
                colors.remove(Pawn.BLUE);
                this.players.get(i).setColor(Pawn.BLUE);
            } else if (chosenColour.equals("YELLOW") && colors.contains(Pawn.YELLOW)) {
                colors.remove(Pawn.YELLOW);
                this.players.get(i).setColor(Pawn.YELLOW);
            } else if (chosenColour.equals("GREEN") && colors.contains(Pawn.GREEN)) {
                colors.remove(Pawn.GREEN);
                this.players.get(i).setColor(Pawn.GREEN);
            } else throw new IllegalArgumentException();
        }

        sc.close();

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
        List<Player> newOrder = new ArrayList<>();
        int randomFirstPlayer = random.nextInt(nPlayers); // sorting a random number between 0 and nPlayers -1
        this.players.get(randomFirstPlayer).setIsFirst(true); // he is the first player
        this.currentPlayer = this.players.get(randomFirstPlayer); // he is the first player and the current player
        newOrder.add(this.players.get(randomFirstPlayer)); // the game-order positions start from 1. Example: 1,2,3,4

        int not_assigned = 3;
        for (int i=randomFirstPlayer; i<this.players.size(); i++) { //assigning the game-order position to the players
            // that follow the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned--;
        }
        for (int i = 0; not_assigned!=0 && i<this.players.size(); i++) { //assigning the game-order position to the
            // players that comes before the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned--;
        }
        this.players = newOrder; // setting the new order
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
     * This method returns a list with the player who won the match. If there are 2 or more winners, it returns a list with more players
     * @return winner
     */
    public List<Player> winner () {
        List<Player> winners = new ArrayList<>();
        int maxPoints = 0;
        for (int i = 0; i < this.players.size(); i++) { // looking for the highest points
            if (maxPoints < this.players.get(i).getPoints()) {
                maxPoints = this.players.get(i).getPoints();
            }
        }

        for (int i = 0; i < this.players.size(); i++) { // putting in the list the player(s) with highest points
            if (this.players.get(i).getPoints() == maxPoints) {
                winners.add(this.players.get(i));
            }
        }

        if (winners.size() > 1) { // I need to check how many objectives every player has respected
            int maxObjectives = 0;

            for (int i = 0; i < this.players.size(); i++) { // checking the higher number of objectives achieved
                if (this.players.get(i).getNumObjectivesReached() > maxObjectives) {
                    maxObjectives = this.players.get(i).getNumObjectivesReached();
                }
            }


            for (int i = 0; i < winners.size(); i++) { // I need to remove the ones who have achieved less objectives
                if (this.players.get(i).getNumObjectivesReached() < maxObjectives) {
                    winners.remove(i);
                }
            }
        }
        return winners;
    }







    /**
     * This method adds a new chat to the List chat in this game. P1 and P2 are the 2 players the chat is composed by
     * @param p1 first player in the chat
     * @param p2 second player in the chat
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
     * This method adds a new chat to the List chat in this game. The new chat is composed by all the players in this game
     */
    public void startGeneralChat() {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...

        // the General Chat includes all the players in the game
        Chat newChat = new Chat(this.players, index);
        chats.add(newChat);
    }






    /**
     *  The first player of the list, after this method is invoked, is the one who will need to play soon, at the next round.
     *  The order of the whole list is modified
     *  @return the next player is who will play soon
     */
    public Player nextRound() {
        this.players.add(this.players.get(0));
        this.players.remove(0);
        this.currentPlayer = this.players.get(0);
        return this.currentPlayer;
    }






    /**
     * These 4 methods are useful to replace a card in the market.
     * The market is formed by 2 gold cards and 2 resource cards, which the player can pick up during the game
     */
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




    /**
     * Getter method
     * @return goldCard1 in the market
     */
    public PlayableCard getGoldCard1() {
        return this.goldCard1;
    }



    /**
     * Getter method
     * @return goldCard2 in the market
     */
    public PlayableCard getGoldCard2() {
        return this.goldCard2;
    }



    /**
     * Getter method
     * @return objectiveCard1 is one of the common objective
     */
    public ObjectiveCard getObjectiveCard1() {
        return this.objectiveCard1;
    }



    /**
     * Getter method
     * @return objectiveCard2 is one of the common objective
     */
    public ObjectiveCard getObjectiveCard2() {
        return this.objectiveCard2;
    }



    /**
     * Getter method
     * @return resourceCard1 in the market
     */
    public PlayableCard getResourceCard1() {
        return this.resourceCard1;
    }



    /**
     * Getter method
     * @return resourceCard2 in the market
     */
    public PlayableCard getResourceCard2() {
        return this.resourceCard2;
    }



    /**
     * Getter method
     * @return resourceDeck of the game
     */
    public Deck getResourceDeck() {
        return this.resourceDeck;
    }



    /**
     * Getter method
     * @return goldDeck of the game
     */
    public Deck getGoldDeck() {
        return this.goldDeck;
    }



    /**
     * Getter method
     * @return state of the game (for example if it's ended)
     */
    public GameState getState() {
        return this.state;
    }



    /**
     * Getter method
     * @return baseDeck of the game
     */
    public Deck getBaseDeck() {
        return baseDeck;
    }



    /**
     * Getter method
     * @return objectiveDeck of the game
     */
    public ObjectiveCard[] getObjectiveDeckDeck() {
        return objectiveDeck;
    }



    /**
     * Getter method
     * @return players are the ones who are playing
     */
    public List<Player> getPlayers () {
        return this.players;
    }



    /**
     * Getter method
     * @return id of the game
     */
    public int getId() {
        return id;
    }



    /**
     * Getter method
     * @return nPlayers is the number of the players
     */
    public int getnPlayers() {
        return nPlayers;
    }



    /**
     * Setter method
     * @param id of the game
     */
    public void setId(int id) {
        this.id = id;
    }



    /**
     * Setter method
     * @param nPlayers is the number of the players
     */
    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }



    /**
     * Setter method
     * @param state of the game
     */
    public void setState (GameState state) {
        this.state = state;
    }

}
