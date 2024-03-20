package org.server;
import java.util.*;

public class Deck {
    private Stack<Integer> cards; // contains all the cards in this deck

    public Deck() {
        cards = new Stack<>();
    }

    public void shuffleDeck() { // the deck is shuffled: all the cards will have a random order
                                // after this function has been called
        Collections.shuffle(cards);
    }

    public int getFirstCard() { // returns the first card on the deck, for example when the player needs to pick it up
        return cards.pop();
    }

    public boolean isFinished() { // if the deck it's finished, then returns TRUE. If it's not, returns FALSE
        return cards.isEmpty();
    }

}
