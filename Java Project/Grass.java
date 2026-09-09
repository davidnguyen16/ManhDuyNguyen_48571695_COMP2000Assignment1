public class Grass extends Vegetation {
    private int density;

    public Grass(int age, int fuel, int burnRate, double moisture, int density) {
        super(age, fuel, burnRate, moisture);
        if (density < 0) {
            throw new IllegalArgumentException("Density cannot be negative");
        }
        this.density = density;
    }

    public int getDensity() {
        return this.density;
    }

    public void setDensity(int density) {
        if (density < 0) {
            throw new IllegalArgumentException("Density cannot be negative");
        }
        this.density = density;
    }

    @Override
    public double calculateSpreadHeat(int fireIntensity) {
        return super.calculateSpreadHeat(fireIntensity)
            + Math.min(this.density / 2.0, 5.0);
    }
}
