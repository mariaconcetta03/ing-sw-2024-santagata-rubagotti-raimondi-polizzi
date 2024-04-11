package org.model;

import java.util.*;
import java.util.zip.CheckedOutputStream;

public class ObjectiveCard extends Card {
        private int cardPoints; // points the card gives when the player achieves the goal (only one time)
        private Map<CountingType, Integer> resources; // each resource is associated with an int, which
        // indicates the number of resources needed by the objective card
        private Coordinates positionCard1; //the next card of the pattern composed by three cards
        private Coordinates positionCard2; //the third card of the pattern
        private AngleType card0Type; //AngleType and not CentralType because of some problems in counting the resources
        private AngleType card1Type;
        private AngleType card2Type;

        /**
         * Class constructor
         * @param cardPoints
         * @param resources
         * @param positionCard1
         * @param positionCard2
         * @param card0Type
         * @param card1Type
         * @param card2Type
         */
        public ObjectiveCard(int id, int cardPoints, Map<CountingType, Integer> resources, Coordinates positionCard1,Coordinates positionCard2,AngleType card0Type,AngleType card1Type,AngleType card2Type){
            this.setId(id);
            this.cardPoints = cardPoints;
            this.resources = resources;
            this.positionCard1 = positionCard1;
            this.positionCard2 = positionCard2;
            this.card0Type = card0Type;
            this.card1Type = card1Type;
            this.card2Type = card2Type;
        }



    /**
     * Class constructor
     */
    public ObjectiveCard(){
        this.setId(0);
        this.cardPoints = 0;
        this.resources = null; //there is no copy
        this.positionCard1 = null;
        this.positionCard2 = null;
        this.card0Type = null;
        this.card1Type = null;
        this.card2Type = null;
    }




        /**
         * This method apply sum to Coordinates object
         * @param c1 first Coordinates object
         * @param c2 second Coordinates object
         * @return a Coordinates representing the sum of c1 and c2
         */
        public Coordinates sumCoordinates(Coordinates c1, Coordinates c2){
            return new Coordinates(c1.getX()+c2.getX(), c1.getY()+c2.getY());
        }



        /**
         * This method returns the number of points collected thanks to the achievement of this objective
         * The method calculates the minor value in order to satisfy the combination
         * (f.e. if the combination needed is : 1 jar, 1 feather; then it counts the resources on the board
         * f.e.  3 jar, 2 feather --> minResources = 2)
         * @param player the Player which we are checking the objective for
         */
        private int addNumberPointsToPlayer(Player player) {
            int minResources = Integer.MAX_VALUE; //that's an improbable superior limit
            Map<AngleType,Integer> symbolsOnBoard=player.getBoard().getNumResources();
            //at least once we have to enter this cycle and we don't know the order of the keys selection
            for(CountingType t : resources.keySet()){ //resources.keySet() will never be empty because we are considering an Objective_card_number
                if((symbolsOnBoard.get(t)/resources.get(t))<minResources){ //to avoid a division by zero resources doesn't have to contain keys associated with zero
                    minResources= symbolsOnBoard.get(t)/resources.get(t);
                }
            }
            int pointsToBeAdded=0;
            pointsToBeAdded=minResources*this.getCardPoints();

            //if we completed the objective at least once we take note of that
            if (pointsToBeAdded!=0){
                player.addNumObjectivesReached();
            }
            //adding the just calculated point to the player
            player.addPoints(pointsToBeAdded);
            return minResources* this.getCardPoints(); //we can use this in the test
        }


    /**
     * This method add to player the points he scored completing this specific pattern in his board
     * @param player is the player that has this Objective Card as personal Objective or Common Objective
     */
    private int addPatternPointsToPlayer(Player player) {
        int counter=0;
        //creates a Set view of the keyset of the player's Board
        Set<Coordinates> cardsPositions=player.getBoard().getPlayedCards().keySet(); //this Set doesn't contain the base card (which has no color)
        Set<Coordinates> alreadyCountedPositions= new HashSet<>();

        for(Coordinates center : cardsPositions){
            //temp values to help me directly write in alreadyCountedPositions the coordinates if found
            Coordinates temp1= sumCoordinates(center,positionCard1);
            Coordinates temp2= sumCoordinates(center,positionCard2);
            if(!(alreadyCountedPositions.contains(center))&&!(alreadyCountedPositions.contains(temp1))&&!(alreadyCountedPositions.contains(temp2))) {
                //checks the positions of the other 2 cards
                if (cardsPositions.contains(temp1) && cardsPositions.contains(temp2)) {
                    //checks the type of all the 3 cards
                    if (player.getBoard().getPlayedCards().get(temp1).equals(card1Type) &&
                            player.getBoard().getPlayedCards().get(temp2).equals(card2Type) &&
                            player.getBoard().getPlayedCards().get(center).equals(card0Type)) {
                        counter++; //increment the counter
                        alreadyCountedPositions.add(center); //to mark the coordinates already counted in a pattern
                        alreadyCountedPositions.add(temp1);
                        alreadyCountedPositions.add(temp2);
                    }
                }
            }
        }
        int pointsToBeAdded=0;
        pointsToBeAdded=counter*this.getCardPoints();
        //if we completed the objective at least once we take note of that
        if (pointsToBeAdded!=0){
            player.addNumObjectivesReached();
        }
        //adding the points just calculated to the player
        player.addPoints(pointsToBeAdded);
        return counter * this.getCardPoints(); //this can be useful for the test
    }




        /** This method updates player's points
         * we have to call this method multiple times changing the player if the objective card is shared
         */
        public int addPointsToPlayer(Player player) {
            int addedPoints = 0;
            if (this.getId() >= 87 && this.getId() <= 94) {
                addedPoints = addPatternPointsToPlayer(player);
            } else if (this.getId() >= 95 && this.getId() <= 102) {
                addedPoints = addNumberPointsToPlayer(player);
            }
            return addedPoints;
        }




        /**
         * Getter
         * @return cardPoints
         */
        public int getCardPoints(){
                return this.cardPoints;
            }

    public void setCardPoints(int cardPoints) {
        this.cardPoints = cardPoints;
    }

    public Map<CountingType, Integer> getResources() {
        return resources;
    }

    public void setResources(Map<CountingType, Integer> resources) {
        this.resources = resources;
    }

    public Coordinates getPositionCard1() {
        return positionCard1;
    }

    public void setPositionCard1(Coordinates positionCard1) {
        this.positionCard1 = positionCard1;
    }

    public Coordinates getPositionCard2() {
        return positionCard2;
    }

    public void setPositionCard2(Coordinates positionCard2) {
        this.positionCard2 = positionCard2;
    }

    public AngleType getCard0Type() {
        return card0Type;
    }

    public void setCard0Type(AngleType card0Type) {
        this.card0Type = card0Type;
    }

    public AngleType getCard1Type() {
        return card1Type;
    }

    public void setCard1Type(AngleType card1Type) {
        this.card1Type = card1Type;
    }

    public AngleType getCard2Type() {
        return card2Type;
    }

    public void setCard2Type(AngleType card2Type) {
        this.card2Type = card2Type;
    }
}