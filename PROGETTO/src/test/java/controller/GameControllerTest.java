package controller;

import Exceptions.CardNotOwnedException;
import junit.framework.TestCase;
import org.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameControllerTest extends TestCase {

    //not a very good test because the id is created by the ServerController...
    public void testCreateGame() {
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
    public void testAddPlayer_CorrectBehaviour() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        s1.startLobby(p1,4);
        s1.addPlayerToLobby(p2, 0);
        s1.addPlayerToLobby(p3, 0);
        s1.addPlayerToLobby(p4, 0);
    }

    //we will never get too many players because when we reach the correct number the game starts and it isn't accessible anymore
    public void testAddPlayer_TooManyPlayers(){
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

    //adding players to ServerController
    s1.getAllPlayers().add(p1);
    s1.getAllPlayers().add(p2);
    s1.getAllPlayers().add(p3);
    s1.getAllPlayers().add(p4);

    s1.startLobby(p1,4);
    s1.addPlayerToLobby(p2, 0);
    s1.addPlayerToLobby(p3, 0);
    s1.addPlayerToLobby(p4, 0);
    s1.addPlayerToLobby(p5, 0); //game already started!
    s1.addPlayerToLobby(p5, 1); //the game doesn't exist!
}
    public void testStartGame() {
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

    public void testPlayCard() {
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

        //adding the players to the ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //all the players have to choose their Pawn's color and their objectiveCard
        g1.choosePawnColor(p1, Pawn.BLUE);
        g1.choosePawnColor(p2, Pawn.GREEN);
        g1.choosePawnColor(p3, Pawn.YELLOW);
        g1.choosePawnColor(p4, Pawn.RED);

        g1.chooseObjectiveCard(p1, p1.getPersonalObjective());
        g1.chooseObjectiveCard(p2, p2.getPersonalObjective());
        g1.chooseObjectiveCard(p3, p3.getPersonalObjective());
        g1.chooseObjectiveCard(p4, p4.getPersonalObjective());

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

    public void testPlayBaseCard(){
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

        //adding the players to the ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

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
    public void testDrawCard() {
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

        //adding the players to the ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

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

    public void testDrawCard_finishDecks(){
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);

        int i=30;
        while(i>0) {
            g1.getGame().getGoldDeck().getFirstCard();
            g1.getGame().getResourceDeck().getFirstCard();
            i--;
        }

        //checking the reset of last card
        Player currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getResourceCard1());
        assertNull(g1.getGame().getResourceCard1());

        //leaving just 1 gold card
        for(int k=0; k<3; k++){
            g1.getGame().getGoldDeck().getFirstCard();
        }

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());


        //drawing the last gold card
        currentPlayer= g1.getGame().getCurrentPlayer();
        g1.playCard(currentPlayer.getNickname(), currentPlayer.getPlayerDeck()[0], new Coordinates(19, 19), true);
        g1.drawCard(currentPlayer.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());

        //assertEquals(g1.getGame().getState(), Game.GameState.ENDING);

        System.out.println( "Remaining gold cards: "+ g1.getGame().getGoldDeck().getCards().size());
        System.out.println( "Remaining resource cards: "+ g1.getGame().getResourceDeck().getCards().size());


    }

    public void testChooseObjectiveCard() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

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
        g1.chooseObjectiveCard(p1, cardToBeSelected);
        assertEquals(cardToBeSelected,p1.getPersonalObjective());
    }

    public void testChoosePawnColor() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        g1.choosePawnColor(p1, Pawn.BLUE);
        g1.choosePawnColor(p2, Pawn.BLUE);
        System.out.println(p1.getChosenColor());
        System.out.println(p2.getChosenColor());
        g1.choosePawnColor(p2, Pawn.YELLOW);
        g1.choosePawnColor(p3, Pawn.YELLOW);
        g1.choosePawnColor(p4, Pawn.RED);
        g1.choosePawnColor(p3, Pawn.GREEN);
        for(Player p: g1.getGame().getPlayers()){
            System.out.println(p.getChosenColor());
        }
    }

    public void testSendMessage() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);

        List<Player> receiver=new ArrayList<>();
        receiver.add(p2);
        receiver.add(p3);
        receiver.add(p4);
        g1.sendMessage(p1, receiver, "ciao");

        System.out.println(g1.getGame().getChats().get(0).messagesReceivedByPlayer(p3).get(0).getMessage());

        List<Player> receiver2=new ArrayList<>();
        receiver2.add(p1);
        receiver2.add(p3);
        receiver2.add(p4);
        g1.sendMessage(p2, receiver2, "ciao2");

        System.out.println(g1.getGame().getChats().get(0).messagesReceivedByPlayer(p1).get(0).getMessage());

        List <Player> couple=new ArrayList<>();
        couple.add(p1);
        g1.sendMessage(p2, couple, "hello Pippo");
        System.out.println(g1.getGame().getChats().get(1).messagesReceivedByPlayer(p1).get(0).getMessage());

        List <Player> couple2=new ArrayList<>();
        //The problem is I can't modify the List of receivers as I'm passing it as a parameter
        List<Player> users= new ArrayList<>();
        couple2.add(p2);
        users.add(p1);
        users.add(p2);
        g1.sendMessage(p1, couple2, "hello Pluto");
        System.out.println(g1.getGame().getChatByUsers(users).messagesReceivedByPlayer(p2).get(0).getMessage());

        List <Player> couple3=new ArrayList<>();
        couple3.add(p3);
        g1.sendMessage(p1, couple3, "hello Paperino");
        System.out.println(g1.getGame().getChats().get(2).messagesReceivedByPlayer(p3).get(0).getMessage());

        /**In effetti avere le chat separate è un po' strano, ma quando dovrò stampare avrò una List<Message> temp
         * che mi permetta di inserire dentro tutti i messaggi delle chat dove lo specifico player è registrato
         * tra gli users. Stampando magari gli ultimi 20 messaggi per TimeStamp (sort())
         */
    }

    //ok!!
    public void testNextPhase_1() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

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
    public void testNextPhase_2(){
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);
    }

    public void testLeaveGame(){
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        p1.addPoints(10);
        g1.leaveGame("Topolino");
    }

    public void testEndGame() {
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

        //adding players to ServerController
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);

        //creating and starting the game
        g1.createGame(players);
        g1.startGame();

        //all the players need to play the baseCard first
        g1.playBaseCard("Pippo", p1.getPlayerDeck(1), true);
        g1.playBaseCard("Topolino", p4.getPlayerDeck(1), true);
        g1.playBaseCard("Paperino", p3.getPlayerDeck(1), true);
        g1.playBaseCard("Pluto", p2.getPlayerDeck(1), true);

        //all the players have to choose their Pawn's color and their objectiveCard
        g1.choosePawnColor(p1, Pawn.BLUE);
        g1.choosePawnColor(p2, Pawn.GREEN);
        g1.choosePawnColor(p3, Pawn.YELLOW);
        g1.choosePawnColor(p4, Pawn.RED);

        g1.chooseObjectiveCard(p1, p1.getPersonalObjective());
        g1.chooseObjectiveCard(p2, p2.getPersonalObjective());
        g1.chooseObjectiveCard(p3, p3.getPersonalObjective());
        g1.chooseObjectiveCard(p4, p4.getPersonalObjective());

        //

    }

}