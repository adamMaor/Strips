package Logic;

import java.awt.*;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Adam on 09/01/2017.
 */
public class Furniture {
    private String ID;
    private FurnitureLocation location;
    private FurnitureLocation finalLocation;
    private Color color;
    // keep the diff stack in order to calculate conditions in utils
    private Stack<Diff> diffStack;
    private HashMap<String, Integer> encounterMap;
    private boolean bIsDiversionMode;

    public Furniture(String ID, FurnitureLocation location, Color fColor) {
        this.ID = ID;
        this.location = location;
        this.finalLocation = null;
        this.color = fColor;
        diffStack = new Stack<>();
        encounterMap = new HashMap<>();
        bIsDiversionMode = false;
    }

    public FurnitureLocation getFinalLocation() {
        return finalLocation;
    }

    public FurnitureLocation getLocation() {
        return location;
    }

    public void setLocation(FurnitureLocation location) {
        this.location = location;
    }

    public String getID() {
        return ID;
    }

    public Color getColor() {
        return color;
    }

    public void moveUp() {
        location.moveUp();
    }

    public void moveDown() {
        location.moveDown();
    }

    public void moveLeft() {
        location.moveLeft();
    }

    public void moveRight() {
        location.moveRight();

    }

    public void setFinalLocation(FurnitureLocation fl) {
        this.finalLocation = fl;
    }

    public Diff getCurrentDiff() {
        return new Diff(this.getFinalLocation(), this.getLocation());
    }

    public boolean isSquare() {
        return (this.location.br.x - this.location.tl.x == this.location.br.y - this.location.tl.y);
    }

    public void pushDiff(Diff diff) {
//        System.out.println(this.getID() + ".->Pushing Diff: " + diff);
        diffStack.push(diff);
    }

    public void popDiff() {
//        System.out.println(this.getID() + ".<-POPING Diff: " + diffStack.pop());
         diffStack.pop();
    }

    /** Gets the furniture virtual location in regards to the current diff fron the final location **/
    public FurnitureLocation getVirtualLocation() {
        // if the stack is empty than real location is needed
        if (diffStack.isEmpty()) {
            return getLocation();
        }
        // other wise get calculated new location from final location with diff
        Diff currentDiff = diffStack.peek();
        Pos vTl = new Pos(finalLocation.tl.x - currentDiff.getTlx(), finalLocation.tl.y - currentDiff.getTly());
        Pos vBr = new Pos(finalLocation.br.x - currentDiff.getBrx(), finalLocation.br.y - currentDiff.getBry());
        return new FurnitureLocation(vTl, vBr);

    }

    @Override
    public String toString() {
        return "Furniture{" +
                "ID='" + ID + '\'' +
                ", location=" + location +
                ", finalLocation=" + finalLocation +
                ", color=" + color +
                ", diffStack=" + diffStack +
                '}';
    }

    public Diff peekDiff() {
        return diffStack.peek();
    }

    public int addEncounter(Furniture resFurniture) {
        int encNum = 1;
        String id = resFurniture.getID();
        if (encounterMap.containsKey(id)) {
            encNum = encounterMap.get(id) + 1;
        }
        encounterMap.put(id, encNum);
//        System.out.println("counter: " + encNum);
        return encNum;
    }

    public void clearEncounter(Furniture encounteredFurniture) {
        encounterMap.remove(encounteredFurniture.getID());
    }

    public boolean isAtFinalLocation() {
        return getLocation().equals(getFinalLocation());
    }

    public int getWidth() {
        return getLocation().br.x - getLocation().tl.x + 1;
    }

    public int getHeight() {
        return getLocation().br.y - getLocation().tl.y + 1;
    }

    public boolean getIsDiversionMode() {
        boolean bRes = bIsDiversionMode;
        if (bRes) {
            bIsDiversionMode = false;
        }
        return bRes;
    }

    public void setDiversionMode(boolean bIsDiversionMode) {
        this.bIsDiversionMode = bIsDiversionMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Furniture furniture = (Furniture) o;
        if (ID != null ? !ID.equals(furniture.ID) : furniture.ID != null) return false;
        if (location != null ? !location.equals(furniture.location) : furniture.location != null) return false;
        return true;
    }

    public boolean isPopStackEmpty() {
        return diffStack.size() < 1;
    }
}
