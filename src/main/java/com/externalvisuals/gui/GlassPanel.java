package com.externalvisuals.gui;

import net.minecraft.client.util.math.MatrixStack;

public final class GlassPanel {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private float hoverAnimation;
    private float glowAnimation;

    public GlassPanel(
            int x,
            int y,
            int width,
            int height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.hoverAnimation = 0.0f;
        this.glowAnimation = 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        float hoverTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        hoverAnimation =
                AMOLEDTheme.animate(
                        hoverAnimation,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        float glowTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        glowAnimation =
                AMOLEDTheme.animate(
                        glowAnimation,
                        glowTarget,
                        AMOLEDTheme.PANEL_SPEED
                );

        /*
         * Внешний glow.
         */
        if (glowAnimation > 0.001f) {

            int glowAlpha =
                    (int) (
                            30.0f
                                    * glowAnimation
                    );

            glowAlpha =
                    Math.max(
                            0,
                            Math.min(
                                    255,
                                    glowAlpha
                            )
                    );

            int glowColor =
                    (glowAlpha << 24)
                            | (
                            AMOLEDTheme.ACCENT
                                    & 0x00FFFFFF
                    );

            fill(
                    matrices,
                    x - 2,
                    y - 2,
                    x + width + 2,
                    y + height + 2,
                    glowColor
            );
        }

        /*
         * Основная glass-панель.
         */
        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                AMOLEDTheme.PANEL
        );

        /*
         * Очень лёгкий внутренний слой.
         */
        int lightAlpha =
                (int) (
                        18.0f
                                * hoverAnimation
                );

        if (lightAlpha > 0) {

            int lightColor =
                    (lightAlpha << 24)
                            | (
                            AMOLEDTheme.ACCENT_LIGHT
                                    & 0x00FFFFFF
                    );

            fill(
                    matrices,
                    x + 1,
                    y + 1,
                    x + width - 1,
                    y + height - 1,
                    lightColor
            );
        }

        /*
         * Верхняя тонкая подсветка.
         */
        int borderColor =
                interpolateColor(
                        AMOLEDTheme.BORDER,
                        AMOLEDTheme.SELECTED_BORDER,
                        glowAnimation
                );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                borderColor
        );

        /*
         * Нижняя граница.
         */
        fill(
                matrices,
                x,
                y + height - 1,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        /*
         * Левая граница.
         */
        fill(
                matrices,
                x,
                y,
                x + 1,
                y + height,
                AMOLEDTheme.BORDER
        );

        /*
         * Правая граница.
         */
        fill(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );
    }

    public boolean isHovered(
            int mouseX,
            int mouseY
    ) {

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
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

    public float getHoverAnimation() {
        return hoverAnimation;
    }

    public float getGlowAnimation() {
        return glowAnimation;
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

        return (
                (alpha << 24)
                        | (red << 16)
                        | (green << 8)
                        | blue
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

        /*
         * Этот helper оставлен специально
         * без привязки к старому GuiRenderer.
         *
         * Реальную Minecraft-отрисовку панели
         * подключим при интеграции в GUI.
         *
         * Пока метод ничего не делает.
         */
    }
}