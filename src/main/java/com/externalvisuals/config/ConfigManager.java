package com.externalvisuals.config;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.KeybindSetting;
import com.externalvisuals.setting.Setting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Persistent client-side configuration and simple named profiles.
 */
public final class ConfigManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final String DEFAULT_PROFILE = "default";

    private static File directory;
    private static ModuleManager moduleManager;
    private static boolean pendingEnabledState;

    private ConfigManager() {
    }

    public static void init(ModuleManager manager) {
        moduleManager = manager;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.runDirectory == null) {
            return;
        }

        directory = new File(mc.runDirectory, "config/externalvisuals");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Startup is intentionally fail-safe: do not auto-enable modules
        // before the first world is available. Settings are still loaded.
        pendingEnabledState = true;
        load(DEFAULT_PROFILE, false);
    }

    /** Restores enabled states once the first playable world is available. */
    public static void applyPendingEnabledState() {
        if (!pendingEnabledState || moduleManager == null || directory == null) {
            return;
        }
        pendingEnabledState = false;
        load(DEFAULT_PROFILE, true);
    }

    public static void save() {
        save(DEFAULT_PROFILE);
    }

    public static void save(String profile) {
        if (moduleManager == null || directory == null) {
            return;
        }

        if (profile == null || profile.trim().isEmpty()) {
            profile = DEFAULT_PROFILE;
        }

        JsonObject root = new JsonObject();
        root.addProperty("version", 1);

        JsonObject modules = new JsonObject();

        for (Module module : moduleManager.getModules()) {
            if (module == null) {
                continue;
            }

            JsonObject data = new JsonObject();
            data.addProperty("enabled", module.isEnabled());
            data.addProperty("key", module.getKey());

            JsonObject settings = new JsonObject();

            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof BooleanSetting) {
                    settings.addProperty(
                            setting.getName(),
                            ((BooleanSetting) setting).isEnabled()
                    );
                } else if (setting instanceof SliderSetting) {
                    settings.addProperty(
                            setting.getName(),
                            ((SliderSetting) setting).getValue()
                    );
                } else if (setting instanceof ColorSetting) {
                    ColorSetting colorSetting =
                            (ColorSetting) setting;

                    settings.addProperty(
                            setting.getName(),
                            colorSetting.getBaseColor()
                    );

                    settings.addProperty(
                            setting.getName() + " Rainbow",
                            colorSetting.isRainbow()
                    );
                } else if (setting instanceof KeybindSetting) {
                    settings.addProperty(
                            setting.getName(),
                            ((KeybindSetting) setting).getKey()
                    );
                } else if (setting instanceof StringSetting) {
                    settings.addProperty(
                            setting.getName(),
                            ((StringSetting) setting).getValue()
                    );
                }
            }

            data.add("settings", settings);
            modules.add(module.getName(), data);
        }

        root.add("modules", modules);

        File file = profileFile(profile);
        File temp = new File(file.getParentFile(), file.getName() + ".tmp");

        try (FileWriter writer = new FileWriter(temp)) {
            GSON.toJson(root, writer);
        } catch (IOException ignored) {
            return;
        }

        if (!temp.renameTo(file)) {
            try {
                if (file.exists() && !file.delete()) {
                    return;
                }
                temp.renameTo(file);
            } catch (RuntimeException ignored) {
            }
        }
    }

    public static void load() {
        load(DEFAULT_PROFILE, true);
    }

    public static void load(String profile) {
        load(profile, true);
    }

    /**
     * Loads a profile. When applyEnabled is false, module enabled states are
     * deliberately not restored. This is used during client startup so a
     * stale profile cannot enable a render module before a world is ready.
     */
    public static void load(String profile, boolean applyEnabled) {
        if (moduleManager == null || directory == null) {
            return;
        }

        if (profile == null || profile.trim().isEmpty()) {
            profile = DEFAULT_PROFILE;
        }

        File file = profileFile(profile);
        if (!file.isFile()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonElement parsed = new JsonParser().parse(reader);
            if (!parsed.isJsonObject()) {
                return;
            }

            JsonObject root = parsed.getAsJsonObject();
            JsonObject modules = getObject(root, "modules");
            if (modules == null) {
                return;
            }

            for (Module module : moduleManager.getModules()) {
                if (module == null || !modules.has(module.getName())) {
                    continue;
                }

                JsonElement moduleElement = modules.get(module.getName());
                if (!moduleElement.isJsonObject()) {
                    continue;
                }

                JsonObject data = moduleElement.getAsJsonObject();

                if (data.has("key")) {
                    try {
                        module.setKey(data.get("key").getAsInt());
                    } catch (RuntimeException ignored) {
                    }
                }

                if (applyEnabled && data.has("enabled")) {
                    try {
                        module.setEnabled(data.get("enabled").getAsBoolean());
                    } catch (RuntimeException ignored) {
                    }
                }

                JsonObject settings = getObject(data, "settings");
                if (settings == null) {
                    continue;
                }

                for (Setting<?> setting : module.getSettings()) {
                    if (setting == null || !settings.has(setting.getName())) {
                        continue;
                    }

                    JsonElement value = settings.get(setting.getName());

                    try {
                        if (setting instanceof BooleanSetting) {
                            ((BooleanSetting) setting).setEnabled(value.getAsBoolean());
                        } else if (setting instanceof SliderSetting) {
                            ((SliderSetting) setting).setSliderValue(value.getAsDouble());
                        } else if (setting instanceof ColorSetting) {
                            ((ColorSetting) setting).setColor(value.getAsInt());

                            JsonElement rainbowValue =
                                    settings.get(
                                            setting.getName() + " Rainbow"
                                    );

                            if (rainbowValue != null) {
                                ((ColorSetting) setting).setRainbow(
                                        rainbowValue.getAsBoolean()
                                );
                            }
                        } else if (setting instanceof KeybindSetting) {
                            ((KeybindSetting) setting).setKey(value.getAsInt());
                        } else if (setting instanceof StringSetting) {
                            ((StringSetting) setting).setValue(value.getAsString());
                        }
                    } catch (RuntimeException ignored) {
                        // Ignore one malformed setting and continue loading the rest.
                    }
                }
            }
        } catch (Exception ignored) {
            // Corrupt config must never prevent the client from starting.
        }
    }

    private static JsonObject getObject(JsonObject object, String name) {
        if (object == null || !object.has(name)) {
            return null;
        }
        JsonElement element = object.get(name);
        return element != null && element.isJsonObject()
                ? element.getAsJsonObject()
                : null;
    }

    private static File profileFile(String profile) {
        String safe = profile.replaceAll("[^a-zA-Z0-9._-]", "_");
        return new File(directory, safe + ".json");
    }
}
