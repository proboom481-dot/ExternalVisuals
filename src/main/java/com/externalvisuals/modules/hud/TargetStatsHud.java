package com.externalvisuals.modules.hud;

import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.entity.LivingEntity;

public final class TargetStatsHud extends HudModule {
    private final BooleanSetting health = addSetting(new BooleanSetting("Health", true));
    private final BooleanSetting distance = addSetting(new BooleanSetting("Distance", true));
    private final BooleanSetting armor = addSetting(new BooleanSetting("Armor", true));
    private final SliderSetting range = addSetting(new SliderSetting("Range", 32.0, 4.0, 128.0, 1.0));

    public TargetStatsHud() { super("Target Stats HUD", 0, 6, 180); }

    public String getText() {
        if (mc.targetedEntity == null || !(mc.targetedEntity instanceof LivingEntity)) return "Target: none";
        LivingEntity e = (LivingEntity) mc.targetedEntity;
        if (mc.player == null || mc.player.distanceTo(e) > range.getValue()) return "Target: none";
        StringBuilder s = new StringBuilder("Target: ").append(e.getDisplayName().getString());
        if (health.isEnabled()) s.append("  ").append(Math.round(e.getHealth())).append("HP");
        if (distance.isEnabled()) s.append("  ").append(Math.round(mc.player.distanceTo(e))).append("m");
        if (armor.isEnabled()) {
            int count = 0;
            for (net.minecraft.item.ItemStack stack : e.getArmorItems()) if (!stack.isEmpty()) count++;
            s.append("  ").append(count).append("A");
        }
        return s.toString();
    }
}
