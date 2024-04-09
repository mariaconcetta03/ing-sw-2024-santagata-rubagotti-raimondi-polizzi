package org.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals; //inspired by Nardi's code :)

public class ObjectiveCardPatternTest extends TestCase {
    public void baseTestBlueDiagonalPattern(){
        //let's use for example the blue diagonal pattern (down to up, left to right)
        Objective_card_pattern usedCard = new Objective_card_pattern(2,new Coordinates(1,1),new Coordinates(2,2),AngleType.INSECT,AngleType.INSECT,AngleType.INSECT);
        Map<Coordinates,AngleType> playedCards=new HashMap<>();
        playedCards.put(new Coordinates(1,1),AngleType.INSECT);
        playedCards.put(new Coordinates(2,2),AngleType.INSECT);
        playedCards.put(new Coordinates(3,3),AngleType.INSECT);
        assertEquals(usedCard.addPointsToPlayer(new Player()),2); //the algorithm is correct if it gives 2 points to the player
    }
   
}
