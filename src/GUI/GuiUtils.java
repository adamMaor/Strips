package GUI;

import Constants.Constants;
import Constants.Globals;
import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.LogicUtils;
import Logic.Pos;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Adam on 09/01/2017.
 */
public class GuiUtils {
    private Globals globals;
    private LogicUtils logicUtils;


    public GuiUtils(Globals globals, LogicUtils logicUtils) {
        this.globals = globals;
        this.logicUtils = logicUtils;


    }

    public CompoundBorder getBoarderForPos(int x, int y) {
        Pos pos = new Pos(x,y);
        int upWallThickness = logicUtils.hasWall(pos, Constants.Directions.UP) ? Constants.Sizes.wallBorderWidth : 0;
        int leftWallThickness = logicUtils.hasWall(pos, Constants.Directions.LEFT) ? Constants.Sizes.wallBorderWidth : 0;
        int downWallThickness = logicUtils.hasWall(pos, Constants.Directions.DOWN) ? Constants.Sizes.wallBorderWidth : 0;
        int rightWallThickness = logicUtils.hasWall(pos, Constants.Directions.RIGHT) ? Constants.Sizes.wallBorderWidth : 0;
        MatteBorder outerBorder = BorderFactory.createMatteBorder(upWallThickness,
                                                                leftWallThickness,
                                                                downWallThickness,
                                                                rightWallThickness,
                                                                Constants.Colors.WallColor);
        MatteBorder standardBorder = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                                                                    Constants.Sizes.normalBorderWidth,
                                                                    Constants.Sizes.normalBorderWidth,
                                                                    Constants.Sizes.normalBorderWidth,
                                                                    Constants.Colors.StandardBorderColor);
        return new CompoundBorder(outerBorder, standardBorder);
    }

    public void addFurniture(Furniture f) {
        logicUtils.addFurniture(f);
    }

    public void moveFurniture(String fId, byte direction) {
        logicUtils.moveFurniture(fId, direction);
    }

    public Furniture getFurniture(String fId) {
        return logicUtils.getFurniture(fId);
    }

    public void deleteFurniture(Furniture f) {
        logicUtils.deleteFurniture(f);
    }

    public boolean checkForNewFurnitureSpace(FurnitureLocation fLocation) {
        return logicUtils.checkForNewFurnitureSpace(fLocation);
    }

    public void rotateFurniture(String fId, byte direction) {
        logicUtils.rotateFurniture(fId, direction);
    }
}
