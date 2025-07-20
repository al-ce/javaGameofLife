import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import cell.Cell;
import plane.Plane;

public class Life {
    private static Plane p;
    private static JFrame frame;
    private static Timer loopTimer;
    private static HashMap<String, Boolean> keyWait;
    private static boolean autoProgress;

    public static void main(String[] args) {
        // Attempt to get a custom size from the cli args
        int size;
        try {
            size = Integer.parseInt(args[0]);
        }
        // If the arg wasn't a valid integer, exit
        catch (NumberFormatException e) {
            System.err.println("Must provide an integer argument for grid size");
            return;

        }
        // If no size was set, default to 40
        catch (ArrayIndexOutOfBoundsException e) {
            size = 40;
        }

        // A plane will have all the logic to enact the rules of Life
        p = new Plane(size);

        // frame is the main point of interaction for the app
        frame = new JFrame("Life");

        // Set gaps of 1px. Since the background is set to GRAY, this will give
        // the illusion of a border around each Cell
        frame.setLayout(new GridLayout(size, size, 1, 1));
        frame.getContentPane().setBackground(Color.GRAY);

        // Add all the cells to the frame
        for (int y = 0; y < p.height; y++) {
            for (int x = 0; x < p.width; x++) {
                Cell c = p.cells[y][x];
                frame.add(c);
            }
        }

        // Set keybindings to interact with the app
        keyWait = new HashMap<String, Boolean>();
        setupKeyBindings();
        setupKeypressLoop(50, p);

        // Finalize the app config
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * setupKeyBindinding sets up the key bind that progresses a generation on
     * the Plane
     */
    private static void setupKeyBindings() {
        bindKey("escape"); // Clears the grid
        bindKey("space"); // Progresses by a single generation / pauses auto
        bindKey("p"); // toggle autoprogress
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
     * setupKeypressLoop checks whether a key was pressed when a period
     * elapses
     *
     * @param period The period for the loop timer
     */
    private static void setupKeypressLoop(int period, Plane p) {
        loopTimer = new Timer(period, e -> {
            // Space actions
            if (!keyWait.get("space")) {
                // Turn space key into a secondary pause button if we are auto
                // progressing
                if (autoProgress) {
                    autoProgress = false;
                }
                // Else, use it as as a stepwise (tick) generation progressor
                else {

                    p.evolve();
                    System.out.printf("Progressing to generation %d\n", p.generation);
                    keyWait.put("space", true);
                }
            }
            // Escape actions
            if (!keyWait.get("escape")) {
                p.clearPlane();
                keyWait.put("escape", true);
            }
            // 'p' actions
            if (!keyWait.get("p")) {
                autoProgress = !autoProgress;
                keyWait.put("p", true);
            }

            // Evovle on autoprogress
            if (autoProgress) {
                System.out.printf("Auto-Progressing to generation %d\n", p.generation);
                p.evolve();
            }

            // Redraw/repaint
            frame.repaint();
        });
        loopTimer.start();
    }

}
