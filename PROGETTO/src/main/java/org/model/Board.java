package org.model;

import java.util.*;

public class Board {
    private static final int numCarte=80;
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private int boardDimensions;
    private Map<PlayableCard.ResourceType, Integer> numResources;

    //this will actually be the board of each player
    private PlayableCard table[][];
    private Player player;

    //constructor of Board that takes the number of player that are playing the game
    public Board(int nPlayers, Player player){
        //to round up the result
        this.boardDimensions = 1+2*(numCarte+nPlayers-1/nPlayers);
        this.playablePositions = new HashSet<>();
        this.unPlayablePositions = new HashSet<>();
        this.table = new PlayableCard [boardDimensions][boardDimensions];
        this.player = player;
        for(int i = 0; i < boardDimensions; i++){
            for(int j = 0; i < boardDimensions; j++){
                table[i][j] = null;
            }
        }
        //initialisation of the resource Map
        numResources = new HashMap<>();
        numResources.put(PlayableCard.ResourceType.FUNGI,0);
        numResources.put(PlayableCard.ResourceType.INSECT,0);
        numResources.put(PlayableCard.ResourceType.ANIMAL,0);
        numResources.put(PlayableCard.ResourceType.NATURE,0);
        numResources.put(PlayableCard.ResourceType.JAR,0);
        numResources.put(PlayableCard.ResourceType.SCROLL,0);
        numResources.put(PlayableCard.ResourceType.FEATHER,0);
        //these 2 should not be counted but we will have to because of our algorithm
        numResources.put(PlayableCard.ResourceType.NO_RESOURCE,0);
        numResources.put(PlayableCard.ResourceType.ABSENT,0);
    }

    /**
     * This method places the first base card for a player in the middle of the table (dimension/2)
     * @param card is the base card you want to place
     */
    public void placeBaseCard(PlayableCard card){
        this.table[boardDimensions/2][boardDimensions/2] = card;

        //adding the new card resources
        if(card.getOrientation()) {
            numResources.put(card.get_front_down_left(), numResources.get(card.get_front_down_left())+1);
            numResources.put(card.get_front_down_right(), numResources.get(card.get_front_down_right())+1);
            numResources.put(card.get_front_up_left(), numResources.get(card.get_front_up_left())+1);
            numResources.put(card.get_front_up_right(), numResources.get(card.get_front_up_right())+1);
        } else {
            //adds the back resource if played on the back side
            numResources.put(card.getCardType(),numResources.get(card.getCardType())+1);
        }
        updateUnplayablePositions(card);
        updatePlayablePositions(card);
    }

    /**
     * Places a card in the table and updates playable/unplayable positions
     * @param card
     * @param position
     * @return true if the card can be placed, false if the card can't be placed in that position
     * throws an exception if the player can't play the card there
     */
    //we have to add the requirements in the gold cards
    public boolean placeCard(PlayableCard card, Coordinates position) {
        int coveredAngles = 0;
        if (playablePositions.contains(position) && enoughResources(card)) {
            //all the angles of the adjacent cards we could cover with the one we are placing
            PlayableCard.ResourceType upLeft=null;
            PlayableCard.ResourceType upRight=null;
            PlayableCard.ResourceType downLeft=null;
            PlayableCard.ResourceType downRight=null;

            //inserting the card
            card.setPosition(position);
            this.table[position.getX()][position.getY()] = card;

            //adding the new card resources
            if(card.getOrientation()) {
                numResources.put(card.get_front_down_left(), numResources.get(card.get_front_down_left())+1);
                numResources.put(card.get_front_down_right(), numResources.get(card.get_front_down_right())+1);
                numResources.put(card.get_front_up_left(), numResources.get(card.get_front_up_left())+1);
                numResources.put(card.get_front_up_right(), numResources.get(card.get_front_up_right())+1);

            }else{
                //adds the back resource if played on the back side
                numResources.put(card.getCardType(),numResources.get(card.getCardType())+1);
            }

            //checks adjacent card's angles we might be covering with the new card
            if(table[position.getX()-1][position.getY()+1]!=null) {
                upLeft = table[position.getX() - 1][position.getY() + 1].get_front_down_right();
            }
            if(table[position.getX()-1][position.getY()+1]!=null) {
                upRight = table[position.getX() + 1][position.getY() + 1].get_front_down_left();
            }
            if(table[position.getX()-1][position.getY()+1]!=null) {
                downLeft = table[position.getX() - 1][position.getY() - 1].get_front_up_right();
            }
            if(table[position.getX()-1][position.getY()+1]!=null) {
                downRight = table[position.getX() + 1][position.getY() - 1].get_front_up_left();
            }

            //subtracting the resource we are loosing when placing the new card
            if(upLeft!=null){
                numResources.put(upLeft, numResources.get(upLeft)-1);
                coveredAngles++;
            }
            if(upRight!=null){
                numResources.put(upRight, numResources.get(upRight)-1);
                coveredAngles++;
            }
            if (downLeft!=null){
                numResources.put(downLeft, numResources.get(downLeft)-1);
                coveredAngles++;
            }
            if(downRight!=null){
                numResources.put(downRight, numResources.get(downRight)-1);
                coveredAngles++;
            }

            //updating the playable and unplayable positions
            updateUnplayablePositions(card);
            updatePlayablePositions(card);
            player.addPoints(cardPoints(card, coveredAngles));
            return true;
        } else {
            return false;
        }
    }

    /**
     * when using a resource card, the method gives points scored by that specific card
     * when using a gold card, the method checks if you need to cover angles to receive points, or if you have to have jard, scrolls and feathers
     * @param card is the card you have placed now
     * @param coveredAngles is the number of angles you have covered when you have placed this card
     * @return points scored
     */
    public int cardPoints (PlayableCard card, int coveredAngles){
        int points = card.getPoints(); // if the card gives you points in any case
            if(card.isCoverAngleToReceivePoints()) {
                points = coveredAngles * card.getPoints();
            } else if (card.isJarToReceivePoints()){
                points = card.getPoints() * numResources.get(PlayableCard.ResourceType.JAR);
            } else if (card.isFeatherToReceivePoints()){
                points = card.getPoints() * numResources.get(PlayableCard.ResourceType.FEATHER);
            } else if (card.isScrollToReceivePoints()) {
                points = card.getPoints() * numResources.get(PlayableCard.ResourceType.SCROLL);
            }
            return points;
    }

    /**
     * the method checks if it's a gold card and the resources needed to place it
     * @param card is the card you have placed now
     * @return boolean value
     */
    private boolean enoughResources  (PlayableCard card) { // CONTROLLARE SE SI PUò USARE per pattern
        if (card.isNeededResourcesBoolean()) {
            for (PlayableCard.ResourceType t: numResources.keySet()){
                if (numResources.get(t) < card.getNeededResources().get(t)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }


    /**
     * Adding all the positions where the angle is ABSENT (players can't use that angle)
     * @param card
     *
     */
    public void updateUnplayablePositions(PlayableCard card) {
        if (card.getOrientation()) {
            if (card.get_front_up_right() == PlayableCard.ResourceType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpRight());
            }
            if (card.get_front_up_left() == PlayableCard.ResourceType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpLeft());
            }
            if (card.get_front_down_right() == PlayableCard.ResourceType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownRight());
            }
            if (card.get_front_down_left() == PlayableCard.ResourceType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownLeft());
            }
        }
    }

    /**
     * Adding all the positions where the player can place a card in the future
     * @param card
     */
    public void updatePlayablePositions(PlayableCard card) {
        //se non è presente in unplayable position aggiungo tutti gli angoli!=ABSENT
        if (card.getOrientation()) {
            if (card.get_front_up_right() != PlayableCard.ResourceType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findUpRight())) {
                playablePositions.add(card.getPosition().findUpRight());
            }
            if (card.get_front_up_left() != PlayableCard.ResourceType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findUpLeft())) {
                playablePositions.add(card.getPosition().findUpLeft());
            }
            if (card.get_front_down_right() != PlayableCard.ResourceType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findDownRight())) {
                playablePositions.add(card.getPosition().findDownRight());
            }
            if (card.get_front_down_left() != PlayableCard.ResourceType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findDownLeft())) {
                playablePositions.add(card.getPosition().findDownLeft());
            }
        } else {
            //If played on the back side, all the angles are present
            if (!unPlayablePositions.contains(card.getPosition().findUpRight())) {
                playablePositions.add(card.getPosition().findUpRight());
            }
            if (!unPlayablePositions.contains(card.getPosition().findUpLeft())) {
                playablePositions.add(card.getPosition().findUpLeft());
            }
            if (!unPlayablePositions.contains(card.getPosition().findDownRight())) {
                playablePositions.add(card.getPosition().findDownRight());
            }
            if (!unPlayablePositions.contains(card.getPosition().findDownLeft())) {
                playablePositions.add(card.getPosition().findDownLeft());
            }
        }
    }
}
