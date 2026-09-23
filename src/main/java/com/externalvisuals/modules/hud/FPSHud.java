package com.externalvisuals.modules.hud;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import org.lwjgl.glfw.GLFW;

public class FPSHud extends Module {

    private float x = 5.0f;
    private float y = 5.0f;

    private float scale = 1.0f;

    private int color = 0xFFFFFFFF;

    public FPSHud() {
        super(
                "FPS",
                ModuleCategory.HUD,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    public String getText() {
        return "FPS: " + getFPS();
    }

    public int getFPS() {

        if (mc == null) {
            return 0;
        }

        String debugString =
                mc.fpsDebugString;

        if (debugString == null
                || debugString.isEmpty()) {
            return 0;
        }

        /*
         * В Minecraft 1.16.5 fpsDebugString
         * начинается с текущего FPS.
         *
         * Пример:
         * "144 fps T: 60 vsync fast ..."
         */
        int length =
                debugString.length();

        int fps = 0;

        for (int i = 0; i < length; i++) {

            char character =
                    debugString.charAt(i);

            if (character >= '0'
                    && character <= '9') {

                fps =
                        fps * 10
                                + (character - '0');

            } else {

                if (i > 0) {
                    break;
                }
            }
        }

        return fps;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = Math.max(
                0.0f,
                x
        );
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = Math.max(
                0.0f,
                y
        );
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Math.max(
                0.5f,
                Math.min(
                        3.0f,
                        scale
                )
        );
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return color & 0xFF;
    }

    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }
}