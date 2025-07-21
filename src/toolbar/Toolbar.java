package toolbar;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JToolBar;

import toolbarButton.ToolbarButton;
import genDisplay.GenerationDisplay;

public class Toolbar extends JToolBar {
    public Toolbar(ToolbarButton[] buttons, GenerationDisplay genDisplay) {
        super();

        // Do not float
        this.setFloatable(false);
        // Create empty border to pad the toolbar
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Add buttons
        for (int i = 0; i < buttons.length; i++) {
            this.addSeparator(new Dimension(50, 0));
            this.add(buttons[i]);
        }

        // Add generation display
        this.add(Box.createHorizontalGlue());
        this.add(genDisplay);
    }
}
