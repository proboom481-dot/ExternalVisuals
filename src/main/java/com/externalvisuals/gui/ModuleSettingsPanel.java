package com.externalvisuals.gui;

import com.externalvisuals.module.Module;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.Setting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import com.externalvisuals.setting.ColorSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

import java.util.List;

public final class ModuleSettingsPanel {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private Module module;

    private int x;
    private int y;
    private int width;
    private int height;

    private float openProgress;

    private boolean draggingSlider;

    private SliderSetting activeSlider;

    private ColorSetting activeColor;
    private boolean colorPickerOpen;
    private float pickerHue;
    private float pickerSaturation;
    private float pickerValue;

    public ModuleSettingsPanel() {

        this.module = null;

        this.x = 0;
        this.y = 0;

        this.width = 300;
        this.height = 360;

        this.openProgress = 0.0f;

        this.draggingSlider = false;
        this.activeSlider = null;
        this.activeColor = null;
        this.colorPickerOpen = false;
        this.pickerHue = 0.0f;
        this.pickerSaturation = 1.0f;
        this.pickerValue = 1.0f;
    }

    public void open(Module module) {

        this.module = module;

        this.openProgress = 0.0f;

        this.draggingSlider = false;
        this.activeSlider = null;
        this.activeColor = null;
        this.colorPickerOpen = false;
        this.pickerHue = 0.0f;
        this.pickerSaturation = 1.0f;
        this.pickerValue = 1.0f;
    }

    public void close() {

        this.module = null;

        this.draggingSlider = false;
        this.activeSlider = null;
        this.activeColor = null;
        this.colorPickerOpen = false;
        this.pickerHue = 0.0f;
        this.pickerSaturation = 1.0f;
        this.pickerValue = 1.0f;
    }

    public boolean isOpen() {
        return module != null;
    }

    public Module getModule() {
        return module;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (module == null) {
            return;
        }

        openProgress =
                AMOLEDTheme.animate(
                        openProgress,
                        1.0f,
                        AMOLEDTheme.PANEL_SPEED
                );

        TextRenderer font =
                mc.textRenderer;

        int right =
                x + width;

        int bottom =
                y + height;

        fill(
                matrices,
                x,
                y,
                right,
                bottom,
                AMOLEDTheme.PANEL
        );

        fill(
                matrices,
                x,
                y,
                right,
                y + 2,
                AMOLEDTheme.ACCENT
        );

        fill(
                matrices,
                x,
                y,
                x + 1,
                bottom,
                AMOLEDTheme.BORDER_LIGHT
        );

        fill(
                matrices,
                right - 1,
                y,
                right,
                bottom,
                AMOLEDTheme.BORDER
        );

        fill(
                matrices,
                x,
                bottom - 1,
                right,
                bottom,
                AMOLEDTheme.BORDER
        );

        font.drawWithShadow(
                matrices,
                module.getName(),
                x + 16,
                y + 15,
                AMOLEDTheme.TEXT
        );

        font.drawWithShadow(
                matrices,
                "Settings",
                x + 16,
                y + 32,
                AMOLEDTheme.TEXT_MUTED
        );

        fill(
                matrices,
                x + 14,
                y + 52,
                right - 14,
                y + 53,
                AMOLEDTheme.DIVIDER
        );

        List<Setting<?>> settings =
                module.getSettings();

        int settingY =
                y + 66;

        for (Setting<?> setting : settings) {

            if (setting == null) {
                continue;
            }

            if (setting instanceof BooleanSetting) {

                renderBoolean(
                        matrices,
                        (BooleanSetting) setting,
                        settingY,
                        mouseX,
                        mouseY
                );

                settingY += 38;

                continue;
            }

            if (setting instanceof SliderSetting) {

                renderSlider(
                        matrices,
                        (SliderSetting) setting,
                        settingY
                );

                settingY += 58;

                continue;
            }

            if (setting instanceof StringSetting) {
                renderStringSetting(
                        matrices,
                        (StringSetting) setting,
                        settingY,
                        mouseX,
                        mouseY
                );
                settingY += 42;
                continue;
            }

            if (setting instanceof ColorSetting) {
                renderColorSetting(
                        matrices,
                        (ColorSetting) setting,
                        settingY
                );
                settingY += 38;
                continue;
            }

            renderGeneric(
                    matrices,
                    setting,
                    settingY
            );

            settingY += 38;
        }

        if (colorPickerOpen && activeColor != null) {
            renderColorPicker(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        int backHeight =
                30;

        int backY =
                bottom
                        - backHeight
                        - 12;

        boolean backHovered =
                mouseX >= x + 14
                        && mouseX <= right - 14
                        && mouseY >= backY
                        && mouseY <= backY + backHeight;

        fill(
                matrices,
                x + 14,
                backY,
                right - 14,
                backY + backHeight,
                backHovered
                        ? AMOLEDTheme.BUTTON_HOVER
                        : AMOLEDTheme.BUTTON
        );

        font.drawWithShadow(
                matrices,
                "Back",
                x + 24,
                backY + 10,
                AMOLEDTheme.TEXT_LIGHT
        );
    }

    private void renderBoolean(
            MatrixStack matrices,
            BooleanSetting setting,
            int settingY,
            int mouseX,
            int mouseY
    ) {

        TextRenderer font =
                mc.textRenderer;

        font.drawWithShadow(
                matrices,
                setting.getName(),
                x + 16,
                settingY + 9,
                AMOLEDTheme.TEXT_LIGHT
        );

        int toggleWidth =
                38;

        int toggleHeight =
                18;

        int toggleX =
                x + width
                        - toggleWidth
                        - 16;

        int toggleY =
                settingY + 4;

        boolean hovered =
                mouseX >= toggleX
                        && mouseX <= toggleX + toggleWidth
                        && mouseY >= toggleY
                        && mouseY <= toggleY + toggleHeight;

        boolean enabled =
                setting.isEnabled();

        fill(
                matrices,
                toggleX,
                toggleY,
                toggleX + toggleWidth,
                toggleY + toggleHeight,
                enabled
                        ? AMOLEDTheme.TOGGLE_ON
                        : hovered
                        ? AMOLEDTheme.BUTTON_HOVER
                        : AMOLEDTheme.TOGGLE_OFF
        );

        int knobX =
                enabled
                        ? toggleX + 21
                        : toggleX + 3;

        fill(
                matrices,
                knobX,
                toggleY + 3,
                knobX + 14,
                toggleY + 15,
                AMOLEDTheme.TOGGLE_KNOB
        );
    }

    private void renderSlider(
            MatrixStack matrices,
            SliderSetting setting,
            int settingY
    ) {

        TextRenderer font =
                mc.textRenderer;

        font.drawWithShadow(
                matrices,
                setting.getName(),
                x + 16,
                settingY + 4,
                AMOLEDTheme.TEXT_LIGHT
        );

        String value =
                formatValue(
                        setting.getValue()
                );

        font.drawWithShadow(
                matrices,
                value,
                x + width - 62,
                settingY + 4,
                AMOLEDTheme.TEXT_MUTED
        );

        int sliderX =
                x + 16;

        int sliderY =
                settingY + 22;

        int sliderWidth =
                width - 32;

        int sliderHeight =
                8;

        double percentage =
                setting.getPercentage();

        if (
                Double.isNaN(percentage)
                        || Double.isInfinite(percentage)
        ) {
            percentage = 0.0;
        }

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        fill(
                matrices,
                sliderX,
                sliderY,
                sliderX + sliderWidth,
                sliderY + sliderHeight,
                AMOLEDTheme.INPUT
        );

        int activeWidth =
                (int) (
                        sliderWidth
                                * percentage
                );

        if (activeWidth > 0) {

            fill(
                    matrices,
                    sliderX,
                    sliderY,
                    sliderX + activeWidth,
                    sliderY + sliderHeight,
                    AMOLEDTheme.ACCENT
            );
        }

        int knobSize =
                12;

        int knobX =
                sliderX
                        + (int) (
                        (
                                sliderWidth
                                        - knobSize
                        )
                                * percentage
                );

        fill(
                matrices,
                knobX,
                sliderY - 2,
                knobX + knobSize,
                sliderY + sliderHeight + 2,
                AMOLEDTheme.TEXT
        );
    }

    private void renderStringSetting(
            MatrixStack matrices,
            StringSetting setting,
            int settingY,
            int mouseX,
            int mouseY
    ) {

        TextRenderer font = mc.textRenderer;
        boolean hovered =
                mouseX >= x + 12
                        && mouseX <= x + width - 12
                        && mouseY >= settingY
                        && mouseY <= settingY + 34;

        font.drawWithShadow(
                matrices,
                setting.getName(),
                x + 16,
                settingY + 9,
                AMOLEDTheme.TEXT_LIGHT
        );

        String value = String.valueOf(setting.getValue());
        font.drawWithShadow(
                matrices,
                value + "  ›",
                x + width - 120,
                settingY + 9,
                hovered ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.TEXT_MUTED
        );
    }

    private void renderColorSetting(
            MatrixStack matrices,
            ColorSetting setting,
            int settingY
    ) {

        TextRenderer font = mc.textRenderer;

        font.drawWithShadow(
                matrices,
                setting.getName(),
                x + 16,
                settingY + 9,
                AMOLEDTheme.TEXT_LIGHT
        );

        int color = setting.getColor();

        drawColorCircle(
                matrices,
                x + width - 36,
                settingY + 13,
                9,
                color
        );

        if (setting.isRainbow()) {
            font.drawWithShadow(
                    matrices,
                    "RAINBOW",
                    x + width - 112,
                    settingY + 9,
                    AMOLEDTheme.ACCENT_LIGHT
            );
        } else {
            font.drawWithShadow(
                    matrices,
                    "COLOR",
                    x + width - 112,
                    settingY + 9,
                    AMOLEDTheme.TEXT_MUTED
            );
        }
    }

    private void renderColorPicker(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        int pickerSize = 132;
        int pickerX = x - pickerSize - 12;

        if (pickerX < 6) {
            pickerX = x + width + 12;
        }

        int pickerY = y + 58;

        int right = pickerX + pickerSize;
        int bottom = pickerY + pickerSize + 44;

        fill(
                matrices,
                pickerX - 2,
                pickerY - 2,
                right + 2,
                bottom + 2,
                AMOLEDTheme.GLOW
        );

        fill(
                matrices,
                pickerX,
                pickerY,
                right,
                bottom,
                AMOLEDTheme.PANEL
        );

        fill(
                matrices,
                pickerX,
                pickerY,
                right,
                pickerY + 2,
                AMOLEDTheme.ACCENT
        );

        TextRenderer font = mc.textRenderer;

        font.drawWithShadow(
                matrices,
                "COLOR",
                pickerX + 10,
                pickerY + 8,
                AMOLEDTheme.TEXT
        );

        /*
         * Hue wheel.
         */
        int centerX = pickerX + 42;
        int centerY = pickerY + 62;
        int outerRadius = 34;
        int innerRadius = 25;

        for (int px = -outerRadius; px <= outerRadius; px += 2) {
            for (int py = -outerRadius; py <= outerRadius; py += 2) {

                float distance =
                        (float) Math.sqrt(
                                px * px + py * py
                        );

                if (distance < innerRadius
                        || distance > outerRadius) {
                    continue;
                }

                float hue =
                        (float) (
                                (
                                        Math.atan2(
                                                py,
                                                px
                                        )
                                        / (Math.PI * 2.0)
                                )
                                + 0.5
                        );

                int rgb =
                        hsbToRgb(
                                hue,
                                0.90f,
                                1.0f
                        );

                fill(
                        matrices,
                        centerX + px,
                        centerY + py,
                        centerX + px + 2,
                        centerY + py + 2,
                        0xFF000000 | rgb
                );
            }
        }

        /*
         * Saturation/value square.
         */
        int squareX = pickerX + 75;
        int squareY = pickerY + 42;
        int squareSize = 54;

        for (int ix = 0; ix < squareSize; ix += 3) {
            for (int iy = 0; iy < squareSize; iy += 3) {

                float saturation =
                        ix / (float) (squareSize - 1);

                float value =
                        1.0f
                                - iy / (float) (squareSize - 1);

                int rgb =
                        hsbToRgb(
                                pickerHue,
                                saturation,
                                value
                        );

                fill(
                        matrices,
                        squareX + ix,
                        squareY + iy,
                        Math.min(
                                squareX + ix + 3,
                                squareX + squareSize
                        ),
                        Math.min(
                                squareY + iy + 3,
                                squareY + squareSize
                        ),
                        0xFF000000 | rgb
                );
            }
        }

        int selectorX =
                squareX
                        + Math.round(
                        pickerSaturation
                                * (squareSize - 1)
                );

        int selectorY =
                squareY
                        + Math.round(
                        (1.0f - pickerValue)
                                * (squareSize - 1)
                );

        fill(
                matrices,
                selectorX - 2,
                selectorY - 2,
                selectorX + 3,
                selectorY + 3,
                AMOLEDTheme.TEXT
        );

        /*
         * Rainbow toggle.
         */
        int rainbowY = pickerY + pickerSize + 8;
        int rainbowWidth = pickerSize - 20;

        boolean rainbowHover =
                mouseX >= pickerX + 10
                        && mouseX <= pickerX + 10 + rainbowWidth
                        && mouseY >= rainbowY
                        && mouseY <= rainbowY + 24;

        fill(
                matrices,
                pickerX + 10,
                rainbowY,
                pickerX + 10 + rainbowWidth,
                rainbowY + 24,
                activeColor.isRainbow()
                        ? AMOLEDTheme.SELECTED
                        : rainbowHover
                        ? AMOLEDTheme.BUTTON_HOVER
                        : AMOLEDTheme.BUTTON
        );

        drawRainbowDot(
                matrices,
                pickerX + 22,
                rainbowY + 12
        );

        font.drawWithShadow(
                matrices,
                activeColor.isRainbow()
                        ? "Rainbow: ON"
                        : "Rainbow: OFF",
                pickerX + 34,
                rainbowY + 8,
                activeColor.isRainbow()
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.TEXT_LIGHT
        );
    }

    private void drawColorCircle(
            MatrixStack matrices,
            int centerX,
            int centerY,
            int radius,
            int color
    ) {

        for (int px = -radius; px <= radius; px++) {
            for (int py = -radius; py <= radius; py++) {
                if (px * px + py * py <= radius * radius) {
                    fill(
                            matrices,
                            centerX + px,
                            centerY + py,
                            centerX + px + 1,
                            centerY + py + 1,
                            color
                    );
                }
            }
        }
    }

    private void drawRainbowDot(
            MatrixStack matrices,
            int centerX,
            int centerY
    ) {

        drawColorCircle(
                matrices,
                centerX,
                centerY,
                7,
                hsbToRgb(
                        (System.currentTimeMillis() % 4000L)
                                / 4000.0f,
                        0.9f,
                        1.0f
                ) | 0xFF000000
        );
    }

    private int hsbToRgb(
            float hue,
            float saturation,
            float brightness
    ) {

        float h =
                (hue - (float) Math.floor(hue))
                        * 6.0f;

        int sector =
                (int) Math.floor(h);

        float fraction =
                h - sector;

        float p =
                brightness
                        * (1.0f - saturation);

        float q =
                brightness
                        * (
                        1.0f
                                - saturation
                                * fraction
                );

        float t =
                brightness
                        * (
                        1.0f
                                - saturation
                                * (1.0f - fraction)
                );

        float r;
        float g;
        float b;

        switch (sector) {

            case 0:
                r = brightness;
                g = t;
                b = p;
                break;

            case 1:
                r = q;
                g = brightness;
                b = p;
                break;

            case 2:
                r = p;
                g = brightness;
                b = t;
                break;

            case 3:
                r = p;
                g = q;
                b = brightness;
                break;

            case 4:
                r = t;
                g = p;
                b = brightness;
                break;

            default:
                r = brightness;
                g = p;
                b = q;
                break;
        }

        return ((int) (r * 255.0f) << 16)
                | ((int) (g * 255.0f) << 8)
                | (int) (b * 255.0f);
    }

    private void syncPickerFromColor(
            ColorSetting setting
    ) {

        int color = setting.getBaseColor();

        float r =
                ((color >> 16) & 0xFF)
                        / 255.0f;

        float g =
                ((color >> 8) & 0xFF)
                        / 255.0f;

        float b =
                (color & 0xFF)
                        / 255.0f;

        float max =
                Math.max(
                        r,
                        Math.max(g, b)
                );

        float min =
                Math.min(
                        r,
                        Math.min(g, b)
                );

        float delta =
                max - min;

        pickerValue = max;

        if (max <= 0.0001f) {
            pickerSaturation = 0.0f;
            pickerHue = 0.0f;
            return;
        }

        pickerSaturation =
                delta / max;

        if (delta <= 0.0001f) {
            pickerHue = 0.0f;
            return;
        }

        if (max == r) {
            pickerHue =
                    ((g - b) / delta) % 6.0f;
        } else if (max == g) {
            pickerHue =
                    ((b - r) / delta) + 2.0f;
        } else {
            pickerHue =
                    ((r - g) / delta) + 4.0f;
        }

        pickerHue /= 6.0f;

        if (pickerHue < 0.0f) {
            pickerHue += 1.0f;
        }
    }

    private void updateColorFromPicker(
            ColorSetting setting,
            double mouseX,
            double mouseY
    ) {

        int pickerSize = 132;
        int pickerX = x - pickerSize - 12;

        if (pickerX < 6) {
            pickerX = x + width + 12;
        }

        int pickerY = y + 58;

        int centerX = pickerX + 42;
        int centerY = pickerY + 62;

        int dx =
                (int) Math.round(
                        mouseX - centerX
                );

        int dy =
                (int) Math.round(
                        mouseY - centerY
                );

        double distance =
                Math.sqrt(
                        dx * dx + dy * dy
                );

        int outerRadius = 34;
        int innerRadius = 25;

        if (distance >= innerRadius
                && distance <= outerRadius) {

            float hue =
                    (float) (
                            (
                                    Math.atan2(
                                            dy,
                                            dx
                                    )
                                    / (Math.PI * 2.0)
                            )
                            + 0.5
                    );

            if (hue < 0.0f) {
                hue += 1.0f;
            }

            pickerHue = hue;
        }

        int squareX = pickerX + 75;
        int squareY = pickerY + 42;
        int squareSize = 54;

        if (mouseX >= squareX
                && mouseX <= squareX + squareSize
                && mouseY >= squareY
                && mouseY <= squareY + squareSize) {

            pickerSaturation =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    (float) (
                                            (mouseX - squareX)
                                                    / (squareSize - 1.0)
                                    )
                            )
                    );

            pickerValue =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    1.0f - (float) (
                                            (mouseY - squareY)
                                                    / (squareSize - 1.0)
                                    )
                            )
                    );
        }

        int rgb =
                hsbToRgb(
                        pickerHue,
                        pickerSaturation,
                        pickerValue
                );

        int alpha =
                setting.getBaseColor()
                        & 0xFF000000;

        setting.setColor(
                alpha | rgb
        );
    }

    private void renderGeneric(
            MatrixStack matrices,
            Setting<?> setting,
            int settingY
    ) {

        mc.textRenderer.drawWithShadow(
                matrices,
                setting.getName(),
                x + 16,
                settingY + 9,
                AMOLEDTheme.TEXT_LIGHT
        );

        mc.textRenderer.drawWithShadow(
                matrices,
                String.valueOf(
                        setting.getValue()
                ),
                x + 16,
                settingY + 23,
                AMOLEDTheme.TEXT_MUTED
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (module == null) {
            return false;
        }

        if (colorPickerOpen && activeColor != null) {

            if (button == 0) {

                int pickerSize = 132;
                int pickerX = x - pickerSize - 12;

                if (pickerX < 6) {
                    pickerX = x + width + 12;
                }

                int pickerY = y + 58;
                int rainbowY = pickerY + pickerSize + 8;
                int rainbowWidth = pickerSize - 20;

                if (mouseX >= pickerX + 10
                        && mouseX <= pickerX + 10 + rainbowWidth
                        && mouseY >= rainbowY
                        && mouseY <= rainbowY + 24) {

                    activeColor.setRainbow(
                            !activeColor.isRainbow()
                    );

                    return true;
                }

                updateColorFromPicker(
                        activeColor,
                        mouseX,
                        mouseY
                );

                return true;
            }

            return true;
        }

        int backHeight =
                30;

        int backY =
                y
                        + height
                        - backHeight
                        - 12;

        if (
                button == 0
                        && mouseX >= x + 14
                        && mouseX <= x + width - 14
                        && mouseY >= backY
                        && mouseY <= backY + backHeight
        ) {

            close();

            return true;
        }

        if (button != 0) {
            return false;
        }

        List<Setting<?>> settings =
                module.getSettings();

        int settingY =
                y + 66;

        for (Setting<?> setting : settings) {

            if (setting == null) {
                continue;
            }

            if (setting instanceof BooleanSetting) {

                int toggleWidth =
                        38;

                int toggleHeight =
                        18;

                int toggleX =
                        x + width
                                - toggleWidth
                                - 16;

                int toggleY =
                        settingY + 4;

                if (
                        mouseX >= toggleX
                                && mouseX <= toggleX
                                + toggleWidth
                                && mouseY >= toggleY
                                && mouseY <= toggleY
                                + toggleHeight
                ) {

                    ((BooleanSetting) setting).toggle();

                    return true;
                }

                settingY += 38;

                continue;
            }

            if (setting instanceof SliderSetting) {

                SliderSetting slider =
                        (SliderSetting) setting;

                int sliderX =
                        x + 16;

                int sliderY =
                        settingY + 22;

                int sliderWidth =
                        width - 32;

                int sliderHeight =
                        8;

                if (
                        mouseX >= sliderX
                                && mouseX <= sliderX
                                + sliderWidth
                                && mouseY >= sliderY - 4
                                && mouseY <= sliderY
                                + sliderHeight + 4
                ) {

                    draggingSlider = true;
                    activeSlider = slider;

                    updateSlider(
                            slider,
                            mouseX
                    );

                    return true;
                }

                settingY += 58;

                continue;
            }

            if (setting instanceof StringSetting) {
                if (mouseX >= x + 12
                        && mouseX <= x + width - 12
                        && mouseY >= settingY
                        && mouseY <= settingY + 34) {
                    ((StringSetting) setting).cycle();
                    return true;
                }
                settingY += 42;
                continue;
            }

            if (setting instanceof ColorSetting) {

                ColorSetting color =
                        (ColorSetting) setting;

                int swatchX =
                        x + width - 64;

                int swatchY =
                        settingY;

                if (mouseX >= swatchX
                        && mouseX <= x + width - 12
                        && mouseY >= swatchY
                        && mouseY <= swatchY + 28) {

                    activeColor = color;
                    colorPickerOpen = true;
                    syncPickerFromColor(color);
                    return true;
                }

                settingY += 38;
                continue;
            }

            settingY += 38;
        }

        return false;
    }

    /*
     * Three-argument version.
     */
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (module == null) {
            return false;
        }

        if (colorPickerOpen && activeColor != null) {

            if (button != 0) {
                return false;
            }

            updateColorFromPicker(
                    activeColor,
                    mouseX,
                    mouseY
            );

            return true;
        }

        if (!draggingSlider) {
            return false;
        }

        if (button != 0) {
            return false;
        }

        SliderSetting slider = activeSlider;

        if (slider == null) {
            draggingSlider = false;
            return false;
        }

        updateSlider(
                slider,
                mouseX
        );

        return true;
    }

    /*
     * Five-argument version required by
     * ModernGuiRenderer.
     */
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        return mouseDragged(
                mouseX,
                mouseY,
                button
        );
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button != 0) {
            return false;
        }

        if (!draggingSlider) {
            return false;
        }

        draggingSlider = false;
        activeSlider = null;

        return true;
    }

    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (module == null) {
            return false;
        }

        if (keyCode == 256 && colorPickerOpen) {
            colorPickerOpen = false;
            activeColor = null;
            return true;
        }

        /*
         * ESC is handled by ModernGuiRenderer.
         * Other keyboard input is currently unused.
         */
        return false;
    }

    public boolean charTyped(
            char chr,
            int modifiers
    ) {

        if (module == null) {
            return false;
        }

        /*
         * Reserved for future text settings.
         */
        return false;
    }

    private void updateSlider(
            SliderSetting setting,
            double mouseX
    ) {

        if (setting == null) {
            return;
        }

        double sliderWidth =
                Math.max(
                        1.0,
                        width - 32.0
                );

        double percentage =
                (
                        mouseX
                                - (x + 16)
                )
                        / sliderWidth;

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        double min =
                setting.getMin();

        double max =
                setting.getMax();

        double value =
                min
                        + (
                        max - min
                )
                        * percentage;

        double step =
                setting.getStep();

        if (step > 0.0) {

            value =
                    Math.round(
                            (
                                    value - min
                            ) / step
                    )
                            * step
                            + min;
        }

        value =
                Math.max(
                        min,
                        Math.min(
                                max,
                                value
                        )
                );

        setting.setSliderValue(
                value
        );
    }

    public void setPosition(
            int x,
            int y
    ) {

        this.x = x;
        this.y = y;
    }

    public void setSize(
            int width,
            int height
    ) {

        this.width =
                Math.max(
                        240,
                        width
                );

        this.height =
                Math.max(
                        240,
                        height
                );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getOpenProgress() {
        return openProgress;
    }

    private static String formatValue(
            Double value
    ) {

        if (value == null) {
            return "0";
        }

        double number =
                value;

        if (
                Math.abs(
                        number
                                - Math.round(
                                number
                        )
                )
                        < 0.0001
        ) {

            return String.valueOf(
                    Math.round(number)
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                number
        );
    }

    private static void fill(
            MatrixStack matrices,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {

        net.minecraft.client.gui.DrawableHelper.fill(
                matrices,
                left,
                top,
                right,
                bottom,
                color
        );
    }
}