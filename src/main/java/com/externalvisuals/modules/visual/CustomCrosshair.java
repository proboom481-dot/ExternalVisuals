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