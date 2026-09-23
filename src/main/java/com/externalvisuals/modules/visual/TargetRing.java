package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.render.ESPNoDepthLayer;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

public final class TargetRing extends Module {
    private final SliderSetting range = addSetting(new SliderSetting("Range", 32.0, 2.0, 64.0, 1.0));
    private final SliderSetting radius = addSetting(new SliderSetting("Radius", 0.75, 0.2, 2.0, 0.05));
    private final SliderSetting speed = addSetting(new SliderSetting("Speed", 1.6, 0.1, 5.0, 0.1));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF9B5CFF));

    public TargetRing() {
        super("Target Ring", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public double getRange() { return Math.max(1.0, range.getValue()); }
    public double getRadius() { return Math.max(0.1, radius.getValue()); }
    public double getSpeed() { return Math.max(0.05, speed.getValue()); }
    public int getColor() { return color.getColor(); }

    public Entity getTarget(MinecraftClient mc) {
        if (mc.targetedEntity instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) mc.targetedEntity;
            if (entity.isAlive() && mc.player != null && mc.player.distanceTo(entity) <= getRange()) return entity;
        }
        return null;
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.world == null || mc.player == null || com.externalvisuals.ExternalVisuals.MODULE_MANAGER == null) return;
            TargetRing module = com.externalvisuals.ExternalVisuals.MODULE_MANAGER.get(TargetRing.class);
            if (module == null || !module.isEnabled()) return;
            Entity target = module.getTarget(mc);
            if (!(target instanceof LivingEntity)) return;
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            try {
                render(context.matrixStack(), context.camera(), consumers.getBuffer(ESPNoDepthLayer.LINES), module, (LivingEntity) target);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] Target Ring renderer failed.");
                throwable.printStackTrace();
                module.setEnabled(false);
            }
        });
    }

    private static void render(MatrixStack matrices, Camera camera, VertexConsumer consumer, TargetRing module, LivingEntity target) {
        Box box = target.getBoundingBox();
        double cx = (box.minX + box.maxX) * 0.5 - camera.getPos().x;
        double cz = (box.minZ + box.maxZ) * 0.5 - camera.getPos().z;
        double y = box.minY - camera.getPos().y + 0.05;
        double baseRadius = Math.max(box.getXLength(), box.getZLength()) * module.getRadius();
        float r = ((module.getColor() >> 16) & 255) / 255.0f;
        float g = ((module.getColor() >> 8) & 255) / 255.0f;
        float b = (module.getColor() & 255) / 255.0f;
        float a = ((module.getColor() >>> 24) & 255) / 255.0f;
        double time = System.currentTimeMillis() / 1000.0;
        double rotation = time * module.getSpeed();
        int segments = 48;
        for (int i = 0; i < segments; i++) {
            double a1 = rotation + (Math.PI * 2.0 * i / segments);
            double a2 = rotation + (Math.PI * 2.0 * (i + 1) / segments);
            drawLine(matrices, consumer,
                    cx + Math.cos(a1) * baseRadius, y, cz + Math.sin(a1) * baseRadius,
                    cx + Math.cos(a2) * baseRadius, y, cz + Math.sin(a2) * baseRadius,
                    r, g, b, a);
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float r, float g, float b, float a) {
        consumer.vertex(matrices.peek().getModel(), (float) x1, (float) y1, (float) z1).color(r, g, b, a).next();
        consumer.vertex(matrices.peek().getModel(), (float) x2, (float) y2, (float) z2).color(r, g, b, a).next();
    }
}
