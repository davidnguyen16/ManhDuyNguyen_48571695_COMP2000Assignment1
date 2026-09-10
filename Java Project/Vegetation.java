public abstract class Vegetation extends Terrain implements Burnable {
    private int age;
    private int fuel;
    private int burnRate;
    private double moisture;

    protected Vegetation(int age, int fuel, int burnRate, double moisture) {
        if (age < 0 || fuel < 0 || burnRate <= 0) {
            throw new IllegalArgumentException(
                "Age and fuel must be non-negative; burn rate must be positive"
            );
        }
        this.age = age;
        this.fuel = fuel;
        this.burnRate = burnRate;
        if (!Double.isFinite(moisture) || moisture < 0.0 || moisture > 1.0) {
            throw new IllegalArgumentException("Moisture must be between 0 and 1");
        }
        this.moisture = moisture;
    }

    public int getAge() {
        return this.age;
    }

    public int getFuel() {
        return this.fuel;
    }

    public int getBurnRate() {
        return this.burnRate;
    }

    public double getMoisture() {
        return this.moisture;
    }

    public void setBurnRate(int burnRate) {
        if (burnRate <= 0) {
            throw new IllegalArgumentException("Burn rate must be positive");
        }
        this.burnRate = burnRate;
    }

    public void setMoisture(double moisture) {
        if (!Double.isFinite(moisture) || moisture < 0.0 || moisture > 1.0) {
            throw new IllegalArgumentException("Moisture must be between 0 and 1");
        }
        this.moisture = moisture;
    }

    @Override
    public void burn(int intensity) {
        if (intensity <= 0) {
            throw new IllegalArgumentException("Fire intensity must be positive");
        }
        double fuelLoss = Math.ceil(
            (double) intensity * this.burnRate * (1.0 - this.moisture)
        );
        this.fuel = (int) Math.max(0.0, this.fuel - fuelLoss);
    }

    @Override
    public boolean isBurnedOut() {
        return this.fuel == 0;
    }

    public double calculateSpreadHeat(int fireIntensity) {
        if (fireIntensity <= 0) {
            throw new IllegalArgumentException("Fire intensity must be positive");
        }
        double ageBonus = Math.min(this.age / 5.0, 10.0);
        return fireIntensity * 2.0 + ageBonus;
    }

    @Override
    public void update() {
        if (!isBurnedOut() && this.age < Integer.MAX_VALUE) {
            this.age++;
        }
    }
}
