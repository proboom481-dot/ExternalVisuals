package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.Hitbox;
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

public final class HitboxRenderer {

    private HitboxRenderer() {
    }

    public static void init() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                context -> {

                    MinecraftClient mc =
                            MinecraftClient.getInstance();

                    if (mc == null) {
                        return;
                    }

                    if (mc.world == null) {
                        return;
                    }

                    if (mc.player == null) {
                        return;
                    }

                    if (ExternalVisuals.MODULE_MANAGER == null) {
                        return;
                    }

                    Hitbox hitbox =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    Hitbox.class
                            );

                    if (hitbox == null) {
                        return;
                    }

                    if (!hitbox.isEnabled()) {
                        return;
                    }

                    try {
                        render(
                                context.matrixStack(),
                                context.camera(),
                                context.consumers(),
                                hitbox
                        );
                    } catch (Throwable throwable) {
                        System.err.println("[ExternalVisuals] Hitbox renderer failed; disabling module.");
                        throwable.printStackTrace();
                        hitbox.setEnabled(false);
                    }
                }
        );
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            VertexConsumerProvider consumers,
            Hitbox hitbox
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.world == null) {
            return;
        }

        if (consumers == null) {
            return;
        }

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
                hitbox.getRed() / 255.0f;

        float green =
                hitbox.getGreen() / 255.0f;

        float blue =
                hitbox.getBlue() / 255.0f;

        float alpha =
                hitbox.getAlpha() / 255.0f;

        for (Entity entity :
                mc.world.getEntities()) {

            if (entity == null) {
                continue;
            }

            if (!hitbox.shouldRender(entity)) {
                continue;
            }

            Box box =
                    createVisualBox(
                            entity,
                            hitbox.getExpand()
                    );

            box =
                    box.offset(
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

            if (hitbox.isShowEyeLine()
                    && entity instanceof LivingEntity) {

                drawEyeLine(
                        matrices,
                        consumer,
                        (LivingEntity) entity,
                        cameraX,
                        cameraY,
                        cameraZ,
                        hitbox,
                        red,
                        green,
                        blue,
                        alpha
                );
            }
        }

    }

    private static Box createVisualBox(
            Entity entity,
            double expand
    ) {

        Box original =
                entity.getBoundingBox();

        return new Box(
                original.minX - expand,
                original.minY - expand,
                original.minZ - expand,
                original.maxX + expand,
                original.maxY + expand,
                original.maxZ + expand
        );
    }

    private static void drawEyeLine(
            MatrixStack matrices,
            VertexConsumer consumer,
            LivingEntity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            Hitbox hitbox,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        double eyeY =
                entity.getEyeY()
                        - cameraY;

        float yaw =
                (float) Math.toRadians(
                        entity.getHeadYaw()
                );

        double startX =
                entity.getX()
                        - cameraX;

        double startZ =
                entity.getZ()
                        - cameraZ;

        double length =
                hitbox.getEyeLineLength();

        double endX =
                startX
                        - Math.sin(yaw) * length;

        double endZ =
                startZ
                        + Math.cos(yaw) * length;

        consumer.vertex(
                matrices.peek().getModel(),
                (float) startX,
                (float) eyeY,
                (float) startZ
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
                (float) endX,
                (float) eyeY,
                (float) endZ
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