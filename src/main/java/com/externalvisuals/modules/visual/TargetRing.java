package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import com.externalvisuals.render.ESPNoDepthLayer;
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
    private final SliderSetting height = addSetting(new SliderSetting("Height", 0.12, 0.02, 1.0, 0.01));
    private final SliderSetting segments = addSetting(new SliderSetting("Segments", 48.0, 16.0, 96.0, 1.0));
    private final StringSetting mode = addSetting(new StringSetting("Mode", "ORBIT", "RING", "ORBIT", "HELIX"));
    private final BooleanSetting pulse = addSetting(new BooleanSetting("Pulse", true));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF9B5CFF));


    private final BooleanSetting doubleRing = addSetting(new BooleanSetting("Double Ring", true));
    private final SliderSetting secondRadius = addSetting(new SliderSetting("Second Radius", 0.90, 0.2, 2.5, 0.05));
    private final BooleanSetting verticalRing = addSetting(new BooleanSetting("Vertical Ring", false));
    private final BooleanSetting targetPulse = addSetting(new BooleanSetting("Target Pulse", true));
    private final SliderSetting pulseAmount = addSetting(new SliderSetting("Pulse Amount", 0.10, 0.0, 0.4, 0.01));
    private final ColorSetting secondaryColor = addSetting(new ColorSetting("Secondary Color", 0xFF42D9FF));
    private final BooleanSetting rainbow = addSetting(new BooleanSetting("Rainbow", false));
    private final SliderSetting lineAlpha = addSetting(new SliderSetting("Line Alpha", 1.0, 0.1, 1.0, 0.05));
    private final BooleanSetting lockRotation = addSetting(new BooleanSetting("Lock Rotation", false));

    public TargetRing() {
        super("Target Ring", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public double getRange() { return Math.max(1.0, range.getValue()); }
    public double getRadius() { return Math.max(0.1, radius.getValue()); }
    public double getSpeed() { return Math.max(0.05, speed.getValue()); }
    public double getHeight() { return Math.max(0.01, height.getValue()); }
    public int getSegments() { return Math.max(16, segments.getValue().intValue()); }
    public int getColor() { return color.getColor(); }
    public StringSetting getModeSetting() { return mode; }
    public boolean isPulse() { return pulse.isEnabled(); }
    public boolean isDoubleRing() { return doubleRing.isEnabled(); }
    public double getSecondRadius() { return Math.max(0.1, secondRadius.getValue()); }
    public boolean isVerticalRing() { return verticalRing.isEnabled(); }
    public boolean isTargetPulse() { return targetPulse.isEnabled(); }
    public double getPulseAmount() { return Math.max(0.0, pulseAmount.getValue()); }
    public int getSecondaryColor() { return secondaryColor.getColor(); }
    public boolean isRainbow() { return rainbow.isEnabled(); }
    public float getLineAlpha() { return Math.max(0.1f, Math.min(1.0f, lineAlpha.getValue().floatValue())); }
    public boolean isLockRotation() { return lockRotation.isEnabled(); }

    public Entity getTarget(MinecraftClient mc) {
        if (mc.targetedEntity instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) mc.targetedEntity;
            if (entity.isAlive() && mc.player != null && mc.player.distanceTo(entity) <= getRange()) {
                return entity;
            }
        }
        return null;
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.world == null || mc.player == null
                    || com.externalvisuals.ExternalVisuals.MODULE_MANAGER == null) return;
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

    private static void render(MatrixStack matrices, Camera camera, VertexConsumer consumer,
                               TargetRing module, LivingEntity target) {
        Box box = target.getBoundingBox();
        double cx = (box.minX + box.maxX) * 0.5 - camera.getPos().x;
        double cz = (box.minZ + box.maxZ) * 0.5 - camera.getPos().z;
        double baseY = box.minY - camera.getPos().y + 0.05;
        double baseRadius = Math.max(box.getXLength(), box.getZLength()) * module.getRadius();
        int color = module.getColor();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = ((color >>> 24) & 255) / 255.0f * module.getLineAlpha();
        double time = System.currentTimeMillis() / 1000.0;
        double rotation = time * module.getSpeed();
        double pulse = module.isPulse() ? 1.0 + Math.sin(time * 4.0) * (module.isTargetPulse() ? module.getPulseAmount() : 0.08) : 1.0;
        baseRadius *= pulse;
        int count = module.getSegments();

        for (int i = 0; i < count; i++) {
            double p1 = rotation + Math.PI * 2.0 * i / count;
            double p2 = rotation + Math.PI * 2.0 * (i + 1) / count;
            double y1 = baseY;
            double y2 = baseY;
            if (module.getModeSetting().is("ORBIT")) {
                y1 += Math.sin(p1 * 2.0 + time * 3.0) * module.getHeight();
                y2 += Math.sin(p2 * 2.0 + time * 3.0) * module.getHeight();
            } else if (module.getModeSetting().is("HELIX")) {
                y1 += (0.5 + 0.5 * Math.sin(p1 + time * 2.0)) * target.getHeight();
                y2 += (0.5 + 0.5 * Math.sin(p2 + time * 2.0)) * target.getHeight();
            }
            drawLine(matrices, consumer,
                    cx + Math.cos(p1) * baseRadius, y1, cz + Math.sin(p1) * baseRadius,
                    cx + Math.cos(p2) * baseRadius, y2, cz + Math.sin(p2) * baseRadius,
                    r, g, b, a);
        }

        if (module.isDoubleRing()) {
            int secondary = module.getSecondaryColor();
            float sr = ((secondary >> 16) & 255) / 255.0f;
            float sg = ((secondary >> 8) & 255) / 255.0f;
            float sb = (secondary & 255) / 255.0f;
            float sa = ((secondary >>> 24) & 255) / 255.0f * module.getLineAlpha() * 0.85f;
            double second = baseRadius * module.getSecondRadius();
            for (int i = 0; i < count; i++) {
                double p1 = rotation * -0.7 + Math.PI * 2.0 * i / count;
                double p2 = rotation * -0.7 + Math.PI * 2.0 * (i + 1) / count;
                double yy1 = baseY + 0.025;
                double yy2 = yy1;
                if (module.isVerticalRing()) {
                    yy1 += Math.cos(p1 + time * 2.0) * module.getHeight() * 1.5;
                    yy2 += Math.cos(p2 + time * 2.0) * module.getHeight() * 1.5;
                }
                drawLine(matrices, consumer,
                        cx + Math.cos(p1) * second, yy1, cz + Math.sin(p1) * second,
                        cx + Math.cos(p2) * second, yy2, cz + Math.sin(p2) * second,
                        sr, sg, sb, sa);
            }
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
