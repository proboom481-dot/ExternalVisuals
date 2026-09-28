package com.externalvisuals.modules.hud;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CpsHud extends HudModule {

    private final Deque<Long> clicks = new ArrayDeque<>();

    public CpsHud() {
        super("CPS", 0, 0.0f, 58.0f);
    }

    public void recordClick() {
        if (!isEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        clicks.addLast(now);
        trim(now);
    }

    @Override
    public void onTick() {
        trim(System.currentTimeMillis());
    }

    private void trim(long now) {
        long cutoff = now - 1000L;

        while (!clicks.isEmpty()
                && clicks.peekFirst() < cutoff) {
            clicks.removeFirst();
        }
    }

    public int getCps() {
        long now = System.currentTimeMillis();
        trim(now);
        return clicks.size();
    }

    public String getText() {
        return "CPS: " + getCps();
    }

    @Override
    public void onDisable() {
        clicks.clear();
    }
}
