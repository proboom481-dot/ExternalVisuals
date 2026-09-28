package com.externalvisuals.modules.hud;

import org.lwjgl.glfw.GLFW;

public final class FPSHud extends HudModule {

    public FPSHud() {
        super("FPS", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 6.0f);
    }

    public String getText() {
        return "FPS: " + getFPS();
    }

    public int getFPS() {
        if (mc == null) {
            return 0;
        }

        String debugString = mc.fpsDebugString;

        if (debugString == null || debugString.isEmpty()) {
            return 0;
        }

        int fps = 0;
        boolean started = false;

        for (int i = 0; i < debugString.length(); i++) {
            char c = debugString.charAt(i);

            if (c >= '0' && c <= '9') {
                started = true;
                fps = fps * 10 + (c - '0');
            } else if (started) {
                break;
            }
        }

        return fps;
    }
}
