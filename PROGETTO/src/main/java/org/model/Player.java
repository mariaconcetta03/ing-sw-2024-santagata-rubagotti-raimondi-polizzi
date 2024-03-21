package org.model;


import java.util.List;

public class Player {

    public enum PlayerState {
        IS_PLAYING,
        IS_WAITING,
        IS_DISCONNECTED
    }

    private String nickname;
    private Board board;
    private int points;
    private Game game;
    private int playOrder;
    private PlayableCard[] playerDeck; //each player has a deck of 3 cards
    private boolean isFirst; // you can see if a player is the first one
    private boolean association; // verify if a game is associated with a specific player
    private Pawn color;
    private Card personalObjective; // set a personal objective chosen by a player
    private Card personalObjectiveRejected; // personal Objective rejected by the player
    private PlayerState state;

    /**
     * Class constructor
     * @param game is the game in which the player is
     */
    public Player(Game game) {
        this.points = 0;
        this.isFirst = false;
        this.personalObjective = null;
        this.state = PlayerState.IS_WAITING;
        this.playOrder = 0;
        this.playerDeck = new PlayableCard[] {null, null, null};
        this.association = false;
        this.game = game;
    }

    /**
     * Setter
     * @param playOrder
     */
    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }

    /**
     * Setter
     * @param color
     */
    public void setColor (Pawn color) {
        this.color = color;
    }

    /**
     * Setter
     * @param game
     */
    public void setGame(Game game){
        this.game = game;
    }

    /**
     * Setter
     * @param isFirst
     */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    /**
     * Setter
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Setter
     * @param card
     */
    public void setObjectiveCard(Card card) {
        this.personalObjective = card; //chosen objective card by command line by player
    }

    /**
     * Getter
     * @return isFirst
     */
    public boolean isFirst() {
        return this.isFirst;
    }

    /**
     * Getter
     * @return nickname
     */
    public String getNickname() {
        return this.nickname;
    }
    /**
     * Getter
     * @return points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Getter
     * @return playOrder
     */
    public int getPlayOrder() {
        return this.playOrder;
    }

    /**
     * Getter
     * @return color
     */
    public Pawn getChosenColor () {
        return color;
    }


    /**
     * draws a card and if there's an association between game and player then
     * modifies the market if it has been used
     * introduces another card in the player's deck
     * @param card
     */
    public void drawCard(PlayableCard card) {

        if(association) {

            PlayableCard resourceCard1 = game.getResourceCard1(); //market
            PlayableCard resourceCard2 = game.getResourceCard2(); //market
            PlayableCard goldCard1 = game.getGoldCard1(); //market
            PlayableCard goldCard2 = game.getGoldCard2(); //market

            //checking card type then calling reset methods from game to set up market area
            if (card == goldCard1) {
                //setting new card of goldCard1 in market
                game.resetGoldCard1();

            } else if (card == goldCard2) {
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
    }

    /**
     * check if there is an association between game and player
     * @param gameId
     * @return association
     */
    public boolean checkPlayerGame(int gameId) { //makes a check if gameid is equal to player's gameid
        if(gameId == game.getId()){
            association = true;
        }
        return association;
    }

    /**
     * plays a card and removes one from player's deck
     * @param card
     * @param position
     * @param orientation
     * @return card
     */
    public PlayableCard playCard(PlayableCard card, Coordinates position, boolean orientation) {
        card.setOrientation(orientation);

        // I'll add a method for giving coordinates to the board
        board.placeCard(card, position);

        for (int i = 0; i < 3; i++) {
            if (playerDeck[i].equals(card)) {
                playerDeck[i] = null;      // value 0 in playerDeck, as no id will be = 0
            }
        }
        return card;
    }


    /**
     * check: if base card,then choose the orientation
     * @param orientation
     * @param card
     * @return
     */
    public boolean decideBaseCardOrientation(boolean orientation, BaseCard card) {
        if (card.getId() >= 81 && card.getId() <= 86) {
            card.setOrientation(orientation);
        }
        return orientation;
    }

    /**
     * the two objective cards are given to the player. He will need to choose one of these.
     * the chosen one will remain in "personalObjective", while the other will remain in "personalObjectiveRejected"
     * @param card1
     * @param card2
     */
    public void obtainObjectiveCards (Card card1, Card card2) {
        this.personalObjective = card1;
        this.personalObjectiveRejected = card2;
    }

    /**
     * Setter method
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Getter method
     * @return
     */
    public Board getBoard () {
        return this.board;
    }

}