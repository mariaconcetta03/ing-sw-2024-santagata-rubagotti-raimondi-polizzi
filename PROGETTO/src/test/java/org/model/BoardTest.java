package org.model;

import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.*;
public class BoardTest extends TestCase {

    public void testSetBoard() {
        Player p1 = new Player();
        Board board = new Board(p1);

        for (int i = 2; i<5; i++) {
            board.setBoard(i);
            System.out.println("The dimension of the board with " + i + " players is " + board.getBoardDimensions());
        }
    }



    public void testPlaceBaseCard() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.INSECT);
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);

        System.out.println("Base card successfully placed");

    }



    public void testPlaceCard() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.INSECT);
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);


        centralResources = null;
        PlayableCard resourceCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        Coordinates position = new Coordinates(41,41);
        resourceCard.setOrientation(true);
        resourceCard.setPosition(position);
        board.placeCard(resourceCard, position);

        // printing the positions where matrix is not null
        for (Map.Entry<Coordinates, AngleType> entry : board.getPlayedCards().entrySet()) {
            System.out.println("Coordinate: " + entry.getKey() + ", Resource: : " + entry.getValue());
        }

        // printing the resources count
        for (Map.Entry<AngleType, Integer> entry : board.getNumResources().entrySet()) {
            System.out.println("Resource: " + entry.getKey() + ", Count: " + entry.getValue());
        }

    }



    public void testCardPoints() {
        Player p1 = new Player();
        Board board = new Board(p1);

        // the card needs FEATHERS to receive points
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                true, false, false, null);

        int numFeathers = board.getNumResources().get(AngleType.FEATHER);
        System.out.println ("The player has " + numFeathers + " feathers. The card gives two points for each feather. " +
                "So the total amount of points is: " + board.cardPoints(card, 0));

    }



    public void testUpdateUnplayablePositions() {
        Player p1 = new Player();
        Board board = new Board(p1);
        Set<Coordinates> unplayablePositions;

        // the card has 3 unplayable positions because
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard basecard = new PlayableCard(1, 2, AngleType.ABSENT, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.ABSENT, AngleType.NO_RESOURCE, centralResources, false,
                true, false, false, null);

        // unplayable positions before the card is played
        unplayablePositions = board.getUnPlayablePositions();
        for (Coordinates coordinate : unplayablePositions) {
            System.out.println("Before playing: " + coordinate);
        }

        // checking unplayable positions
        basecard.setPosition(new Coordinates(40,40));
        basecard.setOrientation(false);
        board.updateUnplayablePositions(basecard);

        // unplayable positions after the card is played
        unplayablePositions = board.getUnPlayablePositions();
        for (Coordinates coordinate : unplayablePositions) {
            System.out.println("After playing: " + coordinate.getX() + " " + coordinate.getY());
        }
    }



    public void testUpdatePlayablePositions() {
        Player p1 = new Player();
        Board board = new Board(p1);
        Set<Coordinates> playablePositions;

        // the card has 3 unplayable positions because
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.ABSENT, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.ABSENT, AngleType.NO_RESOURCE, centralResources, false,
                true, false, false, null);

        // playable positions before the card is played
        playablePositions = board.getPlayablePositions();
        for (Coordinates coordinate : playablePositions) {
            System.out.println("Before playing: " + coordinate.getX() + " " + coordinate.getY());
        }

        // checking unplayable positions
        card.setPosition(new Coordinates(40,40));
        card.setOrientation(true);
        board.updatePlayablePositions(card);

        // playable positions after the card is played
        playablePositions = board.getPlayablePositions();
        for (Coordinates coordinate : playablePositions) {
            System.out.println("After playing: " + coordinate.getX() + " " + coordinate.getY());
        }
    }

}
