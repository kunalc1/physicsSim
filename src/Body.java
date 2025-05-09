public class Body {
    private double[] coords;
    private double[] vels;
    private double m;
    private double r;
    // Cache for collision calculation
    private double r2; // radius squared

    public Body(double[] coords, double[] vels, double m, double r) {
        this.coords = coords;
        this.vels = vels;
        this.m = m;
        this.r = r;
        this.r2 = r * r; // Precalculate r²
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
        double dx = this.coords[0] - other.coords[0];
        double dy = this.coords[1] - other.coords[1];
        
        // First, a quick check using squared distance (avoid sqrt)
        double sumRadii = this.r + other.r;
        double distanceSquared = dx * dx + dy * dy;
        
        // Only calculate sqrt if necessary
        return distanceSquared <= (sumRadii * sumRadii);
    }
}
