package org.model;

import CODEX.org.model.ChatMessage;
import CODEX.org.model.Game;
import CODEX.org.model.Player;
import junit.framework.TestCase;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class GameTest extends TestCase {

    public void testAddPlayer() {
        Player p1 = new Player();
        p1.setNickname("Topino");
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        p.setNickname("Papero");
        List<Player> players = new ArrayList<>();
        players = game.getPlayers();
        game.setnPlayers(2);

        for (int i = 0; i< players.size(); i++) {
            System.out.println("Player number " + i + " with nickname " + players.get(i).getNickname());
        }
    }



    public void testAddPlayerWithException() {
        Player p1 = new Player();
        p1.setNickname("Topino");
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        p.setNickname("Papero");
        List<Player> players = new ArrayList<>();
        players = game.getPlayers();
        game.setnPlayers(2);
        game.addPlayer(p);
        Player p3 = new Player(game);
        p3.setNickname("Minnie");
        assertThrows(ArrayIndexOutOfBoundsException.class, ()->{
            game.addPlayer(p3);
        });
        for (int i = 0; i< players.size(); i++) {
            System.out.println("Player number " + i + " with nickname " + players.get(i).getNickname());
        }
    }




    public void testStartGame() throws RemoteException  {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int numPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        game.startGame();

        // printing the game status
        if (game.getState().equals(Game.GameState.STARTED)) {
            System.out.println("The game is set to STARTED!");
        }

        // printing the colours of the pawns
        for (int i = 0; i<numPlayers; i++) {
            System.out.println ("The player number " + i + " has chosen the Pawn: " + game.getPlayers().get(i).getChosenColor());
        }

        // printing the cards on the market
        System.out.println("The 1 card on market has ID: " + game.getResourceCard1().getId());
        System.out.println("The 2 card on market has ID: " + game.getResourceCard2().getId());
        System.out.println("The 3 card on market has ID: " + game.getGoldCard1().getId());
        System.out.println("The 4 card on market has ID: " + game.getGoldCard2().getId());

    }



    public void testStartGameWithException() {
        Player p1 = new Player();
        Game game = new Game(p1, 1);

        game.setnPlayers(2);

        int numPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");

        assertThrows(IllegalArgumentException.class, () -> {
            game.startGame();
        });

        // printing the game status
        if (game.getState().equals(Game.GameState.STARTED)) {
            System.out.println("The game is set to STARTED!");
        }
    }

    public void testGiveInitialCards() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1,1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);

        game.giveInitialCards();

        for (int i=0; i<nPlayers; i++) {
            System.out.println("Initial cards given to player number: " + i);
            System.out.println("First card ID: " + game.getPlayers().get(i).getPlayerDeck(1).getId());
            System.out.println("Second card ID: " + game.getPlayers().get(i).getPlayerDeck(2).getId());
            System.out.println("Third card ID: " + game.getPlayers().get(i).getPlayerDeck(3).getId());
            System.out.println("------------------------------------------");
        }
        // printing the common objectives
        System.out.println("The 1st common objective has ID: " + game.getObjectiveCard1().getId());
        System.out.println("The 2nd common objective has ID: " + game.getObjectiveCard2().getId());

    }




    public void testWinner() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);

        // adding points to the player
        p1.addPoints(5);
        p.addPoints(3);
        p1.setNickname("Papero");
        p.setNickname("Topolino");



        // printing the points
        System.out.println("The player Papero has 5 points");
        System.out.println("The player Topolino has 3 points");

        /*

        List <Player> winners;
        winners = game.winner();

        for (int i = 0; i < winners.size(); i++) {
            System.out.println("The winner is/are: " + winners.get(i).getNickname());
        }

         */

    }

    public void testWinner4() throws RemoteException {
        Player p1=new Player();
        Player p2=new Player();
        Player p3=new Player();
        Player p4=new Player();
        p1.setNickname("pippo");
        p2.setNickname("pluto");
        p3.setNickname("topolino");
        p4.setNickname("paperone");
        List<Player> tmp=new ArrayList<>();
        tmp.add(p1);
        tmp.add(p2);
        tmp.add(p3);
        tmp.add(p4);


        Game game= new Game(tmp, 0);
        p1.addPoints(0);
        p2.addPoints(0);
        p3.addPoints(0);
        p4.addPoints(0);

        p1.addNumObjectivesReached();

        Map<Integer, List<String>> scoreBoard=game.winner();
        for(Integer i: scoreBoard.keySet()) {
            for (String s : scoreBoard.get(i)) {
                System.out.println(i + "_ " + s);
            }
        }

    }




    public void testWinner2() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);

        // adding points to the player
        p1.addPoints(5);
        p.addPoints(5);
        p1.setNickname("Papero");
        p.setNickname("Topolino");
        p.addNumObjectivesReached();
        p1.addNumObjectivesReached();



        // printing the points
        System.out.println("The player Papero has 5 points");
        System.out.println("The player Topolino has 5 points");


        /*
        List <Player> winners;
        winners = game.winner();

        for (int i = 0; i < winners.size(); i++) {
            System.out.println("The winner is/are: " + winners.get(i).getNickname());
        }

         */

    }




    public void testWinner3() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);

        // adding points to the player
        p1.addPoints(5);
        p.addPoints(5);
        p1.setNickname("Papero");
        p.setNickname("Topolino");
        p.addNumObjectivesReached();


        // printing the points
        System.out.println("The player Papero has 5 points");
        System.out.println("The player Topolino has 5 points");


        /*
        List <Player> winners;
        winners = game.winner();

        for (int i = 0; i < winners.size(); i++) {
            System.out.println("The winner is/are: " + winners.get(i).getNickname());
        }

         */

    }




    public void testStartChat() {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);
        System.out.println("Starting chat between P1 and P2");

        game.startChat(p.getNickname(), p1.getNickname());

        for(int i = 0; i < game.getChats().size(); i++) {
            System.out.println("The number of the active chats is: " + game.getChats().size());
        }
    }




    public void testStartGeneralChat() {
        /*Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p = new Player(game);
        game.setnPlayers(2);
        game.addPlayer(p);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);
        p.setGame(game);
         */
        Player p1=new Player();
        Player p2= new Player();
        p1.setNickname("pippo");
        p2.setNickname("pluto");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        Game game= new Game(players, 0);

        System.out.println("Starting general chat");

        game.startGeneralChat();
        for(int i = 0; i < game.getChats().size(); i++) {
            System.out.println("The number of the active chats is: " + game.getChats().values().size());
        }
    }

    public void testGetChatByUsers() throws RemoteException {
        String s1= "pippo";
        String s2= "pluto";
        String s3= "topolino";
        String s4= "paperino";
        Player p1=new Player();
        Player p2= new Player();
        Player p3= new Player();
        Player p4= new Player();
        p1.setNickname(s1);
        p2.setNickname(s2);
        p3.setNickname(s3);
        p4.setNickname(s4);
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        Game game=new Game(players, 1);

        game.startChat(p1.getNickname(), p2.getNickname());
        game.startChat(p2.getNickname(), p4.getNickname());
        game.startGeneralChat();
        game.startChat(p2.getNickname(), p1.getNickname());
        for(Integer i:game.getChats().keySet()){
            System.out.println("ID: "+i);
        }
        List<String> nicknames1=new ArrayList<>();
        nicknames1.add(p1.getNickname());
        nicknames1.add(p2.getNickname());
        List<String> receivers1=new ArrayList<>();
        receivers1.add(p2.getNickname());
        game.getChatByUsers(nicknames1).sendMessage(new ChatMessage("ciao!", p1.getNickname(), receivers1, new Timestamp(System.currentTimeMillis())));
        for(ChatMessage m: game.getChatByUsers(nicknames1).getMessages()){
            System.out.println("Messaggio: "+m.getMessage());
        }
    }




    public void testNextRound() {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        System.out.println("The current playing order is FIRST PLAYER (" + game.getPlayers().get(0).getNickname() + "), " +
                "SECOND PLAYER (" + game.getPlayers().get(1).getNickname() + ")," + "THIRD PLAYER (" + game.getPlayers().get(2).getNickname() + "), " +
                "FOURTH PLAYER (" + game.getPlayers().get(3).getNickname() + ")");

        // changing the playing order
        System.out.println("Changing the playing order...");
        try {
            game.nextRound();
        }catch (RemoteException ignored){}
        System.out.println("The new playing order is FIRST PLAYER (" + game.getPlayers().get(0).getNickname() + "), " +
                "SECOND PLAYER (" + game.getPlayers().get(1).getNickname() + "), " + "THIRD PLAYER (" + game.getPlayers().get(2).getNickname() + "), " +
                "FOURTH PLAYER (" + game.getPlayers().get(3).getNickname() + ")");
    }




    public void testResetGoldCard1() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        game.startGame();


        System.out.println("The previous gold card (1) had the ID: " + game.getGoldCard1().getId());
            game.resetGoldCard1();
        System.out.println("the new gold card (1) has the ID " +  game.getGoldCard1().getId());
    }



    public void testResetGoldCard2() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        game.startGame();

        System.out.println("The previous gold card (2) had the ID: " + game.getGoldCard2().getId());
            game.resetGoldCard2();

        System.out.println("the new gold card (2) has the ID " +  game.getGoldCard2().getId());
    }



    public void testResetResourceCard1() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        game.startGame();

        System.out.println("The previous resource card (1) had the ID: " + game.getResourceCard1().getId());

            game.resetResourceCard1();
        System.out.println("the new resource card (1) has the ID " +  game.getResourceCard1().getId());
    }



    public void testResetResourceCard2() throws RemoteException {
        Player p1 = new Player();
        Game game = new Game(p1, 1);
        Player p2 = new Player(game);
        Player p3 = new Player(game);
        Player p4 = new Player(game);
        game.setnPlayers(4);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        p1.setGame(game);
        p2.setGame(game);
        p3.setGame(game);
        p4.setGame(game);

        int nPlayers = game.getnPlayers();
        p1.setGame(game);

        p1.setNickname("Papero");
        p2.setNickname("Topolino");
        p3.setNickname("Minnie");
        p4.setNickname("Pluto");

        game.startGame();

        System.out.println("The previous resource card (2) had the ID: " + game.getResourceCard2().getId());

            game.resetResourceCard2();
        System.out.println("the new resource card (2) has the ID " +  game.getResourceCard2().getId());
    }

}