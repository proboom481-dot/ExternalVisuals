package com.externalvisuals.events;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.DamageNumbers;
import com.externalvisuals.modules.combat.HitEffects;
import com.externalvisuals.modules.combat.HitParticles;
import com.externalvisuals.modules.combat.Hitmarker;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public final class CombatEvents {

    private CombatEvents() {
    }

    public static void init() {

        AttackEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult) -> {

                    if (!world.isClient) {
                        return ActionResult.PASS;
                    }

                    if (ExternalVisuals.MODULE_MANAGER == null) {
                        return ActionResult.PASS;
                    }

                    if (player == null) {
                        return ActionResult.PASS;
                    }

                    if (entity == null) {
                        return ActionResult.PASS;
                    }

                    if (entity == player) {
                        return ActionResult.PASS;
                    }

                    DamageNumbers damageNumbers =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    DamageNumbers.class
                            );

                    if (damageNumbers != null
                            && damageNumbers.isEnabled()) {
                        damageNumbers.markAttacked(entity);
                    }

                    /*
                     * Hit Particles
                     */
                    HitParticles particles =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    HitParticles.class
                            );

                    if (particles != null
                            && particles.isEnabled()) {

                        particles.spawn(entity);
                    }

                    /*
                     * Hit Effects
                     */
                    HitEffects effects =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    HitEffects.class
                            );

                    if (effects != null
                            && effects.isEnabled()) {

                        effects.trigger(entity);
                    }

                    /*
                     * Hitmarker
                     *
                     * Только для LivingEntity.
                     */
                    if (entity instanceof LivingEntity) {

                        Hitmarker hitmarker =
                                ExternalVisuals.MODULE_MANAGER.get(
                                        Hitmarker.class
                                );

                        if (hitmarker != null
                                && hitmarker.isEnabled()) {

                            hitmarker.trigger();
                        }
                    }

                    /*
                     * Damage Numbers здесь НЕ запускаем.
                     *
                     * Они теперь получают фактический урон
                     * через LivingEntityMixin.
                     */

                    return ActionResult.PASS;
                }
        );
    }

    /**
     * Единая точка для подтверждённого урона.
     *
     * В дальнейшем сюда можно подключать
     * дополнительные боевые визуалы.
     */
    public static void onConfirmedDamage(
            LivingEntity target,
            float damage
    ) {

        if (target == null) {
            return;
        }

        if (damage <= 0.001f) {
            return;
        }

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        Hitmarker hitmarker =
                ExternalVisuals.MODULE_MANAGER.get(
                        Hitmarker.class
                );

        if (hitmarker != null
                && hitmarker.isEnabled()) {

            hitmarker.trigger();
        }

        HitParticles particles =
                ExternalVisuals.MODULE_MANAGER.get(
                        HitParticles.class
                );

        if (particles != null
                && particles.isEnabled()) {

            particles.spawn(target);
        }

        HitEffects effects =
                ExternalVisuals.MODULE_MANAGER.get(
                        HitEffects.class
                );

        if (effects != null
                && effects.isEnabled()) {

            effects.trigger(target);
        }
    }
}