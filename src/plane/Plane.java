package plane;

/**
 * Plane is the representation of the plane on which Cell objects "live"
 */
public class Plane {
    /**
     * cells represents the Cell objects that reside on the Plane
     */
    public boolean[][] cells;

    /**
     * buffer is a matrix used to hold the cell values for the next state of
     * the Plane as rules are being applied. After the calculations are done
     * and a generation passes, the values from the buffer matrix will be
     * copied to the main matrix, and the buffer matrix will be reset
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

    public Plane(int height, int width) {
        this.height = height;
        this.width = width;
        this.cells = initCells(height, width);
        this.buffer = initCells(height, width);
        this.generation = 0;
    }

    /**
     * initCells initializes the 2D array of Cell objects on the Plane
     *
     * @param width  The width of the Plane
     * @param height The height of the Plane
     */
    boolean[][] initCells(int height, int width) {
        return new boolean[height][width];
    }

    /**
     * zeroMatrix sets all the value of a matrix to false
     */
    void zeroMatrix(boolean[][] m) {
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                m[y][x] = false;
            }
        }
    }

    /**
     * getCells returns the 2D array containing the Cell objects on the Plane
     */
    public boolean[][] GetCells() {
        return this.cells;
    }

    /**
     * printCells prints the representation of the Cell objects' state on the
     * Plane
     */
    public void PrintCells() {
        for (int y = 0; y < this.width; y++) {
            for (int x = 0; x < this.height; x++) {
                boolean c = this.cells[y][x];
                String repr = c ? "□" : "■";
                System.out.print(repr);
            }
            System.out.println();
        }

    }

    /**
     * cellIndexErr prints an out of bounds error message
     */
    void cellIndexErr(char coord, int max, int val) {
        System.err.printf(
                "%c must be in range [0, %d), but x == %d%n",
                coord,
                max,
                val);

    }

    /**
     * ToggleCell toggles the living state of a cell at a given position on the
     * Plane
     *
     * @param y The y coordinate of the Cell on the Plane
     * @param x The x coordinate of the Cell on the Plane
     */
    public void ToggleCell(int y, int x) {
        try {
            this.cells[y][x] = !this.cells[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            if (y < 0 || y >= this.width) {
                cellIndexErr('y', this.height, y);
            }
            if (x < 0 || x >= this.width) {
                cellIndexErr('x', this.width, x);
            }
        }

    }

    /**
     * Evolve increments the generations value by a tick and applies the Life
     * rules to the cells matrix.
     */
    public void Evolve() {

        // Apply Life rules to the buffer matrix
        // NOTE: placeholder rule to test buffer swap
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                this.buffer[y][x] = !this.cells[y][x];
            }
        }

        // Copy the new values from the buffer to the main matrix
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                this.cells[y][x] = this.buffer[y][x];
            }
        }

        // Initialize a new matrix for the new buffer
        this.buffer = initCells(width, height);

        // Increment generation
        this.generation++;
    }
}
