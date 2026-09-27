package com.externalvisuals.gui;

import com.externalvisuals.module.Module;
import net.minecraft.client.util.math.MatrixStack;

public final class GlassModuleCard {

    private final Module module;

    private int x;
    private int y;
    private int width;
    private int height;

    private float hoverProgress;
    private float enabledProgress;

    public GlassModuleCard(
            Module module,
            int x,
            int y,
            int width,
            int height
    ) {

        this.module = module;

        this.x = x;
        this.y = y;

        this.width =
                Math.max(
                        140,
                        width
                );

        this.height =
                Math.max(
                        42,
                        height
                );

        this.hoverProgress = 0.0f;

        this.enabledProgress =
                module != null
                        && module.isEnabled()
                        ? 1.0f
                        : 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (module == null) {
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

        float enabledTarget =
                module.isEnabled()
                        ? 1.0f
                        : 0.0f;

        hoverProgress =
                AMOLEDTheme.animate(
                        hoverProgress,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        enabledProgress =
                AMOLEDTheme.animate(
                        enabledProgress,
                        enabledTarget,
                        AMOLEDTheme.TOGGLE_SPEED
                );

        /*
         * Основной фон.
         */
        int background =
                interpolateColor(
                        AMOLEDTheme.PANEL,
                        AMOLEDTheme.PANEL_LIGHT,
                        hoverProgress
                );

        background =
                interpolateColor(
                        background,
                        AMOLEDTheme.BUTTON_ACTIVE,
                        enabledProgress
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
         * Активная фиолетовая полоска.
         */
        if (enabledProgress > 0.001f) {

            int alpha =
                    (int) (
                            120.0f
                                    * enabledProgress
                    );

            int accent =
                    (alpha << 24)
                            | (
                            AMOLEDTheme.ACCENT
                                    & 0x00FFFFFF
                    );

            drawRect(
                    matrices,
                    x,
                    y,
                    x + 3,
                    y + height,
                    accent
            );
        }

        /*
         * Hover glow.
         */
        if (hoverProgress > 0.001f) {

            int alpha =
                    (int) (
                            35.0f
                                    * hoverProgress
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
                    y + 1,
                    glow
            );
        }

        /*
         * Границы.
         */
        int border =
                interpolateColor(
                        AMOLEDTheme.BORDER,
                        AMOLEDTheme.SELECTED_BORDER,
                        enabledProgress
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
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (module == null) {
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

        module.toggle();

        return true;
    }

    public boolean mouseRightClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (module == null) {
            return false;
        }

        if (button != 1) {
            return false;
        }

        return isHovered(
                mouseX,
                mouseY
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

    public Module getModule() {
        return module;
    }

    public String getName() {

        if (module == null) {
            return "";
        }

        return module.getName();
    }

    public boolean isEnabled() {

        return module != null
                && module.isEnabled();
    }

    public float getHoverProgress() {
        return hoverProgress;
    }

    public float getEnabledProgress() {
        return enabledProgress;
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
                        140,
                        width
                );

        this.height =
                Math.max(
                        42,
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
         * Фактический рендеринг будет
         * централизован в ModernGuiRenderer.
         */
    }
}