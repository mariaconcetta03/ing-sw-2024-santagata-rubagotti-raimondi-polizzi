package org.model;

import CODEX.org.model.Coordinates;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

public class CoordinatesTest extends TestCase {
    @Test
    public void testEquals(){
        Coordinates coordinates1= new Coordinates();
        coordinates1.setX(10);
        coordinates1.setY(20);
        Coordinates coordinates2=new Coordinates(10, 20);
        System.out.println(coordinates1.hashCode());
        System.out.println(coordinates2.hashCode());
        assertTrue(coordinates2.equals(coordinates1));
        assertTrue(coordinates1.equals(coordinates1));
        coordinates2.setX(11);
        assertFalse(coordinates1.equals(coordinates2));
        coordinates2.setX(10);
        coordinates1.setY(21);
        assertFalse(coordinates1.equals(coordinates2));

    }
    @Test
    public void testFindUpRight() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findUpRight();
        assertEquals(newPosition.getX(),initialPosition.getX()+1);
        assertEquals(newPosition.getY(), initialPosition.getY()+1);
    }
    @Test
    public void testFindUpLeft() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findUpLeft();
        assertEquals(newPosition.getX(),initialPosition.getX()-1);
        assertEquals(newPosition.getY(), initialPosition.getY()+1);
    }
    @Test
    public void testFindDownRight() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findDownRight();
        assertEquals(newPosition.getX(),initialPosition.getX()+1);
        assertEquals(newPosition.getY(), initialPosition.getY()-1);
    }
    @Test
    public void testFindDownLeft() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findDownLeft();
        assertEquals(newPosition.getX(),initialPosition.getX()-1);
        assertEquals(newPosition.getY(), initialPosition.getY()-1);
    }
}