package com.externalvisuals.modules.hud;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ClockHud extends HudModule {

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClockHud() {
        super("Clock", 0, 0.0f, 32.0f);
    }

    public String getText() {
        return "Time: " + LocalTime.now().format(formatter);
    }
}
