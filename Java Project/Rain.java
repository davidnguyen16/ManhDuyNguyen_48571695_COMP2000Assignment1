public class Rain extends Weather {
    public Rain(int strength, int duration) {
        super(strength, duration);
    }

    @Override
    protected void affectSimulation(ForestFireSimulation simulation) {
        Grid<Double> heatMap = simulation.getHeatMap();
        for (int row = 0; row < heatMap.getRows(); row++) {
            for (int column = 0; column < heatMap.getColumns(); column++) {
                simulation.removeHeat(row, column, getStrength());
            }
        }
    }
}
