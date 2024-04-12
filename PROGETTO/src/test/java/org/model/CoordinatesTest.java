package org.model;

import junit.framework.TestCase;
public class CoordinatesTest extends TestCase {

    public void testFindUpRight() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findUpRight();
        assertEquals(newPosition.getX(),initialPosition.getX()+1);
        assertEquals(newPosition.getY(), initialPosition.getY()+1);
    }

    public void testFindUpLeft() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findUpLeft();
        assertEquals(newPosition.getX(),initialPosition.getX()-1);
        assertEquals(newPosition.getY(), initialPosition.getY()+1);
    }

    public void testFindDownRight() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findDownRight();
        assertEquals(newPosition.getX(),initialPosition.getX()+1);
        assertEquals(newPosition.getY(), initialPosition.getY()-1);
    }

    public void testFindDownLeft() {
        Coordinates initialPosition= new Coordinates(1,1);
        Coordinates newPosition= initialPosition.findDownLeft();
        assertEquals(newPosition.getX(),initialPosition.getX()-1);
        assertEquals(newPosition.getY(), initialPosition.getY()-1);
    }
}