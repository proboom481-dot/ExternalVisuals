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
import net.minecraft.util.math.Vec3d;

public final class TntTimerRenderer {

    private TntTimerRenderer() {
    }

    public static void init() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                context -> {

                    MinecraftClient mc =
                            MinecraftClient.getInstance();

                    if (mc.player == null) {
                        return;
                    }

                    if (mc.world == null) {
                        return;
                    }

                    if (ExternalVisuals.MODULE_MANAGER == null) {
                        return;
                    }

                    TntTimer timer =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    TntTimer.class
                            );

                    if (timer == null) {
                        return;
                    }

                    if (!timer.isEnabled()) {
                        return;
                    }

                    MatrixStack matrices =
                            context.matrixStack();

                    Camera camera =
                            context.camera();

                    if (matrices == null || camera == null) {
                        return;
                    }

                    render(
                            matrices,
                            camera,
                            timer
                    );
                }
        );
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            TntTimer timer
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.world == null) {
            return;
        }

        if (mc.textRenderer == null) {
            return;
        }

        TextRenderer textRenderer =
                mc.textRenderer;

        Vec3d cameraPos =
                camera.getPos();

        VertexConsumerProvider.Immediate consumers =
                mc.getBufferBuilders()
                        .getEntityVertexConsumers();

        if (consumers == null) {
            return;
        }

        for (Entity entity :
                mc.world.getEntities()) {

            if (!(entity instanceof TntEntity)) {
                continue;
            }

            TntEntity tnt =
                    (TntEntity) entity;

            if (!timer.shouldRender(tnt)) {
                continue;
            }

            int fuse =
                    timer.getFuseTicks(tnt);

            /*
             * TNT без активного fuse
             * не отображаем.
             */
            if (fuse <= 0) {
                continue;
            }

            String text =
                    timer.getText(tnt);

            if (text == null
                    || text.isEmpty()) {
                continue;
            }

            /*
             * Положение TNT относительно камеры.
             */
            double x =
                    tnt.getX()
                            - cameraPos.x;

            double y =
                    tnt.getY()
                            + tnt.getHeight()
                            + 0.55;

            double z =
                    tnt.getZ()
                            - cameraPos.z;

            int color =
                    timer.getColor(tnt);

            /*
             * Размер текста.
             */
            float scale =
                    0.025f
                            * timer.getSize();

            if (scale < 0.01f) {
                scale = 0.01f;
            }

            int width =
                    textRenderer.getWidth(
                            text
                    );

            /*
             * Защита от слишком больших
             * или некорректных значений.
             */
            if (width <= 0) {
                continue;
            }

            matrices.push();

            /*
             * Перемещаем текст
             * непосредственно над TNT.
             */
            matrices.translate(
                    x,
                    y,
                    z
            );

            /*
             * Текст всегда смотрит
             * на камеру.
             */
            matrices.multiply(
                    camera.getRotation()
            );

            /*
             * Minecraft text renderer
             * использует обратное направление
             * по X/Y для world-space текста.
             */
            matrices.scale(
                    -scale,
                    -scale,
                    scale
            );

            float textX =
                    -width / 2.0f;

            /*
             * Тень.
             */
            int shadowColor =
                    0xAA000000;

            textRenderer.draw(
                    text,
                    textX + 1.0f,
                    1.0f,
                    shadowColor,
                    false,
                    matrices.peek().getModel(),
                    consumers,
                    true,
                    0,
                    15728880
            );

            /*
             * Основной текст.
             */
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

            matrices.pop();
        }

        /*
         * Обязательно отправляем накопленный
         * текстовый буфер на GPU.
         */
        consumers.draw();
    }
}