import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import frame.Frame;
import gamePanel.GamePanel;
import genDisplay.GenerationDisplay;
import plane.Plane;
import toolbar.Toolbar;
import toolbarButton.ToolbarButton;

public class Life {
    private static GenerationDisplay genDisplay;
    private static Plane p;
    private static Frame frame;
    private static Timer loopTimer;
    private static HashMap<String, Boolean> keyWait;
    private static boolean autoProgress;
    private static ToolbarButton playPauseButton;
    private static ToolbarButton stepButton;
    private static ToolbarButton clearButton;

    public static void main(String[] args) {
        // Attempt to get a custom size from the cli args
        int size = calcWindowSize(args);

        // A plane will have all the logic to enact the rules of Life
        p = new Plane(size);

        // Create game panel
        GamePanel gamePanel = new GamePanel(size, p);

        // Create action buttons
        playPauseButton = new ToolbarButton("▶ ", "Play", e -> keyWait.put("p", false));
        stepButton = new ToolbarButton("⏭ ", "Step", e -> keyWait.put("space", false));
        clearButton = new ToolbarButton("⏹ ", "Clear", e -> keyWait.put("escape", false));

        // Create generation display box
        genDisplay = new GenerationDisplay();

        // Create toolbar
        Toolbar toolBar = new Toolbar(
                new ToolbarButton[] {
                        playPauseButton,
                        stepButton,
                        clearButton
                }, genDisplay);

        // frame is the main point of interaction for the app
        frame = new Frame("Life", gamePanel, toolBar);

        // Set keybindings to interact with the app
        keyWait = new HashMap<String, Boolean>();
        setupKeyBindings(
                new String[] {
                        "escape", // Clears the grid
                        "space", // Progresses by a single generation / pauses auto
                        "p", // toggle autoprogress
                });
        startEventLoop(50, p);
    }

    /**
     * setupKeyBindinding sets up the key bind that progresses a generation on
     * the Plane
     */
    private static void setupKeyBindings(String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            bindKey(keys[i]);
        }
    }

    /**
     * bindKey binds a key to an action
     */
    private static void bindKey(String key) {
        // Set initial value in keyWait map to wait for keypress
        keyWait.put(key, true);
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(String.format("pressed %s", key.toUpperCase())),
                String.format("%sPressed", key));
        actionMap.put(String.format("%sPressed", key), new AbstractAction() {
            // Signal to the loop we are no longer waiting for this key press
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.printf("key pressed: %s\n", key);
                keyWait.put(key, false);
            }
        });
    }

    private static void pauseAutoProgress() {
        if (autoProgress) {
            autoProgress = false;
            playPauseButton.setText("▶ Play");
        }
    }

    /**
     * startEventLoop checks whether a key was pressed when a period
     * elapses. Any key other than 'p' should pause autoprogress before or
     * instead of performing its action.
     *
     * @param period The period for the loop timer
     */
    private static void startEventLoop(int period, Plane p) {
        loopTimer = new Timer(period, e -> {

            // ----
            // Check for key presses
            // ----

            // Space action: stepwise generation tick
            if (!keyWait.get("space")) {
                if (!autoProgress) {
                    p.evolve();
                    System.out.printf("Progressing to generation %d\n", p.generation);
                }
                pauseAutoProgress();
                keyWait.put("space", true);
            }

            // Escape actions
            if (!keyWait.get("escape")) {
                // If not autoprogressing, clear the plane
                if (!autoProgress) {
                    p.clearPlane();
                    SwingUtilities.invokeLater(() -> {
                        genDisplay.setText("Gen: 0");
                    });
                }
                pauseAutoProgress();
                keyWait.put("escape", true);
            }

            // 'p' action: toggle autoprogress
            if (!keyWait.get("p")) {
                autoProgress = !autoProgress;
                playPauseButton.setText(autoProgress ? "⏸ Pause" : "▶ Play");
                keyWait.put("p", true);
            }

            // ----
            // Evolve on autoprogress
            // ----
            if (autoProgress) {
                System.out.printf("Auto-Progressing to generation %d\n", p.generation);
                p.evolve();
            }

            // Update generation display
            SwingUtilities.invokeLater(() -> {
                genDisplay.setText("Gen: " + p.getGeneration());
            });

            // ----
            // Redraw frame
            // ----
            frame.repaint();
        });
        loopTimer.start();
    }

    /**
     * calcWindowSize calculates the window size based on any command line args,
     * parsing the input, using a default value if the input was invalid or if
     * none was provided
     */
    private static int calcWindowSize(String[] args) {
        int size = 40;
        try {
            size = Integer.parseInt(args[0]);
        }
        // If the arg wasn't a valid integer, keep default
        catch (NumberFormatException e) {
            System.err.printf("Must provide an integer argument for grid size. Defaulting to %d\n", size);

        }
        // If no size was set, keep default
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("No size arg was provided, defaulting to %d\n", size);
        }
        return size;
    }

}
