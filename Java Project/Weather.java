public class Weather {
    private int strength;
    private int duration;
    private final WeatherEffectStrategy effect;

    public Weather(int strength, int duration, WeatherEffectStrategy effect) {
        if (strength < 0 || duration < 0) {
            throw new IllegalArgumentException(
                "Weather strength and duration cannot be negative"
            );
        }
        if (effect == null) {
            throw new IllegalArgumentException("Weather effect cannot be null");
        }
        this.strength = strength;
        this.duration = duration;
        this.effect = effect;
    }

    public int getStrength() {
        return this.strength;
    }

    public int getDuration() {
        return this.duration;
    }

    public WeatherEffectStrategy getEffect() {
        return this.effect;
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
        this.effect.apply(simulation, this.strength);
        this.duration--;
    }
}
