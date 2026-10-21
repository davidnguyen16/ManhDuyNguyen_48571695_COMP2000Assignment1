public final class EmberFlight {
    private final Position source;
    private final Position target;

    public EmberFlight(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        this.source = new Position(sourceRow, sourceColumn);
        this.target = new Position(targetRow, targetColumn);
    }

    public Position getSource() {
        return this.source;
    }

    public Position getTarget() {
        return this.target;
    }
}
