package rleinput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
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
    private JButton button;
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
        button = new JButton("Update");
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.addActionListener(e -> {
            setRLEPattern();
        });

        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);

        // Configure panel size
        setPreferredSize(new Dimension(0, textBoxHeight));
        setMinimumSize(new Dimension(0, textBoxHeight));
        setMaximumSize(new Dimension(0, textBoxHeight));
    }

    public int getTextBoxHeight() {
        return textBoxHeight;
    }

    public void setTextBoxHeight(int height) {
        textBoxHeight = height;
        setPreferredSize(new Dimension(0, height));
        setMinimumSize(new Dimension(0, height));
        setMaximumSize(new Dimension(0, height));
        button.setPreferredSize(new Dimension(80, height));
    }

    public boolean isFocusOwner() {
        return textArea.isFocusOwner();
    }

    /**
     * setRLEPattern sets a pattern onto the grid from a pattern found in the
     * RLE input textbox.
     */
    private void setRLEPattern() {
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

        for (int row = 0; row < patternCells.length; row++) {
            for (int col = 0; col < patternCells[row].length; col++) {
                cells[row][col].setState(patternCells[row][col]);
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
                for (int h = 1; h <= headerMatcher.groupCount(); h++) {
                    String group = headerMatcher.group(h);
                    if (h == 1) {
                        x = Integer.parseInt(group);
                    } else if (h == 2) {
                        y = Integer.parseInt(group);
                    } else if (h == 3) {
                        rule = group;
                    }
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

        // "$" marks the end of a line (i.e. row)
        String[] patternLines = caPattern.split("\\$");
        // looking for a run_count (optional) and a tag
        Pattern groupPattern = Pattern.compile("(\\d*[bo])");

        // Parse each pattern line into a row of the cells matrix
        for (int row = 0; row < patternLines.length; row++) {
            String line = patternLines[row];
            Matcher matcher = groupPattern.matcher(line);

            int col = 0;
            // call find() until matches on this line run out (subsequent calls
            // continue from the first character after the previous match)
            while (matcher.find()) {
                String item = matcher.group(1);

                // Get tag value: 'b' means alive, 'o' means dead
                char tag = item.charAt(item.length() - 1);

                int actualCount = 1; // add at least one cell

                // If this match has a run count, keep setting cell state
                // values into the cells matrix. We can check for a run count
                // by trying to split at a tag.
                String[] runCount = item.split("[bo]");
                if (runCount.length > 0) {
                    actualCount = Integer.parseInt(runCount[0]);
                }
                for (int i = 0; i < actualCount; i++) {
                    patternCells[row][col] = tag == 'o';
                    col++;
                }
            }
            // dead cells at the end of a pattern line to not need to be
            // encoded, so the value of `col` at the end of this iteration
            // may not match the value of `x`, the length of a matrix row
        }
        return patternCells;
    }
}
