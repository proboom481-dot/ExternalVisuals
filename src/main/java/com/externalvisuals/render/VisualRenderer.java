package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public final class VisualRenderer {
    private VisualRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> { renderWorldParticles(ctx.matrixStack(), ctx.camera().getPos()); renderWorldDamage(ctx.matrixStack(), ctx.camera().getPos()); renderCombatFX(ctx.matrixStack(), ctx.camera().getPos()); });
        HudRenderCallback.EVENT.register(VisualRenderer::renderHud);
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static boolean enabled(String name) {
        for (Module m : ExternalVisuals.MODULES.getModules())
            if (m.getName().equals(name)) return m.isEnabled();
        return false;
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderHud(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        VisualState.tick();

        if (enabled("Custom Crosshair")) renderCrosshair(matrices, mc);
        if (enabled("Hitmarkers")) renderHitmarker(matrices, mc);
        if (enabled("Low HP Warning")) renderLowHp(matrices, mc);
        if (enabled("Damage Numbers")) renderDamage(matrices, mc);
        if (enabled("Hit Particles")) renderParticles(matrices, mc);
        if (enabled("Critical Effects")) renderCritical(matrices, mc);
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderCrosshair(MatrixStack m, MinecraftClient mc) {
        int cx = mc.getWindow().getScaledWidth()/2;
        int cy = mc.getWindow().getScaledHeight()/2;
        DrawableHelper.fill(m, cx-1, cy-7, cx+1, cy+7, 0xFFD08AFF);
        DrawableHelper.fill(m, cx-7, cy-1, cx+7, cy+1, 0xFFD08AFF);
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderHitmarker(MatrixStack m, MinecraftClient mc) {
        if (System.currentTimeMillis() - VisualState.lastHitMarker > 350) return;
        int cx = mc.getWindow().getScaledWidth()/2, cy = mc.getWindow().getScaledHeight()/2;
        int c = 0xFFFFFFFF;
        DrawableHelper.fill(m,cx-8,cy-8,cx-6,cy-2,c);
        DrawableHelper.fill(m,cx-8,cy+2,cx-6,cy+8,c);
        DrawableHelper.fill(m,cx+6,cy-8,cx+8,cy-2,c);
        DrawableHelper.fill(m,cx+6,cy+2,cx+8,cy+8,c);
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderLowHp(MatrixStack m, MinecraftClient mc) {
        if (VisualState.lowHpPulse <= 0) return;
        int a = (int)(VisualState.lowHpPulse * 55);
        int c = (a << 24) | 0x550000;
        int w = mc.getWindow().getScaledWidth(), h = mc.getWindow().getScaledHeight();
        DrawableHelper.fill(m,0,0,w,3,c);
        DrawableHelper.fill(m,0,h-3,w,h,c);
        DrawableHelper.fill(m,0,0,3,h,c);
        DrawableHelper.fill(m,w-3,0,w,h,c);
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderDamage(MatrixStack m, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int i = 0;
        for (VisualState.Damage d : VisualState.DAMAGES) {
            float x = sw/2f + (i % 5 - 2) * 18;
            float y = sh/2f - 28 - d.age * 1.2f;
            mc.textRenderer.draw(m, new LiteralText(String.format("§d%.1f", d.value)),
                    x, y, 0xFFFFFFFF);
            i++;
        }
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderParticles(MatrixStack m, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int i = 0;
        for (VisualState.Particle p : VisualState.PARTICLES) {
            int x = sw/2 + (i % 8 - 4) * 9;
            int y = sh/2 - 20 - p.age;
            DrawableHelper.fill(m,x,y,x+2,y+2,0xFFD08AFF);
            i++;
        }
    }

    private static void renderCombatFX(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null) return;
        long now=System.currentTimeMillis();

        if(on("Critical Effects") && now < CombatVisualState.criticalUntil) {
            VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
            matrices.push();
            Vec3d p=mc.player.getCameraPosVec(1.0f);
            matrices.translate(-camera.x,-camera.y,-camera.z);
            float rr=0.35f;
            v.vertex(matrices.peek().getModel(),(float)p.x-rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x+rr,(float)p.y,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y-rr,(float)p.z,1f,0.35f,1f,1f).next();
            v.vertex(matrices.peek().getModel(),(float)p.x,(float)p.y+rr,(float)p.z,1f,0.35f,1f,1f).next();
            matrices.pop();
            consumers.draw();
        }
    }

    private static void renderWorldDamage(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Damage Numbers")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Damage d: VisualState.damages()) {
            if(d.age > 30) continue;
            float scale=0.025f;
            matrices.push();
            matrices.translate(d.x, d.y + d.age*0.015, d.z);
            matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
            matrices.scale(-scale,-scale,scale);
            String txt=String.format("%.1f", d.amount);
            int color= d.critical ? 0xFFFF55FF : 0xFFFFFFFF;
            mc.textRenderer.draw(txt, -mc.textRenderer.getWidth(txt)/2f, 0, color, true, matrices.peek().getModel(), consumers, false, 0, 15728880);
            matrices.pop();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderWorldParticles(MatrixStack matrices, Vec3d camera) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world==null || mc.player==null || !on("Hit Particles")) return;
        VertexConsumerProvider.Immediate consumers=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer v=consumers.getBuffer(RenderLayer.getLightning());
        matrices.push();
        matrices.translate(-camera.x,-camera.y,-camera.z);
        for(VisualState.Particle p: VisualState.particles()) {
            double x=p.x, y=p.y, z=p.z;
            float a=Math.max(0.08f, 1.0f-p.age/20.0f);
            float r=0.04f;
            v.vertex(matrices.peek().getModel(),(float)x-r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x+r,(float)y,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y-r,(float)z,1f,0.35f,1f,a).next();
            v.vertex(matrices.peek().getModel(),(float)x,(float)y+r,(float)z,1f,0.35f,1f,a).next();
        }
        matrices.pop();
        consumers.draw();
    }

    private static void renderCritical(MatrixStack m, MinecraftClient mc) {
        if (System.currentTimeMillis() - VisualState.lastCritical > 260) return;
        int cx=mc.getWindow().getScaledWidth()/2, cy=mc.getWindow().getScaledHeight()/2;
        int s=12;
        DrawableHelper.fill(m,cx-s,cy-1,cx+s,cy+1,0xFFD08AFF);
        DrawableHelper.fill(m,cx-1,cy-s,cx+1,cy+s,0xFFD08AFF);
    }
}
