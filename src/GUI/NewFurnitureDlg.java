package GUI;

import Logic.FurnitureLocation;
import Logic.Pos;

import javax.swing.*;
import java.awt.event.*;

public class NewFurnitureDlg extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel IDLabel;
    private JSpinner TopLeftXSpinner;
    private JSpinner TopLeftYSpinner;
    private JSpinner WidthSpinner;
    private JSpinner HeightSpinner;
    private GuiUtils utils;


    private FurnitureLocation fLocation;


    public NewFurnitureDlg(GuiUtils utils, String fId) {
        this.utils = utils;
        IDLabel.setText("Adding Furniture Item: " + fId);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        SpinnerModel TLXModel = new SpinnerNumberModel(0, 0, 18, 1);
        TopLeftXSpinner.setModel(TLXModel);
        ((JSpinner.DefaultEditor)TopLeftXSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

        SpinnerModel TLYModel = new SpinnerNumberModel(0, 0, 10, 1);
        TopLeftYSpinner.setModel(TLYModel);
        ((JSpinner.DefaultEditor)TopLeftYSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

        SpinnerModel WidthModel = new SpinnerNumberModel(1, 1, 20, 1);
        WidthSpinner.setModel(WidthModel);
        ((JSpinner.DefaultEditor)WidthSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

        SpinnerModel HeightModel = new SpinnerNumberModel(1, 1, 12, 1);
        HeightSpinner.setModel(HeightModel);
        ((JSpinner.DefaultEditor)HeightSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);



        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        int tlx = (Integer)TopLeftXSpinner.getValue();
        int tly = (Integer)TopLeftYSpinner.getValue();

        Pos pos1 = new Pos(tlx,tly);

        int width = (Integer)WidthSpinner.getValue();
        int height = (Integer)HeightSpinner.getValue();

        int brx = tlx + width - 1;
        int bry = tly + height - 1;
        Pos pos2 = new Pos(brx,bry);

        fLocation = new FurnitureLocation(pos1, pos2);
        if (utils.checkForNewFurnitureSpace(fLocation) == false) {
            System.out.println("Can't Set Furniture There");
            fLocation = null;
            return;
        }
        dispose();
    }

    private void onCancel() {
        fLocation = null;
        dispose();
    }


    public FurnitureLocation getfLocation() {
        return fLocation;
    }

}
