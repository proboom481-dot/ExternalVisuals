package com.externalvisuals.modules.hud;

import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class EntityRadarHud extends HudModule {
    private final SliderSetting range = addSetting(new SliderSetting("Range", 48.0, 8.0, 128.0, 1.0));
    private final BooleanSetting players = addSetting(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = addSetting(new BooleanSetting("Mobs", true));
    private final BooleanSetting compact = addSetting(new BooleanSetting("Compact", true));

    public EntityRadarHud() { super("Entity Radar HUD", 0, 6, 150); }

    public String getText() {
        if (mc.player == null || mc.world == null) return "Radar";
        int p = 0, m = 0;
        double max = range.getValue() * range.getValue();
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || e == null || mc.player.squaredDistanceTo(e) > max) continue;
            if (e instanceof net.minecraft.entity.player.PlayerEntity && players.isEnabled()) p++;
            else if (e instanceof LivingEntity && mobs.isEnabled()) m++;
        }
        return compact.isEnabled() ? "Radar  P:" + p + "  M:" + m : "Nearby entities  Players: " + p + "  Mobs: " + m;
    }
}
