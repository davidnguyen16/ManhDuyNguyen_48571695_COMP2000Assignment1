public abstract class Weather {
    private int strength;
    private int duration;

    protected Weather(int strength, int duration) {
        if (strength < 0 || duration < 0) {
            throw new IllegalArgumentException(
                "Weather strength and duration cannot be negative"
            );
        }
        this.strength = strength;
        this.duration = duration;
    }

    public int getStrength() {
        return this.strength;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isActive() {
        return this.duration > 0;
    }

    public void update(ForestFireSimulation simulation) {
        if (simulation == null) {
            throw new IllegalArgumentException("Simulation cannot be null");
        }
        if (!isActive()) {
            return;
        }
        affectSimulation(simulation);
        this.duration--;
    }

    protected abstract void affectSimulation(ForestFireSimulation simulation);
}
