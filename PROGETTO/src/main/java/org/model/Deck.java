package org.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Deck {
    private Stack<? extends Card> cards; // all the cards into deck

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

    public void goo;
}
