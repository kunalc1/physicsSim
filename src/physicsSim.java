import java.awt.*;
import java.util.ArrayList;

// TODO
// be able to make center of mass of entire system

public class physicsSim {
    public myFrame f;
    public Graphics g;
    public ArrayList<Body> bodies = new ArrayList<>();
    public ArrayList<Arrow> forces = new ArrayList<>();
    public boolean showForcesBetweenObjectsEnabled;
    public boolean gravityEnabled;
    public boolean gravityBetweenObjectsEnabled;
    public double GRAVITY = 0.2;
    public double ELASTICITY = 1.0;
    
    // Constants to avoid magic numbers
    private static final double GRAVITY_FORCE_SCALE = 10.0;
    private static final double OBJECT_GRAVITY_SCALE = 100.0;
    private static final double FORCE_ARROW_SCALE = 10000.0;
    
    // Reuse arrays to avoid garbage collection
    private final double[] tempPos = new double[2];
    private final double[] tempVel = new double[2];

    public physicsSim(int w, int h, boolean gravity, boolean gravityBetweenObjects) {
        f = new myFrame(w, h);
        g = f.getGraphics();
        gravityEnabled = gravity;
        gravityBetweenObjectsEnabled = gravityBetweenObjects;
        setupControls();
    }

    private void setupControls() {
        f.getShowForcesToggle().setSelected(showForcesBetweenObjectsEnabled);
        f.getShowForcesToggle().setText(showForcesBetweenObjectsEnabled ? "Show Forces: ON" : "Show Forces: OFF");
        f.getGravityToggle().setSelected(gravityEnabled);
        f.getGravityToggle().setText(gravityEnabled ? "Gravity: ON" : "Gravity: OFF");
        f.getGravityBetweenObjectsToggle().setSelected(gravityBetweenObjectsEnabled);
        f.getGravityBetweenObjectsToggle().setText(gravityBetweenObjectsEnabled ? "Gravity_obj: ON" : "Gravity_obj: OFF");
        f.getGravitySlider().setValue((int)(GRAVITY * 100));
        f.getElasticitySlider().setValue((int)(ELASTICITY * 100));

        f.getShowForcesToggle().addActionListener(e -> {
            showForcesBetweenObjectsEnabled = f.getShowForcesToggle().isSelected();
        });

        f.getGravityToggle().addActionListener(e -> {
            gravityEnabled = f.getGravityToggle().isSelected();
        });

        f.getGravityBetweenObjectsToggle().addActionListener(e -> {
            gravityBetweenObjectsEnabled = f.getGravityBetweenObjectsToggle().isSelected();
        });

        f.getGravitySlider().addChangeListener(e -> {
            GRAVITY = f.getGravitySlider().getValue() / 100.0;
        });

        f.getElasticitySlider().addChangeListener(e -> {
            ELASTICITY = f.getElasticitySlider().getValue() / 100.0;
        });
    }

    public void addBodies(){
        for (Body b: bodies){
            f.addBody(b);
        }
    }

    public ArrayList<Body> getBodies(){
        return bodies;
    }

    public void addBody(Body b){
        bodies.add(b);
        f.addBody(b);
    }

    public void addForce(Arrow a){
        f.addForce(a);
    }

    public void update(){
        forces.clear();
        if (gravityEnabled) {
            handleGravity();
        }
        if (gravityBetweenObjectsEnabled) {
            handleGravityBetweenObjects();
        }
        handleCollisions();
        handleBoundaries();
        
        // Only update forces in UI if they're visible
        f.forces = showForcesBetweenObjectsEnabled ? forces : new ArrayList<>();
        
        // Move bodies after all physics are calculated
        f.move();
        
        // Repaint at the end after all updates
        f.repaint();
    }

    public void handleGravity() {
        for (Body body : bodies) {
            double[] vel = body.getVels();
            vel[1] += GRAVITY;
            
            // Only add forces if they're being shown
            if (showForcesBetweenObjectsEnabled) {
                forces.add(new Arrow(body.getCoords(), Math.PI / 2, GRAVITY_FORCE_SCALE * body.getMass()));
            }
        }
    }

    public void handleGravityBetweenObjects() {
        int size = bodies.size();
        for (int i = 0; i < size; i++) {
            Body b1 = bodies.get(i);
            double[] pos1 = b1.getCoords();
            double[] vel1 = b1.getVels();
            double m1 = b1.getMass();
            
            for (int j = i + 1; j < size; j++) {
                Body b2 = bodies.get(j);
                double[] pos2 = b2.getCoords();
                double[] vel2 = b2.getVels();
                double m2 = b2.getMass();

                double dx = pos2[0] - pos1[0];
                double dy = pos2[1] - pos1[1];
                double distanceSquared = dx*dx + dy*dy;
                double distance = Math.sqrt(distanceSquared);

                if (distance > 0) {
                    double force = OBJECT_GRAVITY_SCALE * (m1 * m2) / distanceSquared;
                    double fx = force * dx / distance;
                    double fy = force * dy / distance;

                    vel1[0] += fx / m1;
                    vel1[1] += fy / m1;
                    vel2[0] -= fx / m2;
                    vel2[1] -= fy / m2;

                    // Only calculate and add force arrows if they're being shown
                    if (showForcesBetweenObjectsEnabled) {
                        double forceArrowLength = FORCE_ARROW_SCALE * m1 * m2 / distanceSquared;
                        forces.add(new Arrow(pos1, Math.atan2(dy, dx), forceArrowLength));
                        forces.add(new Arrow(pos2, Math.atan2(-dy, -dx), forceArrowLength));
                    }
                }
            }
        }
    }

    public void handleCollisions() {
        int size = bodies.size();
        for (int i = 0; i < size; i++) {
            Body b1 = bodies.get(i);
            for (int j = i + 1; j < size; j++) {
                Body b2 = bodies.get(j);
                if (b1.isColliding(b2)) {
                    resolveCollision(b1, b2);
                }
            }
        }
    }

    private void resolveCollision(Body b1, Body b2) {
        double[] pos1 = b1.getCoords();
        double[] pos2 = b2.getCoords();
        double[] vel1 = b1.getVels();
        double[] vel2 = b2.getVels();
        double m1 = b1.getMass();
        double m2 = b2.getMass();

        double dx = pos2[0] - pos1[0];
        double dy = pos2[1] - pos1[1];
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        // Avoid division by zero
        if (distance == 0) {
            // Small position adjustment to avoid division by zero
            pos1[0] -= 0.1;
            pos2[0] += 0.1;
            dx = pos2[0] - pos1[0];
            dy = pos2[1] - pos1[1];
            distance = Math.sqrt(dx*dx + dy*dy);
        }
        
        double nx = dx / distance;
        double ny = dy / distance;

        double v1n = vel1[0]*nx + vel1[1]*ny;
        double v2n = vel2[0]*nx + vel2[1]*ny;

        double v1nNew = ((m1-m2)*v1n + 2*m2*v2n) / (m1+m2);
        double v2nNew = ((m2-m1)*v2n + 2*m1*v1n) / (m1+m2);

        // Apply elasticity factor to the velocity changes
        vel1[0] = vel1[0] + nx*(v1nNew-v1n) * ELASTICITY;
        vel1[1] = vel1[1] + ny*(v1nNew-v1n) * ELASTICITY;
        vel2[0] = vel2[0] + nx*(v2nNew-v2n) * ELASTICITY;
        vel2[1] = vel2[1] + ny*(v2nNew-v2n) * ELASTICITY;

        // Repositioning to avoid overlapping
        double overlap = b1.getR() + b2.getR() - distance;
        pos1[0] -= overlap * nx * 0.5;
        pos1[1] -= overlap * ny * 0.5;
        pos2[0] += overlap * nx * 0.5;
        pos2[1] += overlap * ny * 0.5;
    }

    public void handleBoundaries() {
        int width = f.getContentPane().getWidth();
        int height = f.getContentPane().getHeight();

        for (Body b : bodies) {
            double[] pos = b.getCoords();
            double[] vel = b.getVels();
            double r = b.getR();

            // Handle x-axis boundaries
            if (pos[0] - r < 0) {
                pos[0] = r;
                vel[0] = -vel[0] * ELASTICITY;
            }
            else if (pos[0] + r + 200 > width) {
                pos[0] = width - r - 200;
                vel[0] = -vel[0] * ELASTICITY;
            }

            // Handle y-axis boundaries
            if (pos[1] - r < 0) {
                pos[1] = r;
                vel[1] = -vel[1] * ELASTICITY;
            } else if (pos[1] + r > height) {
                pos[1] = height - r;
                vel[1] = -vel[1] * ELASTICITY;
            }
        }
    }

    public static void main(String[] args) {
        physicsSim p = new physicsSim(800, 800, false, false);

        p.addBody(new Body(new double[]{400, 400}, new double[]{1, 0.5}, 10, 50));
        p.addBody(new Body(new double[]{200, 300}, new double[]{-1, 0.2}, 5, 30));
        p.addBody(new Body(new double[]{500, 200}, new double[]{0.2, 1.3}, 8, 40));

        javax.swing.Timer timer = new javax.swing.Timer(8, e -> p.update());
        timer.start();
    }
}
