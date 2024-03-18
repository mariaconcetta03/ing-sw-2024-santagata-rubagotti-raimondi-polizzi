package org.server;

public class Objective_Card extends Card {
        private int cardPoints; // points the card gives when the player achieves the goal
        private int pointsToPlayer; // points to add to the player (for example if the objective is achieved more times)
                                    // example: the card gives you 2 points for each couple of NATURE. If p1 has
                                    // 5 natures, then pointsToPlayer will be 4 (2 x 2)
        public Objective_Card (int points) {
            this.cardPoints = points;
        }

        public void addPointsToPlayer(int points) {
            this.pointsToPlayer = this.pointsToPlayer + points;
        }

        public int getCardPoints(){
            return this.cardPoints;
        }

        public int getPointsToPlayer() {
            return this.pointsToPlayer;
        }

        public void resetPointsToPlayer() { // PointsToPlayer is set to 0, for example to begin the
                                            // counting of points for another different player
            this.pointsToPlayer = 0;
        }
}