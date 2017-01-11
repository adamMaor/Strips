package Strips;

import Logic.Furniture;

/**
 * Created by Laptop on 01/11/17.
 */
public class CanMovePreCond implements StripsPreCondition {
    private Furniture f;

    public byte getDirection() {
        return direction;
    }

    private byte direction;

    public CanMovePreCond(Furniture f, byte direction) {
        this.f = f;
        this.direction = direction;
    }

    @Override
    public Furniture getFurniture() {
        return f;
    }
}
