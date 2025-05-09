import java.awt.*;

public class Arrow {
    private double[] pos;
    private double direction;
    private double length;
    private Color color;
    
    // Cached values to avoid recalculation
    private double endX;
    private double endY;
    private int x1, y1, x2, y2; // arrowhead points

    public Arrow(double[] pos, double direction, double length) {
        this.pos = pos;
        this.direction = direction;
        this.length = length;
        this.color = Color.RED;
        calculatePoints();
    }

    public Arrow(double[] pos, double direction, double length, Color color) {
        this.pos = pos;
        this.direction = direction;
        this.length = length;
        this.color = color;
        calculatePoints();
    }
    
    private void calculatePoints() {
        // Calculate end point
        endX = pos[0] + length * Math.cos(direction);
        endY = pos[1] + length * Math.sin(direction);

        // Calculate arrowhead points
        int arrowSize = 7;
        double arrowAngle = Math.PI / 6; // 30 degrees

        double angle1 = direction + Math.PI - arrowAngle;
        double angle2 = direction + Math.PI + arrowAngle;

        x1 = (int)(endX + arrowSize * Math.cos(angle1));
        y1 = (int)(endY + arrowSize * Math.sin(angle1));
        x2 = (int)(endX + arrowSize * Math.cos(angle2));
        y2 = (int)(endY + arrowSize * Math.sin(angle2));
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color originalColor = g2d.getColor();
        g2d.setColor(color);

        // Draw main line
        g2d.drawLine((int)pos[0], (int)pos[1], (int)endX, (int)endY);

        // Draw arrowhead
        g2d.drawLine((int)endX, (int)endY, x1, y1);
        g2d.drawLine((int)endX, (int)endY, x2, y2);

        g2d.setColor(originalColor);
    }

    public double[] getPos() {
        return pos;
    }

    public double getDirection() {
        return direction;
    }

    public double getLength() {
        return length;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
