public class Body {
    private double[] coords;
    private double[] vels;
    private double m;
    private double r;

    public Body(double[] coords, double[] vels, double m, double r) {
        this.coords = coords;
        this.vels = vels;
        this.m = m;
        this.r = r;
    }

    public double[] getCoords() {
        return coords;
    }

    public double[] getVels() {
        return vels;
    }

    public double getR() {
        return r;
    }

    public double getMass() {
        return m;
    }

    public void setVels(double[] vels) {
        this.vels = vels;
    }

    public void setCoords(double[] coords) {
        this.coords = coords;
    }

    public void move() {
        coords[0] += vels[0];
        coords[1] += vels[1];
    }

    public boolean isColliding(Body other) {
        double dx = this.coords[0] - other.getCoords()[0];
        double dy = this.coords[1] - other.getCoords()[1];
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (this.r + other.r);
    }
}
