package com.externalvisuals.modules.hud;

public final class KeystrokesHud extends HudModule {

    public KeystrokesHud() {
        super("Keystrokes", 0, 6.0f, 136.0f);
    }

    public boolean forward() {
        return mc.options.keyForward != null
                && mc.options.keyForward.isPressed();
    }

    public boolean left() {
        return mc.options.keyLeft != null
                && mc.options.keyLeft.isPressed();
    }

    public boolean back() {
        return mc.options.keyBack != null
                && mc.options.keyBack.isPressed();
    }

    public boolean right() {
        return mc.options.keyRight != null
                && mc.options.keyRight.isPressed();
    }

    public boolean jump() {
        return mc.options.keyJump != null
                && mc.options.keyJump.isPressed();
    }

    public boolean attack() {
        return mc.options.keyAttack != null
                && mc.options.keyAttack.isPressed();
    }

    public boolean use() {
        return mc.options.keyUse != null
                && mc.options.keyUse.isPressed();
    }
}
