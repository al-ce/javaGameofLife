package frame;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import gamePanel.GamePanel;
import toolbar.Toolbar;

public class Frame extends JFrame {
    public Frame(String title, GamePanel gamePanel, Toolbar toolBar) {
        this.setLayout(new BorderLayout());

        // Add toolbar to the frame
        this.add(toolBar, BorderLayout.NORTH);

        // Add game panel to center of frame
        this.add(gamePanel, BorderLayout.CENTER);

        // Finalize the app config
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setResizable(false);
        this.setVisible(true);
    }
}
