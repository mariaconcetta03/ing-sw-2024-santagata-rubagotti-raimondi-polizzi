package org.server;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Objective_Card_pattern {
    private Coordinates currentPosition;
    private Strategy chosenStrategy;
    private Objective_Card objectiveCardSelected;
    private Set<Objective_Card> playerObjectiveCards;
    private Board board;
    private List<Integer> listOfPlayedCards;

    public void setObjectiveCard(Objective_Card objectiveCardSelected) {
        this.objectiveCardSelected = objectiveCardSelected;
    }

    public void setCurrentPosition(Coordinates currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void divideObjectiveCards() {
        Map<Color,Set<Objective_Card>> cardsDividedByColor= new HashMap<>();
        cardsDividedByColor.put(Color.RED,new HashSet<>());
        cardsDividedByColor.put(Color.BLUE,new HashSet<>());
        cardsDividedByColor.put(Color.GREEN,new HashSet<>());
        cardsDividedByColor.put(Color.YELLOW,new HashSet<>());
        for(Objective_Card c : playerObjectiveCards){
            cardsDividedByColor.get(c.getPredominantColor()).add(c);
        }
        for(Color c : cardsDividedByColor.keySet()){
            if(!(cardsDividedByColor.get(c).isEmpty())){
                for(Objective_Card d:cardsDividedByColor.get(c)){
                    List<Integer> cardsWithTheSameColor=listOfPlayedCards.stream().filter(Integer t->t.getCardById().getColor().equals(c)).collect(Collectors.toList());
                    if(d.getType().equals(ObjectiveCardPatternType.L_PATTERN)){
                        Strategy strategy=new Lstrategy; //in base al colore setto la simmetria del pattern che mi interessa
                        strategy.findPattern(c,cardsWithTheSameColor); //conta anche i punti e va a modificare gli attributi di Objective_card
                    }else if(d.getType().equals(ObjectiveCardPatternType.DIAGONAL_PATTERN)){
                        Strategy strategy=new DiagonalStrategy();
                        strategy.findPattern(c,cardsWithTheSameColor);
                    }
                }
            }
        }
    }
    public void createAListOfPlayedCards(int table[][]){

    }


    public Coordinates getCurrentPosition() {
        return currentPosition;
    }

    public Strategy getChosenStrategy() {
        return chosenStrategy;
    }

    public Objective_Card getObjectiveCardSelected() {
        return objectiveCardSelected;
    }

    public Set<Objective_Card> getPlayerObjectiveCards() {
        return playerObjectiveCards;
    }

    public void setPlayerObjectiveCards(Set<Objective_Card> playerObjectiveCards) {
        this.playerObjectiveCards = playerObjectiveCards;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


    public List<Integer> getListOfPlayedCards() {
        return listOfPlayedCards;
    }
}
