package org.model;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveCardTest extends TestCase {

    public void testAddPointsToPlayer1() {
        //let's use for example the blue diagonal pattern (down to up, left to right)
        ObjectiveCard usedCard = new ObjectiveCard(2, 2, null, new Coordinates(1, 1), new Coordinates(2, 2), AngleType.INSECT, AngleType.INSECT, AngleType.INSECT);
        Map<Coordinates, AngleType> playedCards;
        playedCards = new HashMap<>();
        playedCards.put(new Coordinates(1, 1), AngleType.INSECT);
        playedCards.put(new Coordinates(2, 2), AngleType.INSECT);
        playedCards.put(new Coordinates(3, 3), AngleType.INSECT);
        playedCards.put(new Coordinates(4, 4), AngleType.INSECT);
        playedCards.put(new Coordinates(5, 5), AngleType.INSECT);
        playedCards.put(new Coordinates(6, 6), AngleType.FUNGI);  //see the drive file ObjectiveCardPatternTest
        assertEquals(usedCard.addPointsToPlayer(new Player()), 2); //the algorithm is correct if it gives 2 points to the player
    }

    public void testAddPointsToPlayer2(){
        Map<CountingType, Integer> resources = new HashMap<>(); //the structure tha memorizes the resources needed and that is contained in Objective_card_number
        resources.put(CountingType.JAR,1);
        resources.put(CountingType.FEATHER,1);
        resources.put(CountingType.SCROLL,1);
        ObjectiveCard usedCard = new ObjectiveCard(3, 3, resources, null, null, null, null, null); //three points to be multiplied for the times we found the 'resources' requested (to be taken in groups)
        Map<AngleType, Integer> numResources = new HashMap<>(); //this is the structure used by addPointsToPlayer method
        numResources.put(AngleType.JAR, 3); //that means that in the Table there are three visible jars
        numResources.put(AngleType.FEATHER, 2);
        numResources.put(AngleType.SCROLL, 1); //this is the lowest number (if compared to the two lines above)
        assertEquals(usedCard.addPointsToPlayer(new Player()),3); //three points because we have only one set of: jar, feather, scroll
        }
    }