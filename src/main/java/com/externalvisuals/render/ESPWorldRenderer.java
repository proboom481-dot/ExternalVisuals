package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.ESP;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
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
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc == null
                    || mc.world == null
                    || mc.player == null
                    || ExternalVisuals.MODULE_MANAGER == null) {
                return;
            }

            ESP esp = ExternalVisuals.MODULE_MANAGER.get(ESP.class);

            if (esp == null || !esp.isEnabled()) {
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
                        esp
                );
            } catch (Throwable throwable) {
                System.err.println(
                        "[ExternalVisuals] ESP renderer failed; disabling ESP."
                );
                throwable.printStackTrace();
                esp.setEnabled(false);
            }
        });
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            VertexConsumerProvider consumers,
            ESP esp
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        double cameraX = camera.getPos().x;
        double cameraY = camera.getPos().y;
        double cameraZ = camera.getPos().z;

        float red = esp.getRed() / 255.0f;
        float green = esp.getGreen() / 255.0f;
        float blue = esp.getBlue() / 255.0f;
        float alpha = esp.getAlpha() / 255.0f;

        /*
         * The no-depth layer is intentional: ESP is the feature that is
         * allowed to remain visible through blocks. We do not mutate entity
         * collision boxes or world geometry.
         */
        VertexConsumer lineConsumer =
                consumers.getBuffer(ESPNoDepthLayer.LINES);

        for (Entity entity : mc.world.getEntities()) {
            if (entity == null || !esp.shouldRender(entity)) {
                continue;
            }

            Box box = entity.getBoundingBox().offset(
                    -cameraX,
                    -cameraY,
                    -cameraZ
            );

            if (esp.showBoxes()) {
                WorldRenderer.drawBox(
                        matrices,
                        lineConsumer,
                        box,
                        red,
                        green,
                        blue,
                        alpha
                );
            }

            if (esp.showHealth() && entity instanceof LivingEntity) {
                renderHealthBar(
                        matrices,
                        lineConsumer,
                        (LivingEntity) entity,
                        cameraX,
                        cameraY,
                        cameraZ
                );
            }

            if (esp.showTracers()) {
                drawTracer(
                        matrices,
                        lineConsumer,
                        cameraX,
                        cameraY,
                        cameraZ,
                        entity,
                        red,
                        green,
                        blue,
                        alpha
                );
            }

            if (esp.showNames()) {
                drawName(
                        matrices,
                        consumers,
                        camera,
                        entity,
                        red,
                        green,
                        blue,
                        alpha
                );
            }
        }
    }

    private static void drawName(
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            Camera camera,
            Entity entity,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc == null || mc.textRenderer == null || entity == null) {
            return;
        }

        TextRenderer textRenderer = mc.textRenderer;
        String text = entity.getDisplayName().getString();

        if (text == null || text.isEmpty()) {
            return;
        }

        int a = clampColor(Math.round(alpha * 255.0f));
        int r = clampColor(Math.round(red * 255.0f));
        int g = clampColor(Math.round(green * 255.0f));
        int b = clampColor(Math.round(blue * 255.0f));

        int color = (a << 24) | (r << 16) | (g << 8) | b;

        double x = entity.getX() - camera.getPos().x;
        double y = entity.getY() + entity.getHeight() + 0.35
                - camera.getPos().y;
        double z = entity.getZ() - camera.getPos().z;

        matrices.push();

        try {
            matrices.translate(x, y, z);
            matrices.multiply(camera.getRotation());
            matrices.scale(-0.025f, -0.025f, 0.025f);

            int width = textRenderer.getWidth(text);

            /*
             * seeThrough=true keeps names readable behind blocks while the
             * ESP geometry uses its own no-depth layer.
             */
            textRenderer.draw(
                    text,
                    -width / 2.0f,
                    0.0f,
                    color,
                    true,
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

    private static void drawTracer(
            MatrixStack matrices,
            VertexConsumer consumer,
            double cameraX,
            double cameraY,
            double cameraZ,
            Entity entity,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        double targetX = entity.getX() - cameraX;
        double targetY = entity.getY()
                + entity.getHeight() * 0.5
                - cameraY;
        double targetZ = entity.getZ() - cameraZ;

        drawLine(
                matrices,
                consumer,
                0.0,
                0.0,
                0.0,
                targetX,
                targetY,
                targetZ,
                red,
                green,
                blue,
                Math.min(1.0f, alpha)
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
        float maxHealth = entity.getMaxHealth();

        if (maxHealth <= 0.0f) {
            return;
        }

        float health = Math.max(
                0.0f,
                Math.min(entity.getHealth(), maxHealth)
        );

        float progress = health / maxHealth;
        Box box = entity.getBoundingBox();

        double x = box.minX - cameraX - 0.08;
        double y = box.minY - cameraY;
        double z = box.minZ - cameraZ;

        double height = Math.max(
                0.05,
                box.maxY - box.minY
        );

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

        double healthHeight = height * progress;

        drawLine(
                matrices,
                consumer,
                x,
                y,
                z,
                x,
                y + healthHeight,
                z,
                1.0f - progress,
                progress,
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
        ).color(red, green, blue, alpha).next();

        consumer.vertex(
                matrices.peek().getModel(),
                (float) x2,
                (float) y2,
                (float) z2
        ).color(red, green, blue, alpha).next();
    }

    private static int clampColor(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
