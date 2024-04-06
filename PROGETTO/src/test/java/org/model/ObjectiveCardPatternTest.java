package org.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveCardPatternTest extends TestCase {
    Objective_card_pattern usedCard= null; //we want to try to initialize it with setUp() method
    @BeforeEach //like in Nardi's code
    public void setUp(){ //let's use for example the blue diagonal pattern (down to up, left to right)
        usedCard = new Objective_card_pattern(2,new Coordinates(1,-1),new Coordinates(2,-2),AngleType.INSECT,AngleType.INSECT,AngleType.INSECT);
    }
    public void addPointsToPlayerTest(){
        Map<Coordinates,AngleType> playedCards=new HashMap<>();
        playedCards.put(new Coordinates(1,1),AngleType.FUNGI);

    }
}
