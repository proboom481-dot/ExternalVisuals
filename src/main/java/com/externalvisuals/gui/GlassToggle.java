package com.externalvisuals.gui;

import com.externalvisuals.setting.BooleanSetting;
import net.minecraft.client.util.math.MatrixStack;

public final class GlassToggle {

    private BooleanSetting setting;

    private int x;
    private int y;
    private int width;
    private int height;

    private float animation;

    public GlassToggle(
            BooleanSetting setting,
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
                        32,
                        width
                );

        this.height =
                Math.max(
                        16,
                        height
                );

        this.animation =
                setting != null
                        && setting.isEnabled()
                        ? 1.0f
                        : 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (setting == null) {
            return;
        }

        boolean enabled =
                setting.isEnabled();

        float target =
                enabled
                        ? 1.0f
                        : 0.0f;

        animation =
                AMOLEDTheme.animate(
                        animation,
                        target,
                        AMOLEDTheme.TOGGLE_SPEED
                );

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        /*
         * Фон переключателя.
         */
        int background =
                interpolateColor(
                        AMOLEDTheme.TOGGLE_OFF,
                        AMOLEDTheme.TOGGLE_ON,
                        animation
                );

        if (hovered) {

            background =
                    interpolateColor(
                            background,
                            AMOLEDTheme.ACCENT_LIGHT,
                            0.15f
                    );
        }

        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + height,
                background
        );

        /*
         * Внутренний glow активного состояния.
         */
        if (animation > 0.01f) {

            int alpha =
                    (int) (
                            45.0f
                                    * animation
                    );

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
                    x + width,
                    y + 2,
                    glow
            );
        }

        /*
         * Ползунок.
         */
        int knobSize =
                Math.max(
                        12,
                        height - 4
                );

        float travel =
                width
                        - knobSize
                        - 4.0f;

        int knobX =
                Math.round(
                        x
                                + 2.0f
                                + travel
                                * animation
                );

        int knobY =
                y
                        + (height - knobSize)
                        / 2;

        drawRect(
                matrices,
                knobX,
                knobY,
                knobX + knobSize,
                knobY + knobSize,
                AMOLEDTheme.TOGGLE_KNOB
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

        setting.toggle();

        return true;
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

    public BooleanSetting getSetting() {
        return setting;
    }

    public boolean isEnabled() {

        return setting != null
                && setting.isEnabled();
    }

    public float getAnimation() {
        return animation;
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
                        32,
                        width
                );

        this.height =
                Math.max(
                        16,
                        height
                );
    }

    private static int interpolateColor(
            int first,
            int second,
            float progress
    ) {

        progress =
                Math.max(
                        0.0f,
                        Math.min(
                                1.0f,
                                progress
                        )
                );

        int firstA =
                (first >> 24) & 0xFF;

        int firstR =
                (first >> 16) & 0xFF;

        int firstG =
                (first >> 8) & 0xFF;

        int firstB =
                first & 0xFF;

        int secondA =
                (second >> 24) & 0xFF;

        int secondR =
                (second >> 16) & 0xFF;

        int secondG =
                (second >> 8) & 0xFF;

        int secondB =
                second & 0xFF;

        int alpha =
                Math.round(
                        firstA
                                + (
                                secondA - firstA
                        ) * progress
                );

        int red =
                Math.round(
                        firstR
                                + (
                                secondR - firstR
                        ) * progress
                );

        int green =
                Math.round(
                        firstG
                                + (
                                secondG - firstG
                        ) * progress
                );

        int blue =
                Math.round(
                        firstB
                                + (
                                secondB - firstB
                        ) * progress
                );

        return (alpha << 24)
                | (red << 16)
                | (green << 8)
                | blue;
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
         * Реальный рендеринг будет выполняться
         * централизованным ModernGuiRenderer.
         */
    }
}