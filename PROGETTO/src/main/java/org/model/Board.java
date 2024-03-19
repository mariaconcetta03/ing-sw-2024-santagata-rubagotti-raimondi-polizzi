package org.model;

import java.util.*;

public class Board {
    private static final int numCarte=80;
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private int boardDimensions;
    private int numResources[];

    //this will actually be the board of each player
    private int Table[][];

    //constructor of Board that takes the number of player that are playing the game
    public Board(int nPlayers){
        //to round up the result
        this.boardDimensions= 1+2*(numCarte+nPlayers-1/nPlayers);
        this.playablePositions=null;
        this.unPlayablePositions=null;
        this.Table= new int[boardDimensions][boardDimensions];
        numResources= new int[7];
    }
    public void placeCard(int cardId, Coordinates posizione){
        Table[posizione.getX()][posizione.getY()]=cardId;
        updateUnplayablePositions(cardId);
        updatePlayablePositions(cardId);
    }
    public void updateUnplayablePositions(int cardId){
        //bisogna capire qua cosa fare per sto get playable
        Codex.getInstance().getPlayableCardById(cardId).getOrientatio;

        //aggiungo tutti gli angoli dove ho ABSENT
    }
    public void updatePlayablePositions(int cardId){
        //se non è presente in unplayable position aggiungo tutti gli angoli!=ABSENT
    }

}
