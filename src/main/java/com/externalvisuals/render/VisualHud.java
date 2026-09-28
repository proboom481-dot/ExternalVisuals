package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.visual.CustomCrosshair;
import com.externalvisuals.modules.visual.CinematicOverlay;
import com.externalvisuals.modules.visual.LowHpWarning;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public final class VisualHud {

    private VisualHud() {
    }

    public static void init() {
        HudRenderCallback.EVENT.register(
                VisualHud::render
        );
    }

    private static void render(
            MatrixStack matrices,
            float tickDelta
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.player == null
                || mc.world == null
                || mc.currentScreen != null) {
            return;
        }

        renderCinematic(matrices, mc);
        renderLowHpWarning(matrices, mc);
        renderCrosshair(matrices, mc);
        renderHitmarker(matrices, mc);
    }


    private static void renderCinematic(MatrixStack matrices, MinecraftClient mc) {
        CinematicOverlay overlay = ExternalVisuals.MODULE_MANAGER.get(CinematicOverlay.class);
        if (overlay == null || !overlay.isEnabled()) return;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        int rgb = overlay.getColor() & 0x00FFFFFF;
        if (overlay.isLetterbox()) {
            int bars = Math.max(2, (int) (h * overlay.getLetterboxSize()));
            DrawableHelper.fill(matrices, 0, 0, w, bars, 0xCC000000);
            DrawableHelper.fill(matrices, 0, h - bars, w, h, 0xCC000000);
        }
        if (overlay.isVignette()) {
            int a = Math.max(0, Math.min(180, (int) (overlay.getVignetteStrength() * 255f)));
            int edge = Math.max(8, Math.min(64, Math.round(Math.min(w, h) * 0.06f)));
            int c = (a << 24);
            DrawableHelper.fill(matrices, 0, 0, w, edge, c);
            DrawableHelper.fill(matrices, 0, h-edge, w, h, c);
            DrawableHelper.fill(matrices, 0, 0, edge, h, c);
            DrawableHelper.fill(matrices, w-edge, 0, w, h, c);
        }
        if (overlay.isFocus()) {
            boolean target = mc.targetedEntity != null && overlay.isTargetPulse();
            double pulse = target ? 0.75 + 0.25 * Math.sin(System.currentTimeMillis() / 1000.0 * overlay.getTargetPulseSpeed()) : 1.0;
            int a = Math.max(0, Math.min(100, (int) (overlay.getFocusStrength() * pulse * 255f)));
            int size = Math.max(8, (int) (Math.min(w, h) * overlay.getFocusSize()));
            int cx = w / 2, cy = h / 2;
            int c = (a << 24) | rgb;
            DrawableHelper.fill(matrices, cx-size, cy-1, cx+size, cy+1, c);
            DrawableHelper.fill(matrices, cx-1, cy-size, cx+1, cy+size, c);
        }
    }

    private static void renderLowHpWarning(
            MatrixStack matrices,
            MinecraftClient mc
    ) {
        LowHpWarning warning =
                ExternalVisuals.MODULE_MANAGER.get(LowHpWarning.class);
        if (warning == null || !warning.isEnabled() || mc.player == null) return;

        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (health > warning.getThreshold()) return;

        float ratio = Math.max(0.0f, Math.min(1.0f, health / warning.getThreshold()));
        float danger = 1.0f - ratio;
        float pulse = warning.isHeartbeat()
                ? (float) (0.55 + 0.45 * Math.sin(System.currentTimeMillis() / 1000.0 * warning.getSpeed()))
                : 1.0f;
        int alpha = Math.max(10, Math.min(150, (int) (danger * warning.getIntensity() * pulse * 255.0f)));
        int color = (alpha << 24) | (warning.getColor() & 0x00FFFFFF);
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        int border = Math.max(8, Math.min(32, Math.round(18 + danger * 16)));

        DrawableHelper.fill(matrices, 0, 0, w, border, color);
        DrawableHelper.fill(matrices, 0, h - border, w, h, color);
        DrawableHelper.fill(matrices, 0, 0, border, h, color);
        DrawableHelper.fill(matrices, w - border, 0, w, h, color);
    }

    private static void renderCrosshair(
            MatrixStack matrices,
            MinecraftClient mc
    ) {
        CustomCrosshair crosshair = ExternalVisuals.MODULE_MANAGER.get(CustomCrosshair.class);
        if (crosshair == null || !crosshair.isEnabled()) return;

        int centerX = mc.getWindow().getScaledWidth() / 2;
        int centerY = mc.getWindow().getScaledHeight() / 2;
        int size = crosshair.getSize();
        int gap = crosshair.getEffectiveGap();
        int thickness = crosshair.getThickness();

        float pulse = crosshair.isPulseEnabled()
                ? (float)(1.0 + 0.10 * Math.sin(System.currentTimeMillis() / 1000.0 * crosshair.getPulseSpeed()))
                : 1.0f;
        int drawSize = Math.max(1, Math.round(size * pulse));
        int drawThickness = Math.max(1, Math.round(thickness * pulse));
        int color = crosshair.getColor();

        Hitmarker hitmarker = ExternalVisuals.MODULE_MANAGER.get(Hitmarker.class);
        if (crosshair.isHitFlashEnabled() && hitmarker != null && hitmarker.isVisible()) {
            color = crosshair.getHitColor();
        }

        // Draw a slightly larger underlay first, giving every style a clean outline.
        if (crosshair.isOutlineEnabled()) {
            int outlineThickness = drawThickness + 2;
            drawCrosshairShape(matrices, crosshair, centerX, centerY, drawSize + 1, gap, outlineThickness, crosshair.getOutlineColor());
        }
        drawCrosshairShape(matrices, crosshair, centerX, centerY, drawSize, gap, drawThickness, color);
    }

    private static void drawCrosshairShape(MatrixStack matrices, CustomCrosshair crosshair, int x, int y, int size, int gap, int thickness, int color) {
        switch (crosshair.getStyle()) {
            case PLUS:
                drawPlus(matrices, x, y, size, thickness, color); break;
            case DOT:
                drawDot(matrices, x, y, thickness, color); break;
            case CIRCLE:
                drawCircle(matrices, x, y, size, thickness, color); break;
            case T_SHAPE:
                drawTShape(matrices, x, y, size, gap, thickness, color); break;
            case CROSS:
            default:
                drawCross(matrices, x, y, size, gap, thickness, color); break;
        }
        if (crosshair.isCenterDotEnabled()) {
            drawDot(matrices, x, y, Math.max(1, thickness / 2), color);
        }
    }

    private static void drawCross(
            MatrixStack matrices,
            int x,
            int y,
            int size,
            int gap,
            int thickness,
            int color
    ) {

        // Left
        DrawableHelper.fill(
                matrices,
                x - gap - size,
                y - thickness / 2,
                x - gap,
                y + thickness / 2 + 1,
                color
        );

        // Right
        DrawableHelper.fill(
                matrices,
                x + gap,
                y - thickness / 2,
                x + gap + size,
                y + thickness / 2 + 1,
                color
        );

        // Top
        DrawableHelper.fill(
                matrices,
                x - thickness / 2,
                y - gap - size,
                x + thickness / 2 + 1,
                y - gap,
                color
        );

        // Bottom
        DrawableHelper.fill(
                matrices,
                x - thickness / 2,
                y + gap,
                x + thickness / 2 + 1,
                y + gap + size,
                color
        );
    }

    private static void drawPlus(
            MatrixStack matrices,
            int x,
            int y,
            int size,
            int thickness,
            int color
    ) {

        DrawableHelper.fill(
                matrices,
                x - size,
                y - thickness / 2,
                x + size + 1,
                y + thickness / 2 + 1,
                color
        );

        DrawableHelper.fill(
                matrices,
                x - thickness / 2,
                y - size,
                x + thickness / 2 + 1,
                y + size + 1,
                color
        );
    }

    private static void drawDot(
            MatrixStack matrices,
            int x,
            int y,
            int size,
            int color
    ) {

        int radius = Math.max(1, size);

        DrawableHelper.fill(
                matrices,
                x - radius,
                y - radius,
                x + radius + 1,
                y + radius + 1,
                color
        );
    }

    private static void drawCircle(
            MatrixStack matrices,
            int x,
            int y,
            int radius,
            int thickness,
            int color
    ) {

        int outer = Math.max(2, radius);
        int inner = Math.max(1, outer - thickness);

        for (int px = -outer; px <= outer; px++) {

            for (int py = -outer; py <= outer; py++) {

                int distance =
                        px * px + py * py;

                int outerDistance =
                        outer * outer;

                int innerDistance =
                        inner * inner;

                if (distance <= outerDistance
                        && distance >= innerDistance) {

                    DrawableHelper.fill(
                            matrices,
                            x + px,
                            y + py,
                            x + px + 1,
                            y + py + 1,
                            color
                    );
                }
            }
        }
    }

    private static void drawTShape(
            MatrixStack matrices,
            int x,
            int y,
            int size,
            int gap,
            int thickness,
            int color
    ) {

        // Left
        DrawableHelper.fill(
                matrices,
                x - gap - size,
                y - thickness / 2,
                x - gap,
                y + thickness / 2 + 1,
                color
        );

        // Right
        DrawableHelper.fill(
                matrices,
                x + gap,
                y - thickness / 2,
                x + gap + size,
                y + thickness / 2 + 1,
                color
        );

        // Bottom
        DrawableHelper.fill(
                matrices,
                x - thickness / 2,
                y + gap,
                x + thickness / 2 + 1,
                y + gap + size,
                color
        );
    }

    private static void renderHitmarker(
            MatrixStack matrices,
            MinecraftClient mc
    ) {

        Hitmarker hitmarker =
                ExternalVisuals.MODULE_MANAGER
                        .get(Hitmarker.class);

        if (hitmarker == null
                || !hitmarker.isEnabled()
                || !hitmarker.isVisible()) {
            return;
        }

        int centerX =
                mc.getWindow().getScaledWidth() / 2;

        int centerY =
                mc.getWindow().getScaledHeight() / 2;

        int size = 7;
        int thickness = 2;

        int color = 0xFFFFFFFF;

        DrawableHelper.fill(
                matrices,
                centerX - size,
                centerY - size,
                centerX - size + thickness,
                centerY - size + thickness,
                color
        );

        DrawableHelper.fill(
                matrices,
                centerX + size - thickness,
                centerY - size,
                centerX + size,
                centerY - size + thickness,
                color
        );

        DrawableHelper.fill(
                matrices,
                centerX - size,
                centerY + size - thickness,
                centerX - size + thickness,
                centerY + size,
                color
        );

        DrawableHelper.fill(
                matrices,
                centerX + size - thickness,
                centerY + size - thickness,
                centerX + size,
                centerY + size,
                color
        );
    }
}