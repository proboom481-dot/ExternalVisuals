package com.externalvisuals;

import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.ui.ExternalMenu;
import com.externalvisuals.render.VisualRenderer;
import com.externalvisuals.config.ExternalConfig;
import com.externalvisuals.render.HudVisuals;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ExternalVisuals implements ClientModInitializer {
    public static final String MOD_ID = "externalvisuals";
    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        MODULES.registerDefaults();
        ExternalConfig.load();
        VisualRenderer.register();
        Runtime.getRuntime().addShutdownHook(new Thread(ExternalConfig::save));
        HudVisuals.register();
        AttackVisualHook.register();
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.externalvisuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "ExternalVisuals"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (menuKey.wasPressed()) {
                client.openScreen(new ExternalMenu(null));
            }
        });
    }

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
