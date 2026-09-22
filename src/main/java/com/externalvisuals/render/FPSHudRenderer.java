package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.hud.FPSHud;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public final class FPSHudRenderer {

    private FPSHudRenderer() {
    }

    public static void init() {
        HudRenderCallback.EVENT.register(
                FPSHudRenderer::render
        );
    }

    private static void render(
            MatrixStack matrices,
            float tickDelta
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.player == null
                || mc.world == null) {
            return;
        }

        if (mc.currentScreen != null) {
            return;
        }

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        FPSHud fpsHud =
                ExternalVisuals.MODULE_MANAGER.get(
                        FPSHud.class
                );

        if (fpsHud == null
                || !fpsHud.isEnabled()) {
            return;
        }

        String text =
                fpsHud.getText();

        if (text == null) {
            return;
        }

        float scale =
                fpsHud.getScale();

        if (scale <= 0.0f) {
            scale = 1.0f;
        }

        matrices.push();

        matrices.translate(
                fpsHud.getX(),
                fpsHud.getY(),
                0.0f
        );

        matrices.scale(
                scale,
                scale,
                1.0f
        );

        mc.textRenderer.drawWithShadow(
                matrices,
                text,
                0.0f,
                0.0f,
                fpsHud.getColor()
        );

        matrices.pop();
    }
}