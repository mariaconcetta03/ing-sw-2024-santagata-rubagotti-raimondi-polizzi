package org.model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.json.JsonParserType;
import groovyjarjarasm.asm.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 *  This class represents a deck: a group of cards of the same type
 */


public class Deck {
    private Stack<PlayableCard> cards; // contains all the cards in this deck



    /**
     * Class constructor
     * creates a new deck, allocating space for a stack
     */
    public Deck(Stack<PlayableCard> stack) {
        cards = stack;
    }




    /**
     * This method adds a card to the deck
     * @param card you want to add to the deck
     */
    public void addCard (PlayableCard card) {
        cards.add(card);
    }




    /**
     * This method creates a goldDeck with all his 40 cards
     * @return goldDeck
     */
    public Deck goldDeck() throws IOException {
        Stack<PlayableCard> goldDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             goldDeck = objectMapper.readValue(new File("src/main/java/org/model/jsons/goldCards.json"), new TypeReference<Stack<PlayableCard>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Deck(goldDeck);
    }




    /**
     * This method creates a resourceDeck with all his 40 cards
     * @return resourceDeck
     */
    public Deck resourceDeck() throws IOException {
        Stack<PlayableCard> resourceDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            resourceDeck = objectMapper.readValue(new File("src/main/java/org/model/jsons/resourceCards.json"), new TypeReference<Stack<PlayableCard>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Deck(resourceDeck);
    }




    /**
     * This method creates a baseDeck with all his 6 cards
     * @return baseDeck
     */
    public Deck baseDeck() throws IOException {
        Stack<PlayableCard> baseDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            baseDeck = objectMapper.readValue(new File("src/main/java/org/model/jsons/baseCards.json"), new TypeReference<Stack<PlayableCard>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Deck(baseDeck);
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
