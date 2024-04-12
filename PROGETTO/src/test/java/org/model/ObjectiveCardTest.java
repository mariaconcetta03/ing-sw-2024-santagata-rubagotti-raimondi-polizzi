package org.model;

import com.ctc.wstx.shaded.msv_core.reader.xmlschema.IncludeState;
import junit.framework.TestCase;
import org.junit.jupiter.api.DisplayNameGenerator;

import javax.script.AbstractScriptEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectiveCardTest extends TestCase {

    public void testAddPointsToPlayerPattern() {
        //let's use for example the blue diagonal pattern (down to up, left to right)
        ObjectiveCard usedCard = new ObjectiveCard(87, 2, null, new Coordinates(1, 1), new Coordinates(2, 2), AngleType.INSECT, AngleType.INSECT, AngleType.INSECT);
        Player player = new Player();
        Board board = new Board(player);
        board.setBoard(2);
        player.setBoard(board);
        //central resources
        List<AngleType> centralResources = new ArrayList<>(); //associated with a color
        centralResources.add(AngleType.INSECT);
        List<AngleType> centralResources2 = new ArrayList<>(); //associated with another color
        centralResources2.add(AngleType.FUNGI);

        // placing the base card
        PlayableCard baseCard = new PlayableCard (81, 0, AngleType.NATURE, AngleType.FUNGI, AngleType.ANIMAL, AngleType.INSECT, AngleType.NATURE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, centralResources, false, false, false, false, null);
        board.placeBaseCard(baseCard);
        board.updatePlayablePositions(baseCard);




        //the angles types are random (but they can't be absent)
        board.placeCard(new PlayableCard(2, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(41, 41));
        board.placeCard(new PlayableCard(3, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(42,42));
        board.placeCard(new PlayableCard(4, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(43, 43));
        board.placeCard(new PlayableCard(5, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(44, 44));
        board.placeCard(new PlayableCard(6, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(45, 45));
        board.placeCard(new PlayableCard(7, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources2, false, false, false, false, null), new Coordinates(46, 46));
        //see the drive file ObjectiveCardPatternTest

        System.out.println("Player has: " + player.getPoints() + " points");
        usedCard.addPointsToPlayer(player);
        System.out.println("Adding points to player...");
        System.out.println("Player has: " + player.getPoints() + " points");
    }




    public void testAddPointsToPlayerNumber(){
        Map<CountingType, Integer> resources = new HashMap<>(); //the structure tha memorizes the resources needed and that is contained in Objective_card_number
        resources.put(CountingType.JAR,1);
        resources.put(CountingType.FEATHER,1);
        resources.put(CountingType.SCROLL,1);
        ObjectiveCard usedCard = new ObjectiveCard(95, 3, resources, null, null, null, null, null); //three points to be multiplied for the times we found the 'resources' requested (to be taken in groups)
        Map<AngleType, Integer> numResources = new HashMap<>(); //this is the structure used by addPointsToPlayer method
        numResources.put(AngleType.JAR, 3); //that means that in the Table there are three visible jars
        numResources.put(AngleType.FEATHER, 2);
        numResources.put(AngleType.SCROLL, 1); //this is the lowest number (if compared to the two lines above)
        assertEquals(usedCard.addPointsToPlayer(new Player()),3); //three points because we have only one set of: jar, feather, scroll
        }
    }