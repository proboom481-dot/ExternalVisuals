package com.externalvisuals.modules.hud;

public final class PingHud extends HudModule {

    public PingHud() {
        super("Ping", 0, 0.0f, 6.0f);
        getBackgroundSetting().setEnabled(true);
    }

    public int getPing() {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return 0;
        }

        net.minecraft.client.network.PlayerListEntry entry =
                mc.getNetworkHandler()
                        .getPlayerListEntry(mc.player.getUuid());

        return entry == null ? 0 : Math.max(0, entry.getLatency());
    }

    public String getText() {
        return "Ping: " + getPing() + " ms";
    }
}
