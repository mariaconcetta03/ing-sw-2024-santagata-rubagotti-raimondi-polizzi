package org.model;


import java.util.List;

public class Player {

    private ObjectiveCard card1;

    public enum PlayerState {
        IS_PLAYING,
        IS_WAITING,
        IS_DISCONNECTED
    }

    private String nickname;
    private Board board; //we have a board for each player
    private int points;
    private Game game;
    private PlayableCard[] playerDeck; //each player has a deck of 3 cards
    private boolean isFirst; // you can see if a player is the first one
    private Pawn color;
    /**
     * that's the color that the card has and it's chosen between the colors
     * listed in the Enumeration Pawn
    */
    private Card personalObjective; // set a personal objective chosen by a player
    private PlayerState state;

    /**
     * Class constructor
     *
     * @param game is the game in which the player is
     */
    public Player(Game game) {
        this.nickname = null;
        this.board = null;
        this.points = 0;
        this.isFirst = false;
        this.personalObjective = null;
        this.state = PlayerState.IS_WAITING;
        this.playerDeck = new PlayableCard[]{null, null, null};
        this.game = game;
    }

    /**
     * Constructor method
     * this method would be called in the case the player is created
     * before a Game instance is located
     */
    public Player() {
        this.nickname = null;
        this.board = null;
        this.points = 0;
        this.game = null;
        this.playerDeck = null;
        this.isFirst = false;
        this.color = null;
        this.personalObjective = null;
        this.state = null;
    }


    /**
     * Setter method
     *
     * @param color
     */
    public void setColor(Pawn color) {
        this.color = color;
    }

    /**
     * Setter method
     *
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Setter method
     *
     * @param isFirst
     */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    /**
     * Setter method
     *
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Setter method
     *
     * @param card is the personal objective chosen by the player
     */
    public void setPersonalObjective(Card card) {
        this.personalObjective = card; //chosen objective card by command line by player
    }

    /**
     * Setter method
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /** Setter method [called by the controller]
     * @param card personal objective chosen by the player
     */
    public void setPersonalObjective (ObjectiveCard card) {
        this.personalObjective = card;
    }

    /**
     * This method receives the two random Objective cards from the Game class, and passes them
     * to the view, so that the player can choose a card or the other, and then use the method
     * "setPersonalObjective" to set the chosen objective
     * @param card1 first random objective card
     * @param card2 second random objective card
     */
    public void obtainObjectiveCards (ObjectiveCard card1, ObjectiveCard card2) {
        passToView (card1, card2);
    }
    
    /**
     * Getter method
     *
     * @return isFirst
     */
    public boolean isFirst() {
        return this.isFirst;
    }

    /**
     * Getter method
     *
     * @return nickname
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Getter method
     *
     * @return points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Getter method
     *
     * @return color
     */
    public Pawn getChosenColor() {
        return color;
    }

    /**
     * Getter method
     *
     * @return board
     */
    public Board getBoard() {
        return this.board;
    }


    /**
     * draws a card and if there's an association between game and player then
     * modifies the market if it has been used
     * introduces another card in the player's deck
     *
     */
    public void drawCard(PlayableCard card) {

        PlayableCard resourceCard1 = game.getResourceCard1(); //market
        PlayableCard resourceCard2 = game.getResourceCard2(); //market
        PlayableCard goldCard1 = game.getGoldCard1(); //market
        PlayableCard goldCard2 = game.getGoldCard2(); //market

        //checking card type then calling reset methods from game to set up market area
        if (card == goldCard1) {
            //setting new card of goldCard1 in market
            game.resetGoldCard1();
 
        } else if (card == goldCard2) { //
            //setting new card of goldCard2 in market
            game.resetGoldCard2();

        } else if (card == resourceCard1) {
            //setting new card of resource in market
            game.resetResourceCard1();

        } else if (card == resourceCard2) {
            //setting new card of resource in market
            game.resetResourceCard2();

        }

        // where card is null, the new card is placed
        for (int i = 0; i < 3; i++) {
            if (playerDeck[i] == null) {
                playerDeck[i] = card;
            }
        }
    }

    /**
     * plays a card and removes one from player's deck
     *
     * @param card
     * @param position
     * @param orientation
     */
    public void playCard(PlayableCard card, Coordinates position, boolean orientation) throws IllegalArgumentException {

        card.setOrientation(orientation);

        // I'll add a method for giving coordinates to the board
        if (!board.placeCard(card, position)){
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < 3; i++) {
            if (playerDeck[i].equals(card)) {
                playerDeck[i] = null;      // value 0 in playerDeck, as no id will be = 0
            }
        }
    }


    /**
     * if base card set orientation and place base card
     *
     * @param orientation
     * @param card
     * @return
     */
    public void playBaseCard (boolean orientation, PlayableCard card) {
        board.placeBaseCard(card);
        card.setOrientation(orientation);
    }



    /**
     * This function adds the points to the player
     * @param points points to add to the player
     */
    public void addPoints (int points) {
        this.points = this.points + points;
    }

}

