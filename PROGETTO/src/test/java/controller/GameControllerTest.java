package controller;

import CODEX.Exceptions.ColorAlreadyTakenException;
import CODEX.controller.GameController;
import CODEX.controller.ServerController;
import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.org.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameControllerTest extends TestCase {
    @Test
    //not a very good test because the id is created by the ServerController...
    public void testCreateGame() throws RemoteException {
        GameController g1= new GameController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        g1.createGame(players);
        assertEquals(g1.getGame().getId(), 0);
        assertEquals(g1.getGame().getState(), Game.GameState.WAITING_FOR_START);
    }

    //it correctly starts the game, then you will have to play the baseCards
    @Test
    public void testAddPlayer_CorrectBehaviour() throws RemoteException  {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding players to ServerController (NOT NECESSARY)
        s1.getAllNicknames().add(p1.getNickname());
        s1.getAllNicknames().add(p2.getNickname());
        s1.getAllNicknames().add(p3.getNickname());
        s1.getAllNicknames().add(p4.getNickname());

        s1.startLobby(p1.getNickname(),4);
        try {
            s1.addPlayerToLobby(p2.getNickname(), 0);
            s1.addPlayerToLobby(p3.getNickname(), 0);
            s1.addPlayerToLobby(p4.getNickname(), 0);
        }catch (GameNotExistsException | GameAlreadyStartedException | FullLobbyException ignored){}
    }

    //we will never get too many players because when we reach the correct number the game starts and it isn't accessible anymore
   @Test
    public void testAddPlayer_TooManyPlayers() throws RemoteException {
    GameController g1= new GameController();
    ServerController s1= new ServerController();
    Player p1=new Player();
    p1.setNickname("Pippo");
    Player p2=new Player();
    p2.setNickname("Pluto");
    Player p3=new Player();
    p3.setNickname("Paperino");
    Player p4= new Player();
    p4.setNickname("Topolino");
    Player p5= new Player();
    p5.setNickname("Minnie");

    /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */

    s1.startLobby(p1.getNickname(),4);
    try {
        s1.addPlayerToLobby(p2.getNickname(), 0);
        s1.addPlayerToLobby(p3.getNickname(), 0);
        s1.addPlayerToLobby(p4.getNickname(), 0);
        s1.addPlayerToLobby(p5.getNickname(), 0); //game already started!
        s1.addPlayerToLobby(p5.getNickname(), 1); //the game doesn't exist!
    }catch(GameNotExistsException | GameAlreadyStartedException | FullLobbyException ignored){}
}
@Test
public void testStartGame() throws RemoteException {
        GameController g1= new GameController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        g1.setNumberOfPlayers(4);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        g1.createGame(players);
        g1.startGame();

        //starting situation
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

    }

    @Test
    public void testPlayCard() throws RemoteException, ColorAlreadyTakenException {
        GameController g1 = new GameController();
        ServerController s1= new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p2.setNickname("Pluto");
        Player p3 = new Player();
        p3.setNickname("Paperino");
        Player p4 = new Player();
        p4.setNickname("Topolino");

        //adding the players to the GameController
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */

        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //all the players have to choose their Pawn's color and their objectiveCard
        g1.choosePawnColor(p1.getNickname(), Pawn.BLUE);
        g1.choosePawnColor(p2.getNickname(), Pawn.GREEN);
        g1.choosePawnColor(p3.getNickname(), Pawn.YELLOW);
        g1.choosePawnColor(p4.getNickname(), Pawn.RED);

        g1.chooseObjectiveCard(p1.getNickname(), p1.getPersonalObjective());
        g1.chooseObjectiveCard(p2.getNickname(), p2.getPersonalObjective());
        g1.chooseObjectiveCard(p3.getNickname(), p3.getPersonalObjective());
        g1.chooseObjectiveCard(p4.getNickname(), p4.getPersonalObjective());

        System.out.println(g1.getGame().getCurrentPlayer().getNickname());

        g1.playCard("Pippo", p1.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard("Pippo", g1.getGame().getResourceCard1());

        g1.playCard("Pluto", p2.getPlayerDeck()[0], new Coordinates(21, 21), true);
        g1.drawCard("Pluto", g1.getGame().getResourceCard1());

        g1.playCard("Paperino", p3.getPlayerDeck()[0], new Coordinates(19, 21), true);
        g1.drawCard("Paperino", g1.getGame().getResourceCard1());

        g1.playCard("Topolino", p4.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard("Topolino", g1.getGame().getResourceCard1());

    }

    @Test
    public void testPlayBaseCard() throws RemoteException {
        GameController g1 = new GameController();
        ServerController s1= new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p2.setNickname("Pluto");
        Player p3 = new Player();
        p3.setNickname("Paperino");
        Player p4 = new Player();
        p4.setNickname("Topolino");

        //adding the players to the GameController
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        PlayableCard baseCardP1=p1.getPlayerDeck(1);

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        assertEquals(p1.getBoard().getTable()[p1.getBoard().getBoardDimensions()/2][p1.getBoard().getBoardDimensions()/2], baseCardP1);
    }

    //problemi in player, non vengono tolte le carte dai deck quando chiamo drawCard().
    //2_ Chi controlla se sto pescando una carta legittima allora?
    //3_ Passiamo i player per nickname o Player? Passiamo le carte per id o meno?
    @Test
    public void testDrawCard() throws RemoteException {
        GameController g1 = new GameController();
        ServerController s1= new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p2.setNickname("Pluto");
        Player p3 = new Player();
        p3.setNickname("Paperino");
        Player p4 = new Player();
        p4.setNickname("Topolino");

        //adding the players to the GameController
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //starting situation
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        //removing a card from the player hand
        Player tmp= g1.getGame().getCurrentPlayer();
        PlayableCard[] cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;
        System.out.println("Il giocatore corrente è: "+tmp.getNickname());
        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        tmp= g1.getGame().getCurrentPlayer();
        cardsInHand= tmp.getPlayerDeck();
        cardsInHand[1]=null;

        System.out.println("Il giocatore corrente è: "+tmp.getNickname());
        g1.drawCard(tmp.getNickname(), g1.getGame().getResourceCard1());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

    }

    @Test
    public void testDrawCard_finishDecks() throws RemoteException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);

        System.err.println("The first player is: "+g1.getGame().getPlayers().get(0).getNickname());

        int i=30;
        while(i>0) {
            g1.getGame().getGoldDeck().getFirstCard();
            g1.getGame().getResourceDeck().getFirstCard();
            i--;
        }

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());

        //checking the reset of last card (GoldCard in ResourceCard1)
        Player currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getResourceCard1());

        //leaving just 1 gold card
        for(int k=0; k<2; k++){
            g1.getGame().getGoldDeck().getFirstCard();
        }

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());


        //drawing the last gold card
        currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());

        System.err.println("The player producing the ENDING condition is: "+currentPlayer.getNickname());

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());

        assertEquals(g1.getGame().getState(), Game.GameState.ENDING);

        currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getResourceCard1());
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());

        currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getResourceCard2());
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());


        currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(21, 21), true);
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());

        currentPlayer= g1.getGame().getCurrentPlayer();

        for(AngleType t: currentPlayer.getPlayerDeck()[0].getNeededResources().keySet()) {
            currentPlayer.getBoard().getNumResources().put(t, currentPlayer.getPlayerDeck()[0].getNeededResources().get(t));
        }

        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(21, 21), true);
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());

        currentPlayer= g1.getGame().getCurrentPlayer();
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());

        for(AngleType t: currentPlayer.getPlayerDeck()[0].getNeededResources().keySet()) {
            currentPlayer.getBoard().getNumResources().put(t, currentPlayer.getPlayerDeck()[0].getNeededResources().get(t));
        }
            g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(21, 21), true);

            currentPlayer = g1.getGame().getCurrentPlayer();
        System.err.println("The CURRENT player is: "+currentPlayer.getNickname());

        //adding the necessary resources
            for(AngleType t: currentPlayer.getPlayerDeck()[0].getNeededResources().keySet()) {
                currentPlayer.getBoard().getNumResources().put(t, currentPlayer.getPlayerDeck()[0].getNeededResources().get(t));
            }

            g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(21, 21), true);
            System.out.println(g1.getLastRounds());

            //print the points/objectives completed by player
            for(Player p: g1.getGame().getPlayers()){
                System.out.println(p.getNickname()+" managed to score "+p.getPoints()+" points and completed "+p.getNumObjectivesReached()+" objectives!");
            }
        }

        public void testPlayCard_20Points() throws RemoteException {
            GameController g1= new GameController();
            ServerController s1= new ServerController();
            Player p1=new Player();
            p1.setNickname("Pippo");
            Player p2=new Player();
            p2.setNickname("Pluto");
            Player p3=new Player();
            p3.setNickname("Paperino");
            Player p4= new Player();
            p4.setNickname("Topolino");

            //adding the players to GameController
            List<Player> players=new ArrayList<>();
            players.add(p1);
            players.add(p2);
            players.add(p3);
            players.add(p4);

            /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
            //adding players to GameController (action performed by ServerController usually)
            g1.getGamePlayers().add(p1);
            g1.getGamePlayers().add(p2);
            g1.getGamePlayers().add(p3);
            g1.getGamePlayers().add(p4);

            //creating and starting the game
            g1.createGame(players);
            g1.startGame();

            //all the players need to play the baseCard first
            g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
            g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
            g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
            g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);


            Player currentPlayer= g1.getGame().getCurrentPlayer();
            currentPlayer.addPoints(19);

            //adding the needed resources
            for(AngleType t: currentPlayer.getPlayerDeck()[2].getNeededResources().keySet()) {
                currentPlayer.getBoard().getNumResources().put(t, currentPlayer.getPlayerDeck()[2].getNeededResources().get(t));
            }
            //reaching 20 POINTS
            g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[2], new Coordinates(19, 19), true);
            if(currentPlayer.getPoints()>=20) {
                assertEquals(7, g1.getLastRounds());
                assertEquals(4, g1.getLastDrawingRounds());
            }
            g1.drawCard(currentPlayer.getNickname(), g1.getGame().getResourceCard1());
        }

    @Test
    public void testChooseObjectiveCard() throws RemoteException {
        ObjectiveDeck obDeck= ObjectiveDeck.objectiveDeck();
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //testing if the objective card is chosen correctly
        ObjectiveCard cardToBeSelected= p1.getPersonalObjective();
        g1.chooseObjectiveCard(p1.getNickname(), cardToBeSelected);
        assertEquals(cardToBeSelected,p1.getPersonalObjective());
    }

    @Test
    public void testChoosePawnColor() throws RemoteException, ColorAlreadyTakenException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        g1.choosePawnColor(p1.getNickname(), Pawn.BLUE);
        g1.choosePawnColor(p2.getNickname(), Pawn.BLUE);
        System.out.println(p1.getChosenColor());
        System.out.println(p2.getChosenColor());
        g1.choosePawnColor(p2.getNickname(), Pawn.YELLOW);
        g1.choosePawnColor(p3.getNickname(), Pawn.YELLOW);
        g1.choosePawnColor(p4.getNickname(), Pawn.RED);
        g1.choosePawnColor(p3.getNickname(), Pawn.GREEN);
        for(Player p: g1.getGame().getPlayers()){
            System.out.println(p.getChosenColor());
        }
    }

    @Test
    public void testSendMessage() throws RemoteException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

       /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);

        List<String> receiver=new ArrayList<>();
        receiver.add(p2.getNickname());
        receiver.add(p3.getNickname());
        receiver.add(p4.getNickname());
        g1.sendMessage(p1.getNickname(), receiver, "ciao");
        List<String> tmp= new ArrayList<>(receiver);
        tmp.add(p1.getNickname());

        System.out.println(g1.getGame().getChatByUsers(tmp).messagesReceivedByPlayer(p3.getNickname()).get(0).getMessage());

        List<String> receiver2=new ArrayList<>();
        receiver2.add(p1.getNickname());
        receiver2.add(p3.getNickname());
        receiver2.add(p4.getNickname());
        g1.sendMessage(p2.getNickname(), receiver2, "ciao2");
        tmp= new ArrayList<>(receiver2);
        tmp.add(p2.getNickname());


        System.out.println(g1.getGame().getChatByUsers(tmp).messagesReceivedByPlayer(p1.getNickname()).get(0).getMessage());
        for(Integer i: g1.getGame().getChats().keySet()){
          System.out.println("ID: "+i);
        }

        List <String> couple=new ArrayList<>();
        couple.add(p1.getNickname());
        g1.sendMessage(p2.getNickname(), couple, "hello Pippo");
        couple.add(p2.getNickname());

        System.out.println(g1.getGame().getChatByUsers(couple).messagesReceivedByPlayer(p1.getNickname()).get(0).getMessage());

        List <String> couple2=new ArrayList<>();
        //The problem is I can't modify the List of receivers as I'm passing it as a parameter
        List<String> users= new ArrayList<>();
        couple2.add(p2.getNickname());
        users.add(p1.getNickname());
        users.add(p2.getNickname());
        g1.sendMessage(p1.getNickname(), couple2, "hello Pluto");
        System.out.println(g1.getGame().getChatByUsers(users).messagesReceivedByPlayer(p2.getNickname()).get(0).getMessage());

        List <String> couple3=new ArrayList<>();
        couple3.add(p3.getNickname());
        g1.sendMessage(p1.getNickname(), couple3, "hello Paperino");
        couple3.add(p1.getNickname());
        System.out.println(g1.getGame().getChatByUsers(couple3).messagesReceivedByPlayer(p3.getNickname()).get(0).getMessage());

        /**In effetti avere le chat separate è un po' strano, ma quando dovrò stampare avrò una List<Message> temp
         * che mi permetta di inserire dentro tutti i messaggi delle chat dove lo specifico player è registrato
         * tra gli users. Stampando magari gli ultimi 20 messaggi per TimeStamp (sort())
         */
    }

    //ok!!
    @Test
    public void testNextPhase_1() throws RemoteException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

      /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //starting situation
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        //removing a card from the player hand
        Player tmp= g1.getGame().getCurrentPlayer();
        PlayableCard[] cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;

        System.err.println("Il giocatore corrente è: "+tmp.getNickname());

        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());


        //removing a card from the player hand
        tmp= g1.getGame().getCurrentPlayer();
        cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;

        System.err.println("Il giocatore corrente è: "+tmp.getNickname());

        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        //removing a card from the player hand
        tmp= g1.getGame().getCurrentPlayer();
        cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;

        System.err.println("Il giocatore corrente è: "+tmp.getNickname());

        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        //removing a card from the player hand
        tmp= g1.getGame().getCurrentPlayer();
        cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;

        System.err.println("Il giocatore corrente è: "+tmp.getNickname());

        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

        //removing a card from the player hand
        tmp= g1.getGame().getCurrentPlayer();
        cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;

        System.err.println("Il giocatore corrente è: "+tmp.getNickname());

        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

    }

    //test end game
    @Test
    public void testNextPhase_2() throws RemoteException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
/*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
    }



    @Test
    public void testEndGame() throws RemoteException, ColorAlreadyTakenException {
        GameController g1= new GameController();
        ServerController s1= new ServerController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");

        //adding the players to GameController
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        /*
    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);
     */
        //adding players to GameController (action performed by ServerController usually)
        g1.getGamePlayers().add(p1);
        g1.getGamePlayers().add(p2);
        g1.getGamePlayers().add(p3);
        g1.getGamePlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //all the players have to choose their Pawn's color and their objectiveCard
        g1.choosePawnColor(p1.getNickname(), Pawn.BLUE);
        g1.choosePawnColor(p2.getNickname(), Pawn.GREEN);
        g1.choosePawnColor(p3.getNickname(), Pawn.YELLOW);
        g1.choosePawnColor(p4.getNickname(), Pawn.RED);

        g1.chooseObjectiveCard(p1.getNickname(), p1.getPersonalObjective());
        g1.chooseObjectiveCard(p2.getNickname(), p2.getPersonalObjective());
        g1.chooseObjectiveCard(p3.getNickname(), p3.getPersonalObjective());
        g1.chooseObjectiveCard(p4.getNickname(), p4.getPersonalObjective());

        //

    }

}