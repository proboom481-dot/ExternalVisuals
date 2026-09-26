package com.externalvisuals.modules.hud;

import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public final class TargetHud extends HudModule {

    private final SliderSetting range =
            addSetting(new SliderSetting(
                    "Range", 16.0, 4.0, 64.0, 1.0
            ));


    private final BooleanSetting healthBar = addSetting(new BooleanSetting("Health Bar", true));
    private final BooleanSetting armor = addSetting(new BooleanSetting("Armor", true));
    private final BooleanSetting distance = addSetting(new BooleanSetting("Distance", true));
    private final BooleanSetting heldItem = addSetting(new BooleanSetting("Held Item", true));
    private final BooleanSetting head = addSetting(new BooleanSetting("Head Icon", true));
    private final BooleanSetting blur = addSetting(new BooleanSetting("Glass Blur", true));
    private final ColorSetting accent = addSetting(new ColorSetting("Accent", 0xFF9B5CFF));
    private final ColorSetting healthColor = addSetting(new ColorSetting("Health Color", 0xFF55E86B));
    private final SliderSetting scale = addSetting(new SliderSetting("Scale", 1.0, 0.6, 1.6, 0.05));

    public TargetHud() {
        super("Target HUD", GLFW.GLFW_KEY_UNKNOWN, 6.0f, 92.0f);
    }

    public double getRange() {
        return Math.max(1.0, range.getValue());
    }

    public SliderSetting getRangeSetting() { return range; }
    public boolean isHealthBarEnabled() { return healthBar.isEnabled(); }
    public boolean isArmorEnabled() { return armor.isEnabled(); }
    public boolean isDistanceEnabled() { return distance.isEnabled(); }
    public boolean isHeldItemEnabled() { return heldItem.isEnabled(); }
    public boolean isHeadEnabled() { return head.isEnabled(); }
    public boolean isBlurEnabled() { return blur.isEnabled(); }
    public int getAccent() { return accent.getColor(); }
    public int getHealthColor() { return healthColor.getColor(); }
    public float getTargetScale() { return Math.max(0.6f, scale.getValue().floatValue()); }
}
