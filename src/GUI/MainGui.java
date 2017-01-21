package GUI;

import Constants.Constants;
import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.Pos;
import com.sun.javaws.Globals;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Adam on 09/01/2017.
 */
public class MainGui{
    private JButton addNewFurnitureItemButton;
    private JButton moveUpButton;
    private JButton moveLeftButton;
    private JButton moveRightButton;
    private JButton moveDownButton;
    private JButton rotateRightButton;
    private JButton rotateLeftButton;
    private JComboBox furnitureComboBox;
    private JPanel boardPanel;
    public JPanel mainPanel;
    private JButton deleteButton;
    private JButton setFinalLocationButton;
    private JButton acceptFinalButton;
    private JButton solveButton;
    private JPanel LocationPanel;
    private JButton nextMoveButton;
    private JRadioButton autoPlayRadioButton;
    private JList stackList;
    private JList planList;
    private DefaultListModel<String> stackModel;
    private DefaultListModel<String> planModel;
    private JPanel solvePanel;
    private JButton clearBoardAndRestartButton;
    private JButton stopAndRestoreInitialButton;
    private FurnitureLocation tempFurnitureLocation;

    private GuiUtils utils;
    private GuiBoardItem[][] boardItems;
    private int currentFurniture;
    private boolean bIsAutoRun;


    public MainGui(final GuiUtils utils) {
        this.utils = utils;
        currentFurniture = 1;
        initBoard();
        setNavigationButtonsEnabled(false);
        switchToSolveMode(false);
        addNewFurnitureItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fId = "F" + currentFurniture;
                NewFurnitureDlg dialog = new NewFurnitureDlg(utils, fId);
                dialog.pack();
                dialog.setLocationRelativeTo(mainPanel);
                dialog.setVisible(true);
                FurnitureLocation fl = dialog.getfLocation();
                if (fl != null) {
                    currentFurniture++;
                    Random rand = new Random();
                    int r = rand.nextInt(122);
                    int g = rand.nextInt(122);
                    int b = rand.nextInt(122);
                    Color fColor = new Color(r,g ,b);
                    Furniture f = new Furniture(fId, fl, fColor);
                    // Check legality for new furniture
                    utils.addFurniture(f);
                    furnitureComboBox.addItem(fId);
                    furnitureComboBox.setSelectedItem(fId);
                    paintFurniture(f);
                }
            }
        });
        moveUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCurrentFurniture(Constants.Directions.UP);
            }
        });
        moveDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCurrentFurniture(Constants.Directions.DOWN);
            }
        });
        moveRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCurrentFurniture(Constants.Directions.RIGHT);
            }
        });
        moveLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCurrentFurniture(Constants.Directions.LEFT);
            }
        });
        rotateLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateCurrentFurniture(Constants.Directions.LEFT);
            }
        });
        rotateRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateCurrentFurniture(Constants.Directions.RIGHT);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fId = (String)furnitureComboBox.getSelectedItem();
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + fId + " ?","Are You Sure?",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    Furniture f = utils.getFurniture(fId);
                    unPaintFurniture(f);
                    furnitureComboBox.removeItem(fId);
                    utils.deleteFurniture(f);
                }
            }
        });
        setFinalLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fId = (String)furnitureComboBox.getSelectedItem();
                int dialogResult = JOptionPane.showConfirmDialog (null,
                        "Are you sure you want to set the final location for item " + fId + "?\n no other changes to this item will be possible." ,
                        "Are You Sure?",
                        JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    setSetFinalLocationMode(true);
                    Furniture f = utils.getFurniture(fId);
                    tempFurnitureLocation = new FurnitureLocation(new Pos(f.getLocation().tl.x,f.getLocation().tl.y),
                            new Pos(f.getLocation().br.x, f.getLocation().br.y));
                    markLocation(f.getLocation(), f.getColor());
                }
            }
        });
        acceptFinalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSetFinalLocationMode(false);
                String fId = (String)furnitureComboBox.getSelectedItem();
                Furniture f = utils.getFurniture(fId);
                markLocation(f.getLocation(), f.getColor());
                f.setFinalLocation(new FurnitureLocation(f.getLocation().tl, f.getLocation().br));
                unPaintFurniture(f);
                unMarkLocation(tempFurnitureLocation);
                f.setLocation(tempFurnitureLocation);
                paintFurniture(f);
                furnitureComboBox.removeItem(fId);
                if (furnitureComboBox.getItemCount() > 0) {
                    furnitureComboBox.setSelectedIndex(0);
                }
            }
        });
        furnitureComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = furnitureComboBox.getSelectedIndex();
                boolean bValid = index != -1;
                setNavigationButtonsEnabled(bValid);
                solveButton.setVisible(!bValid);
            }
        });
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (utils.validateStateForSolve()) {
                    switchToSolveMode(true);
                    repaintBoard();
                    repaintStacks();
                }
                else {
                    System.out.println("Error - illegal board!!!");
                }
            }
        });
        nextMoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextMoveButton.setEnabled(!bIsAutoRun);
                generateMove();
            }
        });
        autoPlayRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bIsAutoRun = autoPlayRadioButton.isSelected();
                if (!bIsAutoRun) {
                    nextMoveButton.setEnabled(true);
                }
            }
        });
        clearBoardAndRestartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
            }
        });
        stopAndRestoreInitialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreInitialState(true);
            }
        });
    }

    private void generateMove() {
        if (utils.makeMove() == true) {
            Thread repaint = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (utils.repaintBoardNeeded()) {
                        repaintBoard();
                    }
                    repaintStacks();
                }
            });
            EventQueue.invokeLater(repaint);
        }
        else {
            // Done
            bIsAutoRun = false;
            Object[] options1 = { "Replay the Plan (calculated)", "Move back To Initial State (Re-Calculate)", "Quit" };
            int input;
            if (utils.checkForSuccess()) {
                int planSize = utils.getCurrentPlan().size();
                input = JOptionPane.showOptionDialog(null, "Success!!!\nA solution was found :)\nNumber of moves for solution: " + planSize + "\nOverAll Net Working time: " + utils.getTotalWorkTime() + " MS", "Success!!!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options1, null);
            }
            else {
                input = JOptionPane.showOptionDialog(null, "Failed!!!\nSomething went wrong and a solution was NOT found :)", "Failed...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options1, null);
            }

            if(input == JOptionPane.YES_OPTION)
            {
                restoreInitialState(false);
            }
            else if (input == JOptionPane.NO_OPTION) {
                restoreInitialState(true);
            }
            else if (input == JOptionPane.CANCEL_OPTION){
                System.exit(0);
            }
        }

        if (bIsAutoRun) {
            Thread setNextAvail = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (utils.repaintBoardNeeded()) {
                            Thread.sleep(utils.getDelayTime());
                        }
                        generateMove();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            setNextAvail.start();
        }
    }

    private void restoreInitialState(boolean bIsFullReset) {
        for (Furniture f : utils.getAllFurniture()) {
            unPaintFurniture(f);
            unMarkLocation(f.getFinalLocation());
        }
        initBoard();
        utils.restoreInitialState(bIsFullReset);
        repaintBoard();
        repaintStacks();
        autoPlayRadioButton.setSelected(false);
        nextMoveButton.setEnabled(true);
        bIsAutoRun = false;
        switchToSolveMode(!bIsFullReset);
    }

    private void resetBoard() {
        for (Furniture f : utils.getAllFurniture()) {
            unPaintFurniture(f);
            unMarkLocation(f.getFinalLocation());
        }
        currentFurniture = 1;
        furnitureComboBox.removeAllItems();
//        furnitureComboBox.setSelectedIndex(-1);
        solveButton.setVisible(false);
        utils.resetAll();
        repaintBoard();
        repaintStacks();
        autoPlayRadioButton.setSelected(false);
        nextMoveButton.setEnabled(true);
        bIsAutoRun = false;
        switchToSolveMode(false);
    }

    private void repaintStacks() {
        stackModel.clear();
        ArrayList<String> stack = utils.getCurrentStack();
        for (int i = 0 ; i < stack.size(); i++) {
//            System.out.println("1" + stack.get(i));
            stackModel.addElement(stack.get(i));
        }

        planModel.clear();
        ArrayList<String> plan = utils.getCurrentPlan();
        for (int i = 0 ; i < plan.size(); i++) {
            planModel.addElement(plan.get(i));
        }
    }

    private void repaintBoard() {
        initBoard();
        for (Furniture f : utils.getAllFurniture()) {
            paintFurniture(f);
            markLocation(f.getFinalLocation(), f.getColor());
        }
    }

    private void switchToSolveMode(boolean b) {
        // hide positioning control
        LocationPanel.setVisible(!b);
        // show moves control , stack and plan
        solvePanel.setVisible(b);
        stopAndRestoreInitialButton.setEnabled(b);
    }

    private void setNavigationButtonsEnabled(boolean b) {
        moveUpButton.setEnabled(b);
        moveLeftButton.setEnabled(b);
        moveRightButton.setEnabled(b);
        moveDownButton.setEnabled(b);
        rotateRightButton.setEnabled(b);
        rotateLeftButton.setEnabled(b);
        deleteButton.setEnabled(b);
        setFinalLocationButton.setEnabled(b);
        acceptFinalButton.setEnabled(b);
        furnitureComboBox.setEnabled(b);
    }

    private void unMarkLocation(FurnitureLocation furnitureLocation) {
        if (furnitureLocation != null) {
            int y = furnitureLocation.tl.y;
            while (y <= furnitureLocation.br.y) {
                int x = furnitureLocation.tl.x;
                while (x <= furnitureLocation.br.x) {
                    boardItems[y][x].setBorder(utils.getBoarderForPos(x,y));
                    x++;
                }
                y++;
            }
        }
    }

    private void markLocation(FurnitureLocation furnitureLocation, Color color) {
        if (furnitureLocation != null) {
            int y = furnitureLocation.tl.y;
            int x = furnitureLocation.tl.x;
            int width = furnitureLocation.br.x - x;
            int height = furnitureLocation.br.y - y;

            if (width == 0 && height == 0) {
                boardItems[y][x].setBorder(utils.getMarkBorder(true, true, true, true, color));
            } else if (width == 0) {
                boardItems[y++][x].setBorder(utils.getMarkBorder(true, true, false, true, color));
                CompoundBorder midBorder = utils.getMarkBorder(false, true, false, true, color);
                while (y < furnitureLocation.br.y) {
                    boardItems[y++][x].setBorder(midBorder);
                }
                boardItems[y][x].setBorder(utils.getMarkBorder(false, true, true, true, color));
            } else if (height == 0) {
                boardItems[y][x++].setBorder(utils.getMarkBorder(true, true, true, false, color));
                CompoundBorder midBorder = utils.getMarkBorder(true, false, true, false, color);
                while (x < furnitureLocation.br.x) {
                    boardItems[y][x++].setBorder(midBorder);
                }
                boardItems[y][x].setBorder(utils.getMarkBorder(true, false, true, true, color));
            } else {
                boardItems[y][x++].setBorder(utils.getMarkBorder(true, true, false, false, color));
                CompoundBorder midUpperBorder = utils.getMarkBorder(true, false, false, false, color);
                while (x < furnitureLocation.br.x) {
                    boardItems[y][x++].setBorder(midUpperBorder);
                }
                boardItems[y++][x].setBorder(utils.getMarkBorder(true, false, false, true, color));
                CompoundBorder midRightBorder = utils.getMarkBorder(false, false, false, true, color);
                while (y < furnitureLocation.br.y) {
                    boardItems[y++][x].setBorder(midRightBorder);
                }
                boardItems[y][x--].setBorder(utils.getMarkBorder(false, false, true, true, color));
                CompoundBorder midDownBorder = utils.getMarkBorder(false, false, true, false, color);
                while (x > furnitureLocation.tl.x) {
                    boardItems[y][x--].setBorder(midDownBorder);
                }
                boardItems[y--][x].setBorder(utils.getMarkBorder(false, true, true, false, color));
                CompoundBorder midLeftBorder = utils.getMarkBorder(false, true, false, false, color);
                while (y > furnitureLocation.tl.y) {
                    boardItems[y--][x].setBorder(midLeftBorder);
                }
            }
        }
    }

    private void setSetFinalLocationMode(boolean b) {
        furnitureComboBox.setEnabled(!b);
        acceptFinalButton.setVisible(b);
        deleteButton.setVisible(!b);
        setFinalLocationButton.setEnabled(!b);
    }

    private void rotateCurrentFurniture(byte direction) {
        String fId = (String)furnitureComboBox.getSelectedItem();
        Furniture f = utils.getFurniture(fId);
        if (f != null) {
            unPaintFurniture(f);
            utils.rotateFurniture(fId, direction);
            paintFurniture(f);
        }
    }

    private void moveCurrentFurniture(byte direction) {
        String fId = (String)furnitureComboBox.getSelectedItem();
        Furniture f = utils.getFurniture(fId);
        if (f != null) {
            unPaintFurniture(f);
            utils.moveFurniture(fId, direction);
            paintFurniture(f);
        }
    }

    private void paintFurniture(Furniture f) {
        FurnitureLocation vfl = f.getVirtualLocation();
        Color fColor = f.getColor();
        if (vfl != f.getLocation()) {
            Color virtualColor = new Color (fColor.getRed() + 50, fColor.getGreen() + 50,fColor.getBlue() + 50);
            Pos pos21 = vfl.tl;
            Pos pos22 = vfl.br;
            for (int x = pos21.x; x <= pos22.x; x++) {
                for (int y = pos21.y; y <= pos22.y; y++) {
                    boardItems[y][x].setBackground(virtualColor);
                    boardItems[y][x].setText("v_" + f.getID(), Color.black);
                }
            }
        }
        Pos pos1 = f.getLocation().tl;
        Pos pos2 = f.getLocation().br;
        for (int x = pos1.x; x <= pos2.x; x++) {
            for (int y = pos1.y; y <= pos2.y; y++) {
                boardItems[y][x].setBackground(f.getColor());
                boardItems[y][x].setText(f.getID(), Color.white);
            }
        }

    }

    private void unPaintFurniture(Furniture f) {
        FurnitureLocation vfl = f.getVirtualLocation();
        if (vfl != f.getLocation()) {
            Pos pos21 = vfl.tl;
            Pos pos22 = vfl.br;
            for (int x = pos21.x; x <= pos22.x; x++) {
                for (int y = pos21.y; y <= pos22.y; y++) {
                    boardItems[y][x].setBackground(Constants.Colors.MainBoardItemColor);
                    boardItems[y][x].setText("", Color.white);
                }
            }
        }
        Pos pos1 = f.getLocation().tl;
        Pos pos2 = f.getLocation().br;
        for (int x = pos1.x; x <= pos2.x; x++) {
            for (int y = pos1.y; y <= pos2.y; y++) {
                boardItems[y][x].setBackground(Constants.Colors.MainBoardItemColor);
                boardItems[y][x].setText("", Color.white);
            }
        }
    }

    private void initBoard() {
        boardPanel.removeAll();
        boardItems = new GuiBoardItem[Constants.Sizes.boardHeight][Constants.Sizes.boardWidth];
        for (int y = 0; y < Constants.Sizes.boardHeight; y++) {
            for (int x = 0; x < Constants.Sizes.boardWidth; x++) {
                GuiBoardItem guiItem = new GuiBoardItem(Constants.Colors.MainBoardItemColor, utils.getBoarderForPos(x,y));
                boardItems[y][x] = guiItem;
                boardPanel.add(guiItem);
            }
        }
    }

    private void createUIComponents() {
        boardPanel = new JPanel(new GridLayout(Constants.Sizes.boardHeight,Constants.Sizes.boardWidth));
        stackModel = new DefaultListModel<>();
        stackList = new JList<>(stackModel);
        planModel = new DefaultListModel<>();
        planList = new JList<>(planModel);
    }
}
