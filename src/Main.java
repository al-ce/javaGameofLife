import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import plane.Plane;
import cell.Cell;

public class Main {
    private static Plane p;
    private static JFrame frame;
    private static boolean waitingForKey = true;
    private static Timer loopTimer;

    public static void main(String[] args) {
        int size;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Must provide an integer argument for grid size");
            return;
        }

        p = new Plane(size);
        frame = new JFrame("Life");

        frame.setLayout(new GridLayout(size, size, 1, 1));
        frame.getContentPane().setBackground(Color.GRAY);

        for (int y = 0; y < p.height; y++) {
            for (int x = 0; x < p.width; x++) {
                Cell c = p.cells[y][x];
                frame.add(c);
            }
        }

        setupKeyBinding();

        setupGenerationLoop(50, p);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    /**
     * setupKeyBindinding sets up the key bind that progresses a generation on
     * the Plane
     */
    private static void setupKeyBinding() {
        // Get the root pane's input and action maps
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        // Bind space to action on spacebar press
        inputMap.put(KeyStroke.getKeyStroke("pressed SPACE"), "spacePressed");
        actionMap.put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (waitingForKey) {
                    waitingForKey = false;
                }
            }
        });

    }

    /**
     * setupGenerationLoop checks whether a key was pressed when a period
     * elapses
     *
     * @param period The period for the loop timer
     */
    private static void setupGenerationLoop(int period, Plane p) {
        loopTimer = new Timer(period, e -> {
            if (!waitingForKey) {
                performLoopIteration(p);
                waitingForKey = true;
            }
        });
        loopTimer.start();
    }

    private static void performLoopIteration(Plane p) {

        p.Evolve();
        System.out.printf("Progressing to generation %d\n", p.generation);
        frame.repaint();
    }
}
