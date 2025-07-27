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
import grid.Grid;
import rleinput.RLEInput;
import toolbar.Toolbar;
import toolbarButton.ToolbarButton;

public class Life {
    private static int APP_WIDTH = 1400;

    private static ScheduledExecutorService evolutionExecutor;
    private static AtomicBoolean autoEvolution = new AtomicBoolean(false);
    private static Timer inputTimer;

    private static GenerationDisplay genDisplay;
    private static Grid grid;
    private static GamePanel gamePanel;
    private static Frame frame;
    private static RLEInput rleInput;
    private static HashMap<String, Boolean> keyWait;

    // Topbar tick controls
    private static ToolbarButton playPauseButton;
    private static ToolbarButton stepButton;
    private static ToolbarButton clearButton;
    private static ToolbarButton quitButton;
    private static ToolbarButton wrapToggle;
    private static ToolbarButton zoomInButton;
    private static ToolbarButton zoomOutButton;
    private static ToolbarButton panUpButton;
    private static ToolbarButton panDownButton;
    private static ToolbarButton panLeftButton;
    private static ToolbarButton panRightButton;
    private static ToolbarButton tick1xButton;
    private static ToolbarButton tick10xButton;
    private static ToolbarButton tick50xButton;
    private static ToolbarButton tick100xButton;
    private static ToolbarButton tick1000xButton;

    /**
     * tickMap determines the tick speed of autoevolution set by a key/button.
     * Tick speed is set in ms, so a value of 1000 == 1 second (i. e. bigger
     * value is a longer interval).
     */
    private static final HashMap<String, Integer> tickMap = new HashMap<String, Integer>() {
        {
            put("1", 1000);
            put("2", 100);
            put("3", 20);
            put("4", 10);
            put("5", 1);
        }
    };

    private static final int KEYPRESS_LISTENER_INTERVAL = 16;

    /**
     * Initial evolution interval, can be updated by tick speed buttons
     */
    private static volatile int evolutionInterval = 100;

    public static void main(String[] args) {
        // Attempt to get a custom grid height from the cli args
        int height = calcGridHeight(args);

        // A grid will have all the logic to enact the rules of Life
        grid = new Grid(height);

        // Create game panel
        gamePanel = new GamePanel(grid);

        // Create toolbar action buttons
        playPauseButton = new ToolbarButton("▶ Play", e -> keyWait.put("p", false));
        stepButton = new ToolbarButton("⏭ Step", e -> keyWait.put("space", false));
        clearButton = new ToolbarButton("⏹ Clear", e -> keyWait.put("escape", false));
        quitButton = new ToolbarButton("✖ Quit", e -> keyWait.put("q", false));
        wrapToggle = new ToolbarButton("Wrap (off)", e -> keyWait.put("w", false));

        zoomOutButton = new ToolbarButton("⊖ ", e -> keyWait.put("page_down", false));
        zoomInButton = new ToolbarButton("⊕ ", e -> keyWait.put("page_up", false));
        panLeftButton = new ToolbarButton("← ", e -> keyWait.put("h", false));
        panDownButton = new ToolbarButton("↓ ", e -> keyWait.put("j", false));
        panUpButton = new ToolbarButton("↑ ", e -> keyWait.put("k", false));
        panRightButton = new ToolbarButton("→ ", e -> keyWait.put("l", false));

        tick1xButton = new ToolbarButton("1x", e -> keyWait.put("1", false));
        tick10xButton = new ToolbarButton("10x", e -> keyWait.put("2", false));
        tick50xButton = new ToolbarButton("50x", e -> keyWait.put("3", false));
        tick100xButton = new ToolbarButton("100x", e -> keyWait.put("4", false));
        tick1000xButton = new ToolbarButton("1000x", e -> keyWait.put("5", false));

        // Create generation display box
        genDisplay = new GenerationDisplay();

        // Create toolbar
        Toolbar toolBar = new Toolbar(
                // playControl toolbar
                new ToolbarButton[] {
                        playPauseButton,
                        stepButton,
                        clearButton,
                        quitButton,
                        wrapToggle,
                },
                // viewport toolbar
                new ToolbarButton[] {
                        zoomInButton,
                        zoomOutButton,
                        panLeftButton,
                        panDownButton,
                        panUpButton,
                        panRightButton,
                },
                // speed toolbar
                new ToolbarButton[] {
                        tick1xButton,
                        tick10xButton,
                        tick50xButton,
                        tick100xButton,
                        tick1000xButton,
                },
                genDisplay);

        // Create RLE input
        rleInput = new RLEInput(grid);

        // frame is the main point of interaction for the app
        frame = new Frame("Java Game of Life", APP_WIDTH, gamePanel, toolBar, rleInput);

        // Set keybindings to interact with the app
        keyWait = new HashMap<String, Boolean>();
        setupKeyBindings(
                new String[] {
                        "escape", // Clears the grid
                        "space", // Evolves cells by a single generation / pauses auto
                        "p", // toggle autoevolution
                        "q", // quit the app
                        "w", // toggle wrap-around (e.g. when glider goes oob)
                        "1", "2", "3", "4", "5", // Set evo tick speed
                        "page_up", // zoom in
                        "page_down", // zoom out
                        "h", // pan left
                        "j", // pan down
                        "k", // pan up
                        "l", // pan right
                });

        // Start both event loops
        startInputLoop();
        startEvolutionLoop();
    }

    /**
     * setupKeyBindinding sets up the key bind that evolves a generation on
     * the Grid
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

    /**
     * toggleAutoEvolution starts or stops the evolution of cells and changes the
     * display of some control buttons to reflect what actions those buttons
     * perform based on the auto-evolution state.
     */
    private static void toggleAutoEvolution() {
        boolean newAutoEvolution = !autoEvolution.get();
        autoEvolution.set(newAutoEvolution);
        SwingUtilities.invokeLater(() -> clearButton.setText(newAutoEvolution ? "⏹ Stop" : "⏹ Clear"));
        SwingUtilities.invokeLater(() -> playPauseButton.setText(newAutoEvolution ? "⏸ Pause" : "▶ Play"));
    }

    /**
     * startInputLoop checks whether a key was pressed when a period
     * elapses. Any key other than 'p' should pause autoevolution before or
     * instead of performing its action.
     */
    private static void startInputLoop() {
        inputTimer = new Timer(KEYPRESS_LISTENER_INTERVAL, e -> {

            // If the rle input text box is focused, reset all key waits and
            // do not perform any actions
            if (rleInput.isFocusOwner()) {
                keyWait.forEach((k, _v) -> {
                    keyWait.put(k, true);
                });
                return;
            }

            // ----
            // Check for key presses
            // ----

            // 'q' action: quit the app
            else if (!keyWait.get("q")) {
                System.out.println("Closing program");
                cleanup();
                System.exit(0);
            }

            else if (!keyWait.get("w")) {
                boolean newWrap = !grid.isWrap();
                grid.setWrap(newWrap);
                System.out.printf("Toggle wrap to: %s\n", newWrap);
                keyWait.put("w", true);
                wrapToggle.setText(newWrap ? "Wrap (on)" : "Wrap (off)");
            }

            // Space action: stepwise generation tick
            else if (!keyWait.get("space")) {
                if (!autoEvolution.get()) {
                    evolveAndUpdate();
                    System.out.printf("Evolving to generation %d\n", grid.getGeneration());
                } else {
                    // Pause autoevolution if on
                    toggleAutoEvolution();
                }
                keyWait.put("space", true);
            }

            // Escape actions
            else if (!keyWait.get("escape")) {
                if (!autoEvolution.get()) {
                    grid.clearGrid();
                    SwingUtilities.invokeLater(() -> {
                        genDisplay.setText("Gen: 0");
                    });
                } else {
                    // Pause autoevolution if on
                    toggleAutoEvolution();
                }

                keyWait.put("escape", true);
            }

            // 'page_up' action: zoom in
            else if (!keyWait.get("page_up")) {
                gamePanel.zoomIn();
                keyWait.put("page_up", true);
            }

            // 'page_down' action: zoom in
            else if (!keyWait.get("page_down")) {
                gamePanel.zoomOut();
                keyWait.put("page_down", true);
            }

            // 'k' action: pan up
            else if (!keyWait.get("k")) {
                gamePanel.pan("up");
                keyWait.put("k", true);
            }

            // 'j' action: pan down
            else if (!keyWait.get("j")) {
                gamePanel.pan("down");
                keyWait.put("j", true);
            }

            // 'h' action: pan left
            else if (!keyWait.get("h")) {
                gamePanel.pan("left");
                keyWait.put("h", true);
            }

            // 'l' action: pan right
            else if (!keyWait.get("l")) {
                gamePanel.pan("right");
                keyWait.put("l", true);
            }

            // Tick speed keys
            else if (!keyWait.get("1")) {
                updateTickSpeed("1");
            } else if (!keyWait.get("2")) {
                updateTickSpeed("2");
            } else if (!keyWait.get("3")) {
                updateTickSpeed("3");
            } else if (!keyWait.get("4")) {
                updateTickSpeed("4");
            } else if (!keyWait.get("5")) {
                updateTickSpeed("5");
            }

            // 'p' action: toggle autoevolution
            else if (!keyWait.get("p")) {
                toggleAutoEvolution();
                keyWait.put("p", true);
            }
        });
        inputTimer.start();
    }

    private static void updateTickSpeed(String key) {
        evolutionInterval = (int) tickMap.get(key);

        System.out.printf("Setting tick speed to %dms\n", evolutionInterval);

        if (evolutionExecutor != null && !evolutionExecutor.isShutdown()) {
            restartEvolutionLoop();
        }
        keyWait.put(key, true);
    }

    /**
     * startEvolutionLoop evolves the cells on the grid at a given interval
     */
    private static void startEvolutionLoop() {
        evolutionExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Evolution-Thread");
            t.setDaemon(true);
            return t;
        });

        evolutionExecutor.scheduleAtFixedRate(() -> {
            if (autoEvolution.get()) {
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
        grid.evolve();

        SwingUtilities.invokeLater(() -> {
            genDisplay.setText("Gen: " + grid.getGeneration());
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
     * calcGridHeight gets the height of the game grid from user input if
     * there is any, otherwise returns the default height. e.g. a height of 40
     * would be a 40x40 grid.
     *
     * @param args
     * @return The height of the side of a grid
     */
    private static int calcGridHeight(String[] args) {
        int height = 100;
        try {
            height = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.printf("Must provide an integer argument for grid height. Defaulting to %d\n", height);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("No height arg was provided, defaulting to %d\n", height);
        }
        return height;
    }
}
