package org.model;

import CODEX.org.model.AngleType;
import CODEX.org.model.PlayableCard;
import CODEX.org.model.PlayableDeck;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayableDeckTest extends TestCase {

    @Test
    public void testGetFirstCard() {
        PlayableDeck pdeck= PlayableDeck.goldDeck();
        System.out.println("First card id: "+pdeck.getFirstCard().getId());
        for (int i=0; i<39;i++){
            pdeck.getFirstCard();
        }
        assertThrows(EmptyStackException.class, ()->{pdeck.getFirstCard();});
        assertThrows(EmptyStackException.class, ()->{pdeck.checkFirstCard();});
    }

    @Test
    public void testGoldDeck() {
        PlayableDeck pdeck=new PlayableDeck();
        pdeck= PlayableDeck.goldDeck();
        PlayableCard card1= pdeck.getFirstCard();
        System.out.println(card1.hashCode());
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
        System.out.println("Needed FUNGI to place the card: "+card1.getNeededResources().get(AngleType.FUNGI));
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
        System.out.println("Needed NATURE to place the card: "+card2.getNeededResources().get(AngleType.NATURE));
        System.out.println("Needed INSECT to place the card: "+card2.getNeededResources().get(AngleType.INSECT));
        System.out.println("Needed FUNGI to place the card: "+card2.getNeededResources().get(AngleType.FUNGI));
        System.out.println("Needed ANIMAL to place the card: "+card2.getNeededResources().get(AngleType.ANIMAL));
    }
    @Test
    public void testResourceDeck() {
        PlayableDeck resourceDeck=PlayableDeck.resourceDeck();
        System.out.println(resourceDeck.checkFirstCard().getId());
        resourceDeck.shuffleDeck();
        System.out.println(resourceDeck.checkFirstCard().getId());
    }

    @Test
    public void testBaseDeck() {
        PlayableDeck baseDeck=PlayableDeck.baseDeck();
        baseDeck.shuffleDeck();
    }
}