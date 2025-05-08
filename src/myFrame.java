import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class myFrame extends JFrame {
    public ArrayList<Body> bodies = new ArrayList<Body>();
    private DrawingPanel drawingPanel;
    private JPanel controlPanel;
    private JToggleButton gravityToggle;
    private JSlider gravitySlider;
    private JLabel gravityValueLabel;

    public myFrame(int w, int h) {
        // Set up layout with drawing panel and control panel
        setLayout(new BorderLayout());
        
        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);
        
        // Create and configure control panel
        setupControlPanel();
        add(controlPanel, BorderLayout.EAST);
        
        setSize(w + controlPanel.getPreferredSize().width, h);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, getHeight()));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        gravityToggle = new JToggleButton("Gravity: OFF");
        gravityToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        gravityToggle.addActionListener(e -> {
            if (gravityToggle.isSelected()) {
                gravityToggle.setText("Gravity: ON");
            } else {
                gravityToggle.setText("Gravity: OFF");
            }
        });

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Gravity Strength"));
        
        gravitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 20);
        gravitySlider.setMajorTickSpacing(20);
        gravitySlider.setMinorTickSpacing(5);
        gravitySlider.setPaintTicks(true);
        gravitySlider.setPaintLabels(true);
        
        gravityValueLabel = new JLabel("Value: 0.2");
        gravityValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        gravitySlider.addChangeListener(e -> {
            double value = gravitySlider.getValue() / 100.0;
            gravityValueLabel.setText("Value: " + String.format("%.2f", value));
        });
        
        sliderPanel.add(gravitySlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(gravityValueLabel);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(gravityToggle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(sliderPanel);
        controlPanel.add(Box.createVerticalGlue());
    }

    public JToggleButton getGravityToggle() {
        return gravityToggle;
    }
    
    public JSlider getGravitySlider() {
        return gravitySlider;
    }

    public void addBody(Body b) {
        bodies.add(b);
    }

    public void move() {
        for (int i = 0; i < bodies.size(); i++){
            Body temp = bodies.get(i);
            temp.move();
            bodies.set(i, temp);
        }
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (Body b: bodies) {
                g.fillOval((int) (b.getCoords()[0] - b.getR()), (int) (b.getCoords()[1] - b.getR()),
                        (int) (b.getR() * 2), (int) (b.getR() * 2));
            }
        }
    }
}
