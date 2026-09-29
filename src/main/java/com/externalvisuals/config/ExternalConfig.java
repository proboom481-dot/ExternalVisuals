package com.externalvisuals.config;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.Module;
import com.externalvisuals.setting.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class ExternalConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ExternalConfig() {}

    private static File file() {
        return new File(MinecraftClient.getInstance().runDirectory, "externalvisuals.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ExternalVisuals.MODULES.getModules()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                for (Setting<?> s : m.getSettings()) {
                    if (s instanceof BooleanSetting) o.addProperty(s.getName(), (Boolean)s.getValue());
                    else if (s instanceof ModeSetting) o.addProperty(s.getName(), (String)s.getValue());
                    else if (s instanceof SliderSetting) o.addProperty(s.getName(), (Double)s.getValue());
                }
                root.add(m.getName(), o);
            }
            FileWriter w = new FileWriter(file());
            GSON.toJson(root, w);
            w.close();
        } catch (Exception ignored) {}
    }

    public static void load() {
        try {
            File f=file();
            if(!f.exists()) return;
            JsonObject root=GSON.fromJson(new FileReader(f), JsonObject.class);
            for(Module m:ExternalVisuals.MODULES.getModules()){
                JsonObject o=root.getAsJsonObject(m.getName());
                if(o==null) continue;
                if(o.has("enabled") && o.get("enabled").getAsBoolean()!=m.isEnabled()) m.toggle();
                for(Setting<?> s:m.getSettings()){
                    if(!o.has(s.getName())) continue;
                    if(s instanceof BooleanSetting) s.setValue(o.get(s.getName()).getAsBoolean());
                    else if(s instanceof ModeSetting) s.setValue(o.get(s.getName()).getAsString());
                    else if(s instanceof SliderSetting) s.setValue(o.get(s.getName()).getAsDouble());
                }
            }
        } catch (Exception ignored) {}
    }
}
