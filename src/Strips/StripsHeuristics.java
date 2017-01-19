package Strips;

import Constants.Constants;
import Logic.Diff;
import Logic.Furniture;
import java.util.ArrayList;
import java.util.Collections;

import static Constants.Constants.Directions.*;

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

    public ArrayList<StripsOperator> getMovesList(StripsPreCondition pc, byte lastMoveDirection) {
        ArrayList<StripsOperator> resList = new ArrayList<StripsOperator>();
        if (pc instanceof DiffPreCond) {
//            DiffPreCond dpc = (DiffPreCond)pc;
            Furniture f = pc.getFurniture();
            Diff diff = new Diff(f.getVirtualLocation(), f.getLocation());
            int tlx = diff.getTlx();
            int tly = diff.getTly();
            int dX = tlx - diff.getBrx();
            int dY = tly - diff.getBry();

//            System.out.println("rotate? " + bNeedRotate);

            if (tlx >= 0  && tly >= 0) { // mean that the original position is to the left and above
                if (tlx > tly) { // need more right
                    setOrder(resList, f, RIGHT, DOWN, UP, LEFT);
                }
                else {             // need more down
                    setOrder(resList, f, DOWN, RIGHT, LEFT, UP);
                }
            }
            else if (tlx >= 0) { // mean that the original position is to the left and below
                if (tlx + tly > 0) {   // need more right
                    setOrder(resList, f, RIGHT, UP, DOWN, LEFT);
                }
                else {                  // need more up
                    setOrder(resList, f, UP, RIGHT, LEFT, DOWN);
                }
            }
            else if (tly >= 0) {  // mean that the original position is to the right and above
                if (tlx + tly > 0) {   // need more down
                    setOrder(resList, f, DOWN, LEFT, RIGHT, UP);
                }
                else {                  // need more left
                    setOrder(resList, f, LEFT, DOWN, UP, RIGHT);
                }
            }
            else {      // mean that the original position is to the right and below
                if (tlx > tly) { // need more up
                    setOrder(resList, f, UP, LEFT, RIGHT, DOWN);

                }
                else {             // need more left
                    setOrder(resList, f, LEFT, UP, DOWN, RIGHT);
                }
            }
            // if the furniture is square there is no point in rotates
            if (f.isSquare() == false) { // is it even worth checking for rotation
                boolean bNeedRotate = dX != dY;
                if (bNeedRotate) {
                    resList.add(2, new RotateOperator(f, LEFT));
                    resList.add(5, new RotateOperator(f, RIGHT));
                }
            }
        }
        // prevent situation of moves like up - down - up - down .....
        byte preventLoopingdirection = Constants.Directions.NONE;
        switch (lastMoveDirection) {
            case UP:
                preventLoopingdirection = DOWN;
                break;
            case LEFT:
                preventLoopingdirection = RIGHT;
                break;
            case DOWN:
                preventLoopingdirection = UP;
                break;
            case RIGHT:
                preventLoopingdirection = LEFT;
                break;
        }
        int indexToRemove = -1;
        for (int i = 0; i < resList.size(); i++) {
            if (resList.get(i) instanceof MoveOperator) {
                MoveOperator mo = (MoveOperator)resList.get(i);
                if (mo.getDirection() == preventLoopingdirection) {
                    indexToRemove = i;
                }
            }
        }
        if (indexToRemove != -1) {
            resList.remove(indexToRemove);
        }
//        System.out.println("list: " + resList);
        return resList;
    }

    private void setOrder(ArrayList<StripsOperator> resList, Furniture f, byte d1, byte d2, byte d3, byte d4) {
        resList.add(new MoveOperator(f, d1));
        resList.add(new MoveOperator(f, d2));
        resList.add(new MoveOperator(f, d3));
        resList.add(new MoveOperator(f, d4));
    }
}
