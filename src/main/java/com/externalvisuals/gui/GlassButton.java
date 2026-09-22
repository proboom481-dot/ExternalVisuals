package com.externalvisuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public final class GlassButton {

    private final String text;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean enabled;

    private float hoverProgress;
    private float pressProgress;

    private Runnable action;

    public GlassButton(
            String text,
            int x,
            int y,
            int width,
            int height
    ) {
        this.text = text;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.enabled = true;

        this.hoverProgress = 0.0f;
        this.pressProgress = 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                enabled
                        && isHovered(
                        mouseX,
                        mouseY
                );

        float hoverTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        hoverProgress =
                AMOLEDTheme.animate(
                        hoverProgress,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        float pressTarget =
                hovered
                        ? pressProgress
                        : 0.0f;

        pressProgress =
                AMOLEDTheme.animate(
                        pressProgress,
                        pressTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        int background =
                interpolateColor(
                        AMOLEDTheme.BUTTON,
                        AMOLEDTheme.BUTTON_HOVER,
                        hoverProgress
                );

        if (!enabled) {
            background =
                    AMOLEDTheme.PANEL_DARK;
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
         * Верхняя подсветка.
         */
        if (hoverProgress > 0.001f) {

            int alpha =
                    (int) (
                            50.0f
                                    * hoverProgress
                    );

            alpha =
                    Math.max(
                            0,
                            Math.min(
                                    255,
                                    alpha
                            )
                    );

            int glow =
                    (alpha << 24)
                            | (
                            AMOLEDTheme.ACCENT
                                    & 0x00FFFFFF
                    );

            drawRect(
                    matrices,
                    x,
                    y,
                    x + width,
                    y + 1,
                    glow
            );
        }

        /*
         * Тонкая граница.
         */
        int border =
                interpolateColor(
                        AMOLEDTheme.BORDER,
                        AMOLEDTheme.ACCENT,
                        hoverProgress
                );

        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                border
        );

        drawRect(
                matrices,
                x,
                y + height - 1,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                x,
                y,
                x + 1,
                y + height,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        /*
         * Текст.
         */
        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc == null) {
            return;
        }

        TextRenderer renderer =
                mc.textRenderer;

        if (renderer == null) {
            return;
        }

        int textWidth =
                renderer.getWidth(text);

        int textX =
                x
                        + (width - textWidth) / 2;

        int textY =
                y
                        + (height - 8) / 2;

        int textColor =
                enabled
                        ? AMOLEDTheme.TEXT
                        : AMOLEDTheme.TEXT_DISABLED;

        drawText(
                matrices,
                text,
                textX,
                textY,
                textColor
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (!enabled) {
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

        pressProgress = 1.0f;

        if (action != null) {
            action.run();
        }

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

    public GlassButton setAction(
            Runnable action
    ) {

        this.action = action;

        return this;
    }

    public GlassButton setEnabled(
            boolean enabled
    ) {

        this.enabled = enabled;

        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getText() {
        return text;
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

    public float getHoverProgress() {
        return hoverProgress;
    }

    public float getPressProgress() {
        return pressProgress;
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

        this.width = Math.max(
                1,
                width
        );

        this.height = Math.max(
                1,
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
                (int) (
                        firstA
                                + (
                                secondA - firstA
                        ) * progress
                );

        int red =
                (int) (
                        firstR
                                + (
                                secondR - firstR
                        ) * progress
                );

        int green =
                (int) (
                        firstG
                                + (
                                secondG - firstG
                        ) * progress
                );

        int blue =
                (int) (
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
         * Отрисовка будет выполняться
         * общим ModernGuiRenderer.
         *
         * Здесь пока хранится состояние
         * компонента и его визуальные параметры.
         */
    }

    private static void drawText(
            MatrixStack matrices,
            String text,
            int x,
            int y,
            int color
    ) {

        /*
         * Текст также будет отрисовываться
         * общим ModernGuiRenderer.
         */
    }
}