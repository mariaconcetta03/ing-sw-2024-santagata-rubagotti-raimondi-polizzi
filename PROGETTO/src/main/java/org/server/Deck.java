package org.server;
import java.util.*;

public class Deck {
    private Stack<Integer> cards;

    public Deck() {
        cards = new Stack<>();
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public int getFirstCard() {
        return cards.pop().getId();
    }

    public boolean isFinished() {
        return cards.isEmpty(); //if it's finished, then returns TRUE
    }

}
