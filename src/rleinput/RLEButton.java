package rleinput;

import java.awt.Font;

import javax.swing.JButton;

class RLEButton extends JButton {
    public RLEButton(String text) {
        super(text);

        setFont(new Font("SansSerif", Font.BOLD, 10));
        setFocusPainted(true);
        setBorderPainted(true);
        setContentAreaFilled(false);
        setOpaque(false);
    }
}
