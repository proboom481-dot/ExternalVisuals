package com.externalvisuals.modules.cosmetics;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;

public final class Cosmetics extends Module {
    private final BooleanSetting cape = addSetting(new BooleanSetting("Cape", true));
    private final BooleanSetting selfOnly = addSetting(new BooleanSetting("Self Only", true));
    private final BooleanSetting friendsOnly = addSetting(new BooleanSetting("Friends Only", false));
    private final StringSetting capeStyle = addSetting(new StringSetting("Cape Style", "SOLID", "SOLID", "DOUBLE", "WAVE", "ROYAL"));
    private final ColorSetting capeColor = addSetting(new ColorSetting("Cape Color", 0xFF9B5CFF));
    private final ColorSetting capeSecondColor = addSetting(new ColorSetting("Cape Secondary", 0xFF42D9FF));
    private final SliderSetting capeWidth = addSetting(new SliderSetting("Cape Width", 0.42, 0.15, 1.0, 0.01));
    private final SliderSetting capeHeight = addSetting(new SliderSetting("Cape Height", 1.0, 0.4, 1.8, 0.05));
    private final SliderSetting capeOffset = addSetting(new SliderSetting("Cape Offset", 0.30, 0.05, 0.8, 0.01));
    private final SliderSetting capeWave = addSetting(new SliderSetting("Cape Wave", 0.12, 0.0, 0.5, 0.01));
    private final BooleanSetting capeGlow = addSetting(new BooleanSetting("Cape Glow", true));
    private final SliderSetting glowAlpha = addSetting(new SliderSetting("Glow Alpha", 0.22, 0.05, 0.7, 0.01));
    private final BooleanSetting halo = addSetting(new BooleanSetting("Halo", false));
    private final ColorSetting haloColor = addSetting(new ColorSetting("Halo Color", 0xFF42D9FF));
    private final SliderSetting haloRadius = addSetting(new SliderSetting("Halo Radius", 0.42, 0.15, 1.2, 0.01));
    private final SliderSetting haloHeight = addSetting(new SliderSetting("Halo Height", 2.15, 1.2, 3.5, 0.05));
    private final SliderSetting haloSpeed = addSetting(new SliderSetting("Halo Speed", 1.0, 0.1, 4.0, 0.1));
    private final BooleanSetting wings = addSetting(new BooleanSetting("Wings", false));
    private final ColorSetting wingColor = addSetting(new ColorSetting("Wing Color", 0xFFE45CFF));
    private final SliderSetting wingScale = addSetting(new SliderSetting("Wing Scale", 0.8, 0.2, 2.0, 0.05));
    private final BooleanSetting particles = addSetting(new BooleanSetting("Cosmetic Particles", false));
    private final SliderSetting particleRate = addSetting(new SliderSetting("Particle Rate", 4, 1, 20, 1));
    private final SliderSetting particleRange = addSetting(new SliderSetting("Particle Range", 24, 4, 64, 1));
    private final BooleanSetting nameplate = addSetting(new BooleanSetting("Cosmetic Nameplate", false));
    private final BooleanSetting pulse = addSetting(new BooleanSetting("Pulse", true));
    private final SliderSetting pulseSpeed = addSetting(new SliderSetting("Pulse Speed", 2.0, 0.1, 8.0, 0.1));
    private final BooleanSetting rainbow = addSetting(new BooleanSetting("Rainbow", false));
    private final SliderSetting rainbowSpeed = addSetting(new SliderSetting("Rainbow Speed", 0.6, 0.1, 3.0, 0.1));
    private final BooleanSetting backfaceSafe = addSetting(new BooleanSetting("Backface Safe", true));
    private final BooleanSetting lowFpsSafe = addSetting(new BooleanSetting("Low FPS Safe", true));
    private final SliderSetting maxEntities = addSetting(new SliderSetting("Max Cosmetic Entities", 16, 1, 64, 1));

    public Cosmetics() { super("Cosmetics", ModuleCategory.COSMETICS, 0); }
    public boolean showCape() { return cape.isEnabled(); }
    public boolean selfOnly() { return selfOnly.isEnabled(); }
    public String getCapeStyle() { return capeStyle.getValue(); }
    public int getCapeColor() { return capeColor.getColor(); }
    public int getCapeSecondColor() { return capeSecondColor.getColor(); }
    public float getCapeWidth() { return capeWidth.getValue().floatValue(); }
    public float getCapeHeight() { return capeHeight.getValue().floatValue(); }
    public float getCapeOffset() { return capeOffset.getValue().floatValue(); }
    public float getCapeWave() { return capeWave.getValue().floatValue(); }
    public boolean showHalo() { return halo.isEnabled(); }
    public int getHaloColor() { return haloColor.getColor(); }
    public float getHaloRadius() { return haloRadius.getValue().floatValue(); }
    public float getHaloHeight() { return haloHeight.getValue().floatValue(); }
    public float getHaloSpeed() { return haloSpeed.getValue().floatValue(); }
    public boolean showWings() { return wings.isEnabled(); }
    public int getWingColor() { return wingColor.getColor(); }
    public float getWingScale() { return wingScale.getValue().floatValue(); }
    public boolean isFriendsOnly() { return friendsOnly.isEnabled(); }
    public boolean isCapeGlowEnabled() { return capeGlow.isEnabled(); }
    public float getGlowAlpha() { return glowAlpha.getValue().floatValue(); }
    public boolean isParticleEnabled() { return particles.isEnabled(); }
    public int getParticleRate() { return Math.max(1, particleRate.getValue().intValue()); }
    public double getParticleRange() { return Math.max(1.0, particleRange.getValue()); }
    public boolean isNameplateEnabled() { return nameplate.isEnabled(); }
    public boolean isPulseEnabled() { return pulse.isEnabled(); }
    public float getPulseSpeed() { return pulseSpeed.getValue().floatValue(); }
    public boolean isRainbowEnabled() { return rainbow.isEnabled(); }
    public float getRainbowSpeed() { return rainbowSpeed.getValue().floatValue(); }
    public boolean isLowFpsSafe() { return lowFpsSafe.isEnabled(); }
    public int getMaxEntities() { return Math.max(1, maxEntities.getValue().intValue()); }

    public int animatedColor(int baseColor, double phase) {
        int alpha = (baseColor >>> 24) & 255;
        float hue = (float) ((System.currentTimeMillis() * 0.001 * rainbowSpeed.getValue() + phase) % 1.0);
        if (hue < 0) hue += 1.0f;
        if (!rainbow.isEnabled()) return baseColor;
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.82f, 1.0f) & 0x00FFFFFF;
        return (alpha << 24) | rgb;
    }
}
