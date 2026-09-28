package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;

/**
 * Extra combat-oriented HUD visuals. Render-only; it never changes combat input.
 */
public final class CombatVisuals extends Module {
    private final BooleanSetting targetCard = addSetting(new BooleanSetting("Target Card", true));
    private final BooleanSetting targetHealth = addSetting(new BooleanSetting("Target Health", true));
    private final BooleanSetting targetDistance = addSetting(new BooleanSetting("Target Distance", true));
    private final BooleanSetting targetArmor = addSetting(new BooleanSetting("Target Armor", true));
    private final BooleanSetting targetPulse = addSetting(new BooleanSetting("Target Pulse", true));
    private final ColorSetting targetColor = addSetting(new ColorSetting("Target Color", 0xFF9B5CFF));
    private final BooleanSetting hurtDirection = addSetting(new BooleanSetting("Hurt Direction", true));
    private final ColorSetting hurtColor = addSetting(new ColorSetting("Hurt Direction Color", 0xFFFF365C));
    private final SliderSetting hurtSize = addSetting(new SliderSetting("Hurt Direction Size", 42, 18, 90, 1));
    private final BooleanSetting attackCooldown = addSetting(new BooleanSetting("Attack Cooldown", true));
    private final ColorSetting cooldownColor = addSetting(new ColorSetting("Cooldown Color", 0xFF42D9FF));
    private final BooleanSetting sprintIndicator = addSetting(new BooleanSetting("Sprint Indicator", true));
    private final BooleanSetting positionPill = addSetting(new BooleanSetting("Position Pill", false));
    private final BooleanSetting directionCompass = addSetting(new BooleanSetting("Direction Compass", true));
    private final BooleanSetting fpsGraph = addSetting(new BooleanSetting("FPS Graph", false));
    private final SliderSetting graphLength = addSetting(new SliderSetting("FPS Graph Length", 40, 16, 80, 1));
    private final BooleanSetting watermark = addSetting(new BooleanSetting("Watermark", false));
    private final ColorSetting accentColor = addSetting(new ColorSetting("Accent Color", 0xFF9B5CFF));
    private final BooleanSetting chroma = addSetting(new BooleanSetting("Chroma Accent", false));
    private final SliderSetting chromaSpeed = addSetting(new SliderSetting("Chroma Speed", 0.08, 0.01, 0.5, 0.01));

    public CombatVisuals() {
        super("Combat Visuals", ModuleCategory.VISUALS, 0);
    }

    public boolean showTargetCard() { return targetCard.isEnabled(); }
    public boolean showTargetHealth() { return targetHealth.isEnabled(); }
    public boolean showTargetDistance() { return targetDistance.isEnabled(); }
    public boolean showTargetArmor() { return targetArmor.isEnabled(); }
    public boolean showTargetPulse() { return targetPulse.isEnabled(); }
    public int getTargetColor() { return targetColor.getColor(); }
    public boolean showHurtDirection() { return hurtDirection.isEnabled(); }
    public int getHurtColor() { return hurtColor.getColor(); }
    public int getHurtSize() { return hurtSize.getValue().intValue(); }
    public boolean showAttackCooldown() { return attackCooldown.isEnabled(); }
    public int getCooldownColor() { return cooldownColor.getColor(); }
    public boolean showSprintIndicator() { return sprintIndicator.isEnabled(); }
    public boolean showPositionPill() { return positionPill.isEnabled(); }
    public boolean showDirectionCompass() { return directionCompass.isEnabled(); }
    public boolean showFpsGraph() { return fpsGraph.isEnabled(); }
    public int getGraphLength() { return Math.max(16, graphLength.getValue().intValue()); }
    public boolean showWatermark() { return watermark.isEnabled(); }
    public int getAccentColor() {
        if (!chroma.isEnabled()) return accentColor.getColor();
        float hue = (System.currentTimeMillis() * chromaSpeed.getValue().floatValue() / 1000.0f) % 1.0f;
        if (hue < 0.0f) hue += 1.0f;
        return 0xFF000000 | hsbToRgb(hue, 0.88f, 1.0f);
    }

    private int hsbToRgb(float hue, float saturation, float brightness) {
        float h = (hue - (float)Math.floor(hue)) * 6.0f;
        int sector = (int)Math.floor(h);
        float fraction = h - sector;
        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * fraction);
        float t = brightness * (1.0f - saturation * (1.0f - fraction));
        float r, g, b;
        switch (sector) {
            case 0: r = brightness; g = t; b = p; break;
            case 1: r = q; g = brightness; b = p; break;
            case 2: r = p; g = brightness; b = t; break;
            case 3: r = p; g = q; b = brightness; break;
            case 4: r = t; g = p; b = brightness; break;
            default: r = brightness; g = p; b = q; break;
        }
        return ((int)(r * 255.0f) << 16) | ((int)(g * 255.0f) << 8) | (int)(b * 255.0f);
    }
}
