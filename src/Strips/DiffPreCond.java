package Strips;

import Logic.Diff;
import Logic.Furniture;


/**
 * Created by Laptop on 01/11/17.
 */
public class DiffPreCond implements StripsPreCondition{
    
    private Diff diff;
    private Furniture f;

    public DiffPreCond(Diff diff, Furniture f) {
        this.diff = diff;
        this.f = f;
    }
    public Diff getDiff() {
        return diff;
    }
    @Override
    public Furniture getFurniture() {
        return f;
    }
}
