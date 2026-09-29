package com.externalvisuals;

import com.externalvisuals.gui.ExternalVisualsScreen;
import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.render.VisualEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ExternalVisuals implements ClientModInitializer {
    public static final String NAME = "ExternalVisuals";
    public static final String VERSION = "1.1.0-EXTERNAL-UI";
    public static ModuleManager MODULES;
    private static KeyBinding menuKey;

    @Override public void onInitializeClient() {
        MODULES = new ModuleManager();
        VisualEngine.init(MODULES);
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.externalvisuals.menu", GLFW.GLFW_KEY_RIGHT_SHIFT, "category.externalvisuals"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed() && client.currentScreen == null) client.openScreen(new ExternalVisualsScreen());
            MODULES.tick(); VisualEngine.tick(client);
        });
        System.out.println("[ExternalVisuals] Loaded clean Pulse-style visual engine for 1.16.5");
    }
}
