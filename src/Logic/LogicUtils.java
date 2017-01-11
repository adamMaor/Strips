package Logic;

import Constants.*;

import java.util.ArrayList;
import java.util.HashMap;

import static Constants.Constants.Directions.*;

/**
 * Created by Adam on 09/01/2017.
 */
public class LogicUtils {

    private Globals globals;
    private HashMap<String, Furniture> furnitureMap;

    /**** Strips Logic API ****/

    public boolean canMove(Furniture f, byte direction) {
        FurnitureLocation fLocation = f.getLocation();
        return checkForWalls(fLocation, direction) && checkForOtherFurniture(fLocation, direction);
    }

    public boolean canRotate(Furniture f, byte direction) {
        boolean bRes = true;
        FurnitureLocation fLocation = f.getLocation();
        FurnitureLocation newLocation = getRotatedLocation(fLocation, direction);
        // check location inBounds
        if (inBounds(newLocation) == false) {
            // throw exception - can't rotate - out of bounds
            bRes = false;
        }
        // check for wall
        if (checkForWalls(newLocation, NONE) == false) {
            // throw exception - can't rotate - Walls in new location
            bRes = false;
        }
        // check for other furniture
        deleteFurniture(f); // remove the furniture so to not check for other furniture with it - found other furniture but its the same one
        if(checkForOtherFurniture(newLocation, NONE) == false) {
            // throw exception - can't rotate - other furniture in new location
            bRes = false;
        }
        addFurniture(f);
        return bRes;
    }

    public boolean moveFurniture(Furniture f, byte direction) {
        boolean bRes = true;
        if (f != null) {
            if (canMove(f, direction)) {
                switch (direction) {
                    case NONE:
                        break;
                    case UP:
                        f.moveUp();
                        break;
                    case DOWN:
                        f.moveDown();
                        break;
                    case LEFT:
                        f.moveLeft();
                        break;
                    case RIGHT:
                        f.moveRight();
                        break;
                }
            }
        }
        else {
            bRes = false;
        }
        return bRes;
    }

    public void rotateFurniture(Furniture f, byte direction) {
        if (canRotate(f, direction)) {
            FurnitureLocation fLocation = f.getLocation();
            FurnitureLocation newLocation = getRotatedLocation(fLocation, direction);
            f.setLocation(newLocation);
        }
    }

    /***************************************************************/

    public LogicUtils(Globals globals) {
        this.globals = globals;
        furnitureMap = new HashMap<String, Furniture>();
    }

    /**
     * will return second pos in the dirction if in bounds
     * @param pos1
     * @param direction
     * @return second pos in the dirction if in bounds, NULL otherwise
     */
    private Pos inBounds(Pos pos1, byte direction) {
        Pos pos2 = null;
        int x = pos1.x;
        int y = pos1.y;
        switch (direction) {

            case UP:
                if (y > 0) {
                    pos2 = new Pos(x, y - 1);
                }
                break;
            case DOWN:
                if (y < Constants.Sizes.boardHeight - 1) {
                    pos2 = new Pos(x, y + 1);
                }
                break;
            case LEFT:
                if (x > 0) {
                    pos2 = new Pos(x - 1, y);
                }
                break;
            case RIGHT:
                if (x < Constants.Sizes.boardWidth - 1) {
                    pos2 = new Pos(x + 1, y);
                }
                break;
        }
        return pos2;
    }

    /**
     *
     * @param pos1 - pos to check
     * @param direction - direction to check
     * @return true if wall exists between pos and next pos in direction
     */
    public boolean hasWall(Pos pos1, byte direction) {
        Pos pos2 = inBounds(pos1, direction);
        boolean bRes = false;
        if (pos2 != null){
            Wall possibleWall = new Wall(pos1,pos2);
            for (Wall wall : globals.wallList) {
                if (possibleWall.equals(wall)){
                    bRes = true;
                    break;
                }
            }
        }
        else {
            bRes = true;
        }
        return bRes;
    }



    public void addFurniture(Furniture f) {
        furnitureMap.put(f.getID(), f);
    }

    public boolean moveFurniture(String fId, byte direction) {
        Furniture f = furnitureMap.get(fId);
        return moveFurniture(f, direction);
    }

    private boolean checkForOtherFurniture(FurnitureLocation f, byte direction) {
        ArrayList<Pos> posList = new ArrayList<Pos>(); // a list of points to check
        Pos tlPos = f.tl;
        Pos brPos = f.br;
        switch (direction) {
            case NONE:
                return checkNewFurnitureAreaForOtherFurniture(f);
            case UP:
                if (tlPos.y > 0) {
                    for (int x = tlPos.x; x <= brPos.x; x++) {
                        posList.add(new Pos(x,tlPos.y - 1)); // add all the top pos
                    }
                }
                break;
            case DOWN:
                if (brPos.y < Constants.Sizes.boardHeight - 1) {
                    for (int x = brPos.x; x >= tlPos.x; x--) {
                        posList.add(new Pos(x,brPos.y + 1)); // add all the bottom pos
                    }
                }
                break;
            case LEFT:
                if (tlPos.x > 0) {
                    for (int y = tlPos.y; y <= brPos.y; y++) {
                        posList.add(new Pos(tlPos.x - 1, y)); // add all the Left pos
                    }
                }
                break;
            case RIGHT:
                if (brPos.x < Constants.Sizes.boardWidth - 1) {
                    for (int y = brPos.y; y >= tlPos.y; y--) {
                        posList.add(new Pos(brPos.x + 1,y)); // add all the Right pos
                    }
                }
                break;
        }
        if (posList.size() == 0) { // mean that you cannot move because of a wall
            return false;
        }
        return checkForExistingFurniture(posList);
    }

    private boolean checkForExistingFurniture(ArrayList<Pos> posList) {
        for (Pos pos : posList) {
            for (Furniture furniture : furnitureMap.values()) {
                if (isPosCovered(pos, furniture.getLocation())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks for other furniture in initial positioning
     * @param f
     * @return true if passed the test, false otherwise
     */
    private boolean checkNewFurnitureAreaForOtherFurniture(FurnitureLocation f) {
        boolean bRes = true;
        for (Furniture furniture : furnitureMap.values()) {
            if (isLocationCovered(furniture.getLocation(), f)) {
                bRes = false;
                break;
            }
        }
        return bRes;
    }

    private boolean isLocationCovered(FurnitureLocation f1, FurnitureLocation f2) {
        return  (isPosCovered(f2.tl, f1) || isPosCovered(f2.br, f1) );

    }

    private boolean isPosCovered(Pos pos, FurnitureLocation fl) {
        int minX = fl.tl.x, minY = fl.tl.y, maxX = fl.br.x, maxY = fl.br.y;
        return (pos.x >= minX && pos.x <= maxX && pos.y >= minY && pos.y <= maxY);
    }

    /**
     * check for walls in a movement or an initail positioning
     * @param f
     * @param direction
     * @return True if passed the test (can move), false otherwise
     */
    private boolean checkForWalls(FurnitureLocation f, byte direction) {
        boolean bRes = true;
        ArrayList<Pos> posList = new ArrayList<Pos>(); // a list of points to check
        Pos tlPos = f.tl;
        Pos brPos = f.br;
        byte directionForInnerWallsCheck = NONE;
        switch (direction) {
            case NONE: // check if furniture area is above walls - illegal
                return checkNewFurnitureAreaForWalls(f);
            case UP:
                for (int x = tlPos.x; x <= brPos.x; x++) {
                    posList.add(new Pos(x,tlPos.y)); // add all the top pos
                }
                directionForInnerWallsCheck = RIGHT;
                break;
            case DOWN:
                for (int x = brPos.x; x >= tlPos.x; x--) {
                    posList.add(new Pos(x,brPos.y)); // add all the bottom pos
                }
                directionForInnerWallsCheck = LEFT;
                break;
            case LEFT:
                for (int y = tlPos.y; y <= brPos.y; y++) {
                    posList.add(new Pos(tlPos.x,y)); // add all the Left pos
                }
                directionForInnerWallsCheck = DOWN;
                break;
            case RIGHT:
                for (int y = brPos.y; y >= tlPos.y; y--) {
                    posList.add(new Pos(brPos.x,y)); // add all the Right pos
                }
                directionForInnerWallsCheck = UP;
                break;
        }
        if (checkInnerWalls(posList, direction, directionForInnerWallsCheck) == true) {
            bRes = false;
        }
        else {
            for (Pos pos : posList) {
                if (hasWall(pos,direction) == true) {
                    bRes = false;
                    break;
                }
            }
        }
        return bRes;
    }

    /**
     * Check all the inner walls possible - goes from top left to button right and checks right and button borders
     * @param f
     * @return true if passed the test, false other wise
     */
    private boolean checkNewFurnitureAreaForWalls(FurnitureLocation f) {
        boolean bRes = true;
        for (int y = f.tl.y; y <= f.br.y; y++) {
            for (int x = f.tl.x; x <= f.br.x; x++) {
                Pos pos = new Pos (x,y);
                boolean b1 = false, b2 = false;
                if (y < f.br.y) {
                    b1 = hasWall(pos, DOWN);
                }
                if (x < f.br.x) {
                    b2 = hasWall(pos, RIGHT);
                }
                if (b1 || b2) {
                    bRes = false;
                    break;
                }
            }
            if (bRes == false) break;
        }

        return bRes;

    }

    /**
     * Checks for inner walls (between the position list - used for corner case
     * where the furniture is above a wall (spiked on a wall)
     * @param posList
     * @return true if wall exists, false otherwise
     */
    private boolean checkInnerWalls(ArrayList<Pos> posList, byte originalDirection, byte checkDirection) {
        boolean bRes = false;
        if (checkDirection != NONE) {
            for (int i = 0; i < posList.size() - 1; i++ ) {
                Pos outerPos = inBounds(posList.get(i), originalDirection);
                if (outerPos != null && hasWall(outerPos, checkDirection)) {
                    bRes = true;
                    break;
                }
            }
        }
        return bRes;
    }

    public Furniture getFurniture(String fId) {
        return furnitureMap.get(fId);
    }

    public void deleteFurniture(Furniture f) {
        furnitureMap.remove(f.getID());
    }

    public boolean checkForNewFurnitureSpace(FurnitureLocation fLocation) {
        return (inBounds(fLocation)
                && checkForWalls(fLocation, NONE)
                && checkForOtherFurniture(fLocation, NONE));
    }

    private boolean inBounds(FurnitureLocation fLocation) {
        Pos tl = fLocation.tl;
        Pos br = fLocation.br;
        return (tl.x >= 0 && tl.y >= 0 && br.x < Constants.Sizes.boardWidth && br.y < Constants.Sizes.boardHeight);
    }

    public void rotateFurniture(String fId, byte direction) {
        Furniture f = getFurniture(fId);
        rotateFurniture(f, direction);
    }



    /**
     * get all pos in new location that are not in old location
     * @param oldLocation
     * @param newLocation
     * @return
     */
    private ArrayList<Pos> getDeltaPos(FurnitureLocation oldLocation, FurnitureLocation newLocation) {
        ArrayList<Pos> deltaPos = new ArrayList<Pos>();
        for (int y = newLocation.tl.y; y <= newLocation.br.y; y++ ) {
            for (int x = newLocation.tl.x; x <= newLocation.br.x; x++ ) {
                Pos currPos = new Pos(x,y);
                if (isPosCovered(currPos, oldLocation) == false) {
                    deltaPos.add(currPos);
                }
            }
        }
        return deltaPos;
    }

    /**
     * calculates new location after rotation
     * @param currLocation - old location before rotation
     * @param direction - which direction to rotate
     * @return the new location
     */
    private FurnitureLocation getRotatedLocation(FurnitureLocation currLocation, byte direction) {
        //new width = old height and the opposite
        int newWidth = currLocation.br.y - currLocation.tl.y + 1;
        int newHeight = currLocation.br.x - currLocation.tl.x + 1;
        int oldXCenter = (currLocation.tl.x + currLocation.br.x)/2;
        int oldYCenter = (currLocation.tl.y + currLocation.br.y)/2;

        int newTLX = oldXCenter - newWidth/2;
        int newTLY = oldYCenter - newHeight/2;

        if (newHeight % 2 == 0 && newWidth % 2 == 0) {
            newTLX++;
            newTLY++;
        }
        else if (newHeight % 2 != 0 && newWidth % 2 != 0) {
            // do nothing
        }
        else {
            if (direction == RIGHT) {
                newTLX++;
            }
            if (newHeight % 2 == 0){
                newTLY++;
            }
        }

        int newBRX = newTLX + newWidth - 1;
        int newBRY = newTLY + newHeight - 1;

        Pos newTLPos = new Pos(newTLX, newTLY);
        Pos newBRPos = new Pos(newBRX, newBRY);
        return new FurnitureLocation(newTLPos, newBRPos);
    }
}
