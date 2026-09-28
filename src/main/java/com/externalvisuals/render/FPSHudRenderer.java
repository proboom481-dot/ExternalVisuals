package com.externalvisuals.render;

public final class FPSHudRenderer {

    private FPSHudRenderer() {
    }

    public static void init() {
        // FPS is rendered by ExtraHudRenderer so every HUD module
        // shares the same position/scale/background/border/color system.
    }
}
