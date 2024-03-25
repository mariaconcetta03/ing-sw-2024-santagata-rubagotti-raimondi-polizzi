package org.model;

import java.util.Map;

public class ObjectiveCheckupPatternType implements ObjectiveCheckup{
    public int countObjectiveCardRealPoints(Player player){
        //important: each player has his/her own Board so we have to maintain a reference to the PLayer
        //after we gather our variables and instruments...:
        Map<Coordinates,color> copy; //assegnazione (con apportuno metodo di Map) con qualcosa creata in Board nell'evenienza qualcuno abbiamo pescato un objectiveCardPattern
        int counter=0;
        for(Coordinates k : copy.keySet()){ //that is to have an idea of what we ca do
            Coordinates t= new Coordinates(k.getX()+objectiveCard.getPositionCard1().getX(), k.getY()+objectiveCard.getPositionCard1().getY());
            if(copy.get(t)==getCard1Color()&&....){
                counter++;
                copy.remove(k);
                copy.remove(t); //e rimuoviamo anche la terza carta dopo
            }
        }
        return counter;
    }
}
