package cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Cell extends JButton {
    private static final Color ALIVE_COLOR = Color.BLACK;
    private static final Color DEAD_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.GRAY;

    private boolean isAlive = false;

    public Cell() {
        super();
        initialize();
    }

    private void initialize() {
        // Set default appearance (dead)
        setBackground(DEAD_COLOR);
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
    }

    private void toggleState() {
        isAlive = !isAlive;
        setBackground(isAlive ? ALIVE_COLOR : DEAD_COLOR);
        repaint();
    }

    /**
    * isAlive gets the living state of the cell
    * @return The boolean indicating the living state of the cell
    */
    public boolean isAlive() {
        return isAlive;
    }

    /**
    * setAlive sets the living state of the cell
    * @param alive The value of the living state to be set
    */
    public void setAlive(boolean alive) {
        this.isAlive = alive;
        setBackground(isAlive ? ALIVE_COLOR : DEAD_COLOR);
        repaint();
    }
}
