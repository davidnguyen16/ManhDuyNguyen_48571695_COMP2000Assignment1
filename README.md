# Forest Fire Simulation

A Java Swing application that models how fire spreads across a randomly generated
forest. The map contains trees, grass and rivers, while a heat map controls when
burnable cells ignite. Rain, heatwaves, wind and lightning can be added while the
simulation is running.

## Controls

### Map controls

- **Rows / Columns** set the size of a new map. Each value must be from 1 to 100.
- **Create New Map** generates a map with the entered dimensions.
- **Reset Map** generates a fresh map using the current dimensions.
- **Pause / Start** stops or resumes simulation updates and visual animation.

### Weather controls

Choose a Weather type, enter its strength and duration, then select
**Add Weather**. Duration is measured in simulation updates, not seconds. Multiple
Weather effects can be active at the same time.

- **Rain** removes heat from every position.
- **Heatwave** adds heat to every position.
- **Wind** carries heat from burning cells toward the selected direction.
- **Lightning** heats a configurable number of random burnable cells per update.
- **Clear Weather** removes all active Weather effects without resetting the map,
  existing fires or accumulated heat.

Direction is enabled only for Wind, and strikes per update is enabled only for
Lightning. Invalid values are rejected with an error message.

### Flying embers

- **Flying embers** is off initially. Turn it on to let active Wind carry heat
  from burning vegetation to burnable cells three positions downwind. Orange
  trails show the actual ember paths on the map.
- Wind strength determines the heat deposited by each ember. Landing cells ignite
  only when their accumulated heat reaches the usual ignition threshold, after
  weather and river cooling. Embers can therefore start a separate fire beyond a
  narrow river, but a landing does not guarantee ignition.
- The checkbox affects existing Wind immediately. Turning it off stops new ember
  heat and clears its trails; heat and fires already created remain. Its selection
  is preserved when creating or resetting a map.

To observe this on the map, pause, select **Wind / East**, set strength to **20**
and duration to **20**, and add the weather. Enable flying embers and ignite
vegetation with another burnable cell three columns to its right. Start the
simulation and watch the orange trails. The forest layout, nearby rivers and
surrounding fires affect whether a second fire forms.

### Cell controls

- **Left-click** a cell to select it and attempt to ignite it.
- **Right-click** a cell to inspect it without starting a fire.
- Selected vegetation displays its age and remaining fuel.
- Burn rate and moisture can be edited for trees and grass.
- Density can be edited for grass. Moisture must be between `0` and `1`, burn
  rate must be positive, and density cannot be negative.

Rivers cannot burn. Their cooling strength reduces heat in the river cell and
nearby cells.

### Live forest statistics

The bottom of the window shows three values, refreshed after each simulation
update, manual ignition and map reset:

- **Burning** counts trees and grass currently on fire.
- **Out of fuel** counts trees and grass with zero fuel. An extinguished cell
  can still have fuel, so it does not always belong in this count.
- **Fuel remaining** totals the fuel in all trees and grass, including burning
  cells. Rivers are excluded from the statistics.

## Map colours

- Dark green: tree
- Light green: grass
- Blue: river
- Yellow, orange or red: burning cell, from lower to higher intensity
- Dark grey: burned-out vegetation

## Design notes

`Weather` holds strength and remaining duration, and delegates each update to a
`WeatherEffectStrategy`. Rain, Heatwave, Wind and Lightning implement that
interface. `WeatherManager` keeps the effects in insertion order and removes
expired weather. The GUI selects the strategy when adding weather.

Strategy separates the choice of weather behaviour from its shared lifetime.
The previous inheritance design was valid, but coupled each behaviour to the
Weather superclass. State was considered, but these effects are selected by the
user rather than transitions within one weather lifecycle. Decorator would be
useful for wrapping behaviours; the existing manager already combines separate
effects without nesting them.

Rain and Heatwave use parallel streams over rows. Each task writes only its own
row, and completes before the next weather effect starts. Wind and Lightning
remain sequential because sources can share target cells. Simulation updates and
GUI reads run on Swing's event dispatch thread; worker tasks do not update Swing
components. This preserves weather order, including rain's zero-heat clamp.

Live statistics use a custom collector with a private accumulator for each stream
partition and a separate result object. The GUI collects after each complete update;
the collector does not change cells or share one mutable accumulator between
parallel workers.
