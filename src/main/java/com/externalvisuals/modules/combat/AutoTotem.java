package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import com.externalvisuals.util.InventoryUtils;
import net.minecraft.item.Items;

/** Safe, server-authoritative offhand totem automation. */
public final class AutoTotem extends Module {
    private final SliderSetting minTotems = addSetting(new SliderSetting("Min Totems", 1, 1, 64, 1));
    private final BooleanSetting hotbarOnly = addSetting(new BooleanSetting("Hotbar Only", true));
    private final BooleanSetting replaceEmpty = addSetting(new BooleanSetting("Replace Empty", true));
    private final BooleanSetting replaceNonTotem = addSetting(new BooleanSetting("Replace Non-Totem", true));
    private final BooleanSetting keepOne = addSetting(new BooleanSetting("Keep One In Hotbar", true));
    private final BooleanSetting whileAlive = addSetting(new BooleanSetting("Only While Alive", true));
    private final BooleanSetting onlyWhenNotScreen = addSetting(new BooleanSetting("Only Without Screen", true));
    private final SliderSetting delay = addSetting(new SliderSetting("Swap Delay", 2, 0, 10, 1));
    private final StringSetting priority = addSetting(new StringSetting("Priority", "TOTEM", "TOTEM", "GAP", "PEARL"));
    private final BooleanSetting notify = addSetting(new BooleanSetting("Notify", true));
    private final BooleanSetting smartHealth = addSetting(new BooleanSetting("Smart Health", false));
    private final SliderSetting healthThreshold = addSetting(new SliderSetting("Health Threshold", 10, 1, 20, 0.5));
    private final BooleanSetting fallProtection = addSetting(new BooleanSetting("Fall Protection", true));
    private final SliderSetting fallDistance = addSetting(new SliderSetting("Fall Distance", 8, 3, 40, 1));
    private final BooleanSetting fireProtection = addSetting(new BooleanSetting("Fire Protection", true));
    private final BooleanSetting explosionProtection = addSetting(new BooleanSetting("Explosion Protection", true));
    private final BooleanSetting voidProtection = addSetting(new BooleanSetting("Void Protection", true));
    private final SliderSetting voidY = addSetting(new SliderSetting("Void Y", 8, 0, 64, 1));
    private final BooleanSetting prioritizeOffhand = addSetting(new BooleanSetting("Prioritize Offhand", true));
    private final BooleanSetting antiSpam = addSetting(new BooleanSetting("Anti Spam", true));
    private final SliderSetting tickRate = addSetting(new SliderSetting("Tick Rate", 1, 1, 20, 1));
    private int ticks;

    public AutoTotem() { super("Auto Totem", ModuleCategory.COMBAT, 0); }

    @Override public void onEnable() { ticks = 0; }

    @Override public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (onlyWhenNotScreen.isEnabled() && mc.currentScreen != null) return;
        if (whileAlive.isEnabled() && !mc.player.isAlive()) return;
        int interval = Math.max(1, tickRate.getValue().intValue());
        interval = Math.max(interval, delay.getValue().intValue());
        if (++ticks < interval) return;
        ticks = 0;

        try {
            int visible = InventoryUtils.countItem(mc.player.inventory, Items.TOTEM_OF_UNDYING);
            if (visible < minTotems.getValue().intValue()) return;
            if (!replaceEmpty.isEnabled() && mc.player.getOffHandStack().isEmpty()) return;
            if (!replaceNonTotem.isEnabled() && !mc.player.getOffHandStack().isEmpty()
                    && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) return;
            if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
            if (smartHealth.isEnabled() && mc.player.getHealth() > healthThreshold.getValue().floatValue()) return;
            boolean emergency = false;
            if (fallProtection.isEnabled() && mc.player.fallDistance >= fallDistance.getValue().floatValue()) emergency = true;
            if (fireProtection.isEnabled() && mc.player.isOnFire()) emergency = true;
            if (voidProtection.isEnabled() && mc.player.getY() <= voidY.getValue()) emergency = true;
            if (smartHealth.isEnabled() && mc.player.getHealth() <= healthThreshold.getValue().floatValue()) emergency = true;
            if (!emergency && smartHealth.isEnabled()) return;

            int slot = InventoryUtils.findItem(mc.player.inventory, Items.TOTEM_OF_UNDYING);
            if (slot < 0) return;
            if (hotbarOnly.isEnabled() && slot > 8) return;
            if (slot > 8) return; // no unsafe inventory-screen manipulation
            InventoryUtils.swapHotbarToOffhand(mc, slot);
        } catch (Throwable ignored) {
            // Automation must never crash the client.
        }
    }
}
