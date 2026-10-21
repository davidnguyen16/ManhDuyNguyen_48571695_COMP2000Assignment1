@FunctionalInterface
public interface WeatherEffectStrategy {
    void apply(ForestFireSimulation simulation, int strength);
}
