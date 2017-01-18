package Strips;

import Logic.Diff;
import Logic.Furniture;

/**
 * Created by Adam on 18/01/2017.
 */
public class DiffLegalPreCond implements StripsPreCondition {
    private Furniture f;
    private Diff diff;

    public DiffLegalPreCond(Furniture furniture, Diff diff) {
        this.f = furniture;
        this.diff = diff;
    }

    @Override
    public Furniture getFurniture() {
        return f;
    }

    public Diff getDiff() {
        return diff;
    }

    @Override
    public String toString() {
        return "LegalDiff(" + f.getID() + ", " + diff.getTlx() + ", " + diff.getTly() + ", " + diff.getBrx() + ", " + diff.getBry() + ")";
    }
}
