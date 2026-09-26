package com.externalvisuals.modules.hud;

import org.lwjgl.glfw.GLFW;

public final class MemoryHud extends HudModule {
    public MemoryHud() {
        super("Memory", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 216.0f);
    }

    public String getText() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
        long max = runtime.maxMemory() / (1024L * 1024L);
        return "RAM: " + used + " / " + max + " MB";
    }
}
