package com.externalvisuals.modules.hud;

import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.ArrayList;
import java.util.List;

public final class PotionHud extends HudModule {

    public PotionHud() {
        super("Potion Effects", 0, 6.0f, 196.0f);
    }

    public String[] getLines() {
        if (mc.player == null) {
            return new String[0];
        }

        List<String> lines = new ArrayList<>();

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            if (effect == null || effect.getEffectType() == null) {
                continue;
            }

            String name =
                    effect.getEffectType()
                            .getName()
                            .getString();

            int amplifier =
                    effect.getAmplifier() + 1;

            int ticks =
                    Math.max(
                            0,
                            effect.getDuration()
                    );

            int seconds = ticks / 20;

            lines.add(
                    name
                            + " "
                            + amplifier
                            + "  "
                            + formatTime(seconds)
            );
        }

        return lines.toArray(new String[0]);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int rest = seconds % 60;

        return String.format(
                "%d:%02d",
                minutes,
                rest
        );
    }
}
