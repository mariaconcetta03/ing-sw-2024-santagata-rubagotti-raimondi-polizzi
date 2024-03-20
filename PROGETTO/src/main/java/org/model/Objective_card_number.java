package org.model;
import java.util.Map;

public class Objective_card_number extends ObjectiveCard {
    private Map<PlayableCard.ResourceType, Integer> resources; // each resource is associated with an int, which
                                                                // indicates the number of resources needed by the objective card
    public Objective_card_number (int points, Map<PlayableCard.ResourceType, Integer> resources) {
        super (points);
        this.resources = resources;
    }

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
     * this function returns the number of points collected thanks to the achievement of this objective
     * @param symbols
     * @return pointsCollected
     */
    public int checkResourcesNumber( Map<PlayableCard.ResourceType, Integer> symbols ) {
        int [] counting; //keeping track of the points
        counting = new int[] {0,0,0,0,0,0,0}; //initializing 7 int cells to 0 (each one represents a resource)
        int i = 0;
        int pointsCollected = 0;

        for (Map.Entry<PlayableCard.ResourceType, Integer> copy: resources.entrySet()) {
            // Map.Entry automatically obtains key-value pairs and then iterates over these pairs
            counting[i] = symbols.get(copy.getKey()) / resources.get(copy.getKey());
            i++;
        }

        for (int k=0; k<7; k++) { //checking how many times the player has achieved the Objective

                pointsCollected = counting[k];

            for(int j=i++; j<7; j++ ){
                if(counting[k] < pointsCollected){
                    pointsCollected = counting[j];
                }
            }

           /* if(counting[k] < pointsCollected && counting[k] != 0 ){
                pointsCollected = counting[k]; // lascerei solo questo invece del for e del resto
            } */


            if (counting[k] != 0 && counting[k] < pointsCollected) {
                pointsCollected = counting[k];
            }
        }

        return pointsCollected;
    }

}
