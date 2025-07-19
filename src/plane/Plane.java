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
     * height represents the height of the plane
     */
    public int height;

    /**
     * width represents the width of the plane
     */
    public int width;

    /**
     * time represents a moment in time, starting at 0.
     */
    public int time;

    public Plane(int height, int width) {
        this.height = height;
        this.width = width;
        this.cells = initCells(height, width);
        this.buffer = initCells(height, width);
        this.time = 0;
    }

    /**
     * initCells initializes the 2D array of Cell objects on the Plane
     *
     * @param width  The width of the Plane
     * @param height The height of the Plane
     */
    boolean[][] initCells(int width, int height) {
        boolean[][] cells = new boolean[height][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                cells[y][x] = false;
            }
        }
        return cells;
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
        System.err.println(
                String.format(
                        "%c must be in range [0, %d), but x == %d",
                        coord,
                        max,
                        val));

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
            boolean c = this.cells[y][x];
            this.cells[y][x] = !c;
        } catch (ArrayIndexOutOfBoundsException e) {
            if (y < 0 || y >= this.width) {
                cellIndexErr('y', this.height, y);
            }
            if (x < 0 || x >= this.width) {
                cellIndexErr('x', this.width, x);
            }
        }

    }

}
