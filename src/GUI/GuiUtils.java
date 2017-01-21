package GUI;

import Constants.Constants;
import Constants.Globals;
import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.LogicUtils;
import Logic.Pos;
import Strips.StripsLogic;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Adam on 09/01/2017.
 */
public class GuiUtils {
    private Globals globals;
    private LogicUtils logicUtils;
    private StripsLogic stripsLogic;


    public GuiUtils(Globals globals, LogicUtils logicUtils, StripsLogic stripsLogic) {
        this.globals = globals;
        this.logicUtils = logicUtils;
        this.stripsLogic = stripsLogic;
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

    public CompoundBorder getMarkBorder(boolean bUp, boolean bLeft, boolean bDown, boolean bRight, Color color1) {
        Color color2 = Constants.Colors.StandardBorderColor;
        int thickWidth = Constants.Sizes.ThickBorderWidth;
        int normalWidth = Constants.Sizes.normalBorderWidth;
        int noWidth = 0;

        int top1, top2, left1, left2, down1, down2, right1, right2;
        top1 = bUp ? thickWidth : noWidth;
        left1 = bLeft ? thickWidth : noWidth;
        down1 = bDown ? thickWidth : noWidth;
        right1 = bRight ? thickWidth : noWidth;


        top2 = bUp ? noWidth : normalWidth;
        left2 = bLeft ? noWidth : normalWidth;
        down2 = bDown ? noWidth : normalWidth;
        right2 = bRight ? noWidth : normalWidth;

        return new CompoundBorder(new MatteBorder(top1, left1, down1, right1, color1),
                                    new MatteBorder(top2, left2, down2, right2, color2));

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

    public boolean validateStateForSolve() {
        if (logicUtils.validateStateForSolve()) {
            stripsLogic.init();
            return true;
        }
        return false;
    }

    /**
     * Use Strips Logic to make next move
     * @return true if move made - false if finished all moves
     */
    public boolean makeMove() {
        return stripsLogic.makeMove();
    }

    public boolean repaintBoardNeeded() {
        return stripsLogic.isRepaintBoardNeeded();
    }

    public Collection<Furniture> getAllFurniture() {
        return logicUtils.getAllFurniture();
    }

    public ArrayList<String> getCurrentStack() {
        return stripsLogic.getCurrentStack();
    }

    public ArrayList<String> getCurrentPlan() {
        return stripsLogic.getCurrentPlan();
    }

    public void resetAll() {
        logicUtils.resetAll();
        stripsLogic.resetAll(true);
    }

    public void restoreInitialState(boolean bIsFullReset) {
        logicUtils.restoreInitialState();
        stripsLogic.resetAll(bIsFullReset);
    }

    public boolean checkForSuccess() {
        return stripsLogic.isbSuccess();
    }

    public long getTotalWorkTime() {
        return stripsLogic.getTotalWorkTime();
    }

    public long getDelayTime() {
        return globals.getDelayTime();
    }
}
