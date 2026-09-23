# ExternalVisuals 1.4.4

ExternalVisuals is a Fabric client-side visual/PvP utility project for Minecraft 1.16.5.

## Included systems

- AMOLED glass ClickGUI with animated cards and category navigation
- Searchable module list
- Per-module settings with toggles, sliders, colors and string modes
- UI scale from 50% to 150% with `-` / `+`
- Five-click secret menu
- Three persistent profiles
- Panic disable utility
- ESP boxes, health bars and tracers
- Visual-only hitboxes (no gameplay bounding-box modification)
- Aim Assist with range, FOV, speed and attack-key control
- TNT fuse timer with seconds/ticks and dynamic colors
- Damage numbers with fade, critical colors and limits
- Hitmarker, hit particles and hit effects
- Zoom, Fullbright, No Hurt Cam and Auto Sprint
- FPS, coordinates, session, keystrokes and target HUD
- Ping, CPS, clock, potion effects, held-item counter, reach display and watermark HUDs
- Custom crosshair styles
- Client-side JSON configuration and named profiles

## Build

GitHub Actions uses Java 17 and Gradle 7.4.2. The local AndroidIDE environment does not need to build the project directly.

The produced jar is written to `build/libs/`.

## Controls

- Right Shift: open ClickGUI
- Left click module: toggle
- `+` button or right click module: open settings
- Enter: toggle the selected module
- Escape: close settings / screen
- `-` and `+`: UI scale 50%-150%
- Five clicks on the ExternalVisuals title within two seconds: Secret Menu


## 1.4.2 stability pass

- World renderers use Fabric's shared `WorldRenderContext.consumers()` buffer.
- ESP uses a dedicated no-depth line layer without changing entity hitboxes.
- Damage Numbers, TNT Timer and Hitbox no longer flush the shared world buffer.
- Confirmed damage drives hitmarker/particle/effect feedback.
- Aim Assist supports players and living mobs with visibility/attack-key controls.
- Color settings have an interactive hue/value picker and animated Rainbow mode.
- ClickGUI scale persists while reopening the GUI.
- ClickGUI text uses italic styling and smoother entrance/hover animation.
- FPS HUD exposes X/Y/Scale/Background/Color settings.


## HUD Editor
Open ClickGUI → **HUD EDITOR**. Drag HUD components to reposition them, use the mouse wheel over a component to change its scale, and right-click to reset its position and scale. Every HUD component supports position, scale, background, border, shadow and color controls.


## 1.4.4 visual additions

- Speed / Server / Biome / Memory HUD modules.
- Target Ring with animated rotation.
- Block Outline for the selected block.
- All new HUD modules are editable from the existing HUD Editor.
