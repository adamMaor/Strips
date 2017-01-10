package Logic;

import java.awt.*;

/**
 * Created by Adam on 09/01/2017.
 */
public class Furniture {
    private String ID;
    private FurnitureLocation location;
    private Color color;


    public Furniture(String ID, FurnitureLocation location, Color fColor) {
        this.ID = ID;
        this.location = location;
        this.color = fColor;
    }

    public FurnitureLocation getLocation() {
        return location;
    }

    public void setLocation(FurnitureLocation location) {
        this.location = location;
    }

    public String getID() {
        return ID;
    }

    public Color getColor() {
        return color;
    }

    public void moveUp() {
        location.moveUp();
    }

    public void moveDown() {
        location.moveDown();

    }

    public void moveLeft() {
        location.moveLeft();

    }

    public void moveRight() {
        location.moveRight();

    }

    public void rotateLeft() {

    }

    public void rotateRight() {

    }


}
