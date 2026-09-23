package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.events.CombatEvents;
import com.externalvisuals.modules.combat.DamageNumbers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    private float externalVisuals$lastHealth = -1.0f;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void externalVisuals$trackHealth(
            CallbackInfo callback
    ) {
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
            externalVisuals$lastHealth =
                    entity.getHealth();
            return;
        }

        float currentHealth =
                entity.getHealth();

        if (externalVisuals$lastHealth < 0.0f) {
            externalVisuals$lastHealth =
                    currentHealth;
            return;
        }

        float previousHealth =
                externalVisuals$lastHealth;

        externalVisuals$lastHealth =
                currentHealth;

        float damage =
                previousHealth - currentHealth;

        if (damage <= 0.001f) {
            return;
        }

        if (mc.player.squaredDistanceTo(entity)
                > 64.0 * 64.0) {
            return;
        }

        /*
         * Damage Numbers are fed by observed health loss rather than
         * attack prediction. This also works for zombies, mobs and players.
         */
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

        /*
         * Hitmarker / particles / hit effects are also driven by confirmed
         * damage, so a miss no longer triggers a hit effect.
         */
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
