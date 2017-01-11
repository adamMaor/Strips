package Strips;

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
        return null;
    }

    @Override
    public byte getDirection() {
        return 0;
    }
}
