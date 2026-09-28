package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;

/**
 * Lightweight HUD polish layer. All effects are client-side and render-only.
 */
public final class AdvancedVisuals extends Module {

    private final BooleanSetting vignette = addSetting(new BooleanSetting("Vignette", true));
    private final ColorSetting vignetteColor = addSetting(new ColorSetting("Vignette Color", 0xFF9B5CFF));
    private final SliderSetting vignetteStrength = addSetting(new SliderSetting("Vignette Strength", 0.22, 0.05, 0.65, 0.01));

    private final BooleanSetting lowHpVignette = addSetting(new BooleanSetting("Low HP Vignette", true));
    private final SliderSetting lowHpThreshold = addSetting(new SliderSetting("Low HP Threshold", 30, 5, 90, 1));
    private final ColorSetting lowHpColor = addSetting(new ColorSetting("Low HP Color", 0xFFFF365C));

    private final BooleanSetting hitFlash = addSetting(new BooleanSetting("Hit Flash", true));
    private final SliderSetting hitFlashDuration = addSetting(new SliderSetting("Hit Flash Duration", 180, 60, 600, 20));
    private final ColorSetting hitFlashColor = addSetting(new ColorSetting("Hit Flash Color", 0xFFFF365C));

    private final BooleanSetting targetArrow = addSetting(new BooleanSetting("Target Arrow", true));
    private final ColorSetting targetArrowColor = addSetting(new ColorSetting("Target Arrow Color", 0xFFFF4D67));
    private final SliderSetting targetArrowRadius = addSetting(new SliderSetting("Target Arrow Radius", 52, 24, 110, 1));

    private final BooleanSetting crosshairDot = addSetting(new BooleanSetting("Crosshair Dot", false));
    private final ColorSetting crosshairDotColor = addSetting(new ColorSetting("Crosshair Dot Color", 0xFFFFFFFF));
    private final SliderSetting crosshairDotSize = addSetting(new SliderSetting("Crosshair Dot Size", 2, 1, 5, 1));
    private final BooleanSetting crosshairGap = addSetting(new BooleanSetting("Crosshair Gap", true));
    private final SliderSetting crosshairGapSize = addSetting(new SliderSetting("Crosshair Gap Size", 5, 2, 14, 1));

    private final BooleanSetting infoStrip = addSetting(new BooleanSetting("Info Strip", true));
    private final BooleanSetting showFps = addSetting(new BooleanSetting("FPS", true));
    private final BooleanSetting showCoordinates = addSetting(new BooleanSetting("Coordinates", true));
    private final BooleanSetting showDirection = addSetting(new BooleanSetting("Direction", true));
    private final BooleanSetting showTarget = addSetting(new BooleanSetting("Target", true));
    private final BooleanSetting showArmor = addSetting(new BooleanSetting("Armor", true));

    private long hitFlashUntil;
    private float lastHealth = -1.0f;

    public AdvancedVisuals() {
        super("Advanced Visuals", ModuleCategory.VISUALS, 0);
    }

    @Override
    public void onEnable() {
        hitFlashUntil = 0L;
        lastHealth = mc.player == null ? -1.0f : mc.player.getHealth();
    }

    @Override
    public void onDisable() {
        hitFlashUntil = 0L;
        lastHealth = -1.0f;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        float health = mc.player.getHealth();
        if (lastHealth >= 0.0f && health < lastHealth && hitFlash.isEnabled()) {
            hitFlashUntil = System.currentTimeMillis() + hitFlashDuration.getValue().longValue();
        }
        lastHealth = health;
    }

    public boolean isVignetteEnabled() { return vignette.isEnabled(); }
    public int getVignetteColor() { return vignetteColor.getColor(); }
    public float getVignetteStrength() { return vignetteStrength.getValue().floatValue(); }

    public boolean isLowHpVignetteEnabled() { return lowHpVignette.isEnabled(); }
    public float getLowHpThreshold() { return lowHpThreshold.getValue().floatValue(); }
    public int getLowHpColor() { return lowHpColor.getColor(); }

    public boolean isHitFlashEnabled() { return hitFlash.isEnabled(); }
    public int getHitFlashColor() { return hitFlashColor.getColor(); }
    public float getHitFlashAlpha() {
        if (!hitFlash.isEnabled() || hitFlashUntil <= 0L) return 0.0f;
        double left = hitFlashUntil - System.currentTimeMillis();
        return (float)Math.max(0.0, Math.min(1.0, left / Math.max(1.0, hitFlashDuration.getValue())));
    }

    public boolean isTargetArrowEnabled() { return targetArrow.isEnabled(); }
    public int getTargetArrowColor() { return targetArrowColor.getColor(); }
    public int getTargetArrowRadius() { return getInt(targetArrowRadius); }

    public boolean isCrosshairDotEnabled() { return crosshairDot.isEnabled(); }
    public int getCrosshairDotColor() { return crosshairDotColor.getColor(); }
    public int getCrosshairDotSize() { return getInt(crosshairDotSize); }
    public boolean isCrosshairGapEnabled() { return crosshairGap.isEnabled(); }
    public int getCrosshairGapSize() { return getInt(crosshairGapSize); }

    public boolean isInfoStripEnabled() { return infoStrip.isEnabled(); }
    public boolean showFps() { return showFps.isEnabled(); }
    public boolean showCoordinates() { return showCoordinates.isEnabled(); }
    public boolean showDirection() { return showDirection.isEnabled(); }
    public boolean showTarget() { return showTarget.isEnabled(); }
    public boolean showArmor() { return showArmor.isEnabled(); }

    private int getInt(SliderSetting setting) {
        return Math.max(1, setting.getValue().intValue());
    }
}
