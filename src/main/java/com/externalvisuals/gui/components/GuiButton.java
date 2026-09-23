package com.externalvisuals.gui.components;

import com.externalvisuals.gui.GuiColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GuiButton {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final String text;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean hovered;
    private boolean enabled = true;

    public GuiButton(
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
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        hovered = isMouseOver(mouseX, mouseY);

        int background;

        if (!enabled) {
            background = GuiColors.PANEL;
        } else if (hovered) {
            background = GuiColors.BUTTON_HOVER;
        } else {
            background = GuiColors.BUTTON;
        }

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                background
        );

        TextRenderer renderer = mc.textRenderer;

        int textWidth =
                renderer.getWidth(text);

        int textX =
                x + (width - textWidth) / 2;

        int textY =
                y + (height - 8) / 2;

        int textColor =
                enabled
                        ? GuiColors.TEXT
                        : GuiColors.TEXT_DISABLED;

        renderer.drawWithShadow(
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

        return isMouseOver(
                mouseX,
                mouseY
        );
    }

    public boolean isMouseOver(
            double mouseX,
            double mouseY
    ) {

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    private void fill(
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
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(
            boolean enabled
    ) {
        this.enabled = enabled;
    }
}