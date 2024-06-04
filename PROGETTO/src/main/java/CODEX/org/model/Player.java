package CODEX.org.model;


import CODEX.Exceptions.CardNotDrawableException;
import CODEX.Exceptions.CardNotOwnedException;


import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * This class represents a player in a game
 */

public class Player extends Observable implements Serializable {
    private List<ObjectiveCard> personalObjective; // set a personal objective chosen by a player


    public enum PlayerState {
        IS_PLAYING,
        IS_WAITING,
        IS_DISCONNECTED //TODO da rimuovere, non gestiamo disconnessioni
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
    private PlayerState state;
    private int numObjectivesReached;





    /**
     * Class constructor
     * @param game is the game in which the player is (the player is created when the game already exists)
     */
    public Player(Game game) { //TODO da cancellare perchè non lo usiamo
        this.nickname = null;
        this.board = null;
        this.points = 0;
        this.isFirst = false;
        this.personalObjective = new ArrayList<>();
        this.state = PlayerState.IS_WAITING;
        this.playerDeck = new PlayableCard[]{null, null, null};
        this.game = game;
        this.numObjectivesReached = 0;
    }


    /**
     * Class constructor
     * this method would be called in the case the player is created
     * before a Game instance is located
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
     * draws a card and if there's an association between game and player then
     * modifies the market if it has been used
     * introduces another card in the player's deck
     * @param card is the card the player has taken from a deck or from the market
     */
    public void drawCard(PlayableCard card) throws CardNotDrawableException, RemoteException {
        boolean drawn = false;

        PlayableDeck baseDeck=PlayableDeck.baseDeck();

        PlayableCard resourceCard1 = game.getResourceCard1(); //market
        PlayableCard resourceCard2 = game.getResourceCard2(); //market
        PlayableCard goldCard1 = game.getGoldCard1(); //market
        PlayableCard goldCard2 = game.getGoldCard2(); //market

        //checking card type then calling reset methods from game to set up market area
        if (card.equals(goldCard1)) {
                //setting new card of goldCard1 in market
                game.resetGoldCard1();

        } else if (card.equals(goldCard2)) {
                //setting new card of goldCard2 in market
                game.resetGoldCard2();
        } else if (card.equals(resourceCard1)) {
                //setting new card of resource in market
                game.resetResourceCard1();
        } else if (card.equals(resourceCard2)) {
                //setting new card of resource in market
                game.resetResourceCard2();
        } else if ((!game.getResourceDeck().isFinished())&&(card.equals(game.getResourceDeck().checkFirstCard()))){
            card= game.getResourceDeck().getFirstCard();
            notifyObservers(new updateResourceDeckEvent(game.getResourceDeck()));
        } else if ((!game.getGoldDeck().isFinished())&&card.equals(game.getGoldDeck().checkFirstCard())){
            card= game.getGoldDeck().getFirstCard();
            notifyObservers(new updateGoldDeckEvent(game.getGoldDeck()));
        }else if(!baseDeck.getCards().contains(card)){
            throw new CardNotDrawableException("You can't throw this card!");
        }

        // where card is null, the new card is placed
        for (int i = 0; i < 3 && !drawn; i++) {
            if (playerDeck[i] == null) {
                drawn = true;
                playerDeck[i] = card;
            }
        }

        List<Object> tmp=new ArrayList<>();
        tmp.add(this.nickname);
        tmp.add(playerDeck);
        notifyObservers(new updatePlayerDeckEvent(this.nickname, this.playerDeck));
    }





    /**
     * This method plays a card and removes it from playerDeck
     * @param card the card the player wants to place on his board
     * @param position of the card (decided by the player himself)
     * @param orientation of the card (decided by the player himself)
     */
    public void playCard(PlayableCard card, Coordinates position, boolean orientation) throws IllegalArgumentException {

        card.setOrientation(orientation);

        // I'll add a method for giving coordinates to the board
        if (!board.placeCard(card, position)) {
            throw new IllegalArgumentException(); // if I can't play the card here [position + resources]
        }

        for (int i = 0; i < 3; i++) {
            if (playerDeck[i].equals(card)) {
                playerDeck[i] = null;      // value 0 in playerDeck, as no id will be = 0
            }
        }

        List<Object> tmp1=new ArrayList<>();
        tmp1.add(this.nickname);
        tmp1.add(this.playerDeck);
        List<Object> tmp2=new ArrayList<>();
        tmp2.add(this.nickname);
        tmp2.add(this.board);
        try {
            notifyObservers(new updatePlayerDeckEvent(this.nickname, this.playerDeck));
            notifyObservers(new updateBoardEvent(this.nickname, this.board));
        }catch (RemoteException e){}
    }




    /**
     * if base card set orientation and place base card
     * @param orientation of the card (decided by the player himself)
     * @param card it's the first base card
     */
    public void playBaseCard (boolean orientation, PlayableCard card) {
        card.setOrientation(orientation);
        board.placeBaseCard(card);

        List<Object> tmp1=new ArrayList<>();
        tmp1.add(this.nickname);
        tmp1.add(this.playerDeck);
        List<Object> tmp2=new ArrayList<>();
        tmp2.add(this.nickname);
        tmp2.add(this.board);
        try {
            notifyObservers(new updatePlayerDeckEvent(this.nickname, this.playerDeck));
            notifyObservers(new updateBoardEvent(this.nickname, this.board));
        }catch (RemoteException e){}
    }
    //non serve notify, non puo essere posizionata male.


    /**
     * This function adds the points to the player
     * @param points points to add to the player
     */
    public void addPoints (int points) {
        this.points = this.points + points;
    }



    /**
     * This method adds the number of objective reached by the player (+1)
     */
    public void addNumObjectivesReached() {
        this.numObjectivesReached = numObjectivesReached + 1;
    }



    /**
     * Adds an objective card to the list. The list will contain 2 random objective cards, and the player will need to select only one of them
     * @param card is the objective card you need to add to the list
     */
    public void addPersonalObjective(ObjectiveCard card) throws RemoteException{
        this.personalObjective.add(card);

        List<Object> tmp=new ArrayList<>();
        tmp.add(card);
        tmp.add(this.nickname);
        notifyObservers(new UpdatePersonalObjectiveEvent(card, this.nickname));
    }



    /**
     * Setter method
     * @param color is the one chosen by player
     */
    public void setColor(Pawn color) {
        this.color = color;
    }



    /**
     * Setter method
     * @param game where the player is
     */
    public void setGame(Game game) {
        this.game = game;

    }



    /**
     * Setter method
     * @param isFirst says if the player is the first one in each round or not
     */
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }



    /**
     * Setter method
     * @param nickname of the player
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



    /**
     * Setter method
     * @param card is the personal objective chosen by the player
     */
    public void setPersonalObjective (ObjectiveCard card) throws CardNotOwnedException, RemoteException {
        if (this.personalObjective.contains(card)) {
            if (card.equals(this.personalObjective.get(0))) {
                this.personalObjective.remove(1);
            } else if (card.equals(this.personalObjective.get(1))) {
                this.personalObjective.remove(0);
            }
        }else{
            throw new CardNotOwnedException("You can't select this card!");
        }
    }




    /**
     * Setter method
     * @param board of the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }


    
    /**
     * Getter method
     * @return isFirst says if the player is the first or not
     */
    public boolean isFirst() {
        return this.isFirst;
    }



    /**
     * Getter method
     * @return nickname is the name of the player
     */
    public String getNickname() {
        return this.nickname;
    }



    /**
     * Getter method
     * @return points reached by the player
     */
    public int getPoints() {
        return this.points;
    }



    /**
     * Getter method
     * @return color chosen by the player
     */
    public Pawn getChosenColor() {
        return color;
    }



    /**
     * Getter method
     * @return board of the player
     */
    public Board getBoard() {
        return this.board;
    }



    /**
     * Getter method
     * @return personalObjective is the chosen objective of the player
     */
    public ObjectiveCard getPersonalObjective(){
        return this.personalObjective.get(0);
    }

    public List<ObjectiveCard> getPersonalObjectives(){
        return this.personalObjective;
    }

    /**
     * Getter method
     * @return the number of objectives reached by the player
     */
    public int getNumObjectivesReached() {
        return numObjectivesReached;
    }

    public PlayableCard[] getPlayerDeck() {
        return playerDeck;
    }


    /**
     * Getter method
     * @return game to which the player is associated
     */
    public Game getGame () {
        return this.game;
    }




    /**
     * Getter method
     * @param index of the card, it must be between 1 and 3
     * @return the card of the player in "index" position - 1
     */
    public PlayableCard getPlayerDeck (int index) {
        if (index < 4 && index > 0) {
            return this.playerDeck[index-1];
        }
        else {
            return null;
        }
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public void setPlayerDeck(PlayableCard[] playerDeck){
        this.playerDeck = playerDeck;
    }
}

