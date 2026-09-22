package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.modules.visual.ESP;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public final class SecretMenuScreen extends Screen {

    private float openAnimation;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    private boolean aimSettingsOpen;

    private AimAssistSettingsPanel aimAssistSettingsPanel;

    public SecretMenuScreen() {
        super(
                new LiteralText(
                        "ExternalVisuals Secret"
                )
        );

        openAnimation = 0.0f;
        aimSettingsOpen = false;
        aimAssistSettingsPanel = null;
    }

    @Override
    protected void init() {
        super.init();

        updatePanelSize();
        createAimAssistPanel();
    }

    private void createAimAssistPanel() {

        AimAssist aimAssist =
                getAimAssist();

        if (aimAssist == null) {
            aimAssistSettingsPanel = null;
            return;
        }

        aimAssistSettingsPanel =
                new AimAssistSettingsPanel(
                        aimAssist
                );

        updateAimAssistPanelPosition();
    }

    @Override
    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY,
            float delta
    ) {

        updatePanelSize();

        openAnimation =
                AMOLEDTheme.animate(
                        openAnimation,
                        1.0f,
                        AMOLEDTheme.PANEL_SPEED
                );

        /*
         * Full-screen AMOLED background.
         */
        fillScreen(
                matrices,
                AMOLEDTheme.BACKGROUND
        );

        /*
         * Aim Assist settings.
         */
        if (aimSettingsOpen) {

            renderAimAssistSettings(
                    matrices,
                    mouseX,
                    mouseY
            );

            super.render(
                    matrices,
                    mouseX,
                    mouseY,
                    delta
            );

            return;
        }

        /*
         * Main secret panel.
         */
        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.PANEL
        );

        /*
         * Purple accent glow.
         */
        int glowAlpha =
                (int) (
                        65.0f
                                * openAnimation
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

        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + 2,
                glowColor
        );

        /*
         * Borders.
         */
        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + 1,
                AMOLEDTheme.BORDER_LIGHT
        );

        drawRect(
                matrices,
                panelX,
                panelY + panelHeight - 1,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + 1,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                panelX + panelWidth - 1,
                panelY,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        /*
         * Header.
         */
        drawCenteredText(
                matrices,
                "SECRET MENU",
                panelX + panelWidth / 2,
                panelY + 20,
                AMOLEDTheme.TEXT
        );

        drawCenteredText(
                matrices,
                "ExternalVisuals",
                panelX + panelWidth / 2,
                panelY + 36,
                AMOLEDTheme.TEXT_MUTED
        );

        /*
         * Divider.
         */
        drawRect(
                matrices,
                panelX + 18,
                panelY + 55,
                panelX + panelWidth - 18,
                panelY + 56,
                AMOLEDTheme.DIVIDER
        );

        /*
         * ESP.
         */
        renderModuleCard(
                matrices,
                "ESP",
                "Visual entity overlay",
                getESPEnabled(),
                panelY + 68
        );

        /*
         * Aim Assist.
         */
        renderModuleCard(
                matrices,
                "Aim Assist",
                "Smooth camera assistance",
                getAimAssistEnabled(),
                panelY + 128
        );

        /*
         * Hint.
         */
        drawCenteredText(
                matrices,
                "RMB on Aim Assist for settings",
                panelX + panelWidth / 2,
                panelY + panelHeight - 24,
                AMOLEDTheme.TEXT_MUTED
        );

        super.render(
                matrices,
                mouseX,
                mouseY,
                delta
        );
    }

    private void renderAimAssistSettings(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        /*
         * Background.
         */
        fillScreen(
                matrices,
                AMOLEDTheme.BACKGROUND
        );

        if (aimAssistSettingsPanel == null) {
            createAimAssistPanel();
        }

        if (aimAssistSettingsPanel == null) {
            drawCenteredText(
                    matrices,
                    "Aim Assist is unavailable",
                    panelX + panelWidth / 2,
                    panelY + 40,
                    AMOLEDTheme.TEXT_MUTED
            );
            return;
        }

        updateAimAssistPanelPosition();

        /*
         * Settings panel background.
         */
        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.PANEL
        );

        /*
         * Border.
         */
        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + 1,
                AMOLEDTheme.BORDER_LIGHT
        );

        drawRect(
                matrices,
                panelX,
                panelY + panelHeight - 1,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                panelX,
                panelY,
                panelX + 1,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                panelX + panelWidth - 1,
                panelY,
                panelX + panelWidth,
                panelY + panelHeight,
                AMOLEDTheme.BORDER
        );

        aimAssistSettingsPanel.render(
                matrices,
                mouseX,
                mouseY
        );

        /*
         * Back button.
         */
        int backX =
                panelX + 12;

        int backY =
                panelY
                        + panelHeight
                        - 34;

        int backWidth =
                90;

        int backHeight =
                22;

        drawRect(
                matrices,
                backX,
                backY,
                backX + backWidth,
                backY + backHeight,
                AMOLEDTheme.BUTTON
        );

        drawRect(
                matrices,
                backX,
                backY,
                backX + backWidth,
                backY + 1,
                AMOLEDTheme.BORDER
        );

        drawCenteredText(
                matrices,
                "BACK",
                backX + backWidth / 2,
                backY + 7,
                AMOLEDTheme.TEXT_LIGHT
        );

        drawCenteredText(
                matrices,
                "AIM ASSIST SETTINGS",
                panelX + panelWidth / 2,
                panelY + 8,
                AMOLEDTheme.TEXT
        );
    }

    private void renderModuleCard(
            MatrixStack matrices,
            String title,
            String description,
            boolean enabled,
            int cardY
    ) {

        int cardX =
                panelX + 18;

        int cardWidth =
                panelWidth - 36;

        int cardHeight =
                48;

        int background =
                enabled
                        ? AMOLEDTheme.SELECTED
                        : AMOLEDTheme.PANEL_LIGHT;

        drawRect(
                matrices,
                cardX,
                cardY,
                cardX + cardWidth,
                cardY + cardHeight,
                background
        );

        /*
         * Border.
         */
        drawRect(
                matrices,
                cardX,
                cardY,
                cardX + cardWidth,
                cardY + 1,
                enabled
                        ? AMOLEDTheme.SELECTED_BORDER
                        : AMOLEDTheme.BORDER
        );

        drawRect(
                matrices,
                cardX,
                cardY + cardHeight - 1,
                cardX + cardWidth,
                cardY + cardHeight,
                AMOLEDTheme.BORDER
        );

        /*
         * Active accent.
         */
        if (enabled) {

            drawRect(
                    matrices,
                    cardX,
                    cardY,
                    cardX + 3,
                    cardY + cardHeight,
                    AMOLEDTheme.ACCENT
            );
        }

        drawText(
                matrices,
                title,
                cardX + 12,
                cardY + 8,
                AMOLEDTheme.TEXT
        );

        drawText(
                matrices,
                description,
                cardX + 12,
                cardY + 25,
                AMOLEDTheme.TEXT_MUTED
        );

        /*
         * Toggle.
         */
        int toggleX =
                cardX
                        + cardWidth
                        - 52;

        int toggleY =
                cardY + 13;

        int toggleColor =
                enabled
                        ? AMOLEDTheme.TOGGLE_ON
                        : AMOLEDTheme.TOGGLE_OFF;

        drawRect(
                matrices,
                toggleX,
                toggleY,
                toggleX + 40,
                toggleY + 20,
                toggleColor
        );

        int knobX =
                enabled
                        ? toggleX + 22
                        : toggleX + 3;

        drawRect(
                matrices,
                knobX,
                toggleY + 3,
                knobX + 14,
                toggleY + 17,
                AMOLEDTheme.TOGGLE_KNOB
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Settings screen.
         */
        if (aimSettingsOpen) {

            /*
             * Back.
             */
            if (button == 0
                    && isBackButtonHovered(
                    mouseX,
                    mouseY
            )) {

                aimSettingsOpen = false;
                return true;
            }

            if (aimAssistSettingsPanel != null
                    && aimAssistSettingsPanel.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }

            return true;
        }

        /*
         * Only left/right mouse buttons.
         */
        if (button != 0 && button != 1) {

            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        /*
         * ESP.
         */
        int espY =
                panelY + 68;

        if (isInsideCard(
                mouseX,
                mouseY,
                espY
        )) {

            if (button == 0) {

                ESP esp =
                        getESP();

                if (esp != null) {
                    esp.toggle();
                }

                return true;
            }

            return true;
        }

        /*
         * Aim Assist.
         */
        int aimY =
                panelY + 128;

        if (isInsideCard(
                mouseX,
                mouseY,
                aimY
        )) {

            AimAssist aimAssist =
                    getAimAssist();

            if (button == 0) {

                if (aimAssist != null) {
                    aimAssist.toggle();
                }

                return true;
            }

            if (button == 1) {

                aimSettingsOpen = true;

                if (aimAssistSettingsPanel == null) {
                    createAimAssistPanel();
                }

                return true;
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        if (aimSettingsOpen
                && aimAssistSettingsPanel != null) {

            if (aimAssistSettingsPanel.mouseDragged(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return super.mouseDragged(
                mouseX,
                mouseY,
                button,
                deltaX,
                deltaY
        );
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (aimSettingsOpen
                && aimAssistSettingsPanel != null) {

            if (aimAssistSettingsPanel.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        /*
         * ESC from settings -> back.
         */
        if (keyCode == 256
                && aimSettingsOpen) {

            aimSettingsOpen = false;
            return true;
        }

        /*
         * ESC from secret menu -> close.
         */
        if (keyCode == 256) {

            if (client != null) {
                client.openScreen(null);
            }

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    private boolean isInsideCard(
            double mouseX,
            double mouseY,
            int cardY
    ) {

        int cardX =
                panelX + 18;

        int cardWidth =
                panelWidth - 36;

        int cardHeight =
                48;

        return mouseX >= cardX
                && mouseX <= cardX + cardWidth
                && mouseY >= cardY
                && mouseY <= cardY + cardHeight;
    }

    private boolean isBackButtonHovered(
            double mouseX,
            double mouseY
    ) {

        int backX =
                panelX + 12;

        int backY =
                panelY
                        + panelHeight
                        - 34;

        int backWidth =
                90;

        int backHeight =
                22;

        return mouseX >= backX
                && mouseX <= backX + backWidth
                && mouseY >= backY
                && mouseY <= backY + backHeight;
    }

    private ESP getESP() {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return null;
        }

        return ExternalVisuals.MODULE_MANAGER.get(
                ESP.class
        );
    }

    private AimAssist getAimAssist() {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return null;
        }

        return ExternalVisuals.MODULE_MANAGER.get(
                AimAssist.class
        );
    }

    private boolean getESPEnabled() {

        ESP esp =
                getESP();

        return esp != null
                && esp.isEnabled();
    }

    private boolean getAimAssistEnabled() {

        AimAssist aimAssist =
                getAimAssist();

        return aimAssist != null
                && aimAssist.isEnabled();
    }

    private void updatePanelSize() {

        if (client == null) {
            return;
        }

        int screenWidth =
                client.getWindow()
                        .getScaledWidth();

        int screenHeight =
                client.getWindow()
                        .getScaledHeight();

        panelWidth =
                Math.min(
                        430,
                        screenWidth - 40
                );

        panelHeight =
                Math.min(
                        270,
                        screenHeight - 40
                );

        panelWidth =
                Math.max(
                        280,
                        panelWidth
                );

        panelHeight =
                Math.max(
                        230,
                        panelHeight
                );

        panelX =
                (screenWidth - panelWidth) / 2;

        panelY =
                (screenHeight - panelHeight) / 2;

        updateAimAssistPanelPosition();
    }

    private void updateAimAssistPanelPosition() {

        if (aimAssistSettingsPanel == null) {
            return;
        }

        int settingsWidth =
                Math.min(
                        340,
                        panelWidth - 24
                );

        int settingsHeight =
                Math.min(
                        270,
                        panelHeight - 50
                );

        int settingsX =
                panelX
                        + (
                        panelWidth
                                - settingsWidth
                ) / 2;

        int settingsY =
                panelY + 10;

        aimAssistSettingsPanel.setPosition(
                settingsX,
                settingsY
        );

        aimAssistSettingsPanel.setSize(
                settingsWidth,
                settingsHeight
        );
    }

    private void fillScreen(
            MatrixStack matrices,
            int color
    ) {

        fill(
                matrices,
                0,
                0,
                width,
                height,
                color
        );
    }

    private void drawRect(
            MatrixStack matrices,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {

        fill(
                matrices,
                left,
                top,
                right,
                bottom,
                color
        );
    }

    private void drawText(
            MatrixStack matrices,
            String text,
            int x,
            int y,
            int color
    ) {

        textRenderer.draw(
                matrices,
                text,
                x,
                y,
                color
        );
    }

    private void drawCenteredText(
            MatrixStack matrices,
            String text,
            int centerX,
            int y,
            int color
    ) {

        textRenderer.draw(
                matrices,
                text,
                centerX
                        - textRenderer.getWidth(text) / 2.0f,
                y,
                color
        );
    }
}  