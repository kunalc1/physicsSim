import java.awt.*;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class physicsSim {
    public myFrame f;
    public Graphics g;
    public ArrayList<Body> bodies = new ArrayList<Body>();
    public boolean gravityEnabled = false;
    public boolean gravityBetweenObjectsEnabled = false;
    public double GRAVITY = 0.2;
    public double ELASTICITY = 1.0;

    public physicsSim(int w, int h, boolean gravity, boolean gravityBetweenObjects) {
        f = new myFrame(w, h);
        g = f.getGraphics();
        gravityEnabled = gravity;
        gravityBetweenObjectsEnabled = gravityBetweenObjects;
        setupControls();
    }
    
    private void setupControls() {
        f.getGravityToggle().setSelected(gravityEnabled);
        f.getGravityToggle().setText(gravityEnabled ? "Gravity: ON" : "Gravity: OFF");
        f.getGravityBetweenObjectsToggle().setSelected(gravityBetweenObjectsEnabled);
        f.getGravityBetweenObjectsToggle().setText(gravityBetweenObjectsEnabled ? "Gravity_obj: ON" : "Gravity_obj: OFF");
        f.getGravitySlider().setValue((int)(GRAVITY * 100));
        f.getElasticitySlider().setValue((int)(ELASTICITY * 100));

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

    public void update(){
        f.repaint();
        f.move();
        if (gravityEnabled) {
            handleGravity();
        }
        if (gravityBetweenObjectsEnabled) {
            handleGravityBetweenObjects();
        }
        handleCollisions();
        handleBoundaries();
    }

    public void handleGravity() {
        for (int i = 0; i < bodies.size(); i++) {
            double[] tmp = bodies.get(i).getVels();
            tmp[1] += GRAVITY;
            Body replace = new Body(bodies.get(i).getCoords(), tmp, bodies.get(i).getMass(), bodies.get(i).getR());
            bodies.set(i, replace);
        }
    }

    public void handleGravityBetweenObjects() {
        for (int i = 0; i < bodies.size(); i++){
            for (int j = i + 1; j < bodies.size(); j++){
                Body b1 = bodies.get(i);
                Body b2 = bodies.get(j);

                double[] pos1 = b1.getCoords();
                double[] pos2 = b2.getCoords();

                double dx = pos2[0] - pos1[0];
                double dy = pos2[1] - pos1[1];
                double distance = Math.sqrt(dx*dx + dy*dy);

                if (distance > 0) {
                    double force = 100 * (b1.getMass() * b2.getMass()) / (distance * distance);
                    double fx = force * dx / distance;
                    double fy = force * dy / distance;

                    double[] vel1 = b1.getVels();
                    double[] vel2 = b2.getVels();

                    vel1[0] += fx / b1.getMass();
                    vel1[1] += fy / b1.getMass();
                    vel2[0] -= fx / b2.getMass();
                    vel2[1] -= fy / b2.getMass();

                    b1.setVels(vel1);
                    b2.setVels(vel2);
                }
            }
        }
    }

    public void handleCollisions() {
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                Body b1 = bodies.get(i);
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

        b1.setVels(vel1);
        b2.setVels(vel2);

        double overlap = b1.getR() + b2.getR() - distance;
        pos1[0] -= overlap * nx * 0.5;
        pos1[1] -= overlap * ny * 0.5;
        pos2[0] += overlap * nx * 0.5;
        pos2[1] += overlap * ny * 0.5;
        
        b1.setCoords(pos1);
        b2.setCoords(pos2);
    }
    
    public void handleBoundaries() {
        int width = f.getContentPane().getWidth();
        int height = f.getContentPane().getHeight();
        
        for (Body b : bodies) {
            double[] pos = b.getCoords();
            double[] vel = b.getVels();
            double r = b.getR();

            if (pos[0] - r < 0) {
                pos[0] = r;
                vel[0] = -vel[0] * ELASTICITY;
            } else if (pos[0] + r + 200 > width) {
                pos[0] = width - r - 200;
                vel[0] = -vel[0] * ELASTICITY;
            }
            
            if (pos[1] - r < 0) {
                pos[1] = r;
                vel[1] = -vel[1] * ELASTICITY;
            } else if (pos[1] + r > height) {
                pos[1] = height - r;
                if (!gravityEnabled) {
                    vel[1] = -vel[1] * ELASTICITY;
                } else {
                    vel[1] = -vel[1] * ELASTICITY;
                }
            }
            
            b.setCoords(pos);
            b.setVels(vel);
        }
    }

    public static void main(String[] args) {
        physicsSim p = new physicsSim(800, 800, false, false);
        
        p.addBody(new Body(new double[]{400, 400}, new double[]{1, 0.5}, 10, 50));
        p.addBody(new Body(new double[]{200, 300}, new double[]{-1, 0.2}, 5, 30));
        p.addBody(new Body(new double[]{500, 200}, new double[]{0.2, 1.3}, 8, 40));
//
//        p.addBody(new Body(new double[]{400, 400}, new double[]{0, -1.5}, 10, 50));
//        p.addBody(new Body(new double[]{400, 200}, new double[]{0, 0}, 10, 50));

        javax.swing.Timer timer = new javax.swing.Timer(8, e -> {
            p.update();
        });
        timer.start();
    }
}
