package com.externalvisuals.gui;

import com.externalvisuals.setting.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;

public final class GlassSlider {

    private final SliderSetting setting;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean dragging;

    private float animatedProgress;

    public GlassSlider(
            SliderSetting setting,
            int x,
            int y,
            int width,
            int height
    ) {

        this.setting = setting;

        this.x = x;
        this.y = y;

        this.width =
                Math.max(
                        100,
                        width
                );

        this.height =
                Math.max(
                        16,
                        height
                );

        this.dragging = false;

        this.animatedProgress =
                getSettingProgress();
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (setting == null) {
            return;
        }

        float target =
                getSettingProgress();

        animatedProgress =
                AMOLEDTheme.animate(
                        animatedProgress,
                        target,
                        AMOLEDTheme.SLIDER_SPEED
                );

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        /*
         * Основная дорожка.
         */
        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + height,
                AMOLEDTheme.INPUT
        );

        /*
         * Активная часть.
         */
        int activeWidth =
                Math.round(
                        width
                                * animatedProgress
                );

        if (activeWidth > 0) {

            drawRect(
                    matrices,
                    x,
                    y,
                    x + activeWidth,
                    y + height,
                    AMOLEDTheme.ACCENT
            );
        }

        /*
         * Glow при наведении или перетаскивании.
         */
        if (hovered || dragging) {

            int alpha =
                    dragging
                            ? 100
                            : 60;

            int glow =
                    (alpha << 24)
                            | (
                            AMOLEDTheme.ACCENT_LIGHT
                                    & 0x00FFFFFF
                    );

            drawRect(
                    matrices,
                    x,
                    y,
                    x + activeWidth,
                    y + 2,
                    glow
            );
        }

        /*
         * Ползунок.
         */
        int knobSize =
                Math.max(
                        10,
                        height + 4
                );

        int knobTravel =
                Math.max(
                        0,
                        width - knobSize
                );

        int knobX =
                Math.round(
                        x
                                + knobTravel
                                * animatedProgress
                );

        int knobY =
                y
                        + (
                        height - knobSize
                ) / 2;

        drawRect(
                matrices,
                knobX,
                knobY,
                knobX + knobSize,
                knobY + knobSize,
                AMOLEDTheme.TEXT
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (setting == null) {
            return false;
        }

        if (button != 0) {
            return false;
        }

        if (!isHovered(
                mouseX,
                mouseY
        )) {
            return false;
        }

        dragging = true;

        updateValue(
                mouseX
        );

        return true;
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (!dragging) {
            return false;
        }

        if (button != 0) {
            return false;
        }

        updateValue(
                mouseX
        );

        return true;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button != 0) {
            return false;
        }

        if (!dragging) {
            return false;
        }

        dragging = false;

        updateValue(
                mouseX
        );

        return true;
    }

    private void updateValue(
            double mouseX
    ) {

        if (setting == null) {
            return;
        }

        double sliderWidth =
                Math.max(
                        1.0,
                        (double) width
                );

        double percentage =
                (
                        mouseX - x
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

    private float getSettingProgress() {

        if (setting == null) {
            return 0.0f;
        }

        double percentage =
                setting.getPercentage();

        if (
                Double.isNaN(
                        percentage
                )
                        || Double.isInfinite(
                        percentage
                )
        ) {
            return 0.0f;
        }

        return (float) Math.max(
                0.0,
                Math.min(
                        1.0,
                        percentage
                )
        );
    }

    public boolean isHovered(
            double mouseX,
            double mouseY
    ) {

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    public boolean isDragging() {
        return dragging;
    }

    public SliderSetting getSetting() {
        return setting;
    }

    public float getAnimatedProgress() {
        return animatedProgress;
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
                        100,
                        width
                );

        this.height =
                Math.max(
                        16,
                        height
                );
    }

    private static void drawRect(
            MatrixStack matrices,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {

        /*
         * Фактическая отрисовка будет выполняться
         * централизованно через ModernGuiRenderer.
         */
    }
}