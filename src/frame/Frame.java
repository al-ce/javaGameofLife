package frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import gamePanel.GamePanel;
import rleinput.RLEInput;
import toolbar.Toolbar;

public class Frame extends JFrame {

    public Frame(String title, int appWidth, GamePanel gamePanel, Toolbar toolBar, RLEInput rleInput) {
        super(title);

        this.setLayout(new BorderLayout());

        // Add toolbar to the frame
        this.add(toolBar, BorderLayout.NORTH);

        // Add game panel to center of frame
        this.add(gamePanel, BorderLayout.CENTER);

        // Add RLE input to the bottom of frame
        this.add(rleInput, BorderLayout.SOUTH);

        // Listen for clicks outside of rle input to force it to lose focus
        MouseAdapter clickListener = makeClickListener();
        addClickListenerRecursively(this, clickListener);

        // Finalize the app config
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(appWidth, appWidth);
        this.setResizable(false);
        this.setVisible(true);

    }

    /**
     * addClickListenerRecursively adds a listener to all components of the
     * Frame recursively. This is primarily so we can take focus away from the
     * RLE input text box when we click away for it, which makes the keypress
     * bindings active again.
     *
     * @param container
     * @param listener
     */
    private void addClickListenerRecursively(Container container, MouseAdapter listener) {
        container.addMouseListener(listener);
        for (Component c : container.getComponents()) {
            if (c instanceof Container) {
                addClickListenerRecursively((Container) c, listener);
            } else {
                c.addMouseListener(listener);
            }
        }
    }

    private MouseAdapter makeClickListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Component clickedComponent = e.getComponent();
                System.out.printf("clickedComponent: %s\n", clickedComponent.getClass());
                clickedComponent.requestFocus();
            }
        };
    }
}
