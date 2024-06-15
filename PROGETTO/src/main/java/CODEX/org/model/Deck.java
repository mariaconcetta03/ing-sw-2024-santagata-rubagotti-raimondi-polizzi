package CODEX.org.model;

import java.io.Serializable;
import java.util.*;

/**
 * This abstract class represents a deck of cards of the same type
 */
public abstract class Deck implements Serializable {
    private Stack<? extends Card> cards; // all the cards into deck



    /**
     * Default constructor
     */
    public Deck(){}



    /**
     * This method shuffles the deck: all the cards will have a random order after this function has been called
     */
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }



    /**
     * Check if the deck is finished (contains no more cards)
     * @return true if the deck is finished, false if it's not
     */
    public boolean isFinished() { // if the deck it's finished, then returns TRUE. If it's not, then returns FALSE
        return cards.isEmpty();
    }
}
