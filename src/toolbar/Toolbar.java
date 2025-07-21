package toolbar;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import toolbarButton.ToolbarButton;
import genDisplay.GenerationDisplay;

public class Toolbar extends JPanel {
    private JToolBar topToolbar;
    private JToolBar bottomToolbar;

    public Toolbar(ToolbarButton[] topButtons, ToolbarButton[] bottomButtons, GenerationDisplay genDisplay) {
        super(new BorderLayout());

        // Create the top toolbar
        topToolbar = createToolbar(topButtons);

        // Create the bottom toolbar
        bottomToolbar = createToolbar(bottomButtons);

        bottomToolbar.add(Box.createHorizontalGlue());
        bottomToolbar.add(genDisplay);

        // Add toolbars to the panel
        this.add(topToolbar, BorderLayout.NORTH);
        this.add(bottomToolbar, BorderLayout.CENTER);
    }

    private JToolBar createToolbar(ToolbarButton[] buttons) {
        JToolBar toolbar = new JToolBar();

        // Do not float
        toolbar.setFloatable(false);

        // Create empty border to pad the toolbar
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Add buttons
        for (int i = 0; i < buttons.length; i++) {
            toolbar.addSeparator(new Dimension(50, 0));
            toolbar.add(buttons[i]);
        }

        return toolbar;
    }

    public JToolBar getTopToolbar() {
        return topToolbar;
    }

    public JToolBar getBottomToolbar() {
        return bottomToolbar;
    }
}
