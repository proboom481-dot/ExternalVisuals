package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * Smooth client-side target assistance for living entities.
 *
 * The target list is rebuilt every tick from the current world spatial query,
 * so entities spawned after the module was enabled are immediately eligible.
 */
public final class AimAssist extends Module {

    private final SliderSetting range = addSetting(new SliderSetting("Range", 6.0, 2.0, 12.0, 0.5));
    private final SliderSetting fov = addSetting(new SliderSetting("FOV", 90.0, 10.0, 180.0, 5.0));
    private final SliderSetting speed = addSetting(new SliderSetting("Speed", 5.0, 0.5, 20.0, 0.5));
    private final SliderSetting verticalStrength = addSetting(new SliderSetting("Vertical Strength", 0.75, 0.0, 1.0, 0.05));
    private final SliderSetting prediction = addSetting(new SliderSetting("Prediction", 0.0, 0.0, 1.0, 0.05));

    private final BooleanSetting players = addSetting(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = addSetting(new BooleanSetting("Mobs", true));
    private final BooleanSetting preferPlayers = addSetting(new BooleanSetting("Prefer Players", true));
    private final BooleanSetting ignoreTeammates = addSetting(new BooleanSetting("Ignore Teammates", true));
    private final BooleanSetting requireAttackKey = addSetting(new BooleanSetting("Only While Attacking", false));
    private final BooleanSetting visibleOnly = addSetting(new BooleanSetting("Visible Only", false));
    private final BooleanSetting throughWalls = addSetting(new BooleanSetting("Through Walls", true));
    private final BooleanSetting horizontalOnly = addSetting(new BooleanSetting("Horizontal Only", false));
    private final StringSetting targetPoint = addSetting(new StringSetting("Target Point", "CHEST", "FEET", "CHEST", "HEAD", "CENTER"));
    private final StringSetting priority = addSetting(new StringSetting("Priority", "ANGLE", "ANGLE", "DISTANCE", "HEALTH", "PLAYERS"));
    private final StringSetting aimMode = addSetting(new StringSetting("Aim Mode", "SMOOTH", "SMOOTH", "LINEAR", "SNAP"));

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
        double maxRange = Math.max(1.0, range.getValue());
        double maxFov = Math.max(1.0, fov.getValue());
        Box searchBox = mc.player.getBoundingBox().expand(maxRange);

        List<Entity> candidates = mc.world.getOtherEntities(mc.player, searchBox, entity -> {
            if (!(entity instanceof LivingEntity)) return false;
            LivingEntity living = (LivingEntity) entity;
            if (!living.isAlive() || !living.isAttackable()) return false;

            boolean isPlayer = entity instanceof PlayerEntity;
            if (isPlayer && !players.isEnabled()) return false;
            if (!isPlayer && !mobs.isEnabled()) return false;
            if (ignoreTeammates.isEnabled() && mc.player.isTeammate(entity)) return false;
            if (!throughWalls.isEnabled() && visibleOnly.isEnabled() && !mc.player.canSee(entity)) return false;
            return true;
        });

        Entity bestTarget = null;
        double bestScore = Double.MAX_VALUE;

        for (Entity entity : candidates) {
            double distance = mc.player.distanceTo(entity);
            if (distance > maxRange) continue;

            float yawDifference = Math.abs(getYawDifference(entity));
            if (yawDifference > maxFov * 0.5f) continue;

            double score = scoreTarget(entity, yawDifference, distance);
            if (score < bestScore) {
                bestScore = score;
                bestTarget = entity;
            }
        }
        return bestTarget;
    }

    private double scoreTarget(Entity entity, double yawDifference, double distance) {
        String mode = priority.getValue();
        if ("DISTANCE".equalsIgnoreCase(mode)) return distance + yawDifference * 0.025;
        if ("HEALTH".equalsIgnoreCase(mode) && entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getHealth() + yawDifference * 0.025 + distance * 0.01;
        }
        if ("PLAYERS".equalsIgnoreCase(mode)) {
            return (entity instanceof PlayerEntity ? -100.0 : 0.0) + yawDifference * 2.0 + distance * 0.1;
        }

        double playerBonus = preferPlayers.isEnabled() && entity instanceof PlayerEntity ? -8.0 : 0.0;
        return yawDifference * 2.0 + distance * 0.15 + playerBonus;
    }

    private float getYawDifference(Entity target) {
        double dx = getTargetX(target) - mc.player.getX();
        double dz = getTargetZ(target) - mc.player.getZ();
        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        return MathHelper.wrapDegrees(targetYaw - mc.player.yaw);
    }

    private double getTargetX(Entity target) {
        return target.getX() + target.getVelocity().x * prediction.getValue() * 4.0;
    }

    private double getTargetZ(Entity target) {
        return target.getZ() + target.getVelocity().z * prediction.getValue() * 4.0;
    }

    private double getTargetY(Entity target) {
        double base;
        switch (targetPoint.getValue().toUpperCase()) {
            case "FEET": base = target.getY() + 0.15; break;
            case "HEAD": base = target.getY() + target.getHeight() * 0.88; break;
            case "CENTER": base = target.getY() + target.getHeight() * 0.50; break;
            case "CHEST":
            default: base = target.getY() + target.getHeight() * 0.55; break;
        }
        return base + target.getVelocity().y * prediction.getValue() * 3.0;
    }

    private void rotateTowards(Entity target) {
        double dx = getTargetX(target) - mc.player.getX();
        double dz = getTargetZ(target) - mc.player.getZ();
        double targetY = getTargetY(target);
        double playerY = mc.player.getY() + mc.player.getStandingEyeHeight();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance < 0.001) return;

        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(targetY - playerY, horizontalDistance)));
        float yawDifference = MathHelper.wrapDegrees(targetYaw - mc.player.yaw);
        float pitchDifference = targetPitch - mc.player.pitch;

        float maxStep = (float) Math.max(0.1, speed.getValue());
        float yawStep;
        float pitchStep;

        if (aimMode.is("SNAP")) {
            yawStep = yawDifference;
            pitchStep = pitchDifference;
        } else if (aimMode.is("LINEAR")) {
            yawStep = clamp(yawDifference, -maxStep, maxStep);
            pitchStep = clamp(pitchDifference, -maxStep * 0.75f, maxStep * 0.75f);
        } else {
            float factor = Math.min(1.0f, maxStep / 8.0f);
            yawStep = clamp(yawDifference * factor, -maxStep, maxStep);
            pitchStep = clamp(pitchDifference * factor * verticalStrength.getValue().floatValue(),
                    -maxStep * 0.75f, maxStep * 0.75f);
        }

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
    public SliderSetting getPredictionSetting() { return prediction; }
    public BooleanSetting getPlayersSetting() { return players; }
    public BooleanSetting getMobsSetting() { return mobs; }
    public BooleanSetting getPreferPlayersSetting() { return preferPlayers; }
    public BooleanSetting getIgnoreTeammatesSetting() { return ignoreTeammates; }
    public BooleanSetting getRequireAttackKeySetting() { return requireAttackKey; }
    public BooleanSetting getVisibleOnlySetting() { return visibleOnly; }
    public BooleanSetting getThroughWallsSetting() { return throughWalls; }
    public BooleanSetting getHorizontalOnlySetting() { return horizontalOnly; }
    public StringSetting getTargetPointSetting() { return targetPoint; }
    public StringSetting getPrioritySetting() { return priority; }
    public StringSetting getAimModeSetting() { return aimMode; }
}
