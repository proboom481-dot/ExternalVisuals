package com.externalvisuals.modules.hud;

import org.lwjgl.glfw.GLFW;

public final class ServerHud extends HudModule {
    public ServerHud() {
        super("Server", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 172.0f);
    }

    public String getText() {
        if (mc.isInSingleplayer()) return "Server: Singleplayer";
        if (mc.getCurrentServerEntry() == null) return "Server: Unknown";
        String address = mc.getCurrentServerEntry().address;
        return "Server: " + (address == null || address.isEmpty() ? "Unknown" : address);
    }
}
