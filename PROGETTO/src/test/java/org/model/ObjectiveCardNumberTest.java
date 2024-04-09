package org.model;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class ObjectiveCardNumberTest {
    public void addPointsToPlayerTest() {
        Map<CountingType, Integer> resources = new HashMap<>();
        resources.put(CountingType.JAR,1);
        resources.put(CountingType.FEATHER,1);
        resources.put(CountingType.SCROLL,1);
        Objective_card_number usedCard = new Objective_card_number(3, resources);
        Map<AngleType, Integer> numResources= new HashMap<>(); //this is the structure used by addPointsToPlayer method
        numResources.put(AngleType.JAR, 3);
        numResources.put(AngleType.FEATHER, 2);
        numResources.put(AngleType.SCROLL, 1); //this is the minor number

        assertEquals(usedCard.addPointsToPlayer(new Player()),3);
    }
}
