package toolbarButton;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ToolbarButton extends JButton {

    /**
    * @param symbol The symbol to display left to the button text
    * @param text The text to display on the button
    * @param listener The listener to add to the button
    */
    public ToolbarButton(String symbol, String text, ActionListener listener) {
        super(String.format("%s %s", symbol, text));

        java.awt.Font buttonFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16);
        java.awt.Dimension buttonSize = new java.awt.Dimension(120, 50);

        this.setFont(buttonFont);
        this.setPreferredSize(buttonSize);
        this.addActionListener(listener);

    }
}
