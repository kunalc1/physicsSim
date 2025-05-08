import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class myFrame extends JFrame {
    public ArrayList<Body> bodies = new ArrayList<Body>();
    private DrawingPanel drawingPanel;

    public myFrame(int w, int h) {
        drawingPanel = new DrawingPanel();
        setContentPane(drawingPanel);
        
        setSize(w, h);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
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
