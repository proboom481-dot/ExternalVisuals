package com.externalvisuals.module;

import com.externalvisuals.modules.camera.Zoom;
import com.externalvisuals.modules.camera.ScreenStretch;
import com.externalvisuals.modules.player.AutoSprint;
import com.externalvisuals.modules.player.ElytraSwapper;
import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.modules.combat.DamageNumbers;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.combat.AutoTotem;
import com.externalvisuals.modules.hud.FPSHud;
import com.externalvisuals.modules.hud.CoordinatesHud;
import com.externalvisuals.modules.hud.KeystrokesHud;
import com.externalvisuals.modules.hud.SessionHud;
import com.externalvisuals.modules.hud.TargetHud;
import com.externalvisuals.modules.hud.PingHud;
import com.externalvisuals.modules.hud.ClockHud;
import com.externalvisuals.modules.hud.CpsHud;
import com.externalvisuals.modules.hud.PotionHud;
import com.externalvisuals.modules.hud.ItemCounterHud;
import com.externalvisuals.modules.hud.ReachDisplayHud;
import com.externalvisuals.modules.hud.WatermarkHud;
import com.externalvisuals.modules.hud.SpeedHud;
import com.externalvisuals.modules.hud.ServerHud;
import com.externalvisuals.modules.hud.BiomeHud;
import com.externalvisuals.modules.hud.MemoryHud;
import com.externalvisuals.modules.hud.ArmorHud;
import com.externalvisuals.modules.hud.CompassHud;
import com.externalvisuals.modules.hud.EntityRadarHud;
import com.externalvisuals.modules.hud.TargetStatsHud;
import com.externalvisuals.modules.visual.CustomCrosshair;
import com.externalvisuals.modules.visual.ESP;
import com.externalvisuals.modules.visual.Fullbright;
import com.externalvisuals.modules.visual.Hitbox;
import com.externalvisuals.modules.visual.NoHurtCam;
import com.externalvisuals.modules.visual.TargetRing;
import com.externalvisuals.modules.visual.BlockOutline;
import com.externalvisuals.modules.visual.TrajectoryPrediction;
import com.externalvisuals.modules.visual.LowHpWarning;
import com.externalvisuals.modules.visual.CinematicOverlay;
import com.externalvisuals.modules.visual.PulseVisuals;
import com.externalvisuals.modules.world.TntTimer;
import com.externalvisuals.modules.world.WorldTime;
import com.externalvisuals.modules.world.WeatherControl;
import com.externalvisuals.modules.optimization.Optimization;
import com.externalvisuals.modules.cosmetics.Cosmetics;
import com.externalvisuals.modules.audio.Music;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleManager {

    private final List<Module> modules =
            new ArrayList<>();
    private final Set<Integer> heldKeys = new HashSet<>();

    public ModuleManager() {

        register(new CustomCrosshair());
        register(new Fullbright());
        register(new Hitbox());
        register(new ESP());
        register(new NoHurtCam());
        register(new TargetRing());
        register(new BlockOutline());
        register(new TrajectoryPrediction());
        register(new LowHpWarning());
        register(new CinematicOverlay());
        register(new PulseVisuals());

        register(new AimAssist());
        register(new AutoTotem());

        register(new Zoom());
        register(new ScreenStretch());
        register(new AutoSprint());
        register(new ElytraSwapper());

        register(new Hitmarker());
        register(new HitParticles());
        register(new HitEffects());
        register(new DamageNumbers());

        register(new TntTimer());
        register(new WorldTime());
        register(new WeatherControl());
        register(new Optimization());
        register(new Cosmetics());
        register(new Music());

        register(new FPSHud());
        register(new CoordinatesHud());
        register(new KeystrokesHud());
        register(new SessionHud());
        register(new TargetHud());
        register(new PingHud());
        register(new ClockHud());
        register(new CpsHud());
        register(new PotionHud());
        register(new ItemCounterHud());
        register(new ReachDisplayHud());
        register(new WatermarkHud());
        register(new SpeedHud());
        register(new ServerHud());
        register(new BiomeHud());
        register(new MemoryHud());
        register(new ArmorHud());
        register(new CompassHud());
        register(new EntityRadarHud());
        register(new TargetStatsHud());
    }

    private void register(
            Module module
    ) {

        modules.add(module);
    }

    public List<Module> getModules() {

        return Collections.unmodifiableList(
                modules
        );
    }

    public <T extends Module> T get(
            Class<T> clazz
    ) {

        for (Module module : modules) {

            if (clazz.isInstance(module)) {

                return clazz.cast(module);
            }
        }

        return null;
    }

    public void onTick() {
        handleKeybinds();

        for (Module module : modules) {
            if (module.isEnabled()) {
                try {
                    module.onTick();
                } catch (Throwable throwable) {
                    System.err.println(
                            "[ExternalVisuals] Module tick failed: "
                                    + module.getName()
                    );
                    throwable.printStackTrace();
                    module.setEnabled(false);
                }
            }
        }
    }

    private void handleKeybinds() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;

        Set<Integer> currentlyHeld = new HashSet<>();
        long handle = mc.getWindow().getHandle();

        for (Module module : modules) {
            int key = module.getKey();
            if (key <= GLFW.GLFW_KEY_UNKNOWN || key >= GLFW.GLFW_KEY_LAST) continue;
            if (GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS) {
                currentlyHeld.add(key);
                if (!heldKeys.contains(key)) {
                    module.toggle();
                }
            }
        }

        heldKeys.retainAll(currentlyHeld);
        heldKeys.addAll(currentlyHeld);
    }
}