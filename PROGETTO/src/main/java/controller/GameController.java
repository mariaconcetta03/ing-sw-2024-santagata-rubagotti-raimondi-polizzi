package controller;
import org.model.Coordinates;
import org.model.Game;
import org.model.PlayableCard;
import org.model.Player;

import java.util.*;
public class GameController {
    private Game game;

    /**
     *
     */
    public void startGame(){
        game.setState(Game.GameState.WAITING_FOR_START);
        game.startGame();
    }

    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @return true if the card was correctly played, false otherwise
     */
    public boolean playCard(PlayableCard selectedCard, Coordinates position, boolean orientation) {
        Player p1 = game.getPlayers().get(0);
        try {
            p1.playCard(selectedCard, position, orientation);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }

    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param selectedCard is the Card the Players wants to draw
     */
    public void drawCard(PlayableCard selectedCard){
        Player currentPlayer= game.getPlayers().get(0);
        currentPlayer.drawCard(selectedCard);
    }

    /**
     *
     */
    public void nextPhase(){

    }

    /**
     *
     */
    public void endGame(){
        game.setState(Game.GameState.ENDED);
    }
}
