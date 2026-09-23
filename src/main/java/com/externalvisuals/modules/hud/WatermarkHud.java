package com.externalvisuals.modules.hud;

import com.externalvisuals.ExternalVisuals;

public final class WatermarkHud extends HudModule {

    public WatermarkHud() {
        super("Watermark", 0, 6.0f, 110.0f);
    }

    public String getText() {
        return ExternalVisuals.NAME
                + "  "
                + ExternalVisuals.VERSION;
    }
}
