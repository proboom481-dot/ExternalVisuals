package com.externalvisuals.gui.components;

import com.externalvisuals.gui.GuiColors;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GuiSlider {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final SliderSetting setting;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean dragging;

    public GuiSlider(
            SliderSetting setting,
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

        double percentage =
                setting.getPercentage();

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        int barY =
                y + height / 2 - 2;

        // Фон слайдера
        fill(
                matrices,
                x,
                barY,
                x + width,
                barY + 4,
                GuiColors.SLIDER_BACKGROUND
        );

        // Заполненная часть
        int filledWidth =
                (int) (width * percentage);

        if (filledWidth > 0) {

            fill(
                    matrices,
                    x,
                    barY,
                    x + filledWidth,
                    barY + 4,
                    GuiColors.SLIDER_FILL
            );
        }

        // Ползунок
        int knobX =
                x + filledWidth - 4;

        fill(
                matrices,
                knobX,
                barY - 4,
                knobX + 8,
                barY + 8,
                GuiColors.ACCENT
        );

        // Название
        TextRenderer renderer =
                mc.textRenderer;

        String name =
                setting.getName();

        renderer.drawWithShadow(
                matrices,
                name,
                x,
                y - 13,
                GuiColors.TEXT_LIGHT
        );

        // Значение
        String value =
                formatValue(
                        setting.getValue()
                );

        int valueWidth =
                renderer.getWidth(value);

        renderer.drawWithShadow(
                matrices,
                value,
                x + width - valueWidth,
                y - 13,
                GuiColors.TEXT_MUTED
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

        dragging = true;

        updateValue(mouseX);

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

        updateValue(mouseX);

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

        updateValue(mouseX);

        return true;
    }

    private void updateValue(
            double mouseX
    ) {

        double percentage =
                (mouseX - x)
                        / (double) width;

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
                        + (max - min)
                        * percentage;

        double step =
                setting.getStep();

        if (step > 0.0) {

            value =
                    Math.round(
                            value / step
                    ) * step;
        }

        value =
                Math.max(
                        min,
                        Math.min(
                                max,
                                value
                        )
                );

        setting.setSliderValue(value);
    }

    private boolean isMouseOver(
            double mouseX,
            double mouseY
    ) {

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y - 6
                && mouseY <= y + height + 6;
    }

    private String formatValue(
            Double value
    ) {

        if (value == null) {
            return "0";
        }

        double number = value;

        if (Math.abs(
                number
                        - Math.round(number)
        ) < 0.00001) {

            return String.valueOf(
                    (long) Math.round(number)
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                number
        );
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

    public SliderSetting getSetting() {
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

    public boolean isDragging() {
        return dragging;
    }
}