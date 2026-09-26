package com.externalvisuals.events;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.hud.CpsHud;
import com.externalvisuals.modules.visual.PulseVisuals;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public final class CombatEvents {
    private CombatEvents() {}

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient || ExternalVisuals.MODULE_MANAGER == null || player == null) {
                return ActionResult.PASS;
            }
            CpsHud cps = ExternalVisuals.MODULE_MANAGER.get(CpsHud.class);
            if (cps != null && cps.isEnabled()) cps.recordClick();
            return ActionResult.PASS;
        });
    }

    public static void onConfirmedDamage(LivingEntity target, float damage) {
        if (target == null || damage <= 0.001f || ExternalVisuals.MODULE_MANAGER == null) return;
        PulseVisuals pulse = ExternalVisuals.MODULE_MANAGER.get(PulseVisuals.class);
        if (pulse != null && pulse.isEnabled()) pulse.onConfirmedDamage(target, damage);
        Hitmarker hitmarker = ExternalVisuals.MODULE_MANAGER.get(Hitmarker.class);
        if (hitmarker != null && hitmarker.isEnabled()) hitmarker.trigger();
        HitParticles particles = ExternalVisuals.MODULE_MANAGER.get(HitParticles.class);
        if (particles != null && particles.isEnabled()) particles.spawn(target);
        HitEffects effects = ExternalVisuals.MODULE_MANAGER.get(HitEffects.class);
        if (effects != null && effects.isEnabled()) effects.trigger(target);
    }

    public static void onPlayerDamage(net.minecraft.entity.damage.DamageSource source, float damage) {
        if (ExternalVisuals.MODULE_MANAGER == null || damage <= 0.0f) return;
        PulseVisuals pulse = ExternalVisuals.MODULE_MANAGER.get(PulseVisuals.class);
        if (pulse != null && pulse.isEnabled()) pulse.onPlayerDamage(source, damage);
    }
}
