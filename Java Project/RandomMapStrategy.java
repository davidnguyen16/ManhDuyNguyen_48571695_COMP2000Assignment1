import java.util.Random;

public class RandomMapStrategy implements MapGenerationStrategy {
    @Override
    public Grid<Cell> generate(int rows, int columns, Random random) {
        Grid<Cell> grid = new Grid<>(rows, columns);

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int terrainNumber = random.nextInt(100);
                Terrain terrain;

                if (terrainNumber < 60) {
                    terrain = new Tree(1, 100, 2, 0.2, 5);
                } else if (terrainNumber < 90) {
                    terrain = new Grass(1, 60, 1, 0.1, 10);
                } else {
                    terrain = new River(15.0);
                }
                grid.setCell(row, column, new Cell(terrain));
            }
        }
        return grid;
    }
}
