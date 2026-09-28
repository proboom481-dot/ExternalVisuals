package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.TrajectoryPrediction;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public final class TrajectoryRenderer {
    private TrajectoryRenderer() {}

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.player == null || mc.world == null || ExternalVisuals.MODULE_MANAGER == null) return;
            TrajectoryPrediction module = ExternalVisuals.MODULE_MANAGER.get(TrajectoryPrediction.class);
            if (module == null || !module.isEnabled()) return;
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            try {
                render(context.matrixStack(), context.camera(), consumers.getBuffer(ESPNoDepthLayer.LINES), module);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] Trajectory renderer failed.");
                throwable.printStackTrace();
                module.setEnabled(false);
            }
        });
    }

    private static void render(MatrixStack matrices, Camera camera, VertexConsumer consumer,
                               TrajectoryPrediction module) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d origin = mc.player.getCameraPosVec(1.0f);
        Vec3d direction = mc.player.getRotationVec(1.0f).normalize();
        Vec3d velocity = direction.multiply(module.getVelocity());
        Vec3d point = origin;
        double gravity = module.getGravity();
        double maxRange = module.getRange();
        int color = module.getColor();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = ((color >>> 24) & 255) / 255.0f;

        for (int i = 0; i < module.getSteps(); i++) {
            Vec3d next = point.add(velocity);
            if (next.distanceTo(origin) > maxRange) break;
            Vec3d rel1 = point.subtract(camera.getPos());
            Vec3d rel2 = next.subtract(camera.getPos());
            drawLine(matrices, consumer, rel1, rel2, r, g, b, a);
            point = next;
            velocity = velocity.add(0.0, -gravity, 0.0).multiply(0.99);
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 Vec3d a, Vec3d b, float r, float g, float bl, float alpha) {
        consumer.vertex(matrices.peek().getModel(), (float)a.x, (float)a.y, (float)a.z)
                .color(r, g, bl, alpha).next();
        consumer.vertex(matrices.peek().getModel(), (float)b.x, (float)b.y, (float)b.z)
                .color(r, g, bl, alpha).next();
    }
}
