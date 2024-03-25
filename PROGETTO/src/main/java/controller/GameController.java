package controller;
import org.model.*;

import java.io.IOException;
import java.util.*;


public class GameController {
    private Game game;
    private Deck deck = new Deck(new Stack<>());
    int lastRounds = 10;

    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card)
     */
    public void startGame() throws IllegalStateException {
        List<Integer> usedIndexes = new ArrayList<>();
        // crea oggetto Random
        Random random = new Random();
        // genera numero casuale tra 0 e 15
        int number1 = random.nextInt(16);
        int number2 = random.nextInt(16);


        if(game.getState() == Game.GameState.WAITING_FOR_START) {
            lastRound = false;
            usedIndexes = game.startGame();
            game.giveInitialCards();


            for (int i = 0; i< game.getnPlayers(); i++) {
                while (usedIndexes.contains(number1)) {
                    number1 = random.nextInt(16);
                }
                usedIndexes.add(number1);

                while (usedIndexes.contains(number2)) {
                    number2 = random.nextInt(16);
                }
                usedIndexes.add(number2);
                game.getPlayers().get(i).obtainObjectiveCards(game.getObjectiveDeck()[number1], game.getObjectiveDeck()[number2]);
            }
        } else throw new IllegalStateException();
    }




    /**
     * there would be another package (we can call it 'miniController')
     * that can manage the creation of different games between different
     * players (gives a different ID for each game). This was suggested by Cugula the first lesson
     * @param gameCreator
     * @param nPlayers
     */
    public void createGame (Player gameCreator, int nPlayers) throws RuntimeException {
        try {
            game = new Game(gameCreator, nPlayers, SuperController.getNewId, deck.resourceDeck(),
                    deck.goldDeck(), deck.baseDeck(), deck.objectiveDeck()); //the object Deck uses these methods only to return a structure
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @return true if the card was correctly played, false otherwise
     * this method should include the case in which the card placed is the base card
     */
    public boolean playCard(PlayableCard selectedCard, Coordinates position, boolean orientation) {
        Player p1 = game.getPlayers().get(0); //there is a list of players that change its order every time we shift turn
        try {
            p1.playCard(selectedCard, position, orientation);
            if((game.getState()!=Game.GameState.ENDING)&&(p1.getPoints()>20)){
                game.setState(Game.GameState.ENDING);
            }
            return true; //happy ending
        }catch(IllegalArgumentException e){
            return false; //error ending. This has to be forwarded to the model which will restart the cycle
        }
    }






    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param selectedCard is the Card the Players wants to draw
     */
    public void drawCard(PlayableCard selectedCard){ //we can draw a card from one of the decks or from the uncovered cards
        Player currentPlayer= game.getPlayers().get(0);
        currentPlayer.drawCard(selectedCard);
        if((game.getState()!=Game.GameState.ENDING)&&((game.getResourceDeck().isFinished())&&(game.getGoldDeck().isFinished()))){
            game.setState(Game.GameState.ENDING);
        } //if the score become higher than 20 there would be only one another turn to be played
    }





    /**
     * This method invokes a method in game, which does the necessary actions for the next round.
     * If the game state is ENDING, then the last rounds are done. After that, endGame is invoked.
     * we have decided that is the controller the one that manages the changing of turn
     */
    public void nextPhase(){

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
        } else if (game.getState() == Game.GameState.ENDING && lastRounds == 0) {
            endGame();
        } else if (game.getState() == Game.GameState.STARTED) {
            game.nextRound();
        }
    }





    /**
     *
     */
    public void endGame(){
        game.setState(Game.GameState.ENDED);
    }
}
