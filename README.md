# Forest Fire Simulation

A Java Swing application that models how fire spreads across a randomly generated
forest. The map contains trees, grass and rivers, while a heat map controls when
burnable cells ignite. Rain, heatwaves, wind and lightning can be added while the
simulation is running.

## Getting started

1. The application opens with a random **50 x 50** forest and starts running.
   Simulation updates are scheduled every **500 milliseconds**. Initially there
   are no fires or active weather effects.
2. Click **Pause** to set up the map. To change its size, enter **Rows** and
   **Columns**, then click **Create New Map**.
3. **Left-click** a green tree or grass cell to start a fire. **Right-click** a
   cell to inspect it without igniting it.
4. Choose a **Weather** type and enter **Strength** and **Duration**. For Wind,
   choose a direction; for Lightning, enter a strike count. Click **Add Weather**
   to apply these settings.
5. Click **Start** to watch the fire spread. The bottom of the window shows
   active weather, its remaining duration and live forest statistics.
6. Use **Pause** to inspect the forest, **Clear Weather** to remove weather
   effects, or **Reset Map** to generate a fresh forest.

## Controls

### Map controls

- **Rows / Columns** set the size of a new map. Each value must be from 1 to 100.
- **Create New Map** generates a random map with the entered dimensions.
- **Reset Map** generates a new random layout using the current map's dimensions.
  To apply a different size entered in the fields, use **Create New Map**.
- **Pause / Start** stops or resumes simulation updates and visual animation.

Creating or resetting a map clears existing fires, accumulated heat, active
weather and the selected cell. The running or paused state and the **Flying
embers** checkbox selection are preserved. The window adjusts to the map size;
use the scrollbars when the whole map cannot fit on the screen.

### Weather controls

Choose a weather type, enter its strength and duration, then select
**Add Weather**. Duration is measured in simulation updates, not seconds. Multiple
weather effects can be active at the same time and are applied in the order they
were added. Pausing also pauses their remaining duration.

| Input | Allowed values |
| --- | --- |
| **Strength** | A whole number of `0` or greater. |
| **Duration** | A whole number greater than `0`, measured in updates. |
| **Direction** | North, East, South or West; used only by Wind. |
| **Strikes/update** | A whole number greater than `0`; used only by Lightning. |

- **Rain** removes heat from every position.
- **Heatwave** adds heat to every position.
- **Wind** carries heat from burning cells toward the selected direction.
- **Lightning** heats up to the selected number of different burnable cells that
  are not already burning per update. Fewer strikes occur if there are too few
  suitable cells.
- **Clear Weather** removes all active weather effects and their visual overlays
  without resetting the map, existing fires or accumulated heat.

Direction is enabled only for Wind, and strikes per update is enabled only for
Lightning. Invalid values are rejected with an error message.

### Flying embers

- **Flying embers** is off initially. Turn it on to let active Wind carry heat
  from burning vegetation to burnable cells three positions downwind. Orange
  trails show the actual ember paths on the map.
- Wind strength determines the heat deposited by each ember. Landing cells ignite
  only when their accumulated heat reaches the ignition threshold of **100**, after
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
- To edit a cell, select it, enter the new values and click **Apply Cell Changes**.
- **Burn rate** can be edited for trees and grass and must be a positive whole
  number.
- **Moisture** can be edited for trees and grass and must be a number from `0`
  to `1`, including both endpoints.
- **Density** can be edited for grass and must be a whole number of `0` or greater.

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