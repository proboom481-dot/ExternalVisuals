package com.externalvisuals.modules.hud;

import net.minecraft.item.ItemStack;

public final class ItemCounterHud extends HudModule {

    public ItemCounterHud() {
        super("Item Counter", 0, 0.0f, 84.0f);
    }

    public String getText() {
        if (mc.player == null) {
            return "Item: --";
        }

        ItemStack stack = mc.player.getMainHandStack();

        if (stack == null || stack.isEmpty()) {
            return "Item: Empty";
        }

        return stack.getName().getString()
                + " x"
                + stack.getCount();
    }
}
