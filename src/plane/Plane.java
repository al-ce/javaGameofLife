package plane;

import cell.Cell;

/**
 * Plane is the representation of the plane on which Cell objects "live"
 */
public class Plane {
    /**
     * cells represents the Cell objects that reside on the Plane
     */
    public Cell[][] cells;
    /**
     * height represents the height of the plane
     */
    public int height;
    /**
     * width represents the width of the plane
     */
    public int width;

    public Plane(int height, int width) {
        this.height = height;
        this.width = width;
        this.cells = initCells(height, width);
    }

    /**
     * initCells initializes the 2D array of Cell objects on the Plane
     *
     * @param width  The width of the Plane
     * @param height The height of the Plane
     */
    Cell[][] initCells(int width, int height) {
        Cell[][] cells = new Cell[height][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                cells[y][x] = new Cell(y, x, false);
            }
        }
        return cells;
    }

    /**
     * getCells returns the 2D array containing the Cell objects on the Plane
     */
    public Cell[][] GetCells() {
        return this.cells;
    }

    /**
     * printCells prints the representation of the Cell objects' state on the
     * Plane
     */
    public void PrintCells() {
        for (int y = 0; y < this.width; y++) {
            for (int x = 0; x < this.height; x++) {
                Cell c = this.cells[y][x];
                String repr = c.live ? "□" : "■";
                System.out.print(repr);
            }
            System.out.println();
        }

    }

    /**
    * ToggleCell toggles the living state of a cell at a given position on the
    * Plane
    *
    * @param x The x coordinate of the Cell on the Plane
    * @param y The y coordinate of the Cell on the Plane
    */
    public void ToggleCell(int y, int x) {
        Cell c = this.cells[y][x];
        c.live = !c.live;
    }

}
