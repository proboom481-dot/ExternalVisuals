package com.externalvisuals.modules.player;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import com.externalvisuals.util.InventoryUtils;
import net.minecraft.item.Items;

/** Swaps a hotbar elytra into the chest slot using vanilla SWAP actions. */
public final class ElytraSwapper extends Module {
    private final BooleanSetting auto = addSetting(new BooleanSetting("Auto Swap", true));
    private final BooleanSetting onlyFlying = addSetting(new BooleanSetting("Only While Flying", true));
    private final BooleanSetting onlyFalling = addSetting(new BooleanSetting("Only While Falling", false));
    private final BooleanSetting replaceChestplate = addSetting(new BooleanSetting("Replace Chestplate", true));
    private final BooleanSetting preferDurability = addSetting(new BooleanSetting("Prefer Durability", true));
    private final SliderSetting minDurability = addSetting(new SliderSetting("Min Durability", 20, 1, 432, 1));
    private final BooleanSetting hotbarOnly = addSetting(new BooleanSetting("Hotbar Only", true));
    private final SliderSetting delay = addSetting(new SliderSetting("Delay", 3, 0, 20, 1));
    private final StringSetting trigger = addSetting(new StringSetting("Trigger", "AUTO", "AUTO", "FLY", "FALL"));
    private final BooleanSetting preserveSlot = addSetting(new BooleanSetting("Preserve Selected Slot", true));
    private final BooleanSetting notify = addSetting(new BooleanSetting("Notify", true));
    private final BooleanSetting smartTakeoff = addSetting(new BooleanSetting("Smart Takeoff", true));
    private final BooleanSetting smartLanding = addSetting(new BooleanSetting("Smart Landing", true));
    private final SliderSetting landingTicks = addSetting(new SliderSetting("Landing Ticks", 8, 1, 40, 1));
    private final BooleanSetting fireworkReady = addSetting(new BooleanSetting("Firework Ready", false));
    private final BooleanSetting antiSpam = addSetting(new BooleanSetting("Anti Spam", true));
    private final BooleanSetting swapBack = addSetting(new BooleanSetting("Swap Back", true));
    private final BooleanSetting jumpAssist = addSetting(new BooleanSetting("Jump Assist", false));
    private final SliderSetting jumpVelocity = addSetting(new SliderSetting("Jump Velocity", 0.25, 0.05, 1.0, 0.05));
    private final BooleanSetting hotbarChestplate = addSetting(new BooleanSetting("Hotbar Chestplate", true));
    private int ticks;
    private boolean swappedByUs;

    public ElytraSwapper() { super("Elytra Swapper", ModuleCategory.PLAYER, 0); }

    @Override public void onEnable() { ticks = 0; swappedByUs = false; }

    @Override public void onTick() {
        if (!auto.isEnabled() || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (++ticks < Math.max(1, delay.getValue().intValue())) return;
        ticks = 0;
        try {
            boolean shouldDeploy = mc.player.isFallFlying() ||
                    (!mc.player.isOnGround() && mc.player.getVelocity().y < -0.08) ||
                    (jumpAssist.isEnabled() && mc.player.getVelocity().y > jumpVelocity.getValue());
            if (onlyFlying.isEnabled() && !shouldDeploy) {
                if (swapBack.isEnabled() && swappedByUs && mc.player.isOnGround()) restoreChestplate();
                return;
            }
            if (onlyFalling.isEnabled() && mc.player.getVelocity().y >= 0) return;
            if (!replaceChestplate.isEnabled() && !mc.player.inventory.getArmorStack(2).isEmpty()) return;
            int slot = findBestElytraSlot();
            if (slot < 0 || (hotbarOnly.isEnabled() && slot > 8) || slot > 8) return;
            if (InventoryUtils.swapHotbarToChest(mc, slot)) swappedByUs = true;
        } catch (Throwable ignored) {}
    }

    private int findBestElytraSlot() {
        int best = -1;
        int bestRemaining = -1;
        for (int i = 0; i < mc.player.inventory.size(); i++) {
            net.minecraft.item.ItemStack stack = mc.player.inventory.getStack(i);
            if (stack.isEmpty() || stack.getItem() != Items.ELYTRA) continue;
            int remaining = stack.isDamageable() ? stack.getMaxDamage() - stack.getDamage() : Integer.MAX_VALUE;
            if (remaining < minDurability.getValue().intValue()) continue;
            if (!preferDurability.isEnabled()) return i;
            if (remaining > bestRemaining) {
                bestRemaining = remaining;
                best = i;
            }
        }
        return best;
    }

    private void restoreChestplate() {
        try {
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                net.minecraft.item.ItemStack stack = mc.player.inventory.getStack(i);
                if (!stack.isEmpty() && stack.getItem() != Items.ELYTRA &&
                        (stack.getItem() instanceof net.minecraft.item.ArmorItem) &&
                        ((net.minecraft.item.ArmorItem) stack.getItem()).getSlotType() == net.minecraft.entity.EquipmentSlot.CHEST) {
                    slot = i; break;
                }
            }
            if (slot >= 0 && InventoryUtils.swapHotbarToChest(mc, slot)) swappedByUs = false;
        } catch (Throwable ignored) {}
    }
}
