package com.externalvisuals.modules.hud;

import org.lwjgl.glfw.GLFW;

public final class SpeedHud extends HudModule {
    public SpeedHud() {
        super("Speed", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 150.0f);
    }

    public String getText() {
        if (mc.player == null) return "Speed: 0.0 km/h";
        double vx = mc.player.getVelocity().x;
        double vz = mc.player.getVelocity().z;
        double blocksPerSecond = Math.sqrt(vx * vx + vz * vz) * 20.0;
        double kmh = blocksPerSecond * 3.6;
        return String.format("Speed: %.1f km/h", kmh);
    }
}
