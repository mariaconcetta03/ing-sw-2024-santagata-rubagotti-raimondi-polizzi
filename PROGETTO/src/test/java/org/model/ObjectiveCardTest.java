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
        //the id of the card is to be chosen among the ids of the objective cards of type pattern
        ObjectiveCard usedCard= new ObjectiveCard(); //this is the default one (sets everything to zero/null)
        usedCard.ObjectiveCardPattern(87, 2, new Coordinates(1, 1), new Coordinates(2, 2), AngleType.INSECT, AngleType.INSECT, AngleType.INSECT);
        Player player = new Player(); //this constructor set the points to zero
        Board board = new Board(player);
        board.setBoard(2); //two is the number of players
        player.setBoard(board);
        //central resources
        List<AngleType> centralResources = new ArrayList<>(); //associated with a color
        centralResources.add(AngleType.INSECT);
        List<AngleType> centralResources2 = new ArrayList<>(); //associated with another color
        centralResources2.add(AngleType.FUNGI);

        // placing the base card
        PlayableCard baseCard = new PlayableCard (81, 0, AngleType.NATURE, AngleType.FUNGI, AngleType.ANIMAL, AngleType.INSECT, AngleType.NATURE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, centralResources, false, false, false, false, null);
        board.placeBaseCard(baseCard); //placing this card is something that is needed because in this way we update for the first time the playable positions




        //the angles types are random (but they can't be absent)
        board.placeCard(new PlayableCard(2, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(41, 41));
        board.placeCard(new PlayableCard(3, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(42,42));
        board.placeCard(new PlayableCard(4, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(43, 43));
        board.placeCard(new PlayableCard(5, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(44, 44));
        board.placeCard(new PlayableCard(6, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(45, 45));
        board.placeCard(new PlayableCard(7, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources2, false, false, false, false, null), new Coordinates(46, 46));
        //see the drive file ObjectiveCardPatternTest

        assertEquals(player.getPoints(),0); //to be sure that the starting point is correct
        usedCard.addPointsToPlayer(player); //this method should add 2 points to the player (which currently should have 0 points)
        assertEquals(2,player.getPoints()); //the algorithm is correct only if after that the above method is called the player has 2 points
    }




    public void testAddPointsToPlayerNumber(){
        Player player = new Player(); //this constructor set the points to zero
        Board board = new Board(player);
        board.setBoard(2); //two is the number of players
        player.setBoard(board);
        Map<AngleType, Integer> resources = new HashMap<>(); //the structure tha memorizes the resources needed and that is contained in Objective_card_number
        resources.put(AngleType.JAR,1);
        resources.put(AngleType.FEATHER,1);
        resources.put(AngleType.SCROLL,1);
        //the id of the card is to be chosen among the ids of the objective cards of type number
        ObjectiveCard usedCard= new ObjectiveCard(); //this is the default one (sets everything to zero/null)
        usedCard.ObjectiveCardNumber(95, 3, resources); //three points to be multiplied for the times we found the 'resources' requested (to be taken in groups)
        //let's populate the board: we want to have 3 jars, 2 feathers and 1 scroll
        List<AngleType> centralResources = new ArrayList<>(); //this is random
        centralResources.add(AngleType.INSECT);
        PlayableCard baseCard = new PlayableCard (81, 0, AngleType.NATURE, AngleType.FUNGI, AngleType.ANIMAL, AngleType.INSECT, AngleType.NATURE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, centralResources, false, false, false, false, null);
        board.placeBaseCard(baseCard); //placing this card is something that is needed because in this way we update for the first time the playable positions
        //the resources different from jar, feather and scroll (the ones contained in 'resources') are random
        //the angles are chosen to be sure that all the cards that we want to place are placeable in the given position
        board.placeCard(new PlayableCard(2, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.SCROLL, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(41, 41));
        board.placeCard(new PlayableCard(3, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.FEATHER, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(42,42));
        board.placeCard(new PlayableCard(4, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.FEATHER, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(43, 43));
        board.placeCard(new PlayableCard(5, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.JAR, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(44, 44));
        board.placeCard(new PlayableCard(6, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.JAR, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(45, 45));
        board.placeCard(new PlayableCard(7, 0, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.JAR, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, AngleType.INSECT, centralResources, false, false, false, false, null), new Coordinates(46, 46));

        usedCard.addPointsToPlayer(player); //this method should add 3 points to the player (which currently should have 0 points)
        assertEquals(3,player.getPoints()); //three points because we have only one set of: jar, feather, scroll
        }
    }