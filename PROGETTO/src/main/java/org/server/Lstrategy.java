package org.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Lstrategy implements Strategy{
    void findPattern(Color c,List<Integer> cardsWithTheSameColor){
        //dopo aver identificato il pattern da cercare in base ai colori dominanti
        //cerco le carte che sono una sopra l'altra
        List<Integer> cardsWithTheSameColorAndOneAboveTheOther=cardsWithTheSameColor.stream().reduce()

    }

}
