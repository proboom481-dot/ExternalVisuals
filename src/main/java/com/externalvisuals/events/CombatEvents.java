package com.externalvisuals.events;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
import com.externalvisuals.modules.hud.CpsHud;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public final class CombatEvents {

    private CombatEvents() {
    }

    public static void init() {

        AttackEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult) -> {

                    if (!world.isClient
                            || ExternalVisuals.MODULE_MANAGER == null
                            || player == null) {
                        return ActionResult.PASS;
                    }

                    CpsHud cps =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    CpsHud.class
                            );

                    if (cps != null && cps.isEnabled()) {
                        cps.recordClick();
                    }

                    /*
                     * Do not trigger hit visuals here.
                     * An attack callback means the player attempted an attack,
                     * not that damage was actually dealt.
                     */
                    return ActionResult.PASS;
                }
        );
    }

    public static void onConfirmedDamage(
            LivingEntity target,
            float damage
    ) {

        if (target == null
                || damage <= 0.001f
                || ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        Hitmarker hitmarker =
                ExternalVisuals.MODULE_MANAGER.get(
                        Hitmarker.class
                );

        if (hitmarker != null && hitmarker.isEnabled()) {
            hitmarker.trigger();
        }

        HitParticles particles =
                ExternalVisuals.MODULE_MANAGER.get(
                        HitParticles.class
                );

        if (particles != null && particles.isEnabled()) {
            particles.spawn(target);
        }

        HitEffects effects =
                ExternalVisuals.MODULE_MANAGER.get(
                        HitEffects.class
                );

        if (effects != null && effects.isEnabled()) {
            effects.trigger(target);
        }
    }
}
