# ExternalVisuals Changelog

## 1.4.4 — HUD + Visual Expansion

### HUD
- Added Speed HUD.
- Added Server HUD.
- Added Biome HUD.
- Added Memory/RAM HUD.
- All new HUD modules inherit the shared X/Y/Scale/Background/Border/Shadow/Color settings.
- New HUD modules are automatically available in the existing HUD Editor.

### Visuals
- Added Target Ring with animated rotation, range, radius, speed and color settings.
- Added Block Outline for the block under the crosshair.
- Both world visuals use the existing shared no-depth line layer and the Fabric world-render context consumer path.

### Stability
- New world visuals fail-safe: if their renderer throws, only that module is disabled.
- No entity hitboxes or world collision data are modified.
