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

    public Coordinates getPositionCard1() {
        return positionCard1;
    }

    public void setPositionCard1(Coordinates positionCard1) { //these are relative coordinates maybe initialized by Jason
        this.positionCard1 = positionCard1;
    }

    public Coordinates getPositionCard2() {
        return positionCard2;
    }

    public void setPositionCard2(Coordinates positionCard2) {
        this.positionCard2 = positionCard2;
    }

    public Pawn getCard0Color() {
        return card0Color;
    }

    public void setCard0Color(Pawn card0Color) {
        this.card0Color = card0Color;
    }

    public Pawn getCard1Color() {
        return card1Color;
    }

    public void setCard1Color(Pawn card1Color) {
        this.card1Color = card1Color;
    }

    public Pawn getCard2Color() {
        return card2Color;
    }

    public void setCard2Color(Pawn card2Color) {
        this.card2Color = card2Color;
    }
}
