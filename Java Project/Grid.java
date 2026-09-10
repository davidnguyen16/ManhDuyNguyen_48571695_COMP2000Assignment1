import java.util.ArrayList;
import java.util.List;

public class Grid<T> {
    private int rows;
    private int columns;
    private List<List<T>> cells;

    public Grid(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                "Rows and columns must be greater than zero"
            );
        }

        this.rows = rows;
        this.columns = columns;
        this.cells = new ArrayList<List<T>>();

        // Create each row
        for (int row = 0; row < rows; row++) {
            List<T> currentRow = new ArrayList<T>();

            // create columns in rows
            for (int column = 0; column < columns; column++) {
                currentRow.add(null);
            }

            cells.add(currentRow);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public T getCell(int row, int column) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        return cells.get(row).get(column);
    }

    public void setCell(int row, int column, T value) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        cells.get(row).set(column, value);
    }

    /** Reuses the same value in every slot; use separate Cells for mutable terrain. */
    public void fill(T value) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells.get(row).set(column, value);
            }
        }
    }

    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public List<Position> neighbourPositions(int row, int column) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        List<Position> positions = new ArrayList<Position>();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                int neighbourRow = row + rowOffset;
                int neighbourColumn = column + columnOffset;

                if (isInBounds(neighbourRow, neighbourColumn)) {
                    positions.add(new Position(neighbourRow, neighbourColumn));
                }
            }
        }
        return positions;
    }

}
