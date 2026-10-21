import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lightning implements WeatherEffectStrategy {
    private int strikesPerUpdate;
    private Random random;

    public Lightning(int strikesPerUpdate, Random random) {
        if (strikesPerUpdate <= 0 || random == null) {
            throw new IllegalArgumentException(
                "Strikes must be positive and Random cannot be null"
            );
        }
        this.strikesPerUpdate = strikesPerUpdate;
        this.random = random;
    }

    public int getStrikesPerUpdate() {
        return this.strikesPerUpdate;
    }

    @Override
    public void apply(ForestFireSimulation simulation, int strength) {
        Grid<Cell> grid = simulation.getGrid();
        List<Position> candidates = new ArrayList<Position>();
        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                Cell cell = grid.getCell(row, column);
                if (cell.canBurn() && !cell.isBurning()) {
                    candidates.add(new Position(row, column));
                }
            }
        }

        int count = Math.min(this.strikesPerUpdate, candidates.size());
        for (int strike = 0; strike < count; strike++) {
            Position target = candidates.remove(this.random.nextInt(candidates.size()));
            simulation.addHeat(
                target.getRow(), target.getColumn(),
                simulation.getIgnitionThreshold() + strength
            );
            simulation.recordLightningStrike(target.getRow(), target.getColumn());
        }
    }
}
