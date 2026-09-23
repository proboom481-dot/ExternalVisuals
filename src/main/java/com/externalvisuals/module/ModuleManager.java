package com.externalvisuals.module;

import com.externalvisuals.modules.camera.Zoom;
import com.externalvisuals.modules.player.AutoSprint;
import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.modules.combat.DamageNumbers;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
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
import com.externalvisuals.modules.visual.CustomCrosshair;
import com.externalvisuals.modules.visual.ESP;
import com.externalvisuals.modules.visual.Fullbright;
import com.externalvisuals.modules.visual.Hitbox;
import com.externalvisuals.modules.visual.NoHurtCam;
import com.externalvisuals.modules.visual.TargetRing;
import com.externalvisuals.modules.visual.BlockOutline;
import com.externalvisuals.modules.world.TntTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules =
            new ArrayList<>();

    public ModuleManager() {

        register(new CustomCrosshair());
        register(new Fullbright());
        register(new Hitbox());
        register(new ESP());
        register(new NoHurtCam());
        register(new TargetRing());
        register(new BlockOutline());

        register(new AimAssist());

        register(new Zoom());
        register(new AutoSprint());

        register(new Hitmarker());
        register(new HitParticles());
        register(new HitEffects());
        register(new DamageNumbers());

        register(new TntTimer());

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

        for (Module module :
                modules) {

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
}