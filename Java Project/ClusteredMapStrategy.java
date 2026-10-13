import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class ClusteredMapStrategy implements MapGenerationStrategy {
    @Override 
    public Grid<Cell> generate(int rows, int columns, Random random) {
        Grid<Cell> grid = new RandomMapStrategy().generate(rows, columns, random);

        List<Cell> trees = new ArrayList<>();
        List<Cell> otherCells = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = grid.getCell(row, column);
                if (cell.getTerrain() instanceof Tree) {
                    trees.add(cell);
                } else {
                    otherCells.add(cell);
                }
            }
        }

        List<Position> positions = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                positions.add(new Position(row, column));
            }
        }

        double centerRow = (rows - 1) / 2.0;
        double centerColumn = (columns - 1) / 2.0;

        positions.sort(Comparator.comparingDouble(position -> {
            double rowDistance = position.getRow() - centerRow;
            double columnDistance = position.getColumn() - centerColumn;
            return rowDistance * rowDistance + columnDistance * columnDistance;
        }));

        List<Cell> orderedCells = new ArrayList<>(trees);
        orderedCells.addAll(otherCells);

        for (int i = 0; i < positions.size(); i++) {
            Position position = positions.get(i);
            Cell cell = orderedCells.get(i);
            grid.setCell(position.getRow(), position.getColumn(), cell);
        }
        return grid;
    }
    
}
