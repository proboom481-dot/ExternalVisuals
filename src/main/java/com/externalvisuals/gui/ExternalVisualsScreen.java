package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.Module;
import com.externalvisuals.setting.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.*;

/**
 * ExternalVisuals GUI. The layout is an original implementation inspired by
 * modern PvP visual clients: left category rail, module cards and a settings panel.
 */
public final class ExternalVisualsScreen extends Screen {
    private enum Category { COMBAT, HUD, WORLD, EFFECTS, ALL }

    private Category category = Category.ALL;
    private Module selected;
    private int moduleScroll;
    private int settingsScroll;
    private long logoWindow;
    private int logoClicks;
    private boolean secret;

    private static final int BG = 0xFF08070D;
    private static final int PANEL = 0xFF10101A;
    private static final int PANEL_2 = 0xFF151522;
    private static final int CARD = 0xFF14141F;
    private static final int CARD_HOVER = 0xFF1B1929;
    private static final int PURPLE = 0xFF9B5CFF;
    private static final int PURPLE_2 = 0xFF6D35C9;
    private static final int TEXT = 0xFFF4F0FA;
    private static final int MUTED = 0xFF858090;

    public ExternalVisualsScreen() { this(false); }
    public ExternalVisualsScreen(boolean secret) {
        super(new LiteralText(secret ? "ExternalVisuals Secret" : "ExternalVisuals"));
        this.secret = secret;
    }

    @Override
    public void render(MatrixStack m, int mouseX, int mouseY, float delta) {
        DrawableHelper.fill(m, 0, 0, width, height, BG);
        drawHeader(m, mouseX, mouseY);
        drawRail(m, mouseX, mouseY);
        drawModules(m, mouseX, mouseY);
        drawSettings(m, mouseX, mouseY);
    }

    private void drawHeader(MatrixStack m, int mx, int my) {
        DrawableHelper.fill(m, 0, 0, width, 64, 0xFF0B0A12);
        DrawableHelper.fill(m, 0, 63, width, 64, 0xFF211733);
        int logoX = 22;
        int logoColor = (mx >= logoX && mx <= logoX + 150 && my >= 14 && my <= 48) ? 0xFFB879FF : TEXT;
        textRenderer.draw(m, "EXTERNAL", logoX, 15, logoColor);
        textRenderer.draw(m, "VISUALS", logoX, 31, PURPLE);
        textRenderer.draw(m, secret ? "SECRET CORE" : "VISUAL CORE", 174, 28, secret ? 0xFFE05CFF : MUTED);
        textRenderer.draw(m, "1.16.5", width - 52, 27, MUTED);
    }

    private void drawRail(MatrixStack m, int mx, int my) {
        int x = 14, y = 78, w = 142;
        DrawableHelper.fill(m, x, y, x + w, height - 14, PANEL);
        textRenderer.draw(m, secret ? "SECRET" : "CATEGORIES", x + 14, y + 14, PURPLE);
        int yy = y + 40;
        Category[] cats = {Category.ALL, Category.COMBAT, Category.HUD, Category.WORLD, Category.EFFECTS};
        for (Category c : cats) {
            boolean active = c == category;
            boolean hover = mx >= x + 7 && mx <= x + w - 7 && my >= yy && my < yy + 28;
            if (active || hover) DrawableHelper.fill(m, x + 7, yy, x + w - 7, yy + 28, active ? 0xFF291A3B : 0xFF1A1724);
            textRenderer.draw(m, label(c), x + 17, yy + 9, active ? TEXT : MUTED);
            yy += 34;
        }
        if (!secret) {
            textRenderer.draw(m, "5× logo → Secret", x + 12, height - 36, 0xFF5F586A);
        } else {
            textRenderer.draw(m, "5× logo → back", x + 12, height - 36, 0xFF5F586A);
        }
    }

    private String label(Category c) {
        switch (c) {
            case COMBAT: return "Combat Visuals";
            case HUD: return "HUD";
            case WORLD: return "World";
            case EFFECTS: return "Effects";
            default: return "All Visuals";
        }
    }

    private void drawModules(MatrixStack m, int mx, int my) {
        int left = 170, top = 78, right = selected == null ? width - 14 : width - 350;
        DrawableHelper.fill(m, left, top, right, height - 14, PANEL);
        textRenderer.draw(m, secret ? "Secret modules" : label(category), left + 16, top + 14, TEXT);
        textRenderer.draw(m, "click a card to toggle • right panel for settings", left + 16, top + 31, MUTED);

        List<Module> modules = filtered();
        int cardW = Math.max(150, (right - left - 28) / 2);
        int cardH = 62;
        int gap = 10;
        int startY = top + 52 + moduleScroll;
        for (int i = 0; i < modules.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            int x = left + 10 + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            if (y + cardH < top + 48 || y > height - 20) continue;
            boolean hover = mx >= x && mx <= x + cardW && my >= y && my <= y + cardH;
            int base = hover ? CARD_HOVER : CARD;
            if (selected == modules.get(i)) base = 0xFF241B35;
            DrawableHelper.fill(m, x, y, x + cardW, y + cardH, base);
            if (modules.get(i).isEnabled()) DrawableHelper.fill(m, x, y, x + 3, y + cardH, PURPLE);
            textRenderer.draw(m, modules.get(i).getName(), x + 12, y + 12, TEXT);
            textRenderer.draw(m, modules.get(i).isEnabled() ? "ON" : "OFF", x + cardW - 32, y + 12, modules.get(i).isEnabled() ? 0xFFB879FF : MUTED);
            textRenderer.draw(m, shortDescription(modules.get(i)), x + 12, y + 34, MUTED);
        }
    }

    private String shortDescription(Module mod) {
        String n = mod.getName();
        if (n.equals("Hit Particles")) return "impact particles & burst";
        if (n.equals("Damage Numbers")) return "floating damage text";
        if (n.equals("Target HUD")) return "target info overlay";
        if (n.equals("Critical Effects")) return "crit pulse & particles";
        if (n.equals("Trajectory")) return "projectile prediction";
        if (n.equals("Hitmarker")) return "center hit feedback";
        if (n.equals("Low HP Warning")) return "dynamic screen pulse";
        if (n.equals("Custom Crosshair")) return "dynamic reticle";
        if (n.equals("Kill Effects")) return "custom kill burst";
        if (n.equals("Armor & Potion HUD")) return "equipment & effects";
        return "external visual module";
    }

    private void drawSettings(MatrixStack m, int mx, int my) {
        if (selected == null) return;
        int x = width - 340, y = 78, w = 326;
        DrawableHelper.fill(m, x, y, width - 14, height - 14, PANEL_2);
        textRenderer.draw(m, selected.getName(), x + 16, y + 15, TEXT);
        textRenderer.draw(m, selected.isEnabled() ? "Enabled" : "Disabled", x + 16, y + 33, selected.isEnabled() ? 0xFFB879FF : MUTED);
        int sy = y + 58 + settingsScroll;
        for (Setting<?> setting : selected.getSettings()) {
            if (sy + 50 >= y + 46 && sy < height - 22) drawSetting(m, setting, x + 14, sy, w - 28, mx, my);
            sy += 58;
        }
    }

    private void drawSetting(MatrixStack m, Setting<?> s, int x, int y, int w, int mx, int my) {
        textRenderer.draw(m, s.getName(), x, y, TEXT);
        String value = String.valueOf(s.getValue());
        if (s instanceof BooleanSetting) {
            boolean on = (Boolean) s.getValue();
            DrawableHelper.fill(m, x + w - 38, y - 3, x + w, y + 15, on ? 0xFF6D35C9 : 0xFF282631);
            DrawableHelper.fill(m, on ? x + w - 18 : x + w - 34, y, on ? x + w - 4 : x + w - 20, y + 12, 0xFFEDE8F7);
        } else if (s instanceof SliderSetting) {
            SliderSetting slider = (SliderSetting) s;
            double min = slider.getMin(), max = slider.getMax(), cur = ((Number) slider.getValue()).doubleValue();
            double ratio = (cur - min) / Math.max(0.0001, max - min);
            int barY = y + 18;
            DrawableHelper.fill(m, x, barY, x + w, barY + 4, 0xFF292532);
            DrawableHelper.fill(m, x, barY, x + (int) (w * ratio), barY + 4, PURPLE);
            DrawableHelper.fill(m, x + (int) (w * ratio) - 4, barY - 4, x + (int) (w * ratio) + 4, barY + 8, 0xFFEDE8F7);
            textRenderer.draw(m, value, x + w - textRenderer.getWidth(value), y, MUTED);
        } else {
            textRenderer.draw(m, value, x + w - textRenderer.getWidth(value), y, 0xFFB879FF);
        }
    }

    private List<Module> filtered() {
        List<Module> all = new ArrayList<>(ExternalVisuals.MODULES.all());
        if (category == Category.ALL || secret) return all;
        List<Module> out = new ArrayList<>();
        for (Module m : all) {
            String n = m.getName().toLowerCase(Locale.ROOT);
            if (category == Category.HUD && (n.contains("hud") || n.contains("crosshair") || n.contains("damage") || n.contains("hitmarker"))) out.add(m);
            else if (category == Category.COMBAT && (n.contains("target") || n.contains("critical") || n.contains("hit") || n.contains("kill"))) out.add(m);
            else if (category == Category.WORLD && n.contains("trajectory")) out.add(m);
            else if (category == Category.EFFECTS && (n.contains("particle") || n.contains("effect") || n.contains("low"))) out.add(m);
        }
        return out;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= 18 && mouseX <= 160 && mouseY >= 10 && mouseY <= 54 && button == 0) {
            long now = System.currentTimeMillis();
            if (now - logoWindow > 2000) { logoWindow = now; logoClicks = 0; }
            logoClicks++;
            if (logoClicks >= 5) { logoClicks = 0; secret = !secret; selected = null; moduleScroll = settingsScroll = 0; }
            return true;
        }
        if (mouseX >= 14 && mouseX <= 156 && mouseY >= 118 && mouseY < 118 + 34 * 5) {
            int idx = (int) ((mouseY - 118) / 34);
            category = new Category[]{Category.ALL, Category.COMBAT, Category.HUD, Category.WORLD, Category.EFFECTS}[Math.max(0, Math.min(4, idx))];
            selected = null; moduleScroll = 0;
            return true;
        }
        List<Module> modules = filtered();
        int left = 170, right = selected == null ? width - 14 : width - 350;
        int cardW = Math.max(150, (right - left - 28) / 2), cardH = 62, gap = 10, startY = 130 + moduleScroll;
        for (int i = 0; i < modules.size(); i++) {
            int col = i % 2, row = i / 2;
            int x = left + 10 + col * (cardW + gap), y = startY + row * (cardH + gap);
            if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                if (button == 0) { selected = modules.get(i); selected.toggle(); }
                else if (button == 1) selected = modules.get(i);
                return true;
            }
        }
        if (selected != null && mouseX >= width - 340 && mouseY >= 132) {
            int row = (int) ((mouseY - 136 - settingsScroll) / 58);
            if (row >= 0 && row < selected.getSettings().size()) {
                Setting<?> s = selected.getSettings().get(row);
                if (s instanceof BooleanSetting) ((BooleanSetting) s).toggle();
                else if (s instanceof ModeSetting) ((ModeSetting) s).cycle();
                else if (s instanceof SliderSetting) {
                    SliderSetting sl = (SliderSetting) s;
                    double ratio = Math.max(0, Math.min(1, (mouseX - (width - 326)) / 298.0));
                    sl.setSliderValue(sl.getMin() + (sl.getMax() - sl.getMin()) * ratio);
                }
                return true;
            }
        }
        return true;
    }

    @Override public boolean mouseScrolled(double x, double y, double amount) {
        if (x >= 170 && (selected == null || x < width - 350)) {
            moduleScroll = clampScroll(moduleScroll + (int) (amount * 24), filtered().size(), 2, 62, height - 140);
        } else if (selected != null) {
            settingsScroll = Math.min(0, settingsScroll + (int) (amount * 24));
        }
        return true;
    }

    private int clampScroll(int value, int count, int columns, int rowHeight, int viewport) {
        int rows = (count + columns - 1) / columns;
        int max = Math.max(0, rows * rowHeight - viewport);
        return Math.max(-max, Math.min(0, value));
    }
}
