package org.model;

import org.json.simple.parser.JSONParser;

import java.io.Serializable;

/**
 * This abstract class represents a general card with his id
 */

public abstract class Card implements Serializable {
    private int id; // each card has a different ID

    /**
     * Getter method
     * @return the id of a card
     */
    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

}