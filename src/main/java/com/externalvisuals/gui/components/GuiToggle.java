package com.externalvisuals.gui.components;

import com.externalvisuals.gui.GuiColors;
import com.externalvisuals.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GuiToggle {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final BooleanSetting setting;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean hovered;

    public GuiToggle(
            BooleanSetting setting,
            int x,
            int y,
            int width,
            int height
    ) {
        this.setting = setting;
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

        TextRenderer renderer =
                mc.textRenderer;

        String name =
                setting.getName();

        renderer.drawWithShadow(
                matrices,
                name,
                x,
                y + 3,
                GuiColors.TEXT_LIGHT
        );

        int switchWidth = 28;
        int switchHeight = 14;

        int switchX =
                x + width - switchWidth;

        int switchY =
                y + 1;

        int background;

        if (setting.isEnabled()) {
            background = GuiColors.ENABLED;
        } else if (hovered) {
            background = GuiColors.HOVER;
        } else {
            background = GuiColors.SLIDER_BACKGROUND;
        }

        fill(
                matrices,
                switchX,
                switchY,
                switchX + switchWidth,
                switchY + switchHeight,
                background
        );

        int knobSize = 10;

        int knobX;

        if (setting.isEnabled()) {
            knobX =
                    switchX
                            + switchWidth
                            - knobSize
                            - 2;
        } else {
            knobX =
                    switchX + 2;
        }

        int knobY =
                switchY
                        + (switchHeight - knobSize) / 2;

        fill(
                matrices,
                knobX,
                knobY,
                knobX + knobSize,
                knobY + knobSize,
                GuiColors.TEXT
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button != 0) {
            return false;
        }

        if (!isMouseOver(
                mouseX,
                mouseY
        )) {
            return false;
        }

        setting.toggle();

        return true;
    }

    private boolean isMouseOver(
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

    public BooleanSetting getSetting() {
        return setting;
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
}