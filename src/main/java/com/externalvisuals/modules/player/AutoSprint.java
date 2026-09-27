package com.externalvisuals.modules.player;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public final class AutoSprint extends Module {

    private final BooleanSetting onlyForward =
            addSetting(
                    new BooleanSetting(
                            "Only Forward",
                            true
                    )
            );

    public AutoSprint() {
        super(
                "Auto Sprint",
                ModuleCategory.PLAYER,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    @Override
    public void onTick() {

        if (!isEnabled()
                || mc.player == null
                || mc.world == null) {
            return;
        }

        if (mc.player.isSneaking()
                || mc.player.isTouchingWater()
                || mc.player.isSubmergedInWater()) {
            return;
        }

        boolean movingForward =
                mc.player.forwardSpeed > 0.0f;

        boolean movingAnyDirection =
                Math.abs(mc.player.forwardSpeed) > 0.0f
                        || Math.abs(mc.player.sidewaysSpeed) > 0.0f;

        boolean shouldSprint =
                onlyForward.isEnabled()
                        ? movingForward
                        : movingAnyDirection;

        mc.player.setSprinting(
                shouldSprint
        );
    }

    @Override
    public void onDisable() {

        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }

    public BooleanSetting getOnlyForwardSetting() {
        return onlyForward;
    }
}
