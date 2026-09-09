public class Heatwave extends Weather {
    public Heatwave(int strength, int duration) {
        super(strength, duration);
    }

    @Override
    protected void affectSimulation(ForestFireSimulation simulation) {
        Grid<Double> heatMap = simulation.getHeatMap();
        for (int row = 0; row < heatMap.getRows(); row++) {
            for (int column = 0; column < heatMap.getColumns(); column++) {
                simulation.addHeat(row, column, getStrength());
            }
        }
    }
}
