package CODEX.org.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ObjectiveDeck extends Deck implements Serializable {
    private Stack<ObjectiveCard> cards; // contains all the cards in this deck



    /**
     * Class constructor
     * creates a new deck, allocating space for a stack
     */
    public ObjectiveDeck(Stack<ObjectiveCard> stack) {
        cards = stack;
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
     * @return true if the deck is finished, false if it's not
     */
    @Override
    public boolean isFinished() {
        return cards.isEmpty();
    }



    /**
     * Class constructor
     * creates a new deck, with a null stack
     */
    public ObjectiveDeck() {
        cards = null;
    }



    /**
     * This method adds a card to the deck
     * @param card you want to add to the deck
     */
    public void addCard (ObjectiveCard card) {
        cards.add(card);
    }



    /**
     * This method returns the first card in the deck
     * @return the first card in the deck
     */
    public ObjectiveCard getFirstCard() { // returns the first card on the deck, for example when the player needs to pick it up
        return cards.pop();
    }



    /**
     * This method creates a goldDeck with all his 40 cards
     * @return goldDeck
     */
    public static ObjectiveDeck objectiveDeck(){
        Stack<ObjectiveCard> objectiveDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file= new File("PROGETTO/src/main/java/org/model/jsons/objectiveCards.json");
        try{
            objectiveDeck=objectMapper.readValue(file, new TypeReference<Stack<ObjectiveCard>>() {});
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ObjectiveDeck(objectiveDeck);
    }

}
