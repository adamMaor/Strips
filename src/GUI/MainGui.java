package GUI;

import Constants.Constants;
import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.Pos;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Adam on 09/01/2017.
 */
public class MainGui {
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
    private JPanel solvePanel;
    private FurnitureLocation tempFurnitureLocation;

    private GuiUtils utils;

    private GuiBoardItem[][] boardItems;

    public MainGui(final GuiUtils utils) {
        this.utils = utils;
        currentFurniture = 1;
        initBoard();
        setNavigationButtonsEnabled(false);

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
                    int r = rand.nextInt(255);
                    int g = rand.nextInt(255);
                    int b = rand.nextInt(255);
                    Color fColor = new Color(r,g,b);
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
                solveButton.setVisible(!bValid && utils.validateStateForSolve());
            }
        });
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (utils.validateStateForSolve()) {
                    switchToSolveMode();
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
    }

    private int currentFurniture;
    private boolean bIsAutoRun;
    private DefaultListModel<String> stackModel;
    private DefaultListModel<String> planModel;



    private void generateMove() {
        if (utils.makeMove() == true) {
            stackModel.clear();
            ArrayList<String> stack = utils.getCurrentStack();
            for (int i = 0 ; i < stack.size(); i++) {
                stackModel.addElement(stack.get(i));
            }

            planModel.clear();
            ArrayList<String> plan = utils.getCurrentPlan();
            for (int i = 0 ; i < plan.size(); i++) {
                planModel.addElement(plan.get(i));
            }

            Thread repaint = new Thread(new Runnable() {
                @Override
                public void run() {
                    repaintBoard();
                }
            });
            repaint.start();
        }
        else {
            // Done
        }

        if (bIsAutoRun) {
            Thread setNextAvail = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(250);
                        generateMove();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            setNextAvail.start();
        }
    }

    private void repaintBoard() {
        initBoard();
        for (Furniture f : utils.getAllFurniture()) {
            paintFurniture(f);
            markLocation(f.getFinalLocation(), f.getColor());
        }
    }

    private void switchToSolveMode() {
        // hide positioning control
        LocationPanel.setVisible(false);
        // show moves control , stack and plan
        solvePanel.setVisible(true);
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

    private void markLocation(FurnitureLocation furnitureLocation, Color color) {
        int y = furnitureLocation.tl.y;
        int x = furnitureLocation.tl.x;
        int width = furnitureLocation.br.x - x;
        int height = furnitureLocation.br.y - y;

        if (width == 0 && height == 0) {
            boardItems[y][x].setBorder(utils.getMarkBorder(true, true, true, true, color));
        }
        else if (width == 0) {
            boardItems[y++][x].setBorder(utils.getMarkBorder(true, true, false, true, color));
            CompoundBorder midBorder = utils.getMarkBorder(false, true, false, true, color);
            while (y < furnitureLocation.br.y) {
                boardItems[y++][x].setBorder(midBorder);
            }
            boardItems[y][x].setBorder(utils.getMarkBorder(false, true, true, true, color));
        }
        else if (height == 0) {
            boardItems[y][x++].setBorder(utils.getMarkBorder(true, true, true, false, color));
            CompoundBorder midBorder = utils.getMarkBorder(true, false, true, false, color);
            while (x < furnitureLocation.br.x) {
                boardItems[y][x++].setBorder(midBorder);
            }
            boardItems[y][x].setBorder(utils.getMarkBorder(true, false, true, true, color));
        }
        else {
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
        Pos pos1 = f.getLocation().tl;
        Pos pos2 = f.getLocation().br;
        for (int x = pos1.x; x <= pos2.x; x++) {
            for (int y = pos1.y; y <= pos2.y; y++) {
                boardItems[y][x].setBackground(f.getColor());
                boardItems[y][x].setText(f.getID());
            }
        }
    }

    private void unPaintFurniture(Furniture f) {
        Pos pos1 = f.getLocation().tl;
        Pos pos2 = f.getLocation().br;
        for (int x = pos1.x; x <= pos2.x; x++) {
            for (int y = pos1.y; y <= pos2.y; y++) {
                boardItems[y][x].setBackground(Constants.Colors.MainBoardItemColor);
                boardItems[y][x].setText("");
            }
        }
    }

    private void initBoard() {
        boardItems = new GuiBoardItem[Constants.Sizes.boardHeight][Constants.Sizes.boardWidth];
        boardPanel.removeAll();
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
        ArrayList<String> stack = utils.getCurrentStack();
        for (int i = 0 ; i < stack.size(); i++) {
            stackModel.addElement(stack.get(i));
        }

        planModel = new DefaultListModel<>();
        planList = new JList<>(planModel);
        ArrayList<String> plan = utils.getCurrentStack();
        for (int i = 0 ; i < plan.size(); i++) {
            planModel.addElement(plan.get(i));
        }
    }
}
