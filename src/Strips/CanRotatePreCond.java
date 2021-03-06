package Strips;

import Constants.Constants;
import Logic.Furniture;

/**
 * Created by Laptop on 01/11/17.
 */
public class CanRotatePreCond implements StripsPreCondition {
    private Furniture f;
    private byte direction;

    public CanRotatePreCond(Furniture f, byte direction) {
        this.f = f;
        this.direction = direction;
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
        String dirString = "Right";
        if (direction == Constants.Directions.LEFT) {
            dirString = "Left";
        }
        return "CanRotate(" + f.getID() + ", " + dirString + ")";
    }
}
