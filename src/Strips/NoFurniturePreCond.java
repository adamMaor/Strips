package Strips;

import Logic.Furniture;

/**
 * Created by Laptop on 01/17/17.
 */
public class NoFurniturePreCond implements StripsPreCondition {

    private Furniture f;
    private byte direction;

    public NoFurniturePreCond(Furniture f, byte direction) {
        this.f = f;
        this.direction = direction;
    }

    public byte getDirection() {
        return direction;
    }
    @Override
    public Furniture getFurniture() {
        return null;
    }
}
