package org.server;
import java.util.Map;

public class Objective_card_number extends Objective_Card {
    private Map<Playable_Card.ResourceType, Integer> resources;
    public Objective_card_number (int points, Map<Playable_Card.ResourceType, Integer> resources) {
        super (points);
        this.resources = resources;
    }

    private enum CountingType {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        FEATHER,
        JAR
    }


    public int checkResourcesNumber( Map<Playable_Card.ResourceType, Integer> symbols){
        int [] counting; //keeping track of the points
        counting = new int[] {0,0,0,0,0,0,0}; //initializing 7 int cells to 0 (each one represents a resource)
        int i = 0;
        int pointsCollected = 0;

        for (Map.Entry<Playable_Card.ResourceType, Integer> copy: resources.entrySet()) {
            // Map.Entry automatically obtains key-value pairs and then iterates over these pairs
            counting[i] = symbols.get(copy.getKey()) / resources.get(copy.getKey());
            i++;
        }

        for (int i=0; i<7; i++) { //checking how many times the player has achieved the Objective

            pointsCollected = counting[i];

            for(int j=i++; j<7; j++ ){
                if(counting[j] < pointsCollected){
                    pointsCollected = counting[j];
                }
            }

            if (counting[i] != 0 && counting[i] < pointsCollected) {
                pointsCollected = counting[i];
            }
        }

        return pointsCollected;
    }

}









