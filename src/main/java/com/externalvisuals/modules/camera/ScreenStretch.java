package com.externalvisuals.modules.camera;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side horizontal projection stretch.
 *
 * This does not change the actual window resolution. It changes the camera
 * projection so the scene is rendered wider, which is the useful in-game
 * "stretched" visual effect for PvP layouts.
 */
public final class ScreenStretch extends Module {

    private final SliderSetting horizontalScale =
            addSetting(
                    new SliderSetting(
                            "Horizontal Stretch",
                            1.15,
                            1.0,
                            1.75,
                            0.05
                    )
            );

    public ScreenStretch() {
        super(
                "Screen Stretch",
                ModuleCategory.CAMERA,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    public float getHorizontalScale() {
        return Math.max(
                1.0f,
                Math.min(
                        1.75f,
                        horizontalScale.getValue().floatValue()
                )
        );
    }

    public SliderSetting getHorizontalScaleSetting() {
        return horizontalScale;
    }
}
