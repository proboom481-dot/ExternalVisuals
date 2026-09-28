package com.externalvisuals.modules.visual;

import com.externalvisuals.ExternalVisuals;
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
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

public final class BlockOutline extends Module {
    private final SliderSetting range = addSetting(new SliderSetting("Range", 6.0, 1.0, 12.0, 0.5));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF9B5CFF));

    public BlockOutline() {
        super("Block Outline", ModuleCategory.VISUALS, GLFW.GLFW_KEY_UNKNOWN);
    }

    public double getRange() { return Math.max(1.0, range.getValue()); }
    public int getColor() { return color.getColor(); }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.world == null || mc.player == null || ExternalVisuals.MODULE_MANAGER == null) return;
            BlockOutline module = ExternalVisuals.MODULE_MANAGER.get(BlockOutline.class);
            if (module == null || !module.isEnabled()) return;
            if (!(mc.crosshairTarget instanceof BlockHitResult)) return;
            BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
            BlockPos pos = hit.getBlockPos();
            if (mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > module.getRange() * module.getRange()) return;
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            try {
                render(context.matrixStack(), context.camera(), consumers.getBuffer(ESPNoDepthLayer.LINES), module, pos);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] Block Outline renderer failed.");
                throwable.printStackTrace();
                module.setEnabled(false);
            }
        });
    }

    private static void render(MatrixStack matrices, Camera camera, VertexConsumer consumer, BlockOutline module, BlockPos pos) {
        double x = pos.getX() - camera.getPos().x;
        double y = pos.getY() - camera.getPos().y;
        double z = pos.getZ() - camera.getPos().z;
        Box box = new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0).expand(0.002);
        int color = module.getColor();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = ((color >>> 24) & 255) / 255.0f;
        WorldRenderer.drawBox(matrices, consumer, box, r, g, b, a);
    }
}
