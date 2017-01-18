package Strips;

import Logic.Diff;
import Logic.Furniture;

import java.util.ArrayList;


/**
 * Created by Laptop on 01/11/17.
 */
public class DiffPreCond implements StripsPreCondition{
    
    private Diff diff;
    private Furniture f;
    private ArrayList<StripsOperator> nextMoveList;
    int currentMoveIndex = 0;

    public DiffPreCond(Diff diff, Furniture f, StripsHeuristics h) {
        this.diff = diff;
        this.f = f;
        nextMoveList = null;
    }
    public Diff getDiff() {
        return diff;
    }
    @Override
    public Furniture getFurniture() {
        return f;
    }

    public StripsOperator getNextMove(){

        if (nextMoveList.size() > currentMoveIndex) {
            return nextMoveList.get(currentMoveIndex++);
        }
        return null;
    }


    @Override
    public String toString() {
        return "Diff(" + f.getID() + ", " + diff.getTlx() + ", " + diff.getTly() + ", " + diff.getBrx() + ", " + diff.getBry() + ")";
    }

    public void setMoves(StripsHeuristics heuristics) {
        if (nextMoveList == null ){
            nextMoveList = heuristics.getMovesList(this);
        }
    }
}
