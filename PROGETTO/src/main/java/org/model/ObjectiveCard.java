package org.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveCard extends Card {

    public enum objectiveCardType{
            PATTERN,
            NUMBER
        }

        private Player player;
        //private objectiveCardType type;
        private int cardPoints; // points the card gives when the player achieves the goal


        /**
         * Class constructor. i've removed the type
         * @param points
         */
        public ObjectiveCard(int points) {
            this.cardPoints = points;
        }

        /** To be Overridden
         * updates player's points
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