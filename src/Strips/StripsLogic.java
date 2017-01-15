package Strips;

import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.LogicUtils;
import Logic.Pos;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Laptop on 01/11/17.
 */
public class StripsLogic {
    private LogicUtils utils;

    public StripsLogic(LogicUtils utils) {
        this.utils = utils;
    }

    private void operate(StripsOperator operator) {
        if (operator instanceof MoveOperator) {
            utils.moveFurniture(operator.getFurniture(), operator.getDirection());
        }
        else if (operator instanceof RotateOperator) {
            utils.canRotate(operator.getFurniture(), operator.getDirection());
        }
    }
    private boolean isSatisfied(StripsPreCondition condition) {
        Furniture f = condition.getFurniture();
        boolean bRes = false;
        if (condition instanceof DiffPreCond) {
            DiffPreCond c = (DiffPreCond)condition;
            FurnitureLocation cfl = f.getLocation();
            Pos ctl = cfl.tl;
            Pos cbr = cfl.br;

            FurnitureLocation ffl = f.getFinalLocation();
            Pos ftl = ffl.tl;
            Pos fbr = ffl.br;
            bRes = ( (ctl.x + c.tlx == ftl.x)
                    && (ctl.y + c.tly == ftl.y)
                    && (cbr.x + c.brx == fbr.x)
                    && (cbr.y + c.bry == fbr.y)
            );
        }
        else if (condition instanceof CanMovePreCond) {
            CanMovePreCond c = (CanMovePreCond)condition;
            bRes = utils.canMove(c.getFurniture(), c.getDirection());
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
