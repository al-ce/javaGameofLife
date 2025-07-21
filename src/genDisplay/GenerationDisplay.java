package genDisplay;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class GenerationDisplay extends JLabel {
    public GenerationDisplay() {
        super();

        this.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setPreferredSize(new Dimension(200, 25));
        this.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.setText("Gen: 0");
    }
}
