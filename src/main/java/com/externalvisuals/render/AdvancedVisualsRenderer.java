package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.AdvancedVisuals;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public final class AdvancedVisualsRenderer {
    private AdvancedVisualsRenderer() {}

    public static void init() {
        HudRenderCallback.EVENT.register(AdvancedVisualsRenderer::render);
    }

    private static AdvancedVisuals get() {
        return ExternalVisuals.MODULE_MANAGER == null ? null :
                ExternalVisuals.MODULE_MANAGER.get(AdvancedVisuals.class);
    }

    private static void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        AdvancedVisuals fx = get();
        if (mc == null || mc.player == null || mc.world == null || fx == null || !fx.isEnabled()) return;

        try {
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();

            float lowHp = 1.0f - MathHelper.clamp(
                    mc.player.getHealth() / Math.max(1.0f, mc.player.getMaxHealth()), 0.0f, 1.0f);

            float vignetteAlpha = fx.isVignetteEnabled() ? fx.getVignetteStrength() : 0.0f;
            if (fx.isLowHpVignetteEnabled() &&
                    mc.player.getHealth() <= mc.player.getMaxHealth() * fx.getLowHpThreshold() / 100.0f) {
                vignetteAlpha = Math.max(vignetteAlpha, 0.10f + lowHp * 0.28f);
            }
            if (vignetteAlpha > 0.005f) {
                drawVignette(matrices, w, h, fx.getVignetteColor(), vignetteAlpha);
            }

            float flash = fx.getHitFlashAlpha();
            if (flash > 0.005f) {
                drawBorder(matrices, w, h, fx.getHitFlashColor(), flash * 0.55f);
            }

            int cx = w / 2;
            int cy = h / 2;
            if (fx.isCrosshairDotEnabled()) {
                int s = fx.getCrosshairDotSize();
                drawCenterDot(matrices, cx, cy, s, fx.getCrosshairDotColor());
            }
            if (fx.isCrosshairGapEnabled()) {
                drawCrosshairGap(matrices, cx, cy, fx.getCrosshairGapSize());
            }

            Entity target = mc.targetedEntity;
            if (fx.isTargetArrowEnabled() && target instanceof LivingEntity &&
                    ((LivingEntity) target).isAlive()) {
                drawTargetArrow(matrices, cx, cy, (LivingEntity) target,
                        fx.getTargetArrowRadius(), fx.getTargetArrowColor());
            }

            if (fx.isInfoStripEnabled()) {
                drawInfoStrip(matrices, mc, fx, w, h);
            }
        } catch (Throwable throwable) {
            System.err.println("[ExternalVisuals] Advanced visual renderer recovered: " +
                    throwable.getClass().getSimpleName());
        }
    }

    private static void drawVignette(MatrixStack m, int w, int h, int color, float strength) {
        strength = Math.max(0.0f, Math.min(1.0f, strength));
        int a = Math.max(0, Math.min(180, (int)(strength * 150.0f)));
        int c = (a << 24) | (color & 0x00FFFFFF);
        int b = Math.max(10, (int)(18 + strength * 35));
        DrawableHelper.fill(m, 0, 0, w, b, c);
        DrawableHelper.fill(m, 0, h - b, w, h, c);
        DrawableHelper.fill(m, 0, 0, b, h, c);
        DrawableHelper.fill(m, w - b, 0, w, h, c);
    }

    private static void drawBorder(MatrixStack m, int w, int h, int color, float alpha) {
        int a = Math.max(0, Math.min(150, (int)(alpha * 255.0f)));
        int c = (a << 24) | (color & 0x00FFFFFF);
        DrawableHelper.fill(m, 0, 0, w, 3, c);
        DrawableHelper.fill(m, 0, h - 3, w, h, c);
        DrawableHelper.fill(m, 0, 0, 3, h, c);
        DrawableHelper.fill(m, w - 3, 0, w, h, c);
    }

    private static void drawCenterDot(MatrixStack m, int cx, int cy, int size, int color) {
        int s = Math.max(1, size);
        DrawableHelper.fill(m, cx - s, cy - s, cx + s + 1, cy + s + 1, color);
    }

    private static void drawCrosshairGap(MatrixStack m, int cx, int cy, int gap) {
        int c = 0xAAFFFFFF;
        DrawableHelper.fill(m, cx - 1, cy - gap - 5, cx + 2, cy - gap, c);
        DrawableHelper.fill(m, cx - 1, cy + gap, cx + 2, cy + gap + 5, c);
        DrawableHelper.fill(m, cx - gap - 5, cy - 1, cx - gap, cy + 2, c);
        DrawableHelper.fill(m, cx + gap, cy - 1, cx + gap + 5, cy + 2, c);
    }

    private static void drawTargetArrow(MatrixStack m, int cx, int cy, LivingEntity target,
                                        int radius, int color) {
        double dx = target.getX() - MinecraftClient.getInstance().player.getX();
        double dz = target.getZ() - MinecraftClient.getInstance().player.getZ();
        double yaw = Math.toRadians(MinecraftClient.getInstance().player.yaw);
        double angle = Math.atan2(dz, dx) - yaw + Math.PI / 2.0;
        int px = cx + (int)(Math.cos(angle) * radius);
        int py = cy + (int)(Math.sin(angle) * radius);
        int a = 0xDD000000 | (color & 0x00FFFFFF);
        int leftX = px - (int)(Math.cos(angle - 2.5) * 9);
        int leftY = py - (int)(Math.sin(angle - 2.5) * 9);
        int rightX = px - (int)(Math.cos(angle + 2.5) * 9);
        int rightY = py - (int)(Math.sin(angle + 2.5) * 9);
        drawLine(m, px, py, leftX, leftY, a);
        drawLine(m, px, py, rightX, rightY, a);
        drawLine(m, leftX, leftY, rightX, rightY, a);
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

    private static void drawInfoStrip(MatrixStack m, MinecraftClient mc, AdvancedVisuals fx, int w, int h) {
        StringBuilder s = new StringBuilder();

        if (fx.showFps()) s.append("FPS ").append(getFps(mc));
        if (fx.showCoordinates()) {
            appendSep(s);
            s.append("XYZ ").append(Math.round(mc.player.getX())).append(" ")
                    .append(Math.round(mc.player.getY())).append(" ")
                    .append(Math.round(mc.player.getZ()));
        }
        if (fx.showDirection()) {
            appendSep(s);
            s.append(direction(mc.player.yaw));
        }
        if (fx.showTarget() && mc.targetedEntity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) mc.targetedEntity;
            appendSep(s);
            s.append("TARGET ").append(target.getDisplayName().getString());
            if (fx.showArmor()) s.append(" [").append(armor(target)).append("]");
        }

        if (s.length() == 0) return;

        int width = mc.textRenderer.getWidth(s.toString()) + 20;
        int x = Math.max(6, (w - Math.min(width, w - 12)) / 2);
        int y = h - 30;
        int bg = 0xB0090A10;
        DrawableHelper.fill(m, x, y, x + Math.min(width, w - 12), y + 20, bg);
        DrawableHelper.fill(m, x, y, x + Math.min(width, w - 12), y + 2, 0xFF9B5CFF);
        mc.textRenderer.drawWithShadow(m, s.toString(), x + 10, y + 6, 0xFFF3F0FA);
    }

    private static void appendSep(StringBuilder s) {
        if (s.length() > 0) s.append("  •  ");
    }

    private static int getFps(MinecraftClient mc) {
        try {
            String debug = mc.fpsDebugString;
            int slash = debug.indexOf(' ');
            String first = slash > 0 ? debug.substring(0, slash) : debug;
            return Integer.parseInt(first.replaceAll("[^0-9]", ""));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private static String direction(float yaw) {
        float normalized = MathHelper.wrapDegrees(yaw);
        if (normalized >= -22.5f && normalized < 22.5f) return "S";
        if (normalized >= 22.5f && normalized < 67.5f) return "SW";
        if (normalized >= 67.5f && normalized < 112.5f) return "W";
        if (normalized >= 112.5f && normalized < 157.5f) return "NW";
        if (normalized >= 157.5f || normalized < -157.5f) return "N";
        if (normalized >= -157.5f && normalized < -112.5f) return "NE";
        if (normalized >= -112.5f && normalized < -67.5f) return "E";
        return "SE";
    }

    private static String armor(LivingEntity entity) {
        StringBuilder result = new StringBuilder();
        for (ItemStack stack : entity.getArmorItems()) {
            if (result.length() > 0) result.append("/");
            if (stack.isEmpty()) result.append("--");
            else if (stack.isDamageable()) {
                int max = Math.max(1, stack.getMaxDamage());
                int left = Math.max(0, max - stack.getDamage());
                result.append(Math.round(left * 100.0f / max));
            } else result.append("100");
        }
        return result.toString();
    }
}
