package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.PulseVisuals;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Deque;

public final class PulseVisualsRenderer {
    private PulseVisualsRenderer() {}

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            PulseVisuals pulse = get();
            if (mc == null || mc.world == null || mc.player == null || pulse == null || !pulse.isEnabled()) return;
            try {
                renderTrails(context.matrixStack(), context.camera(), context.consumers(), pulse);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] Pulse world renderer recovered: " + throwable.getClass().getSimpleName());
            }
        });

        HudRenderCallback.EVENT.register(PulseVisualsRenderer::renderHud);
    }

    private static PulseVisuals get() {
        return ExternalVisuals.MODULE_MANAGER == null
                ? null : ExternalVisuals.MODULE_MANAGER.get(PulseVisuals.class);
    }

    private static void renderTrails(MatrixStack matrices, Camera camera,
                                      VertexConsumerProvider consumers, PulseVisuals pulse) {
        if (consumers == null) return;
        VertexConsumer consumer = consumers.getBuffer(ESPNoDepthLayer.LINES);
        Vec3d cam = camera.getPos();

        if (pulse.isWeaponTrailEnabled()) {
            drawTrail(matrices, consumer, cam, pulse.getWeaponTrail(),
                    pulse.getWeaponTrailColor(), pulse.getWeaponTrailLength());
        }
        if (pulse.isSprintTrailEnabled()) {
            drawTrail(matrices, consumer, cam, pulse.getSprintTrail(),
                    pulse.getSprintTrailColor(), pulse.getSprintTrailLength());
        }
    }

    private static void drawTrail(MatrixStack matrices, VertexConsumer consumer,
                                  Vec3d cam, Deque<Vec3d> points, int color, int maxPoints) {
        if (points.size() < 2) return;
        Vec3d previous = null;
        int index = 0;
        for (Vec3d point : points) {
            if (index++ >= maxPoints) break;
            if (previous != null) {
                float alpha = Math.max(0.08f, 1.0f - (index - 1) / (float) Math.max(1, maxPoints));
                line(matrices, consumer,
                        previous.x - cam.x, previous.y - cam.y, previous.z - cam.z,
                        point.x - cam.x, point.y - cam.y, point.z - cam.z,
                        color, alpha);
            }
            previous = point;
        }
    }

    private static void line(MatrixStack matrices, VertexConsumer consumer,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             int color, float alpha) {
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = alpha * (((color >>> 24) & 255) / 255.0f);
        consumer.vertex(matrices.peek().getModel(), (float)x1, (float)y1, (float)z1)
                .color(r, g, b, a).next();
        consumer.vertex(matrices.peek().getModel(), (float)x2, (float)y2, (float)z2)
                .color(r, g, b, a).next();
    }

    private static void renderHud(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PulseVisuals pulse = get();
        if (mc == null || mc.player == null || mc.world == null || pulse == null || !pulse.isEnabled()) return;

        try {
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();

            float pulseAlpha = pulse.getPulseAlpha();
            if (pulse.shouldShowLowHpPulse()) {
                float hp = mc.player.getHealth() / Math.max(1.0f, mc.player.getMaxHealth());
                float low = 1.0f - Math.max(0.0f, Math.min(1.0f, hp));
                pulseAlpha = Math.max(pulseAlpha, low * (0.18f + 0.12f *
                        (float)Math.sin(System.currentTimeMillis() / 120.0)));
            }
            if (pulseAlpha > 0.005f) {
                drawScreenPulse(matrices, w, h, pulse.getPulseColor(), pulseAlpha);
            }

            if (pulse.getDirectionAlpha() > 0.005f) {
                drawDirection(matrices, w / 2, h / 2, pulse.getDirectionAngle(),
                        pulse.getDirectionSize(), pulse.getDirectionColor(), pulse.getDirectionAlpha());
            }

            if (pulse.isDynamicCrosshairEnabled()) {
                drawDynamicCrosshair(matrices, mc, w / 2, h / 2, pulse);
            }

            if (pulse.isComboEnabled() && pulse.getCombo() > 0) {
                drawCombo(matrices, mc, w / 2, h / 2 + 28, pulse);
            }

            if (pulse.isDynamicIslandEnabled()) {
                drawDynamicIsland(matrices, mc, pulse);
            }

            if (pulse.isKillFeedEnabled()) {
                drawKillFeed(matrices, mc, pulse);
            }
        } catch (Throwable throwable) {
            System.err.println("[ExternalVisuals] Pulse HUD renderer recovered: " + throwable.getClass().getSimpleName());
        }
    }

    private static void drawDynamicIsland(MatrixStack matrices, MinecraftClient mc, PulseVisuals pulse) {
        int w = mc.getWindow().getScaledWidth();
        int enabled = 0;
        if (ExternalVisuals.MODULE_MANAGER != null) {
            for (com.externalvisuals.module.Module module : ExternalVisuals.MODULE_MANAGER.getModules()) {
                if (module.isEnabled()) enabled++;
            }
        }
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (pulse.showIslandHealth()) parts.add("HP " + Math.round(mc.player.getHealth() * 10.0f) / 10.0f);
        if (pulse.showIslandFps()) {
            int fps = 0;
            if (ExternalVisuals.MODULE_MANAGER != null) {
                com.externalvisuals.modules.hud.FPSHud fpsHud = ExternalVisuals.MODULE_MANAGER.get(com.externalvisuals.modules.hud.FPSHud.class);
                if (fpsHud != null) fps = fpsHud.getFPS();
            }
            parts.add("FPS " + fps);
        }
        if (pulse.showIslandCombo() && pulse.getCombo() > 0) parts.add("COMBO " + pulse.getCombo());
        if (pulse.showIslandModules()) parts.add("MOD " + enabled);
        if (pulse.showIslandTarget() && mc.targetedEntity instanceof net.minecraft.entity.LivingEntity) {
            String name = mc.targetedEntity.getDisplayName().getString();
            if (name.length() > 18) name = name.substring(0, 18);
            parts.add(name);
        }
        if (parts.isEmpty()) return;

        String text = String.join("  •  ", parts);
        int baseWidth = Math.min(320, Math.max(150, mc.textRenderer.getWidth(text) + 34));
        int baseHeight = 28;
        float scale = pulse.getIslandScale();
        int x = (w - (int)(baseWidth * scale)) / 2;
        int y = 8;
        int bg = (((int)(pulse.getIslandAlpha() * 220.0f) & 255) << 24) | 0x00090A10;
        int accent = pulse.getIslandColor();

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(scale, scale, 1.0f);
        DrawableHelper.fill(matrices, 0, 0, baseWidth, baseHeight, bg);
        DrawableHelper.fill(matrices, 0, 0, baseWidth, 2, accent);
        DrawableHelper.fill(matrices, 0, baseHeight - 1, baseWidth, baseHeight, 0x553A3A48);
        int dot = 0xFF42D9FF;
        DrawableHelper.fill(matrices, 9, 10, 13, 14, dot);
        mc.textRenderer.drawWithShadow(matrices, text, 19, 9, 0xFFF5F3FA);
        matrices.pop();
    }

    private static void drawScreenPulse(MatrixStack matrices, int w, int h, int color, float alpha) {
        int a = Math.max(0, Math.min(170, (int)(alpha * 150.0f)));
        int c = (a << 24) | (color & 0x00FFFFFF);
        int border = Math.max(10, Math.min(38, (int)(14 + alpha * 24)));
        DrawableHelper.fill(matrices, 0, 0, w, border, c);
        DrawableHelper.fill(matrices, 0, h - border, w, h, c);
        DrawableHelper.fill(matrices, 0, 0, border, h, c);
        DrawableHelper.fill(matrices, w - border, 0, w, h, c);
    }

    private static void drawDirection(MatrixStack matrices, int cx, int cy, double angle,
                                      int size, int color, float alpha) {
        double x = Math.cos(angle);
        double y = Math.sin(angle);
        int px = cx + (int)(x * size);
        int py = cy + (int)(y * size);
        int c = (((int)(alpha * 255.0f) & 255) << 24) | (color & 0x00FFFFFF);
        int leftX = px - (int)(y * 6.0);
        int leftY = py + (int)(x * 6.0);
        int rightX = px + (int)(y * 6.0);
        int rightY = py - (int)(x * 6.0);
        DrawableHelper.fill(matrices, px - 2, py - 2, px + 3, py + 3, c);
        drawThickLine(matrices, cx, cy, px, py, c, 2);
        drawThickLine(matrices, px, py, leftX, leftY, c, 2);
        drawThickLine(matrices, px, py, rightX, rightY, c, 2);
    }

    private static void drawDynamicCrosshair(MatrixStack matrices, MinecraftClient mc,
                                             int cx, int cy, PulseVisuals pulse) {
        boolean target = mc.targetedEntity instanceof net.minecraft.entity.LivingEntity
                && ((net.minecraft.entity.LivingEntity) mc.targetedEntity).isAlive();
        int color = target ? pulse.getCrosshairTargetColor() : pulse.getCrosshairColor();
        double phase = System.currentTimeMillis() / 220.0;
        int radius = pulse.getCrosshairTargetRadius() + (int)(Math.sin(phase) * 1.5);
        int c = 0xAA000000 | (color & 0x00FFFFFF);
        for (int i = 0; i < 12; i++) {
            double a = i * Math.PI * 2.0 / 12.0;
            int x = cx + (int)(Math.cos(a) * radius);
            int y = cy + (int)(Math.sin(a) * radius);
            DrawableHelper.fill(matrices, x - 1, y - 1, x + 2, y + 2, c);
        }
    }

    private static void drawCombo(MatrixStack matrices, MinecraftClient mc,
                                  int cx, int y, PulseVisuals pulse) {
        String text = "COMBO " + pulse.getCombo();
        int width = mc.textRenderer.getWidth(text);
        int x = cx - width / 2;
        float scale = pulse.isComboPulse()
                ? 1.0f + 0.06f * (float)Math.sin(System.currentTimeMillis() / 100.0)
                : 1.0f;
        matrices.push();
        matrices.translate(cx, y, 0);
        matrices.scale(scale, scale, 1.0f);
        mc.textRenderer.drawWithShadow(matrices, text, -width / 2.0f, 0, pulse.getComboColor());
        matrices.pop();
    }

    private static void drawKillFeed(MatrixStack matrices, MinecraftClient mc, PulseVisuals pulse) {
        int x = mc.getWindow().getScaledWidth() - 8;
        int y = 28;
        for (PulseVisuals.KillEntry entry : pulse.getKills()) {
            String text = "✦ " + entry.name + " eliminated";
            int width = mc.textRenderer.getWidth(text);
            int left = x - width - 8;
            DrawableHelper.fill(matrices, left, y - 3, x, y + 12, 0xB00A0A12);
            mc.textRenderer.drawWithShadow(matrices, text, left + 4, y, entry.critical ? 0xFFFF3B57 : 0xFFFFFFFF);
            y += 18;
        }
    }

    private static void drawThickLine(MatrixStack matrices, int x1, int y1, int x2, int y2, int color, int thickness) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        for (int i = 0; i <= steps; i++) {
            int x = x1 + dx * i / steps;
            int y = y1 + dy * i / steps;
            DrawableHelper.fill(matrices, x - thickness / 2, y - thickness / 2,
                    x + thickness / 2 + 1, y + thickness / 2 + 1, color);
        }
    }
}
