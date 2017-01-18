package Strips;

import Constants.Constants;
import Logic.*;

import java.util.*;

/**
 * Created by Laptop on 01/11/17.
 */
public class StripsLogic {
    private LogicUtils utils;
    private StripsHeuristics heuristics;
    private Stack<StripsObject> stack;
    private ArrayList<String> guiStack;
    private ArrayList<StripsObject> plan;
    private ArrayList<String> guiPlan;
    private boolean bIsFinished;
    private boolean bIsPoping;
    private boolean bSuccess;

    public boolean isbIsFinished() {
        return bIsFinished;
    }

    public StripsLogic(LogicUtils utils) {
        this.utils = utils;
        heuristics = new StripsHeuristics();
        stack = new Stack<>();
        guiStack = new ArrayList<>();
        plan = new ArrayList<>();
        guiPlan = new ArrayList<>();
        bIsFinished = false;
        // this boolean is kept in order to know if I'm currently poping the stack or pushing
        bIsPoping = false;
        // this will indicate if success was found
        bSuccess = false;
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
            pushStack(new PreConditionAnd(list));
        }
        else if (fList.size() == 1){
            for (Furniture f : fList) {
                Diff diff = new Diff(0,0,0,0);
                f.pushDiff(diff);
                pushStack(new DiffPreCond(diff, f, heuristics));
            }
        }
    }

    private void operate(StripsOperator operator) {
        if (operator instanceof MoveOperator) {
            utils.moveFurniture(operator.getFurniture(), operator.getDirection());
        }
        else if (operator instanceof RotateOperator) {
            utils.rotateFurniture(operator.getFurniture(), operator.getDirection());
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
            bRes = utils.noWalls(f, c.getDirection());
        }
        else if (condition instanceof NoWallsPreCond) {
            NoWallsPreCond c = (NoWallsPreCond)condition;
            bRes = utils.noOtherFurniture(f, c.getDirection());
        }
        else if (condition instanceof CanRotatePreCond) {
            CanRotatePreCond c = (CanRotatePreCond)condition;
            bRes = utils.canRotate(c.getFurniture(),c.getDirection());
        }
        else if (condition instanceof DiffLegalPreCond) {
            DiffLegalPreCond c = (DiffLegalPreCond) condition;
            bRes = utils.isLocationLegal(c.getFurniture(), c.getDiff());
        }
        return bRes;
    }

    public ArrayList<String> getCurrentStack() {
//        ArrayList<String> res = new ArrayList<String>();
//        res.add("Hey");
//        res.add("I'm");
//        res.add("The");
//        res.add("Stack");
//        return res;
//        System.out.println(guiStack);
        return guiStack;
    }

    public ArrayList<String> getCurrentPlan() {
        // TODO - make sure stack is reverse and plan is in order

        return guiPlan;
    }

    public boolean makeMove() {
        if (stack.isEmpty()){
            //Done!!! - set boolean to done
            bIsFinished = true;
            return false;
        }

        StripsObject obj = peekStack();
        if (obj instanceof PreConditionAnd) {
            PreConditionAnd pcAnd = (PreConditionAnd) peekStack();
            ArrayList<StripsPreCondition> pcList = pcAnd.getPcList();
            if (isSatisfied(pcList)) {
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
                popStack();
            }
            else {
                if (pc instanceof DiffLegalPreCond) {
                    popTillLastOp();
                }
                if (pc instanceof DiffPreCond){
                    DiffPreCond dPc = (DiffPreCond) pc;
                    dPc.setMoves(heuristics);
                    StripsOperator o = dPc.getNextMove();
                    if (o != null) { // a new move is available
                        // set the current diff to furniture for utils calculations - creates a virtual location
//                        dPc.getFurniture().pushDiff(dPc.getDiff());
                        pushStack(o);
                    }
                    else {          // no more move available - pop
//                        dPc.getFurniture().popDiff(); // resume to previous diff for virtual location of furniture
                        popStack();
                    }

                }
                else if (pc instanceof CanRotatePreCond) {
                    // Currently will not handle this - later can try to move other furniture
                    popTillLastOp();
                }
                else if (pc instanceof NoFurniturePreCond) {
                    // Currently will not handle this - later can try to move other furniture
                    popTillLastOp();
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
            }
            else {
                ArrayList<StripsPreCondition> pcList = new ArrayList<StripsPreCondition>();
                Furniture f = o.getFurniture();
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
                    pcList.add(new DiffPreCond(diff, f, heuristics));
                    pcList.add(new CanRotatePreCond(f, ro.getDirection()));
                    pcList.add(new DiffLegalPreCond(ro.getFurniture(), diff));
                }
                else if (o instanceof MoveOperator) {
                    MoveOperator mo = (MoveOperator) o;
                    int tlx = mo.getFurniture().getVirtualLocation().tl.x;
                    int tly = mo.getFurniture().getVirtualLocation().tl.y;
                    int brx = mo.getFurniture().getVirtualLocation().br.x;
                    int bry = mo.getFurniture().getVirtualLocation().br.y;
                    byte direction = mo.getDirection();
//                    System.out.println("mo: " + mo.toString());
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
                    pcList.add(new DiffPreCond(diff, f, heuristics));
                    pcList.add(new NoFurniturePreCond(f, direction));
                    pcList.add(new NoWallsPreCond(f, direction));
                    pcList.add(new DiffLegalPreCond(mo.getFurniture(), diff));
                }
                pushStack(new PreConditionAnd(pcList));
            }
        }
        return true;
    }

    private void popTillLastOp() {
        StripsObject obj = stack.peek();
        while ((obj instanceof StripsOperator ) == false) {
            if (obj instanceof DiffPreCond) {
                DiffPreCond dPc = (DiffPreCond)obj;
                dPc.getFurniture().popDiff();
            }
            popStack();
            obj = stack.peek();
        }
        // pop the last operator - it will never happen.
        popStack();
    }

    private void pushStack(StripsObject obj) {
        bIsPoping = false;
        stack.push(obj);
        guiStack.add(0,obj.toString());
    }

    private StripsObject popStack() {
        bIsPoping = true;
        guiStack.remove(0);
        return stack.pop();
    }

    private StripsObject peekStack() {
        return stack.peek();
    }

    private void pushPlan(StripsOperator o) {
        plan.add(0,o);
        guiPlan.add(0,o.toString());
        operate(o);
    }

    public void resetAll() {
        stack.clear();
        guiStack.clear();
        plan.clear();
        guiPlan.clear();
        bIsFinished = false;
        bIsPoping = false;
        bSuccess = false;
    }
}
