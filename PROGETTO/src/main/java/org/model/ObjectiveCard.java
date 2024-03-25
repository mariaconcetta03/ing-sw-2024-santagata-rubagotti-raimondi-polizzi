package org.model;

public class ObjectiveCard extends Card {

    public enum objectiveCardType{
            PATTERN,
            NUMBER

        }
        private Player player;
        private objectiveCardType type;
        private int cardPoints; // points the card gives when the player achieves the goal
        private int pointsToPlayer; // points to add to the player (for example if the objective is achieved more times)
                                    // example: the card gives you 2 points for each couple of NATURE. If p1 has
                                    // 5 natures, then pointsToPlayer will be 4 (2 x 2)

        /**
         * Class constructor
         * @param points
         */
        public ObjectiveCard(int points,objectiveCardType type) {
            this.cardPoints = points;
            this.pointsToPlayer = 0;
            this.type=type;
        }

    /**
     * this method has to be called in the moment we select one of the players (in the case
     * the objective card is shared between the players) to start counting the associated points
     * @param player
     */
    public void setPlayer(Player player) {
            this.player = player;
        }

        /**
         * updates player's points
         */
        public void addPointsToPlayer() {
            int points=0;
            ObjectiveCheckup objectiveCheckup; //that's not an attribute because an instance of ObjectiveCard could remain unextracted
            if(this.type.equals(objectiveCardType.PATTERN)){
                objectiveCheckup=new ObjectiveCheckupPatternType();
                points=objectiveCheckup.countObjectiveCardRealPoints(this.player);
            }
            else if(this.type.equals(objectiveCardType.NUMBER)){
                objectiveCheckup=new ObjectiveCheckupNumberType();
                points=objectiveCheckup.countObjectiveCardRealPoints(this.player);
            }
            this.pointsToPlayer = this.pointsToPlayer * points;
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