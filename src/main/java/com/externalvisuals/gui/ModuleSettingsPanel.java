package com.externalvisuals.gui;

import com.externalvisuals.module.Module;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.Setting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

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

    public ModuleSettingsPanel() {

        this.module = null;

        this.x = 0;
        this.y = 0;

        this.width = 300;
        this.height = 360;

        this.openProgress = 0.0f;

        this.draggingSlider = false;
    }

    public void open(Module module) {

        this.module = module;

        this.openProgress = 0.0f;

        this.draggingSlider = false;
    }

    public void close() {

        this.module = null;

        this.draggingSlider = false;
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

            renderGeneric(
                    matrices,
                    setting,
                    settingY
            );

            settingY += 38;
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

                    updateSlider(
                            slider,
                            mouseX
                    );

                    return true;
                }

                settingY += 58;

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

        if (!draggingSlider) {
            return false;
        }

        if (button != 0) {
            return false;
        }

        SliderSetting slider =
                findFirstSlider();

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

    private SliderSetting findFirstSlider() {

        if (module == null) {
            return null;
        }

        for (Setting<?> setting :
                module.getSettings()) {

            if (setting instanceof SliderSetting) {

                return (SliderSetting) setting;
            }
        }

        return null;
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