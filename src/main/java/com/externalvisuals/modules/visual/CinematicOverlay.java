package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

/** Lightweight screen-space polish layer. It intentionally uses only HUD primitives. */
public final class CinematicOverlay extends Module {
    private final BooleanSetting vignette = addSetting(new BooleanSetting("Vignette", true));
    private final SliderSetting vignetteStrength = addSetting(new SliderSetting("Vignette Strength", 0.22, 0.0, 0.8, 0.01));
    private final BooleanSetting letterbox = addSetting(new BooleanSetting("Letterbox", false));
    private final SliderSetting letterboxSize = addSetting(new SliderSetting("Letterbox Size", 0.04, 0.01, 0.16, 0.01));
    private final BooleanSetting focus = addSetting(new BooleanSetting("Center Focus", true));
    private final SliderSetting focusSize = addSetting(new SliderSetting("Focus Size", 0.18, 0.05, 0.45, 0.01));
    private final SliderSetting focusStrength = addSetting(new SliderSetting("Focus Strength", 0.12, 0.0, 0.4, 0.01));
    private final BooleanSetting targetPulse = addSetting(new BooleanSetting("Target Pulse", true));
    private final SliderSetting targetPulseSpeed = addSetting(new SliderSetting("Target Pulse Speed", 5.0, 0.5, 12.0, 0.5));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF8D6BFF));

    public CinematicOverlay() {
        super("Cinematic Overlay", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public boolean isVignette() { return vignette.isEnabled(); }
    public float getVignetteStrength() { return vignetteStrength.getValue().floatValue(); }
    public boolean isLetterbox() { return letterbox.isEnabled(); }
    public float getLetterboxSize() { return letterboxSize.getValue().floatValue(); }
    public boolean isFocus() { return focus.isEnabled(); }
    public float getFocusSize() { return focusSize.getValue().floatValue(); }
    public float getFocusStrength() { return focusStrength.getValue().floatValue(); }
    public boolean isTargetPulse() { return targetPulse.isEnabled(); }
    public float getTargetPulseSpeed() { return targetPulseSpeed.getValue().floatValue(); }
    public int getColor() { return color.getColor(); }
}
