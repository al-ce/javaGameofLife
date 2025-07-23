package toolbarButton;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ToolbarButton extends JButton {

    /**
     * @param symbol   The symbol to display left to the button text
     * @param text     The text to display on the button
     * @param listener The listener to add to the button
     */
    public ToolbarButton(String text, ActionListener listener) {
        super(String.format("%s", text));

        this.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.setPreferredSize(new Dimension(150, 50));
        this.addActionListener(listener);

    }
}
