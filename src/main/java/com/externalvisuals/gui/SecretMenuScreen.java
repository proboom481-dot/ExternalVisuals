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
    private int moduleScroll;

    public SecretMenuScreen() {
        super(new LiteralText("ExternalVisuals Secret"));
    }

    @Override
    protected void init() {
        super.init();
        updatePanelSize();
        moduleScroll = 0;
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
                "VISUAL LAB  •  advanced ESP, effects & targeting",
                panelX + 22, panelY + 36, AMOLEDTheme.TEXT_MUTED);

        drawStatusPill(matrices, mouseX, mouseY);

        List<Module> modules = getSecretModules();
        int left = panelX + 20;
        int top = panelY + 68;
        int gap = 12;
        int columns = 3;
        int cardWidth = (panelWidth - 40 - gap * (columns - 1)) / columns;
        int cardHeight = 82;

        int rows = Math.max(1, (modules.size() + columns - 1) / columns);
        int visibleRows = Math.max(1, (panelHeight - 68 - 74) / (cardHeight + gap));
        int maxScroll = Math.max(0, rows - visibleRows);
        moduleScroll = Math.max(0, Math.min(moduleScroll, maxScroll));

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int column = i % columns;
            int row = i / columns;
            int visibleRow = row - moduleScroll;
            if (visibleRow < 0 || visibleRow >= visibleRows) continue;
            int x = left + column * (cardWidth + gap);
            int y = top + visibleRow * (cardHeight + gap);
            drawModuleCard(matrices, mouseX, mouseY,
                    x, y, cardWidth, cardHeight, module);
        }

        int hintY = top + visibleRows * (cardHeight + gap) + 2;
        drawScrollButtons(matrices, mouseX, mouseY,
                panelX + panelWidth - 154, hintY, maxScroll);

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

    private void drawScrollButtons(MatrixStack matrices, int mouseX, int mouseY,
                                    int x, int y, int maxScroll) {
        int w = 62;
        int h = 22;
        boolean up = inside(mouseX, mouseY, x, y, w, h);
        boolean down = inside(mouseX, mouseY, x + w + 6, y, w, h);
        drawButton(matrices, mouseX, mouseY, x, y, w, h, "UP");
        drawButton(matrices, mouseX, mouseY, x + w + 6, y, w, h, "DOWN");
        if (moduleScroll <= 0) fill(matrices, x, y, x + w, y + h, AMOLEDTheme.BUTTON);
        if (moduleScroll >= maxScroll) fill(matrices, x + w + 6, y, x + w * 2 + 6, y + h, AMOLEDTheme.BUTTON);
        if (up || down) { /* hover is rendered by drawButton */ }
    }

    private void drawStatusPill(MatrixStack matrices, int mouseX, int mouseY) {
        String status = getSecretModules().size() + " VISUAL MODULES";
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

        String detail;
        String name = module.getName();
        if ("ESP".equalsIgnoreCase(name)) {
            detail = "Boxes • Tracers • Glow • Target";
        } else if ("Aim Assist".equalsIgnoreCase(name)) {
            detail = "FOV • Smooth • Prediction • Priority";
        } else if ("Target Ring".equalsIgnoreCase(name)) {
            detail = "Ring • Orbit • Helix • Pulse";
        } else if ("Trajectory Prediction".equalsIgnoreCase(name)) {
            detail = "Path • Impact • Color • Range";
        } else if ("Pulse Visuals".equalsIgnoreCase(name)) {
            detail = "Hit FX • Particles • Island • Flash";
        } else if ("Advanced Visuals".equalsIgnoreCase(name)) {
            detail = "Vignette • Arrow • Crosshair • Info";
        } else if ("Combat Visuals".equalsIgnoreCase(name)) {
            detail = "Target Card • Direction • Cooldown";
        } else if ("Hit Particles".equalsIgnoreCase(name)) {
            detail = "Critical • Damage • Burst • Trail";
        } else {
            detail = "Hit Flash • Effects • Impact FX";
        }
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
        int rowsForScroll = Math.max(1, (modules.size() + 2) / 3);
        int visibleRowsForScroll = Math.max(1, (panelHeight - 68 - 74) / (82 + 12));
        int maxScroll = Math.max(0, rowsForScroll - visibleRowsForScroll);
        int scrollY = panelY + 68 + visibleRowsForScroll * (82 + 12) + 2;
        int scrollX = panelX + panelWidth - 154;
        if (button == 0 && maxScroll > 0 && mouseY >= scrollY && mouseY <= scrollY + 22) {
            if (mouseX >= scrollX && mouseX <= scrollX + 62) {
                moduleScroll = Math.max(0, moduleScroll - 1);
                return true;
            }
            if (mouseX >= scrollX + 68 && mouseX <= scrollX + 130) {
                moduleScroll = Math.min(maxScroll, moduleScroll + 1);
                return true;
            }
        }
        int left = panelX + 20;
        int top = panelY + 68;
        int gap = 12;
        int columns = 3;
        int cardWidth = (panelWidth - 40 - gap * (columns - 1)) / columns;
        int cardHeight = 82;

        for (int i = 0; i < modules.size(); i++) {
            int column = i % columns;
            int row = i / columns;
            int visibleRow = row - moduleScroll;
            if (visibleRow < 0 || visibleRow >= visibleRowsForScroll) continue;
            int x = left + column * (cardWidth + gap);
            int y = top + visibleRow * (cardHeight + gap);
            if (inside(mouseX, mouseY, x, y, cardWidth, cardHeight)) {
                Module module = modules.get(i);
                int settingsWidth = 82;
                int settingsHeight = 18;
                int settingsX = x + cardWidth - settingsWidth - 10;
                int settingsY = y + cardHeight - settingsHeight - 8;
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
        if (keyCode == 265 || keyCode == 266) {
            moduleScroll = Math.max(0, moduleScroll - 1);
            return true;
        }
        if (keyCode == 264 || keyCode == 267) {
            List<Module> modules = getSecretModules();
            int rows = Math.max(1, (modules.size() + 2) / 3);
            int visibleRows = Math.max(1, (panelHeight - 68 - 74) / (82 + 12));
            moduleScroll = Math.min(Math.max(0, rows - visibleRows), moduleScroll + 1);
            return true;
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
            if (ModernGuiRenderer.isSecretModule(module)) result.add(module);
        }
        return result;
    }

    private void updatePanelSize() {
        if (client == null) return;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        panelWidth = Math.min(760, Math.max(500, screenWidth - 28));
        panelHeight = Math.min(520, Math.max(420, screenHeight - 28));
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
