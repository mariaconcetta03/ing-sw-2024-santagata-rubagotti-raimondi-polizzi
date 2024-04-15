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

    public PlayableCard(){
        this.points = -1;
        this.front_down_left = null;
        this.front_up_left = null;
        this.front_down_right = null;
        this.front_up_right = null;
        this.back_down_left = null;
        this.back_down_right = null;
        this.back_up_left = null;
        this.back_up_right = null;
        this.centralResources = null;
        this.coverAngleToReceivePoints = false;
        this.haveFeatherToReceivePoints = false;
        this.haveJarToReceivePoints = false;
        this.haveScrollToReceivePoints = false;
        this.neededResources =null;
    }
    public PlayableCard (int id, int points, AngleType front_up_right, AngleType front_up_left, AngleType front_down_right, AngleType front_down_left,
                         AngleType back_up_right, AngleType back_up_left, AngleType back_down_right, AngleType back_down_left,
                         List<AngleType> centralResources, boolean coverAngleToReceivePoints, boolean haveFeatherToReceivePoints,
                         boolean haveScrollToReceivePoints, boolean haveJarToReceivePoints,
                         Map <CentralType, Integer> neededResources) {
        this.setId(id);
        this.points = points;
        this.front_down_left = front_down_left;
        this.front_up_left = front_up_left;
        this.front_down_right = front_down_right;
        this.front_up_right = front_up_right;
        this.back_down_left = back_down_left;
        this.back_down_right = back_down_right;
        this.back_up_left = back_up_left;
        this.back_up_right = back_up_right;
        this.centralResources = centralResources;
        this.coverAngleToReceivePoints = coverAngleToReceivePoints;
        this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
        this.haveJarToReceivePoints = haveJarToReceivePoints;
        this.haveScrollToReceivePoints = haveScrollToReceivePoints;
        this.neededResources = neededResources;
        this.orientation=true;
    }



    // these variables contains the type of resource present in a specific corner of the card
    private AngleType front_up_right;
    private AngleType front_up_left;
    private AngleType front_down_right;
    private AngleType front_down_left;
    private AngleType back_up_right;
    private AngleType back_up_left;
    private AngleType back_down_right;
    private AngleType back_down_left;
    private List<AngleType> centralResources;

    private Coordinates position;

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
    private Map<CentralType, Integer> neededResources;
    /**
     * this structure would be used only when the above attribute is true. The
     * key is the type of the resource, the value is the number of that type needed
     */

    ///SETTER
    public void setCentralResources (List<AngleType> centralResources) {
        this.centralResources = centralResources;
    }

    public void setFront_down_right(AngleType front_down_right) {
        this.front_down_right = front_down_right;
    }

    public void setFront_up_left(AngleType front_up_left) {
        this.front_up_left = front_up_left;
    }

    public void setFront_up_right(AngleType front_up_right) {
        this.front_up_right = front_up_right;
    }

    public void setFront_down_left(AngleType front_down_left) {
        this.front_down_left = front_down_left;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setNeededResources(Map<CentralType, Integer> neededResources) {
        this.neededResources = neededResources;
    }

    public void setScrollToReceivePoints(boolean haveScrollToReceivePoints) {
           this.haveScrollToReceivePoints = haveScrollToReceivePoints;
    }

    public void setFeatherToReceivePoints(boolean haveFeatherToReceivePoints) {
            this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
    }

    public void setJarToReceivePoints(boolean haveJarToReceivePoints) {
            this.haveJarToReceivePoints = haveJarToReceivePoints;
    }

    public void setCoverAngleToReceivePoints(boolean coverAngleToReceivePoints) {
            this.coverAngleToReceivePoints = coverAngleToReceivePoints;
    }

    public void setOrientation(boolean orientation) {
             this.orientation = orientation;
    }

    public void setPosition(Coordinates position) {
             this.position = position;
    }

    //GETTER
    public Map<CentralType, Integer> getNeededResources() {
           return neededResources;
    }

    public boolean isScrollToReceivePoints() {
        return haveScrollToReceivePoints;
    }

    public boolean isFeatherToReceivePoints() {
        return haveFeatherToReceivePoints;
    }

    public boolean isJarToReceivePoints() {
        return haveJarToReceivePoints;
    }

    public boolean isCoverAngleToReceivePoints() {
        return coverAngleToReceivePoints;
    }

    public List<AngleType> getCentralResources() {
        return centralResources;
    }

    public int getPoints() {
        return points;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public AngleType get_front_up_right() {
        return front_up_right;
    }

    public AngleType get_front_up_left() {
        return front_up_left;
    }

    public AngleType get_front_down_right() {
        return front_down_right;
    }

    public AngleType get_front_down_left() {
        return front_down_left;
    }

    public AngleType get_back_up_right() {
        return back_up_right;
    }

    public AngleType get_back_up_left() {
        return back_up_left;
    }

    public AngleType get_back_down_right() {
        return back_down_right;
    }

    public AngleType get_back_down_left() {
        return back_down_left;
    }

    public Coordinates getPosition() {
        return position;
    }


    public void setBack_up_right(AngleType back_up_right) {
        this.back_up_right = back_up_right;
    }

    public void setBack_up_left(AngleType back_up_left) {
        this.back_up_left = back_up_left;
    }

    public void setBack_down_right(AngleType back_down_right) {
        this.back_down_right = back_down_right;
    }

    public void setBack_down_left(AngleType back_down_left) {
        this.back_down_left = back_down_left;
    }

    public void setHaveJarToReceivePoints(boolean haveJarToReceivePoints) {
        this.haveJarToReceivePoints = haveJarToReceivePoints;
    }

    public void setHaveFeatherToReceivePoints(boolean haveFeatherToReceivePoints) {
        this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
    }

    public void setHaveScrollToReceivePoints(boolean haveScrollToReceivePoints) {
        this.haveScrollToReceivePoints = haveScrollToReceivePoints;
    }
}
