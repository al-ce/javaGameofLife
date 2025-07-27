package rleinput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cell.Cell;
import grid.Grid;

/**
 * RLEInput is a text input box that accepts Run Length Encoded format text and
 * places the corresponding patterns on the Grid
 */
public class RLEInput extends JPanel {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private RLEButton resetButton;
    private RLEButton updateButton;
    private int textBoxHeight = 80;
    private Grid grid;

    public RLEInput(Grid grid) {

        this.grid = grid;
        setLayout(new BorderLayout());

        // Create the text area for RLE patterns
        textArea = new JTextArea(textBoxHeight, 0);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Create scroll pane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Create update button
        updateButton = new RLEButton("Update");
        updateButton.addActionListener(e -> {
            setRLEPattern();
        });

        // Create reset button
        resetButton = new RLEButton("Reset");
        resetButton.addActionListener(e -> {
            textArea.setText("");
        });

        // Add components
        add(resetButton, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(updateButton, BorderLayout.EAST);

        // Configure panel size
        setPreferredSize(new Dimension(0, textBoxHeight));
        setMinimumSize(new Dimension(0, textBoxHeight));
        setMaximumSize(new Dimension(0, textBoxHeight));
    }

    public boolean isFocusOwner() {
        return textArea.isFocusOwner();
    }

    /**
     * setRLEPattern sets a pattern onto the grid from a pattern found in the
     * RLE input textbox.
     */
    private void setRLEPattern() {
        // Clear grid before trying to set a pattern
        grid.clearGrid();

        boolean[][] patternCells = this.parseRLE();

        // ignore empty pattern
        int sum = 0;
        for (int row = 0; row < patternCells.length; row++) {
            for (int col = 0; col < patternCells[row].length; col++) {
                sum += patternCells[row][col] ? 1 : 0;
            }
        }
        if (sum == 0) {
            return;
        }

        Cell[][] cells = this.grid.getCells();

        int offsetRow = (this.grid.getHeight() / 2) - (patternCells.length / 2);
        int offsetCol = (this.grid.getHeight() / 2) - (patternCells[0].length / 2);

        for (int row = 0; row < patternCells.length; row++) {
            for (int col = 0; col < patternCells[row].length; col++) {
                cells[row + offsetRow][col + offsetCol].setState(patternCells[row][col]);
            }
        }
    }

    /**
     * Parse an RLE string into a matrix of booleans indicating a cell pattern
     * See https://conwaylife.com/wiki/Run_Length_Encoded
     *
     * @return boolean matrix indicating cell state
     */
    private boolean[][] parseRLE() {
        String[] lines = textArea.getText().split(System.getProperty("line.separator"));

        // Pattern to check if a line is a comment that can be ignored
        Pattern isComment = Pattern.compile("^[\\s]*#");
        // Pattern to check if a line is a header, with width and height of the
        // cellular automata pattern (rule is optional)
        Pattern isHeader = Pattern.compile("^x\\s?=\\s?([0-9]+),\\s?y\\s?=\\s?([0-9]+),?\\s?[a-z]?\\s?\\s?=?\\s?(.*)?");

        // Header values
        int x = 0;
        int y = 0;

        String rule = ""; // NOTE: we are not doing anything with the rule yet

        // Empty string to build Cellular automata pattern
        String caPattern = "";

        // Parse each line of the original text box string
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Ignore any comment lines
            if (isComment.matcher(line).find()) {
                continue;
            }

            // Populate header matches
            Matcher headerMatcher = isHeader.matcher(line);
            if (headerMatcher.find()) {
                x = Integer.parseInt(headerMatcher.group(1));
                y = Integer.parseInt(headerMatcher.group(2));
                if (headerMatcher.group(3) != null) {
                    rule = headerMatcher.group(3);
                }
            }

            // Assume all other lines are pattern lines
            else {
                caPattern += line;
            }
        }

        System.out.printf("Parsed RLE header x: %s y: %s rule: %s\n", x, y, rule);

        // Track cell state as bools
        boolean[][] patternCells = new boolean[y][x];

        // If the pattern is out of bounds of the grid dimensions, early return
        if (this.grid.getWidth() < x || this.grid.getHeight() < y) {
            System.out.printf(
                    "Grid is too small for pattern; w: %s h: %s\n",
                    this.grid.getWidth(),
                    this.grid.getHeight());
            return patternCells;
        }

        // Parse pattern one char at a time
        int runCount = 0;
        int row = 0;
        int col = 0;
        for (char c : caPattern.toCharArray()) {
            // Update run_count, accounting for counts > 10
            if (c >= '0' && c <= '9') {
                runCount = (runCount * 10) + c - 48;
                continue;
            }
            // Minimum run_count of 1 (might still be 0 for parser)
            runCount = Math.max(runCount, 1);
            if (c == 'b' || c == 'o') {
                for (int i = 0; i < runCount; i++) {
                    patternCells[row][col] = c == 'o';
                    col++;
                }
                runCount = 0;
            }
            // Add line breaks (e.g 5$ means "finish the current row and insert
            // four blank rows")
            // cf https://golly.sourceforge.io/Help/formats.html#rle
            else if (c == '$') {
                col = 0;
                row += runCount;
                runCount = 0;
            }
        }

        return patternCells;
    }
}
