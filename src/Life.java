import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static Timer inputTimer;
    private static ScheduledExecutorService evolutionExecutor;
    private static HashMap<String, Boolean> keyWait;
    private static AtomicBoolean autoProgress = new AtomicBoolean(false);

    // Topbar tick controls
    private static ToolbarButton playPauseButton;
    private static ToolbarButton stepButton;
    private static ToolbarButton clearButton;
    private static ToolbarButton quitButton;

    private static final HashMap<String, Integer> tickMap = new HashMap<String, Integer>() {
        {
            put("1", 1000);
            put("2", 500);
            put("3", 100);
            put("4", 10);
            put("5", 1);
        }
    };

    private static final int KEYPRESS_LISTENER_INTERVAL = 16;

    // Initial evolution interval, can be updated by tick speed buttons
    private static volatile int evolutionInterval = 50;

    public static void main(String[] args) {
        // Attempt to get a custom size from the cli args
        int size = calcWindowSize(args);

        // A plane will have all the logic to enact the rules of Life
        p = new Plane(size);

        // Create game panel
        GamePanel gamePanel = new GamePanel(size, p);

        // Create top toolbar action buttons
        playPauseButton = new ToolbarButton("▶ Play", e -> keyWait.put("p", false));
        stepButton = new ToolbarButton("⏭ Step", e -> keyWait.put("space", false));
        clearButton = new ToolbarButton("⏹ Clear", e -> keyWait.put("escape", false));
        quitButton = new ToolbarButton("✖ Quit", e -> keyWait.put("q", false));

        // Create bottom toolbar action buttons

        // Create generation display box
        genDisplay = new GenerationDisplay();

        // Create toolbar
        Toolbar toolBar = new Toolbar(
                new ToolbarButton[] {
                        playPauseButton,
                        stepButton,
                        clearButton,
                        quitButton,
                },
                new ToolbarButton[] {
                        new ToolbarButton("1x", e -> keyWait.put("1", false)),
                        new ToolbarButton("2x", e -> keyWait.put("2", false)),
                        new ToolbarButton("10x", e -> keyWait.put("3", false)),
                        new ToolbarButton("100x", e -> keyWait.put("4", false)),
                        new ToolbarButton("1000x", e -> keyWait.put("5", false)),
                },
                genDisplay);

        // frame is the main point of interaction for the app
        frame = new Frame("Life", gamePanel, toolBar);

        // Set keybindings to interact with the app
        keyWait = new HashMap<String, Boolean>();
        setupKeyBindings(
                new String[] {
                        "escape", // Clears the grid
                        "space", // Progresses by a single generation / pauses auto
                        "p", // toggle autoprogress
                        "q", // quit the app
                        "1", "2", "3", "4", "5", // Set evo tick speed
                });

        // Start both event loops
        startInputLoop();
        startEvolutionLoop();
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

    private static void toggleAutoProgress() {
        boolean newAutoProgress = !autoProgress.get();
        autoProgress.set(newAutoProgress);
        SwingUtilities.invokeLater(() -> clearButton.setText(newAutoProgress ? "⏹ Stop" : "⏹ Clear"));
        SwingUtilities.invokeLater(() -> playPauseButton.setText(newAutoProgress ? "⏸ Pause" : "▶ Play"));
    }

    /**
     * startInputLoop checks whether a key was pressed when a period
     * elapses. Any key other than 'p' should pause autoprogress before or
     * instead of performing its action.
     */
    private static void startInputLoop() {
        inputTimer = new Timer(KEYPRESS_LISTENER_INTERVAL, e -> {

            // ----
            // Check for key presses
            // ----

            // 'q' action: quit the app
            if (!keyWait.get("q")) {
                System.out.println("Closing program");
                cleanup();
                System.exit(0);
            }

            // Space action: stepwise generation tick
            if (!keyWait.get("space")) {
                if (!autoProgress.get()) {
                    evolveAndUpdate();
                    System.out.printf("Progressing to generation %d\n", p.generation);
                } else {
                    // Pause autoprogress if on
                    toggleAutoProgress();
                }
                keyWait.put("space", true);
            }

            // Escape actions
            if (!keyWait.get("escape")) {
                if (!autoProgress.get()) {
                    p.clearPlane();
                    SwingUtilities.invokeLater(() -> {
                        genDisplay.setText("Gen: 0");
                    });
                } else {
                    // Pause autoprogress if on
                    toggleAutoProgress();
                }

                keyWait.put("escape", true);
            }

            // Tick speed keys
            for (int i = 0; i < 5; i++) {
                String key = String.format("%d", i + 1);
                if (!keyWait.get(key)) {
                    evolutionInterval = (int) tickMap.get(key);

                    System.out.printf("Setting tick speed to %dms\n", evolutionInterval);

                    if (evolutionExecutor != null && !evolutionExecutor.isShutdown()) {
                        restartEvolutionLoop();
                    }
                    keyWait.put(key, true);
                    break;
                }
            }
            // 'p' action: toggle autoprogress
            if (!keyWait.get("p")) {
                toggleAutoProgress();
                keyWait.put("p", true);
            }

            frame.repaint();
        });
        inputTimer.start();
    }

    /**
     * startEvolutionLoop evolves the cells on the plane at a given interval
     */
    private static void startEvolutionLoop() {
        evolutionExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Evolution-Thread");
            t.setDaemon(true);
            return t;
        });

        evolutionExecutor.scheduleAtFixedRate(() -> {
            if (autoProgress.get()) {
                evolveAndUpdate();
            }
        }, evolutionInterval, evolutionInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * restartEvolutionLoop stops and starts the evolution executor. Used to
     * update the executor with a new interval set by any UI interface
     */
    private static void restartEvolutionLoop() {
        if (evolutionExecutor != null && !evolutionExecutor.isShutdown()) {
            evolutionExecutor.shutdownNow();
        }
        startEvolutionLoop();
    }

    /**
     * evolveAndUpdate evolves all the cell objects and repaints the frame
     */
    private static void evolveAndUpdate() {
        p.evolve();

        SwingUtilities.invokeLater(() -> {
            genDisplay.setText("Gen: " + p.getGeneration());
            frame.repaint();
        });
    }

    /**
     * cleanup cleanly stops the input timer and shuts down the evolution
     * executor
     */
    private static void cleanup() {
        if (inputTimer != null) {
            inputTimer.stop();
        }
        if (evolutionExecutor != null) {
            evolutionExecutor.shutdownNow();
        }
    }

    /**
     * calcGridSize gets the size of the game grid from user input if there is
     * any, otherwise returns the default size.
     * 
     * @param args
     * @return The size of the grid
     */
    private static int calcWindowSize(String[] args) {
        int size = 40;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.printf("Must provide an integer argument for grid size. Defaulting to %d\n", size);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("No size arg was provided, defaulting to %d\n", size);
        }
        return size;
    }
}
