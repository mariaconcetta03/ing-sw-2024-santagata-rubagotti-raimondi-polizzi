package org.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveCard extends Card {
        private int cardPoints; // points the card gives when the player achieves the goal (only one time)

        /**
         * Class constructor
         * @param points
         */
        public ObjectiveCard(int points) {
            this.cardPoints = points;
        }


        /** To be Overridden
         * updates player's points
         * we have to call this method multiple times changing the player if the objective card is shared
         */
        public int addPointsToPlayer(Player player) {
            return 0;
        }


        /**
         * Getter
         * @return cardPoints
         */
        public int getCardPoints(){
                return this.cardPoints;
            }

}