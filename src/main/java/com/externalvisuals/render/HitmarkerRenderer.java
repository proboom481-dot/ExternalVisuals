package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.Hitmarker;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public final class HitmarkerRenderer {

    private HitmarkerRenderer() {
    }

    public static void init() {

        HudRenderCallback.EVENT.register(
                HitmarkerRenderer::render
        );
    }

    private static void render(
            MatrixStack matrices,
            float tickDelta
    ) {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.player == null) {
            return;
        }

        if (mc.world == null) {
            return;
        }

        Hitmarker hitmarker =
                ExternalVisuals.MODULE_MANAGER.get(
                        Hitmarker.class
                );

        if (hitmarker == null) {
            return;
        }

        if (!hitmarker.isEnabled()) {
            return;
        }

        if (!hitmarker.isVisible()) {
            return;
        }

        if (hitmarker.isOnlyTargeted() && mc.targetedEntity == null) {
            return;
        }

        int screenWidth =
                mc.getWindow().getScaledWidth();

        int screenHeight =
                mc.getWindow().getScaledHeight();

        int centerX =
                screenWidth / 2;

        int centerY =
                screenHeight / 2;

        /*
         * Плавное затухание.
         *
         * timer уменьшается от Duration до 0.
         */
        float progress =
                hitmarker.getProgress();

        progress =
                Math.max(
                        0.0f,
                        Math.min(
                                1.0f,
                                progress
                        )
                );

        /*
         * Небольшое сглаживание между тиками.
         */
        float smoothProgress =
                progress;

        if (hitmarker.getRemainingTicks() > 0) {

            float duration =
                    Math.max(
                            1.0f,
                            hitmarker.getDuration()
                    );

            float interpolated =
                    (
                            hitmarker.getRemainingTicks()
                                    - tickDelta
                    ) / duration;

            smoothProgress =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    interpolated
                            )
                    );
        }

        /*
         * Квадратичное затухание.
         */
        float fade = hitmarker.isFadeEnabled()
                ? smoothProgress * smoothProgress
                : 1.0f;

        int baseColor = hitmarker.getColor();
        if (hitmarker.isDynamicColorEnabled() && mc.targetedEntity != null) {
            baseColor = hitmarker.getCriticalColor();
        }
        if (hitmarker.isCriticalFlashEnabled() && smoothProgress > 0.82f) {
            baseColor = hitmarker.getCriticalColor();
        }

        float pulseBoost = 1.0f + hitmarker.getPulseAmount() * (0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() / 70.0));
        int alpha = Math.round(hitmarker.getAlpha() * fade * pulseBoost);

        alpha =
                Math.max(
                        0,
                        Math.min(
                                255,
                                alpha
                        )
                );

        if (alpha <= 0) {
            return;
        }

        int color =
                (alpha << 24)
                        | (baseColor & 0x00FFFFFF);

        int size = Math.max(2, hitmarker.getSize());
        if (hitmarker.isScaleAnimationEnabled()) {
            size = Math.max(2, Math.round(size * (0.82f + 0.22f * (1.0f - smoothProgress))));
        }

        int thickness =
                Math.max(
                        1,
                        hitmarker.getThickness()
                );

        /*
         * Расстояние от центра.
         */
        int gap =
                Math.max(
                        0,
                        hitmarker.getGap()
                );

        /*
         * Рисуем X.
         *
         * Каждая диагональ состоит из маленьких
         * квадратов. Такой вариант значительно
         * надёжнее для HUD, чем тонкие GL-линии.
         */
        drawDiagonal(
                matrices,
                centerX - gap,
                centerY - gap,
                centerX - size,
                centerY - size,
                thickness,
                color
        );

        drawDiagonal(
                matrices,
                centerX - gap,
                centerY + gap,
                centerX - size,
                centerY + size,
                thickness,
                color
        );

        drawDiagonal(
                matrices,
                centerX + gap,
                centerY - gap,
                centerX + size,
                centerY - size,
                thickness,
                color
        );

        drawDiagonal(
                matrices,
                centerX + gap,
                centerY + gap,
                centerX + size,
                centerY + size,
                thickness,
                color
        );

        if (hitmarker.isCenterDotEnabled()) {
            DrawableHelper.fill(matrices, centerX - thickness / 2, centerY - thickness / 2,
                    centerX + thickness / 2 + 1, centerY + thickness / 2 + 1, color);
        }
    }

    private static void drawDiagonal(
            MatrixStack matrices,
            int startX,
            int startY,
            int endX,
            int endY,
            int thickness,
            int color
    ) {

        int dx =
                endX - startX;

        int dy =
                endY - startY;

        int length =
                Math.max(
                        Math.abs(dx),
                        Math.abs(dy)
                );

        if (length <= 0) {

            drawSquare(
                    matrices,
                    startX,
                    startY,
                    thickness,
                    color
            );

            return;
        }

        for (int i = 0;
             i <= length;
             i++) {

            float progress =
                    i / (float) length;

            int x =
                    Math.round(
                            startX
                                    + dx * progress
                    );

            int y =
                    Math.round(
                            startY
                                    + dy * progress
                    );

            drawSquare(
                    matrices,
                    x,
                    y,
                    thickness,
                    color
            );
        }
    }

    private static void drawSquare(
            MatrixStack matrices,
            int x,
            int y,
            int thickness,
            int color
    ) {

        int half =
                thickness / 2;

        DrawableHelper.fill(
                matrices,
                x - half,
                y - half,
                x + half + 1,
                y + half + 1,
                color
        );
    }
}