package com.externalvisuals.module;

import com.externalvisuals.setting.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void registerDefaults() {
        if (!modules.isEmpty()) return;
        addVisual("Hit Particles", "Soft", "Sharp", "Classic");
        addVisual("Damage Numbers", "Classic", "Compact", "Bold");
        addVisual("Target HUD", "Classic", "Minimal", "Compact");
        addVisual("Critical Effects", "Purple", "White", "Pulse");
        addVisual("Hitmarkers", "Cross", "Dot", "Ring");
        addVisual("Low HP Warning", "Vignette", "Pulse", "Border");
        addVisual("Custom Crosshair", "Dot", "Cross", "Gap");
        addVisual("Kill Effects", "Pulse", "Burst", "Ring");
        addVisual("Armor HUD", "Horizontal", "Vertical", "Compact");
        addVisual("Potion HUD", "Horizontal", "Vertical", "Compact");
        addVisual("Trajectory", "Line", "Dots", "Arrow");
    }

    private void addVisual(String name, String... modes) {
        Module m = new Module(name, "Visuals");
        m.addSetting(new BooleanSetting("Enabled", false));
        m.addSetting(new ModeSetting("Style", modes[0], modes));
        m.addSetting(new SliderSetting("Size", 1.0, 0.5, 3.0, 0.1));
        m.addSetting(new SliderSetting("Opacity", 0.85, 0.1, 1.0, 0.05));
        modules.add(m);
    }

    public List<Module> getModules(){ return Collections.unmodifiableList(modules); }
}
