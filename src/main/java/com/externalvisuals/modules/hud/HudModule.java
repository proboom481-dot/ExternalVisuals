package com.externalvisuals.modules.hud;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;

public abstract class HudModule extends Module {

    private final SliderSetting xSetting =
            addSetting(new SliderSetting("X", 6.0, 0.0, 3840.0, 1.0));

    private final SliderSetting ySetting =
            addSetting(new SliderSetting("Y", 6.0, 0.0, 2160.0, 1.0));

    private final SliderSetting scaleSetting =
            addSetting(new SliderSetting("Scale", 1.0, 0.50, 3.0, 0.05));

    private final BooleanSetting backgroundSetting =
            addSetting(new BooleanSetting("Background", true));

    private final BooleanSetting borderSetting =
            addSetting(new BooleanSetting("Border", true));

    private final BooleanSetting shadowSetting =
            addSetting(new BooleanSetting("Shadow", true));

    private final ColorSetting colorSetting =
            addSetting(new ColorSetting("Color", 0xFFF5F3FA));

    protected HudModule(
            String name,
            int key,
            float defaultX,
            float defaultY
    ) {
        super(name, ModuleCategory.HUD, key);
        setHudPosition(defaultX, defaultY);
    }

    public float getX() {
        return xSetting.getValue().floatValue();
    }

    public float getY() {
        return ySetting.getValue().floatValue();
    }

    public float getScale() {
        return Math.max(0.50f, scaleSetting.getValue().floatValue());
    }

    public void setX(float x) {
        xSetting.setSliderValue(Math.max(0.0f, Math.min(3840.0f, x)));
    }

    public void setY(float y) {
        ySetting.setSliderValue(Math.max(0.0f, Math.min(2160.0f, y)));
    }

    public void setScale(float scale) {
        scaleSetting.setSliderValue(
                Math.max(0.50f, Math.min(3.0f, scale))
        );
    }

    public void setHudPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public SliderSetting getXSetting() {
        return xSetting;
    }

    public SliderSetting getYSetting() {
        return ySetting;
    }

    public SliderSetting getScaleSetting() {
        return scaleSetting;
    }

    public BooleanSetting getBackgroundSetting() {
        return backgroundSetting;
    }

    public BooleanSetting getBorderSetting() {
        return borderSetting;
    }

    public BooleanSetting getShadowSetting() {
        return shadowSetting;
    }

    public ColorSetting getColorSetting() {
        return colorSetting;
    }

    public boolean isBackgroundEnabled() {
        return backgroundSetting.isEnabled();
    }

    public boolean isBorderEnabled() {
        return borderSetting.isEnabled();
    }

    public boolean isShadowEnabled() {
        return shadowSetting.isEnabled();
    }

    public int getColor() {
        return colorSetting.getColor();
    }
}
