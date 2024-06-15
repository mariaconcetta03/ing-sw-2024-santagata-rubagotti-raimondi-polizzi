package CODEX.org.model;

import java.io.Serializable;

/**
 * This abstract class represents a general card with his id
 */

public abstract class Card implements Serializable {
    private int id; // each card has a different ID

    /**
     * Default constructor
     */
    public Card(){}

    /**
     * Getter method
     * @return the id of a card
     */
    public int getId() {
        return id;
    }



    /**
     * Setter method
     * @param id of the card
     */
    public void setId (int id) {
        this.id = id;
    }

}