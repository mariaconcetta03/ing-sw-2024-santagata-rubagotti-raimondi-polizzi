package org.server;

public class Playable_Card extends Card {
    private int points;
    private boolean orientation; //true if it's the upper side
    private enum ResourceType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        NATURE_FUNGI,
        ANIMAL_INSECT,
        ANIMAL_INSECT_NATURE,
        NATURE_ANIMAL_FUNGI,
        PARCHMENT,
        FEATHER,
        JAR,
        NO_RESOURCE,
        ABSENT
    }
    private ResourceType front_up_right;
    private ResourceType front_up_left;
    private ResourceType front_down_right;
    private ResourceType front_down_left;
    private ResourceType back_up_right;
    private ResourceType back_up_left;
    private ResourceType back_down_right;
    private ResourceType back_down_left;
    private ResourceType back_center;
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

    public ResourceType get_back_up_right() {
        return back_up_right;
    }

    public ResourceType get_back_up_left() {
        return back_up_left;
    }

    public ResourceType get_back_down_right() {
        return back_down_right;
    }

    public ResourceType get_back_down_left() {
        return back_down_left;
    }

    public ResourceType get_back_center() {
        return back_center;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

}
