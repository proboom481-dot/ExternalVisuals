package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public final class TrajectoryPrediction extends Module {
    private final SliderSetting range = addSetting(new SliderSetting("Range", 32.0, 8.0, 64.0, 1.0));
    private final SliderSetting steps = addSetting(new SliderSetting("Steps", 48.0, 12.0, 100.0, 1.0));
    private final SliderSetting gravity = addSetting(new SliderSetting("Gravity", 0.05, 0.0, 0.12, 0.005));
    private final SliderSetting velocity = addSetting(new SliderSetting("Velocity", 2.6, 0.5, 5.0, 0.1));
    private final BooleanSetting bow = addSetting(new BooleanSetting("Bow / Projectile", true));
    private final BooleanSetting pearl = addSetting(new BooleanSetting("Ender Pearl", true));
    private final BooleanSetting potion = addSetting(new BooleanSetting("Potion", true));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF55D7FF));

    public TrajectoryPrediction() {
        super("Trajectory Prediction", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public double getRange() { return Math.max(1.0, range.getValue()); }
    public int getSteps() { return Math.max(8, steps.getValue().intValue()); }
    public double getGravity() { return Math.max(0.0, gravity.getValue()); }
    public double getVelocity() { return Math.max(0.1, velocity.getValue()); }
    public int getColor() { return color.getColor(); }
    public boolean isBowEnabled() { return bow.isEnabled(); }
    public boolean isPearlEnabled() { return pearl.isEnabled(); }
    public boolean isPotionEnabled() { return potion.isEnabled(); }
}
