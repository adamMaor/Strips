package Strips;

import java.util.ArrayList;

/**
 * Created by Laptop on 01/17/17.
 */
public class PreConditionAnd implements StripsObject {
    public ArrayList<StripsPreCondition> getList() {
        return list;
    }

    public void setList(ArrayList<StripsPreCondition> list) {
        this.list = list;
    }

    private ArrayList<StripsPreCondition> list;


    public PreConditionAnd(ArrayList<StripsPreCondition> list) {
        this.list = list;
    }
}
