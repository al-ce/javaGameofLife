import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import plane.Plane;
import cell.Cell;

public class Main {
    private static Plane p;
    private static JFrame frame;
    private static Timer loopTimer;
    private static HashMap<String, Boolean> keyWait;

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

        bindKey("escape");
        bindKey("space");

    }

    /**
     * bindKey binds a key to an action
     */
    private static void bindKey(String key) {

        keyWait.put(key, true);

        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(String.format("pressed %s", key.toUpperCase())),
                String.format("%sPressed", key));
        actionMap.put(String.format("%sPressed", key), new AbstractAction() {

            // Reset the waiting state for this key
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
            if (!keyWait.get("space")) {
                p.Evolve();
                System.out.printf("Progressing to generation %d\n", p.generation);
                keyWait.put("space", true);
            }
            if (!keyWait.get("escape")) {
                p.ClearPlane();
                keyWait.put("escape", true);
            }

            // Redraw/repaint
            frame.repaint();
        });
        loopTimer.start();
    }

}
