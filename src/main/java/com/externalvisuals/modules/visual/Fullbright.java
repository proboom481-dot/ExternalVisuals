package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {

    private double previousGamma;

    public Fullbright() {
        super("Fullbright", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onEnable() {
        previousGamma = mc.options.gamma;
        mc.options.gamma = 16.0;
    }

    @Override
    public void onDisable() {
        mc.options.gamma = previousGamma;
    }

    @Override
    public void onTick() {
        if (mc.options.gamma < 16.0) {
            mc.options.gamma = 16.0;
        }
    }
}