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
                    - cameraPos.y;
            double z = tnt.getZ() - cameraPos.z;

            int color = timer.getColor(tnt);
            float scale =
                    Math.max(
                            0.0125f,
                            0.030f * timer.getSize()
                    );

            int width = textRenderer.getWidth(text);

            if (width <= 0) {
                continue;
            }

            matrices.push();

            try {
                matrices.translate(x, y, z);
                matrices.multiply(camera.getRotation());
                matrices.scale(-scale, -scale, scale);

                float textX = -width / 2.0f;

                textRenderer.draw(
                        text,
                        textX + 1.0f,
                        1.0f,
                        0xAA000000,
                        false,
                        matrices.peek().getModel(),
                        consumers,
                        true,
                        0,
                        15728880
                );

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
            } finally {
                matrices.pop();
            }
        }
    }
}
