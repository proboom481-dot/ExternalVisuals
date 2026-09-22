package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class AimAssist extends Module {

    private final SliderSetting range =
            addSetting(
                    new SliderSetting(
                            "Range",
                            6.0,
                            2.0,
                            12.0,
                            0.5
                    )
            );

    private final SliderSetting fov =
            addSetting(
                    new SliderSetting(
                            "FOV",
                            90.0,
                            10.0,
                            180.0,
                            5.0
                    )
            );

    private final SliderSetting speed =
            addSetting(
                    new SliderSetting(
                            "Speed",
                            4.0,
                            0.5,
                            20.0,
                            0.5
                    )
            );

    private final BooleanSetting players =
            addSetting(
                    new BooleanSetting(
                            "Players",
                            true
                    )
            );

    private final BooleanSetting requireAttackKey =
            addSetting(
                    new BooleanSetting(
                            "Only While Attacking",
                            false
                    )
            );

    public AimAssist() {
        super(
                "Aim Assist",
                ModuleCategory.COMBAT,
                0
        );
    }

    @Override
    public void onTick() {

        if (!isEnabled()) {
            return;
        }

        if (mc.world == null
                || mc.player == null) {
            return;
        }

        if (requireAttackKey.isEnabled()
                && (mc.options == null
                || mc.options.keyAttack == null
                || !mc.options.keyAttack.isPressed())) {
            return;
        }

        Entity target =
                findTarget();

        if (target == null) {
            return;
        }

        rotateTowards(target);
    }

    private Entity findTarget() {

        Entity bestTarget = null;

        double bestDistance = Double.MAX_VALUE;

        double maxRange =
                Math.max(
                        1.0,
                        range.getValue()
                );

        double maxFov =
                Math.max(
                        1.0,
                        fov.getValue()
                );

        for (Entity entity :
                mc.world.getEntities()) {

            if (!(entity instanceof PlayerEntity)) {
                continue;
            }

            if (!players.isEnabled()) {
                continue;
            }

            if (entity == mc.player) {
                continue;
            }

            if (!entity.isAlive()) {
                continue;
            }

            double distance =
                    mc.player.distanceTo(entity);

            if (distance > maxRange) {
                continue;
            }

            float yawDifference =
                    getYawDifference(entity);

            if (Math.abs(yawDifference)
                    > maxFov / 2.0) {
                continue;
            }

            if (distance < bestDistance) {

                bestDistance = distance;
                bestTarget = entity;
            }
        }

        return bestTarget;
    }

    private float getYawDifference(
            Entity target
    ) {

        double dx =
                target.getX()
                        - mc.player.getX();

        double dz =
                target.getZ()
                        - mc.player.getZ();

        float targetYaw =
                (float) (
                        Math.toDegrees(
                                Math.atan2(
                                        dz,
                                        dx
                                )
                        ) - 90.0
                );

        return MathHelper.wrapDegrees(
                targetYaw
                        - mc.player.yaw
        );
    }

    private void rotateTowards(
            Entity target
    ) {

        double dx =
                target.getX()
                        - mc.player.getX();

        double dz =
                target.getZ()
                        - mc.player.getZ();

        double targetX =
                target.getX();

        double targetY =
                target.getY()
                        + target.getHeight()
                        * 0.85;

        double targetZ =
                target.getZ();

        double playerX =
                mc.player.getX();

        double playerY =
                mc.player.getY()
                        + mc.player.getStandingEyeHeight();

        double playerZ =
                mc.player.getZ();

        double horizontalDistance =
                Math.sqrt(
                        dx * dx
                                + dz * dz
                );

        float targetYaw =
                (float) (
                        Math.toDegrees(
                                Math.atan2(
                                        dz,
                                        dx
                                )
                        ) - 90.0
                );

        float targetPitch =
                (float) (
                        -Math.toDegrees(
                                Math.atan2(
                                        targetY - playerY,
                                        horizontalDistance
                                )
                        )
                );

        float yawDifference =
                MathHelper.wrapDegrees(
                        targetYaw
                                - mc.player.yaw
                );

        float pitchDifference =
                targetPitch
                        - mc.player.pitch;

        float rotationSpeed =
                (float) Math.max(
                        0.1,
                        speed.getValue()
                );

        float yawStep =
                clamp(
                        yawDifference
                                * (
                                rotationSpeed
                                        / 10.0f
                        ),
                        -rotationSpeed,
                        rotationSpeed
                );

        float pitchStep =
                clamp(
                        pitchDifference
                                * (
                                rotationSpeed
                                        / 10.0f
                        ),
                        -rotationSpeed,
                        rotationSpeed
                );

        mc.player.yaw += yawStep;
        mc.player.pitch =
                MathHelper.clamp(
                        mc.player.pitch
                                + pitchStep,
                        -90.0f,
                        90.0f
                );
    }

    private float clamp(
            float value,
            float min,
            float max
    ) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    public SliderSetting getRangeSetting() {
        return range;
    }

    public SliderSetting getFovSetting() {
        return fov;
    }

    public SliderSetting getSpeedSetting() {
        return speed;
    }

    public BooleanSetting getPlayersSetting() {
        return players;
    }

    public BooleanSetting getRequireAttackKeySetting() {
        return requireAttackKey;
    }
}