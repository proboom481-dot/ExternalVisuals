package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
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

    private int externalVisuals$attackWindow = 0;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void externalVisuals$trackHealth(
            CallbackInfo callback
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc == null) {
            return;
        }

        if (mc.world == null) {
            return;
        }

        if (mc.player == null) {
            return;
        }

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        LivingEntity entity =
                (LivingEntity) (Object) this;

        /*
         * Не показываем урон на самом игроке.
         */
        if (entity == mc.player) {
            externalVisuals$lastHealth =
                    entity.getHealth();

            return;
        }

        float currentHealth =
                entity.getHealth();

        /*
         * Первый тик только запоминает здоровье.
         */
        if (externalVisuals$lastHealth < 0.0f) {

            externalVisuals$lastHealth =
                    currentHealth;

            return;
        }

        /*
         * Определяем реальное уменьшение
         * здоровья на клиенте.
         */
        float damage =
                externalVisuals$lastHealth
                        - currentHealth;

        /*
         * Обновляем значение ДО выхода,
         * чтобы следующий тик сравнивался
         * с актуальным здоровьем.
         */
        externalVisuals$lastHealth =
                currentHealth;

        if (damage <= 0.001f) {
            return;
        }

        /*
         * Проверяем, что сущность находится
         * достаточно близко к игроку.
         */
        double distance =
                mc.player.squaredDistanceTo(entity);

        if (distance > 64.0 * 64.0) {
            return;
        }

        DamageNumbers damageNumbers =
                ExternalVisuals.MODULE_MANAGER.get(
                        DamageNumbers.class
                );

        if (damageNumbers == null) {
            return;
        }

        if (!damageNumbers.isEnabled()) {
            return;
        }

        if (!damageNumbers.wasRecentlyAttacked(entity)
                && mc.targetedEntity != entity) {
            return;
        }

        damageNumbers.show(
                entity,
                damage
        );
    }
}