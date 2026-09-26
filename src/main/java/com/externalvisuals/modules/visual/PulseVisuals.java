package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Pulse-style visual effects for 1.16.5.
 *
 * This is intentionally self-contained: it adds the visual systems that were
 * missing from the original ExternalVisuals feature set without touching
 * vanilla chunk/render distance settings.
 */
public final class PulseVisuals extends Module {

    private final BooleanSetting screenPulse = addSetting(new BooleanSetting("Screen Pulse", true));
    private final SliderSetting pulseIntensity = addSetting(new SliderSetting("Pulse Intensity", 0.55, 0.05, 1.0, 0.05));
    private final SliderSetting pulseDuration = addSetting(new SliderSetting("Pulse Duration", 320, 80, 900, 20));
    private final ColorSetting pulseColor = addSetting(new ColorSetting("Pulse Color", 0xFFFF365C));
    private final BooleanSetting pulseLowHealth = addSetting(new BooleanSetting("Low HP Pulse", true));
    private final SliderSetting lowHpThreshold = addSetting(new SliderSetting("Low HP Threshold", 35, 5, 100, 1));

    private final BooleanSetting hitDirection = addSetting(new BooleanSetting("Hit Direction", true));
    private final SliderSetting directionDuration = addSetting(new SliderSetting("Direction Duration", 700, 150, 1500, 25));
    private final SliderSetting directionSize = addSetting(new SliderSetting("Direction Size", 28, 10, 60, 1));
    private final ColorSetting directionColor = addSetting(new ColorSetting("Direction Color", 0xFFFF4D67));

    private final BooleanSetting comboCounter = addSetting(new BooleanSetting("Combo Counter", true));
    private final SliderSetting comboTimeout = addSetting(new SliderSetting("Combo Timeout", 900, 250, 2500, 50));
    private final BooleanSetting comboPulse = addSetting(new BooleanSetting("Combo Pulse", true));
    private final ColorSetting comboColor = addSetting(new ColorSetting("Combo Color", 0xFFFFD34D));

    private final BooleanSetting killFeed = addSetting(new BooleanSetting("Kill Feed", true));
    private final SliderSetting killFeedDuration = addSetting(new SliderSetting("Kill Feed Duration", 2500, 500, 6000, 100));
    private final SliderSetting killFeedEntries = addSetting(new SliderSetting("Kill Feed Entries", 4, 1, 8, 1));

    private final BooleanSetting weaponTrailEnabled = addSetting(new BooleanSetting("Weapon Trail", true));
    private final SliderSetting weaponTrailLength = addSetting(new SliderSetting("Weapon Trail Length", 8, 2, 24, 1));
    private final ColorSetting weaponTrailColor = addSetting(new ColorSetting("Weapon Trail Color", 0xFFFFC83D));
    private final ColorSetting criticalTrailColor = addSetting(new ColorSetting("Critical Trail Color", 0xFFFF3B57));

    private final BooleanSetting sprintTrailEnabled = addSetting(new BooleanSetting("Sprint Trail", false));
    private final SliderSetting sprintTrailLength = addSetting(new SliderSetting("Sprint Trail Length", 10, 2, 30, 1));
    private final ColorSetting sprintTrailColor = addSetting(new ColorSetting("Sprint Trail Color", 0xFF42D9FF));

    private final BooleanSetting dynamicIsland = addSetting(new BooleanSetting("Dynamic Island", true));
    private final SliderSetting islandScale = addSetting(new SliderSetting("Island Scale", 1.0, 0.75, 1.35, 0.05));
    private final SliderSetting islandAlpha = addSetting(new SliderSetting("Island Alpha", 0.88, 0.35, 1.0, 0.05));
    private final ColorSetting islandColor = addSetting(new ColorSetting("Island Accent", 0xFF9B5CFF));
    private final BooleanSetting islandHealth = addSetting(new BooleanSetting("Island Health", true));
    private final BooleanSetting islandFps = addSetting(new BooleanSetting("Island FPS", true));
    private final BooleanSetting islandCombo = addSetting(new BooleanSetting("Island Combo", true));
    private final BooleanSetting islandModules = addSetting(new BooleanSetting("Island Modules", true));
    private final BooleanSetting islandTarget = addSetting(new BooleanSetting("Island Target", true));
    private final BooleanSetting criticalParticles = addSetting(new BooleanSetting("Critical Particles", true));
    private final BooleanSetting bloodParticles = addSetting(new BooleanSetting("Blood Particles", false));
    private final BooleanSetting damageTint = addSetting(new BooleanSetting("Damage Tint", true));
    private final ColorSetting damageTintColor = addSetting(new ColorSetting("Damage Tint Color", 0xFFFF365C));

    private final BooleanSetting dynamicCrosshair = addSetting(new BooleanSetting("Pulse Crosshair", true));
    private final SliderSetting crosshairTargetRadius = addSetting(new SliderSetting("Target Radius", 10, 4, 24, 1));
    private final ColorSetting crosshairColor = addSetting(new ColorSetting("Crosshair Color", 0xFF42D9FF));
    private final ColorSetting crosshairTargetColor = addSetting(new ColorSetting("Crosshair Target Color", 0xFFFF4D67));

    private final BooleanSetting criticalEffects = addSetting(new BooleanSetting("Critical Effects", true));
    private final BooleanSetting killEffects = addSetting(new BooleanSetting("Kill Effects", true));
    private final StringSetting killEffectStyle = addSetting(new StringSetting("Kill Effect", "LIGHTNING", "LIGHTNING", "EXPLOSION", "HEART"));
    private final BooleanSetting critFlash = addSetting(new BooleanSetting("Critical Screen Flash", true));
    private final SliderSetting critIntensity = addSetting(new SliderSetting("Critical Intensity", 0.30, 0.05, 0.8, 0.05));
    private final BooleanSetting hitMarker = addSetting(new BooleanSetting("Hit Marker", true));
    private final SliderSetting hitMarkerDuration = addSetting(new SliderSetting("Hit Marker Duration", 180, 60, 600, 20));
    private final SliderSetting hitMarkerSize = addSetting(new SliderSetting("Hit Marker Size", 9, 4, 20, 1));
    private final ColorSetting hitMarkerColor = addSetting(new ColorSetting("Hit Marker Color", 0xFFFFFFFF));
    private final BooleanSetting islandArmor = addSetting(new BooleanSetting("Island Armor", true));

    private long pulseUntil;
    private float pulsePower;
    private long directionUntil;
    private double directionAngle;
    private int combo;
    private long lastHitAt;
    private long lastCritAt;
    private long hitMarkerUntil;

    private final Deque<Vec3d> weaponTrailPoints = new ArrayDeque<>();
    private final Deque<Vec3d> sprintTrailPoints = new ArrayDeque<>();
    private final List<KillEntry> kills = new ArrayList<>();

    public PulseVisuals() {
        super("Pulse Visuals", ModuleCategory.VISUALS, 0);
    }

    @Override
    public void onDisable() {
        weaponTrailPoints.clear();
        sprintTrailPoints.clear();
        kills.clear();
        combo = 0;
        pulsePower = 0.0f;
        pulseUntil = 0L;
        directionUntil = 0L;
        hitMarkerUntil = 0L;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();

        if (lastHitAt > 0L && now - lastHitAt > comboTimeout.getValue().longValue()) {
            combo = 0;
        }

        trimTrail(weaponTrailPoints, weaponTrailLength.getValue().intValue());
        trimTrail(sprintTrailPoints, sprintTrailLength.getValue().intValue());

        if (mc.player.isSprinting() && sprintTrailPoints.isEmpty() ||
                mc.player.isSprinting() && mc.player.age % 2 == 0) {
            sprintTrailPoints.addFirst(mc.player.getPos().add(0.0, 0.05, 0.0));
        }

        Iterator<KillEntry> iterator = kills.iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().time > killFeedDuration.getValue().longValue()) {
                iterator.remove();
            }
        }

        if (pulseUntil < now) {
            pulsePower *= 0.82f;
            if (pulsePower < 0.01f) pulsePower = 0.0f;
        }
    }

    private void trimTrail(Deque<Vec3d> trail, int max) {
        while (trail.size() > Math.max(2, max)) trail.removeLast();
    }

    public void onConfirmedDamage(LivingEntity target, float damage) {
        if (!isEnabled() || mc.player == null || target == null) return;

        long now = System.currentTimeMillis();
        combo = (lastHitAt > 0L && now - lastHitAt <= comboTimeout.getValue().longValue())
                ? combo + 1 : 1;
        lastHitAt = now;
        if (hitMarker.isEnabled()) {
            hitMarkerUntil = now + hitMarkerDuration.getValue().longValue();
        }

        boolean critical = !mc.player.isOnGround()
                && mc.player.fallDistance > 0.0f
                && !mc.player.isSprinting();

        if (criticalParticles.isEnabled() && critical) {
            spawnCriticalBurst(target);
        } else if (bloodParticles.isEnabled()) {
            spawnBloodBurst(target);
        }

        if (weaponTrailEnabled.isEnabled()) {
            weaponTrailPoints.addFirst(mc.player.getPos().add(0.0, 1.0, 0.0));
            trimTrail(weaponTrailPoints, weaponTrailLength.getValue().intValue());
        }

        if (screenPulse.isEnabled()) {
            pulsePower = Math.min(1.0f, Math.max(pulsePower, (float) (pulseIntensity.getValue() * (critical ? 1.15 : 0.75))));
            pulseUntil = now + pulseDuration.getValue().longValue();
        }

        if (critical && criticalEffects.isEnabled()) {
            lastCritAt = now;
            if (critFlash.isEnabled()) {
                pulsePower = Math.min(1.0f, pulsePower + critIntensity.getValue().floatValue());
            }
        }

        if (target.isDead() || target.getHealth() <= 0.0f) {
            addKill(target.getDisplayName().getString(), critical);
            if (killEffects.isEnabled()) spawnKillEffect(target);
        }
    }

    public void onPlayerDamage(net.minecraft.entity.damage.DamageSource source, float damage) {
        if (!isEnabled() || mc.player == null || damage <= 0.0f) return;

        long now = System.currentTimeMillis();
        if (screenPulse.isEnabled() && damageTint.isEnabled()) {
            pulsePower = Math.min(1.0f, Math.max(pulsePower, pulseIntensity.getValue().floatValue()));
            pulseUntil = now + pulseDuration.getValue().longValue();
        }

        if (hitDirection.isEnabled() && source != null && source.getAttacker() != null) {
            net.minecraft.entity.Entity attacker = source.getAttacker();
            double dx = attacker.getX() - mc.player.getX();
            double dz = attacker.getZ() - mc.player.getZ();
            if (Math.abs(dx) > 0.001 || Math.abs(dz) > 0.001) {
                directionAngle = Math.atan2(dz, dx);
                directionUntil = now + directionDuration.getValue().longValue();
            }
        }
    }

    private void addKill(String name, boolean critical) {
        kills.add(0, new KillEntry(name, System.currentTimeMillis(), critical));
        while (kills.size() > killFeedEntries.getValue().intValue()) kills.remove(kills.size() - 1);
    }

    public float getPulseAlpha() {
        if (!screenPulse.isEnabled() || pulsePower <= 0.0f) return 0.0f;
        long now = System.currentTimeMillis();
        double remaining = Math.max(0.0, pulseUntil - now);
        double fade = Math.min(1.0, remaining / Math.max(1.0, pulseDuration.getValue()));
        return (float) Math.max(0.0, Math.min(1.0, pulsePower * fade));
    }

    public boolean shouldShowLowHpPulse() {
        if (!pulseLowHealth.isEnabled() || mc.player == null) return false;
        return mc.player.getHealth() / Math.max(1.0f, mc.player.getMaxHealth()) * 100.0f <= lowHpThreshold.getValue();
    }

    public int getPulseColor() { return pulseColor.getColor(); }
    public int getDirectionColor() { return directionColor.getColor(); }
    public float getDirectionAlpha() {
        if (!hitDirection.isEnabled() || directionUntil <= System.currentTimeMillis()) return 0.0f;
        return (float) Math.min(1.0, (directionUntil - System.currentTimeMillis()) / (double) directionDuration.getValue());
    }
    public double getDirectionAngle() { return directionAngle; }
    public int getDirectionSize() { return directionSize.getValue().intValue(); }
    public boolean isComboEnabled() { return comboCounter.isEnabled(); }
    public int getCombo() { return combo; }
    public boolean isComboPulse() { return comboPulse.isEnabled(); }
    public int getComboColor() { return comboColor.getColor(); }
    public boolean isKillFeedEnabled() { return killFeed.isEnabled(); }
    public List<KillEntry> getKills() { return kills; }
    public boolean isWeaponTrailEnabled() { return weaponTrailEnabled.isEnabled(); }
    public Deque<Vec3d> getWeaponTrail() { return weaponTrailPoints; }
    public int getWeaponTrailLength() { return weaponTrailLength.getValue().intValue(); }
    public int getWeaponTrailColor() { return weaponTrailColor.getColor(); }
    public int getCriticalTrailColor() { return criticalTrailColor.getColor(); }
    public boolean isSprintTrailEnabled() { return sprintTrailEnabled.isEnabled(); }
    public Deque<Vec3d> getSprintTrail() { return sprintTrailPoints; }
    public int getSprintTrailLength() { return sprintTrailLength.getValue().intValue(); }
    public int getSprintTrailColor() { return sprintTrailColor.getColor(); }
    public boolean isDynamicCrosshairEnabled() { return dynamicCrosshair.isEnabled(); }
    public int getCrosshairTargetRadius() { return crosshairTargetRadius.getValue().intValue(); }
    public int getCrosshairColor() { return crosshairColor.getColor(); }
    public int getCrosshairTargetColor() { return crosshairTargetColor.getColor(); }
    public boolean isCriticalEffectsEnabled() { return criticalEffects.isEnabled(); }
    public boolean isKillEffectsEnabled() { return killEffects.isEnabled(); }
    public String getKillEffectStyle() { return killEffectStyle.getValue(); }
    public boolean isCritFlashEnabled() { return critFlash.isEnabled(); }
    public float getCritIntensity() { return critIntensity.getValue().floatValue(); }


    public boolean isDynamicIslandEnabled() { return dynamicIsland.isEnabled(); }
    public float getIslandScale() { return islandScale.getValue().floatValue(); }
    public float getIslandAlpha() { return islandAlpha.getValue().floatValue(); }
    public int getIslandColor() { return islandColor.getColor(); }
    public boolean showIslandHealth() { return islandHealth.isEnabled(); }
    public boolean showIslandFps() { return islandFps.isEnabled(); }
    public boolean showIslandCombo() { return islandCombo.isEnabled(); }
    public boolean showIslandModules() { return islandModules.isEnabled(); }
    public boolean showIslandTarget() { return islandTarget.isEnabled(); }
    public int getDamageTintColor() { return damageTintColor.getColor(); }
    public boolean isHitMarkerEnabled() { return hitMarker.isEnabled(); }
        return (float) Math.max(0.0, Math.min(1.0,
                (hitMarkerUntil - System.currentTimeMillis()) / (double) hitMarkerDuration.getValue()));
                (hitMarkerUntil - System.currentTimeMillis()) / (double) hitMarkerDuration.getValue()));
    }
    public int getHitMarkerSize() { return hitMarkerSize.getValue().intValue(); }
    public int getHitMarkerColor() { return hitMarkerColor.getColor(); }
    public boolean showIslandArmor() { return islandArmor.isEnabled(); }

    private void spawnCriticalBurst(LivingEntity target) {
        if (mc.world == null) return;
        for (int i = 0; i < 6; i++) {
            double a = i * Math.PI * 2.0 / 6.0;
            mc.world.addParticle(net.minecraft.particle.ParticleTypes.CRIT,
                    target.getX(), target.getY() + target.getHeight() * 0.55, target.getZ(),
                    Math.cos(a) * 0.08, 0.08, Math.sin(a) * 0.08);
        }
        mc.world.addParticle(net.minecraft.particle.ParticleTypes.FLASH,
                target.getX(), target.getY() + target.getHeight() * 0.55, target.getZ(), 0.0, 0.0, 0.0);
    }

    private void spawnBloodBurst(LivingEntity target) {
        if (mc.world == null) return;
        net.minecraft.particle.DustParticleEffect dust = new net.minecraft.particle.DustParticleEffect(0.95f, 0.08f, 0.12f, 1.0f);
        for (int i = 0; i < 5; i++) {
            double a = i * Math.PI * 2.0 / 5.0;
            mc.world.addParticle(dust,
                    target.getX(), target.getY() + target.getHeight() * 0.55, target.getZ(),
                    Math.cos(a) * 0.04, 0.03, Math.sin(a) * 0.04);
        }
    }

    private void spawnKillEffect(LivingEntity target) {
        try {
            String style = killEffectStyle.getValue();
            if ("EXPLOSION".equalsIgnoreCase(style)) {
                mc.world.addParticle(net.minecraft.particle.ParticleTypes.EXPLOSION,
                        target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ(),
                        0.0, 0.0, 0.0);
            } else if ("HEART".equalsIgnoreCase(style)) {
                for (int i = 0; i < 4; i++) {
                    mc.world.addParticle(net.minecraft.particle.ParticleTypes.HEART,
                            target.getX(), target.getY() + 0.8 + i * 0.12, target.getZ(),
                            (i - 1.5) * 0.02, 0.03, (1.5 - i) * 0.02);
                }
            } else {
                mc.world.addParticle(net.minecraft.particle.ParticleTypes.END_ROD,
                        target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ(),
                        0.0, 0.25, 0.0);
            }
        } catch (Throwable ignored) {
        }
    }

    public static final class KillEntry {
        public final String name;
        public final long time;
        public final boolean critical;
        KillEntry(String name, long time, boolean critical) {
            this.name = name;
            this.time = time;
            this.critical = critical;
        }
    }
}
