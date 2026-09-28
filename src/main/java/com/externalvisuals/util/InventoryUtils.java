package com.externalvisuals.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/** Client-side inventory helpers. All operations are fail-safe and only run in-game. */
public final class InventoryUtils {
    private InventoryUtils() {}

    public static int findItem(PlayerInventory inventory, net.minecraft.item.Item item) {
        if (inventory == null || item == null) return -1;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && stack.getItem() == item) return slot;
        }
        return -1;
    }

    public static int countItem(PlayerInventory inventory, net.minecraft.item.Item item) {
        if (inventory == null || item == null) return 0;
        int total = 0;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && stack.getItem() == item) total += stack.getCount();
        }
        return total;
    }

    /** Counts what the client can actually see on a remote player. Remote full inventories are not synchronized by vanilla. */
    public static int countVisibleTotems(PlayerEntity player) {
        if (player == null) return 0;
        int total = 0;
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if (!main.isEmpty() && main.getItem() == Items.TOTEM_OF_UNDYING) total += main.getCount();
        if (!off.isEmpty() && off.getItem() == Items.TOTEM_OF_UNDYING) total += off.getCount();
        return total;
    }

    public static boolean hasElytra(PlayerInventory inventory) {
        return findItem(inventory, Items.ELYTRA) >= 0;
    }

    public static int findElytra(PlayerInventory inventory) {
        return findItem(inventory, Items.ELYTRA);
    }

    public static boolean hasTotem(PlayerInventory inventory) {
        return findItem(inventory, Items.TOTEM_OF_UNDYING) >= 0;
    }

    /**
     * Safely moves a hotbar slot into the offhand. This uses the vanilla SWAP
     * slot action so the server remains authoritative over the inventory.
     */
    public static boolean swapHotbarToOffhand(MinecraftClient mc, int hotbarSlot) {
        try {
            if (mc == null || mc.player == null || mc.interactionManager == null) return false;
            if (hotbarSlot < 0 || hotbarSlot > 8) return false;
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    45,
                    hotbarSlot,
                    SlotActionType.SWAP,
                    mc.player
            );
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Swaps a hotbar item with the chest slot (slot 6 in the player handler). */
    public static boolean swapHotbarToChest(MinecraftClient mc, int hotbarSlot) {
        try {
            if (mc == null || mc.player == null || mc.interactionManager == null) return false;
            if (hotbarSlot < 0 || hotbarSlot > 8) return false;
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    6,
                    hotbarSlot,
                    SlotActionType.SWAP,
                    mc.player
            );
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
