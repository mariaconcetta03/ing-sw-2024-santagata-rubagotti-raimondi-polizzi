package org.server;

public class Coordinates {
    private int x;
    private int y;

    Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    // these 4 functions return the coordinates which surround the current Coordinate object
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
