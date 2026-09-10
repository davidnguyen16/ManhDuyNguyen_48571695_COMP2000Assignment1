public class Fire {
    private int intensity;

    public Fire(int intensity) {
        if (intensity <= 0) {
            throw new IllegalArgumentException("Fire intensity must be positive");
        }
        this.intensity = intensity;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void strengthen(int amount) {
        if (amount < 0 || amount > Integer.MAX_VALUE - this.intensity) {
            throw new IllegalArgumentException("Invalid fire strength increase");
        }
        this.intensity += amount;
    }

    public void weaken(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Weakening amount cannot be negative");
        }
        this.intensity = Math.max(0, this.intensity - amount);
    }

    public boolean isExtinguished() {
        return this.intensity == 0;
    }
}
