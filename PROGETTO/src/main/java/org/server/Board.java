package org.server;

import jdk.internal.loader.Resource;

import java.util.*;

public class Board {
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private Map<Integer,Map<Integer,Integer>> table;
    private int maxExtensionX;
    private int minExtensionX;
    private int maxExtensionY;
    private int minExtensionY;
    private List<Integer> playedCards;
    private int[] numResourcesPlaced;
    public void addPlayable&UnplayablePositions(Integer id){ //una volta che tramite controller il giocatore sceglie il posto (viene inizializzato attributo position)
        Card c=getCardById(id);
        if(c.orientation) {
            if (c.get_front_up_right().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findUpRight());
            } else if (!(c.get_front_up_right().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findUpRight());
            } else if (c.get_front_up_left().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findUpLeft());
            }else if (!(c.get_front_up_left().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findUpLeft());
            } else if (c.get_front_down_right().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findDownRight());
            } else if (!(c.get_front_down_right().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findDownRight());
            } else if (c.get_front_down_left().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findDownLeft());
            }else if (!(c.get_front_down_left().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findDownLeft());
            }
        }else if(!(c.orientation)) {
            if (c.get_back_up_right().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findUpRight());
            } else if (!(c.get_back_up_right().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findUpRight());
            } else if (c.get_back_up_left().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findUpLeft());
            }else if (!(c.get_back_up_left().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findUpLeft());
            } else if (c.get_back_down_right().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findDownRight());
            } else if (!(c.get_back_down_right().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findDownRight());
            } else if (c.get_back_down_left().equals(ResourceType.ABSENT)) {
                unPlayablePositions.add(c.position.findDownLeft());
            }else if (!(c.get_back_down_left().equals(ResourceType.ABSENT))) {
                playablePositions.add(c.position.findDownLeft());
            }

        }


    }
    public void checkPlayablePositions(){

    }
    public void addResource(int resource, int quantity){

    }
    public void subtractResource(int resource, int quantity) {

    }
    public int getMaxExtensionX() {
        return maxExtensionX;
    }

    public void setMaxExtensionX(int maxExtensionX) {
        this.maxExtensionX = maxExtensionX;
    }


    public int getMinExtensionX() {
        return minExtensionX;
    }

    public void setMinExtensionX(int minExtensionX) {
        this.minExtensionX = minExtensionX;
    }

    public int getMaxExtensionY() {
        return maxExtensionY;
    }

    public void setMaxExtensionY(int maxExtensionY) {
        this.maxExtensionY = maxExtensionY;
    }

    public int getMinExtensionY() {
        return minExtensionY;
    }

    public void setMinExtensionY(int minExtensionY) {
        this.minExtensionY = minExtensionY;
    }
    public void placeCard(Integer id){ //assumo che nel controller ci sia un metodo per permettere al giocatore di scegliere dove mettere la carta
        int x=getCardById(id).position.getX();
        int y=getCardById(id).position.getY();
        if(!(table.containsKey(x))){
            table.put(x, new HashMap<>());
        }
        table.get(x).put(y,id);

    }
    public void placeFirstCard(Integer id) {
        table.put(0,new HashMap<>()); //prima abbiamo table completamente vuoto
        table.get(0).put(0,id);
    }
}
