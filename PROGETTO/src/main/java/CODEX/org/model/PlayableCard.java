package CODEX.org.model;

import java.io.Serializable;
import java.util.*;


/**
 *  This class represents a PlayableCard which can be one of the resource, gold or base ones
 */
public class PlayableCard extends Card implements Serializable {

    /**
      * we have to add these attributes to PlayableCard because when
      * the Player wants to place a Card, if this is a GoldCard there are
      * some Resources needed (a certain number of one type and a certain
      * number of another type)
      */
    private int points;
    /**
     *  points the card gives when it's placed (the minimun number)
     * (these are the visible points on the card)
     * here are not counted the other points that can be added (such as
     * when we have a card that adds points for each visible jar in the table)
     */
    private int playOrder = 0;

    // these variables contain the type of resource present in a specific corner of the card
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
    private boolean orientation; //true if it's the upper side, false otherwise
    private boolean coverAngleToReceivePoints;

     /** true if covered angles give extra points (when a card is placed we count
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
    private Map<AngleType, Integer> neededResources;
    /**
     * this structure would be used only when the above attribute is true. The
     * key is the type of the resource, the value is the number of that type needed
     */



    /**
     * Class constructor
     * This method creates an instance of different types of playableCards
     */
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



    /**
     * Class constructor
     * This method creates an instance of different types of playableCards
     * @param id cardID
     * @param points card's points
     * @param front_up_right
     * @param front_up_left
     * @param front_down_right
     * @param front_down_left
     * @param back_up_right
     * @param back_up_left
     * @param back_down_right
     * @param back_down_left
     * @param centralResources
     * @param coverAngleToReceivePoints
     * @param haveFeatherToReceivePoints
     * @param haveScrollToReceivePoints
     * @param haveJarToReceivePoints
     * @param neededResources
     */
    public PlayableCard (int id, int points, AngleType front_up_right, AngleType front_up_left, AngleType front_down_right, AngleType front_down_left,
                         AngleType back_up_right, AngleType back_up_left, AngleType back_down_right, AngleType back_down_left,
                         List<AngleType> centralResources, boolean coverAngleToReceivePoints, boolean haveFeatherToReceivePoints,
                         boolean haveScrollToReceivePoints, boolean haveJarToReceivePoints,
                         Map <AngleType, Integer> neededResources) {
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



  @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayableCard other = (PlayableCard) obj;
        if ( this.getId()!= other.getId()) {
            return false;
        }
        if (this.get_front_up_right() != other.get_front_up_right()) {
            return false;
        }
        if (this.get_front_down_left() != other.get_front_down_left()) {
            return false;
        }
        if (this.get_front_up_left() != other.get_front_up_left()) {
            return false;
        }
        if (this.get_front_down_right() != other.get_front_down_right()) {
            return false;
        }
        if (this.get_back_up_right() != other.get_back_up_right()) {
            return false;
        }
        if (this.get_back_up_left() != other.get_back_up_left()) {
            return false;
        }
        if (this.get_back_down_left() != other.get_back_down_left()) {
            return false;
        }
        if (this.get_back_down_right() != other.get_back_down_right()) {
            return false;
        }
        if (this.getPoints() != other.getPoints()) {
            return false;
        }
        return true;
    }



    @Override
    public int hashCode() {
        final int start=31;
        int res=1;
        res=start*res+this.getId();
        res=start*res+2;
        return res;
    }



    /**
     * Getter method
     * @return central resources
     */
    public List<AngleType> getCentralResources() {
        return centralResources;
    }



    /**
     * Getter method
     * @return card's points
     */
    public int getPoints() {
        return points;
    }



    /**
     * Getter method
     * @return the playing order of this card
     */
    public int getPlayOrder() {
        return playOrder;
    }



    /**
     * Getter method
     * @return the orientation
     */
    public boolean getOrientation() {
        return orientation;
    }



    /**
     * Getter method
     * @return front up right angletype
     */
    public AngleType get_front_up_right() {
        return front_up_right;
    }



    /**
     * Getter method
     * @return front up left angletype
     */
    public AngleType get_front_up_left() {
        return front_up_left;
    }



    /**
     * Getter method
     * @return front down right angletype
     */
    public AngleType get_front_down_right() {
        return front_down_right;
    }



    /**
     * Getter method
     * @return front down left angletype
     */
    public AngleType get_front_down_left() {
        return front_down_left;
    }



    /**
     * Getter method
     * @return back up right angletype
     */
    public AngleType get_back_up_right() {
        return back_up_right;
    }



    /**
     * Getter method
     * @return back up left angletype
     */
    public AngleType get_back_up_left() {
        return back_up_left;
    }



    /**
     * Getter method
     * @return back down right angletype
     */
    public AngleType get_back_down_right() {
        return back_down_right;
    }



    /**
     * Getter method
     * @return back down left angletype
     */
    public AngleType get_back_down_left() {
        return back_down_left;
    }



    /**
     * Getter method
     * @return card's position (coordinates)
     */
    public Coordinates getPosition() {
        return position;
    }



    /**
     * Setter method
     * @param playOrder is the playing order of the player in the game
     */
    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }



    /**
     * Setter method
     * @param orientation is true when the card is played upfront, false otherwise
     */
    public void setOrientation(boolean orientation) {
             this.orientation = orientation;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setFront_up_right(AngleType front_up_right) {
        this.front_up_right = front_up_right;
    }

    public void setFront_up_left(AngleType front_up_left) {
        this.front_up_left = front_up_left;
    }

    public void setFront_down_right(AngleType front_down_right) {
        this.front_down_right = front_down_right;
    }

    public void setFront_down_left(AngleType front_down_left) {
        this.front_down_left = front_down_left;
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

    public void setCentralResources(List<AngleType> centralResources) {
        this.centralResources = centralResources;
    }

    public void setCoverAngleToReceivePoints(boolean coverAngleToReceivePoints) {
        this.coverAngleToReceivePoints = coverAngleToReceivePoints;
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

    public void setNeededResources(Map<AngleType, Integer> neededResources) {
        this.neededResources = neededResources;
    }

    /**
     * Setter method
     * @param position of the card when it's played
     */
    public void setPosition(Coordinates position) {
             this.position = position;
    }



    /**
     * Getter method
     * @return neededResources which are the resources needed to play a card
     */
    public Map<AngleType, Integer> getNeededResources() {
           return neededResources;
    }



    /**
     * Getter method
     * @return haveScrollToReceivePoints is true if you need a scroll to make points, false otherwise
     */
    public boolean isScrollToReceivePoints() {
        return haveScrollToReceivePoints;
    }



    /**
     * Getter method
     * @return haveFeatherToReceivePoints is true if you need a feather to make points, false otherwise
     */
    public boolean isFeatherToReceivePoints() {
        return haveFeatherToReceivePoints;
    }



    /**
     * Getter method
     * @return haveJarToReceivePoints is true if you need a jar to make points, false otherwise
     */
    public boolean isJarToReceivePoints() {
        return haveJarToReceivePoints;
    }



    /**
     * Getter method
     * @return true if you need to cover n angles to receive points, false otherwise
     */
    public boolean isCoverAngleToReceivePoints() {
        return coverAngleToReceivePoints;
    }

}
