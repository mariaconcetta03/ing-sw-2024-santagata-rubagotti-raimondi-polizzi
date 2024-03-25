package org.model;

import java.util.*;

public class Objective_card_pattern extends ObjectiveCard{
    private Coordinates positionCard1; //the next card of the pattern composed by three cards
    private Coordinates positionCard2; //the third card of the pattern
    private AngleType card0Type;
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
    public void addPointsToPlayer(Player player) {
        int counter=0;
        //creates a Set view of the keyset of the player's Board
        Set<Coordinates> cardsPositions=player.getBoard().getPlayedCards().keySet();

        //temp values to help me directly remove the coordinates if found
        Coordinates temp1=positionCard1;
        Coordinates temp2=positionCard2;

        for(Coordinates center : cardsPositions){
            temp1= sumCoordinates(center,temp1);
            temp2= sumCoordinates(center,temp2);
            //checks the positions of the other 2 cards
            if(cardsPositions.contains(temp1)&&cardsPositions.contains(temp2)){
                //checks the type of all the 3 cards
                if(player.getBoard().getPlayedCards().get(temp1).getCentralResources().get(0).equals(card1Type)&&
                        player.getBoard().getPlayedCards().get(temp2).getCentralResources().get(0).equals(card2Type)&&
                        player.getBoard().getPlayedCards().get(center).getCentralResources().get(0).equals(card0Type)){
                    //increment the counter and remove from the Set the position already used
                    counter++;
                    cardsPositions.remove(center);
                    cardsPositions.remove(temp1);
                    cardsPositions.remove(temp2);
                }
            }
        }

        player.addPoints(counter * this.getCardPoints());

    }
}
