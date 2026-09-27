package com.externalvisuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public final class GuiSearchBar {

    private int x;
    private int y;
    private int width;
    private int height;

    private String text;

    private boolean focused;
    private boolean hovered;

    private float focusProgress;
    private float hoverProgress;

    public GuiSearchBar(
            int x,
            int y,
            int width,
            int height
    ) {

        this.x = x;
        this.y = y;

        this.width =
                Math.max(
                        140,
                        width
                );

        this.height =
                Math.max(
                        24,
                        height
                );

        this.text = "";

        this.focused = false;
        this.hovered = false;

        this.focusProgress = 0.0f;
        this.hoverProgress = 0.0f;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        float hoverTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        float focusTarget =
                focused
                        ? 1.0f
                        : 0.0f;

        hoverProgress =
                AMOLEDTheme.animate(
                        hoverProgress,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        focusProgress =
                AMOLEDTheme.animate(
                        focusProgress,
                        focusTarget,
                        AMOLEDTheme.PANEL_SPEED
                );

        /*
         * Фон.
         */
        int background =
                interpolateColor(
                        AMOLEDTheme.INPUT,
                        AMOLEDTheme.INPUT_HOVER,
                        hoverProgress
                );

        background =
                interpolateColor(
                        background,
                        AMOLEDTheme.BUTTON_ACTIVE,
                        focusProgress
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
         * Рамка.
         */
        int border =
                interpolateColor(
                        AMOLEDTheme.INPUT_BORDER,
                        AMOLEDTheme.SELECTED_BORDER,
                        focusProgress
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
                AMOLEDTheme.INPUT_BORDER
        );

        drawRect(
                matrices,
                x,
                y,
                x + 1,
                y + height,
                border
        );

        drawRect(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                border
        );

        /*
         * Фиолетовая линия фокуса.
         */
        if (focusProgress > 0.001f) {

            int alpha =
                    (int) (
                            180.0f
                                    * focusProgress
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
                    y + height - 2,
                    x + width,
                    y + height,
                    accent
            );
        }
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button != 0) {
            return false;
        }

        if (isHovered(
                mouseX,
                mouseY
        )) {

            focused = true;

            return true;
        }

        focused = false;

        return false;
    }

    public boolean charTyped(
            char chr,
            int modifiers
    ) {

        if (!focused) {
            return false;
        }

        if (!isAllowedCharacter(chr)) {
            return false;
        }

        if (text.length() >= 64) {
            return true;
        }

        text += chr;

        return true;
    }

    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (!focused) {
            return false;
        }

        /*
         * Backspace.
         */
        if (keyCode == 259) {

            if (!text.isEmpty()) {

                text =
                        text.substring(
                                0,
                                text.length() - 1
                        );
            }

            return true;
        }

        /*
         * Escape снимает фокус,
         * но не закрывает ClickGUI.
         */
        if (keyCode == 256) {

            focused = false;

            return true;
        }

        /*
         * Ctrl + A.
         */
        if (
                keyCode == 65
                        && (
                        modifiers & 2
                ) != 0
        ) {

            return true;
        }

        return false;
    }

    public void clear() {
        text = "";
    }

    public boolean hasText() {
        return !text.isEmpty();
    }

    public String getText() {
        return text;
    }

    public String getSearchText() {
        return text.trim();
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean isHovered() {
        return hovered;
    }

    public float getFocusProgress() {
        return focusProgress;
    }

    public float getHoverProgress() {
        return hoverProgress;
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

    public void setFocused(
            boolean focused
    ) {

        this.focused = focused;
    }

    public void setText(
            String text
    ) {

        if (text == null) {
            this.text = "";
            return;
        }

        if (text.length() > 64) {
            this.text =
                    text.substring(
                            0,
                            64
                    );
            return;
        }

        this.text = text;
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
                        24,
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

    private static boolean isAllowedCharacter(
            char chr
    ) {

        return chr >= 32
                && chr != 127;
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
         * Фактический рендеринг выполняется
         * централизованно через ModernGuiRenderer.
         */
    }
}