package org.model;

public class Coordinates {
    private int x;
    private int y;

    Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter method
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Getter method
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Setter method
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setter method
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * these 4 functions return the coordinates which surround the current Coordinate object
     * @return Coordinates(x,y)
     */
    public Coordinates findUpRight() {
        return new Coordinates(this.x + 1, this.y + 1);
    }
    public Coordinates findUpLeft() {
        return new Coordinates(this.x - 1, this.y + 1);
    }
    public Coordinates findDownRight() {
        return new Coordinates(this.x + 1, this.y - 1);
    }
    public Coordinates findDownLeft() {
        return new Coordinates(this.x - 1, this.y - 1);
    }
}
