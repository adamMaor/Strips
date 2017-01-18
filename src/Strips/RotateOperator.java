package Strips;

import Constants.Constants;
import Logic.Furniture;

import java.util.ArrayList;

/**
 * Created by Laptop on 01/11/17.
 */
public class RotateOperator implements StripsOperator {
    private Furniture f;
    private byte direction;
    ArrayList<StripsPreCondition> pcList;

    public RotateOperator(Furniture f, byte direction) {
        this.f = f;
        this.direction = direction;
    }

    @Override
    public Furniture getFurniture() {
        return f;
    }

    @Override
    public byte getDirection() {
        return direction;
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
        return "Rotate(" + f.getID() + ", " + dirString + ")";
    }
}
