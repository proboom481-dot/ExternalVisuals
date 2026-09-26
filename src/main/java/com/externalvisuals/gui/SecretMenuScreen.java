package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.config.ConfigManager;
import com.externalvisuals.module.Module;
import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.modules.visual.ESP;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public final class SecretMenuScreen extends Screen {

    private static final Identifier ACCENT_TEXTURE =
            new Identifier("externalvisuals", "textures/gui/accent_noise.png");

    private final ModuleSettingsPanel settingsPanel = new ModuleSettingsPanel();
    private int panelX, panelY, panelWidth, panelHeight;

    public SecretMenuScreen() {
        super(new LiteralText("ExternalVisuals Secret"));
    }

    @Override
    protected void init() {
        super.init();
        updatePanelSize();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        updatePanelSize();

        fill(matrices, 0, 0, width, height, AMOLEDTheme.BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        client.getTextureManager().bindTexture(ACCENT_TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, width, height, 768, 768);
        RenderSystem.disableBlend();

        fill(matrices, panelX - 3, panelY - 3,
                panelX + panelWidth + 3, panelY + panelHeight + 3,
                AMOLEDTheme.GLOW_STRONG);
        fill(matrices, panelX, panelY,
                panelX + panelWidth, panelY + panelHeight,
                AMOLEDTheme.PANEL);
        fill(matrices, panelX, panelY,
                panelX + panelWidth, panelY + 3,
                AMOLEDTheme.ACCENT);
        fill(matrices, panelX, panelY + 3,
                panelX + panelWidth, panelY + 4,
                AMOLEDTheme.ACCENT_DARK);

        textRenderer.drawWithShadow(matrices, "SECRET MENU",
                panelX + 22, panelY + 18, AMOLEDTheme.TEXT);
        textRenderer.drawWithShadow(matrices,
                "ESP + AIM LAB  •  advanced visual targeting",
                panelX + 22, panelY + 36, AMOLEDTheme.TEXT_MUTED);

        drawStatusPill(matrices, mouseX, mouseY);

        List<Module> modules = getSecretModules();
        int left = panelX + 20;
        int top = panelY + 68;
        int gap = 14;
        int cardWidth = (panelWidth - 54) / 2;
        int cardHeight = 86;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int x = left + i * (cardWidth + gap);
            drawModuleCard(matrices, mouseX, mouseY,
                    x, top, cardWidth, cardHeight, module);
        }

        int hintY = top + cardHeight + 18;
        textRenderer.drawWithShadow(matrices,
                "LMB  Enable / Disable",
                left, hintY, AMOLEDTheme.TEXT_MUTED);
        textRenderer.drawWithShadow(matrices,
                "RMB  Open settings",
                left, hintY + 16, AMOLEDTheme.TEXT_MUTED);

        int backWidth = 112;
        int backHeight = 28;
        int backX = panelX + (panelWidth - backWidth) / 2;
        int backY = panelY + panelHeight - 48;
        drawButton(matrices, mouseX, mouseY,
                backX, backY, backWidth, backHeight, "BACK");

        if (settingsPanel.isOpen()) {
            int settingsWidth = Math.min(360, panelWidth - 30);
            int settingsHeight = Math.min(500, panelHeight - 30);
            settingsPanel.setPosition(
                    panelX + (panelWidth - settingsWidth) / 2,
                    panelY + 15
            );
            settingsPanel.setSize(settingsWidth, settingsHeight);
            settingsPanel.render(matrices, mouseX, mouseY);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawStatusPill(MatrixStack matrices, int mouseX, int mouseY) {
        String status = "2 ADVANCED MODULES";
        int width = textRenderer.getWidth(status) + 20;
        int x = panelX + panelWidth - width - 18;
        int y = panelY + 17;
        boolean hovered = inside(mouseX, mouseY, x, y, width, 20);
        fill(matrices, x, y, x + width, y + 20,
                hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        textRenderer.drawWithShadow(matrices, status, x + 10, y + 6,
                AMOLEDTheme.ACCENT_LIGHT);
    }

    private void drawModuleCard(MatrixStack matrices, int mouseX, int mouseY,
                                int x, int y, int width, int height, Module module) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        int background = module.isEnabled()
                ? (hovered ? AMOLEDTheme.BUTTON_ACTIVE : AMOLEDTheme.SELECTED)
                : (hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        int accent = module.isEnabled() ? AMOLEDTheme.ACCENT : AMOLEDTheme.BORDER_LIGHT;

        fill(matrices, x, y, x + width, y + height, background);
        fill(matrices, x, y, x + width, y + 2, accent);
        fill(matrices, x, y + height - 1, x + width, y + height, accent);

        textRenderer.drawWithShadow(matrices, module.getName(),
                x + 14, y + 15, AMOLEDTheme.TEXT);
        String state = module.isEnabled() ? "ON" : "OFF";
        textRenderer.drawWithShadow(matrices, state,
                x + width - textRenderer.getWidth(state) - 14,
                y + 15,
                module.isEnabled() ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.TEXT_MUTED);

        String detail = module instanceof ESP
                ? "Boxes • Tracers • Names • Health • Trail"
                : "Range • FOV • Speed • Target Point";
        textRenderer.drawWithShadow(matrices, detail,
                x + 14, y + 37, AMOLEDTheme.TEXT_MUTED);

        int settingsWidth = 82;
        int settingsHeight = 18;
        int settingsX = x + width - settingsWidth - 10;
        int settingsY = y + height - settingsHeight - 8;
        boolean settingsHover = inside(mouseX, mouseY, settingsX, settingsY, settingsWidth, settingsHeight);
        fill(matrices, settingsX, settingsY, settingsX + settingsWidth, settingsY + settingsHeight,
                settingsHover ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        textRenderer.drawWithShadow(matrices, "SETTINGS", settingsX + 8, settingsY + 5,
                settingsHover ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.TEXT_MUTED);
    }

    private void drawButton(MatrixStack matrices, int mouseX, int mouseY,
                            int x, int y, int width, int height, String text) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        fill(matrices, x, y, x + width, y + height,
                hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        fill(matrices, x, y, x + width, y + 1,
                hovered ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.BORDER);
        textRenderer.drawWithShadow(matrices, text,
                x + (width - textRenderer.getWidth(text)) / 2.0f,
                y + 9, hovered ? AMOLEDTheme.TEXT : AMOLEDTheme.TEXT_LIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 && button != 1) return super.mouseClicked(mouseX, mouseY, button);
        updatePanelSize();

        if (settingsPanel.isOpen()) {
            if (settingsPanel.mouseClicked(mouseX, mouseY, button)) return true;
            if (button == 0) {
                settingsPanel.close();
                return true;
            }
            return true;
        }

        List<Module> modules = getSecretModules();
        int left = panelX + 20;
        int top = panelY + 68;
        int gap = 14;
        int cardWidth = (panelWidth - 54) / 2;
        int cardHeight = 86;

        for (int i = 0; i < modules.size(); i++) {
            int x = left + i * (cardWidth + gap);
            if (inside(mouseX, mouseY, x, top, cardWidth, cardHeight)) {
                Module module = modules.get(i);
                int settingsWidth = 82;
                int settingsHeight = 18;
                int settingsX = x + cardWidth - settingsWidth - 10;
                int settingsY = top + cardHeight - settingsHeight - 8;
                if (button == 0 && inside(mouseX, mouseY, settingsX, settingsY, settingsWidth, settingsHeight)) {
                    settingsPanel.open(module);
                } else if (button == 1) {
                    settingsPanel.open(module);
                } else {
                    module.toggle();
                }
                return true;
            }
        }

        int backWidth = 112;
        int backHeight = 28;
        int backX = panelX + (panelWidth - backWidth) / 2;
        int backY = panelY + panelHeight - 48;
        if (inside(mouseX, mouseY, backX, backY, backWidth, backHeight)) {
            if (client != null) client.openScreen(null);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,
                                double deltaX, double deltaY) {
        if (settingsPanel.isOpen()
                && settingsPanel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (settingsPanel.isOpen()
                && settingsPanel.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (settingsPanel.isOpen()
                && settingsPanel.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (settingsPanel.isOpen()
                && settingsPanel.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (settingsPanel.isOpen()) {
            if (keyCode == 256) {
                settingsPanel.close();
                return true;
            }
            if (settingsPanel.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        if (keyCode == 256) {
            if (client != null) client.openScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private List<Module> getSecretModules() {
        List<Module> result = new ArrayList<>();
        if (ExternalVisuals.MODULE_MANAGER == null) return result;
        for (Module module : ExternalVisuals.MODULE_MANAGER.getModules()) {
            if (module instanceof ESP || module instanceof AimAssist) result.add(module);
        }
        return result;
    }

    private void updatePanelSize() {
        if (client == null) return;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        panelWidth = Math.min(760, Math.max(500, screenWidth - 28));
        panelHeight = Math.min(420, Math.max(330, screenHeight - 28));
        panelX = (screenWidth - panelWidth) / 2;
        panelY = (screenHeight - panelHeight) / 2;
    }

    private boolean inside(double mouseX, double mouseY,
                           int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public void removed() {
        ConfigManager.save();
        super.removed();
    }
}
