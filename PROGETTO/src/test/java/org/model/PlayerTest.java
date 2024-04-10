package org.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.*;

public class PlayerTest extends TestCase {

    void drawCardTest() {
        // creating the player
        Player p1 = new Player();

        // creating 3 cards (playable card) with ID: 1, 2, 3
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard playableCard1 = new PlayableCard(1, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                true, false, false, null);
        PlayableCard playableCard2 = new PlayableCard(2, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                true, false, false, null);
        PlayableCard playableCard3 = new PlayableCard(3, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                true, false, false, null);

        // the player draws each of the 3 cards
        p1.drawCard(playableCard1);
        p1.drawCard(playableCard2);
        p1.drawCard(playableCard3);

        System.out.println("The 1st card in player's hand has the ID: " + p1.getPlayerDeck(1).getId());
        System.out.println("The 2nd card in player's hand has the ID: " + p1.getPlayerDeck(2).getId());
        System.out.println("The 3rd card in player's hand has the ID: " + p1.getPlayerDeck(3).getId());



    }




    void playCardTest() {
        // creating the structures
        Player p1 = new Player();
        Board board = new Board(p1);
        p1.setBoard(board);
        board.setBoard(2);

        // this is the card already placed on the board
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard card = new PlayableCard(1, 2, AngleType.FEATHER, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.ABSENT, AngleType.NO_RESOURCE, centralResources, false,
                true, false, false, null);

        // checking playable and unplayable positions of the card which is already existing
        card.setPosition(new Coordinates(2,2));
        card.setOrientation(true);
        board.updatePlayablePositions(card);
        board.updateUnplayablePositions(card);
        board.placeCard(card, new Coordinates(2, 2));

        // creating the card which the player wants to play
        PlayableCard cardToPlay = new PlayableCard(2, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.INSECT, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.ABSENT, AngleType.NO_RESOURCE, centralResources, false,
                true, false, false, null);

        // played cards before the cardToPlay is played
        for (Map.Entry<Coordinates, AngleType> entry : board.getPlayedCards().entrySet()) {
            System.out.println("Played positions before playing are: " + board.getPlayedCards().get(entry.getKey()));
        }

        // the player plays the card an then we check playable and unplayable positions of that
        p1.playCard(cardToPlay, new Coordinates(3, 3), true);
        board.updatePlayablePositions(cardToPlay);
        board.updateUnplayablePositions(cardToPlay);

        // played cards after the cardToPlay is played
        for (Map.Entry<Coordinates, AngleType> entry : board.getPlayedCards().entrySet()) {
            System.out.println("Played positions after playing are: " + board.getPlayedCards().get(entry.getKey()));
        }
    }




    void playBaseCardTest() {

        // creating the structures
        Player p1 = new Player();
        Board board = new Board(p1);
        p1.setBoard(board);
        board.setBoard(2);

        // creating the baseCard
        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);
        PlayableCard baseCard = new PlayableCard(1, 2, AngleType.FEATHER, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.ABSENT, AngleType.NO_RESOURCE, centralResources, false,
                true, false, false, null);


        // playable positions before the card is played
        for (Coordinates coordinate : board.getPlayablePositions()) {
            System.out.println("Before playing the base card, playable positions are: " + coordinate);
        }

        // the player plays the base card
        p1.playBaseCard(true, baseCard);
        board.updatePlayablePositions(baseCard);
        board.updateUnplayablePositions(baseCard);

        // playable positions after the card is played
        for (Coordinates coordinate : board.getPlayablePositions()) {
            System.out.println("After playing the base card, playable positions are: " + coordinate);
        }
    }




    void addPointsTest() {
        Player p1 = new Player();

        // printing the points before and after the invoking of the function
        System.out.println("The points before calling the function \"addPoints\" are: " + p1.getPoints());
        p1.addPoints(5);
        System.out.println("Adding 5 points to the player...");
        System.out.println("The points after calling the function \"addPoints\" are: " + p1.getPoints());
    }




    void addNumObjectivesReachedTest() {
        Player p1 = new Player();

        // printing the number of objectives reached before and after the invoking of the function
        System.out.println("The number of objectives reached before calling the function \"addNumObjectivesReached\" are: " + p1.getNumObjectivesReached());
        p1.addNumObjectivesReached();
        System.out.println("Adding one objective as reached to the player...");
        System.out.println("The number of objectives reached after calling the function \"addNumObjectivesReached\" are: " + p1.getNumObjectivesReached());
    }

}