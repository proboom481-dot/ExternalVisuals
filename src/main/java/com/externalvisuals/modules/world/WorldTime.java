package com.externalvisuals.modules.world;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;

/** Client-side visual time control. */
public final class WorldTime extends Module {
    private final SliderSetting time = addSetting(new SliderSetting("Time", 18000.0, 0.0, 24000.0, 100.0));
    private final BooleanSetting freeze = addSetting(new BooleanSetting("Freeze Time", true));
    private final SliderSetting speed = addSetting(new SliderSetting("Speed", 1.0, 0.0, 20.0, 0.5));

    public WorldTime() { super("World Time", ModuleCategory.WORLD, 0); }

    @Override public void onTick() {
        if (mc.world == null) return;
        if (freeze.isEnabled()) {
            mc.world.setTimeOfDay(time.getValue().longValue());
        } else if (speed.getValue() > 0.0) {
            long current = mc.world.getTimeOfDay();
            mc.world.setTimeOfDay(current + Math.max(1L, speed.getValue().longValue()));
        }
    }
}
