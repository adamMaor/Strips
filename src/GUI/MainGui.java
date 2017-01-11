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
    private int currentFurniture;
    private FurnitureLocation tempFurnitureLocation;

    private GuiUtils utils;

    private GuiBoardItem[][] boardItems;

    public MainGui(final GuiUtils utils) {
        this.utils = utils;
        currentFurniture = 1;
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

        initBoard();
        setNavigationButtonsEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fId = (String)furnitureComboBox.getSelectedItem();
                int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete " + fId + " ?","Are You Sure?",JOptionPane.YES_NO_OPTION);
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
                setNavigationButtonsEnabled(index != -1);
            }
        });
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
            MatteBorder upBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    color);
            boardItems[y][x].setBorder(upBorder);
            return;
        }

        if (width == 0) {
            MatteBorder upBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    0,
                    Constants.Sizes.ThickBorderWidth,
                    color);
            MatteBorder upBorder2 = BorderFactory.createMatteBorder(0,
                    0,
                    Constants.Sizes.normalBorderWidth,
                    0,
                    Constants.Colors.StandardBorderColor);
            boardItems[y++][x].setBorder(new CompoundBorder(upBorder, upBorder2));
            MatteBorder middleBorder = BorderFactory.createMatteBorder(0,
                    Constants.Sizes.ThickBorderWidth,
                    0,
                    Constants.Sizes.ThickBorderWidth,
                    color);
            MatteBorder middleBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                    0,
                    Constants.Sizes.normalBorderWidth,
                    0,
                    Constants.Colors.StandardBorderColor);
            while (y < furnitureLocation.br.y) {
                boardItems[y++][x].setBorder(new CompoundBorder(middleBorder, middleBorder2));
            }
            MatteBorder downBorder = BorderFactory.createMatteBorder(0,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    color);
            MatteBorder downBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                    0,
                    0,
                    0,
                    Constants.Colors.StandardBorderColor);
            boardItems[y][x].setBorder(new CompoundBorder(downBorder, downBorder2));
            return;
        }

        if (height == 0) {
            MatteBorder leftBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    0,
                    color);
            MatteBorder leftBorder2 = BorderFactory.createMatteBorder(0,0,0,
                    Constants.Sizes.normalBorderWidth,
                    Constants.Colors.StandardBorderColor);
            boardItems[y][x++].setBorder(new CompoundBorder(leftBorder, leftBorder2));
            MatteBorder middleBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                    0, Constants.Sizes.ThickBorderWidth, 0, color);
            MatteBorder middleBorder2 = BorderFactory.createMatteBorder(0, Constants.Sizes.normalBorderWidth,
                    0, Constants.Sizes.normalBorderWidth,
                    Constants.Colors.StandardBorderColor);
            while (x < furnitureLocation.br.x) {
                boardItems[y][x++].setBorder(new CompoundBorder(middleBorder, middleBorder2));
            }
            MatteBorder rightBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                    0,
                    Constants.Sizes.ThickBorderWidth,
                    Constants.Sizes.ThickBorderWidth,
                    color);
            MatteBorder rightBorder2 = BorderFactory.createMatteBorder(0,
                    Constants.Sizes.normalBorderWidth,
                    0,
                    0,
                    Constants.Colors.StandardBorderColor);
            boardItems[y][x].setBorder(new CompoundBorder(rightBorder,rightBorder2));
            return;
        }


        //uperLeft Corner Border
        MatteBorder upLeftBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                Constants.Sizes.ThickBorderWidth,
                0,
                0,
                color);
        MatteBorder upLeftBorder2 = BorderFactory.createMatteBorder(0,
                0,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                Constants.Colors.StandardBorderColor);
        boardItems[y][x++].setBorder(new CompoundBorder(upLeftBorder, upLeftBorder2));
        // first set upper border
        MatteBorder upBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                0,
                0,
                0,
                color);
        MatteBorder upBorder2 = BorderFactory.createMatteBorder(0,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                Constants.Colors.StandardBorderColor);
        while (x < furnitureLocation.br.x) {
            boardItems[y][x++].setBorder(new CompoundBorder(upBorder, upBorder2));

        }
        //uper rightt Corner Border
        MatteBorder upRightBorder = BorderFactory.createMatteBorder(Constants.Sizes.ThickBorderWidth,
                0,
                0,
                Constants.Sizes.ThickBorderWidth,
                color);
        MatteBorder upRightBorder2 = BorderFactory.createMatteBorder(0,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                0,
                Constants.Colors.StandardBorderColor);
        boardItems[y++][x].setBorder(new CompoundBorder(upRightBorder, upRightBorder2));
        // now set right border
        MatteBorder rightBorder = BorderFactory.createMatteBorder(0,
                0,
                0,
                Constants.Sizes.ThickBorderWidth,
                color);
        MatteBorder rightBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                0,
                Constants.Colors.StandardBorderColor);
        while(y < furnitureLocation.br.y) {
            boardItems[y++][x].setBorder(new CompoundBorder(rightBorder, rightBorder2));
        }

        //downright Corner Border
        MatteBorder downRightBorder = BorderFactory.createMatteBorder(0,
                0,
                Constants.Sizes.ThickBorderWidth,
                Constants.Sizes.ThickBorderWidth,
                color);
        MatteBorder downRightBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                0,0,
                Constants.Colors.StandardBorderColor);
        boardItems[y][x--].setBorder(new CompoundBorder(downRightBorder, downRightBorder2));
        // downborder
        MatteBorder downBorder = BorderFactory.createMatteBorder(0,
                0,
                Constants.Sizes.ThickBorderWidth,
                0,
                color);
        MatteBorder downBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                0,
                Constants.Sizes.normalBorderWidth,
                Constants.Colors.StandardBorderColor);
        while (x > furnitureLocation.tl.x) {
            boardItems[y][x--].setBorder(new CompoundBorder(downBorder, downBorder2));
        }
        //downright Corner Border
        MatteBorder downLeftBorder = BorderFactory.createMatteBorder(0,
                Constants.Sizes.ThickBorderWidth,
                Constants.Sizes.ThickBorderWidth,
                0,
                color);
        MatteBorder downLeftBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                0,0,
                Constants.Sizes.normalBorderWidth,
                Constants.Colors.StandardBorderColor);
        boardItems[y--][x].setBorder(new CompoundBorder(downLeftBorder, downLeftBorder2));
        MatteBorder leftBorder = BorderFactory.createMatteBorder(0,
                Constants.Sizes.ThickBorderWidth,
                0,
                0,
                color);
        MatteBorder leftBorder2 = BorderFactory.createMatteBorder(Constants.Sizes.normalBorderWidth,
                0,
                Constants.Sizes.normalBorderWidth,
                Constants.Sizes.normalBorderWidth,
                Constants.Colors.StandardBorderColor);
        while(y > furnitureLocation.tl.y) {
            boardItems[y--][x].setBorder(new CompoundBorder(leftBorder, leftBorder2));
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
        for (int y = 0; y < Constants.Sizes.boardHeight; y++) {
            for (int x = 0; x < Constants.Sizes.boardWidth; x++) {
                GuiBoardItem guiItem = new GuiBoardItem(Constants.Colors.MainBoardItemColor, utils.getBoarderForPos(x,y));
                boardItems[y][x] = guiItem;
                boardPanel.add(guiItem);
            }
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        boardPanel = new JPanel(new GridLayout(Constants.Sizes.boardHeight,Constants.Sizes.boardWidth));
    }
}
