# ExternalVisuals 1.7.0 — Pulse-style visual pass

This pass is an original implementation inspired by publicly documented Pulse-style PvP visual UX. It does not copy proprietary implementation code.

## Included
- Pulse Visuals module with 46 configurable settings.
- Dynamic Island HUD: HP, FPS, combo, enabled-module count and target name.
- Screen pulse / low-HP pulse / damage tint.
- Hit direction indicator.
- Combo counter and kill feed.
- Weapon trail and sprint trail.
- Dynamic target-aware crosshair.
- Critical burst and optional blood-style dust burst.
- Critical flash and kill effects.
- Existing ESP, Aim Assist, Hit Particles, Hit Effects, Damage Numbers, Hitmarker, Target HUD, Trajectory and HUD modules retained.
- Secret Menu retained; ESP and Aim Assist remain hidden from the normal module list.
- Explicit SETTINGS buttons remain in both normal and Secret Menu.
- Vanilla render distance is not modified by Optimization.
- Secret Menu saves configuration when closed.

## Verification performed here
- 100 Java source files.
- 47 registered modules.
- 46 settings on Pulse Visuals.
- Parenthesis/brace/bracket balance: 0 mismatches.
- Removed legacy `getCurrentFps()`, `getInventory()`, and no-arg `getYaw()` calls.
- Removed Optimization references to vanilla `viewDistance`.
- ZIP integrity checked with `unzip -t`.

A full Fabric/Loom compile still needs to be confirmed by the project's GitHub Actions because this environment does not contain the Minecraft/Fabric dependency cache.
