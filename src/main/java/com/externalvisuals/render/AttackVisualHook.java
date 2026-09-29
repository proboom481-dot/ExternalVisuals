package com.externalvisuals.render;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public final class AttackVisualHook {
    private static int previousCooldown;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(AttackVisualHook::tick);
    }

    private static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        int cooldown = mc.player.getAttackCooldownProgressPerTick();
        if (previousCooldown > cooldown && cooldown == 0 && mc.targetedEntity != null) {
            Entity target = mc.targetedEntity;
            VisualState.hit(target, 1.0f, mc.player.fallDistance > 0.0f && !mc.player.isOnGround());
        }
        previousCooldown = cooldown;
    }
}
