package org.server;

import java.util.List;
import java.util.Set;

public class Board {
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private int[][] table;
    private int maxExtensionX;
    private int minExtensionX;
    private int maxExtensionY;
    private int minExtensionY;
    private List<Integer> playedCards;
    private int[] numResourcesPlaced;
    public void addPlayablePositions(int id){

    }
    public void addUnplayablePositions(int id){

    }
    public void checkPlayablePositions(){

    }
    public void addResource(int resource, int quantity){

    }
    public void subtractResource(int resource, int quantity) {

    }
    public int getMaxExtensionX() {
        return maxExtensionX;
    }

    public void setMaxExtensionX(int maxExtensionX) {
        this.maxExtensionX = maxExtensionX;
    }


    public int getMinExtensionX() {
        return minExtensionX;
    }

    public void setMinExtensionX(int minExtensionX) {
        this.minExtensionX = minExtensionX;
    }

    public int getMaxExtensionY() {
        return maxExtensionY;
    }

    public void setMaxExtensionY(int maxExtensionY) {
        this.maxExtensionY = maxExtensionY;
    }

    public int getMinExtensionY() {
        return minExtensionY;
    }

    public void setMinExtensionY(int minExtensionY) {
        this.minExtensionY = minExtensionY;
    }
}
