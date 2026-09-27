package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public final class LowHpWarning extends Module {
    private final SliderSetting threshold = addSetting(new SliderSetting("Threshold", 6.0, 1.0, 19.0, 0.5));
    private final SliderSetting intensity = addSetting(new SliderSetting("Intensity", 0.65, 0.1, 1.0, 0.05));
    private final SliderSetting speed = addSetting(new SliderSetting("Pulse Speed", 4.0, 0.5, 10.0, 0.5));
    private final BooleanSetting heartbeat = addSetting(new BooleanSetting("Heartbeat", true));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFFFF3030));

    public LowHpWarning() {
        super("Low HP Warning", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public float getThreshold() { return threshold.getValue().floatValue(); }
    public float getIntensity() { return intensity.getValue().floatValue(); }
    public float getSpeed() { return speed.getValue().floatValue(); }
    public boolean isHeartbeat() { return heartbeat.isEnabled(); }
    public int getColor() { return color.getColor(); }
}
