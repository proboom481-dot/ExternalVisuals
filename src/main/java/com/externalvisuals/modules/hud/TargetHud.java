package com.externalvisuals.modules.hud;

import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public final class TargetHud extends HudModule {

    private final SliderSetting range =
            addSetting(new SliderSetting(
                    "Range", 16.0, 4.0, 64.0, 1.0
            ));

    public TargetHud() {
        super("Target HUD", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 92.0f);
    }

    public double getRange() {
        return Math.max(1.0, range.getValue());
    }

    public SliderSetting getRangeSetting() {
        return range;
    }
}
