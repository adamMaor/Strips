import Constants.Globals;
import GUI.GuiUtils;
import GUI.MainGui;
import Logic.LogicUtils;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Globals globals = new Globals();
        LogicUtils logicUtils = new LogicUtils(globals);
        GuiUtils guiUtils = new GuiUtils(globals, logicUtils);
        MainGui mainGui = new MainGui(guiUtils);
        JFrame mainFrame = new JFrame();
        mainFrame.setContentPane(mainGui.mainPanel);
        mainFrame.setSize(new Dimension(1270, 650));
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
