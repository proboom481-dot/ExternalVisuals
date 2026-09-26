package com.externalvisuals.modules.world;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;

/** Client-side weather intensity control; never sends weather packets. */
public final class WeatherControl extends Module {
    private final BooleanSetting clear = addSetting(new BooleanSetting("Clear Weather", true));
    private final SliderSetting rain = addSetting(new SliderSetting("Rain", 0.0, 0.0, 1.0, 0.05));
    private final SliderSetting thunder = addSetting(new SliderSetting("Thunder", 0.0, 0.0, 1.0, 0.05));
    private final BooleanSetting smooth = addSetting(new BooleanSetting("Smooth", true));

    public WeatherControl() { super("Weather Control", ModuleCategory.WORLD, 0); }

    @Override public void onTick() {
        if (mc.world == null) return;
        float r = clear.isEnabled() ? 0.0f : rain.getValue().floatValue();
        float t = clear.isEnabled() ? 0.0f : thunder.getValue().floatValue();
        if (!smooth.isEnabled()) { r = Math.round(r); t = Math.round(t); }
        mc.world.setRainGradient(r);
        mc.world.setThunderGradient(t);
    }
}
