package org.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ObjectiveCardPatternTest extends TestCase {
    public void addPointsToPlayerTest(){
        //let's use for example the blue diagonal pattern (down to up, left to right)
        Objective_card_pattern usedCard = new Objective_card_pattern(2,new Coordinates(1,-1),new Coordinates(2,-2),AngleType.INSECT,AngleType.INSECT,AngleType.INSECT);
        Map<Coordinates,AngleType> playedCards=new HashMap<>();
        playedCards.put(new Coordinates(1,1),AngleType.INSECT);
        assertEquals();

    }
}
