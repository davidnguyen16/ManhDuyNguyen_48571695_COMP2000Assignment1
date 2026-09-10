public class Cell {
    private Terrain terrain;
    private Fire fire;

    public Cell(Terrain terrain) {
        if (terrain == null) {
            throw new IllegalArgumentException("Cell terrain cannot be null");
        }
        this.terrain = terrain;
        this.fire = null;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public Fire getFire() {
        return this.fire;
    }

    public boolean canBurn() {
        return this.terrain instanceof Burnable burnable
            && !burnable.isBurnedOut();
    }

    public boolean isBurning() {
        return this.fire != null && !this.fire.isExtinguished();
    }

    public void ignite(int intensity) {
        if (intensity <= 0) {
            throw new IllegalArgumentException("Fire intensity must be positive");
        }
        if (!canBurn()) {
            return;
        }
        if (isBurning()) {
            this.fire.strengthen(intensity);
        } else {
            this.fire = new Fire(intensity);
        }
    }

    private void extinguish() {
        this.fire = null;
    }

    public void updateBurningState() {
        if (!isBurning()) {
            return;
        }
        if (!(this.terrain instanceof Burnable burnable)
                || burnable.isBurnedOut()) {
            extinguish();
            return;
        }

        burnable.burn(this.fire.getIntensity());
        this.fire.weaken(1);

        if (burnable.isBurnedOut() || this.fire.isExtinguished()) {
            extinguish();
        }
    }
}
