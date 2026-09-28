package com.externalvisuals.gui;

import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;

public final class AimAssistSettingsPanel {

    private final AimAssist aimAssist;

    private GlassSlider rangeSlider;
    private GlassSlider fovSlider;
    private GlassSlider speedSlider;

    private GlassToggle playersToggle;
    private GlassToggle attackToggle;

    private int x;
    private int y;
    private int width;
    private int height;

    public AimAssistSettingsPanel(
            AimAssist aimAssist
    ) {

        this.aimAssist = aimAssist;

        this.x = 0;
        this.y = 0;
        this.width = 300;
        this.height = 250;

        createControls();
    }

    private void createControls() {

        if (aimAssist == null) {
            return;
        }

        SliderSetting range =
                aimAssist.getRangeSetting();

        SliderSetting fov =
                aimAssist.getFovSetting();

        SliderSetting speed =
                aimAssist.getSpeedSetting();

        BooleanSetting players =
                aimAssist.getPlayersSetting();

        BooleanSetting attack =
                aimAssist.getRequireAttackKeySetting();

        rangeSlider =
                new GlassSlider(
                        range,
                        x + 18,
                        y + 45,
                        width - 36,
                        20
                );

        fovSlider =
                new GlassSlider(
                        fov,
                        x + 18,
                        y + 85,
                        width - 36,
                        20
                );

        speedSlider =
                new GlassSlider(
                        speed,
                        x + 18,
                        y + 125,
                        width - 36,
                        20
                );

        playersToggle =
                new GlassToggle(
                        players,
                        x + width - 58,
                        y + 164,
                        40,
                        20
                );

        attackToggle =
                new GlassToggle(
                        attack,
                        x + width - 58,
                        y + 198,
                        40,
                        20
                );
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (aimAssist == null) {
            return;
        }

        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + height,
                AMOLEDTheme.PANEL
        );

        drawRect(
                matrices,
                x,
                y,
                x + width,
                y + 2,
                AMOLEDTheme.ACCENT
        );

        drawText(
                matrices,
                "AIM ASSIST",
                x + 18,
                y + 14,
                AMOLEDTheme.TEXT
        );

        drawText(
                matrices,
                "Range",
                x + 18,
                y + 35,
                AMOLEDTheme.TEXT_MUTED
        );

        if (rangeSlider != null) {
            rangeSlider.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        drawText(
                matrices,
                "FOV",
                x + 18,
                y + 75,
                AMOLEDTheme.TEXT_MUTED
        );

        if (fovSlider != null) {
            fovSlider.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        drawText(
                matrices,
                "Speed",
                x + 18,
                y + 115,
                AMOLEDTheme.TEXT_MUTED
        );

        if (speedSlider != null) {
            speedSlider.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        drawText(
                matrices,
                "Players",
                x + 18,
                y + 170,
                AMOLEDTheme.TEXT
        );

        if (playersToggle != null) {
            playersToggle.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        drawText(
                matrices,
                "Only While Attacking",
                x + 18,
                y + 204,
                AMOLEDTheme.TEXT
        );

        if (attackToggle != null) {
            attackToggle.render(
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

        if (rangeSlider != null
                && rangeSlider.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (fovSlider != null
                && fovSlider.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (speedSlider != null
                && speedSlider.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (playersToggle != null
                && playersToggle.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (attackToggle != null
                && attackToggle.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        return false;
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (rangeSlider != null
                && rangeSlider.mouseDragged(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (fovSlider != null
                && fovSlider.mouseDragged(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (speedSlider != null
                && speedSlider.mouseDragged(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        return false;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (rangeSlider != null
                && rangeSlider.mouseReleased(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (fovSlider != null
                && fovSlider.mouseReleased(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        if (speedSlider != null
                && speedSlider.mouseReleased(
                mouseX,
                mouseY,
                button
        )) {
            return true;
        }

        /*
         * GlassToggle не требует mouseReleased().
         * Он переключается непосредственно при клике.
         */

        return false;
    }

    public void setPosition(
            int x,
            int y
    ) {

        this.x = x;
        this.y = y;

        updateControlPositions();
    }

    public void setSize(
            int width,
            int height
    ) {

        this.width = Math.max(
                240,
                width
        );

        this.height = Math.max(
                230,
                height
        );

        updateControlPositions();
    }

    private void updateControlPositions() {

        if (rangeSlider != null) {

            rangeSlider.setPosition(
                    x + 18,
                    y + 45
            );

            rangeSlider.setSize(
                    width - 36,
                    20
            );
        }

        if (fovSlider != null) {

            fovSlider.setPosition(
                    x + 18,
                    y + 85
            );

            fovSlider.setSize(
                    width - 36,
                    20
            );
        }

        if (speedSlider != null) {

            speedSlider.setPosition(
                    x + 18,
                    y + 125
            );

            speedSlider.setSize(
                    width - 36,
                    20
            );
        }

        if (playersToggle != null) {

            playersToggle.setPosition(
                    x + width - 58,
                    y + 164
            );
        }

        if (attackToggle != null) {

            attackToggle.setPosition(
                    x + width - 58,
                    y + 198
            );
        }
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

    private static void drawRect(
            MatrixStack matrices,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {

        /*
         * Actual drawing will be handled
         * by the modern GUI renderer.
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
         * Actual font rendering will be handled
         * by UiFont / ModernGuiRenderer.
         */
    }
}