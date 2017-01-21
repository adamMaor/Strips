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
 * This Class Holds all special calculations and assumptions.
 *
 * Created by Laptop on 01/17/17.
 */
public class StripsHeuristics {
    private LogicUtils utils;

    public StripsHeuristics(LogicUtils utils) {
        this.utils = utils;
    }

    /**
     * Orders Precondition held in an 'And' precondition in a specific order
     * @param pcAnd
     * @return a list of pre conditions in the they should be inserted to the stack.
     */
    public ArrayList<StripsPreCondition> getSortedPreConditions(PreConditionAnd pcAnd) {
        if (pcAnd.isbIsListOrderSet()) {
            return pcAnd.getPcList();
        }
        ArrayList<StripsPreCondition> diffList = new ArrayList<StripsPreCondition>();
        ArrayList<StripsPreCondition> wallsList = new ArrayList<StripsPreCondition>();
        ArrayList<StripsPreCondition> otherList = new ArrayList<StripsPreCondition>();
        for (StripsPreCondition pc : pcAnd.getPcList()) {
            if (pc instanceof DiffPreCond) {
                diffList.add(pc);
            } else if (pc instanceof NoWallsPreCond) {
                wallsList.add(pc);
            } else {
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

    /**
     * Returns an ordered list of moves - the best is first an the worst is last
     * @param pc the precondition to which list is needed
     * @param lastMoveDirection - in order to prevent chains like - Up Down Up Down etc...
     * @return the ordered list
     */
    public ArrayList<StripsOperator> getMovesList(StripsPreCondition pc, byte lastMoveDirection) {
        ArrayList<StripsOperator> resList = new ArrayList<StripsOperator>();
        if (pc instanceof DiffPreCond) {
            Furniture f = pc.getFurniture();
            Diff diff = new Diff(f.getVirtualLocation(), f.getLocation());
            int tlx = diff.getTlx();
            int tly = diff.getTly();
            int dX = tlx - diff.getBrx();
            int dY = tly - diff.getBry();

            if (tlx >= 0 && tly >= 0) { // mean that the original position is to the left and above
                if (tlx > tly) { // need more right
                    setOrder(resList, f, RIGHT, DOWN, UP, LEFT);
                } else {             // need more down
                    setOrder(resList, f, DOWN, RIGHT, LEFT, UP);
                }
            } else if (tlx >= 0) { // mean that the original position is to the left and below
                if (tlx + tly > 0) {   // need more right
                    setOrder(resList, f, RIGHT, UP, DOWN, LEFT);
                } else {                  // need more up
                    setOrder(resList, f, UP, RIGHT, LEFT, DOWN);
                }
            } else if (tly >= 0) {  // mean that the original position is to the right and above
                if (tlx + tly > 0) {   // need more down
                    setOrder(resList, f, DOWN, LEFT, RIGHT, UP);
                } else {                  // need more left
                    setOrder(resList, f, LEFT, DOWN, UP, RIGHT);
                }
            } else {      // mean that the original position is to the right and below
                if (tlx > tly) { // need more up
                    setOrder(resList, f, UP, LEFT, RIGHT, DOWN);

                } else {             // need more left
                    setOrder(resList, f, LEFT, UP, DOWN, RIGHT);
                }
            }
            // if the furniture is square there is no point in rotates
            if (f.isSquare() == false) {
                resList.add(2, new RotateOperator(f, LEFT));
                resList.add(5, new RotateOperator(f, RIGHT));
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
                MoveOperator mo = (MoveOperator) resList.get(i);
                if (mo.getDirection() == preventLoopingdirection) {
                    indexToRemove = i;
                }
            }
        }
        if (indexToRemove != -1) {
            resList.remove(indexToRemove);
        }
        return resList;
    }

    /**
     * utility - save code lines
     * @param resList
     * @param f
     * @param d1
     * @param d2
     * @param d3
     * @param d4
     */
    private void setOrder(ArrayList<StripsOperator> resList, Furniture f, byte d1, byte d2, byte d3, byte d4) {
        resList.add(new MoveOperator(f, d1));
        resList.add(new MoveOperator(f, d2));
        resList.add(new MoveOperator(f, d3));
        resList.add(new MoveOperator(f, d4));
    }

    /**
     * Encounter heuristics - mentioned in the Documentation file
     * moves a blocking furniture from the path
     * @param nfpc
     * @return
     */
    public DiffPreCond activateEncounterHeuristic(NoFurniturePreCond nfpc) {
        Furniture furniture = nfpc.getFurniture();
        Furniture encFurniture = nfpc.getEncounteredFurniture();
        int numberOfEncounters = furniture.addEncounter(nfpc.getEncounteredFurniture());
        if (numberOfEncounters >= Constants.Numbers.ENCSIZE) {
            furniture.clearEncounter(nfpc.getEncounteredFurniture());
            if (encFurniture.isAtFinalLocation()) {
                encFurniture.setDiversionMode(true);
            }
            byte direction = getEncounterDirection(nfpc);

            int numOfSteps = 0;
            switch (direction) {
                case Constants.Directions.UP:
                case Constants.Directions.DOWN:
                    numOfSteps = Math.min((furniture.getWidth() + encFurniture.getWidth() / 2), 5);
                    break;
                case Constants.Directions.RIGHT:
                case Constants.Directions.LEFT:
                    numOfSteps = Math.min((furniture.getHeight() + encFurniture.getHeight() / 2), 5);
                    break;
            }
            Diff diff = getRandomDiversionDiff(encFurniture, numOfSteps);
            if (diff != null) {
                if (utils.isLocationLegal(encFurniture, diff)) {
                    // if found diversion - reset original furniture moves list
                    DiffPreCond dpc =new DiffPreCond(diff, encFurniture, this);
                    return dpc;
                } else {
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

        if (f1tl.x > f2tl.x) {
            if (f1tl.x > f2br.x) {
                return LEFT;
            } else {
                if (f1tl.y > f2tl.y) {
                    return UP;
                } else {
                    return DOWN;
                }
            }
        } else {
            if (f2tl.x >= f1br.x) {
                return RIGHT;
            } else {
                if (f1tl.y > f2tl.y) {
                    return UP;
                } else {
                    return DOWN;
                }
            }
        }
    }

    /**
     * Get a random direction of a diff to move the blocking furniture
     * @param encFurniture
     * @param numOfSteps
     * @return
     */
    private Diff getRandomDiversionDiff(Furniture encFurniture, int numOfSteps) {
        Diff diff = null;
        Diff currDiff = encFurniture.getCurrentDiff();
        int tlx = currDiff.getTlx();
        int tly = currDiff.getTly();
        int brx = currDiff.getBrx();
        int bry = currDiff.getBry();
        Random rand = new Random();
        int choice = rand.nextInt(4);
        int retries = 0;
        while (retries < 4) {
            switch (choice) {
                case 0:
                    diff = new Diff(tlx, tly - numOfSteps, brx, bry - numOfSteps);
                    break;
                case 1:
                    diff = new Diff(tlx, tly + numOfSteps, brx, bry + numOfSteps);
                    break;
                case 2:
                    diff = new Diff(tlx + numOfSteps, tly, brx + numOfSteps, bry);
                    break;
                case 3:
                    diff = new Diff(tlx - numOfSteps, tly, brx - numOfSteps, bry);
                    break;
            }
            encFurniture.pushDiff(diff);
            if (utils.isLocationLegal(encFurniture, diff)) {
                return diff;
            }
            encFurniture.popDiff();
            choice = rand.nextInt(4);
            retries++;
        }
        return null;
    }
}