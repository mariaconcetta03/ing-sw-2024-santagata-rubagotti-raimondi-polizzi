package org.server;
import java.util.*;



public class Player {

    public enum Pawn {
        RED,
        BLUE,
        GREEN,
        YELLOW
    }

    public enum PlayerState {
        IS_PLAYING,
        IS_WAITING,
        IS_DISCONNECTED
    }

    private String nickname;
    private boolean Orientation;
    private int points;
    private int gameId;
    private int playOrder;
    private int[] playerDeck; //each player has a deck of 3 cards
    private boolean isFirst; // you can see if a player is the first one
    private boolean association; // verify if a game is associated with a specific player
    private Pawn color;
    private Card personalObjective; // set a personal objective chosen by a player
    private Card personalObjectiveRejected; // personal Objective rejected by the player
    private PlayerState state;
    private Game game;


    public Player() {
        this.points = 0;
        this.isFirst = false;
        this.personalObjective = null;
        this.state = PlayerState.IS_WAITING;
        this.playOrder = 0;
        this.playerDeck = new int[]{0, 0, 0};
        this.association = false;
        this.gameId = 0;
    }

    //SETTERS
    public void setPlayOrder(int playOrder) { //Playorder starts from 1!!
        this.playOrder = playOrder;
    }

    public void setColor(Pawn color) {
        this.color = color;
    }

    public void setGame(Game game){
        this.game = game;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void setObjectiveCard(Card card) {
        this.personalObjective = card; //chosen objective card by command line by player
    }

    //GETTER
    public boolean isFirst() {
        return this.isFirst;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Pawn getColor() {
        return this.color;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPlayOrder() {
        return this.playOrder;
    }

    public void drawCard(int cardId) {

    if(association) {

        int resourceCard1 = game.getResourceCard1(); //market
        int resourceCard2 = game.getResourceCard2(); //market
        int goldCard1 = game.getGoldCard1(); //market
        int goldCard2 = game.getGoldCard2(); //market

        //checking card type then calling reset methods from game to set up market area
        if (cardId == goldCard1) {
            //setting new id of goldCard1 in market
            game.resetGoldCard1();

        } else if (cardId == goldCard2) {
            //setting new id of goldCard2 in market
            game.resetGoldCard2();

        } else if (cardId == resourceCard1) {
            //setting new id of resource in market
            game.resetResourceCard1();

        } else if (cardId == resourceCard2) {
            //setter di gold
            game.resetResourceCard2();

        }
    }
        // where id card == 0, the new card is placed
        for (int i = 0; i < 3; i++) {
            if (playerDeck[i] == 0) {
                playerDeck[i] = cardId;
            }
        }

    }

    public boolean checkPlayerGame(int gameId) { //makes a check if gameid is equal to player's gameid
        if(gameId == game.getId()){
            association = true;
        }
        return association;
    }

    public int playCard(int cardId, Coordinates position, boolean orientation) {

        Playable_Card tmp = (Playable_Card) Codex.getInstance().getCardById(cardId);
        tmp.setPosition(position);      // we'll check if it's ok
        tmp.setOrientation(orientation);
       // if(!baseCard) {
            //I'll add a method for giving coordinates to the board
            //board.placeCard(cardId, position);
        // }
        for (int i = 0; i < 3; i++) {
            if (playerDeck[i] == cardId) {
                playerDeck[i] = 0;      // value 0 in playerDeck, as no id will be = 0
            }
        }
        return cardId;
    }


    //check: if base card,then choose the orientation
    public boolean decideBaseCardOrientation(boolean orientation, int cardId) {
        if (cardId >= 81 && cardId <= 86) {
            Playable_Card tmp = (Playable_Card) Codex.getInstance().getCardById(cardId);
            tmp.setOrientation(orientation);
        }
        return orientation;
    }

    // the two objective cards are given to the player. He will need to choose one of these.
    // the chosen one will remain in "personalObjective", while the other will remain in "personalObjectiveRejected"
    public void obtainObjectiveCards (Card card1, Card card2) {
        this.personalObjective = card1;
        this.personalObjectiveRejected = card2;
    }

}