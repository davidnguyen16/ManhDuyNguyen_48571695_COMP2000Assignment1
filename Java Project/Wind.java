public class Wind implements WeatherEffectStrategy {
    private static final int EMBER_DISTANCE = 3;
    private final int direction;

    public Wind(int direction) {
        if (direction < 0 || direction > 3) {
            throw new IllegalArgumentException("Wind direction must be 0, 1, 2 or 3");
        }
        this.direction = direction;
    }

    public int getDirection() {
        return this.direction;
    }

    @Override
    public void apply(ForestFireSimulation simulation, int strength) {
        int rowChange = 0;
        int columnChange = 0;
        switch (this.direction) {
            case 0: rowChange = -1; break;
            case 1: columnChange = 1; break;
            case 2: rowChange = 1; break;
            case 3: columnChange = -1; break;
            default: throw new IllegalStateException("Invalid wind direction");
        }

        Grid<Cell> grid = simulation.getGrid();
        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                if (!grid.getCell(row, column).isBurning()) {
                    continue;
                }
                int targetRow = row + rowChange;
                int targetColumn = column + columnChange;
                if (grid.isInBounds(targetRow, targetColumn)) {
                    simulation.addHeat(targetRow, targetColumn, strength);
                }

                if (!simulation.isFlyingEmbersEnabled() || strength <= 0) {
                    continue;
                }

                int emberRow = row + rowChange * EMBER_DISTANCE;
                int emberColumn = column + columnChange * EMBER_DISTANCE;
                if (!grid.isInBounds(emberRow, emberColumn)) {
                    continue;
                }

                Cell targetCell = grid.getCell(emberRow, emberColumn);
                if (targetCell.canBurn() && !targetCell.isBurning()) {
                    simulation.addHeat(emberRow, emberColumn, strength);
                    simulation.recordEmberFlight(row, column, emberRow, emberColumn);
                }
            }
        }
    }
}
