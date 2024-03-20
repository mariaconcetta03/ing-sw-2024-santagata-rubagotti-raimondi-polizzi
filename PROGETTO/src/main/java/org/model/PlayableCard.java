package org.model;

public class PlayableCard extends Card {
    private int points; // points the card gives when it's placed
    private boolean orientation; // true if it's the upper side

    public ResourceType getCardType() {
        return cardType;
    }

    public void setCardType(ResourceType cardType) {
        this.cardType = cardType;
    }

    public void setFront_down_right(ResourceType front_down_right) {
        this.front_down_right = front_down_right;
    }

    public void setFront_up_left(ResourceType front_up_left) {
        this.front_up_left = front_up_left;
    }

    public void setFront_up_right(ResourceType front_up_right) {
        this.front_up_right = front_up_right;
    }

    public void setFront_down_left(ResourceType front_down_left) {
        this.front_down_left = front_down_left;
    }

    public enum ResourceType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        PARCHMENT,
        FEATHER,
        JAR,
        NO_RESOURCE,
        ABSENT
    }

    // these variables contains the type of resource present in a specific corner of the card
    private ResourceType front_up_right;
    private ResourceType front_up_left;
    private ResourceType front_down_right;
    private ResourceType front_down_left;
    private ResourceType cardType;
    private Coordinates position;


    public int getPoints() {
        return points;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public ResourceType get_front_up_right() {
        return front_up_right;
    }

    public ResourceType get_front_up_left() {
        return front_up_left;
    }

    public ResourceType get_front_down_right() {
        return front_down_right;
    }

    public ResourceType get_front_down_left() {
        return front_down_left;
    }

    public Coordinates getPosition() {
        return position;
    }
    public void setPosition(Coordinates position) {
        this.position = position;
    }

}
