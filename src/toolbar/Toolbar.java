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
    private JToolBar playControlToolbar;
    private JToolBar viewportToolbar;
    private JToolBar speedToolbar;

    public Toolbar(
            ToolbarButton[] playControlButtons,
            ToolbarButton[] viewportButtons,
            ToolbarButton[] speedButtons,
            GenerationDisplay genDisplay) {
        super(new BorderLayout());

        // Create the playControl toolbar
        playControlToolbar = createToolbar(playControlButtons);

        // Create the viewport toolbar
        viewportToolbar = createToolbar(viewportButtons);

        // Create the speed toolbar
        speedToolbar = createToolbar(speedButtons);

        speedToolbar.add(Box.createHorizontalGlue());
        speedToolbar.add(genDisplay);

        // Add toolbars to the panel
        this.add(playControlToolbar, BorderLayout.NORTH);
        this.add(viewportToolbar, BorderLayout.CENTER);
        this.add(speedToolbar, BorderLayout.SOUTH);
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

    public JToolBar getPlayControlToolbar() {
        return playControlToolbar;
    }

    public JToolBar getSpeedToolbar() {
        return speedToolbar;
    }
}
