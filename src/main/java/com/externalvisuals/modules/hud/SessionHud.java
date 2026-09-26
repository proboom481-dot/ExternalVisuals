package com.externalvisuals.modules.hud;

public final class SessionHud extends HudModule {

    private long startTime;

    public SessionHud() {
        super("Session Info", 0, 6.0f, 58.0f);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onEnable() {
        startTime = System.currentTimeMillis();
    }

    public String getText() {
        if (mc.player == null) {
            return "Session: 00:00";
        }

        long seconds =
                Math.max(
                        0L,
                        (System.currentTimeMillis() - startTime) / 1000L
                );

        long minutes = seconds / 60L;
        long hours = minutes / 60L;

        minutes %= 60L;
        seconds %= 60L;

        if (hours > 0L) {
            return String.format(
                    "Session: %02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
            );
        }

        return String.format(
                "Session: %02d:%02d",
                minutes,
                seconds
        );
    }
}
