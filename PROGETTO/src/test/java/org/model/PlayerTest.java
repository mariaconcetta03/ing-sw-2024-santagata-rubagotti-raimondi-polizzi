package org.model;

import Exceptions.CardNotDrawableException;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.IncludeState;
import junit.framework.TestCase;
import org.junit.jupiter.api.DisplayNameGenerator;

import javax.script.AbstractScriptEngine;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest extends TestCase {

    public void testDrawCard() {
        // creating the player
        Player p1 = new Player();
        Player p2=new Player();
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);

        Game game= new Game(players, 1);
        p1.setGame(game);
        p2.setGame(game);
        game.setnPlayers(2);
        game.startGame();
        p1.playBaseCard(true, p1.getPlayerDeck()[0]);
        p2.playBaseCard(true, p2.getPlayerDeck()[0]);
        game.giveInitialCards();
        /**
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
         */

        List<AngleType> centralResources = new ArrayList<>();
        centralResources.add (AngleType.INSECT);

        p1.getPlayerDeck()[0]=null;
        p1.getPlayerDeck()[1]=null;
        p1.getPlayerDeck()[2]=null;

        try {
            p1.drawCard(game.getGoldCard1());
            p1.drawCard(game.getGoldCard2());
            p1.drawCard(game.getGoldDeck().checkFirstCard());
        }catch (CardNotDrawableException ignored){}
        System.out.println("The 1st card in player's hand has the ID: " + p1.getPlayerDeck(1).getId());
        System.out.println("The 2nd card in player's hand has the ID: " + p1.getPlayerDeck(2).getId());
        System.out.println("The 3rd card in player's hand has the ID: " + p1.getPlayerDeck(3).getId());

        assertThrows(CardNotDrawableException.class, ()->{p1.drawCard(new PlayableCard(3, 2, AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE,
                AngleType.FUNGI, AngleType.SCROLL, AngleType.FEATHER, AngleType.NATURE, centralResources, false,
                true, false, false, null));});


    }




    public void testPlayCard() {
        // creating the structures
        Player p1 = new Player();
        Player p2 = new Player();
        Game game = new Game(p1, 1);
        game.setnPlayers(2);
        game.addPlayer(p2);
        p1.setGame(game);
        p2.setGame(game);
        game.giveInitialCards();
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
            System.out.println("The card played has the central type: " + board.getPlayedCards().get(entry.getKey()));
        }
    }




    public void testPlayBaseCard() {

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
            System.out.println("Before playing the base card, playable positions are (x, y): " + coordinate.getX() + " " +coordinate.getY());
        }

        // the player plays the base card
        p1.playBaseCard(true, baseCard);
        board.updatePlayablePositions(baseCard);
        board.updateUnplayablePositions(baseCard);

        // playable positions after the card is played
        for (Coordinates coordinate : board.getPlayablePositions()) {
            System.out.println("After playing the base card, playable positions are (x, y): " + coordinate.getX() + " " + coordinate.getY());
        }
    }




    public void testAddPoints() {
        Player p1 = new Player();

        // printing the points before and after the invoking of the function
        System.out.println("The points before calling the function \"addPoints\" are: " + p1.getPoints());
        p1.addPoints(5);
        System.out.println("Adding 5 points to the player...");
        System.out.println("The points after calling the function \"addPoints\" are: " + p1.getPoints());
    }




    public void testAddNumObjectivesReached() {
        Player p1 = new Player();

        // printing the number of objectives reached before and after the invoking of the function
        System.out.println("The number of objectives reached before calling the function \"addNumObjectivesReached\" are: " + p1.getNumObjectivesReached());
        p1.addNumObjectivesReached();
        System.out.println("Adding one objective as reached to the player...");
        System.out.println("The number of objectives reached after calling the function \"addNumObjectivesReached\" are: " + p1.getNumObjectivesReached());
    }

}