public class Rain implements WeatherEffectStrategy {
    @Override
    public void apply(ForestFireSimulation simulation, int strength) {
        Grid<Double> heatMap = simulation.getHeatMap();
        for (int row = 0; row < heatMap.getRows(); row++) {
            for (int column = 0; column < heatMap.getColumns(); column++) {
                simulation.removeHeat(row, column, strength);
            }
        }
    }
}
