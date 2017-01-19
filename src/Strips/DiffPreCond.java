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

    public StripsOperator getNextMove(StripsHeuristics heuristics, byte lastMoveDirection){
        if (nextMoveList == null ){
            System.out.println("requesting moves for operator: " + this.toString() + ", last Direction was: " + lastMoveDirection);
            nextMoveList = heuristics.getMovesList(this, lastMoveDirection);
            System.out.println("list is: " + nextMoveList);
        }
        if (nextMoveList.size() > currentMoveIndex) {
            return nextMoveList.get(currentMoveIndex++);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DiffPreCond other = (DiffPreCond)obj;
        return (this.getFurniture().getID().equals(other.getFurniture().getID())
                && this.getDiff().equals(other.getDiff()));

    }

    @Override
    public String toString() {
        return "Diff(" + f.getID() + ", " + diff.getTlx() + ", " + diff.getTly() + ", " + diff.getBrx() + ", " + diff.getBry() + ")";
    }

}
