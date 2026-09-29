package com.externalvisuals.setting;
public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean value) { super(name, value); }
    public void toggle() { value = !value; }
}
