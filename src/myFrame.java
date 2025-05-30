import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class myFrame extends JFrame {
    public ArrayList<Body> bodies = new ArrayList<>();
    public ArrayList<Arrow> forces = new ArrayList<>();
    private DrawingPanel drawingPanel;
    private JPanel controlPanel;
    private JToggleButton gravityToggle;
    private JToggleButton gravityBetweenObjectsToggle;
    private JToggleButton showForcesToggle;
    private JSlider gravitySlider;
    private JSlider elasticitySlider;
    private JLabel gravityValueLabel;
    private JLabel elasticityValueLabel;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, getHeight()));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        showForcesToggle = new JToggleButton("Show Forces: OFF");
        showForcesToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        showForcesToggle.addActionListener(e -> {
            if (showForcesToggle.isSelected()) {
                showForcesToggle.setText("Show Forces: ON");
            } else {
                showForcesToggle.setText("Show Forces: OFF");
            }
        });

        gravityToggle = new JToggleButton("Gravity: OFF");
        gravityToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        gravityToggle.addActionListener(e -> {
            if (gravityToggle.isSelected()) {
                gravityToggle.setText("Gravity: ON");
            } else {
                gravityToggle.setText("Gravity: OFF");
            }
        });

        gravityBetweenObjectsToggle = new JToggleButton("Gravity_obj: OFF");
        gravityBetweenObjectsToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        gravityBetweenObjectsToggle.addActionListener(e -> {
            if (gravityBetweenObjectsToggle.isSelected()) {
                gravityBetweenObjectsToggle.setText("Gravity_obj: ON");
            } else {
                gravityBetweenObjectsToggle.setText("Gravity_obj: OFF");
            }
        });

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Physical Variables"));

        // Add gravity label
        JLabel gravityLabel = new JLabel("Gravity Strength:");
        gravityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // Add elasticity label
        JLabel elasticityLabel = new JLabel("Elasticity Coefficient:");
        elasticityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        elasticitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        elasticitySlider.setMajorTickSpacing(20);
        elasticitySlider.setMinorTickSpacing(5);
        elasticitySlider.setPaintTicks(true);
        elasticitySlider.setPaintLabels(true);

        elasticityValueLabel = new JLabel("Value: 1.00");
        elasticityValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        elasticitySlider.addChangeListener(e -> {
            double value = elasticitySlider.getValue() / 100.0;
            elasticityValueLabel.setText("Value: " + String.format("%.2f", value));
        });


        sliderPanel.add(gravityLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(gravitySlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(gravityValueLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sliderPanel.add(elasticityLabel);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(elasticitySlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sliderPanel.add(elasticityValueLabel);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(showForcesToggle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(gravityToggle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(gravityBetweenObjectsToggle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(sliderPanel);
        controlPanel.add(Box.createVerticalGlue());
    }

    public JToggleButton getShowForcesToggle() {
        return showForcesToggle;
    }

    public JToggleButton getGravityToggle() {
        return gravityToggle;
    }

    public JToggleButton getGravityBetweenObjectsToggle() {
        return gravityBetweenObjectsToggle;
    }

    public JSlider getGravitySlider() {
        return gravitySlider;
    }

    public JSlider getElasticitySlider() {
        return elasticitySlider;
    }

    public void addBody(Body b) {
        bodies.add(b);
    }

    public void addForce(Arrow a) {
        forces.add(a);
    }

    public void move() {
        // Update all body positions at once
        for (Body body : bodies) {
            body.move();
        }
    }

    private class DrawingPanel extends JPanel {
        // Use double buffering for smoother rendering
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable antialiasing for smoother drawing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all bodies
            for (Body b: bodies) {
                g2d.fillOval((int) (b.getCoords()[0] - b.getR()), (int) (b.getCoords()[1] - b.getR()),
                        (int) (b.getR() * 2), (int) (b.getR() * 2));
            }

            // Draw forces if any
            if (!forces.isEmpty()) {
                for (Arrow f : forces) {
                    f.draw(g2d);
                }
            }
        }
    }
}
