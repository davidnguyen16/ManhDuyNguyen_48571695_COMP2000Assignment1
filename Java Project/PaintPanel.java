import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PaintPanel extends JPanel {
    private ForestFireSimulation simulation;
    private int cellSize;
    private int animationFrame;

    public PaintPanel(ForestFireSimulation simulation) {
        if (simulation == null) {
            throw new IllegalArgumentException(
                "Simulation cannot be null"
            );
        }

        this.simulation = simulation;
        this.cellSize = 12;

        Grid<Cell> grid = this.simulation.getGrid();

        setPreferredSize(
            new Dimension(
                grid.getColumns() * this.cellSize,
                grid.getRows() * this.cellSize
            )
        );

        setBackground(Color.WHITE);
        setToolTipText(
            "Left-click to select and ignite; right-click to select only"
        );
    }

    public void setSimulation(ForestFireSimulation simulation) {
        if (simulation == null) {
            throw new IllegalArgumentException(
                "Simulation cannot be null"
            );
        }

        this.simulation = simulation;
        this.animationFrame = 0;

        Grid<Cell> grid = this.simulation.getGrid();

        setPreferredSize(
            new Dimension(
                grid.getColumns() * this.cellSize,
                grid.getRows() * this.cellSize
            )
        );

        revalidate();
        repaint();
    }

    // Visual time only: never update heat, fuel or Weather duration here.
    public void advanceAnimation() {
        this.animationFrame = (this.animationFrame + 1) % 10000;
        repaint();
    }

    public Position getGridPositionAt(int x,int y) {

        if (x < 0 || y < 0) {
            return null;
        }

        int column = x / this.cellSize;
        int row = y / this.cellSize;

        Grid<Cell> grid = this.simulation.getGrid();

        if (!grid.isInBounds(row, column)) {
            return null;
        }

        return new Position(row, column);
    }

    // Set protected access because paintComponent method in JPanel class is protected
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Grid<Cell> grid = this.simulation.getGrid();
        drawGrid(graphics, grid);
        drawWeatherEffects(graphics, grid);
    }

    private void drawWeatherEffects(Graphics graphics, Grid<Cell> grid) {
        Graphics2D overlay = (Graphics2D) graphics.create();
        try {
            int width = grid.getColumns() * this.cellSize;
            int height = grid.getRows() * this.cellSize;
            overlay.clipRect(0, 0, width, height);
            overlay.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
            );
            for (Weather weather : this.simulation.getActiveWeatherEffects()) {
                if (weather instanceof Rain) {
                    drawRain(overlay, width, height);
                } else if (weather instanceof Wind) {
                    drawWind(overlay, width, height, ((Wind) weather).getDirection());
                }
            }
            // Lightning may already have expired; draw the recorded hits instead.
            drawLightning(overlay);
        } finally {
            overlay.dispose();
        }
    }

    private void drawRain(Graphics2D graphics, int width, int height) {
        graphics.setColor(new Color(120, 210, 255, 200));
        graphics.setStroke(new BasicStroke(1.5f));
        int offset = (this.animationFrame * 6) % 36;
        for (int x = 8; x < width + 10; x += 24) {
            for (int y = -36; y < height; y += 36) {
                int dropY = y + offset + (x / 24 % 2) * 18;
                graphics.drawLine(x, dropY, x - 4, dropY + 12);
            }
        }
    }

    private void drawWind(Graphics2D graphics, int width, int height, int direction) {
        int dx = 0;
        int dy = 0;
        switch (direction) {
            case 0: dy = -1; break; // North
            case 1: dx = 1; break;  // East
            case 2: dy = 1; break;  // South
            case 3: dx = -1; break; // West
            default: return;
        }
        graphics.setColor(new Color(235, 250, 255, 210));
        graphics.setStroke(new BasicStroke(2.0f));
        int offset = (this.animationFrame * 4) % 60;
        for (int x = -60; x < width + 60; x += 60) {
            for (int y = -60; y < height + 60; y += 60) {
                int tipX = x + 6 + dx * offset;
                int tipY = y + 6 + dy * offset;
                graphics.drawLine(tipX - dx * 26, tipY - dy * 26, tipX, tipY);
                graphics.drawLine(tipX, tipY,
                    tipX - dx * 7 - dy * 5, tipY - dy * 7 + dx * 5);
                graphics.drawLine(tipX, tipY,
                    tipX - dx * 7 + dy * 5, tipY - dy * 7 - dx * 5);
            }
        }
    }

    private void drawLightning(Graphics2D graphics) {
        for (Position strike : this.simulation.getLastLightningStrikes()) {
            int x = strike.getColumn() * this.cellSize + this.cellSize / 2;
            int y = strike.getRow() * this.cellSize + this.cellSize / 2;
            // Short bolts end at the real target, including cells on the top edge.
            int top = Math.max(0, y - 80);
            int length = y - top;
            int[] xs = {x - 8, x + 5, x - 4, x};
            int[] ys = {top, top + length / 3, top + length * 2 / 3, y};
            int alpha = this.animationFrame % 6 < 3 ? 230 : 140;
            graphics.setColor(new Color(255, 225, 60, alpha));
            graphics.setStroke(new BasicStroke(6.0f));
            graphics.drawPolyline(xs, ys, xs.length);
            graphics.drawOval(x - 8, y - 8, 16, 16);
            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(2.0f));
            graphics.drawPolyline(xs, ys, xs.length);
        }
    }

    private void drawGrid(Graphics graphics, Grid<Cell> grid) {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                Cell cell = grid.getCell(row, column);

                drawCell(graphics, cell, row, column);
            }
        }
    }

    private void drawCell(Graphics graphics, Cell cell, int row, int column) {
        int x = column * this.cellSize;
        int y = row * this.cellSize;

        graphics.setColor(getCellColor(cell));

        graphics.fillRect(x, y, this.cellSize, this.cellSize);

        graphics.setColor(Color.DARK_GRAY);

        graphics.drawRect(x, y, this.cellSize - 1, this.cellSize - 1);
    }

    private Color getCellColor(Cell cell) {
        if (cell == null) {
            return Color.WHITE;
        }

        if (cell.isBurning()) {
            int intensity = cell.getFire().getIntensity();

            if (intensity >= 8) {
                return Color.RED;
            } else if (intensity >= 4) {
                return Color.ORANGE;
            } else {
                return Color.YELLOW;
            }
        }

        Terrain terrain = cell.getTerrain();

        if (terrain instanceof Burnable) {
            Burnable burnable = (Burnable) terrain;

            if (burnable.isBurnedOut()) {
                return Color.DARK_GRAY;
            }
        }

        if (terrain instanceof Tree) {
            return new Color(34, 110, 45);
        }

        if (terrain instanceof Grass) {
            return new Color(120, 190, 70);
        }

        if (terrain instanceof River) {
            return new Color(50, 140, 220);
        }

        return Color.LIGHT_GRAY;
    }
}
