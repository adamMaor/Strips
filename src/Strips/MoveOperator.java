package Strips;

import Constants.Constants;
import Logic.Furniture;

/**
 * Created by Laptop on 01/11/17.
 */
public class MoveOperator implements StripsOperator {
    private Furniture f;
    private byte direction;

    public MoveOperator(Furniture f, byte direction) {
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
        return "Move(" + f.getID() + ", " + dirString + ")";
    }
}
