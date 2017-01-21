package Strips;

import Constants.Constants;
import Logic.*;
import com.sun.javaws.Globals;

import java.util.*;

/**
 * This class holds all the logic side of the algorithm
 * holds and manges the stack as well and creates moves and progresses the algorithm
 * Created by Laptop on 01/11/17.
 */
public class StripsLogic {
    private LogicUtils utils;
    private StripsHeuristics heuristics;
    private Stack<StripsObject> stack;
    private ArrayList<String> guiStack;
    private ArrayList<StripsObject> plan;
    private ArrayList<String> guiPlan;
    private boolean bIsPoping;
    private boolean bSuccess;
    private byte lastMoveDirection;
    private boolean repaintBoardNeeded;
    private boolean bIsReplayMode;
    private int replayMoveCount;
    private long totalWorkTime;
    private int currentTry;
    private int currentMoveCount;

    public StripsLogic(LogicUtils utils) {
        this.utils = utils;
        heuristics = new StripsHeuristics(utils);
        stack = new Stack<>();
        guiStack = new ArrayList<>();
        plan = new ArrayList<>();
        guiPlan = new ArrayList<>();
        // this boolean is kept in order to know if I'm currently poping the stack or pushing
        bIsPoping = false;
        // this will indicate if success was found
        bSuccess = false;
        bIsReplayMode = false;
        replayMoveCount = 0;
        totalWorkTime = 0;
        currentTry = 1;
        currentMoveCount = 1;

    }

    public void init() {
        // Create Initial stack and condition:
        // For each furniture create diff(0,0,0,0) condition
        Collection<Furniture> fList = utils.getAllFurniture();
        if (fList.size() > 1) {
            ArrayList<StripsPreCondition> list = new ArrayList<>();
            for (Furniture f : fList) {
                Diff diff = new Diff(0,0,0,0);
                f.pushDiff(diff);
                list.add(new DiffPreCond(diff, f, heuristics));
            }
            pushStack(new PreConditionAnd(list, false));
        }
        else if (fList.size() == 1){
            for (Furniture f : fList) {
                Diff diff = new Diff(0,0,0,0);
                f.pushDiff(diff);
                pushStack(new DiffPreCond(diff, f, heuristics));
            }
        }
    }

    /**
     * This is the main method of the Strip Algorithm
     * @return true if a move was made, false otherwise
     */
    public boolean makeMove() {
        // Playback Mode - solution was found - only displaying the plan now
        if (bIsReplayMode) {
            repaintBoardNeeded = true;
            if (replayMoveCount >= 0) {
                StripsObject obj = plan.get(replayMoveCount);
                if (obj instanceof StripsOperator) {
                    replayMoveCount--;
                    StripsOperator so = (StripsOperator)obj;
                    operate(so);
                    return true;
                }
            }
            return false;
        }
        // normal mode - calculating the plan using the stack
        else {
            if (currentTry > Constants.Numbers.MAX_TRIES) {
                bSuccess = false;
                return false;
            }
            else if (currentMoveCount > Constants.Numbers.MAX_MOVES_PER_TRY) {
                currentTry++;
                currentMoveCount = 0;
                bSuccess = false;
                utils.resetAll();
                System.out.println("Resetting and Trying again !");
                resetAll(true);
                return true;
            }
            if (stack.isEmpty()){
                //Done!!! - check if successful
                bSuccess = checkForSuccees();
                return false;
            }
            currentMoveCount++;
            long startTime = System.nanoTime();
            repaintBoardNeeded = false;
            StripsObject obj = peekStack();
            if (obj instanceof PreConditionAnd) {
                PreConditionAnd pcAnd = (PreConditionAnd) peekStack();
                ArrayList<StripsPreCondition> pcList = heuristics.getSortedPreConditions(pcAnd);
                if (bIsPoping && isSatisfied(pcList)) {
                    for (StripsPreCondition spc : pcList) {
                        if (spc instanceof DiffPreCond) {
                            spc.getFurniture().popDiff();
                        }
                    }
                    popStack();
                }
                else {
                    for (int i = 0; i < pcList.size(); i++) {
                        StripsPreCondition pc = pcList.get(i);
                        pushStack(pc);
                    }
                }
            }
            else if (obj instanceof StripsPreCondition) {
                StripsPreCondition pc = (StripsPreCondition) obj;
                if (isSatisfied(pc)) {
                    if (pc instanceof DiffLegalPreCond) {
                        repaintBoardNeeded = true;
                    }
                    else if (pc instanceof DiffPreCond) {
                        DiffPreCond dpc = (DiffPreCond) pc;
                        if (dpc.shouldPopDiff()) {
                            dpc.getFurniture().popDiff();
                        }
                    }
                    popStack();
                }
                else {
                    if (pc instanceof DiffLegalPreCond) {
                        popTillLastOp();
                    }
                    if (pc instanceof DiffPreCond){
                        repaintBoardNeeded = true;
                        DiffPreCond dPc = (DiffPreCond) pc;

                        StripsOperator o = dPc.getNextMove(heuristics, lastMoveDirection);
                        if (o != null) { // a new move is available
                            // set the current diff to furniture for utils calculations - creates a virtual location
//                        dPc.getFurniture().pushDiff(dPc.getDiff());
                            if (o instanceof MoveOperator) {
                                lastMoveDirection = o.getDirection();
                            }
                            else {
                                lastMoveDirection = Constants.Directions.NONE;
                            }
                            pushStack(o);
                        }
                        else {          // no more move available - pop
                            if (dPc.shouldPopDiff()) {
                                dPc.getFurniture().popDiff();
                            }
                            popTillLastOp();
                        }

                    }
                    else if (pc instanceof CanRotatePreCond) {
                        popTillLastOp();
                    }
                    else if (pc instanceof NoFurniturePreCond) {
                        DiffPreCond dpc = heuristics.activateEncounterHeuristic((NoFurniturePreCond) pc);
                        if (dpc != null) {
                            dpc.setbIsDeversionFirst(true);
                            pushStack(dpc);
                        }
                        else {
                            popTillLastOp();
                        }
                    }
                    else if (pc instanceof NoWallsPreCond) { //can't move because of walls - will never change
                        popTillLastOp();
                    }
                }
            }
            else if (obj instanceof StripsOperator) {
                StripsOperator o = (StripsOperator)obj;
                if (bIsPoping) { // this means If got here after check all preconditions - and we know it can be executed
                    pushPlan(o);
                    popStack();
                    repaintBoardNeeded = true;
                }
                else {
                    ArrayList<StripsPreCondition> pcList = new ArrayList<StripsPreCondition>();
                    Furniture f = o.getFurniture();
                    DiffPreCond dpc = null;
                    DiffLegalPreCond dlpc = null;
                    if (o instanceof RotateOperator) {
                        RotateOperator ro = (RotateOperator) o;
                        FurnitureLocation newLocation = null;
                        if (ro.getDirection() == Constants.Directions.LEFT){
                            newLocation = utils.getRotatedLocation(f.getVirtualLocation(), Constants.Directions.RIGHT);
                        }
                        else if (ro.getDirection() == Constants.Directions.RIGHT){
                            newLocation = utils.getRotatedLocation(f.getVirtualLocation(), Constants.Directions.LEFT);
                        }
                        // new diff is relative to final location
                        Diff diff = new Diff(f.getFinalLocation(), newLocation);
                        dpc = new DiffPreCond(diff, f, heuristics);
                        dlpc = new DiffLegalPreCond(ro.getFurniture(), diff);
                        pcList.add(dpc);
                        pcList.add(new CanRotatePreCond(f, ro.getDirection()));
                        pcList.add(dlpc);
                    }
                    else if (o instanceof MoveOperator) {
                        MoveOperator mo = (MoveOperator) o;
                        int tlx = mo.getFurniture().getVirtualLocation().tl.x;
                        int tly = mo.getFurniture().getVirtualLocation().tl.y;
                        int brx = mo.getFurniture().getVirtualLocation().br.x;
                        int bry = mo.getFurniture().getVirtualLocation().br.y;
                        byte direction = mo.getDirection();
                        switch (direction) {
                            case Constants.Directions.UP:
                                tly++;
                                bry++;
                                break;
                            case Constants.Directions.LEFT:
                                tlx++;
                                brx++;
                                break;
                            case Constants.Directions.DOWN:
                                tly--;
                                bry--;
                                break;
                            case Constants.Directions.RIGHT:
                                tlx--;
                                brx--;
                                break;
                            default:
                                break;
                        }
                        FurnitureLocation newLocation = new FurnitureLocation(new Pos(tlx, tly), new Pos(brx, bry));
                        Diff diff = new Diff(f.getFinalLocation(), newLocation);
                        dpc = new DiffPreCond(diff, f, heuristics);
                        dlpc = new DiffLegalPreCond(mo.getFurniture(), diff);
                        pcList.add(dpc);
                        pcList.add(new NoFurniturePreCond(f, direction));
                        pcList.add(new NoWallsPreCond(f, direction));
                        pcList.add(dlpc);
                    }
                    if (dpc != null && notInLoop(dpc)) {
                        dlpc.getFurniture().pushDiff(dlpc.getDiff());
                        pushStack(new PreConditionAnd(pcList, true));
                    }
                }
            }
            long addedTime = System.nanoTime() - startTime;
            totalWorkTime += addedTime;
            return true;
        }
    }

    private void operate(StripsOperator operator) {
        if (operator instanceof MoveOperator) {
            utils.moveFurniture(operator.getFurniture().getID(), operator.getDirection());
        }
        else if (operator instanceof RotateOperator) {
            utils.rotateFurniture(operator.getFurniture().getID(), operator.getDirection());
        }
    }

    private boolean isSatisfied(ArrayList<StripsPreCondition> conditions) {
        for (StripsPreCondition c : conditions) {
            if (isSatisfied(c) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean isSatisfied(StripsPreCondition condition) {
        Furniture f = condition.getFurniture();
        boolean bRes = false;
        if (condition instanceof DiffPreCond) {
            DiffPreCond c = (DiffPreCond)condition;
            Diff pcDiff = c.getDiff();
            Diff fDiff = f.getCurrentDiff();
            Diff deltaDiff = new Diff(pcDiff, fDiff);
            bRes =  (   deltaDiff.getBry() == 0
                    && deltaDiff.getBrx() == 0
                    && deltaDiff.getTlx() == 0
                    && deltaDiff.getTly() == 0);
        }
        else if (condition instanceof NoFurniturePreCond) {
            NoFurniturePreCond c = (NoFurniturePreCond)condition;
            Furniture resFurniture = utils.noOtherFurniture(f, c.getDirection());
            if (resFurniture != null) {
                // Assign the furniture of conflict to the preCondition - for encounterHeuristics
                c.setFurnitureEncounterd(resFurniture);
                bRes = false;
            }
            else {
                bRes = true;
            }
        }
        else if (condition instanceof NoWallsPreCond) {
            NoWallsPreCond c = (NoWallsPreCond)condition;
            bRes = utils.noWalls(f, c.getDirection());
        }
        else if (condition instanceof CanRotatePreCond) {
            CanRotatePreCond c = (CanRotatePreCond)condition;
            bRes = utils.canVirtualRotate(c.getFurniture(), c.getDirection());
        }
        else if (condition instanceof DiffLegalPreCond) {
            DiffLegalPreCond c = (DiffLegalPreCond) condition;
            bRes = utils.isLocationLegal(c.getFurniture(), c.getDiff());
        }
        return bRes;
    }

    private boolean checkForSuccees() {
        boolean bRes = true;
        for (Furniture f: utils.getAllFurniture()) {
            if (f.getLocation().equals(f.getFinalLocation()) == false) {
                bRes = false;
                break;
            }
        }
        return bRes;
    }

    private boolean notInLoop(DiffPreCond dpc) {
        if (dpc.getDiff().equals(new Diff(0,0,0,0))) {      // This is single shot - only once will be true for each time
            if( dpc.getFurniture().getIsDiversionMode()) {
                return true;
            }
        }
        int numToPop = stack.search(dpc);
        if (numToPop != -1) { // this DiffPreCondition is already in the stack - we are in a loop
            // we'll need to pop till this diff is found and poped
            // and then till the previous operator is poped
            // in order to choose another operator
            while (numToPop-- > 0) {
                StripsObject obj = popStack();
                if (obj instanceof DiffPreCond) {
                    DiffPreCond dPc = (DiffPreCond)obj;
                    dPc.getFurniture().popDiff();
                }
            }
            if (dpc.getDiff().getTly() != 0 ||  dpc.getDiff().getTlx() != 0
                    || dpc.getDiff().getBry() != 0 || dpc.getDiff().getBrx() != 0) {
                popTillLastOp(); // if diff is 0,0,0,0 -> no new operator exists
            }
            // Corner Case - can't kill a furniture virtual location this way - only organically
            else if (dpc.getFurniture().isPopStackEmpty()) {
                dpc.getFurniture().pushDiff(new Diff(0,0,0,0));
            }
            return false;
        }

        return true;
    }

    private void popTillLastOp() {
        StripsObject obj = stack.peek();
        while (stack.size() > 0 && (obj instanceof StripsOperator) == false) {
            obj = popStack();
            if (obj instanceof DiffPreCond) {
                DiffPreCond dPc = (DiffPreCond)obj;
//                if (dPc.getDiff().getTly() != 0 ||  dPc.getDiff().getTlx() != 0
//                        || dPc.getDiff().getBry() != 0 || dPc.getDiff().getBrx() != 0) {
//                }
                dPc.getFurniture().popDiff();
                // Corner Case - can't kill a furniture virtual location this way - only organically
                if (dPc.getFurniture().isPopStackEmpty()) {
                    dPc.getFurniture().pushDiff(new Diff(0,0,0,0));
                }
            }
        }
        if (stack.size() == 0) {
            init();
        }
    }

    private void pushStack(StripsObject obj) {
        bIsPoping = false;
        stack.push(obj);
        guiStack.add(0,obj.toString());
    }

    private StripsObject popStack() {
        bIsPoping = true;
        guiStack.remove(0);
        StripsObject obj = stack.pop();
        // Corner case
        if (stack.size() == 0) {
            if (obj instanceof DiffPreCond) {
                ((DiffPreCond) obj).getFurniture().popDiff();
            }
        }
        return obj;
    }

    private StripsObject peekStack() {
        return stack.peek();
    }

    private void pushPlan(StripsOperator o) {
        plan.add(0,o);
        guiPlan.add(o.toString());
        operate(o);
//        Furniture f = o.getFurniture();
//        if (f.getLocation().equals(f.getVirtualLocation()) == false) {
////            System.out.println("poping diff from pushPlan");
////            f.popDiff();
//        }
    }


    /*************************
     *** UTILITY FUNCTIONS ***
     *************************/

    public void resetAll(boolean bIsFullReset) {
        stack.clear();
        guiStack.clear();
        bIsReplayMode = !bIsFullReset;
        bIsPoping = false;
        replayMoveCount = plan.size() - 1;
        currentMoveCount = 0;
        currentTry = 0;
        if (bIsFullReset) {
            totalWorkTime = 0;
            plan.clear();
            guiPlan.clear();
            bSuccess = false;
        }
    }

    public ArrayList<String> getCurrentStack() {
        return guiStack;
    }

    public ArrayList<String> getCurrentPlan() {
        return guiPlan;
    }

    public boolean isRepaintBoardNeeded() {
        return repaintBoardNeeded;
    }

    public boolean isbSuccess() {
        return bSuccess;
    }

    public long getTotalWorkTime() {
        return totalWorkTime / 1000000 ;
    }
}
