package CODEX.org.model;

import CODEX.Exceptions.CardNotDrawableException;
import CODEX.Exceptions.CardNotOwnedException;
import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.*;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents a player in a game
 */

public class Player extends Observable implements Serializable {

    private List<ObjectiveCard> personalObjective;
    private String nickname;
    private Board board;
    private int points;
    private Game game;
    private PlayableCard[] playerDeck; // each player has a deck of 3 cards
    private boolean isFirst;
    private Pawn color;
    private PlayerState state;
    private int numObjectivesReached;

    public enum PlayerState {
        /**
         * The player is playing
         */
        IS_PLAYING,


        /**
         * The player is waiting
         */
        IS_WAITING
    }


    /**
     * Class constructor
     * this method would be called if the player is created
     * before locating a Game instance
     */
    public Player() {
        this.nickname = null;
        this.board = null;
        this.points = 0;
        this.game = null;
        this.playerDeck = new PlayableCard[]{null, null, null};
        this.isFirst = false;
        this.color = null;
        this.personalObjective = new ArrayList<>();
        this.state = PlayerState.IS_WAITING;
    }


    /**
     * This method draws a card and if there's an association between game and player then
     * modifies the market, whilst if it has been used
     * introduces another card in the player's deck
     *
     * @param card is the card that the player has taken from a deck or from the market
     * @throws CardNotDrawableException if the player is trying to draw a not drawable card
     */
    public void drawCard(PlayableCard card) throws CardNotDrawableException {
        boolean drawn = false;

        PlayableDeck baseDeck = PlayableDeck.baseDeck();

        // this next ones are the cards belonging to the market
        PlayableCard resourceCard1 = game.getResourceCard1();
        PlayableCard resourceCard2 = game.getResourceCard2();
        PlayableCard goldCard1 = game.getGoldCard1();
        PlayableCard goldCard2 = game.getGoldCard2();

        //checking card type then calling reset methods from game to set up market area
        if (card.equals(goldCard1)) {
            game.resetGoldCard1();
        } else if (card.equals(goldCard2)) {
            game.resetGoldCard2();
        } else if (card.equals(resourceCard1)) {
            game.resetResourceCard1();
        } else if (card.equals(resourceCard2)) {
            game.resetResourceCard2();
        } else if ((!game.getResourceDeck().isFinished()) && (card.equals(game.getResourceDeck().checkFirstCard()))) {
            card = game.getResourceDeck().getFirstCard();
            notifyObservers(new UpdateResourceDeckEvent(game.getResourceDeck()));
        } else if ((!game.getGoldDeck().isFinished()) && card.equals(game.getGoldDeck().checkFirstCard())) {
            card = game.getGoldDeck().getFirstCard();
            notifyObservers(new UpdateGoldDeckEvent(game.getGoldDeck()));
        } else if (!baseDeck.getCards().contains(card)) {
            throw new CardNotDrawableException("You can't draw this card!");
        }

        // where card is null, the new card is placed
        for (int i = 0; i < 3 && !drawn; i++) {
            if (playerDeck[i] == null) {
                drawn = true;
                playerDeck[i] = card;
            }
        }

        notifyObservers(new UpdatePlayerDeckEvent(this.nickname, this.playerDeck));
    }


    /**
     * This method plays a card and removes it from playerDeck
     *
     * @param card        the card the player wants to place on his board
     * @param position    of the card (decided by the player himself)
     * @param orientation of the card (decided by the player himself)
     */
    public void playCard(PlayableCard card, Coordinates position, boolean orientation) throws IllegalArgumentException {

        card.setOrientation(orientation);

        if (!board.placeCard(card, position)) {// if I can't play the card here
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < 3; i++) {
            if (playerDeck[i].equals(card)) {
                playerDeck[i] = null;
            }
        }

        notifyObservers(new UpdatePlayerDeckEvent(this.nickname, this.playerDeck));
        notifyObservers(new UpdateBoardEvent(this.nickname, this.board, card)); // add the last added card
    }


    /**
     * This method sets base card orientation and places it
     *
     * @param orientation of the card (decided by the player himself)
     * @param card        it's the first base card
     */
    public void playBaseCard(boolean orientation, PlayableCard card) {
        card.setOrientation(orientation);
        board.placeBaseCard(card);

        notifyObservers(new UpdatePlayerDeckEvent(this.nickname, this.playerDeck));
        notifyObservers(new UpdateBoardEvent(this.nickname, this.board, card));
    }


    /**
     * This method adds the points to the player
     *
     * @param points points to add to the player
     */
    public void addPoints(int points) {
        this.points = this.points + points;
    }


    /**
     * This method adds the number of objective reached by the player (+1)
     */
    public void addNumObjectivesReached() {
        this.numObjectivesReached = numObjectivesReached + 1;
    }


    /**
     * This method adds an objective card to the list. The list will contain 2 random objective cards and the player will need to select only one of them
     *
     * @param card is the objective card you need to add to the list
     */
    public void addPersonalObjective(ObjectiveCard card) {
        this.personalObjective.add(card);
        notifyObservers(new UpdatePersonalObjectiveEvent(card, this.nickname));
    }


    /**
     * Setter method
     *
     * @param color is the one chosen by player
     */
    public void setColor(Pawn color) {
        this.color = color;
        notifyObservers(new UpdatePlayerPawnEvent(this.nickname, this.color));
    }


    /**
     * Setter method
     *
     * @param game where the player is
     */
    public void setGame(Game game) {
        this.game = game;

    }


    /**
     * Setter method
     *
     * @param isFirst says if the player is the first one in each round or not
     */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }


    /**
     * Setter method
     *
     * @param nickname of the player
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    /**
     * Setter method
     *
     * @param card is the personal objective chosen by the player
     * @throws CardNotOwnedException if the card is not owned by the players who is trying to select it
     */
    public void setPersonalObjective(ObjectiveCard card) throws CardNotOwnedException {
        if (this.personalObjective.contains(card)) {
            if (card.equals(this.personalObjective.get(0))) {
                this.personalObjective.remove(1);
            } else if (card.equals(this.personalObjective.get(1))) {
                this.personalObjective.remove(0);
            }
        } else {
            throw new CardNotOwnedException("You can't select this card!");
        }
    }


    /**
     * Setter method
     *
     * @param state of the player
     */
    public void setState(PlayerState state) {
        this.state = state;
    }


    /**
     * Setter method
     *
     * @param playerDeck which represents the cards that the player has
     */
    public void setPlayerDeck(PlayableCard[] playerDeck) {
        this.playerDeck = playerDeck;
    }


    /**
     * Setter method
     *
     * @param board of the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }


    /**
     * Getter method
     *
     * @return isFirst says if the player is the first or not
     */
    public boolean isFirst() {
        return this.isFirst;
    }


    /**
     * Getter method
     *
     * @return nickname is the name of the player
     */
    public String getNickname() {
        return this.nickname;
    }


    /**
     * Getter method
     *
     * @return points reached by the player
     */
    public int getPoints() {
        return this.points;
    }


    /**
     * Getter method
     *
     * @return color chosen by the player
     */
    public Pawn getChosenColor() {
        return color;
    }


    /**
     * Getter method
     *
     * @return board of the player
     */
    public Board getBoard() {
        return this.board;
    }


    /**
     * Getter method
     *
     * @return personalObjective is the chosen objective of the player
     */
    public ObjectiveCard getPersonalObjective() {
        return this.personalObjective.get(0);
    }


    /**
     * Getter method
     *
     * @return a list of objective to make the player choose the personal objective
     */
    public List<ObjectiveCard> getPersonalObjectives() {
        return this.personalObjective;
    }


    /**
     * Getter method
     *
     * @return the number of objectives reached by the player
     */
    public int getNumObjectivesReached() {
        return numObjectivesReached;
    }


    /**
     * Getter method
     *
     * @return the player's deck
     */
    public PlayableCard[] getPlayerDeck() {
        return playerDeck;
    }


    /**
     * Getter method
     *
     * @return game to which the player is associated
     */
    public Game getGame() {
        return this.game;
    }


    /**
     * Getter method
     *
     * @param index of the card, it must be between 1 and 3
     * @return the card of the player in "index" position - 1
     */
    public PlayableCard getPlayerDeck(int index) {
        if (index < 4 && index > 0) {
            return this.playerDeck[index - 1];
        } else {
            return null;
        }
    }

}