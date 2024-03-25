package org.model;

public class Objective_card_pattern extends ObjectiveCard{
    private Coordinates positionCard1; //the next card of the pattern composed by three cards
    private Coordinates positionCard2; //the third card of the pattern
    private Pawn card0Color;
    private Pawn card1Color;
    private Pawn card2Color;
    /**
     * Class constructor
     *
     * @param points
     * @param type
     */
    public Objective_card_pattern(int points, objectiveCardType type) {
        super(points, type);
    }
}
