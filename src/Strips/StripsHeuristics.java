package Strips;

import Constants.Constants;
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
        for (StripsPreCondition pc : pcAnd.getList()) {
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
        Furniture f = pc.getFurniture();
        int dX = f.getFinalLocation().tl.x - f.getLocation().tl.x;
        int dY = f.getFinalLocation().tl.y - f.getLocation().tl.y;



        resList.add(new MoveOperator(f, Constants.Directions.UP));
        resList.add(new MoveOperator(f, Constants.Directions.LEFT));
        resList.add(new MoveOperator(f, Constants.Directions.DOWN));
        resList.add(new MoveOperator(f, Constants.Directions.RIGHT));
        resList.add(new RotateOperator(f, Constants.Directions.LEFT));
        resList.add(new RotateOperator(f, Constants.Directions.RIGHT));
        Collections.shuffle(resList);
        return resList;
    }

}
