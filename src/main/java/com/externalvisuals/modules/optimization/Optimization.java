package com.externalvisuals.modules.optimization;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

/**
 * Client-side optimization profile. It never touches server state and keeps
 * an optional Sodium compatibility flag instead of bundling another mod.
 */
public final class Optimization extends Module {
    private final BooleanSetting auto = addSetting(new BooleanSetting("Auto Optimize", true));
    private final BooleanSetting sodiumCompat = addSetting(new BooleanSetting("Sodium Compatibility", true));
    private final BooleanSetting disableClouds = addSetting(new BooleanSetting("Disable Clouds", true));
    private final BooleanSetting fastGraphics = addSetting(new BooleanSetting("Fast Graphics", false));
    private final BooleanSetting minimalParticles = addSetting(new BooleanSetting("Minimal Particles", false));
    private final BooleanSetting noEntityShadows = addSetting(new BooleanSetting("No Entity Shadows", true));
    private final BooleanSetting noViewBob = addSetting(new BooleanSetting("No View Bob", false));
    private final BooleanSetting noVsync = addSetting(new BooleanSetting("Disable VSync", false));
    private final BooleanSetting fpsLimit = addSetting(new BooleanSetting("FPS Limit", false));
    private final SliderSetting maxFps = addSetting(new SliderSetting("Max FPS", 240, 30, 1000, 10));
    private final SliderSetting entityDistance = addSetting(new SliderSetting("Entity Distance", 1.0, 0.25, 2.0, 0.05));
    private final SliderSetting mipmap = addSetting(new SliderSetting("Mipmap", 2, 0, 4, 1));
    private final SliderSetting biomeBlend = addSetting(new SliderSetting("Biome Blend", 0, 0, 7, 1));
    private final BooleanSetting smartParticles = addSetting(new BooleanSetting("Smart Particles", true));
    private final BooleanSetting cacheEsp = addSetting(new BooleanSetting("Cache ESP", true));
    private final SliderSetting espEntityBudget = addSetting(new SliderSetting("ESP Entity Budget", 80, 10, 256, 1));
    private final SliderSetting trailBudget = addSetting(new SliderSetting("Trail Point Budget", 1200, 100, 10000, 100));
    private final SliderSetting hudRate = addSetting(new SliderSetting("HUD Update Rate", 20, 1, 60, 1));
    private final BooleanSetting artificialRendering = addSetting(new BooleanSetting("Artificial Rendering", true));
    private final SliderSetting artificialBudget = addSetting(new SliderSetting("Artificial Budget", 48, 4, 256, 4));
    private final BooleanSetting cullDistantEffects = addSetting(new BooleanSetting("Cull Distant Effects", true));
    private final SliderSetting effectRange = addSetting(new SliderSetting("Effect Range", 48, 8, 128, 1));
    private final BooleanSetting skipMenuRender = addSetting(new BooleanSetting("Skip Menu Effects", true));
    private final BooleanSetting batchLines = addSetting(new BooleanSetting("Batch Lines", true));
    private final BooleanSetting batchHud = addSetting(new BooleanSetting("Batch HUD", true));
    private final BooleanSetting reuseMatrices = addSetting(new BooleanSetting("Reuse Matrices", true));
    private final BooleanSetting safeRenderer = addSetting(new BooleanSetting("Safe Renderer", true));
    private final BooleanSetting recoverEsp = addSetting(new BooleanSetting("Recover ESP", true));
    private final BooleanSetting failSoft = addSetting(new BooleanSetting("Fail Soft", true));
    private final BooleanSetting reduceParticlesOnLowFps = addSetting(new BooleanSetting("Reduce On Low FPS", true));
    private final SliderSetting lowFps = addSetting(new SliderSetting("Low FPS Threshold", 45, 15, 120, 1));
    private final BooleanSetting reduceTrailsOnLowFps = addSetting(new BooleanSetting("Reduce Trails On Low FPS", true));
    private final SliderSetting lowFpsTrailScale = addSetting(new SliderSetting("Low FPS Trail Scale", 0.45, 0.10, 1.0, 0.05));
    private final BooleanSetting sodiumFriendlyBuffers = addSetting(new BooleanSetting("Sodium Friendly Buffers", true));
    private final BooleanSetting avoidImmediateGl = addSetting(new BooleanSetting("Avoid Immediate GL", true));
    private final BooleanSetting worldChangeCleanup = addSetting(new BooleanSetting("World Cleanup", true));
    private final BooleanSetting memoryGuard = addSetting(new BooleanSetting("Memory Guard", true));
    private final SliderSetting maxTrailEntities = addSetting(new SliderSetting("Max Trail Entities", 64, 8, 256, 1));
    private final SliderSetting renderInterval = addSetting(new SliderSetting("Artificial Render Interval", 1, 1, 10, 1));
    private final StringSetting profile = addSetting(new StringSetting("Profile", "BALANCED", "QUALITY", "BALANCED", "FPS", "EXTREME"));
    private final BooleanSetting boost100 = addSetting(new BooleanSetting("100 FPS Boost", false));
    private final BooleanSetting adaptiveBoost = addSetting(new BooleanSetting("Adaptive FPS Boost", true));
    private final SliderSetting targetFps = addSetting(new SliderSetting("Target FPS", 100, 30, 100, 5));
    private final BooleanSetting aggressiveParticles = addSetting(new BooleanSetting("Aggressive Particles", true));
    private final BooleanSetting aggressiveEntityDistance = addSetting(new BooleanSetting("Aggressive Entity Distance", true));

    private boolean captured;
    private CloudRenderMode oldClouds;
    private GraphicsMode oldGraphics;
    private ParticlesMode oldParticles;
    private boolean oldEntityShadows, oldBobView, oldVsync;
    private int oldMaxFps, oldMipmap, oldBiomeBlend;
    private float oldEntityDistance;

    public Optimization() { super("Optimization", ModuleCategory.OPTIMIZATION, 0); }

    @Override public void onEnable() {
        captureOptions();
    }

    @Override public void onDisable() {
        restoreOptions();
    }

    private void captureOptions() {
        if (captured || mc.options == null) return;
        oldClouds = mc.options.cloudRenderMode;
        oldGraphics = mc.options.graphicsMode;
        oldParticles = mc.options.particles;
        oldEntityShadows = mc.options.entityShadows;
        oldBobView = mc.options.bobView;
        oldVsync = mc.options.enableVsync;
        oldMaxFps = mc.options.maxFps;
        oldEntityDistance = mc.options.entityDistanceScaling;
        oldMipmap = mc.options.mipmapLevels;
        oldBiomeBlend = mc.options.biomeBlendRadius;
        captured = true;
    }

    private void restoreOptions() {
        if (!captured || mc.options == null) return;
        try {
            mc.options.cloudRenderMode = oldClouds;
            mc.options.graphicsMode = oldGraphics;
            mc.options.particles = oldParticles;
            mc.options.entityShadows = oldEntityShadows;
            mc.options.bobView = oldBobView;
            mc.options.enableVsync = oldVsync;
            mc.options.maxFps = oldMaxFps;
            mc.options.entityDistanceScaling = oldEntityDistance;
            mc.options.mipmapLevels = oldMipmap;
            mc.options.biomeBlendRadius = oldBiomeBlend;
        } catch (Throwable ignored) {
        } finally {
            captured = false;
        }
    }

    public boolean isSodiumLoaded() { return FabricLoader.getInstance().isModLoaded("sodium"); }
    public int getEspEntityBudget() { return Math.max(1, espEntityBudget.getValue().intValue()); }
    public int getTrailBudget() { return Math.max(100, trailBudget.getValue().intValue()); }
    public int getArtificialBudget() { return Math.max(1, artificialBudget.getValue().intValue()); }
    public int getLowFpsThreshold() { return Math.max(15, lowFps.getValue().intValue()); }
    public float getLowFpsTrailScale() { return Math.max(0.10f, Math.min(1.0f, lowFpsTrailScale.getValue().floatValue())); }
    public boolean isArtificialRendering() { return artificialRendering.isEnabled(); }
    public boolean shouldCullEffects() { return cullDistantEffects.isEnabled(); }
    public double getEffectRange() { return effectRange.getValue(); }
    public int getMaxTrailEntities() { return Math.max(1, maxTrailEntities.getValue().intValue()); }
    public int getRenderInterval() { return Math.max(1, renderInterval.getValue().intValue()); }
    public boolean isMemoryGuardEnabled() { return memoryGuard.isEnabled(); }
    public boolean is100FpsBoostEnabled() { return boost100.isEnabled(); }
    public int getTargetFps() { return Math.max(30, Math.min(100, targetFps.getValue().intValue())); }

    private int currentFps() {
        try {
            String debug = mc.fpsDebugString;
            if (debug == null) return 0;
            int end = debug.indexOf(" fps");
            if (end < 0) return 0;
            int start = end - 1;
            while (start >= 0 && Character.isDigit(debug.charAt(start))) start--;
            return Integer.parseInt(debug.substring(start + 1, end));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private void apply100FpsBoost(int fps) {
        mc.options.enableVsync = false;
        mc.options.cloudRenderMode = CloudRenderMode.OFF;
        mc.options.graphicsMode = GraphicsMode.FAST;
        mc.options.entityShadows = false;
        mc.options.bobView = false;
        mc.options.biomeBlendRadius = 0;
        mc.options.mipmapLevels = Math.min(mc.options.mipmapLevels, 1);
        mc.options.entityDistanceScaling = Math.min(mc.options.entityDistanceScaling, 0.75f);
        if (aggressiveParticles.isEnabled() && fps > 0 && fps < getTargetFps()) {
            mc.options.particles = ParticlesMode.MINIMAL;
        }
        // Never cap the renderer below the requested target in boost mode.
        mc.options.maxFps = Math.max(100, getTargetFps());
    }

    @Override public void onTick() {
        if (!auto.isEnabled() || mc.options == null) return;
        try {
            if (disableClouds.isEnabled()) mc.options.cloudRenderMode = CloudRenderMode.OFF;
            if (fastGraphics.isEnabled()) mc.options.graphicsMode = GraphicsMode.FAST;
            if (minimalParticles.isEnabled()) mc.options.particles = ParticlesMode.MINIMAL;
            if (noEntityShadows.isEnabled()) mc.options.entityShadows = false;
            if (noViewBob.isEnabled()) mc.options.bobView = false;
            if (noVsync.isEnabled()) mc.options.enableVsync = false;
            if (fpsLimit.isEnabled()) mc.options.maxFps = Math.max(30, maxFps.getValue().intValue());
            mc.options.entityDistanceScaling = (float) entityDistance.getValue().doubleValue();
            mc.options.mipmapLevels = mipmap.getValue().intValue();
            mc.options.biomeBlendRadius = biomeBlend.getValue().intValue();

            if (boost100.isEnabled()) {
                int fps = currentFps();
                if (!adaptiveBoost.isEnabled() || fps == 0 || fps < getTargetFps()) {
                    apply100FpsBoost(fps);
                } else {
                    // Once the target is reached, keep the lightweight profile but avoid
                    // unnecessary extra reductions. This prevents visual quality from
                    // oscillating every tick around the target.
                    mc.options.maxFps = Math.max(100, getTargetFps());
                }
            }
        } catch (Throwable ignored) {}
    }
}
