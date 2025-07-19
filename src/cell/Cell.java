package cell;

/**
 * Cell is the main entity of the Life program that is alive or dead based on
 * the state of its neighbors and the rules of the game.
 */
public class Cell {
    /**
     * live represents the living state of the cell
     */
    public boolean live;
    /**
     * x represents the position of the Cell on the Plane's x coordinate
     */
    public int x;
    /**
     * y represents the position of the Cell on the Plane's y coordinate
     */
    public int y;

    public Cell(int x, int y, boolean live) {
        this.x = x;
        this.y = y;
        this.live = live;
    }
}
