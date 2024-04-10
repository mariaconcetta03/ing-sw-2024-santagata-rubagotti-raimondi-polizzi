package org.model;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectiveCardNumberTest {
    public void addPointsToPlayerTest() {
        Map<CountingType, Integer> resources = new HashMap<>(); //the structure tha memorizes the resources needed and that is contained in Objective_card_number
        resources.put(CountingType.JAR,1);
        resources.put(CountingType.FEATHER,1);
        resources.put(CountingType.SCROLL,1);
        Objective_card_number usedCard = new Objective_card_number(3, resources); //three points to be multiplied for the times we found the 'resources' requested (to be taken in groups)
        Map<AngleType, Integer> numResources= new HashMap<>(); //this is the structure used by addPointsToPlayer method
        numResources.put(AngleType.JAR, 3); //that means that in the Table there are three visible jars
        numResources.put(AngleType.FEATHER, 2);
        numResources.put(AngleType.SCROLL, 1); //this is the lowest number (if compared to the two lines above)

        assertEquals(usedCard.addPointsToPlayer(new Player()),3); //three points because we have only one set of: jar, feather, scroll
    }
}
