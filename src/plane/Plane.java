package plane;

import javax.swing.*;
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
     * buffer is a matrix used to hold the cell values for the next state of
     * the Plane as rules are being applied. After the calculations are done
     * and a generation passes, the boolean values from the buffer matrix will
     * be copied to the main matrix's Cells' alive values, and the buffer
     * matrix will be reset
     */
    public boolean[][] buffer;

    /**
     * height represents the height of the plane
     */
    public int height;

    /**
     * width represents the width of the plane
     */
    public int width;

    /**
     * generation represents a generation timestep, starting at 0.
     */
    public int generation;

    public Plane(int size) {
        this.height = size;
        this.width = size;
        this.cells = initCells(height, width);
        this.buffer = new boolean[size][size];
        this.generation = 0;

    }

    /**
     * initCells initializes the matrix of Cell objects on the Plane
     *
     * @param width  The width of the Plane
     * @param height The height of the Plane
     */
    Cell[][] initCells(int height, int width) {
        Cell[][] cells = new Cell[height][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                Cell c = new Cell();
                cells[y][x] = c;
            }
        }
        return cells;
    }

    /**
     * countLiveNeighbors counts the number of live neighbors of a cell at a
     * given coordinate in a Moore neighborhood (8-cell neighborhood)
     * 
     * @return The number of live neighbors found
     */
    int countLiveNeighbors(int y, int x) {
        // Initialize neighbor count
        int n = 0;

        // Set start to row or col above/before the current one,
        // unless that would be out of bounds, in which case start at the
        // current row/col (0)
        int yStart = Math.max(y - 1, 0);
        int xStart = Math.max(x - 1, 0);
        // Iterate over row range of [y-1, y+1] and col range [x-1, x+1]
        // but don't go out of bounds
        for (int row = yStart; row <= y + 1 && row < this.height; row++) {
            for (int col = xStart; col <= x + 1 && col < this.width; col++) {
                // Skip if it's the cell we're evolving
                if (row == y && col == x) {
                    continue;
                }
                // Increment count if neighbor cell is live
                if (this.cells[row][col].isAlive()) {
                    n++;
                }
            }
        }
        return n;
    }

    /**
     * Evolve increments the generations value by a tick and applies the Life
     * rules to the cells matrix. The rules are as follows from Johson and
     * Greene p. 3 (conwaylife.com/book)
     * 1. "If a cell is alive, it survives to the next generation if
     * --- has 2 or 3 live neighbors [...]."
     * 2. "If a cell is dead, it comes to life in the next generation if it
     * --- has exactly 3 live neighbors [...]."
     */
    public void Evolve() {

        // Apply Life rules to the buffer matrix
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell c = this.cells[y][x];
                int n = countLiveNeighbors(y, x);

                if (c.isAlive()) {
                    this.buffer[y][x] = n == 2 || n == 3;
                } else {
                    this.buffer[y][x] = n == 3;
                }
            }
        }

        // Toggle the cells based on the buffer values
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                this.cells[y][x].setAlive(this.buffer[y][x]);
            }
        }

        // Initialize a new matrix for the new buffer
        this.buffer = new boolean[this.height][this.width];

        // Increment generation
        this.generation++;
    }

    /**
     * ClearPlane clears the Plane so that all cells are set to a dead state
     */
    public void ClearPlane() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.cells[y][x].setAlive(false);
            }
        }
    }
}
