package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.world.TntTimer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.client.render.VertexConsumer;
import com.externalvisuals.render.ESPNoDepthLayer;
import net.minecraft.util.math.Vec3d;

public final class TntTimerRenderer {

    private TntTimerRenderer() {
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc == null
                    || mc.player == null
                    || mc.world == null
                    || mc.textRenderer == null
                    || ExternalVisuals.MODULE_MANAGER == null) {
                return;
            }

            TntTimer timer =
                    ExternalVisuals.MODULE_MANAGER.get(TntTimer.class);

            if (timer == null || !timer.isEnabled()) {
                return;
            }

            VertexConsumerProvider consumers = context.consumers();

            if (consumers == null) {
                return;
            }

            try {
                render(
                        context.matrixStack(),
                        context.camera(),
                        consumers,
                        timer
                );
            } catch (Throwable throwable) {
                System.err.println(
                        "[ExternalVisuals] TNT Timer renderer failed; disabling module."
                );
                throwable.printStackTrace();
                timer.setEnabled(false);
            }
        });
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            VertexConsumerProvider consumers,
            TntTimer timer
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d cameraPos = camera.getPos();
        TextRenderer textRenderer = mc.textRenderer;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof TntEntity)) {
                continue;
            }

            TntEntity tnt = (TntEntity) entity;

            if (!timer.shouldRender(tnt)) {
                continue;
            }

            String text = timer.getText(tnt);

            if (text == null || text.isEmpty()) {
                continue;
            }

            double x = tnt.getX() - cameraPos.x;
            double y = tnt.getY()
                    + tnt.getHeight()
                    + 0.55
                    + timer.getOffsetY()
                    - cameraPos.y;
            double z = tnt.getZ() - cameraPos.z;

            int color = timer.getColor(tnt);
            float scale = Math.max(0.0125f, 0.030f * timer.getSize());
            if (timer.isPulseDanger() && timer.getSeconds(tnt) <= 1.0f) {
                scale *= (float)(0.92 + 0.12 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 1000.0 * timer.getPulseSpeed())));
            }

            if (timer.isShowDistance() && mc.player != null) {
                text = text + " " + String.format(java.util.Locale.US, "%.1fm", mc.player.distanceTo(tnt));
            }
            if (timer.isCompact()) {
                text = text.replace(" ", "");
            }
            int width = textRenderer.getWidth(text);

            if (width <= 0) {
                continue;
            }

            matrices.push();

            try {
                matrices.translate(x, y, z);
                matrices.multiply(camera.getRotation());
                matrices.scale(-scale, -scale, scale);

                float textX = timer.isCentered() ? -width / 2.0f : 0.0f;

                if (timer.isShadowEnabled()) {
                    textRenderer.draw(
                            text, textX + 1.0f, 1.0f, timer.getShadowColor(), false,
                            matrices.peek().getModel(), consumers, true, 0, 15728880);
                }

                textRenderer.draw(
                        text,
                        textX,
                        0.0f,
                        color,
                        false,
                        matrices.peek().getModel(),
                        consumers,
                        true,
                        0,
                        15728880
                );

                if (timer.isFuseBarEnabled()) {
                    drawFuseBar(matrices, consumers, tnt, cameraPos, timer);
                }

            } finally {
                matrices.pop();
            }
        }
    }
    private static void drawFuseBar(MatrixStack matrices, VertexConsumerProvider consumers,
                                     TntEntity tnt, Vec3d cameraPos, TntTimer timer) {
        float progress = Math.max(0.0f, Math.min(1.0f, timer.getSeconds(tnt) / 4.0f));
        double y = tnt.getY() + tnt.getHeight() + 0.36 - cameraPos.y;
        double x = tnt.getX() - cameraPos.x;
        double z = tnt.getZ() - cameraPos.z;
        float half = 0.34f;
        VertexConsumer line = consumers.getBuffer(ESPNoDepthLayer.LINES);
        drawLine(matrices, line, x - half, y, z, x + half, y, z, 0.12f, 0.12f, 0.12f, 0.70f);
        float r = ((timer.getColor(tnt) >> 16) & 255) / 255.0f;
        float g = ((timer.getColor(tnt) >> 8) & 255) / 255.0f;
        float b = (timer.getColor(tnt) & 255) / 255.0f;
        drawLine(matrices, line, x - half, y, z, x - half + half * 2.0 * progress, y, z, r, g, b, 1.0f);
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float r, float g, float b, float a) {
        consumer.vertex(matrices.peek().getModel(), (float)x1, (float)y1, (float)z1)
                .color(r, g, b, a).next();
        consumer.vertex(matrices.peek().getModel(), (float)x2, (float)y2, (float)z2)
                .color(r, g, b, a).next();
    }

}
