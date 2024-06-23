package CODEX.org.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * This class represent a deck which contains a stack of objective cards
 */
public class ObjectiveDeck extends Deck implements Serializable {
    private Stack<ObjectiveCard> cards;


    /**
     * Class constructor
     * creates a new deck, allocating space for a stack
     *
     * @param stack of cards
     */
    public ObjectiveDeck(Stack<ObjectiveCard> stack) {
        cards = stack;
    }


    /**
     * This method creates the objective deck
     *
     * @return objective deck created
     */
    public static ObjectiveDeck objectiveDeck() {
        Stack<ObjectiveCard> objectiveDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();

        InputStream stream = ObjectiveDeck.class.getClassLoader().getResourceAsStream("objectiveCards.json");

        try {
            objectiveDeck = objectMapper.readValue(stream, new TypeReference<Stack<ObjectiveCard>>() {
            });
        } catch (IOException ignored) {
        }
        return new ObjectiveDeck(objectiveDeck);
    }


    /**
     * This method shuffles the deck: all the cards will have a random order after this function has been called
     */
    @Override
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }


    /**
     * Check if the deck is finished (contains no more cards)
     *
     * @return true if the deck is finished, false if it's not
     */
    @Override
    public boolean isFinished() {
        return cards.isEmpty();
    }


    /**
     * Getter method
     * This method returns the first card in the deck
     *
     * @return the first card in the deck
     */
    public ObjectiveCard getFirstCard() { // returns the first card on the deck, for example when the player needs to pick it up
        return cards.pop();
    }


    /**
     * Getter method
     *
     * @return the whole stack
     */
    public Stack<ObjectiveCard> getCards() {
        return cards;
    }

}
