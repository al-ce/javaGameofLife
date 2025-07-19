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
     * width represents the width of the plane
     */
    public int width;
    /**
     * height represents the height of the plane
     */
    public int height;

    public Plane(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = initCells(width, height);
    }

    /**
     * getCells returns the 2D array containing the Cell objects on the Plane
     */
    public Cell[][] getCells() {
        return this.cells;
    }

    /**
     * initCells initializes the 2D array of Cell objects on the Plane
     * 
     * @param width  The width of the Plane
     * @param height The height of the Plane
     */
    public Cell[][] initCells(int width, int height) {
        Cell[][] cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y, false);
            }
        }
        return cells;
    }

    /**
     * printCells prints the representation of the Cell objects' state on the
     * Plane
     */
    public void printCells() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                Cell c = this.cells[x][y];
                String repr = c.live ? "□" : "■";
                System.out.print(repr);
            }
            System.out.println();
        }

    }

}
