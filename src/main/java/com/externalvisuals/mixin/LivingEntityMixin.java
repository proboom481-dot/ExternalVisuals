package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.events.CombatEvents;
import com.externalvisuals.modules.combat.DamageNumbers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side confirmed damage hook.
 *
 * The old implementation compared health once per tick. That could miss
 * short-lived health changes and made the damage visual dependent on the
 * update order of tracked entity data. Hooking LivingEntity.damage directly
 * gives the visual system the exact moment when vanilla accepted damage.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    private float externalVisuals$healthBeforeDamage;
    private boolean externalVisuals$trackingDamage;

    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    private void externalVisuals$captureDamageStart(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> callback
    ) {
        LivingEntity entity =
                (LivingEntity) (Object) this;

        externalVisuals$healthBeforeDamage =
                entity.getHealth();

        externalVisuals$trackingDamage =
                amount > 0.0f;
    }

    @Inject(
            method = "damage",
            at = @At("RETURN")
    )
    private void externalVisuals$handleConfirmedDamage(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!externalVisuals$trackingDamage
                || !Boolean.TRUE.equals(callback.getReturnValue())) {
            externalVisuals$trackingDamage = false;
            return;
        }

        externalVisuals$trackingDamage = false;

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc == null
                || mc.world == null
                || mc.player == null
                || ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        LivingEntity entity =
                (LivingEntity) (Object) this;

        if (entity == mc.player) {
            try {
                CombatEvents.onPlayerDamage(source, amount);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] Player-damage visual failed.");
            }
            return;
        }

        float currentHealth =
                entity.getHealth();

        float damage =
                externalVisuals$healthBeforeDamage
                        - currentHealth;

        if (damage <= 0.001f) {
            return;
        }

        if (mc.player.squaredDistanceTo(entity)
                > 128.0 * 128.0) {
            return;
        }

        DamageNumbers damageNumbers =
                ExternalVisuals.MODULE_MANAGER.get(
                        DamageNumbers.class
                );

        if (damageNumbers != null
                && damageNumbers.isEnabled()) {
            damageNumbers.show(
                    entity,
                    damage
            );
        }

        try {
            CombatEvents.onConfirmedDamage(
                    entity,
                    damage
            );
        } catch (Throwable throwable) {
            System.err.println(
                    "[ExternalVisuals] Confirmed-damage visual failed."
            );
            throwable.printStackTrace();
        }
    }
}
