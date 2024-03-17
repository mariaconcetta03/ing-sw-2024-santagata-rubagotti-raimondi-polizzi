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
    private int playOrder;
    private int[] playerDeck;
    private boolean isFirst;
    private Pawn color;
    private Card personalObjective;
    private PlayerState state;
    private Game game;

    public Player() {
        this.points = 0;
        this.isFirst = false;
        this.personalObjective = null;
        this.state = PlayerState.IS_WAITING;
        this.playOrder = 0;
        this.playerDeck = new int[]{0, 0, 0};
    }

    //SETTERS
    public void setPlayOrder(int playOrder) { //Playorder starts from 1!!
        this.playOrder = playOrder;
    }

    public void setColor(Pawn color) {
        this.color = color;
    }

    public void setPriority(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setObjectiveCard(int cardId) {
        this.personalObjective = Codex.getInstance().getCardById(cardId);
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

    //OTHER METHODS
    public void drawCard(int cardId) {

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
        // where id card == 0, the new card is placed
        for (int i = 0; i < 3; i++) {
            if (playerDeck[i] == 0) {
                playerDeck[i] = cardId;
            }
        }

    }

    public int playCard(int cardId, Coordinates position, boolean orientation) {

        Playable_Card tmp = (Playable_Card) Codex.getInstance().getCardById(cardId);
        tmp.setPosition(position);
        tmp.setOrientation(orientation);

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

}