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
        //private int pointsToPlayer; // points to add to the player (for example if the objective is achieved more times)
                                    // example: the card gives you 2 points for each couple of NATURE. If p1 has
                                    // 5 natures, then pointsToPlayer will be 4 (2 x 2)


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
        public void addPointsToPlayer(Player player) {
        }

        /**
         * Getter
         * @return cardPoints
         */
        public int getCardPoints(){
                return this.cardPoints;
            }

        /**
         * Getter
          * @return pointsToPlayer

        public int getPointsToPlayer() {
                return this.pointsToPlayer;
            }



        /**
         * PointsToPlayer is set to 0, for example to begin the counting of points for another different player

        public void resetPointsToPlayer() { //we have first to give the points to the right player
                this.pointsToPlayer = 0;
        }

        */


}