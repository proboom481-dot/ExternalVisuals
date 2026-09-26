package com.externalvisuals.modules.hud;

import org.lwjgl.glfw.GLFW;

public final class BiomeHud extends HudModule {
    public BiomeHud() {
        super("Biome", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 194.0f);
    }

    public String getText() {
        if (mc.player == null || mc.world == null) return "Biome: Unknown";
        String category = mc.world.getBiome(mc.player.getBlockPos()).getCategory().getName();
        return "Biome: " + category;
    }
}
