package com.externalvisuals.module;

import com.externalvisuals.setting.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {
    private final String name;
    private final String category;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String category) { this.name=name; this.category=category; }

    public String getName(){ return name; }
    public String getCategory(){ return category; }
    public boolean isEnabled(){ return enabled; }
    public void toggle(){ enabled=!enabled; }
    public void addSetting(Setting<?> s){ settings.add(s); }
    public List<Setting<?>> getSettings(){ return Collections.unmodifiableList(settings); }
}
