package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.ESP;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

public final class ESPWorldRenderer {

    private ESPWorldRenderer() {
    }

    public static void init() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                context -> {

                    MinecraftClient mc =
                            MinecraftClient.getInstance();

                    if (mc == null
                            || mc.world == null
                            || mc.player == null) {
                        return;
                    }

                    if (ExternalVisuals.MODULE_MANAGER == null) {
                        return;
                    }

                    ESP esp =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    ESP.class
                            );

                    if (esp == null
                            || !esp.isEnabled()
                            || !esp.showBoxes()) {
                        return;
                    }

                    render(
                            context.matrixStack(),
                            context.camera(),
                            esp
                    );
                }
        );
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            ESP esp
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        VertexConsumerProvider.Immediate consumers =
                mc.getBufferBuilders()
                        .getEntityVertexConsumers();

        if (consumers == null) {
            return;
        }

        /*
         * Stable vanilla line layer.
         *
         * We intentionally do NOT use ESPNoDepthLayer here.
         * The previous custom no-depth layer could crash the
         * game at runtime.
         */
        VertexConsumer consumer =
                consumers.getBuffer(
                        RenderLayer.getLines()
                );

        double cameraX =
                camera.getPos().x;

        double cameraY =
                camera.getPos().y;

        double cameraZ =
                camera.getPos().z;

        float red =
                esp.getRed() / 255.0f;

        float green =
                esp.getGreen() / 255.0f;

        float blue =
                esp.getBlue() / 255.0f;

        float alpha =
                esp.getAlpha() / 255.0f;

        for (Entity entity :
                mc.world.getEntities()) {

            if (entity == null) {
                continue;
            }

            if (!esp.shouldRender(entity)) {
                continue;
            }

            /*
             * Entity bounding box relative to camera.
             */
            Box box =
                    entity.getBoundingBox()
                            .offset(
                                    -cameraX,
                                    -cameraY,
                                    -cameraZ
                            );

            WorldRenderer.drawBox(
                    matrices,
                    consumer,
                    box,
                    red,
                    green,
                    blue,
                    alpha
            );

            /*
             * Health bar.
             */
            if (esp.showHealth()
                    && entity instanceof LivingEntity) {

                renderHealthBar(
                        matrices,
                        consumer,
                        (LivingEntity) entity,
                        cameraX,
                        cameraY,
                        cameraZ
                );
            }
        }

        /*
         * Flush vanilla line layer.
         */
        consumers.draw(
                RenderLayer.getLines()
        );
    }

    private static void renderHealthBar(
            MatrixStack matrices,
            VertexConsumer consumer,
            LivingEntity entity,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {

        float maxHealth =
                entity.getMaxHealth();

        if (maxHealth <= 0.0f) {
            return;
        }

        float health =
                Math.max(
                        0.0f,
                        Math.min(
                                entity.getHealth(),
                                maxHealth
                        )
                );

        float progress =
                health / maxHealth;

        Box box =
                entity.getBoundingBox();

        double x =
                box.minX
                        - cameraX
                        - 0.08;

        double y =
                box.minY
                        - cameraY;

        double z =
                box.minZ
                        - cameraZ;

        double height =
                Math.max(
                        0.05,
                        box.maxY - box.minY
                );

        /*
         * Dark health-bar background.
         */
        drawLine(
                matrices,
                consumer,
                x,
                y,
                z,
                x,
                y + height,
                z,
                0.08f,
                0.08f,
                0.08f,
                0.85f
        );

        /*
         * Health amount.
         */
        double healthHeight =
                height * progress;

        float healthRed =
                1.0f - progress;

        float healthGreen =
                progress;

        drawLine(
                matrices,
                consumer,
                x,
                y,
                z,
                x,
                y + healthHeight,
                z,
                healthRed,
                healthGreen,
                0.15f,
                1.0f
        );
    }

    private static void drawLine(
            MatrixStack matrices,
            VertexConsumer consumer,
            double x1,
            double y1,
            double z1,
            double x2,
            double y2,
            double z2,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        consumer.vertex(
                matrices.peek().getModel(),
                (float) x1,
                (float) y1,
                (float) z1
        )
                .color(
                        red,
                        green,
                        blue,
                        alpha
                )
                .next();

        consumer.vertex(
                matrices.peek().getModel(),
                (float) x2,
                (float) y2,
                (float) z2
        )
                .color(
                        red,
                        green,
                        blue,
                        alpha
                )
                .next();
    }
}