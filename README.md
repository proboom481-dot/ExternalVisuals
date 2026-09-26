# ExternalVisuals 1.7.1 — Pulse-style Visual / PvP Client

ExternalVisuals is a client-side visual/PvP enhancement mod for **Minecraft 1.16.5 + Fabric/Yarn**.

## 1.7.1 pass

- ESP hardening: render budget, adaptive detail, safe text rendering, trail memory budget, and renderer recovery without disabling ESP after a single bad render call.
- ESP target info now includes a **visible totem counter** above players (main/offhand stacks synchronized to the client).
- **Auto Totem**: server-authoritative hotbar→offhand swap, emergency conditions, health/fall/fire/void guards and fail-safe automation.
- **Elytra Swapper**: hotbar→chest swap plus optional swap-back to a chestplate.
- **Optimization**: vanilla performance profile, adaptive ESP budgets, trail budgets, artificial-render budget, memory guards and **Sodium compatibility detection**.
- Sodium is an **optional external mod**; ExternalVisuals does not bundle Sodium. `fabric.mod.json` declares it as a suggestion and the optimizer stays compatible when Sodium is installed.
- **Cosmetics**: procedural client-side cape, halo and wing cosmetics with distance/budget limits. They use lightweight line/box geometry rather than expensive texture uploads.
- **Music**: local/resource-pack OGG player with loop, volume, pitch, stream and fail-safe controls.
- **372 granular settings** across the current module set.

## Music track

The Music module contains a slot named `VORUEM_ALCOHOL_SLOWED`. The requested copyrighted track is **not bundled** because no licensed audio file was supplied. To use a copy you are licensed to use, place it at:

`src/main/resources/assets/externalvisuals/sounds/music/voruem_alcohol_slowed.ogg`

See `docs/MUSIC.md`.

## Secret Menu

The Secret Menu still contains only:

- ESP
- Aim Assist

ESP and Aim Assist remain hidden from the normal module list/search.

## Compatibility target

- Minecraft **1.16.5**
- Fabric Loader **0.12.x**
- Fabric API **0.42.0+1.16**
- Yarn **1.16.5+build.10**
- Java **8 bytecode target** (works with modern Java runtimes supported by the launcher)

## Build status

The source was statically checked for Java-file balance and module/settings registration. A local Gradle executable is not available in the build environment used to prepare this archive, so **GitHub Actions remains the authoritative compile test**.


## 1.7.1 Pulse-style visual pass

- Dynamic Island HUD with configurable health/FPS/combo/module/target sections.
- Screen Pulse, Low HP Pulse, Damage Tint, Hit Direction, Combo Counter and Kill Feed.
- Weapon Trail, Sprint Trail, Dynamic Crosshair, Critical Burst, Blood-style dust and Kill Effects.
- Explicit SETTINGS buttons in the normal and Secret menus.
- Secret Menu remains hidden from the normal module list; ESP and Aim Assist stay there.
- Optimization no longer changes vanilla render distance.
