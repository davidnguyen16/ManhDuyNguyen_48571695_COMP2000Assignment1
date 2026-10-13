public final class ExperimentResult {
    private final String layoutName;
    private final long seed;
    private final int initialVegetationCount;
    private final int everIgnitedCount;

    public ExperimentResult(String layoutName, long seed, int initialVegetationCount, int everIgnitedCount) {
        this.layoutName = layoutName;
        this.seed = seed;
        this.initialVegetationCount = initialVegetationCount;
        this.everIgnitedCount = everIgnitedCount;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public long getSeed() {
        return seed;
    }

    public int getInitialVegetationCount() {
        return initialVegetationCount;
    }

    public int getEverIgnitedCount() {
        return everIgnitedCount;
    }

    public double getIgnitedPercentage() {
        if (initialVegetationCount == 0) {
            return 0.0;
        }
        return 100.0 * everIgnitedCount / initialVegetationCount;
    }
}
