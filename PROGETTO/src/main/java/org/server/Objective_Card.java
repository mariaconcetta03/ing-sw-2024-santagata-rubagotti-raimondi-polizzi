package org.server;

public class Objective_Card extends Card {
        private int cardPoints; //points of the card
        private int pointsToPlayer; //points to add to the player (for example if the objective is achieved more times)
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