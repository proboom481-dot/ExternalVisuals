package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.Hitbox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class HitboxMixin {

    @Inject(
            method = "renderEntity",
            at = @At("HEAD")
    )
    private void externalVisuals$renderHitbox(
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            CallbackInfo callbackInfo
    ) {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        Hitbox hitbox =
                ExternalVisuals.MODULE_MANAGER.get(
                        Hitbox.class
                );

        if (hitbox == null || !hitbox.isEnabled()) {
            return;
        }

        if (!hitbox.shouldRender(entity)) {
            return;
        }

        double expand =
                hitbox.getExpand();

        Box originalBox =
                entity.getBoundingBox();

        Box visualBox =
                new Box(
                        originalBox.minX - expand,
                        originalBox.minY - expand,
                        originalBox.minZ - expand,
                        originalBox.maxX + expand,
                        originalBox.maxY + expand,
                        originalBox.maxZ + expand
                );

        Box box =
                visualBox.offset(
                        -cameraX,
                        -cameraY,
                        -cameraZ
                );

        float red =
                hitbox.getRed() / 255.0f;

        float green =
                hitbox.getGreen() / 255.0f;

        float blue =
                hitbox.getBlue() / 255.0f;

        float alpha =
                hitbox.getAlpha() / 255.0f;

        RenderLayer layer =
                RenderLayer.getLines();

        /*
         * Обычный режим.
         */
        if (!hitbox.isThroughWalls()) {

            VertexConsumer consumer =
                    vertexConsumers.getBuffer(layer);

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

            return;
        }

        /*
         * Through Walls:
         *
         * Используем отдельный Immediate provider,
         * чтобы RenderLayer.getLines() реально
         * отрисовался в момент, когда depth test отключён.
         */
        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc == null) {
            return;
        }

        VertexConsumerProvider.Immediate immediate =
                mc.getBufferBuilders()
                        .getEntityVertexConsumers();

        if (immediate == null) {
            return;
        }

        VertexConsumer consumer =
                immediate.getBuffer(layer);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        try {

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

            /*
             * Критически важно:
             * здесь буфер реально отправляется
             * на GPU до возврата depth test.
             */
            immediate.draw(layer);

        } finally {

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    private static void drawEyeLine(
            MatrixStack matrices,
            VertexConsumer consumer,
            LivingEntity living,
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
                living.getEyeY()
                        - cameraY;

        float yaw =
                (float) Math.toRadians(
                        living.getHeadYaw()
                );

        double startX =
                living.getX()
                        - cameraX;

        double startZ =
                living.getZ()
                        - cameraZ;

        double length =
                hitbox.getEyeLineLength();

        double endX =
                startX
                        - Math.sin(yaw)
                        * length;

        double endZ =
                startZ
                        + Math.cos(yaw)
                        * length;

        consumer.vertex(
                matrices.peek().getModel(),
                (float) startX,
                (float) eyeY,
                (float) startZ
        ).color(
                red,
                green,
                blue,
                alpha
        ).next();

        consumer.vertex(
                matrices.peek().getModel(),
                (float) endX,
                (float) eyeY,
                (float) endZ
        ).color(
                red,
                green,
                blue,
                alpha
        ).next();
    }
}