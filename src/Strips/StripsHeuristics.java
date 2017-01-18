package Strips;

import Constants.Constants;
import Logic.Diff;
import Logic.Furniture;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Laptop on 01/17/17.
 */
public class StripsHeuristics {

    public ArrayList<StripsPreCondition> getSortedPreConditions(PreConditionAnd pcAnd){
        ArrayList<StripsPreCondition> diffList = new ArrayList<StripsPreCondition>();
        ArrayList<StripsPreCondition> wallsList = new ArrayList<StripsPreCondition>();
        ArrayList<StripsPreCondition> otherList = new ArrayList<StripsPreCondition>();
        for (StripsPreCondition pc : pcAnd.getPcList()) {
            if (pc instanceof DiffPreCond) {
                diffList.add(pc);
            }
            else if (pc instanceof NoWallsPreCond) {
                wallsList.add(pc);
            }
            else {
                otherList.add(pc);
            }
        }
        ArrayList<StripsPreCondition> resList = new ArrayList<StripsPreCondition>();
        resList.addAll(diffList);
        resList.addAll(otherList);
        resList.addAll(wallsList);
        return resList;
    }

    public ArrayList<StripsOperator> getMovesList(StripsPreCondition pc) {
        ArrayList<StripsOperator> resList = new ArrayList<StripsOperator>();
        if (pc instanceof DiffPreCond) {
            DiffPreCond dpc = (DiffPreCond)pc;
            Furniture f = pc.getFurniture();
            Diff diff = new Diff(f.getVirtualLocation(), f.getLocation());
            int tlx = diff.getTlx();
            int tly = diff.getTly();
            int dX = tlx - diff.getBrx();
            int dY = tly - diff.getBry();
            boolean bNeedRotate = dX != dY;
//            System.out.println("rotate? " + bNeedRotate);
            if (bNeedRotate) { // rotate is needed
                resList.add(new RotateOperator(f, Constants.Directions.LEFT));
                resList.add(new RotateOperator(f, Constants.Directions.RIGHT));
            }

            if (tlx >= 0  && tly >= 0) { // mean that the original position is to the left and above
                if (tlx > tly) { // need more right
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                }
                else {             // need more down

                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                }
            }
            else if (tlx >= 0) { // mean that the original position is to the left and below
                if (tlx + tly > 0) {   // need more right
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                }
                else {                  // need more up
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                }
            }
            else if (tly >= 0) {  // mean that the original position is to the right and above
                if (tlx + tly > 0) {   // need more down
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                }
                else {                  // need more left
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                }
            }
            else {      // mean that the original position is to the right and below
                if (tlx > tly) { // need more up
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                }
                else {             // need more left
                    resList.add(new MoveOperator(f, Constants.Directions.LEFT));
                    resList.add(new MoveOperator(f, Constants.Directions.UP));
                    resList.add(new MoveOperator(f, Constants.Directions.DOWN));
                    resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
                }
            }
            // if the furniture is square there is no point in rotates
            if (f.isSquare() == false && bNeedRotate == false) {
                resList.add(new RotateOperator(f, Constants.Directions.LEFT));
                resList.add(new RotateOperator(f, Constants.Directions.RIGHT));
            }
//            System.out.println("Deltas (diff): " + diff + ". For pc: " + dpc);
        }
//        System.out.println("list: " + resList);
        return resList;
    }
}
