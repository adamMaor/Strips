package Strips;

import Constants.Constants;
import Logic.Diff;
import Logic.Furniture;
import Logic.LogicUtils;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static Constants.Constants.Directions.*;

/**
 * Created by Laptop on 01/17/17.
 */
public class StripsHeuristics {
    private LogicUtils utils;

    public StripsHeuristics(LogicUtils utils) {
        this.utils = utils;
    }

    public ArrayList<StripsPreCondition> getSortedPreConditions(PreConditionAnd pcAnd){
        if (pcAnd.isbIsListOrderSet()) {
            return pcAnd.getPcList();
        }
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
        Collections.shuffle(diffList);
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
                resList.add(2, new RotateOperator(f, LEFT));
                resList.add(5, new RotateOperator(f, RIGHT));
//                boolean bNeedRotate = dX != dY;
//                if (bNeedRotate) {
//                    resList.add(2, new RotateOperator(f, LEFT));
//                    resList.add(5, new RotateOperator(f, RIGHT));
//                }

            }
        }
        // prevent situation of moves like up - down - up - down .....
        byte preventLoopingdirection = NONE;
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

    public DiffPreCond activateEncounterHeuristic(NoFurniturePreCond nfpc) {
        int numberOfEncounters = nfpc.getFurniture().addEncounter(nfpc.getEncounteredFurniture());
        if (numberOfEncounters >= Constants.Numbers.ENCSIZE) {
            nfpc.getFurniture().clearEncounter(nfpc.getEncounteredFurniture());
            Furniture encFurniture = nfpc.getEncounteredFurniture();
            Diff diff = null;
            if (encFurniture.isAtFinalLocation()) {
                encFurniture.setDiversionMode(true);
            }
            byte direction = getEncounterDirection(nfpc);
            switch (direction) {
                case Constants.Directions.UP:
                    diff = getDiversionDiff(nfpc.getFurniture(), encFurniture, Constants.Directions.RIGHT, Constants.Directions.UP);
                    break;
                case Constants.Directions.DOWN:
                    diff = getDiversionDiff(nfpc.getFurniture(), encFurniture, Constants.Directions.RIGHT,  Constants.Directions.DOWN);
                    break;
                case Constants.Directions.LEFT:
                    diff = getDiversionDiff(nfpc.getFurniture(), encFurniture, Constants.Directions.UP,  Constants.Directions.LEFT);
                    break;
                case Constants.Directions.RIGHT:
                    diff = getDiversionDiff(nfpc.getFurniture(), encFurniture, Constants.Directions.UP,  Constants.Directions.RIGHT);
                    break;
                case NONE:
                    break;
            }

            if (diff != null ) {
                if (utils.isLocationLegal(encFurniture, diff)) {
//                    System.out.println("diff is legal: " + encFurniture.getID() + diff);
                    // if found diversion - reset original furniture moves list
                    return new DiffPreCond(diff, encFurniture, this);
                }
                else{
//                System.out.println("popinh diff from heuristics");
                    encFurniture.popDiff();
                }
            }
        }
        return null;
    }

    /**
     * get the direction from which f1 is 'attacking' f2
     * @param nfpc
     * @return
     */
    private byte getEncounterDirection(NoFurniturePreCond nfpc) {

        // get all Pos of both furniture
        Logic.Pos f1tl = nfpc.getFurniture().getVirtualLocation().tl;
        Logic.Pos f1br = nfpc.getFurniture().getVirtualLocation().br;

        Logic.Pos f2tl = nfpc.getEncounteredFurniture().getLocation().tl;
        Logic.Pos f2br = nfpc.getEncounteredFurniture().getLocation().br;


        /*
        f1.tlx > f2.tlx && f1.tly > f2.tly && f1.brx > f2.brx && f1.bry > f2.bry (f1 is left)

        f1.tlx > f2.tlx && f1.tly < f2.tly && f1.brx < f2.brx && f1.bry < f2.bry (f1 down)

        f1.tlx > f2.tlx && f1.tly > f2.tly && f1.brx < f2.brx && f1.bry > f2.bry (f1 up)

        f1.tlx < f2.tlx && f1.tly > f2.tly && f1.brx < f2.brx && f1.bry < f2.bry (f1 right)
         */


        if (f1tl.x > f2tl.x) {
            if (f1tl.x > f2br.x) {
                return LEFT;
            }
            else {
                if (f1tl.y > f2tl.y) {
                    return UP;
                }
                else {
                    return DOWN;
                }
            }
        }
        else {
            if (f2tl.x >= f1br.x) {
                return RIGHT;
            }
            else {
                if (f1tl.y > f2tl.y) {
                    return UP;
                }
                else {
                    return DOWN;
                }
            }
        }

    }

    private Diff getDiversionDiff(Furniture furniture, Furniture encFurniture, byte d1, byte d2) {
        int numOfSteps = 1;
        Diff diff = null;
        Diff currDiff = encFurniture.getCurrentDiff();
        int tlx = currDiff.getTlx();
        int tly = currDiff.getTly();
        int brx = currDiff.getBrx();
        int bry = currDiff.getBry();
        Random rand = new Random();
        int choise = rand.nextInt(2);
        int multiplyer = choise == 1 ? 1 : -1 ;
        switch (d1) {
            case Constants.Directions.UP:
                numOfSteps = (furniture.getHeight() + encFurniture.getHeight()/2)  * multiplyer;
                diff = new Diff(tlx, tly - numOfSteps, brx, bry - numOfSteps);
                encFurniture.pushDiff(diff);
                if (utils.isLocationLegal(encFurniture, diff) == false) {
                    encFurniture.popDiff();
                    diff = new Diff(tlx, tly + numOfSteps, brx, bry + numOfSteps);
                    encFurniture.pushDiff(diff);
                    if (utils.isLocationLegal(encFurniture, diff) == false) {
                        encFurniture.popDiff();
                        switch (d2) {
                            case Constants.Directions.RIGHT:
                                diff = new Diff(tlx + numOfSteps, tly, brx + numOfSteps, bry);
                                encFurniture.pushDiff(diff);
                                break;
                            case Constants.Directions.LEFT:
                                diff = new Diff(tlx - numOfSteps, tly, brx - numOfSteps, bry);
                                encFurniture.pushDiff(diff);
                                break;
                        }
                    }
                }
                break;
            case Constants.Directions.RIGHT:
                numOfSteps = (furniture.getWidth() + encFurniture.getWidth()/2) * multiplyer;
                diff = new Diff(tlx + numOfSteps, tly, brx + numOfSteps, bry);
                encFurniture.pushDiff(diff);
                if (utils.isLocationLegal(encFurniture, diff) == false) {
                    encFurniture.popDiff();
                    diff = new Diff(tlx - numOfSteps, tly, brx - numOfSteps, bry);
                    encFurniture.pushDiff(diff);
                    if (utils.isLocationLegal(encFurniture, diff) == false) {
                        encFurniture.popDiff();
                        switch (d2) {
                            case Constants.Directions.UP:
                                diff = new Diff(tlx, tly - numOfSteps, brx, bry - numOfSteps);
                                encFurniture.pushDiff(diff);
                                break;
                            case Constants.Directions.DOWN:
                                diff = new Diff(tlx, tly + numOfSteps, brx, bry + numOfSteps);
                                encFurniture.pushDiff(diff);
                                break;
                        }
                    }
                }
                break;
        }

        return diff;
    }
}
