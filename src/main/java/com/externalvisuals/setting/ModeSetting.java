package com.externalvisuals.setting;
public class ModeSetting extends Setting<String> {
    private final String[] modes;
    public ModeSetting(String name, String value, String... modes) {
        super(name, value); this.modes=modes;
    }
    public void next() {
        for (int i=0;i<modes.length;i++) if (modes[i].equals(value)) {
            value=modes[(i+1)%modes.length]; return;
        }
    }
}
