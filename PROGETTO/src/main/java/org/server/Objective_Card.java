package org.server;

public class Objective_Card extends Card{
        private int cardPoints;
        private int pointsToPlayer;

        public Objective_Card (int points) {
            this.cardPoints = points;
        }

        public void addPointsToPlayer(int points){
            this.pointsToPlayer = this.pointsToPlayer + points;
        }

        public int getCardPoints(){
            return this.cardPoints;
        }

        public int getPointsToPlayer(){
            return this.pointsToPlayer;
        }
}
