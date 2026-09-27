package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import org.lwjgl.glfw.GLFW;

public final class NoHurtCam extends Module {

    public NoHurtCam() {
        super("No Hurt Cam", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }
}
