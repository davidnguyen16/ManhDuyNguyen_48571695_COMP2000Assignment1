import java.util.Random;

public interface MapGenerationStrategy {
    Grid<Cell> generate(int rows, int columns, Random random);
}
