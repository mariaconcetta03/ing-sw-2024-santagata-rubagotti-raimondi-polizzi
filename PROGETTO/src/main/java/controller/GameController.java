package controller;
import Exceptions.CardNotOwnedException;
import org.model.*;
import utils.Event;

import java.sql.Timestamp;
import java.util.*;


public class GameController {
    private Game game;
    int lastRounds;
    private List<Player> winners;
    private List<Player> gamePlayers;
    private int numberOfPlayers;
    private int id;

    public GameController(){
        game=null;
        lastRounds=10;
        winners=new ArrayList<>();
        gamePlayers= new ArrayList<>();
        numberOfPlayers=0;
        id=0;
    }

    /**
     * This method creates the Game that will be managed by GameController
     * @param gamePlayers is the List of players that will be in the Game
     */
    public Event createGame (List<Player> gamePlayers){
        game = new Game(gamePlayers, id);
        return Event.OK;
    }

    public Event addPlayer(Player player) /*throws ArrayIndexOutOfBoundsException*/ {
        if (gamePlayers.size() < numberOfPlayers) {
            gamePlayers.add(player);
        } else {
            return Event.FULL_LOBBY;
            //throw new ArrayIndexOutOfBoundsException("This lobby is already full!");
        }
        if (gamePlayers.size() == numberOfPlayers) {
            createGame(gamePlayers);
            try {
                startGame();
            } catch (IllegalStateException e) {
                System.out.println("The game is already started!");
                return Event.GAME_ALREADY_STARTED;
            }
        }
        return Event.OK;
    }

    /**
    public boolean waitingForPlayers(Player player) { //when we call this method we are adding another player
        game.addPlayer(player);
        if (game.getPlayers().size() < game.getnPlayers()) {
            return true; //this method would be called at least another time
        } else {
            startGame(); //do we have to use the parameter game.getPlayers() to identify which game we are talking about?
            return false;
        }
    }
     */

    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card)
     */
    public Event startGame() /*throws IllegalStateException */ {
        /**List<Integer> usedIndexes = new ArrayList<>();
        // crea oggetto Random
        Random random = new Random();
        // genera numero casuale tra 0 e 15
        int number1 = random.nextInt(16);
        int number2 = random.nextInt(16);
         */
        if(game.getState() == Game.GameState.WAITING_FOR_START) {
            game.startGame();
            return Event.OK;
        } else {
            return Event.INVALID_GAME_STATUS;
            ///throw new IllegalStateException();
        }
    }

    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up fase giving the last necessary cards
     * @param nickname
     * @param baseCard
     * @param orientation
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation){
        Player player= ServerController.getPlayerByNickname(nickname);
        player.playBaseCard(orientation, baseCard);
        PlayableCard[][] tmp;
        //for all players in the game
        for(Player p1: game.getPlayers()){
            tmp=p1.getBoard().getTable();
            //if the players haven't all played their baseCard
            if(tmp[p1.getBoard().getBoardDimensions()/2][p1.getBoard().getBoardDimensions()/2]==null){
                return;
            }
        }
        game.giveInitialCards();
    }

    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param nickname is the nickname of the Player that wants to play a card
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @return true if the card was correctly played, false otherwise
     * this method should include the case in which the card placed is the base card
     */
    public Event playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) {
        Player currentPlayer = game.getPlayers().get(0);
        if (!ServerController.getPlayerByNickname(nickname).equals(currentPlayer)) {
            System.out.println("NON E IL TUO TURNO, NON PUOI GIOCARE LA CARTA");
            return Event.NOT_YOUR_TURN;
        }else{
            try {
                currentPlayer.playCard(selectedCard, position, orientation);
                if ((game.getState() != Game.GameState.ENDING) && (currentPlayer.getPoints() > 20)) {
                    game.setState(Game.GameState.ENDING);
                }
                return Event.OK; //happy ending
            } catch (IllegalArgumentException e) {
                return Event.UNABLE_TO_PLAY_CARD; //error ending. This has to be forwarded to the view.
            }
        }
    }

    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param selectedCard is the Card the Players wants to draw
     */
    public Event drawCard(String nickname, PlayableCard selectedCard) { //we can draw a card from one of the decks or from the uncovered cards
        Player currentPlayer = game.getPlayers().get(0);
        if (!ServerController.getPlayerByNickname(nickname).equals(currentPlayer)) {
            System.out.println("NON E IL TUO TURNO, NON PUOI PESCARE");
            return Event.NOT_YOUR_TURN;
        } else {
            currentPlayer.drawCard(selectedCard);
            if ((game.getState() != Game.GameState.ENDING) && ((game.getResourceDeck().isFinished()) && (game.getGoldDeck().isFinished()))) {
                game.setState(Game.GameState.ENDING);
            } //if the score become higher than 20 there would be only one another turn to be played
            return Event.OK;
        }
    }

    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooser is the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     */
    public Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) {
        try {
            chooser.setPersonalObjective(selectedCard);
            return Event.OK;
        }catch(CardNotOwnedException ignored){
            return Event.OBJECTIVE_CARD_NOT_OWNED;
        }
    }

    public void choosePawnColor(Player chooser, Pawn selectedColor) {
        synchronized (game.getAlreadySelectedColors()) {
            if (!game.getAlreadySelectedColors().contains(selectedColor)) {
                chooser.setColor(selectedColor);
                game.getAlreadySelectedColors().add(selectedColor);
                //return Event.GOOD;
            } else {
                //return Event.BAD
            }
        }
    }

    /**
     * This method allows the player to send a text message in the chat
     * @param sender
     * @param receivers
     * @param message
     * @return
     */
    public void sendMessage(Player sender, List<Player> receivers, String message){
       receivers.add(sender);
       Chat tmp;
       if(!game.getChats().isEmpty()) {
           for (Chat chat : game.getChats()) {
               tmp=chat;
               for(Player player:receivers){
                   if(!chat.getUsers().contains(player)){
                       tmp=null;
                       break;
                   }
               }
               if((tmp!=null)&&(tmp==chat)){
                   receivers.remove(sender);
                   tmp.sendMessage(new Message(message, sender, receivers, new Timestamp(System.currentTimeMillis())));
               }
           }
       }
           receivers.remove(sender);
           if (receivers.size() == game.getnPlayers()) {
               tmp=game.startGeneralChat();
               tmp.sendMessage(new Message(message, sender, receivers, new Timestamp(System.currentTimeMillis())));
           }else {
               tmp = game.startChat(sender, receivers.get(0));
               tmp.sendMessage(new Message(message, sender, receivers, new Timestamp(System.currentTimeMillis())));
           }
    }


    /**
     * This method invokes a method in game, which does the necessary actions for the next round.
     * If the game state is ENDING, then the last rounds are done. After that, endGame is invoked.
     * we have decided that is the controller the one that manages the changing of turn
     */
    public Event nextPhase(){

        if (game.getState() == Game.GameState.ENDING && lastRounds == 10) {
            int firstPlayer = 0;
            lastRounds = game.getnPlayers();

            for (int i = 0; i<game.getnPlayers(); i++) {
                if (game.getPlayers().get(i).isFirst()) {
                    firstPlayer = i;
                }
            }
           if (firstPlayer > 0) {
                lastRounds = lastRounds + firstPlayer;
            } else if (firstPlayer == 0) {
                lastRounds = game.getnPlayers();
            }
        }

        if (game.getState() == Game.GameState.ENDING && lastRounds > 0) {
            lastRounds --;
            game.nextRound();
        } else if (game.getState() == Game.GameState.ENDING && lastRounds == 0) { //20 points reached the players will have only another round
            endGame();
        } else if (game.getState() == Game.GameState.STARTED) {
            game.nextRound(); //the order of the players in the list will be changed
        }
    }


    /**
     * This method ends the game. It sets the game state to ENDED, checks all the objectives (2 common objs
     * and 1 personalObj) and adds the points to the correct player.
     * Finally, it checks the winner (or winners) of the game, and puts them in a list called "winners"
     */
    public void endGame(){
        // setting the game state to ENDED
        game.setState(Game.GameState.ENDED);

        // checking the objectives and adding the points to the correct player
        ObjectiveCard commonObj1 = game.getObjectiveCard1();
        ObjectiveCard commonObj2 = game.getObjectiveCard2();
        ObjectiveCard personalObjective;
        for (int i = 0; i<game.getnPlayers(); i++) {
            personalObjective = game.getPlayers().get(i).getPersonalObjective();
            commonObj1.addPointsToPlayer(game.getPlayers().get(i));
            commonObj2.addPointsToPlayer(game.getPlayers().get(i));
            personalObjective.addPointsToPlayer(game.getPlayers().get(i));
        }

        // checking the winner(s)
        winners = game.winner();
    }

    public Game getGame() {
        return game;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
