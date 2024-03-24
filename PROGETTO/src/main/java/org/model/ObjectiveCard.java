package org.model;

public class ObjectiveCard extends Card {
        private int cardPoints; // points the card gives when the player achieves the goal
        private int pointsToPlayer; // points to add to the player (for example if the objective is achieved more times)
                                    // example: the card gives you 2 points for each couple of NATURE. If p1 has
                                    // 5 natures, then pointsToPlayer will be 4 (2 x 2)

        /**
         * Class constructor
         * @param points
         */
        public ObjectiveCard(int points) {
            this.cardPoints = points;
            this.pointsToPlayer = 0;
        }

        /**
         * updates player's points
         * @param points
         */
        public void addPointsToPlayer(int points) {
            this.pointsToPlayer = this.pointsToPlayer + points;
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
         */
        public int getPointsToPlayer() {
                return this.pointsToPlayer;
            }

        /**
         * PointsToPlayer is set to 0, for example to begin the counting of points for another different player
         */
        public void resetPointsToPlayer() {
                this.pointsToPlayer = 0;
            }
}