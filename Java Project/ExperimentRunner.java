import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.DoubleSummaryStatistics;

public class ExperimentRunner {
    public List<ExperimentResult> runComparison(int rows, int columns, long seeds, int ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Ticks must be positive");
        }

        ForestFireSimulation randomSimulation = new ForestFireSimulation(rows, columns, new Random(seeds), new RandomMapStrategy());

        ForestFireSimulation clusteredSimulation = new ForestFireSimulation(rows, columns, new Random(seeds), new ClusteredMapStrategy());

        Position ignitionPosition = findCommonTreePosition(randomSimulation.getGrid(), clusteredSimulation.getGrid());
        List<ExperimentResult> results = new ArrayList<>();

        results.add(measureTrial("Random", randomSimulation, seeds, ticks, ignitionPosition));
        results.add(measureTrial("Clustered", clusteredSimulation, seeds, ticks, ignitionPosition));
        return results;
    }

    public List<ExperimentResult> runBatch(int rows, int columns, int seedCount, int ticks) {
        if (seedCount <= 0) {
            throw new IllegalArgumentException("Seed count must be positive");
        }

        return LongStream.range(0, seedCount)
                .parallel()
                .mapToObj(seed -> runComparison(rows, columns, seed, ticks))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<String, DoubleSummaryStatistics> summarizeResults(List<ExperimentResult> results) {
        return results.stream()
                .collect(Collectors.groupingBy(
                        ExperimentResult::getLayoutName,
                        Collectors.summarizingDouble(ExperimentResult::getIgnitedPercentage)
                ));
    }

    private Position findCommonTreePosition(Grid<Cell> firstGrid, Grid<Cell> secondGrid) {
        for (int row = 0; row < firstGrid.getRows(); row++) {
            for (int column = 0; column < firstGrid.getColumns(); column++) {
                boolean firstIsTree =  firstGrid.getCell(row, column).getTerrain() instanceof Tree;
                boolean secondIsTree = secondGrid.getCell(row, column).getTerrain() instanceof Tree;

                if (firstIsTree && secondIsTree) {
                    return new Position(row, column);
                }
            }
        }
        
        throw new IllegalArgumentException("No common tree position. Try another seed or a larger map");
    }

    private int countVegetation(Grid<Cell> grid) {
        int count = 0;
        
        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                if (grid.getCell(row, column).getTerrain() instanceof Vegetation) {
                    count++;
                }
            }
        }
        return count;
    }

    private int markNewIgnitions(Grid<Cell> grid, boolean[][] everIgnited) {
        int newIgnitions = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                if (grid.getCell(row, column).isBurning() && !everIgnited[row][column]) {
                    everIgnited[row][column] = true;
                    newIgnitions++;
                }
            }
        }
        return newIgnitions;
    }

    private ExperimentResult measureTrial(String layoutName, ForestFireSimulation simulation, long seed, int ticks, Position ignitionPosition) {
        Grid<Cell> grid = simulation.getGrid();
        int initialVegetationCount = countVegetation(grid);

        boolean[][] everIgnited = new boolean[grid.getRows()][grid.getColumns()];

        simulation.igniteCell(ignitionPosition.getRow(), ignitionPosition.getColumn(), 10);

        int everIgnitedCount = markNewIgnitions(grid, everIgnited);

        for (int tick = 0; tick < ticks; tick++) {
            simulation.update();
            everIgnitedCount += markNewIgnitions(grid, everIgnited);
        }

        return new ExperimentResult(layoutName, seed, initialVegetationCount, everIgnitedCount);
    }
}
