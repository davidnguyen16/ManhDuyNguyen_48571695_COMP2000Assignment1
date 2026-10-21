import java.util.stream.IntStream;

public class Rain implements WeatherEffectStrategy {
    @Override
    public void apply(ForestFireSimulation simulation, int strength) {
        Grid<Double> heatMap = simulation.getHeatMap();
        IntStream.range(0, heatMap.getRows()).parallel().forEach(row -> {
            for (int column = 0; column < heatMap.getColumns(); column++) {
                simulation.removeHeat(row, column, strength);
            }
        });
    }
}
