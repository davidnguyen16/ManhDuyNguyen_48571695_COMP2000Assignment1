public class River extends Terrain {
    private double coolingStrength;

    public River(double coolingStrength) {
        if (!Double.isFinite(coolingStrength) || coolingStrength < 0.0) {
            throw new IllegalArgumentException(
                "Cooling strength must be non-negative"
            );
        }
        this.coolingStrength = coolingStrength;
    }

    public double getCoolingStrength() {
        return this.coolingStrength;
    }
}
