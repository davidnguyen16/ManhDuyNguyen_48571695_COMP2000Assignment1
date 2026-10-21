import java.util.stream.Collector;

public class ForestStatistics {
    private int burningCellCount;
    private int burnedOutCellCount;
    private long totalFuel;

    private ForestStatistics(int burningCellCount, int burnedOutCellCount,
            long totalFuel) {
        this.burningCellCount = burningCellCount;
        this.burnedOutCellCount = burnedOutCellCount;
        this.totalFuel = totalFuel;
    }

    public int getBurningCellCount() {
        return this.burningCellCount;
    }

    public int getBurnedOutCellCount() {
        return this.burnedOutCellCount;
    }

    public long getTotalFuel() {
        return this.totalFuel;
    }

    public static Collector<Cell, ?, ForestStatistics> collector() {
        return Collector.of(
            Accumulator::new,
            Accumulator::addCell,
            Accumulator::combine,
            Accumulator::toStatistics
        );
    }

    private static class Accumulator {
        private int burningCellCount;
        private int burnedOutCellCount;
        private long totalFuel;

        private void addCell(Cell cell) {
            Terrain terrain = cell.getTerrain();
            if (!(terrain instanceof Vegetation)) {
                return;
            }

            Vegetation vegetation = (Vegetation) terrain;
            if (cell.isBurning()) {
                this.burningCellCount++;
            }
            if (vegetation.isBurnedOut()) {
                this.burnedOutCellCount++;
            }
            this.totalFuel += vegetation.getFuel();
        }

        private Accumulator combine(Accumulator other) {
            this.burningCellCount += other.burningCellCount;
            this.burnedOutCellCount += other.burnedOutCellCount;
            this.totalFuel += other.totalFuel;
            return this;
        }

        private ForestStatistics toStatistics() {
            return new ForestStatistics(
                this.burningCellCount, this.burnedOutCellCount, this.totalFuel
            );
        }
    }
}
