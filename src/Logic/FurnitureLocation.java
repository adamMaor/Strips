package Logic;

import Constants.Constants;

/**
 * Created by Adam on 09/01/2017.
 */
public class FurnitureLocation {
    public Pos tl, br; // Top Left and Bottom Right Positions;

    public FurnitureLocation(Pos tl, Pos br) {
        this.tl = tl;
        this.br = br;
    }

    public void moveUp() {
        if (tl.y > 0) {
            tl.y--;
            br.y--;
        }
        else {
            // ERROR
        }
    }

    public void moveDown() {
        if (br.y < Constants.Sizes.boardHeight - 1) {
            tl.y++;
            br.y++;
        }
        else {
            // ERROR
        }
    }

    public void moveLeft() {
        if (tl.x > 0 ) {
            tl.x--;
            br.x--;
        }
        else {
            // ERROR
        }
    }

    public void moveRight() {
        if (br.x < Constants.Sizes.boardWidth - 1) {
            tl.x++;
            br.x++;
        }
    }

    @Override
    public String toString() {
        return "FurnitureLocation{" +
                "tl=" + tl +
                ", br=" + br +
                '}';
    }
}
