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
    private Map<Coordinates, Playable_Card.ResourceType> externalResourcesPositions;
    private Map<Playable_Card.ResourceType, Integer> numberOfExternalAndCentralResources;
    public void addPlayablePlusUnplayablePositions(Integer id){ //una volta che tramite controller il giocatore sceglie il posto (viene inizializzato attributo position)
        Playable_Card c=Codex.getInstance().getCardById(id);
        if(c.getOrientation()) {
            if (c.get_front_up_right().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findUpRight());
            } else if (!(c.get_front_up_right().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findUpRight());
            } else if (c.get_front_up_left().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findUpLeft());
            }else if (!(c.get_front_up_left().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findUpLeft());
            } else if (c.get_front_down_right().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findDownRight());
            } else if (!(c.get_front_down_right().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findDownRight());
            } else if (c.get_front_down_left().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findDownLeft());
            }else if (!(c.get_front_down_left().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findDownLeft());
            }
        }else if(!(c.getOrientation())) {
            if (c.get_back_up_right().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findUpRight());
            } else if (!(c.get_back_up_right().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findUpRight());
            } else if (c.get_back_up_left().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findUpLeft());
            }else if (!(c.get_back_up_left().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findUpLeft());
            } else if (c.get_back_down_right().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findDownRight());
            } else if (!(c.get_back_down_right().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findDownRight());
            } else if (c.get_back_down_left().equals(Playable_Card.ResourceType.ABSENT)) {
                unPlayablePositions.add(c.getPosition().findDownLeft());
            }else if (!(c.get_back_down_left().equals(Playable_Card.ResourceType.ABSENT))) {
                playablePositions.add(c.getPosition().findDownLeft());
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
        Playable_Card tmp = (Playable_Card) Codex.getInstance().getCardById(id);
        int x=tmp.getPosition().getX();
        int y=tmp.getPosition().getY();
        if(!(table.containsKey(x))){
            table.put(x, new HashMap<>());
        }
        table.get(x).put(y,id);
        //sottraggo la risorsa coperta
        numberOfExternalAndCentralResources.get(externalResourcesPositions.get(tmp.getPosition()))--;
        externalResourcesPositions.remove(tmp.getPosition()); //tolgo l'angolo occupato
        //aggiungo le nuove risorse (4 angoli)
        if(!(tmp.getOrientation())) {
            numberOfExternalAndCentralResources.get(tmp.get_back_up_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findUpRight(),tmp.get_back_up_right ());
            numberOfExternalAndCentralResources.get(tmp.get_back_up_left ())++;
            externalResourcesPositions.put(tmp.getPosition().finfUpLeft(),tmp.get_back_up_left ());
            numberOfExternalAndCentralResources.get(tmp.get_back_down_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownRight(),tmp.get_back_down_right ());
            numberOfExternalAndCentralResources.get(tmp.get_back_down_left ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownLeft(),tmp.get_back_down_left ());
            numberOfExternalAndCentralResources.get(tmp.get_back_center())++; //abbiamo assunto che la risorsa in centro è solo da un lato
        }else if(tmp.getOrientation()){
            numberOfExternalAndCentralResources.get(tmp.get_front_up_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findUpRight(),tmp.get_front_up_right ());
            numberOfExternalAndCentralResources.get(tmp.get_front_up_left ())++;
            externalResourcesPositions.put(tmp.getPosition().finfUpLeft(),tmp.get_front_up_left ());
            numberOfExternalAndCentralResources.get(tmp.get_front_down_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownRight(),tmp.get_front_down_right ());
            numberOfExternalAndCentralResources.get(tmp.get_front_down_left ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownLeft(),tmp.get_front_down_left ());
        }





    }
    public void placeFirstCard(Integer id) {
        table.put(0,new HashMap<>()); //prima abbiamo table completamente vuoto
        table.get(0).put(0,id); //la prima carta non copre nessun angolo
        Playable_Card tmp = (Playable_Card) Codex.getInstance().getCardById(id);
        //le carte iniziali sono le uniche che possono avere più di una risorsa al centro
        //devo aggiungere le risorse al centro
        if(!(tmp.getOrientation())){ //retro sempre con simbolo in centro
            if(tmp.get_back_center().equals(Playable_Card.ResourceType.NATURE_FUNGI)) {
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.NATURE)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.FUNGI)++;
            }else if(tmp.get_back_center().equals(Playable_Card.ResourceType.ANIMAL_INSECT)){
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.ANIMAL)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.INSECT)++;
            }else if(tmp.get_back_center().equals(Playable_Card.ResourceType.ANIMAL_INSECT_NATURE)){
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.ANIMAL)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.INSECT)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.NATURE)++;
            }else if(tmp.get_back_center().equals(Playable_Card.ResourceType.NATURE_ANIMAL_FUNGI)){
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.NATURE)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.ANIMAL)++;
                numberOfExternalAndCentralResources.get(Playable_Card.ResourceType.FUNGI)++;
            }else{
                numberOfExternalAndCentralResources.get(tmp.get_back_center())++;
            }
            numberOfExternalAndCentralResources.get(tmp.get_back_up_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findUpRight(),tmp.get_back_up_right ());
            numberOfExternalAndCentralResources.get(tmp.get_back_up_left ())++;
            externalResourcesPositions.put(tmp.getPosition().finfUpLeft(),tmp.get_back_up_left ());
            numberOfExternalAndCentralResources.get(tmp.get_back_down_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownRight(),tmp.get_back_down_right ());
            numberOfExternalAndCentralResources.get(tmp.get_back_down_left ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownLeft(),tmp.get_back_down_left ());
        }else if(tmp.getOrientation()){ //non ho risorse al centro
            numberOfExternalAndCentralResources.get(tmp.get_front_up_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findUpRight(),tmp.get_front_up_right ());
            numberOfExternalAndCentralResources.get(tmp.get_front_up_left ())++;
            externalResourcesPositions.put(tmp.getPosition().finfUpLeft(),tmp.get_front_up_left ());
            numberOfExternalAndCentralResources.get(tmp.get_front_down_right ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownRight(),tmp.get_front_down_right ());
            numberOfExternalAndCentralResources.get(tmp.get_front_down_left ())++;
            externalResourcesPositions.put(tmp.getPosition().findDownLeft(),tmp.get_front_down_left ());
        }
    }
    public void addToListANewPlayedCard(Integer id){
        playedCards.add(id);
    }
}
