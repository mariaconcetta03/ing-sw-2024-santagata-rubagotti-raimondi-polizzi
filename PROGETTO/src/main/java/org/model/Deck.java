package org.model;
import java.util.*;

/**
 *  This class represents a deck: a group of cards of the same type
 */


public class Deck {
    private Stack<PlayableCard> cards; // contains all the cards in this deck
    public Deck() {
        cards = new Stack<>();
    }




    /**
     * This mehtod shuffles the deck: all the cards will have a random order after this function has been called
     */
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }



    /**
     * This method returns the first card in the deck
     * @return the first card in the deck
     */
    public PlayableCard getFirstCard() { // returns the first card on the deck, for example when the player needs to pick it up
        return cards.pop();
    }



    /**
     * Check if the deck is finished (contains no more cards)
     * @return true if the deck is finished, false if it's not
     */
    public boolean isFinished() { // if the deck it's finished, then returns TRUE. If it's not, returns FALSE
        return cards.isEmpty();
    }
}
