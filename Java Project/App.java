import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public class App {
    private JFrame frame;
    private ForestFireSimulation simulation;
    private PaintPanel paintPanel;
    private Timer timer;
    private Timer animationTimer;

    private JTextField rowsField;
    private JTextField columnsField;
    private JButton startPauseButton;
    private JButton createMapButton;
    private JButton resetMapButton;
    private JButton applyWeatherButton;
    private JButton clearWeatherButton;
    private JLabel statusLabel;
    private JComboBox<String> weatherBox;
    private JComboBox<String> windDirectionBox;
    private JTextField weatherStrengthField;
    private JTextField weatherDurationField;
    private JTextField lightningStrikesField;

    private Position selectedPosition;
    private JLabel selectedCellLabel;
    private JTextField burnRateField;
    private JTextField moistureField;
    private JTextField densityField;
    private JButton applyCellChangesButton;

    public App() {
        this.simulation = new ForestFireSimulation(50, 50, new Random());

        this.paintPanel = new PaintPanel(this.simulation);

        this.frame = new JFrame("Forest Fire Simulation");

        this.rowsField = new JTextField("50", 4);

        this.columnsField = new JTextField("50", 4);

        this.startPauseButton = new JButton("Pause");

        this.statusLabel = new JLabel("Paused - Weather: None");

        this.createMapButton = new JButton("Create New Map");
        this.resetMapButton = new JButton("Reset Map");
        this.resetMapButton.setToolTipText("Generate a new forest with the current map size");

        this.weatherBox = new JComboBox<String>(
                new String[] {
                        "Rain",
                        "Heatwave",
                        "Lightning",
                        "Wind"
                });

        this.windDirectionBox = new JComboBox<String>(
                new String[] {
                        "North",
                        "East",
                        "South",
                        "West"
                });
        this.windDirectionBox.setEnabled(false);

        this.weatherStrengthField = new JTextField("8", 3);
        this.weatherDurationField = new JTextField("20", 3);
        this.lightningStrikesField = new JTextField("3", 3);
        this.lightningStrikesField.setEnabled(false);

        this.applyWeatherButton = new JButton("Add Weather");
        this.clearWeatherButton = new JButton("Clear Weather");
        this.clearWeatherButton.setToolTipText(
            "Remove all weather and visual effects without resetting the forest"
        );

        this.selectedPosition = null;
        this.selectedCellLabel = new JLabel("Selected Cell: None");
        this.burnRateField = new JTextField(3);
        this.moistureField = new JTextField(4);
        this.densityField = new JTextField(3);
        this.applyCellChangesButton = new JButton("Apply Cell Changes");
        setCellEditorEnabled(false, false);

        this.timer = new Timer(
                500,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        App.this.simulation.update();
                        App.this.paintPanel.repaint();
                        App.this.updateStatus();
                    }
                });

        this.animationTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (App.this.timer.isRunning()) {
                    App.this.paintPanel.advanceAnimation();
                }
            }
        });

        configureFrame();

        configureActions();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        App app = new App();
                        app.showApp();
                    }
                });
    }

    private void configureFrame() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                App.this.timer.stop();
                App.this.animationTimer.stop();
            }
        });

        this.frame.setLayout(new BorderLayout(8, 8));

        JPanel mapControlPanel = new JPanel(new FlowLayout());

        mapControlPanel.add(new JLabel("Rows:"));
        mapControlPanel.add(this.rowsField);

        mapControlPanel.add(new JLabel("Columns:"));
        mapControlPanel.add(this.columnsField);

        mapControlPanel.add(this.createMapButton);
        mapControlPanel.add(this.resetMapButton);
        mapControlPanel.add(this.startPauseButton);

        JPanel weatherControlPanel = new JPanel(new FlowLayout());

        weatherControlPanel.add(new JLabel("Weather:"));
        weatherControlPanel.add(this.weatherBox);
        weatherControlPanel.add(new JLabel("Direction:"));
        weatherControlPanel.add(this.windDirectionBox);
        weatherControlPanel.add(new JLabel("Strength:"));
        weatherControlPanel.add(this.weatherStrengthField);
        weatherControlPanel.add(new JLabel("Duration:"));
        weatherControlPanel.add(this.weatherDurationField);
        weatherControlPanel.add(new JLabel("Strikes/update:"));
        weatherControlPanel.add(this.lightningStrikesField);
        weatherControlPanel.add(this.applyWeatherButton);
        weatherControlPanel.add(this.clearWeatherButton);

        JPanel cellEditorPanel = new JPanel(new FlowLayout());
        cellEditorPanel.add(this.selectedCellLabel);
        cellEditorPanel.add(new JLabel("Burn rate:"));
        cellEditorPanel.add(this.burnRateField);
        cellEditorPanel.add(new JLabel("Moisture:"));
        cellEditorPanel.add(this.moistureField);
        cellEditorPanel.add(new JLabel("Density:"));
        cellEditorPanel.add(this.densityField);
        cellEditorPanel.add(this.applyCellChangesButton);

        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        controlPanel.add(mapControlPanel);
        controlPanel.add(weatherControlPanel);
        controlPanel.add(cellEditorPanel);

        JScrollPane scrollPane = new JScrollPane(this.paintPanel);

        // Keep the map at its natural size, even when the controls are wider.
        JPanel mapContainer = new JPanel(new BorderLayout()) {
            @Override
            public void doLayout() {
                fitMapViewport(this, scrollPane);
            }
        };
        mapContainer.add(scrollPane, BorderLayout.CENTER);

        this.frame.add(controlPanel, BorderLayout.NORTH);

        this.frame.add(mapContainer, BorderLayout.CENTER);

        this.frame.add(this.statusLabel, BorderLayout.SOUTH);

        fitWindowToGrid();
        this.frame.setLocationRelativeTo(null);
    }

    private void fitMapViewport(JPanel container, JScrollPane scrollPane) {
        Dimension gridSize = this.paintPanel.getPreferredSize();
        Insets border = scrollPane.getInsets();
        int width = gridSize.width + border.left + border.right;
        int height = gridSize.height + border.top + border.bottom;
        // Leave room for scrollbars when the screen cannot fit the whole grid.
        boolean needsVerticalScroll = container.getHeight() < height;
        boolean needsHorizontalScroll = container.getWidth() < width;
        if (needsVerticalScroll) {
            width += scrollPane.getVerticalScrollBar().getPreferredSize().width;
        }
        if (needsHorizontalScroll) {
            height += scrollPane.getHorizontalScrollBar().getPreferredSize().height;
        }
        width = Math.min(width, container.getWidth());
        height = Math.min(height, container.getHeight());
        scrollPane.setBounds(
            (container.getWidth() - width) / 2,
            (container.getHeight() - height) / 2,
            width, height
        );
    }

    private void fitWindowToGrid() {
        this.frame.pack();
        Rectangle screen = this.frame.getGraphicsConfiguration().getBounds();
        Insets margins = Toolkit.getDefaultToolkit().getScreenInsets(
            this.frame.getGraphicsConfiguration()
        );
        int availableWidth = screen.width - margins.left - margins.right;
        int availableHeight = screen.height - margins.top - margins.bottom;
        this.frame.setSize(
            Math.min(this.frame.getWidth(), availableWidth),
            Math.min(this.frame.getHeight(), availableHeight)
        );
        this.frame.setLocation(
            Math.max(screen.x + margins.left,
                Math.min(this.frame.getX(), screen.x + screen.width - margins.right - this.frame.getWidth())),
            Math.max(screen.y + margins.top,
                Math.min(this.frame.getY(), screen.y + screen.height - margins.bottom - this.frame.getHeight()))
        );
        this.frame.validate();
    }

    private void configureActions() {
        this.startPauseButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        toggleSimulation();
                    }
                });

        this.createMapButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        createNewMap();
                    }
                });

        this.resetMapButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        resetMap();
                    }
                });

        this.weatherBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        String selectedWeather = (String) App.this.weatherBox
                                .getSelectedItem();

                        configureWeatherInputs(selectedWeather);
                    }
                });

        this.applyWeatherButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        applySelectedWeather();
                    }
                });

        this.clearWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                clearWeather();
            }
        });

        this.applyCellChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                applySelectedCellChanges();
            }
        });

        this.paintPanel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent event) {

                        handleGridClick(event);
                    }
                });
    }

    private void toggleSimulation() {
        if (this.timer.isRunning()) {
            this.timer.stop();
            this.startPauseButton.setText("Start");
            updateStatus();
        } else {
            this.timer.start();
            this.startPauseButton.setText("Pause");
            updateStatus();
        }
    }

    private void createNewMap() {
        boolean timerWasRunning = this.timer.isRunning();

        this.timer.stop();

        try {
            int rows = Integer.parseInt(this.rowsField.getText().trim());

            int columns = Integer.parseInt(this.columnsField.getText().trim());

            if (rows > 100 || columns > 100) {
                throw new IllegalArgumentException(
                        "Rows and columns cannot exceed 100.");
            }

            replaceMap(rows, columns);

            this.statusLabel.setText(
                    "New map created: " + rows + " x " + columns);

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this.frame,
                    "Rows and columns must be whole numbers.",
                    "Invalid input",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this.frame,
                    exception.getMessage(),
                    "Invalid map size",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            if (timerWasRunning) {
                this.timer.start();
            }
        }
    }

    private void resetMap() {
        int rows = this.simulation.getGrid().getRows();
        int columns = this.simulation.getGrid().getColumns();
        boolean timerWasRunning = this.timer.isRunning();
        this.timer.stop();

        try {
            replaceMap(rows, columns);
        } finally {
            if (timerWasRunning) {
                this.timer.start();
            }
            updateStatus();
        }
    }

    private void replaceMap(int rows, int columns) {
        ForestFireSimulation newSimulation =
            new ForestFireSimulation(rows, columns, new Random());
        this.simulation = newSimulation;
        this.paintPanel.setSimulation(newSimulation);
        this.rowsField.setText(Integer.toString(rows));
        this.columnsField.setText(Integer.toString(columns));
        this.weatherBox.setSelectedItem("Rain");
        clearCellSelection();
        fitWindowToGrid();
    }

    private void applySelectedWeather() {
        String selectedWeather = (String) this.weatherBox.getSelectedItem();
        try {
            int strength = Integer.parseInt(this.weatherStrengthField.getText().trim());
            int duration = Integer.parseInt(this.weatherDurationField.getText().trim());
            if (strength < 0 || duration <= 0) {
                throw new IllegalArgumentException(
                    "Strength must be non-negative and duration must be positive."
                );
            }

            Weather weather;
            switch (selectedWeather) {
                case "Rain":
                    weather = new Rain(strength, duration);
                    break;
                case "Heatwave":
                    weather = new Heatwave(strength, duration);
                    break;
                case "Lightning":
                    int strikes = Integer.parseInt(
                        this.lightningStrikesField.getText().trim()
                    );
                    weather = new Lightning(
                        strength, duration, strikes, new Random()
                    );
                    break;
                case "Wind":
                    weather = new Wind(
                        strength, duration,
                        this.windDirectionBox.getSelectedIndex()
                    );
                    break;
                default:
                    throw new IllegalStateException("Unknown Weather selection");
            }

            this.simulation.addWeather(weather);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this.frame,
                "Weather values must be whole numbers.",
                "Invalid Weather values",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this.frame,
                exception.getMessage(),
                "Invalid Weather values",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        this.paintPanel.repaint();
        updateStatus();
    }

    private void configureWeatherInputs(String selectedWeather) {
        boolean isWind = "Wind".equals(selectedWeather);
        boolean isLightning = "Lightning".equals(selectedWeather);
        this.windDirectionBox.setEnabled(isWind);
        this.lightningStrikesField.setEnabled(isLightning);
    }

    private void clearWeather() {
        this.simulation.clearWeather();
        this.paintPanel.repaint();
        updateStatus();
    }

    private void handleGridClick(MouseEvent event) {
        Position position = this.paintPanel.getGridPositionAt(event.getX(), event.getY());

        if (position == null) {
            return;
        }

        selectCell(position);

        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        int row = position.getRow();
        int column = position.getColumn();

        Cell cell = this.simulation.getGrid().getCell(row, column);

        if (cell.isBurning()) {
            this.statusLabel.setText(
                    "This Cell is already burning.");

            return;
        }

        if (!cell.canBurn()) {
            this.statusLabel.setText(
                    "This Cell cannot burn.");

            return;
        }

        this.simulation.igniteCell(
                row,
                column,
                10);

        this.paintPanel.repaint();

        this.statusLabel.setText("Fire started at row " + row + ", column " + column);
    }

    private void selectCell(Position position) {
        this.selectedPosition = position;
        Terrain terrain = this.simulation.getGrid().getCell(
            position.getRow(), position.getColumn()
        ).getTerrain();

        if (terrain instanceof Vegetation) {
            Vegetation vegetation = (Vegetation) terrain;
            this.burnRateField.setText(Integer.toString(vegetation.getBurnRate()));
            this.moistureField.setText(Double.toString(vegetation.getMoisture()));

            boolean isGrass = terrain instanceof Grass;
            if (isGrass) {
                this.densityField.setText(
                    Integer.toString(((Grass) terrain).getDensity())
                );
            } else {
                this.densityField.setText("");
            }
            setCellEditorEnabled(true, isGrass);
        } else {
            this.burnRateField.setText("");
            this.moistureField.setText("");
            this.densityField.setText("");
            setCellEditorEnabled(false, false);
        }

        refreshSelectedCellSummary();
    }

    private void clearCellSelection() {
        this.selectedPosition = null;
        this.selectedCellLabel.setText("Selected Cell: None");
        this.burnRateField.setText("");
        this.moistureField.setText("");
        this.densityField.setText("");
        setCellEditorEnabled(false, false);
    }

    private void setCellEditorEnabled(boolean vegetation, boolean grass) {
        this.burnRateField.setEnabled(vegetation);
        this.moistureField.setEnabled(vegetation);
        this.densityField.setEnabled(grass);
        this.applyCellChangesButton.setEnabled(vegetation);
    }

    private void applySelectedCellChanges() {
        if (this.selectedPosition == null) {
            return;
        }

        Terrain terrain = this.simulation.getGrid().getCell(
            this.selectedPosition.getRow(), this.selectedPosition.getColumn()
        ).getTerrain();
        if (!(terrain instanceof Vegetation)) {
            return;
        }

        try {
            int burnRate = Integer.parseInt(this.burnRateField.getText().trim());
            double moisture = Double.parseDouble(this.moistureField.getText().trim());
            int density = 0;
            if (terrain instanceof Grass) {
                density = Integer.parseInt(this.densityField.getText().trim());
            }

            if (burnRate <= 0) {
                throw new IllegalArgumentException("Burn rate must be positive.");
            }
            if (!Double.isFinite(moisture) || moisture < 0.0 || moisture > 1.0) {
                throw new IllegalArgumentException(
                    "Moisture must be a number between 0 and 1."
                );
            }
            if (density < 0) {
                throw new IllegalArgumentException("Density cannot be negative.");
            }

            Vegetation vegetation = (Vegetation) terrain;
            vegetation.setBurnRate(burnRate);
            vegetation.setMoisture(moisture);
            if (terrain instanceof Grass) {
                ((Grass) terrain).setDensity(density);
            }

            refreshSelectedCellSummary();
            this.paintPanel.repaint();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this.frame,
                "Cell values must contain valid numbers.",
                "Invalid Cell values",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this.frame,
                exception.getMessage(),
                "Invalid Cell values",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshSelectedCellSummary() {
        if (this.selectedPosition == null) {
            return;
        }

        Cell cell = this.simulation.getGrid().getCell(
            this.selectedPosition.getRow(), this.selectedPosition.getColumn()
        );
        Terrain terrain = cell.getTerrain();
        StringBuilder summary = new StringBuilder("Cell (")
            .append(this.selectedPosition.getRow())
            .append(", ")
            .append(this.selectedPosition.getColumn())
            .append("): ")
            .append(terrain.getClass().getSimpleName());

        if (terrain instanceof Vegetation) {
            Vegetation vegetation = (Vegetation) terrain;
            summary.append(" | age=").append(vegetation.getAge())
                .append(", fuel=").append(vegetation.getFuel());
            if (terrain instanceof Tree) {
                summary.append(", height=").append(((Tree) terrain).getHeight());
            }
        } else if (terrain instanceof River) {
            summary.append(" | cooling=")
                .append(((River) terrain).getCoolingStrength());
        }

        this.selectedCellLabel.setText(summary.toString());
    }

    private void showApp() {
        this.frame.setVisible(true);
        this.timer.start();
        this.animationTimer.start();
        updateStatus();
    }

    private void updateStatus() {
        refreshSelectedCellSummary();
        StringBuilder text = new StringBuilder(
            this.timer.isRunning() ? "Running - Weather: " : "Paused - Weather: "
        );
        java.util.List<Weather> effects = this.simulation.getActiveWeatherEffects();
        if (effects.isEmpty()) {
            text.append("None");
        } else {
            for (int index = 0; index < effects.size(); index++) {
                if (index > 0) {
                    text.append(", ");
                }
                Weather weather = effects.get(index);
                text.append(weather.getClass().getSimpleName());
                if (weather instanceof Wind) {
                    text.append(" ").append(this.windDirectionBox.getItemAt(
                        ((Wind) weather).getDirection()
                    ));
                } else if (weather instanceof Lightning) {
                    text.append(" x")
                        .append(((Lightning) weather).getStrikesPerUpdate())
                        .append("/update");
                }
                text.append(" (").append(weather.getDuration()).append(")");
            }
        }
        this.statusLabel.setText(text.toString());
        this.statusLabel.setToolTipText(text.toString());
    }
}
