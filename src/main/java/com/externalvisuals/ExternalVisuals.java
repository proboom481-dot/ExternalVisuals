package com.externalvisuals;

import com.externalvisuals.events.CombatEvents;
import com.externalvisuals.config.ConfigManager;
import com.externalvisuals.gui.ClickGuiScreen;
import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.render.DamageNumbersRenderer;
import com.externalvisuals.render.ESPWorldRenderer;
import com.externalvisuals.render.ExtraHudRenderer;
import com.externalvisuals.render.HitboxRenderer;
import com.externalvisuals.render.HitmarkerRenderer;
import com.externalvisuals.render.TntTimerRenderer;
import com.externalvisuals.render.VisualHud;
import com.externalvisuals.modules.visual.TargetRing;
import com.externalvisuals.modules.visual.BlockOutline;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ExternalVisuals
        implements ClientModInitializer {

    public static final String NAME =
            "ExternalVisuals";

    public static final String VERSION =
            "1.4.4";

    public static ModuleManager MODULE_MANAGER;

    private static KeyBinding clickGuiKey;

    @Override
    public void onInitializeClient() {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "          ExternalVisuals"
        );

        System.out.println(
                "          Version " + VERSION
        );

        System.out.println(
                "          Minecraft 1.16.5"
        );

        System.out.println(
                "======================================"
        );

        MODULE_MANAGER =
                new ModuleManager();

        ConfigManager.init(MODULE_MANAGER);

        ClientLifecycleEvents.CLIENT_STOPPING.register(
                client -> ConfigManager.save()
        );

        /*
         * HUD / visual systems.
         */
        VisualHud.init();

        ExtraHudRenderer.init();

        DamageNumbersRenderer.init();

        HitmarkerRenderer.init();

        TntTimerRenderer.init();

        HitboxRenderer.init();

        /*
         * ESP renderer.
         */
        ESPWorldRenderer.init();
        TargetRing.init();
        BlockOutline.init();

        /*
         * Combat events.
         */
        CombatEvents.init();

        /*
         * ClickGUI key.
         */
        clickGuiKey =
                KeyBindingHelper.registerKeyBinding(
                        new KeyBinding(
                                "key.externalvisuals.clickgui",
                                GLFW.GLFW_KEY_RIGHT_SHIFT,
                                "category.externalvisuals"
                        )
                );

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {

                    while (
                            clickGuiKey.wasPressed()
                    ) {

                        if (
                                client.currentScreen
                                        == null
                        ) {

                            client.openScreen(
                                    new ClickGuiScreen()
                            );
                        }
                    }

                    if (
                            MODULE_MANAGER != null
                    ) {

                        MODULE_MANAGER.onTick();
                    }
                }
        );

        System.out.println(
                "ExternalVisuals successfully loaded!"
        );

        System.out.println(
                "Press RIGHT SHIFT to open ClickGUI."
        );
    }
}