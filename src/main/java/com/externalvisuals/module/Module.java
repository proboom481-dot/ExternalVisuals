package com.externalvisuals.module;

import com.externalvisuals.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {

    protected final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final String name;
    private final ModuleCategory category;

    private boolean enabled;
    private int key;

    private final List<Setting<?>> settings =
            new ArrayList<>();

    public Module(
            String name,
            ModuleCategory category,
            int key
    ) {
        this.name = name;
        this.category = category;
        this.key = key;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {

        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;

        try {
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        } catch (Throwable throwable) {
            // A single broken module must never leave the manager in a
            // half-enabled state or crash the client.
            this.enabled = false;
            try {
                onDisable();
            } catch (Throwable ignored) {
            }
            System.err.println("[ExternalVisuals] Module state change failed: " + name);
            throwable.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    protected <T extends Setting<?>> T addSetting(T setting) {

        settings.add(setting);

        return setting;
    }

    public List<Setting<?>> getSettings() {

        return Collections.unmodifiableList(
                settings
        );
    }

    public <T extends Setting<?>> T getSetting(
            Class<T> type
    ) {

        for (Setting<?> setting : settings) {

            if (type.isInstance(setting)) {
                return type.cast(setting);
            }
        }

        return null;
    }

    public Setting<?> getSetting(
            String settingName
    ) {

        for (Setting<?> setting : settings) {

            if (setting.getName()
                    .equalsIgnoreCase(settingName)) {

                return setting;
            }
        }

        return null;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }
}