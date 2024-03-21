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

    //constructor of Board that takes the number of player that are playing the game
    public Board(int nPlayers){
        //to round up the result
        this.boardDimensions = 1+2*(numCarte+nPlayers-1/nPlayers);
        this.playablePositions = new HashSet<>();
        this.unPlayablePositions = new HashSet<>();
        this.table = new PlayableCard [boardDimensions][boardDimensions];
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
        numResources.put(PlayableCard.ResourceType.PARCHMENT,0);
        numResources.put(PlayableCard.ResourceType.FEATHER,0);
        //these 2 should not be counted but we will have to because of our algorithm
        numResources.put(PlayableCard.ResourceType.NO_RESOURCE,0);
        numResources.put(PlayableCard.ResourceType.ABSENT,0);
    }
    //metodo setboard che inizializza la board, non posso prendere nPlayers come parametro
    public void placeBaseCard(){

    }

    /**
     * Places a card in the table and updates playable/unplayable positions
     * @param card
     * @param position
     * throws an exception if the player can't play the card there
     */
    //we have to add the requirements in the gold cards
    public void placeCard(PlayableCard card, Coordinates position) throws IllegalArgumentException{
        if (playablePositions.contains(position)) {
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
            }
            if(upRight!=null){
                numResources.put(upRight, numResources.get(upRight)-1);
            }
            if (downLeft!=null){
                numResources.put(downLeft, numResources.get(downLeft)-1);
            }
            if(downRight!=null){
                numResources.put(downRight, numResources.get(downRight)-1);
            }

            //updating the playable and unplayable positions
            updateUnplayablePositions(card);
            updatePlayablePositions(card);
        } else {
            throw new IllegalArgumentException();
        }
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
     *  Adding all the positions where the player can place a card in the future
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
