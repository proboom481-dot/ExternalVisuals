package com.externalvisuals.modules.hud;

public final class CoordinatesHud extends HudModule {

    public CoordinatesHud() {
        super("Coordinates", 0, 6.0f, 32.0f);
    }

    public String getText() {
        if (mc.player == null) {
            return "XYZ: -- -- --";
        }

        return String.format(
                "XYZ: %.1f %.1f %.1f",
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ()
        );
    }
}
