package gamePanel;

import java.awt.GridLayout;

import javax.swing.JPanel;

import cell.Cell;
import grid.Grid;

public class GamePanel extends JPanel {
    private Grid grid;

    // viewport sets how many cells are visible in the game UI out of the
    // actual cells. This is used to implement a viewport-zoom
    public int viewportX = 0;
    public int viewportY = 0;
    public int viewportWidth;
    public int viewportHeight;

    /**
     * GamePanel is the component that displays the cells on a grid
     * 
     * @param grid
     */
    public GamePanel(Grid grid) {
        this.grid = grid;
        // Set initial viewport width to half of grid width rounded to 10
        this.viewportHeight = (this.grid.getHeight() / 2 / 10) * 10;
        this.viewportWidth = (this.grid.getWidth() / 2 / 10) * 10;
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

    /**
     * updateViewport redraws the visible cells on the game panel depending on
     * the viewport dimensions
     */
    public void updateViewport() {
        this.removeAll();
        this.setLayout(new GridLayout(viewportHeight, viewportWidth, 1, 1));

        Cell[][] cells = this.grid.getCells();
        int height = this.grid.getHeight();
        int width = this.grid.getWidth();
        for (int y = 0; y < viewportHeight; y++) {
            for (int x = 0; x < viewportWidth; x++) {
                int actualY = (this.viewportY + y) % height;
                int actualX = (this.viewportX + x) % width;
                Cell cell = cells[actualY][actualX];
                this.add(cell);
            }
        }
        this.revalidate();
        this.repaint();
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
     * of cells created when the Grid object is instantiated (set by the `size`
     * value in the Life class.
     */
    public void zoomOut() {
        this.setViewportWidth(Math.min(grid.getWidth(), this.getViewportWidth() + 10));
        this.setViewportHeight(Math.min(grid.getHeight(), this.getViewportHeight() + 10));
        this.updateViewport();
    }

    /**
     * pan moves the viewport in some direction by 20% of the viewport's
     * current columns or rows.
     * 
     * @param direction Which way to pan the viewport
     */
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

        // Move viewport but remain within maximum grid dimension
        this.viewportX = Math.min(
                grid.getWidth() - this.viewportWidth,
                Math.max(0, this.viewportX + deltaX));

        this.viewportY = Math.min(
                grid.getHeight() - this.viewportHeight,
                Math.max(0, this.viewportY + deltaY));

        this.updateViewport();
    }
}
