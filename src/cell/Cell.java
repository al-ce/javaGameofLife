package cell;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class Cell extends JButton {

    /**
     * ALIVE_COLOR is the color of a living cell
     */
    public Color ALIVE_COLOR = Color.BLACK;
    /**
     * BORDER_COLOR is the color of the border around any cell
     */
    public Color BORDER_COLOR = Color.GRAY;

    /**
     * afterlifeColors is used to lookup the color of a dead cell based on how
     * long ago it died (its 'afterlife')
     */
    private Color[] afterlifeColors = {
            hsb(217, 18, 95),
            hsb(217, 15, 93),
            hsb(223, 9, 90),
            hsb(227, 6, 89),
            hsb(248, 4, 88),
            hsb(300, 2, 86),
            hsb(0, 4, 88),
            hsb(12, 7, 89),
            hsb(19, 10, 90),
            hsb(21, 12, 92),
            hsb(23, 15, 93),
            hsb(25, 18, 94),
            hsb(25, 21, 96),
            hsb(26, 23, 97),
            hsb(27, 26, 99),
            hsb(27, 27, 100),
            Color.WHITE,
    };

    /**
     * deadColor is the color to set on a dead cell. Initially white, but if a
     * cell was once alive, the color should be set based on how long ago the
     * most recent generation was. See Johnson and Green p. xii Figure 1
     **/
    private Color deadColor = Color.WHITE;

    /**
     * mrg indicates the most recent generation this cell was living. The
     * initial value of -1 indicates this cell has never been alive.
     */
    private int mrg = -1;

    /**
     * age it the the age of a living cell. A living cell that has not lived
     * through a change in generation has age 0. A dead cell has age -1.
     */
    private int age = -1;

    private boolean state = false;

    public Cell() {
        super();

        // Set default appearance (dead)
        setBackground(deadColor);

        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true); // Ensure background is painted

        // Remove margins (no space between cells)
        setMargin(new Insets(0, 0, 0, 0));

        // Add click handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleState();
            }
        });

        // Remove default spacebar action from all cells
        InputMap focusMap = this.getInputMap(JComponent.WHEN_FOCUSED);
        focusMap.put(KeyStroke.getKeyStroke("SPACE"), "none");

    }

    public void setDeadColor(Color deadColor) {
        this.deadColor = deadColor;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * setDeadColor sets the dead color of a cell according to its "afterlife".
     * "afterlife" indicates how long a cell has been dead. The default color
     * of a cell is white, meaning it was never born. Recently living cells
     * are blue and progress towards an orange hue. Once a cell has been dead
     * long enough, it revers to white. See Johnson and Green p. xii Figure 1.
     **/
    public void setDeadColor(int generation) {
        Color afterlifeColor;
        if (this.mrg == -1) {
            afterlifeColor = Color.WHITE;
        } else {
            int afterlife = generation - this.mrg;
            afterlifeColor = getAfterlifeColor(afterlife);
        }
        this.deadColor = afterlifeColor;
    }

    /**
     * getAfterlifeColor gets the color from the afterlifeColorMap based on the
     * give afterlife value, rounded down to the nearest divisor of 5
     */
    public Color getAfterlifeColor(int afterlife) {
        /**
         * interval sets the number of generations until the afterlife color
         * should change
         */
        int interval = 5;
        int index = Math.min(afterlife / interval, afterlifeColors.length - 1);
        return afterlifeColors[index];
    }

    /**
     * state gets the living state of the cell
     *
     * @return The boolean indicating the living state of the cell
     */
    public boolean state() {
        return this.state;
    }

    /**
     * setState sets the living state of the cell and its GUI representation
     *
     * @param state The value of the living state to be set
     */
    public void setState(boolean state) {
        this.state = state;
        setBackground(state ? ALIVE_COLOR : deadColor);
        repaint();
    }

    /**
     * getMrg returns the value for mrg, indicating the most recent
     * generation this Cell was living
     */
    public int getMrg() {
        return this.mrg;
    }

    /**
     * setMrg sets a value for mrg, indicating the most recent
     * generation this Cell was living
     */
    public void setMrg(int mrg) {
        this.mrg = mrg;
    }

    /**
     * getAge returns the age of a living cell. A cell that was born on the same
     * generation getAge is called has age 0. A dead cell has age -1.
     */
    public int getAge() {
        return this.age;
    }

    /**
     * incAge increments this cell's age by one tick
     */
    public void incAge() {
        this.age++;
    }

    /**
     * resetCell sets the cell to a new state without creating a new object.
     */
    public void resetCell() {
        this.state = false;
        this.age = -1;
        this.mrg = -1;
        setBackground(Color.WHITE);
    }

    /**
     * hsb calculates the hsb float values from the hue, saturation and
     * brightness (or value in hsv)
     */
    private Color hsb(int h, int s, int b) {
        return Color.getHSBColor(h / 360f, s / 100f, b / 100f);
    }

    /**
     * toggleState toggles the living state of a cell and redraws it according
     * to its appropriate color
     */
    private void toggleState() {
        this.state = !this.state;
        setBackground(state ? ALIVE_COLOR : deadColor);
        repaint();
    }
}
