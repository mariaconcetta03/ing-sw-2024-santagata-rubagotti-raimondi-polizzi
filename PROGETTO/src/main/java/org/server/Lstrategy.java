package org.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Lstrategy implements Strategy{
    void findPattern(List<Coordinates> cardsWithTheSameColor, Color c){
        Set<Integer> setX= new HashSet<>();
        setX= cardsWithTheSameColor.stream().map(Coordinates::getX).collect(Collectors.toSet());
        Map<Integer, Map<Integer,Integer>> localVariable= getTable();
        Set<Integer> restrictedXKeys= localVariable.keySet().stream().filter(n->setX.contains(n)).collect(Collectors.toSet());
        for(Integer key:restrictedXKeys){

        }
    }

}
