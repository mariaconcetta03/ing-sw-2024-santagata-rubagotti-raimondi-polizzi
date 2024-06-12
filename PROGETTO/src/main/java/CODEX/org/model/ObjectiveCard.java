package CODEX.org.model;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents an objective card in the game
 */
public class ObjectiveCard extends Card implements Serializable {
    private int cardPoints; // points the card gives when the player achieves the goal (only one time)
    private Map<AngleType, Integer> resources; // each resource is associated with an int, which
    // indicates the number of resources needed by the objective card
    private Coordinates positionCard1; // the next card of the pattern composed by three cards
    private Coordinates positionCard2; // the third card of the pattern
    private AngleType card0Type; // AngleType and not CentralType because of some problems in counting the resources
    private AngleType card1Type;
    private AngleType card2Type;



    /**
     * Class constructor for the obj cards which require a specific pattern
     * @param id
     * @param cardPoints
     * @param positionCard1
     * @param positionCard2
     * @param card0Type
     * @param card1Type
     * @param card2Type
     */
    public void ObjectiveCardPattern(int id, int cardPoints, Coordinates positionCard1, Coordinates positionCard2, AngleType card0Type, AngleType card1Type, AngleType card2Type) {
        this.setId(id);
        this.cardPoints = cardPoints;
        this.positionCard1 = positionCard1;
        this.positionCard2 = positionCard2;
        this.card0Type = card0Type;
        this.card1Type = card1Type;
        this.card2Type = card2Type;
    }



    /**
     * Class constructor for the obj cards which require a specific number of resources
     * @param id
     * @param cardPoints
     * @param resources
     */
    public void ObjectiveCardNumber(int id, int cardPoints, Map<AngleType, Integer> resources) {
        this.setId(id);
        this.cardPoints = cardPoints;
        this.resources = resources;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ObjectiveCard other = (ObjectiveCard) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        if ((this.getCard0Type() != null) && (other.getCard0Type() != null)) {
            if (this.getCard0Type() != other.getCard0Type()) {
                return false;
            }
        }
        if ((this.getCard1Type() != null) && (other.getCard1Type() != null)) {
            if (this.getCard1Type() != other.getCard1Type()) {
                return false;
            }
        }
        if ((this.getCard2Type() != null) && (other.getCard2Type() != null)) {
            if (this.getCard2Type() != other.getCard2Type()) {
                return false;
            }
        }
        if (this.getCardPoints() != other.getCardPoints()) {
            return false;
        }
        if((this.getPositionCard1()!=null)&&(other.getPositionCard1()!=null)) {
            if (!this.getPositionCard1().equals(other.getPositionCard1())) {
                return false;
            }
        }
        if((this.getPositionCard2()!=null)&&(other.getPositionCard2()!=null)) {
            if (!this.getPositionCard2().equals(other.getPositionCard2())) {
                return false;
            }
        }
        return true;
    }



    @Override
    public int hashCode() {
        final int start = 12;
        int res = 1;
        res = start * res + this.getId();
        res = start * res + 2;
        return res;
    }



    /**
     * This method apply sum to Coordinates object
     * @param c1 first Coordinates object
     * @param c2 second Coordinates object
     * @return a Coordinates representing the sum of c1 and c2
     */
    public Coordinates sumCoordinates(Coordinates c1, Coordinates c2) {
        return new Coordinates(c1.getX() + c2.getX(), c1.getY() + c2.getY());
    }



    /**
     * This method returns the number of points collected thanks to the achievement of this objective
     * The method calculates the minor value in order to satisfy the combination
     * (f.e. if the combination needed is : 1 jar, 1 feather; then it counts the resources on the board
     * f.e.  3 jar, 2 feather --> minResources = 2)
     * @param player the Player which we are checking the objective for
     */
    private void addNumberPointsToPlayer(Player player) {
        int minResources = Integer.MAX_VALUE; // that's an improbable superior limit
        Map<AngleType, Integer> symbolsOnBoard = player.getBoard().getNumResources();

        for (AngleType t : resources.keySet()) { // resources.keySet() will never be empty because we are considering an Objective_card_number
            if (symbolsOnBoard.containsKey(t)) {
                if ((symbolsOnBoard.get(t) / resources.get(t)) < minResources) {
                    // to avoid a division by zero resources doesn't have to contain keys associated with zero
                    minResources = symbolsOnBoard.get(t) / resources.get(t);
                }
            }
        }
        int pointsToBeAdded = 0;
        pointsToBeAdded = minResources * this.getCardPoints();

        // if we completed the objective at least once we take note of that
        if (pointsToBeAdded != 0) {
            player.addNumObjectivesReached();
        }
        // adding the just calculated point to the player
        player.addPoints(pointsToBeAdded);
    }



    /**
     * This method add to player the points he scored completing this specific pattern in his board
     * @param player is the player that has this Objective Card as personal Objective or Common Objective
     */
    private void addPatternPointsToPlayer(Player player) {
        int counter = 0;
        Set<Coordinates> cardsPositions = player.getBoard().getPlayedCards().keySet();
        Set<Coordinates> alreadyCountedPositions = new HashSet<>();
        // temp values to help me directly remove the coordinates if found
        Coordinates temp1 = null;
        Coordinates temp2 = null;

        for (Coordinates center : cardsPositions) {
            // temp values to help me directly write in alreadyCountedPositions the coordinates if found
            temp1 = sumCoordinates(center, positionCard1);
            temp2 = sumCoordinates(center, positionCard2);
            if (!(alreadyCountedPositions.contains(center)) && !(alreadyCountedPositions.contains(temp1)) && !(alreadyCountedPositions.contains(temp2))) {
                // checks the positions of the other 2 cards
                if (cardsPositions.contains(temp1) && cardsPositions.contains(temp2)) {
                    // checks the type of all the 3 cards
                    if (player.getBoard().getPlayedCards().get(temp1).equals(card1Type) &&
                            player.getBoard().getPlayedCards().get(temp2).equals(card2Type) &&
                            player.getBoard().getPlayedCards().get(center).equals(card0Type)) {
                        counter++; // increment the counter
                        alreadyCountedPositions.add(center); // to mark the coordinates already counted in a pattern
                        alreadyCountedPositions.add(temp1);
                        alreadyCountedPositions.add(temp2);
                    }
                }
            }
        }

        int pointsToBeAdded = 0;
        pointsToBeAdded = counter * this.getCardPoints();
        // if we completed the objective at least once we take note of that
        if (pointsToBeAdded != 0) {
            player.addNumObjectivesReached();
        }

        // adding the points just calculated to the player
        player.addPoints(pointsToBeAdded);
    }



    /**
     * This method updates player's points
     * we have to call this method multiple times changing the player if the objective card is shared
     * @param player is the player that has this Objective Card as personal Objective or Common Objective
     */
    public void addPointsToPlayer(Player player) {
        if (this.getId() >= 87 && this.getId() <= 94) {
            addPatternPointsToPlayer(player);
        } else if (this.getId() >= 95 && this.getId() <= 102) {
            addNumberPointsToPlayer(player);
        }
    }



    /**
     * Getter
     * @return cardPoints
     */
    public int getCardPoints() {
        return this.cardPoints;
    }



    /**
     * Getter method
     * @return the coordinates of the first card
     */
    public Coordinates getPositionCard1() {
        return positionCard1;
    }



    /**
     * Getter method
     * @return the coordinates of the second card
     */
    public Coordinates getPositionCard2() {
        return positionCard2;
    }



    /**
     * Getter method
     * @return the type of the card number 0
     */
    public AngleType getCard0Type() {
        return card0Type;
    }



    /**
     * Getter method
     * @return the type of the card number 1
     */
    public AngleType getCard1Type() {
        return card1Type;
    }



    /**
     * Getter method
     * @return the type of the card number 2
     */
    public AngleType getCard2Type() {
        return card2Type;
    }



    /**
     * Setter method
     * @param cardPoints points of the card
     */
    public void setCardPoints(int cardPoints) {
        this.cardPoints = cardPoints;
    }



    /**
     * Setter method
     * @param positionCard1 position of the first card
     */
    public void setPositionCard1(Coordinates positionCard1) {
        this.positionCard1 = positionCard1;
    }



    /**
     * Setter method
     * @param positionCard2 position of the second card
     */
    public void setPositionCard2(Coordinates positionCard2) {
        this.positionCard2 = positionCard2;
    }



    /**
     * Setter method
     * @param card0Type type of zero card
     */
    public void setCard0Type(AngleType card0Type) {
        this.card0Type = card0Type;
    }



    /**
     * Setter method
     * @param card1Type type of first card
     */
    public void setCard1Type(AngleType card1Type) {
        this.card1Type = card1Type;
    }



    /**
     * Setter method
     * @param card2Type type of zero card
     */
    public void setCard2Type(AngleType card2Type) {
        this.card2Type = card2Type;
    }

    public void setResources(Map<AngleType, Integer> resources) {
        this.resources = resources;
    }
}