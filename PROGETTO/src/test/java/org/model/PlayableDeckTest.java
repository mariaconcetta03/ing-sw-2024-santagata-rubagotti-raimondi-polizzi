package org.model;

import junit.framework.TestCase;

public class PlayableDeckTest extends TestCase {

    public void testAddCard() {
    }

    public void testGetFirstCard() {
    }

    public void testGoldDeck() {
        PlayableDeck pdeck= PlayableDeck.goldDeck();
        PlayableCard card= pdeck.getFirstCard();
        System.out.println("Id: "+card.getId());
        System.out.println("Fronte basso sinistra: "+card.get_front_down_left());
        System.out.println("Fronte basso destra: "+card.get_front_down_right());
        System.out.println("Fronte alto sinistra: "+card.get_front_up_left());
        if(card.get_front_up_left().equals(AngleType.ABSENT)){
            System.out.println("Angolo in alto a sinistra assente!");
        }
        System.out.println("Fronte alto destra: "+card.get_front_up_right());
        System.out.println("Dietro basso sinistra: "+card.get_back_down_left());
        System.out.println("Dietro basso destro: "+card.get_back_down_right());
        System.out.println("Dietro alto sinistra: "+card.get_back_up_left());
        System.out.println("Dietro alto destra: "+card.get_back_up_right());
        System.out.println("Orientazione: "+card.getOrientation());
        System.out.println("Piume?: "+card.isFeatherToReceivePoints());
        System.out.println("Cover angle?: "+card.isCoverAngleToReceivePoints());
        System.out.println("Jar?: "+card.isJarToReceivePoints());
        System.out.println("Scroll?: "+card.isScrollToReceivePoints());
        System.out.println("Risorse al centro: "+card.getCentralResources().get(0)+", "+card.getCentralResources().get(1));
        System.out.println("Risorse necessarie: "+card.getNeededResources().get(CentralType.FUNGI));

    }

    public void testResourceDeck() {
    }

    public void testBaseDeck() {
    }
}