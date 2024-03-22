package org.model;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Map;

/**
 * This class represents all the Objective cards which needs a group of resources on the board to collect extra points
 */

public class Objective_card_number extends ObjectiveCard {
    private Map<PlayableCard.ResourceType, Integer> resources; // each resource is associated with an int, which
    // indicates the number of resources needed by the objective card

    private enum CountingType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        FEATHER,
        JAR,
        PARCHMENT
    }




    /**
     * Class constructor
     *
     * @param points    the points the card gives to the player if he has achieved the objective
     * @param resources a Map of the resources needed for the extra points
     */
    public Objective_card_number(int points, Map<PlayableCard.ResourceType, Integer> resources) {
        super(points);
        this.resources = resources;
    }





    /**
     * This method returns the number of points collected thanks to the achievement of this objective
     *
     * @param symbolsOnBoard all the resources, collected in a Map, which the player has on his own board
     * @return the number of total extra points collected by the player thanks to the achievement of the objective
     */
    public int checkResourcesNumber (Map<PlayableCard.ResourceType, Integer> symbolsOnBoard) {
        int minResources = Integer.MAX_VALUE;

        for (PlayableCard.ResourceType t : resources.keySet()) {
            if (resources.get(t) != 0) { // se l'obiettivo non richiede nulla --> non controllo!
                if (symbolsOnBoard.get(t) / resources.get(t) < minResources) {
                    minResources = symbolsOnBoard.get(t) / resources.get(t);
                }
            }
        }
        return minResources * this.getCardPoints();
    }

}