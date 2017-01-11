package Strips;

import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.Pos;

/**
 * Created by Laptop on 01/11/17.
 */
public class DiffPreCond implements StripsPreCondition{
    public int tlx, tly, brx, bry;
    private Furniture f;

    public DiffPreCond(int tlx, int tly, int brx, int bry, Furniture f) {
        this.tlx = tlx;
        this.tly = tly;
        this.brx = brx;
        this.bry = bry;
        this.f = f;
    }

    @Override
    public Furniture getFurniture() {
        return f;
    }
}
