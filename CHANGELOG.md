# ExternalVisuals 1.7.1

- Reworked Aim Assist target discovery to query live world entities every tick.
- Added teammate filtering, wall targeting, prediction, priority modes, and aim modes.
- Added Pulse target box/ring/beam effects and detailed armor durability information to ESP.
- Added Target HUD armor details, durability bars, and pulsing accent.
- Added Pulse hit marker and target armor information to Dynamic Island.
- Fixed Pulse Visuals weapon/sprint trail setting-vs-buffer name collisions.
- Preserved Minecraft 1.16.5/Yarn-compatible APIs.

# Changelog

## 1.6.0 — Ultimate Function Pass

- Hardened ESP rendering and removed the renderer behavior that disabled ESP after a render exception.
- Added adaptive ESP budgets, safe info rendering and trail memory limits.
- Added visible Totem Count above player targets.
- Added Auto Totem with fail-safe hotbar/offhand swapping and emergency guards.
- Added Elytra Swapper with optional swap-back to chestplate.
- Added Optimization module with vanilla FPS/render controls, artificial-render budgets and Sodium compatibility detection.
- Added lightweight procedural Cosmetics: cape, halo and wings.
- Added Music module and a local OGG slot for `VORUEM_ALCOHOL_SLOWED`.
- Added Audio and Optimization GUI categories.
- Expanded the project to 372 granular settings.

## 1.5.1

- Visual polish and additional safe settings.

## 1.5.0

- Ultimate visual pass, advanced ESP, Target Ring, HUD and world controls.


## 1.7.1 — Pulse-style visual pass
- Added Dynamic Island and configurable HUD sections.
- Added Pulse damage tint, critical burst, blood-style dust, combo, kill feed and hit direction polish.
- Kept Secret Menu and explicit SETTINGS access in both menus.
- Removed Optimization's vanilla render-distance control.
- Fixed player-damage callback parameter and GUI settings-card syntax.
