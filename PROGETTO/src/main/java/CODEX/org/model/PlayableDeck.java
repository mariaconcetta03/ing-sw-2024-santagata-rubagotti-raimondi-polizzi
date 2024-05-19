package CODEX.org.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


/**
 *  This class represents a deck: a group of cards of the same type
 */


public class PlayableDeck extends Deck implements Serializable {
    private Stack<PlayableCard> cards; // contains all the cards in this deck



    /**
     * Class constructor
     * creates a new deck, allocating space for a stack
     */
    public PlayableDeck(Stack<PlayableCard> stack) {
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
    public PlayableDeck() {
        cards = null;
    }

    /**
     * This method return all the card in the deck as a Stack
     * @return a Stack of PlayableCard
     */
    public Stack<PlayableCard> getCards() {
        return cards;
    }

    /**
     * This method returns the first card in the deck
     * @return the first card in the deck
     */
    public PlayableCard getFirstCard() throws EmptyStackException{// returns the first card on the deck, for example when the player needs to pick it up
        if(!cards.isEmpty()) {
            return cards.pop();
        }else{
            throw new EmptyStackException();
        }
    }

    /**
     * This method checks which is the first card in the deck
     * @return the first card in the deck
     */
    public PlayableCard checkFirstCard() throws EmptyStackException{
        if(!cards.isEmpty()) {
            return cards.peek();
        }else{
            throw new EmptyStackException();
        }
    }

    /**
     * This method creates a goldDeck with all his 40 cards
     * @return goldDeck
     */
    public static PlayableDeck goldDeck(){
        Stack<PlayableCard> goldDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file= new File("PROGETTO/src/main/java/org/model/jsons/goldCards.json");
        try{
            goldDeck=objectMapper.readValue(file, new TypeReference<Stack<PlayableCard>>() {});
        }catch (IOException e){
            e.printStackTrace();
        }
        return new PlayableDeck(goldDeck);
    }


    /**
     * This method creates a resourceDeck with all his 40 cards
     * @return resourceDeck
     */
    public static PlayableDeck resourceDeck(){
        Stack<PlayableCard> resourceDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file= new File("PROGETTO/src/main/java/org/model/jsons/resourceCards.json");
        try{
            resourceDeck=objectMapper.readValue(file, new TypeReference<Stack<PlayableCard>>() {});
        }catch (IOException e){
            e.printStackTrace();
        }
        return new PlayableDeck(resourceDeck);
    }


    /**
     * This method creates a baseDeck with all his 6 cards
     * @return baseDeck
     */
    public static PlayableDeck baseDeck() {
        Stack<PlayableCard> baseDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file= new File("PROGETTO/src/main/java/org/model/jsons/baseCards.json");
        try{
            baseDeck=objectMapper.readValue(file, new TypeReference<Stack<PlayableCard>>() {});
        } catch (IOException e){
            e.printStackTrace();
        }
        return new PlayableDeck(baseDeck);
    }

}
