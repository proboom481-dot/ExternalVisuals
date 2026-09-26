package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.cosmetics.Cosmetics;
import com.externalvisuals.modules.optimization.Optimization;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;

/** Lightweight procedural cosmetics; no texture uploads and no per-frame allocations for large buffers. */
public final class CosmeticsRenderer {
    private CosmeticsRenderer() {}

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.world == null || mc.player == null || ExternalVisuals.MODULE_MANAGER == null) return;
            Cosmetics cosmetics = ExternalVisuals.MODULE_MANAGER.get(Cosmetics.class);
            if (cosmetics == null || !cosmetics.isEnabled()) return;
            try {
                render(context.matrixStack(), context.camera(), context.consumers(), cosmetics);
            } catch (Throwable ignored) {
                // Cosmetic effects are strictly optional and never allowed to break rendering.
            }
        });
    }

    private static void render(MatrixStack matrices, Camera camera, VertexConsumerProvider consumers, Cosmetics c) {
        if (consumers == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        Optimization opt = ExternalVisuals.MODULE_MANAGER.get(Optimization.class);
        int budget = Math.min(c.getMaxEntities(), opt == null ? c.getMaxEntities() : opt.getArtificialBudget());
        int rendered = 0;
        boolean lowFps = false;
        if (c.isLowFpsSafe() && mc.fpsDebugString != null) {
            int fps = 0;
            boolean started = false;
            for (int i = 0; i < mc.fpsDebugString.length(); i++) {
                char ch = mc.fpsDebugString.charAt(i);
                if (ch >= '0' && ch <= '9') { started = true; fps = Math.min(1000, fps * 10 + ch - '0'); }
                else if (started) break;
            }
            lowFps = fps > 0 && fps < 45;
        }
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (rendered >= budget) break;
            if (player == null || !player.isAlive()) continue;
            if (c.selfOnly() && player != mc.player) continue;
            if (mc.player.squaredDistanceTo(player) > c.getParticleRange() * c.getParticleRange()) continue;
            rendered++;
            renderPlayer(matrices, camera, consumers, player, c, lowFps);
        }
    }

    private static void renderPlayer(MatrixStack matrices, Camera camera, VertexConsumerProvider consumers, PlayerEntity player, Cosmetics c, boolean lowFps) {
        double cx = camera.getPos().x;
        double cy = camera.getPos().y;
        double cz = camera.getPos().z;
        float yaw = player.getYaw(1.0f) * 0.017453292f;
        double behindX = Math.sin(yaw) * c.getCapeOffset();
        double behindZ = -Math.cos(yaw) * c.getCapeOffset();
        double x = player.getX() + behindX - cx;
        double y = player.getY() + 0.35 - cy;
        double z = player.getZ() + behindZ - cz;

        VertexConsumer lines = consumers.getBuffer(ESPNoDepthLayer.LINES);
        int color = c.animatedColor(c.getCapeColor(), player.getEntityId() * 0.071);
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = ((color >>> 24) & 255) / 255f;
        if (c.isPulseEnabled()) {
            float pulse = (float) (0.72 + 0.28 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() * 0.001 * c.getPulseSpeed())));
            a = Math.min(1.0f, a * pulse);
        }
        Box cape = new Box(
                x - c.getCapeWidth() * 0.5, y, z - 0.035,
                x + c.getCapeWidth() * 0.5, y + c.getCapeHeight(), z + 0.035
        );
        if (c.showCape()) {
            WorldRenderer.drawBox(matrices, lines, cape, r, g, b, a);
            if ("DOUBLE".equalsIgnoreCase(c.getCapeStyle())) {
                int second = c.getCapeSecondColor();
                WorldRenderer.drawBox(matrices, lines, cape.expand(0.018),
                        ((second >> 16) & 255) / 255f, ((second >> 8) & 255) / 255f,
                        (second & 255) / 255f, a * 0.75f);
            }
            if ("WAVE".equalsIgnoreCase(c.getCapeStyle())) {
                float wave = (float) Math.sin(System.currentTimeMillis() * 0.004) * c.getCapeWave();
                Box waveBox = cape.offset(0, 0, wave);
                WorldRenderer.drawBox(matrices, lines, waveBox, r, g, b, a * 0.65f);
            }
            if (c.isCapeGlowEnabled()) {
                WorldRenderer.drawBox(matrices, lines, cape.expand(0.025), r, g, b, a * c.getGlowAlpha());
            }
        }
        if (c.showHalo()) {
            float t = (float) (System.currentTimeMillis() * 0.001 * c.getHaloSpeed());
            double rr = c.getHaloRadius();
            double hx = player.getX() - cx;
            double hz = player.getZ() - cz;
            double hy = player.getY() + c.getHaloHeight() - cy;
            for (int i = 0; i < 24; i++) {
                double a0 = (i / 24.0) * Math.PI * 2.0 + t;
                double a1 = ((i + 1) / 24.0) * Math.PI * 2.0 + t;
                drawLine(matrices, lines,
                        hx + Math.cos(a0) * rr, hy, hz + Math.sin(a0) * rr,
                        hx + Math.cos(a1) * rr, hy, hz + Math.sin(a1) * rr,
                        ((c.getHaloColor() >> 16) & 255) / 255f,
                        ((c.getHaloColor() >> 8) & 255) / 255f,
                        (c.getHaloColor() & 255) / 255f, 0.85f);
            }
        }
        if (c.showWings()) {
            double wing = c.getWingScale();
            double wx = Math.cos(yaw) * wing;
            double wz = Math.sin(yaw) * wing;
            int wc = c.getWingColor();
            drawLine(matrices, lines, x, y + 0.75, z, x + wx, y + 1.05, z + wz,
                    ((wc >> 16) & 255) / 255f, ((wc >> 8) & 255) / 255f, (wc & 255) / 255f, 0.9f);
            drawLine(matrices, lines, x, y + 0.75, z, x - wx, y + 1.05, z - wz,
                    ((wc >> 16) & 255) / 255f, ((wc >> 8) & 255) / 255f, (wc & 255) / 255f, 0.9f);
        }
        if (c.isNameplateEnabled() && !lowFps) {
            net.minecraft.client.font.TextRenderer text = mc.textRenderer;
            if (text != null) {
                String label = player.getDisplayName().getString();
                int width = text.getWidth(label);
                matrices.push();
                try {
                    matrices.translate(x, player.getY() + 2.55 - cy, z);
                    matrices.multiply(camera.getRotation());
                    matrices.scale(-0.018f, -0.018f, 0.018f);
                    text.draw(label, -width / 2.0f, 0.0f, c.animatedColor(c.getCapeColor(), 0.0), true, matrices.peek().getModel(), consumers, true, 0, 15728880);
                } finally {
                    matrices.pop();
                }
            }
        }
        if (c.isParticleEnabled() && !lowFps && (System.currentTimeMillis() / 50L) % c.getParticleRate() == 0) {
            mc.world.addParticle(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 0.0, 0.015, 0.0);
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 double x1, double y1, double z1, double x2, double y2, double z2,
                                 float r, float g, float b, float a) {
        MatrixStack.Entry entry = matrices.peek();
        consumer.vertex(entry.getModel(), (float) x1, (float) y1, (float) z1).color(r, g, b, a).next();
        consumer.vertex(entry.getModel(), (float) x2, (float) y2, (float) z2).color(r, g, b, a).next();
    }
}
