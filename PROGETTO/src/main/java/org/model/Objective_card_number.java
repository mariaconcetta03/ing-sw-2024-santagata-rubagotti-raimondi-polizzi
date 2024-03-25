package org.model;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Map;

/**
 * This class represents all the Objective cards which needs a group of resources on the board to collect extra points
 */

public class Objective_card_number extends ObjectiveCard {
    private Map<AngleType, Integer> resources; // each resource is associated with an int, which
    // indicates the number of resources needed by the objective card


    /**
     * Class constructor
     *
     * @param points    the points the card gives to the player if he has achieved the objective
     */
    public Objective_card_number(int points) {
        super(points);
    }

    /**
     * This method returns the number of points collected thanks to the achievement of this objective
     * The method calculates the minor value in order to satisfy the combination
     * (f.e. if the combination needed is : 1 jar, 1 feather; then it counts the resources on the board
     * f.e.  3 jar, 2 feather --> minResources = 2)
     *
     * @param player the Player which we are checking the objective for
     */
    @Override
    public void addPointsToPlayer(Player player) {
        int minResources = Integer.MAX_VALUE; //that's an improbable superior limit
        Map<AngleType,Integer> symbolsOnBoard=player.getBoard().getNumResources();

        for(AngleType t : resources.keySet()){
            if((symbolsOnBoard.get(t)/resources.get(t))<minResources){
                minResources= symbolsOnBoard.get(t)/resources.get(t);
            }
        }
        //adding the just calculated point to the player
        player.addPoints(minResources* this.getCardPoints());
    }
}