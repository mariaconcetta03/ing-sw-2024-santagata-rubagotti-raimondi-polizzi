package org.model;

import junit.framework.TestCase;

public class PlayableDeckTest extends TestCase {

    public void testAddCard() {
    }

    public void testGetFirstCard() {
    }

    public void testGoldDeck() {
        PlayableDeck pdeck= PlayableDeck.goldDeck();
        PlayableCard card1= pdeck.getFirstCard();
        pdeck.shuffleDeck();
        System.out.println("Id: "+card1.getId());
        System.out.println("Front down left corner: "+card1.get_front_down_left());
        System.out.println("Front down right corner: "+card1.get_front_down_right());
        System.out.println("Front up left corner: "+card1.get_front_up_left());
        if(card1.get_front_up_left().equals(AngleType.ABSENT)){
            System.out.println("Front up left corner is absent!");
        }
        System.out.println("Front up right corner: "+card1.get_front_up_right());
        System.out.println("Back down left corner: "+card1.get_back_down_left());
        System.out.println("Back down right corner: "+card1.get_back_down_right());
        System.out.println("Back up left corner: "+card1.get_back_up_left());
        System.out.println("Back up right corner: "+card1.get_back_up_right());
        System.out.println("Orientation: "+card1.getOrientation());
        System.out.println("Need to count feather to score points?: "+card1.isFeatherToReceivePoints());
        System.out.println("Need to cover angle to score points?: "+card1.isCoverAngleToReceivePoints());
        System.out.println("Need to count jar to score points?: "+card1.isJarToReceivePoints());
        System.out.println("Need to count scroll to score points?: "+card1.isScrollToReceivePoints());
        System.out.println("Center resource (card's type): "+card1.getCentralResources().get(0));
        System.out.println("Needed FUNGI to place the card: "+card1.getNeededResources().get(CentralType.FUNGI));
        PlayableCard card2= pdeck.getFirstCard();
        System.out.println("Id: "+card2.getId());
        System.out.println("Front down left corner: "+card2.get_front_down_left());
        System.out.println("Front down right corner: "+card2.get_front_down_right());
        System.out.println("Front up left corner: "+card2.get_front_up_left());
        if(card2.get_front_up_left().equals(AngleType.ABSENT)){
            System.out.println("Front up left corner is absent!");
        }
        System.out.println("Front up right corner: "+card2.get_front_up_right());
        System.out.println("Back down left corner: "+card2.get_back_down_left());
        System.out.println("Back down right corner: "+card2.get_back_down_right());
        System.out.println("Back up left corner: "+card2.get_back_up_left());
        System.out.println("Back up right corner: "+card2.get_back_up_right());
        System.out.println("Orientation: "+card2.getOrientation());
        System.out.println("Need to count feather to score points?: "+card2.isFeatherToReceivePoints());
        System.out.println("Need to cover angle to score points?: "+card2.isCoverAngleToReceivePoints());
        System.out.println("Need to count jar to score points?: "+card2.isJarToReceivePoints());
        System.out.println("Need to count scroll to score points?: "+card2.isScrollToReceivePoints());
        System.out.println("Center resource (card's type): "+card2.getCentralResources().get(0));
        System.out.println("Needed FUNGI to place the card: "+card2.getNeededResources().get(CentralType.FUNGI));


    }

    public void testResourceDeck() {
    }

    public void testBaseDeck() {
    }
}