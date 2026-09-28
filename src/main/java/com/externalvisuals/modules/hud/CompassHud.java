package com.externalvisuals.modules.hud;

import com.externalvisuals.module.ModuleCategory;

public final class CompassHud extends HudModule {
    public CompassHud() { super("Compass HUD", 0, 180, 6); }
    public String getText() {
        if (mc.player == null) return "Compass";
        float yaw = mc.player.getYaw(1.0f) % 360.0f;
        if (yaw < 0) yaw += 360.0f;
        String dir;
        if (yaw >= 337.5 || yaw < 22.5) dir = "S";
        else if (yaw < 67.5) dir = "SW";
        else if (yaw < 112.5) dir = "W";
        else if (yaw < 157.5) dir = "NW";
        else if (yaw < 202.5) dir = "N";
        else if (yaw < 247.5) dir = "NE";
        else if (yaw < 292.5) dir = "E";
        else dir = "SE";
        return "◈ " + dir + "  " + Math.round(yaw) + "°";
    }
}
