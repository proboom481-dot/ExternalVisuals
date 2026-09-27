# ExternalVisuals 1.7.2

## Aim Assist
- Added sticky target lock so close targets do not drop during small FOV changes.
- Added `FOV Check` toggle; disabled by default so nearby targets can be acquired even when they enter from the side.
- Added `Target Lock` and `Lock Duration` settings.
- Strengthened teammate filtering using Minecraft's teammate check plus matching scoreboard teams.
- Fixed visibility logic so `Visible Only` and `Through Walls` behave independently and predictably.
- Added a public current-target accessor for visual target highlighting.

## ESP / Wallhack
- ESP entities are now sorted by distance before the render budget is applied.
- This prevents far entities from consuming the budget and making nearby players/mobs appear to lose ESP.
- Aim Assist target can now receive the same target highlight, ring, pulse box and beam treatment.
- Existing no-depth ESP layer remains in use for wall-visible outlines/tracers.

## GUI
- Added a compact live status indicator showing active and total visible modules.
- Preserved the AMOLED/glass UI, animated cards, settings buttons, search and Secret Menu.

## Version
- ExternalVisuals: 1.7.2
- Minecraft: 1.16.5
