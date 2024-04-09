package org.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 *  This class represents a deck: a group of cards of the same type
 */


public class PlayableDeck extends Deck{
    private Stack<PlayableCard> cards; // contains all the cards in this deck



    /**
     * Class constructor
     * creates a new deck, allocating space for a stack
     */
    public PlayableDeck(Stack<PlayableCard> stack) {
        cards = stack;
    }


    /**
     * Class constructor
     * creates a new deck, with a null stack
     */
    public PlayableDeck() {
        cards = null;
    }


    /**
     * This method adds a card to the deck
     * @param card you want to add to the deck
     */
    public void addCard (PlayableCard card) {
        cards.add(card);
    }



    /**
     * This method returns the first card in the deck
     * @return the first card in the deck
     */
    public PlayableCard getFirstCard() { // returns the first card on the deck, for example when the player needs to pick it up
        return cards.pop();
    }


    /**
     * This method creates a goldDeck with all his 40 cards
     * @return goldDeck
     */
    public static PlayableDeck goldDeck(){
        Stack<PlayableCard> goldDeck = new Stack<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file= new File("src/main/java/org/model/jsons/goldCards.json");
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
        File file= new File(".\\jsons\\resourceCards.json");
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
        File file= new File(".\\jsons\\baseCards.json");
        try{
            baseDeck=objectMapper.readValue(file, new TypeReference<Stack<PlayableCard>>() {});
        } catch (IOException e){
            e.printStackTrace();
        }
        return new PlayableDeck(baseDeck);
    }

}
