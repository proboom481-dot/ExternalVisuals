package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class AimAssist extends Module {

    private final SliderSetting range = addSetting(new SliderSetting("Range", 6.0, 2.0, 12.0, 0.5));
    private final SliderSetting fov = addSetting(new SliderSetting("FOV", 90.0, 10.0, 180.0, 5.0));
    private final SliderSetting speed = addSetting(new SliderSetting("Speed", 5.0, 0.5, 20.0, 0.5));
    private final SliderSetting verticalStrength = addSetting(new SliderSetting("Vertical Strength", 0.75, 0.0, 1.0, 0.05));
    private final BooleanSetting players = addSetting(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = addSetting(new BooleanSetting("Mobs", true));
    private final BooleanSetting preferPlayers = addSetting(new BooleanSetting("Prefer Players", true));
    private final BooleanSetting requireAttackKey = addSetting(new BooleanSetting("Only While Attacking", false));
    private final BooleanSetting visibleOnly = addSetting(new BooleanSetting("Visible Only", true));
    private final BooleanSetting horizontalOnly = addSetting(new BooleanSetting("Horizontal Only", false));
    private final StringSetting targetPoint = addSetting(new StringSetting("Target Point", "CHEST", "FEET", "CHEST", "HEAD"));

    public AimAssist() {
        super("Aim Assist", ModuleCategory.COMBAT, 0);
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.world == null || mc.player == null) return;
        if (requireAttackKey.isEnabled() && (mc.options == null || mc.options.keyAttack == null
                || !mc.options.keyAttack.isPressed())) return;
        Entity target = findTarget();
        if (target != null) rotateTowards(target);
    }

    private Entity findTarget() {
        Entity bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        double maxRange = Math.max(1.0, range.getValue());
        double maxFov = Math.max(1.0, fov.getValue());

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) entity;
            if (entity == mc.player || !living.isAlive()) continue;

            boolean isPlayer = entity instanceof PlayerEntity;
            if (isPlayer && !players.isEnabled()) continue;
            if (!isPlayer && !mobs.isEnabled()) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance > maxRange) continue;
            if (visibleOnly.isEnabled() && !mc.player.canSee(entity)) continue;

            float yawDifference = Math.abs(getYawDifference(entity));
            if (yawDifference > maxFov / 2.0f) continue;

            double playerBonus = preferPlayers.isEnabled() && isPlayer ? -8.0 : 0.0;
            double score = yawDifference * 2.0 + distance * 0.15 + playerBonus;
            if (score < bestScore) {
                bestScore = score;
                bestTarget = entity;
            }
        }
        return bestTarget;
    }

    private float getYawDifference(Entity target) {
        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        return MathHelper.wrapDegrees(targetYaw - mc.player.yaw);
    }

    private double getTargetY(Entity target) {
        switch (targetPoint.getValue().toUpperCase()) {
            case "FEET": return target.getY() + 0.15;
            case "HEAD": return target.getY() + target.getHeight() * 0.88;
            case "CHEST":
            default: return target.getY() + target.getHeight() * 0.55;
        }
    }

    private void rotateTowards(Entity target) {
        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        double targetY = getTargetY(target);
        double playerY = mc.player.getY() + mc.player.getStandingEyeHeight();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance < 0.001) return;

        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(targetY - playerY, horizontalDistance)));
        float yawDifference = MathHelper.wrapDegrees(targetYaw - mc.player.yaw);
        float pitchDifference = targetPitch - mc.player.pitch;

        float rotationSpeed = (float) Math.max(0.1, speed.getValue());
        float factor = Math.min(1.0f, rotationSpeed / 8.0f);
        float yawStep = clamp(yawDifference * factor, -rotationSpeed, rotationSpeed);
        float pitchStep = clamp(pitchDifference * factor * verticalStrength.getValue().floatValue(),
                -rotationSpeed * 0.75f, rotationSpeed * 0.75f);

        mc.player.yaw += yawStep;
        if (!horizontalOnly.isEnabled()) {
            mc.player.pitch = MathHelper.clamp(mc.player.pitch + pitchStep, -90.0f, 90.0f);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public SliderSetting getRangeSetting() { return range; }
    public SliderSetting getFovSetting() { return fov; }
    public SliderSetting getSpeedSetting() { return speed; }
    public SliderSetting getVerticalStrengthSetting() { return verticalStrength; }
    public BooleanSetting getPlayersSetting() { return players; }
    public BooleanSetting getMobsSetting() { return mobs; }
    public BooleanSetting getPreferPlayersSetting() { return preferPlayers; }
    public BooleanSetting getRequireAttackKeySetting() { return requireAttackKey; }
    public BooleanSetting getVisibleOnlySetting() { return visibleOnly; }
    public BooleanSetting getHorizontalOnlySetting() { return horizontalOnly; }
    public StringSetting getTargetPointSetting() { return targetPoint; }
}
