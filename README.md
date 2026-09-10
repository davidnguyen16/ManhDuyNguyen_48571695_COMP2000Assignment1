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

### Cell controls

- **Left-click** a cell to select it and attempt to ignite it.
- **Right-click** a cell to inspect it without starting a fire.
- Selected vegetation displays its age and remaining fuel.
- Burn rate and moisture can be edited for trees and grass.
- Density can be edited for grass. Moisture must be between `0` and `1`, burn
  rate must be positive, and density cannot be negative.

Rivers cannot burn. Their cooling strength reduces heat in the river cell and
nearby cells.

## Map colours

- Dark green: tree
- Light green: grass
- Blue: river
- Yellow, orange or red: burning cell, from lower to higher intensity
- Dark grey: burned-out vegetation