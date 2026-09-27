package com.externalvisuals.gui.components;

import com.externalvisuals.gui.GuiColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GuiPanel {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private String title;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean hovered;

    public GuiPanel(
            String title,
            int x,
            int y,
            int width,
            int height
    ) {
        this.title = title;
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

        hovered = isMouseOver(
                mouseX,
                mouseY
        );

        // Основная панель
        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                GuiColors.PANEL
        );

        // Верхняя часть панели
        fill(
                matrices,
                x,
                y,
                x + width,
                y + 24,
                GuiColors.PANEL_LIGHT
        );

        // Нижняя граница заголовка
        fill(
                matrices,
                x,
                y + 23,
                x + width,
                y + 24,
                GuiColors.DIVIDER
        );

        TextRenderer renderer =
                mc.textRenderer;

        renderer.drawWithShadow(
                matrices,
                title,
                x + 10,
                y + 8,
                GuiColors.TEXT
        );

        // Левая декоративная линия
        fill(
                matrices,
                x,
                y,
                x + 2,
                y + height,
                GuiColors.ACCENT
        );
    }

    public void renderBackground(
            MatrixStack matrices
    ) {

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                GuiColors.PANEL
        );

        fill(
                matrices,
                x,
                y,
                x + 2,
                y + height,
                GuiColors.ACCENT
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title
    ) {
        this.title =
                title == null
                        ? ""
                        : title;
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
                Math.max(1, width);

        this.height =
                Math.max(1, height);
    }

    public boolean isHovered() {
        return hovered;
    }
}