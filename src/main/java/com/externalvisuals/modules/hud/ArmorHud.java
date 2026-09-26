package com.externalvisuals.modules.hud;

import net.minecraft.item.ItemStack;

public final class ArmorHud extends HudModule {
    public ArmorHud() {
        super("Armor HUD", 0, 6.0f, 226.0f);
    }

    public String[] getLines() {
        if (mc.player == null) return new String[0];
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (ItemStack stack : mc.player.getArmorItems()) {
            if (stack == null || stack.isEmpty()) {
                lines.add("-");
            } else {
                lines.add(stack.getName().getString() + "  " + stack.getDamage());
            }
        }
        return lines.toArray(new String[0]);
    }
}
