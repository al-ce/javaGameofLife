package gamePanel;

import java.awt.GridLayout;

import javax.swing.JPanel;

import cell.Cell;
import plane.Plane;

public class GamePanel extends JPanel {
    private Plane plane;

    // viewport sets how many cells are visible in the game UI out of the
    // actual cells. This is used to implement a viewport-zoom
    public int viewportX = 0;
    public int viewportY = 0;
    public int viewportWidth;
    public int viewportHeight;

    public GamePanel(Plane p) {
        this.plane = p;
        // Set initial viewport width to half of plane width rounded to 10
        this.viewportHeight = (this.plane.getHeight() / 2 / 10) * 10;
        this.viewportWidth = (this.plane.getWidth() / 2 / 10) * 10;
        updateViewport();
    }

    public void setViewportHeight(int viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public int getViewportWidth() {
        return this.viewportWidth;
    }

    public void setViewportWidth(int viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public void updateViewport() {
        this.removeAll();
        this.setLayout(new GridLayout(viewportHeight, viewportWidth, 1, 1));

        Cell[][] viewportCells = getViewportCells();
        for (int y = 0; y < viewportHeight; y++) {
            for (int x = 0; x < viewportWidth; x++) {
                this.add(viewportCells[y][x]);
            }
        }
        this.revalidate();
        this.repaint();
    }

    /**
     * getViewportCells calculates which cells should actually be visible in
     * the game UI at a given time
     *
     * @return viewport
     */
    public Cell[][] getViewportCells() {
        Cell[][] cells = this.plane.getCells();
        int height = this.plane.getHeight();
        int width = this.plane.getWidth();
        Cell[][] viewport = new Cell[this.viewportHeight][this.viewportWidth];
        for (int y = 0; y < this.viewportHeight; y++) {
            for (int x = 0; x < this.viewportWidth; x++) {
                int actualY = (this.viewportY + y) % height;
                int actualX = (this.viewportX + x) % width;
                viewport[y][x] = cells[actualY][actualX];
            }
        }
        return viewport;
    }

    /**
     * zoomIn zooms the view port in by some set amount on each call. The
     * minimum number of rows/cols visible is fixed at 10.
     */
    public void zoomIn() {
        this.setViewportWidth(Math.max(10, this.getViewportWidth() - 10));
        this.setViewportHeight(Math.max(10, this.getViewportHeight() - 10));
        this.updateViewport();
    }

    /**
     * zoomOut zooms the view port out by some set amount on each call. The
     * maximum number of rows/cols visible cannot be greater than the rows/cols
     * of cells created when the Plane object is instantiated (set by the `size`
     * value in the Life class.
     */
    public void zoomOut() {
        this.setViewportWidth(Math.min(plane.getWidth(), this.getViewportWidth() + 10));
        this.setViewportHeight(Math.min(plane.getHeight(), this.getViewportHeight() + 10));
        this.updateViewport();
    }

    public void pan(String direction) {

        // Move 20% of current viewport on each pan action
        int deltaX = 0;
        int deltaY = 0;

        switch (direction) {
            case "left":
                deltaX = -(int) Math.ceil(this.viewportWidth * 0.2);
                break;
            case "right":
                deltaX = (int) Math.ceil(this.viewportWidth * 0.2);
                break;
            case "up":
                deltaY = -(int) Math.ceil(this.viewportHeight * 0.2);
                break;
            case "down":
                deltaY = (int) Math.ceil(this.viewportHeight * 0.2);
                break;
            default:
                System.out.println("Invalid direction '" + direction);
                return;
        }

        // Move viewport but remain within maximum plane dimension
        this.viewportX = Math.min(
                plane.getWidth() - this.viewportWidth,
                Math.max(0, this.viewportX + deltaX));

        this.viewportY = Math.min(
                plane.getHeight() - this.viewportHeight,
                Math.max(0, this.viewportY + deltaY));

        this.updateViewport();
    }
}
