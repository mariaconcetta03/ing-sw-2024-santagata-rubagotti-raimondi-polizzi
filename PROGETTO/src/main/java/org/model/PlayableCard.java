package org.model;
import java.util.*;

public class PlayableCard extends Card {
    /**
      * we have to add these attributes to PlayableCard because when
      * the Player wants to place a Card, if this is a GoldCard there are
      * some Resources needed (a certain number of one type and a certain
      * number of another type) //
      */

    private int points;
    /**
     *  points the card gives when it's placed (the minimun number)
     * (these are the visible points on the card)
     * here are not counted the other points that can be added (such as
     * when we have a card that adds points for each visible jar in the table)
    */
    private boolean orientation; //true if it's the upper side
    private boolean coverAngleToReceivePoints;
    /**
     *
     * true if covered angles give extra points (when a card is placed we count
     * always at least a covered angle)
     */
    private boolean haveJarToReceivePoints;
    /**
     * true if other jars give extra points. If you check the cards that we can have
     * you'll notice that every card that has this option has also one jar (we can
     * say the same thing for the following two attributes) that means that this
     * kind of card gives always at least the points (only one for what I can see)
     * associated with one jar
    */
    private boolean haveFeatherToReceivePoints; //read the comment above
    private boolean haveScrollToReceivePoints; //read the comment above
    private boolean neededResourcesBoolean;
    /**
     * always true when we are talking about a GoldCard because in this case there
     * are always resources needed
    */
    private Map<ResourceType, Integer> neededResources;
    /**
     * this structure would be used only when the above attribute is true. The
     * key is the type of the resource, the value is the number of that type needed
     */



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

    public void setPoints(int points) {
        this.points = points;
    }

    public Map<ResourceType, Integer> getNeededResources() {
        return neededResources;
    }

    public void setNeededResources(Map<ResourceType, Integer> neededResources) {
        this.neededResources = neededResources;
    }

    public boolean isNeededResourcesBoolean() {
        return neededResourcesBoolean;
    }

    public void setNeededResourcesBoolean(boolean neededResourcesBoolean) {
        this.neededResourcesBoolean = neededResourcesBoolean;
    }

    public boolean isScrollToReceivePoints() {
        return haveScrollToReceivePoints;
    }

    public void setScrollToReceivePoints(boolean haveScrollToReceivePoints) {
        this.haveScrollToReceivePoints = haveScrollToReceivePoints;
    }

    public boolean isFeatherToReceivePoints() {
        return haveFeatherToReceivePoints;
    }

    public void setFeatherToReceivePoints(boolean haveFeatherToReceivePoints) {
        this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
    }

    public boolean isJarToReceivePoints() {
        return haveJarToReceivePoints;
    }

    public void setJarToReceivePoints(boolean haveJarToReceivePoints) {
        this.haveJarToReceivePoints = haveJarToReceivePoints;
    }

    public boolean isCoverAngleToReceivePoints() {
        return coverAngleToReceivePoints;
    }

    public void setCoverAngleToReceivePoints(boolean coverAngleToReceivePoints) {
        this.coverAngleToReceivePoints = coverAngleToReceivePoints;
    }

    public enum ResourceType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        SCROLL,
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
package org.model;
import java.util.*;

public class PlayableCard extends Card {
    /**
      * we have to add these attributes to PlayableCard because when
      * the Player wants to place a Card, if this is a GoldCard there are
      * some Resources needed (a certain number of one type and a certain
      * number of another type) //
      */

    private int points;
    /**
     *  points the card gives when it's placed (the minimun number)
     * (these are the visible points on the card)
     * here are not counted the other points that can be added (such as
     * when we have a card that adds points for each visible jar in the table)
    */
    private boolean orientation; //true if it's the upper side
    private boolean coverAngleToReceivePoints;
    /**
     *
     * true if covered angles give extra points (when a card is placed we count
     * always at least a covered angle)
     */
    private boolean haveJarToReceivePoints;
    /**
     * true if other jars give extra points. If you check the cards that we can have
     * you'll notice that every card that has this option has also one jar (we can
     * say the same thing for the following two attributes) that means that this
     * kind of card gives always at least the points (only one for what I can see)
     * associated with one jar
    */
    private boolean haveFeatherToReceivePoints; //read the comment above
    private boolean haveScrollToReceivePoints; //read the comment above
    private boolean neededResourcesBoolean;
    /**
     * always true when we are talking about a GoldCard because in this case there
     * are always resources needed
    */
    private Map<ResourceType, Integer> neededResources;
    /**
     * this structure would be used only when the above attribute is true. The
     * key is the type of the resource, the value is the number of that type needed
     */



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

    public void setPoints(int points) {
        this.points = points;
    }

    public Map<ResourceType, Integer> getNeededResources() {
        return neededResources;
    }

    public void setNeededResources(Map<ResourceType, Integer> neededResources) {
        this.neededResources = neededResources;
    }

    public boolean isNeededResourcesBoolean() {
        return neededResourcesBoolean;
    }

    public void setNeededResourcesBoolean(boolean neededResourcesBoolean) {
        this.neededResourcesBoolean = neededResourcesBoolean;
    }

    public boolean isScrollToReceivePoints() {
        return haveScrollToReceivePoints;
    }

    public void setScrollToReceivePoints(boolean haveScrollToReceivePoints) {
        this.haveScrollToReceivePoints = haveScrollToReceivePoints;
    }

    public boolean isFeatherToReceivePoints() {
        return haveFeatherToReceivePoints;
    }

    public void setFeatherToReceivePoints(boolean haveFeatherToReceivePoints) {
        this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
    }

    public boolean isJarToReceivePoints() {
        return haveJarToReceivePoints;
    }

    public void setJarToReceivePoints(boolean haveJarToReceivePoints) {
        this.haveJarToReceivePoints = haveJarToReceivePoints;
    }

    public boolean isCoverAngleToReceivePoints() {
        return coverAngleToReceivePoints;
    }

    public void setCoverAngleToReceivePoints(boolean coverAngleToReceivePoints) {
        this.coverAngleToReceivePoints = coverAngleToReceivePoints;
    }

    public enum ResourceType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        SCROLL,
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
