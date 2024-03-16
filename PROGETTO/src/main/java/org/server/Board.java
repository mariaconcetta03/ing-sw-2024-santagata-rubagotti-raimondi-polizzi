package org.server;

import jdk.internal.loader.Resource;

import java.util.*;
import java.util.stream.Collectors;

public class Board {
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private Map<Integer,Map<Integer,Integer>> table;
    private int maxExtensionX; //ci servono?? se utilizziamo gli stream li troviamo subito
    private int minExtensionX;
    private int maxExtensionY;
    private int minExtensionY;
    private List<Integer> playedCards;
    private Map<Coordinates,ResourceType> externalResourcesPositions;
    private Map<ResourceType, Integer> numberOfExternalAndCentralResources;
    public void addPlayablePlusUnplayablePositions(Integer id){ //una volta che tramite controller il giocatore sceglie il posto (viene inizializzato attributo position)
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
        playablePositions=playablePositions.stream().filter(n->!(unPlayablePositions.contains(n))).collect(Collectors.toSet());
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
        //sottraggo la risorsa coperta
        numberOfExternalAndCentralResources.get(externalResourcesPositions.get(getCardById(id).position))--;
        externalResourcesPositions.remove(getCardById(id).position); //tolgo l'angolo occupato
        //aggiungo le nuove risorse (4 angoli)
        if(!(getCardById(id).orientation)) {
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_up_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findUpRight(),getCardById(id).get_back_up_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_up_left ())++;
            externalResourcesPositions.put(getCardById(id).position.finfUpLeft(),getCardById(id).get_back_up_left ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_down_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownRight(),getCardById(id).get_back_down_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_down_left ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownLeft(),getCardById(id).get_back_down_left ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_center())++; //abbiamo assunto che la risorsa in centro è solo da un lato
        }else if(getCardById(id).orientation){
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_up_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findUpRight(),getCardById(id).get_front_up_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_up_left ())++;
            externalResourcesPositions.put(getCardById(id).position.finfUpLeft(),getCardById(id).get_front_up_left ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_down_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownRight(),getCardById(id).get_front_down_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_down_left ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownLeft(),getCardById(id).get_front_down_left ());
        }





    }
    public void placeFirstCard(Integer id) {
        table.put(0,new HashMap<>()); //prima abbiamo table completamente vuoto
        table.get(0).put(0,id); //la prima carta non copre nessun angolo
        //le carte iniziali sono le uniche che possono avere più di una risorsa al centro
        //devo aggiungere le risorse al centro
        if(!(getCardById(id).getOrientation())){ //retro sempre con simbolo in centro
            if(getCardById(id).get_back_center().equals(ResourceType.NATURE_FUNGI)) {
                numberOfExternalAndCentralResources.get(NATURE)++;
                numberOfExternalAndCentralResources.get(FUNGI)++;
            }else if(getCardById(id).get_back_center().equals(ResourceType.ANIMAL_INSECT)){
                numberOfExternalAndCentralResources.get(ANIMAL)++;
                numberOfExternalAndCentralResources.get(INSECT)++;
            }else if(getCardById(id).get_back_center().equals(ResourceType.ANIMAL_INSECT_NATURE)){
                numberOfExternalAndCentralResources.get(ANIMAL)++;
                numberOfExternalAndCentralResources.get(INSECT)++;
                numberOfExternalAndCentralResources.get(NATURE)++;
            }else if(getCardById(id).get_back_center().equals(ResourceType.NATURE_ANIMAL_FUNGI)){
                numberOfExternalAndCentralResources.get(NATURE)++;
                numberOfExternalAndCentralResources.get(ANIMAL)++;
                numberOfExternalAndCentralResources.get(FUNGI)++;
            }else{
                numberOfExternalAndCentralResources.get(getCardById(id).get_back_center())++;
            }
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_up_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findUpRight(),getCardById(id).get_back_up_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_up_left ())++;
            externalResourcesPositions.put(getCardById(id).position.finfUpLeft(),getCardById(id).get_back_up_left ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_down_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownRight(),getCardById(id).get_back_down_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_back_down_left ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownLeft(),getCardById(id).get_back_down_left ());
        }else if(getCardById(id).getOrientation()){ //non ho risorse al centro
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_up_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findUpRight(),getCardById(id).get_front_up_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_up_left ())++;
            externalResourcesPositions.put(getCardById(id).position.finfUpLeft(),getCardById(id).get_front_up_left ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_down_right ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownRight(),getCardById(id).get_front_down_right ());
            numberOfExternalAndCentralResources.get(getCardById(id).get_front_down_left ())++;
            externalResourcesPositions.put(getCardById(id).position.findDownLeft(),getCardById(id).get_front_down_left ());
        }
    }
    public void addToListANewPlayedCard(Integer id){
        playedCards.add(id);
    }
}
