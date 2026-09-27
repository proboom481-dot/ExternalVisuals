package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.CombatVisuals;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Deque;

/** Lightweight HUD effects for target feedback and combat readability. */
public final class CombatVisualsRenderer {
    private static final Deque<Integer> FPS = new ArrayDeque<>();
    private static long lastSample;

    private CombatVisualsRenderer() {}

    public static void init() {
        HudRenderCallback.EVENT.register(CombatVisualsRenderer::render);
    }

    private static void render(MatrixStack m, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        CombatVisuals fx = ExternalVisuals.MODULE_MANAGER == null ? null :
                ExternalVisuals.MODULE_MANAGER.get(CombatVisuals.class);
        if (mc == null || mc.player == null || mc.world == null || fx == null || !fx.isEnabled()) return;

        try {
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();
            LivingEntity target = mc.targetedEntity instanceof LivingEntity && ((LivingEntity) mc.targetedEntity).isAlive()
                    ? (LivingEntity) mc.targetedEntity : null;

            if (fx.showTargetCard() && target != null) drawTargetCard(m, mc, fx, target, w, h);
            if (fx.showHurtDirection() && mc.player.hurtTime > 0 && mc.player.getAttacker() != null)
                drawHurtDirection(m, mc, fx, w, h, mc.player.getAttacker());
            if (fx.showAttackCooldown()) drawCooldown(m, mc, fx, w, h);
            if (fx.showSprintIndicator()) drawSprint(m, mc, fx, w, h);
            if (fx.showPositionPill()) drawPosition(m, mc, fx, w, h);
            if (fx.showDirectionCompass()) drawCompass(m, mc, fx, w, h);
            if (fx.showWatermark()) drawWatermark(m, mc, fx, w, h);
            sampleFps(mc, fx.getGraphLength());
            if (fx.showFpsGraph()) drawFpsGraph(m, mc, fx, w, h);
        } catch (Throwable ignored) {
            // Visual-only renderer must never break the HUD.
        }
    }

    private static void drawTargetCard(MatrixStack m, MinecraftClient mc, CombatVisuals fx, LivingEntity t, int w, int h) {
        int x = 10, y = h - 92, width = 170, height = 68;
        int accent = fx.getTargetColor();
        if (fx.showTargetPulse()) {
            float pulse = (float)(0.72 + 0.28 * Math.sin(System.currentTimeMillis() / 1000.0 * 5.0));
            accent = (Math.max(90, Math.min(255, (int)(255 * pulse))) << 24) | (accent & 0x00FFFFFF);
        }
        DrawableHelper.fill(m, x, y, x + width, y + height, 0xB00A0A12);
        DrawableHelper.fill(m, x, y, x + width, y + 2, accent);
        mc.textRenderer.drawWithShadow(m, t.getDisplayName().getString(), x + 10, y + 9, 0xFFF4F1FF);

        float max = Math.max(1.0f, t.getMaxHealth());
        float hp = MathHelper.clamp(t.getHealth() / max, 0.0f, 1.0f);
        int barW = width - 20;
        DrawableHelper.fill(m, x + 10, y + 27, x + 10 + barW, y + 34, 0xFF20202A);
        DrawableHelper.fill(m, x + 10, y + 27, x + 10 + (int)(barW * hp), y + 34, accent);

        StringBuilder info = new StringBuilder();
        if (fx.showTargetHealth()) info.append(String.format("HP %.1f", t.getHealth()));
        if (fx.showTargetDistance()) append(info, String.format("%.1fm", mc.player.distanceTo(t)));
        if (fx.showTargetArmor()) append(info, "ARM " + armorPercent(t));
        mc.textRenderer.drawWithShadow(m, info.toString(), x + 10, y + 42, 0xFFBDB8C8);
    }

    private static void drawHurtDirection(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h, net.minecraft.entity.Entity attacker) {
        double dx = attacker.getX() - mc.player.getX();
        double dz = attacker.getZ() - mc.player.getZ();
        double angle = Math.atan2(dz, dx) - Math.toRadians(mc.player.yaw) + Math.PI / 2.0;
        int cx = w / 2, cy = h / 2, r = fx.getHurtSize();
        int px = cx + (int)(Math.cos(angle) * r);
        int py = cy + (int)(Math.sin(angle) * r);
        int alpha = Math.min(220, 70 + mc.player.hurtTime * 18);
        int color = (alpha << 24) | (fx.getHurtColor() & 0x00FFFFFF);
        drawLine(m, px, py, px - (int)(Math.cos(angle - 2.45) * 11), py - (int)(Math.sin(angle - 2.45) * 11), color);
        drawLine(m, px, py, px - (int)(Math.cos(angle + 2.45) * 11), py - (int)(Math.sin(angle + 2.45) * 11), color);
        drawLine(m, px, py, px + (int)(Math.cos(angle) * 8), py + (int)(Math.sin(angle) * 8), color);
    }

    private static void drawCooldown(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        float progress = mc.player.getAttackCooldownProgress(0.0f);
        int width = 52, x = w / 2 - width / 2, y = h / 2 + 24;
        DrawableHelper.fill(m, x, y, x + width, y + 4, 0xAA101018);
        DrawableHelper.fill(m, x, y, x + (int)(width * progress), y + 4, fx.getCooldownColor());
    }

    private static void drawSprint(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        String s = mc.player.isSprinting() ? "SPRINT" : "WALK";
        int color = mc.player.isSprinting() ? fx.getAccentColor() : 0xFF77727F;
        int width = mc.textRenderer.getWidth(s) + 14;
        int x = w - width - 10, y = h - 30;
        DrawableHelper.fill(m, x, y, x + width, y + 18, 0xB0090A10);
        mc.textRenderer.drawWithShadow(m, s, x + 7, y + 5, color);
    }

    private static void drawPosition(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        String s = String.format("X %.0f  Y %.0f  Z %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        int width = mc.textRenderer.getWidth(s) + 16, x = 10, y = h - 30;
        DrawableHelper.fill(m, x, y, x + width, y + 18, 0xB0090A10);
        mc.textRenderer.drawWithShadow(m, s, x + 8, y + 5, fx.getAccentColor());
    }

    private static void drawCompass(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        float yaw = MathHelper.wrapDegrees(mc.player.yaw);
        int idx = Math.round(yaw / 45.0f);
        idx = ((idx % 8) + 8) % 8;
        String center = dirs[idx];
        int width = 58, x = w / 2 - width / 2, y = 8;
        DrawableHelper.fill(m, x, y, x + width, y + 20, 0xB0090A10);
        DrawableHelper.fill(m, x, y, x + width, y + 2, fx.getAccentColor());
        mc.textRenderer.drawWithShadow(m, center, x + width / 2 - 3, y + 6, 0xFFF5F1FF);
    }

    private static void drawWatermark(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        String s = "ExternalVisuals 1.7.4";
        int width = mc.textRenderer.getWidth(s) + 18;
        DrawableHelper.fill(m, 10, 8, 10 + width, 29, 0xB0090A10);
        DrawableHelper.fill(m, 10, 8, 13, 29, fx.getAccentColor());
        mc.textRenderer.drawWithShadow(m, s, 20, 14, 0xFFF5F1FF);
    }

    private static void sampleFps(MinecraftClient mc, int max) {
        long now = System.currentTimeMillis();
        if (now - lastSample < 120) return;
        lastSample = now;
        int fps = 0;
        try {
            String debug = mc.fpsDebugString;
            int space = debug.indexOf(' ');
            String first = space > 0 ? debug.substring(0, space) : debug;
            fps = Integer.parseInt(first.replaceAll("[^0-9]", ""));
        } catch (Throwable ignored) {}
        FPS.addLast(fps);
        while (FPS.size() > max) FPS.removeFirst();
    }

    private static void drawFpsGraph(MatrixStack m, MinecraftClient mc, CombatVisuals fx, int w, int h) {
        int width = Math.min(180, Math.max(90, FPS.size() * 3));
        int x = w - width - 10, y = 34, height = 42;
        DrawableHelper.fill(m, x, y, x + width, y + height, 0xA0090A10);
        int i = 0;
        for (Integer fps : FPS) {
            int bar = Math.min(height - 4, Math.max(1, fps / 6));
            DrawableHelper.fill(m, x + 4 + i * 3, y + height - 3 - bar, x + 6 + i * 3, y + height - 3, fx.getAccentColor());
            i++;
            if (x + 6 + i * 3 > x + width - 3) break;
        }
        mc.textRenderer.drawWithShadow(m, "FPS", x + 6, y + 5, 0xFFBDB8C8);
    }

    private static String armorPercent(LivingEntity entity) {
        int count = 0, total = 0;
        for (net.minecraft.item.ItemStack stack : entity.getArmorItems()) {
            if (stack.isEmpty()) continue;
            int max = stack.isDamageable() ? Math.max(1, stack.getMaxDamage()) : 1;
            int left = stack.isDamageable() ? Math.max(0, max - stack.getDamage()) : 1;
            total += Math.round(left * 100.0f / max);
            count++;
        }
        return count == 0 ? "--" : String.valueOf(Math.round(total / (float)count)) + "%";
    }

    private static void append(StringBuilder b, String value) {
        if (b.length() > 0) b.append("  •  ");
        b.append(value);
    }

    private static void drawLine(MatrixStack m, int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1, dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        for (int i = 0; i <= steps; i++) {
            int x = x1 + dx * i / steps;
            int y = y1 + dy * i / steps;
            DrawableHelper.fill(m, x - 1, y - 1, x + 2, y + 2, color);
        }
    }
}
