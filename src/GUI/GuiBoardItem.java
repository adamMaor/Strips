package GUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * Created by Adam on 09/01/2017.
 */
public class GuiBoardItem extends JPanel {

    private Color color;
    private JLabel label;

    public GuiBoardItem(Color color, CompoundBorder border) {
        this.setLayout(new GridBagLayout());
        label = new JLabel("");
        this.add(label);
        this.setPreferredSize(new Dimension(50, 50));
        this.color = color;
        this.setForeground(color);
        this.setBackground(color);
        this.setBorder(border);
    }

    public void setText(String text) {
        label.setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
