package rleinput;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * RLEInput is a text input box that accepts Run Length Encoded format text and
 * places the corresponding patterns on the Plane
 */

public class RLEInput extends JScrollPane {
    private JTextArea textArea;

    public RLEInput(int width, int height) {
        // Create the text area for RLE patterns
        textArea = new JTextArea(height, 0);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Configure
        setViewportView(textArea);
        setPreferredSize(new Dimension(0, height));
        setMinimumSize(new Dimension(0, height));
        setMaximumSize(new Dimension(0, height));
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    }

    // Make text area methods accessible as class methods

    public JTextArea getTextArea() {
        return textArea;
    }

    public boolean isFocusOwner() {
        return textArea.isFocusOwner();
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
    }
}
