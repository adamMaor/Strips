import Constants.Globals;
import GUI.GuiUtils;
import GUI.MainGui;
import Logic.LogicUtils;
import Strips.StripsLogic;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Globals globals = new Globals();
        LogicUtils logicUtils = new LogicUtils(globals);
        StripsLogic stripsLogic = new StripsLogic(logicUtils);
        GuiUtils guiUtils = new GuiUtils(globals, logicUtils, stripsLogic);
        MainGui mainGui = new MainGui(guiUtils);
        JFrame mainFrame = new JFrame();
        mainFrame.setContentPane(mainGui.mainPanel);
        mainFrame.setSize(new Dimension(1270, 675));
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setTitle("STRIPS");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
