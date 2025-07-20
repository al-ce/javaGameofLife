package cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Cell extends JButton {
    private static final Color ALIVE_COLOR = Color.BLACK;
    private static final Color BORDER_COLOR = Color.GRAY;

    /**
    * deadColor is the color to set on a dead cell. Initially white, but if a
    * cell was once alive, the color should be set based on how long ago the
    * most recent generation was. See Johnson and Green p. xii Figure 1
    **/
    private  Color deadColor = Color.WHITE;

    /**
     * mrg indicates the most recent generation this cell was living. The
     * initial value of -1 indicates this cell has never been alive.
     */
    private int mrg = -1;

    private boolean isAlive = false;

    public Cell() {
        super();
        initialize();
    }

    private void initialize() {

        // Set default appearance (dead)
        setBackground(deadColor);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true); // Ensure background is painted

        // Remove margins (no space between cells)
        setMargin(new Insets(0, 0, 0, 0));

        // Add click handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleState();
            }
        });

        // Remove default spacebar action from all cells
        InputMap focusMap = this.getInputMap(JComponent.WHEN_FOCUSED);
        focusMap.put(KeyStroke.getKeyStroke("SPACE"), "none");

    }

    private void toggleState() {
        this.isAlive = !this.isAlive;
        setBackground(isAlive ? ALIVE_COLOR : deadColor);
        repaint();
    }

    /**
     * isAlive gets the living state of the cell
     *
     * @return The boolean indicating the living state of the cell
     */
    public boolean isAlive() {
        return this.isAlive;
    }

    /**
     * setAlive sets the living state of the cell
     *
     * @param alive The value of the living state to be set
     */
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
        setBackground(isAlive ? ALIVE_COLOR : deadColor);
        repaint();
    }

    /**
     * getMrg returns the value for mrg, indicating the most recent
     * generation this Cell was living
     */
    public int getMrg() {
        return this.mrg;
    }

    /**
     * setMrg sets a value for mrg, indicating the most recent
     * generation this Cell was living
     */
    public void setMrg(int mrg) {
        this.mrg = mrg;
    }
}
