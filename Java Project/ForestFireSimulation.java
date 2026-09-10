import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class ForestFireSimulation {
    private Grid<Cell> grid;
    private Grid<Double> heatMap;
    private WeatherManager weatherManager;
    private double ignitionThreshold;
    private List<Position> lastLightningStrikes;

    public ForestFireSimulation(int rows, int columns, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null");
        }

        this.grid = new Grid<Cell>(rows, columns);
        initializeGrid(random);

        this.heatMap = new Grid<Double>(rows, columns);
        this.heatMap.fill(0.0);

        this.weatherManager = new WeatherManager();
        this.ignitionThreshold = 100.0;
        this.lastLightningStrikes = new ArrayList<Position>();
    }

    public Grid<Cell> getGrid() {
        return this.grid;
    }

    public Grid<Double> getHeatMap() {
        return this.heatMap;
    }

    public void addWeather(Weather weather) {
        this.weatherManager.addWeather(weather);
    }

    public void clearWeather() {
        this.weatherManager.clearWeather();
        this.lastLightningStrikes.clear();
    }

    // Keep actual strike positions for one simulation step.
    public void recordLightningStrike(int row, int column) {
        if (!this.grid.isInBounds(row, column)) {
            throw new IndexOutOfBoundsException("Lightning strike outside the grid");
        }
        this.lastLightningStrikes.add(new Position(row, column));
    }

    public List<Position> getLastLightningStrikes() {
        return new ArrayList<Position>(this.lastLightningStrikes);
    }

    public List<Weather> getActiveWeatherEffects() {
        return this.weatherManager.getActiveWeatherEffects();
    }

    public double getIgnitionThreshold() {
        return this.ignitionThreshold;
    }

    public void igniteCell(int row, int column, int intensity) {
        if (intensity <= 0) {
            throw new IllegalArgumentException(
                "Fire intensity must be greater than zero"
            );
        }

        Cell cell = this.grid.getCell(row, column);

        if (cell == null) {
            throw new IllegalStateException(
                "The selected Grid position has no Cell"
            );
        }

        cell.ignite(intensity);
    }

    public void addHeat(int row, int column, double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                "Heat amount must be finite and non-negative"
            );
        }

        double currentHeat = this.heatMap.getCell(row, column);

        double newHeat = currentHeat + amount;
        if (!Double.isFinite(newHeat)) {
            throw new IllegalArgumentException("Resulting heat must be finite");
        }
        this.heatMap.setCell(row, column, newHeat);
    }

    public void removeHeat(int row, int column, double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                "Heat amount must be finite and non-negative"
            );
        }

        double currentHeat = this.heatMap.getCell(row, column);
        double newHeat = Math.max(0.0, currentHeat - amount);

        this.heatMap.setCell(row, column, newHeat);
    }

    public boolean hasReachedIgnitionThreshold(int row, int column) {
        return this.heatMap.getCell(row, column) >= this.ignitionThreshold;
    }

    public void update() {
        this.lastLightningStrikes.clear();
        spreadFires();
        applyWeather();
        applyRiverCooling();
        updateCells();
        igniteHeatedCells();
        evolveTerrain();
    }

    private void initializeGrid(Random random) {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0; column < this.grid.getColumns(); column++) {
                int terrainNumber = random.nextInt(100);
                Terrain terrain;

                if (terrainNumber < 60) {
                    terrain = new Tree(1, 100, 2, 0.2, 5);
                } else if (terrainNumber < 90) {
                    terrain = new Grass(1, 60, 1, 0.1, 10);
                } else {
                    terrain = new River(15.0);
                }

                this.grid.setCell(row, column, new Cell(terrain));
            }
        }
    }

    private void igniteHeatedCells() {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0; column < this.grid.getColumns(); column++) {
                if (!hasReachedIgnitionThreshold(row, column)) {
                    continue;
                }

                Cell cell = this.grid.getCell(row, column);

                if (!cell.canBurn()) {
                    this.heatMap.setCell(row, column, 0.0);
                    continue;
                }

                if (cell.isBurning()) {
                    continue;
                }

                int intensity = (int) Math.ceil(this.ignitionThreshold / 10.0);

                cell.ignite(intensity);

                if (cell.isBurning()) {
                    removeHeat(row, column, this.ignitionThreshold);
                }
            }
        }
    }

    private void updateCells() {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0; column < this.grid.getColumns(); column++) {

                Cell cell = this.grid.getCell(row, column);
                cell.updateBurningState();
            }
        }
    }

    private void spreadFires() {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0; column < this.grid.getColumns(); column++) {

                Cell sourceCell = this.grid.getCell(row, column);

                if (!sourceCell.isBurning()) {
                    continue;
                }

                int fireIntensity = sourceCell.getFire().getIntensity();

                double spreadHeat = fireIntensity;
                Terrain terrain = sourceCell.getTerrain();

                if (terrain instanceof Vegetation) {
                    Vegetation vegetation = (Vegetation) terrain;

                    spreadHeat =
                        vegetation.calculateSpreadHeat(fireIntensity);
                }

                for (Position neighbour : this.grid.neighbourPositions(row,column)) {

                    addHeat(neighbour.getRow(), neighbour.getColumn(), spreadHeat);
                }
            }
        }
    }

    private void applyWeather() {
        this.weatherManager.update(this);
    }

    private void applyRiverCooling() {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0; column < this.grid.getColumns(); column++) {

                Cell cell = this.grid.getCell(row, column);
                Terrain terrain = cell.getTerrain();

                if (!(terrain instanceof River)) {
                    continue;
                }

                River river = (River) terrain;
                double cooling = river.getCoolingStrength();

                removeHeat(row, column, cooling);

                for (Position neighbour : this.grid.neighbourPositions(row, column)) {

                    removeHeat(neighbour.getRow(), neighbour.getColumn(), cooling / 2.0);
                }
            }
        }
    }

    private void evolveTerrain() {
        for (int row = 0; row < this.grid.getRows(); row++) {
            for (int column = 0;
                column < this.grid.getColumns();
                column++) {

                Cell cell = this.grid.getCell(row, column);

                if (cell != null
                        && !cell.isBurning()
                        && cell.getTerrain() != null) {

                    cell.getTerrain().update();
                }
            }
        }
    }
}
