public class Wind extends Weather {
    // 0 = north, 1 = east, 2 = south, 3 = west.
    private int direction;

    public Wind(int strength, int duration, int direction) {
        super(strength, duration);
        if (direction < 0 || direction > 3) {
            throw new IllegalArgumentException("Wind direction must be 0, 1, 2 or 3");
        }
        this.direction = direction;
    }

    public int getDirection() {
        return this.direction;
    }

    @Override
    protected void affectSimulation(ForestFireSimulation simulation) {
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
                    simulation.addHeat(targetRow, targetColumn, getStrength());
                }
            }
        }
    }
}
