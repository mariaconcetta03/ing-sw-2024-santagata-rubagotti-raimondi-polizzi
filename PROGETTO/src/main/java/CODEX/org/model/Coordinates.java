package CODEX.org.model;

import java.io.Serializable;

/**
 * This class represents a couple of coordinates: (x,y)
 */
public class Coordinates implements Serializable {
    private int x;
    private int y;


    /**
     * Class constructor
     *
     * @param x coordinate x
     * @param y coordinate y
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Class constructor default
     */
    public Coordinates() {
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinates other = (Coordinates) obj;
        if (this.getX() != other.getX()) {
            return false;
        }
        if (this.getY() != other.getY()) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        final int start = 17;
        int res = 1;
        res = start * res + this.getX();
        res = start * res + this.getY();
        return res;
    }


    /**
     * Getter method
     *
     * @return x
     */
    public int getX() {
        return x;
    }


    /**
     * Getter method
     *
     * @return y
     */
    public int getY() {
        return y;
    }


    /**
     * Setter method
     *
     * @param x is the coordinate x
     */
    public void setX(int x) {
        this.x = x;
    }


    /**
     * Setter method
     *
     * @param y is the coordinate y
     */
    public void setY(int y) {
        this.y = y;
    }


    /**
     * These function returns the coordinates which surround the current Coordinate object (up right)
     *
     * @return Coordinates(x, y)
     */
    public Coordinates findUpRight() {
        return new Coordinates(this.x + 1, this.y + 1);
    }


    /**
     * These function returns the coordinates which surround the current Coordinate object (up left)
     *
     * @return Coordinates(x, y)
     */
    public Coordinates findUpLeft() {
        return new Coordinates(this.x - 1, this.y + 1);
    }


    /**
     * These function returns the coordinates which surround the current Coordinate object (down right)
     *
     * @return Coordinates(x, y)
     */
    public Coordinates findDownRight() {
        return new Coordinates(this.x + 1, this.y - 1);
    }


    /**
     * These function returns the coordinates which surround the current Coordinate object (down left)
     *
     * @return Coordinates(x, y)
     */
    public Coordinates findDownLeft() {
        return new Coordinates(this.x - 1, this.y - 1);
    }

}
