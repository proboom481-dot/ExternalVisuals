package com.externalvisuals.gui;

import com.externalvisuals.module.Module;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.Setting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GlassSettingsPanel {

    private Module module;

    private int x;
    private int y;
    private int width;
    private int height;

    private float openProgress;

    private final List<GlassToggle> toggles =
            new ArrayList<>();

    private final List<GlassSlider> sliders =
            new ArrayList<>();

    public GlassSettingsPanel(
            int x,
            int y,
            int width,
            int height
    ) {

        this.x = x;
        this.y = y;

        this.width =
                Math.max(
                        180,
                        width
                );

        this.height =
                Math.max(
                        100,
                        height
                );

        this.openProgress = 0.0f;
    }

    public void setModule(
            Module module
    ) {

        if (this.module == module) {
            return;
        }

        this.module = module;

        rebuildSettings();

        if (module == null) {
            openProgress = 0.0f;
        } else {
            openProgress = Math.min(
                    openProgress,
                    1.0f
            );
        }
    }

    private void rebuildSettings() {

        toggles.clear();
        sliders.clear();

        if (module == null) {
            return;
        }

        int currentY =
                y + 50;

        int settingHeight = 30;

        for (Setting<?> setting :
                module.getSettings()) {

            if (setting instanceof BooleanSetting) {

                GlassToggle toggle =
                        new GlassToggle(
                                (BooleanSetting) setting,
                                x + width - 58,
                                currentY + 3,
                                46,
                                22
                        );

                toggles.add(toggle);

                currentY += settingHeight;

            } else if (setting instanceof SliderSetting) {

                GlassSlider slider =
                        new GlassSlider(
                                (SliderSetting) setting,
                                x + 12,
                                currentY + 18,
                                width - 24,
                                18
                        );

                sliders.add(slider);

                currentY += 48;
            }
        }
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        float target =
                module == null
                        ? 0.0f
                        : 1.0f;

        openProgress =
                AMOLEDTheme.animate(
                        openProgress,
                        target,
                        AMOLEDTheme.PANEL_SPEED
                );

        if (openProgress <= 0.001f) {
            return;
        }

        /*
         * Панель.
         */
        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + height,
                AMOLEDTheme.PANEL
        );

        /*
         * Верхняя граница.
         */
        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                AMOLEDTheme.BORDER_LIGHT
        );

        /*
         * Левая граница.
         */
        drawRect(
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
        drawRect(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        /*
         * Нижняя граница.
         */
        drawRect(
                matrices,
                x,
                y + height - 1,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        /*
         * Акцентная линия.
         */
        int accentAlpha =
                (int) (
                        90.0f
                                * openProgress
                );

        int accentColor =
                (accentAlpha << 24)
                        | (
                        AMOLEDTheme.ACCENT
                                & 0x00FFFFFF
                );

        drawRect(
                matrices,
                x + 12,
                y + 38,
                x + width - 12,
                y + 39,
                accentColor
        );

        /*
         * Настройки.
         */
        for (GlassToggle toggle :
                toggles) {

            toggle.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        for (GlassSlider slider :
                sliders) {

            slider.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (module == null) {
            return false;
        }

        for (GlassToggle toggle :
                toggles) {

            if (toggle.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        for (GlassSlider slider :
                sliders) {

            if (slider.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return isHovered(
                mouseX,
                mouseY
        );
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (GlassSlider slider :
                sliders) {

            if (slider.mouseDragged(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (GlassSlider slider :
                sliders) {

            if (slider.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return false;
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

    public float getOpenProgress() {
        return openProgress;
    }

    public List<GlassToggle> getToggles() {
        return Collections.unmodifiableList(
                toggles
        );
    }

    public List<GlassSlider> getSliders() {
        return Collections.unmodifiableList(
                sliders
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

    public void setPosition(
            int x,
            int y
    ) {

        this.x = x;
        this.y = y;

        rebuildSettings();
    }

    public void setSize(
            int width,
            int height
    ) {

        this.width =
                Math.max(
                        180,
                        width
                );

        this.height =
                Math.max(
                        100,
                        height
                );

        rebuildSettings();
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
         * Реальная Minecraft-отрисовка
         * будет централизована в
         * ModernGuiRenderer.
         */
    }
}