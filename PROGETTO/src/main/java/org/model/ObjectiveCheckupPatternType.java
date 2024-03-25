package org.model;

import java.util.Map;

public class ObjectiveCheckupPatternType implements ObjectiveCheckup{
    public int countObjectiveCardRealPoints(Player player){
        //after we gather our variables and instruments...:
        Map<Coordinates,Card> copy; //assegnazione con qualcosa creata in Board nell'evenienza qualcuno abbiamo pescato un objectiveCardPattern
        int counter=0;
        for(Coordinates k : copy.keySet()){ //that is to have an idea of what we ca do
            if(copy.get(k+objectiveCard.getPositionCard1())==getCard1Color()){

            }
        }
    }
}
