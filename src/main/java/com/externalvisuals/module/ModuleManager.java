package com.externalvisuals.module;

import com.externalvisuals.modules.camera.Zoom;
import com.externalvisuals.modules.combat.AimAssist;
import com.externalvisuals.modules.combat.DamageNumbers;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.hud.FPSHud;
import com.externalvisuals.modules.visual.CustomCrosshair;
import com.externalvisuals.modules.visual.ESP;
import com.externalvisuals.modules.visual.Fullbright;
import com.externalvisuals.modules.visual.Hitbox;
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

        register(new AimAssist());

        register(new Zoom());

        register(new Hitmarker());
        register(new HitParticles());
        register(new HitEffects());
        register(new DamageNumbers());

        register(new TntTimer());

        register(new FPSHud());
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

                module.onTick();
            }
        }
    }
}