package Strips;

import java.util.ArrayList;

/**
 * Created by Laptop on 01/17/17.
 */
public class PreConditionAnd implements StripsObject {
    private ArrayList<StripsPreCondition> pcList;
    private boolean bIsListOrderSet;

    public boolean isbIsListOrderSet() {
        return bIsListOrderSet;
    }

    public ArrayList<StripsPreCondition> getPcList() {
        return pcList;
    }

    public PreConditionAnd(ArrayList<StripsPreCondition> pcList, boolean bListOrderPreSet) {
        this.pcList = pcList;
        this.bIsListOrderSet = bListOrderPreSet;
    }

    @Override
    public String toString() {
        String strRes = "";
        int i = 0;
        for (; i < pcList.size() - 1; i++) {
            strRes += pcList.get(i).toString() + " & ";
        }
        strRes += pcList.get(i).toString();
        return strRes;
    }
}
