package org.model;

import java.util.*;

public class Objective_card_pattern extends ObjectiveCard{
    private Coordinates positionCard1; //the next card of the pattern composed by three cards
    private Coordinates positionCard2; //the third card of the pattern
    private AngleType card0Type; //AngleType and not CentralType because of some problems in counting the resources
    private AngleType card1Type;
    private AngleType card2Type;

    /**
     * Class constructor
     * @param points the card gives for each time the Player completes the pattern
     */
    public Objective_card_pattern(int points) {
        super(points);
    }

    /**
     * Class constructor
     * @param points the card gives for each time the Player completes the pattern
     */
    public Objective_card_pattern(int points,Coordinates positionCard1,Coordinates positionCard2,AngleType card0Type,AngleType card1Type,AngleType card2Type) {
        super(points);
        this.positionCard1=positionCard1;
        this.positionCard2=positionCard2;
        this.card0Type=card0Type;
        this.card1Type=card1Type;
        this.card2Type=card2Type;
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
     * This method add to player the points he scored completing this specific pattern in his board
     * @param player is the player that has this Objective Card as personal Objective or Common Objective
     */
    @Override
    public int addPointsToPlayer(Player player) {
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
}
