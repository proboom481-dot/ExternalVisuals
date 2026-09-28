package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;

public class CustomCrosshair extends Module {

    public enum CrosshairStyle {
        PLUS,
        DOT,
        CIRCLE,
        T_SHAPE,
        CROSS
    }

    private final SliderSetting size =
            addSetting(
                    new SliderSetting(
                            "Size",
                            6.0,
                            2.0,
                            16.0,
                            1.0
                    )
            );

    private final SliderSetting gap =
            addSetting(
                    new SliderSetting(
                            "Gap",
                            3.0,
                            0.0,
                            12.0,
                            1.0
                    )
            );

    private final SliderSetting thickness =
            addSetting(
                    new SliderSetting(
                            "Thickness",
                            2.0,
                            1.0,
                            5.0,
                            1.0
                    )
            );

    private final ColorSetting color =
            addSetting(
                    new ColorSetting(
                            "Color",
                            0xFFFFFFFF
                    )
            );

    private final BooleanSetting centerDot =
            addSetting(
                    new BooleanSetting(
                            "Center Dot",
                            false
                    )
            );

    private final StringSetting styleSetting =
            addSetting(
                    new StringSetting(
                            "Style",
                            "PLUS",
                            "PLUS",
                            "DOT",
                            "CIRCLE",
                            "T_SHAPE",
                            "CROSS"
                    )
            );


    private final BooleanSetting dynamic = addSetting(new BooleanSetting("Dynamic Spread", true));
    private final SliderSetting sprintSpread = addSetting(new SliderSetting("Sprint Spread", 3.0, 0.0, 10.0, 0.5));
    private final SliderSetting attackSpread = addSetting(new SliderSetting("Attack Spread", 2.0, 0.0, 8.0, 0.5));
    private final BooleanSetting outline = addSetting(new BooleanSetting("Outline", true));
    private final ColorSetting outlineColor = addSetting(new ColorSetting("Outline Color", 0xFF101018));
    private final ColorSetting hitColor = addSetting(new ColorSetting("Hit Color", 0xFFFF4D67));
    private final BooleanSetting hitFlash = addSetting(new BooleanSetting("Hit Flash", true));
    private final BooleanSetting pulse = addSetting(new BooleanSetting("Pulse", false));
    private final SliderSetting pulseSpeed = addSetting(new SliderSetting("Pulse Speed", 3.0, 0.5, 10.0, 0.5));

    public CustomCrosshair() {
        super(
                "CustomCrosshair",
                ModuleCategory.VISUALS,
                0
        );
    }

    public int getSize() {
        return (int) Math.round(
                size.getValue()
        );
    }

    public int getGap() {
        return (int) Math.round(
                gap.getValue()
        );
    }

    public int getEffectiveGap() {
        int base = getGap();
        if (!dynamic.isEnabled() || mc.player == null) return base;
        double extra = 0.0;
        if (mc.player.isSprinting()) extra += sprintSpread.getValue();
        if (mc.options.keyAttack.isPressed()) extra += attackSpread.getValue();
        return Math.max(0, (int) Math.round(base + extra));
    }

    public int getThickness() {
        return (int) Math.round(
                thickness.getValue()
        );
    }

    public int getColor() {
        return color.getColor();
    }

    public boolean isCenterDotEnabled() {
        return centerDot.isEnabled();
    }

    public CrosshairStyle getStyle() {

        try {
            return CrosshairStyle.valueOf(
                    styleSetting.getValue()
                            .toUpperCase()
            );
        } catch (Exception ignored) {
            return CrosshairStyle.PLUS;
        }
    }

    public void setStyle(
            CrosshairStyle style
    ) {

        if (style == null) {
            return;
        }

        styleSetting.setValue(
                style.name()
        );
    }

    public SliderSetting getSizeSetting() {
        return size;
    }

    public SliderSetting getGapSetting() {
        return gap;
    }

    public SliderSetting getThicknessSetting() {
        return thickness;
    }

    public boolean isDynamicEnabled() { return dynamic.isEnabled(); }
    public float getSprintSpread() { return sprintSpread.getValue().floatValue(); }
    public float getAttackSpread() { return attackSpread.getValue().floatValue(); }
    public boolean isOutlineEnabled() { return outline.isEnabled(); }
    public int getOutlineColor() { return outlineColor.getColor(); }
    public int getHitColor() { return hitColor.getColor(); }
    public boolean isHitFlashEnabled() { return hitFlash.isEnabled(); }
    public boolean isPulseEnabled() { return pulse.isEnabled(); }
    public float getPulseSpeed() { return pulseSpeed.getValue().floatValue(); }

    public ColorSetting getColorSetting() {
        return color;
    }

    public BooleanSetting getCenterDotSetting() {
        return centerDot;
    }

    public StringSetting getStyleSetting() {
        return styleSetting;
    }
}