package Logic;

import java.awt.*;
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

    public Furniture(String ID, FurnitureLocation location, Color fColor) {
        this.ID = ID;
        this.location = location;
        this.finalLocation = null;
        this.color = fColor;
        diffStack = new Stack<>();
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

    public void rotateLeft() {

    }

    public void rotateRight() {

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
//        System.out.println("Pushing Diff: " + diff);
        diffStack.push(diff);
    }

    public void popDiff() {
//        System.out.println("POPING Diff: " + diffStack.pop());
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
}
