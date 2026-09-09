import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lightning extends Weather {
    private int strikesPerUpdate;
    private Random random;

    public Lightning(int strength, int duration, int strikesPerUpdate, Random random) {
        super(strength, duration);
        if (strikesPerUpdate < 0 || random == null) {
            throw new IllegalArgumentException(
                "Strikes must be non-negative and Random cannot be null"
            );
        }
        this.strikesPerUpdate = strikesPerUpdate;
        this.random = random;
    }

    public int getStrikesPerUpdate() {
        return this.strikesPerUpdate;
    }

    @Override
    protected void affectSimulation(ForestFireSimulation simulation) {
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
                simulation.getIgnitionThreshold() + getStrength()
            );
            simulation.recordLightningStrike(target.getRow(), target.getColumn());
        }
    }
}
