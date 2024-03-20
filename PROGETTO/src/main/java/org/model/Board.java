package org.model;

import java.util.*;

public class Board {
    private static final int numCarte=80;
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private int boardDimensions;
    private int numResources[];

    //this will actually be the board of each player
    private PlayableCard table[][];

    //constructor of Board that takes the number of player that are playing the game
    public Board(int nPlayers){
        //to round up the result
        this.boardDimensions= 1+2*(numCarte+nPlayers-1/nPlayers);
        this.playablePositions= new HashSet<>();
        this.unPlayablePositions= new HashSet<>();
        this.table= new PlayableCard [boardDimensions][boardDimensions];
        numResources= new int[7];
    }

    /**
     * Places a card in the table and updates playable/unplayable positions
     * @param card
     * @param position
     * throws an exception if the player can't play the card there
     */
    public void placeCard(PlayableCard card, Coordinates position) throws IllegalArgumentException{
        IllegalArgumentException e;
        if (playablePositions.contains(position)) {
            this.table[position.getX()][position.getY()] = card;
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

    /**
     *  Adding all the positions where the player can place a card in the future
     * @param card
     */
    public void updatePlayablePositions(PlayableCard card){
        //se non è presente in unplayable position aggiungo tutti gli angoli!=ABSENT
        if (card.get_front_up_right() != PlayableCard.ResourceType.ABSENT && !unPlayablePositions.contains(card.getPosition())) {
            unPlayablePositions.add(card.getPosition().findUpRight());
        }
        if (card.get_front_up_left() != PlayableCard.ResourceType.ABSENT && !unPlayablePositions.contains(card.getPosition())) {
            unPlayablePositions.add(card.getPosition().findUpLeft());
        }
        if (card.get_front_down_right() != PlayableCard.ResourceType.ABSENT && !unPlayablePositions.contains(card.getPosition())) {
            unPlayablePositions.add(card.getPosition().findDownRight());
        }
        if (card.get_front_down_left() != PlayableCard.ResourceType.ABSENT && !unPlayablePositions.contains(card.getPosition())) {
            unPlayablePositions.add(card.getPosition().findDownLeft());
        }
    }

}
