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



    public void testEnoughResources() {
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

        List<AngleType> centralRes1=new ArrayList<>();
        centralRes1.add(AngleType.FUNGI);
        Map<AngleType, Integer> neededResources = new HashMap<>();
        neededResources.put(AngleType.FUNGI, 10);
        PlayableCard pc =new PlayableCard(1, 2, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE,
                AngleType.NO_RESOURCE, centralRes1 , false, false, false, false, neededResources);


        if (board.enoughResources(pc)) {
            System.out.println("The resources are enough to place the card");
        } else {
            System.out.println("The resources are NOT enough to place the card");
        }

    }



    public void testEnoughResources2() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.INSECT);
        centralResources.add(AngleType.FUNGI);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FUNGI, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(true);
        board.placeBaseCard(baseCard);

        List<AngleType> centralRes1=new ArrayList<>();
        centralRes1.add(AngleType.FUNGI);
        Map<AngleType, Integer> neededResources = new HashMap<>();
        neededResources.put(AngleType.FUNGI, 0);
        PlayableCard pc =new PlayableCard(1, 2, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE,
                AngleType.NO_RESOURCE, centralRes1 , false, false, false, false, neededResources);


        if (board.enoughResources(pc)) {
            System.out.println("The resources are enough to place the card");
        } else {
            System.out.println("The resources are NOT enough to place the card");
        }

    }





    public void testPlaceBaseCardBack() {
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


    public void testPlaceBaseCardFront() {
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
        baseCard.setOrientation(true);
        board.placeBaseCard(baseCard);

        System.out.println("Base card successfully placed");

    }

    //Test created by FRA to solve the problem related to contains
    public void testContainsCard(){
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);
        List<AngleType> centralRes1=new ArrayList<>();
        centralRes1.add(AngleType.FUNGI);
        PlayableCard pc=new PlayableCard(1, 2, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE,
                AngleType.NO_RESOURCE, centralRes1 , false, false, false, false, null);
        Coordinates position1= new Coordinates(board.getBoardDimensions()/2, board.getBoardDimensions()/2);
        board.getPlayablePositions().add(position1);
        board.placeCard(pc, position1);
        List<AngleType> centralRes2=new ArrayList<>();
        centralRes2.add(AngleType.ANIMAL);
        PlayableCard pc2= new PlayableCard(1, 2, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE, AngleType.NO_RESOURCE,
                AngleType.NO_RESOURCE, centralRes2 , false, false, false, false, null);
        System.out.println("Type of the card I placed: "+board.getPlayedCards().get(position1));
        System.out.println("Did I manage to place the card? "+board.placeCard(pc2, position1));
        Coordinates position2= new Coordinates(board.getBoardDimensions()/2 +1, board.getBoardDimensions()/2 + 1);
        System.out.println("Did I manage to place the card? "+board.placeCard(pc2, position2));
    }

    //Test created by FRA to solve the problem related to contains
    public void testContainsCoordinates(){
        Set<Coordinates> testSet= new HashSet<>();
        Coordinates p1= new Coordinates(0,0);
        testSet.add(p1);
        Coordinates p2= new Coordinates(0, 0);
        System.out.println("Does the set already contains the position? "+testSet.contains(p2));

    }
    public void testPlaceCardFront() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);


        centralResources = new ArrayList<>();
        centralResources.add(AngleType.NATURE);
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




    public void testPlaceCardBack() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);


        centralResources = new ArrayList<>();
        centralResources.add(AngleType.NATURE);
        PlayableCard resourceCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        Coordinates position = new Coordinates(41,41);
        resourceCard.setOrientation(false);
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



    public void testPlaceCardInTheMiddleFront() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);

        // card 1
        centralResources = new ArrayList<>();
        centralResources.add(AngleType.NATURE);
        PlayableCard resourceCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        Coordinates position = new Coordinates(41,41);
        resourceCard.setOrientation(true);
        resourceCard.setPosition(position);
        board.placeCard(resourceCard, position);

        // card 2
        PlayableCard resourceCard2 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard2.setOrientation(true);
        Coordinates position2 = new Coordinates(41,39);
        board.placeCard(resourceCard2, position2);

        // card 3
        PlayableCard resourceCard3 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard3.setOrientation(true);
        Coordinates position3 = new Coordinates(42,42);
        board.placeCard(resourceCard3, position3);

        // card 4
        PlayableCard resourceCard4 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard4.setOrientation(true);
        Coordinates position4 = new Coordinates(42, 38);
        board.placeCard(resourceCard4, position4);

        // card 5
        PlayableCard resourceCard5 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard5.setOrientation(true);
        Coordinates position5 = new Coordinates(43,41);
        board.placeCard(resourceCard5, position5);

        // card 6
        PlayableCard resourceCard6 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard6.setOrientation(true);
        Coordinates position6 = new Coordinates(43, 39);
        board.placeCard(resourceCard6, position6);

        // card 7
        PlayableCard resourceCard7 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard7.setOrientation(true);
        Coordinates position7 = new Coordinates(42, 40);
        board.placeCard(resourceCard7, position7);


        // printing the positions where matrix is not null
        for (Map.Entry<Coordinates, AngleType> entry : board.getPlayedCards().entrySet()) {
            System.out.println("Coordinate: " + entry.getKey() + ", Resource: : " + entry.getValue());
        }

        // printing the resources count
        for (Map.Entry<AngleType, Integer> entry : board.getNumResources().entrySet()) {
            System.out.println("Resource: " + entry.getKey() + ", Count: " + entry.getValue());
        }

    }



    public void testPlaceCardInTheMiddleBack() {
        Player p1 = new Player();
        Board board = new Board(p1);
        board.setBoard(2);

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add(AngleType.ANIMAL);

        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);

        // placing the card on the BACK side
        baseCard.setPosition(new Coordinates(40, 40));
        baseCard.setOrientation(false);
        board.placeBaseCard(baseCard);

        // card 1
        centralResources = new ArrayList<>();
        centralResources.add(AngleType.NATURE);
        PlayableCard resourceCard = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        Coordinates position = new Coordinates(41,41);
        resourceCard.setOrientation(false);
        resourceCard.setPosition(position);
        board.placeCard(resourceCard, position);

        // card 2
        PlayableCard resourceCard2 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard2.setOrientation(false);
        Coordinates position2 = new Coordinates(41,39);
        board.placeCard(resourceCard2, position2);

        // card 3
        PlayableCard resourceCard3 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard3.setOrientation(false);
        Coordinates position3 = new Coordinates(42,42);
        board.placeCard(resourceCard3, position3);

        // card 4
        PlayableCard resourceCard4 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard4.setOrientation(false);
        Coordinates position4 = new Coordinates(42, 38);
        board.placeCard(resourceCard4, position4);

        // card 5
        PlayableCard resourceCard5 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard5.setOrientation(false);
        Coordinates position5 = new Coordinates(43,41);
        board.placeCard(resourceCard5, position5);

        // card 6
        PlayableCard resourceCard6 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard6.setOrientation(false);
        Coordinates position6 = new Coordinates(43, 39);
        board.placeCard(resourceCard6, position6);

        // card 7
        PlayableCard resourceCard7 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, false, null);
        resourceCard7.setOrientation(false);
        Coordinates position7 = new Coordinates(42, 40);
        board.placeCard(resourceCard7, position7);


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



    public void testCardPoints2() {
        Player p1 = new Player();
        Board board = new Board(p1);

        // the card needs FEATHERS to receive points
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, true, false, null);

        int numFeathers = board.getNumResources().get(AngleType.SCROLL);
        System.out.println ("The player has " + numFeathers + " scroll. The card gives two points for each scroll. " +
                "So the total amount of points is: " + board.cardPoints(card, 0));

    }



    public void testCardPoints3() {
        Player p1 = new Player();
        Board board = new Board(p1);

        // the card needs FEATHERS to receive points
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                false, false, true, null);

        int numFeathers = board.getNumResources().get(AngleType.JAR);
        System.out.println ("The player has " + numFeathers + " jar. The card gives two points for each jar. " +
                "So the total amount of points is: " + board.cardPoints(card, 0));

    }



    public void testCardPoints4() {
        Player p1 = new Player();
        Board board = new Board(p1);

        // the card needs FEATHERS to receive points
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, true,
                false, false, false, null);

        System.out.println ("The player has 2 covered angles. The card gives two points for each angle. " +
                "So the total amount of points is: " + board.cardPoints(card, 2));

    }



    public void testUpdateUnplayablePositionsBack() {
        Player p1 = new Player();
        Board board = new Board(p1);
        Set<Coordinates> unplayablePositions;

        // the card has 3 unplayable positions because
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard basecard = new PlayableCard(1, 2, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT,
                AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, centralResources, false,
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



    public void testUpdateUnplayablePositionsFront() {
        Player p1 = new Player();
        Board board = new Board(p1);
        Set<Coordinates> unplayablePositions;

        // the card has 3 unplayable positions because
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard basecard = new PlayableCard(1, 2, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT,
                AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, AngleType.ABSENT, centralResources, false,
                true, false, false, null);

        // unplayable positions before the card is played
        unplayablePositions = board.getUnPlayablePositions();
        for (Coordinates coordinate : unplayablePositions) {
            System.out.println("Before playing: " + coordinate);
        }

        // checking unplayable positions
        basecard.setPosition(new Coordinates(40,40));
        basecard.setOrientation(true);
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
