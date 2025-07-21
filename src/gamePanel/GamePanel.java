package gamePanel;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import cell.Cell;
import plane.Plane;

public class GamePanel extends JPanel {
    public GamePanel(int size, Plane p) {
        super();

        this.setLayout(new GridLayout(size, size, 1, 1));
        this.setBackground(Color.GRAY);

        // Add all the cells to the game panel
        for (int y = 0; y < p.height; y++) {
            for (int x = 0; x < p.width; x++) {
                Cell c = p.cells[y][x];
                this.add(c);
            }
        }
    }
}
