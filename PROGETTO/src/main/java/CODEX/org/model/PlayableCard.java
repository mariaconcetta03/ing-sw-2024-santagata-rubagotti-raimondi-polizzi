package CODEX.org.model;

import java.io.Serializable;
import java.util.*;


/**
 * This class represents a PlayableCard which can be one of the resource, gold or base ones
 */
public class PlayableCard extends Card implements Serializable {

    private int points;
    private int playOrder = 0;

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
    private boolean orientation; // true if it's the upper side, false otherwise

    private boolean coverAngleToReceivePoints;
    private boolean haveJarToReceivePoints;
    private boolean haveFeatherToReceivePoints;
    private boolean haveScrollToReceivePoints;

    private Map<AngleType, Integer> neededResources;


    /**
     * Class constructor
     * This method creates an instance of different types of playableCards
     */
    public PlayableCard() {
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
        this.neededResources = null;
    }


    /**
     * Class constructor
     * This method creates an instance of different types of playableCards
     *
     * @param id                         cardID
     * @param points                     card's points
     * @param front_up_right             is the resource in the specified corner
     * @param front_up_left              is the resource in the specified corner
     * @param front_down_right           is the resource in the specified corner
     * @param front_down_left            is the resource in the specified corner
     * @param back_up_right              is the resource in the specified corner
     * @param back_up_left               is the resource in the specified corner
     * @param back_down_right            is the resource in the specified corner
     * @param back_down_left             is the resource in the specified corner
     * @param centralResources           are the resources in the center of the card
     * @param coverAngleToReceivePoints  if it gives points related to the angles covered when placed
     * @param haveFeatherToReceivePoints if it gives points related to Feather count when placed
     * @param haveScrollToReceivePoints  if it gives points related to Scroll count when placed
     * @param haveJarToReceivePoints     if it gives points related to Jar count when placed
     * @param neededResources            are the resources needed to place the card
     */
    public PlayableCard(int id, int points, AngleType front_up_right, AngleType front_up_left, AngleType front_down_right, AngleType front_down_left,
                        AngleType back_up_right, AngleType back_up_left, AngleType back_down_right, AngleType back_down_left,
                        List<AngleType> centralResources, boolean coverAngleToReceivePoints, boolean haveFeatherToReceivePoints,
                        boolean haveScrollToReceivePoints, boolean haveJarToReceivePoints,
                        Map<AngleType, Integer> neededResources) {
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
        this.orientation = true;
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
        if (this.getId() != other.getId()) {
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
        final int start = 31;
        int res = 1;
        res = start * res + this.getId();
        res = start * res + 2;
        return res;
    }


    /**
     * Getter method
     *
     * @return central resources
     */
    public List<AngleType> getCentralResources() {
        return centralResources;
    }


    /**
     * Getter method
     *
     * @return card's points
     */
    public int getPoints() {
        return points;
    }


    /**
     * Getter method
     *
     * @return the playing order of this card
     */
    public int getPlayOrder() {
        return playOrder;
    }


    /**
     * Getter method
     *
     * @return the orientation of the card
     */
    public boolean getOrientation() {
        return orientation;
    }


    /**
     * Getter method
     *
     * @return front up right angletype
     */
    public AngleType get_front_up_right() {
        return front_up_right;
    }


    /**
     * Getter method
     *
     * @return front up left angletype
     */
    public AngleType get_front_up_left() {
        return front_up_left;
    }


    /**
     * Getter method
     *
     * @return front down right angletype
     */
    public AngleType get_front_down_right() {
        return front_down_right;
    }


    /**
     * Getter method
     *
     * @return front down left angletype
     */
    public AngleType get_front_down_left() {
        return front_down_left;
    }


    /**
     * Getter method
     *
     * @return back up right angletype
     */
    public AngleType get_back_up_right() {
        return back_up_right;
    }


    /**
     * Getter method
     *
     * @return back up left angletype
     */
    public AngleType get_back_up_left() {
        return back_up_left;
    }


    /**
     * Getter method
     *
     * @return back down right angletype
     */
    public AngleType get_back_down_right() {
        return back_down_right;
    }


    /**
     * Getter method
     *
     * @return back down left angletype
     */
    public AngleType get_back_down_left() {
        return back_down_left;
    }


    /**
     * Getter method
     *
     * @return card's position (coordinates)
     */
    public Coordinates getPosition() {
        return position;
    }


    /**
     * Setter method
     *
     * @param playOrder is the playing order of the player in the game
     */
    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }


    /**
     * Setter method
     *
     * @param orientation is true when the card is played upfront, false otherwise
     */
    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }


    /**
     * Setter method
     *
     * @param points of the card
     */
    public void setPoints(int points) {
        this.points = points;
    }


    /**
     * Setter method
     *
     * @param front_up_right which is an angle of the card
     */
    public void setFront_up_right(AngleType front_up_right) {
        this.front_up_right = front_up_right;
    }


    /**
     * Setter method
     *
     * @param front_up_left which is an angle of the card
     */
    public void setFront_up_left(AngleType front_up_left) {
        this.front_up_left = front_up_left;
    }


    /**
     * Setter method
     *
     * @param front_down_right which is an angle of the card
     */
    public void setFront_down_right(AngleType front_down_right) {
        this.front_down_right = front_down_right;
    }


    /**
     * Setter method
     *
     * @param front_down_left which is an angle of the card
     */
    public void setFront_down_left(AngleType front_down_left) {
        this.front_down_left = front_down_left;
    }

    /**
     * Setter method
     *
     * @param back_up_right which is an angle of the card
     */
    public void setBack_up_right(AngleType back_up_right) {
        this.back_up_right = back_up_right;
    }

    /**
     * Setter method
     *
     * @param back_up_left which is an angle of the card
     */
    public void setBack_up_left(AngleType back_up_left) {
        this.back_up_left = back_up_left;
    }

    /**
     * Setter method
     *
     * @param back_down_right which is an angle of the card
     */
    public void setBack_down_right(AngleType back_down_right) {
        this.back_down_right = back_down_right;
    }

    /**
     * Setter method
     *
     * @param back_down_left which is an angle of the card
     */
    public void setBack_down_left(AngleType back_down_left) {
        this.back_down_left = back_down_left;
    }

    /**
     * Setter method
     *
     * @param centralResources of the card
     */
    public void setCentralResources(List<AngleType> centralResources) {
        this.centralResources = centralResources;
    }

    /**
     * Setter method
     *
     * @param coverAngleToReceivePoints which is true if needed covered angles to receive points, false otherwise
     */
    public void setCoverAngleToReceivePoints(boolean coverAngleToReceivePoints) {
        this.coverAngleToReceivePoints = coverAngleToReceivePoints;
    }

    /**
     * Setter method
     *
     * @param haveJarToReceivePoints which is true if needed a jar to receive points, false otherwise
     */
    public void setHaveJarToReceivePoints(boolean haveJarToReceivePoints) {
        this.haveJarToReceivePoints = haveJarToReceivePoints;
    }

    /**
     * Setter method
     *
     * @param haveFeatherToReceivePoints which is true if needed a feather to receive points, false otherwise
     */
    public void setHaveFeatherToReceivePoints(boolean haveFeatherToReceivePoints) {
        this.haveFeatherToReceivePoints = haveFeatherToReceivePoints;
    }

    /**
     * Setter method
     *
     * @param haveScrollToReceivePoints which is true if needed a scroll to receive points, false otherwise
     */
    public void setHaveScrollToReceivePoints(boolean haveScrollToReceivePoints) {
        this.haveScrollToReceivePoints = haveScrollToReceivePoints;
    }

    /**
     * Setter method
     *
     * @param neededResources to make points
     */
    public void setNeededResources(Map<AngleType, Integer> neededResources) {
        this.neededResources = neededResources;
    }

    /**
     * Setter method
     *
     * @param position of the card when it's played
     */
    public void setPosition(Coordinates position) {
        this.position = position;
    }


    /**
     * Getter method
     *
     * @return neededResources which are the resources needed to play a card
     */
    public Map<AngleType, Integer> getNeededResources() {
        return neededResources;
    }


    /**
     * Getter method
     *
     * @return haveScrollToReceivePoints is true if you need a scroll to make points, false otherwise
     */
    public boolean isScrollToReceivePoints() {
        return haveScrollToReceivePoints;
    }


    /**
     * Getter method
     *
     * @return haveFeatherToReceivePoints is true if you need a feather to make points, false otherwise
     */
    public boolean isFeatherToReceivePoints() {
        return haveFeatherToReceivePoints;
    }


    /**
     * Getter method
     *
     * @return haveJarToReceivePoints is true if you need a jar to make points, false otherwise
     */
    public boolean isJarToReceivePoints() {
        return haveJarToReceivePoints;
    }


    /**
     * Getter method
     *
     * @return true if you need to cover n angles to receive points, false otherwise
     */
    public boolean isCoverAngleToReceivePoints() {
        return coverAngleToReceivePoints;
    }

}
