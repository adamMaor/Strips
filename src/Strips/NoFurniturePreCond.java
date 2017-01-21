package Strips;

import Constants.Constants;
import Logic.Furniture;

/**
 * Created by Laptop on 01/17/17.
 */
public class NoFurniturePreCond implements StripsPreCondition {

    private Furniture f;
    private Furniture encounteredFurniture;
    private byte direction;

    public NoFurniturePreCond(Furniture f, byte direction) {
        this.f = f;
        this.direction = direction;
        this.encounteredFurniture = null;
    }

    public byte getDirection() {
        return direction;
    }
    @Override
    public Furniture getFurniture() {
        return f;
    }

    @Override
    public String toString() {
        String dirString = "";
        switch (direction) {
            case Constants.Directions.UP:
                dirString = "Up";
                break;
            case Constants.Directions.LEFT:
                dirString = "Left";
                break;
            case Constants.Directions.DOWN:
                dirString = "Down";
                break;
            case Constants.Directions.RIGHT:
                dirString = "Right";
                break;
            case Constants.Directions.NONE:
                dirString = "None";
                break;
        }
        return "HasNoFurniture(" + f.getID() + ", " + dirString + ")";
    }

    public void setFurnitureEncounterd(Furniture resFurniture) {
        this.encounteredFurniture = resFurniture;
    }

    public Furniture getEncounteredFurniture() {
        return encounteredFurniture;
    }

}
