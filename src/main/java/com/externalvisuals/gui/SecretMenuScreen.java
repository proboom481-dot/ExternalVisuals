package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.config.ConfigManager;
import com.externalvisuals.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Hidden advanced module screen. It is intentionally separate from the normal
 * ClickGUI so advanced combat/render modules do not clutter the public menu.
 */
public final class SecretMenuScreen extends Screen {

    private static final Identifier ACCENT_TEXTURE =
            new Identifier("externalvisuals", "textures/gui/accent_noise.png");

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private boolean disableAllArmed;

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

        fill(matrices, panelX - 2, panelY - 2,
                panelX + panelWidth + 2, panelY + panelHeight + 2,
                AMOLEDTheme.GLOW);
        fill(matrices, panelX, panelY,
                panelX + panelWidth, panelY + panelHeight,
                AMOLEDTheme.PANEL);
        fill(matrices, panelX, panelY,
                panelX + panelWidth, panelY + 2,
                AMOLEDTheme.ACCENT);
        fill(matrices, panelX, panelY,
                panelX + 1, panelY + panelHeight,
                AMOLEDTheme.BORDER);
        fill(matrices, panelX + panelWidth - 1, panelY,
                panelX + panelWidth, panelY + panelHeight,
                AMOLEDTheme.BORDER);

        drawCenteredText(matrices, "SECRET MENU",
                panelX + panelWidth / 2, panelY + 15,
                AMOLEDTheme.TEXT);
        drawCenteredText(matrices, "advanced modules • profiles • panic",
                panelX + panelWidth / 2, panelY + 32,
                AMOLEDTheme.TEXT_MUTED);

        List<Module> modules = getSecretModules();
        int left = panelX + 18;
        int top = panelY + 52;
        int gap = 7;
        int buttonWidth = (panelWidth - 54) / 2;
        int buttonHeight = 30;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int column = i % 2;
            int row = i / 2;
            int x = left + column * (buttonWidth + gap);
            int y = top + row * (buttonHeight + gap);
            drawModuleButton(matrices, mouseX, mouseY,
                    x, y, buttonWidth, buttonHeight, module);
        }

        int rows = (modules.size() + 1) / 2;
        int utilityTop = top + rows * (buttonHeight + gap) + 10;
        int utilityHeight = 26;

        int profileWidth = (panelWidth - 54) / 3;
        drawButton(matrices, mouseX, mouseY,
                left, utilityTop, profileWidth, utilityHeight, "SAVE 1");
        drawButton(matrices, mouseX, mouseY,
                left + profileWidth + gap, utilityTop,
                profileWidth, utilityHeight, "LOAD 1");
        drawButton(matrices, mouseX, mouseY,
                left + (profileWidth + gap) * 2, utilityTop,
                profileWidth, utilityHeight, "SAVE 2");

        int utilityTop2 = utilityTop + utilityHeight + gap;
        drawButton(matrices, mouseX, mouseY,
                left, utilityTop2, profileWidth, utilityHeight, "LOAD 2");
        drawButton(matrices, mouseX, mouseY,
                left + profileWidth + gap, utilityTop2,
                profileWidth, utilityHeight, "SAVE 3");
        drawButton(matrices, mouseX, mouseY,
                left + (profileWidth + gap) * 2, utilityTop2,
                profileWidth, utilityHeight, "LOAD 3");

        int dangerY = utilityTop2 + utilityHeight + 8;
        int dangerWidth = panelWidth - 36;
        drawButton(matrices, mouseX, mouseY,
                left, dangerY, dangerWidth, 27,
                disableAllArmed ? "CLICK AGAIN TO PANIC DISABLE" : "PANIC DISABLE ALL");

        int backWidth = 90;
        int backHeight = 23;
        int backX = panelX + panelWidth / 2 - backWidth / 2;
        int backY = panelY + panelHeight - 32;
        drawButton(matrices, mouseX, mouseY,
                backX, backY, backWidth, backHeight, "BACK");

        drawCenteredText(matrices,
                "5 title clicks • profiles: config/externalvisuals/",
                panelX + panelWidth / 2,
                panelY + panelHeight - 14,
                AMOLEDTheme.TEXT_MUTED);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        updatePanelSize();
        List<Module> modules = getSecretModules();

        int left = panelX + 18;
        int top = panelY + 52;
        int gap = 7;
        int buttonWidth = (panelWidth - 54) / 2;
        int buttonHeight = 30;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int column = i % 2;
            int row = i / 2;
            int x = left + column * (buttonWidth + gap);
            int y = top + row * (buttonHeight + gap);

            if (inside(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
                module.toggle();
                return true;
            }
        }

        int rows = (modules.size() + 1) / 2;
        int utilityTop = top + rows * (buttonHeight + gap) + 10;
        int utilityHeight = 26;
        int profileWidth = (panelWidth - 54) / 3;

        if (inside(mouseX, mouseY, left, utilityTop,
                profileWidth, utilityHeight)) {
            ConfigManager.save("profile1");
            return true;
        }
        if (inside(mouseX, mouseY, left + profileWidth + gap, utilityTop,
                profileWidth, utilityHeight)) {
            ConfigManager.load("profile1");
            return true;
        }
        if (inside(mouseX, mouseY, left + (profileWidth + gap) * 2, utilityTop,
                profileWidth, utilityHeight)) {
            ConfigManager.save("profile2");
            return true;
        }

        int utilityTop2 = utilityTop + utilityHeight + gap;
        if (inside(mouseX, mouseY, left, utilityTop2,
                profileWidth, utilityHeight)) {
            ConfigManager.load("profile2");
            return true;
        }
        if (inside(mouseX, mouseY, left + profileWidth + gap, utilityTop2,
                profileWidth, utilityHeight)) {
            ConfigManager.save("profile3");
            return true;
        }
        if (inside(mouseX, mouseY, left + (profileWidth + gap) * 2, utilityTop2,
                profileWidth, utilityHeight)) {
            ConfigManager.load("profile3");
            return true;
        }

        int dangerY = utilityTop2 + utilityHeight + 8;
        int dangerWidth = panelWidth - 36;
        if (inside(mouseX, mouseY, left, dangerY, dangerWidth, 27)) {
            if (!disableAllArmed) {
                disableAllArmed = true;
            } else {
                disableAllModules();
                ConfigManager.save();
                disableAllArmed = false;
            }
            return true;
        }

        int backWidth = 90;
        int backHeight = 23;
        int backX = panelX + panelWidth / 2 - backWidth / 2;
        int backY = panelY + panelHeight - 32;
        if (inside(mouseX, mouseY, backX, backY, backWidth, backHeight)) {
            if (client != null) {
                client.openScreen(null);
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (client != null) {
                client.openScreen(null);
            }
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            disableAllModules();
            ConfigManager.save();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private List<Module> getSecretModules() {
        List<Module> result = new ArrayList<>();
        if (ExternalVisuals.MODULE_MANAGER == null) {
            return result;
        }
        for (Module module : ExternalVisuals.MODULE_MANAGER.getModules()) {
            if (ModernGuiRenderer.isSecretModule(module)) {
                result.add(module);
            }
        }
        return result;
    }

    private void disableAllModules() {
        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }
        for (Module module : ExternalVisuals.MODULE_MANAGER.getModules()) {
            if (module != null) {
                try {
                    module.setEnabled(false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    private void updatePanelSize() {
        if (client == null) {
            return;
        }
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        panelWidth = Math.min(620, Math.max(340, screenWidth - 24));
        panelHeight = Math.min(620, Math.max(400, screenHeight - 24));
        panelX = (screenWidth - panelWidth) / 2;
        panelY = (screenHeight - panelHeight) / 2;
    }

    private void drawModuleButton(MatrixStack matrices, int mouseX, int mouseY,
                                  int x, int y, int width, int height, Module module) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        int background = module.isEnabled()
                ? (hovered ? AMOLEDTheme.BUTTON_ACTIVE : AMOLEDTheme.SELECTED)
                : (hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        int border = module.isEnabled()
                ? AMOLEDTheme.SELECTED_BORDER
                : (hovered ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.BORDER);

        fill(matrices, x, y, x + width, y + height, background);
        fill(matrices, x, y, x + width, y + 1, border);
        fill(matrices, x, y + height - 1, x + width, y + height, border);

        String state = module.isEnabled() ? "ON" : "OFF";
        String text = module.getName();
        textRenderer.drawWithShadow(matrices, text, x + 8, y + 6,
                module.isEnabled() ? AMOLEDTheme.TEXT : AMOLEDTheme.TEXT_LIGHT);
        textRenderer.drawWithShadow(matrices, state,
                x + width - textRenderer.getWidth(state) - 8, y + 6,
                module.isEnabled() ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.TEXT_MUTED);
    }

    private void drawButton(MatrixStack matrices, int mouseX, int mouseY,
                            int x, int y, int width, int height, String text) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        fill(matrices, x, y, x + width, y + height,
                hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        fill(matrices, x, y, x + width, y + 1,
                hovered ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.BORDER);
        drawCenteredText(matrices, text, x + width / 2, y + 8,
                hovered ? AMOLEDTheme.TEXT : AMOLEDTheme.TEXT_LIGHT);
    }

    private boolean inside(double mouseX, double mouseY,
                           int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + height;
    }

    private void drawCenteredText(MatrixStack matrices, String text,
                                  int centerX, int y, int color) {
        textRenderer.draw(matrices, text,
                centerX - textRenderer.getWidth(text) / 2.0f,
                y, color);
    }
}
