package org.model;

public interface ObjectiveCheckup {
    int countObjectiveCardRealPoints(Player player);

    /**
     * in PLayer we could have the objective cards of the Player and an attribute
     * that identifies the current card that is being checked.
     * The idea of using this parameter is because we don't know if we are going
     * to check an ObjectiveCardNumber or an ObjectiveCardPattern
     */

}
