package org.model;

import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.*;
public class BoardTest extends TestCase {
    Player p1 = new Player();
    Board board = new Board(p1);

    public void testSetBoard() {
        for (int i = 2; i<5; i++) {
            board.setBoard(i);
            System.out.println("The dimension of the board with " + i + " players is " + board.getBoardDimensions());
        }
    }




}
