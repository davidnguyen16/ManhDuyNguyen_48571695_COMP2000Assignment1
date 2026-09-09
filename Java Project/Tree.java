public class Tree extends Vegetation {
    private int height;

    public Tree(int age, int fuel, int burnRate, double moisture, int height) {
        super(age, fuel, burnRate, moisture);
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be negative");
        }
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public double calculateSpreadHeat(int fireIntensity) {
        return super.calculateSpreadHeat(fireIntensity)
            + Math.min(this.height / 2.0, 10.0);
    }

    @Override
    public void update() {
        if (isBurnedOut()) {
            return;
        }
        int previousAge = getAge();
        super.update();
        if (getAge() != previousAge && getAge() % 5 == 0
                && this.height < Integer.MAX_VALUE) {
            this.height++;
        }
    }
}
