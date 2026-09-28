package com.externalvisuals.modules.hud;

import net.minecraft.entity.Entity;

public final class ReachDisplayHud extends HudModule {

    public ReachDisplayHud() {
        super("Reach Display", 0, 0.0f, 84.0f);
    }

    public String getText() {
        if (mc.player == null) {
            return "Reach: --";
        }

        Entity target = mc.targetedEntity;

        if (target == null) {
            return "Reach: --";
        }

        return String.format(
                "Reach: %.2f",
                mc.player.distanceTo(target)
        );
    }
}
