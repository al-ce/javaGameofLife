package grid;

import cell.Cell;

/**
 * Grid is the representation of the grid on which Cell objects "live"
 */
public class Grid {
    /**
     * cells represents the Cell objects that reside on the Grid
     */
    private Cell[][] cells;

    /**
     * buffer is a matrix used to hold the cell values for the next state of
     * the Grid as rules are being applied. After the calculations are done
     * and a generation passes, the boolean values from the buffer matrix will
     * be copied to the main matrix's Cells' alive values, and the buffer
     * matrix will be reset
     */
    private boolean[][] buffer;

    /**
     * height represents the height of the grid in number of cells
     */
    private int height;

    /**
     * width represents the width of the grid in number of cells
     */
    private int width;

    /**
     * generation represents a generation timestep, starting at 0.
     */
    private int generation;

    /**
     * @param gridHeight The height of the side of the grid, e.g. 40 means a
     *                   40x40 grid
     */
    public Grid(int gridHeight) {

        this.height = gridHeight;
        this.width = gridHeight;
        this.cells = initCells();
        this.buffer = new boolean[gridHeight][gridHeight];
        this.generation = 0;
    }

    /**
     * getCells returns the cell array of all cells on the grid
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * @return The height of the grid in number of cells
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The width of the grid in number of cells
     */
    public int getWidth() {
        return width;
    }

    /**
     * evolve increments the generations value by a tick and applies the Life
     * rules to the cells matrix. The rules are as follows from Johnston and
     * Greene p. 3 (conwaylife.com/book)
     * 1. "If a cell is alive, it survives to the next generation if
     * --- has 2 or 3 live neighbors [...]."
     * 2. "If a cell is dead, it comes to life in the next generation if it
     * --- has exactly 3 live neighbors [...]."
     */
    public void evolve() {

        // Apply Life rules to the buffer matrix
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell c = this.cells[y][x];
                int n = countLiveNeighbors(y, x);

                // Simplified rules: a cell survives/generates if it has three
                // neighbors or if it is both alive and has 2 neighbors;
                // otherwise it dies
                boolean lives = n == 3 || (c.state() && n == 2);

                this.buffer[y][x] = lives;

            }
        }

        // Toggle the cells based on the buffer values
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                Cell cell = this.cells[y][x];
                boolean state = this.buffer[y][x];

                // Increment or reset age
                if (state) {
                    cell.incAge();
                } else {
                    cell.setDeadColor(this.generation);
                    cell.setAge(-1);
                }

                cell.setState(state);
                if (state) {
                    cell.setMrg(this.generation);
                }
            }
        }

        // Initialize a new matrix for the new buffer
        this.buffer = new boolean[this.height][this.width];

        // Increment generation
        this.generation++;
    }

    /**
     * clearGrid clears the Grid so that all cells are set to a dead state.
     * Generation is reset to 0.
     */
    public void clearGrid() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.cells[y][x].resetCell();
            }
        }

        this.generation = 0;
    }

    public int getGeneration() {
        return this.generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * initCells initializes the matrix of Cell objects on the Grid
     */
    Cell[][] initCells() {
        Cell[][] cells = new Cell[this.height][this.width];
        for (int y = 0; y < this.width; y++) {
            for (int x = 0; x < this.height; x++) {
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

        int rows = this.cells.length;
        int cols = this.cells[0].length;

        // Iterate over row range of [y-1, y+1] and col range [x-1, x+1]
        // but don't go out of bounds
        for (int rowCursor = y - 1; rowCursor <= y + 1; rowCursor++) {
            for (int colCursor = x - 1; colCursor <= x + 1; colCursor++) {
                // Skip if it's the cell we're evolving
                if (rowCursor == y && colCursor == x) {
                    continue;
                }

                // Wrap around rows/cols
                int row = (rowCursor + rows) % rows;
                int col = (colCursor + cols) % cols;
                // Increment count if neighbor cell is live
                if (this.cells[row][col].state()) {
                    n++;
                }
            }
        }
        return n;
    }
}
