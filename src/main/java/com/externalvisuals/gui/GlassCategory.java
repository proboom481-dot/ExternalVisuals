package com.externalvisuals.gui;

import com.externalvisuals.module.ModuleCategory;
import net.minecraft.client.util.math.MatrixStack;

public final class GlassCategory {

    private final ModuleCategory category;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean selected;

    private float hoverProgress;
    private float selectedProgress;

    public GlassCategory(
            ModuleCategory category,
            int x,
            int y,
            int width,
            int height
    ) {

        this.category = category;

        this.x = x;
        this.y = y;

        this.width =
                Math.max(
                        90,
                        width
                );

        this.height =
                Math.max(
                        28,
                        height
                );

        this.selected = false;

        this.hoverProgress = 0.0f;

        this.selectedProgress = 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (category == null) {
            return;
        }

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        float hoverTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        float selectedTarget =
                selected
                        ? 1.0f
                        : 0.0f;

        hoverProgress =
                AMOLEDTheme.animate(
                        hoverProgress,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        selectedProgress =
                AMOLEDTheme.animate(
                        selectedProgress,
                        selectedTarget,
                        AMOLEDTheme.PANEL_SPEED
                );

        /*
         * Базовый фон.
         */
        int background =
                interpolateColor(
                        AMOLEDTheme.PANEL_DARK,
                        AMOLEDTheme.HOVER,
                        hoverProgress
                );

        /*
         * Выбранная категория получает
         * фиолетовый стеклянный оттенок.
         */
        background =
                interpolateColor(
                        background,
                        AMOLEDTheme.SELECTED,
                        selectedProgress
                );

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
        if (
                hoverProgress > 0.001f
                        || selectedProgress > 0.001f
        ) {

            float intensity =
                    Math.max(
                            hoverProgress,
                            selectedProgress
                    );

            int alpha =
                    (int) (
                            35.0f
                                    * intensity
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
         * Нижняя линия выбранной категории.
         */
        if (selectedProgress > 0.001f) {

            int alpha =
                    (int) (
                            220.0f
                                    * selectedProgress
                    );

            int accent =
                    (alpha << 24)
                            | (
                            AMOLEDTheme.ACCENT
                                    & 0x00FFFFFF
                    );

            int lineWidth =
                    Math.max(
                            1,
                            Math.round(
                                    width
                                            * selectedProgress
                            )
                    );

            int lineX =
                    x
                            + (
                            width - lineWidth
                    ) / 2;

            drawRect(
                    matrices,
                    lineX,
                    y + height - 2,
                    lineX + lineWidth,
                    y + height,
                    accent
            );
        }

        /*
         * Тонкая рамка.
         */
        int border =
                interpolateColor(
                        AMOLEDTheme.BORDER,
                        AMOLEDTheme.SELECTED_BORDER,
                        selectedProgress
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
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (category == null) {
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

        selected = true;

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

    public ModuleCategory getCategory() {
        return category;
    }

    public String getName() {

        if (category == null) {
            return "";
        }

        return category.getDisplayName();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(
            boolean selected
    ) {

        this.selected = selected;
    }

    public float getHoverProgress() {
        return hoverProgress;
    }

    public float getSelectedProgress() {
        return selectedProgress;
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
                        90,
                        width
                );

        this.height =
                Math.max(
                        28,
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
         * Реальная отрисовка будет выполняться
         * через централизованный ModernGuiRenderer.
         */
    }
}