package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.visual.CustomCrosshair;
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

        renderCrosshair(matrices, mc);
        renderHitmarker(matrices, mc);
    }

    private static void renderCrosshair(
            MatrixStack matrices,
            MinecraftClient mc
    ) {

        CustomCrosshair crosshair =
                ExternalVisuals.MODULE_MANAGER
                        .get(CustomCrosshair.class);

        if (crosshair == null
                || !crosshair.isEnabled()) {
            return;
        }

        int centerX =
                mc.getWindow().getScaledWidth() / 2;

        int centerY =
                mc.getWindow().getScaledHeight() / 2;

        int size = crosshair.getSize();
        int gap = crosshair.getGap();
        int thickness = crosshair.getThickness();
        int color = crosshair.getColor();

        switch (crosshair.getStyle()) {

            case PLUS:
                drawPlus(
                        matrices,
                        centerX,
                        centerY,
                        size,
                        thickness,
                        color
                );
                break;

            case DOT:
                drawDot(
                        matrices,
                        centerX,
                        centerY,
                        thickness,
                        color
                );
                break;

            case CIRCLE:
                drawCircle(
                        matrices,
                        centerX,
                        centerY,
                        size,
                        thickness,
                        color
                );
                break;

            case T_SHAPE:
                drawTShape(
                        matrices,
                        centerX,
                        centerY,
                        size,
                        gap,
                        thickness,
                        color
                );
                break;

            case CROSS:
            default:
                drawCross(
                        matrices,
                        centerX,
                        centerY,
                        size,
                        gap,
                        thickness,
                        color
                );
                break;
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