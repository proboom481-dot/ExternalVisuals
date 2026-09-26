# ExternalVisuals 1.6.0 — final polish audit

- Invisible ESP alert: configurable `Invisible Alert`; invisible entities receive a high-contrast `!!!` marker with yellow background and black text instead of the normal info overlay.
- Removed remaining suspicious 1.16.5 API calls found by source scan (`getCurrentFps()`, `getInventory()`, no-arg `getYaw()`).
- No TODO/FIXME/UnsupportedOperationException markers found in Java sources during final source scan.
- `git diff --check` clean for the final changes.
- 98 Java source files present.
- Distribution padding is documentation-only and is not referenced by runtime code.

A full Fabric/Loom build still needs to be executed in the user's GitHub Actions environment because this environment does not contain the Minecraft/Fabric dependency cache.
