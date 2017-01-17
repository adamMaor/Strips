package Strips;

import Logic.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Laptop on 01/11/17.
 */
public class StripsLogic {
    private LogicUtils utils;
    private StripsHeuristics heuristics;
    private Stack<StripsObject> stack;

    public StripsLogic(LogicUtils utils) {
        this.utils = utils;
        heuristics = new StripsHeuristics();
    }

    private void operate(StripsOperator operator) {
        if (operator instanceof MoveOperator) {
            utils.moveFurniture(operator.getFurniture(), operator.getDirection());
        }
        else if (operator instanceof RotateOperator) {
            utils.canRotate(operator.getFurniture(), operator.getDirection());
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
        return bRes;
    }

    public ArrayList<String> getCurrentStack() {
        ArrayList<String> res = new ArrayList<String>();
        res.add("Hey");
        res.add("I'm");
        res.add("The");
        res.add("Stack");
        return res;
    }

    public ArrayList<String> getCurrentPlan() {
        // TODO - make sure stack is reverse and plan is in order
        ArrayList<String> res = new ArrayList<String>();
        res.add("Hello");
        res.add("I'm");
        res.add("The");
        res.add("Plan");
        return res;
    }

    public boolean makeMove() {
        // use Strips for next move
        Random rand = new Random();
        for (Furniture f : utils.getAllFurniture()) {
            byte direction = (byte)rand.nextInt(5);
            utils.moveFurniture(f, direction);
        }
        return true;
    }
}
