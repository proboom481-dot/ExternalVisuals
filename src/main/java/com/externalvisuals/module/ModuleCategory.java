package com.externalvisuals.module;

public enum ModuleCategory {

    COMBAT("Combat"),
    VISUALS("Visuals"),
    HUD("HUD"),
    PLAYER("Player"),
    WORLD("World"),
    COSMETICS("Cosmetics"),
    CAMERA("Camera");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}