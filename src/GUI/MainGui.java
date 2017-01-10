package GUI;

import Constants.Constants;
import Logic.Furniture;
import Logic.FurnitureLocation;
import Logic.Pos;

import javax.swing.*;
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
    private int currentFurniture;

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
