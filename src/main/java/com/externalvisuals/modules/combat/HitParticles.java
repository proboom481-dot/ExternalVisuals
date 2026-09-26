package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.lwjgl.glfw.GLFW;

public class HitParticles extends Module {

    private int cooldown;

    private final StringSetting type =
            addSetting(
                    new StringSetting(
                            "Type",
                            "CRIT",
                            "CRIT",
                            "DUST",
                            "ENCHANT",
                            "FLAME",
                            "HEART",
                            "FIREWORK"
                    )
            );

    private final SliderSetting count =
            addSetting(
                    new SliderSetting(
                            "Count",
                            8.0,
                            1.0,
                            30.0,
                            1.0
                    )
            );

    private final SliderSetting spread =
            addSetting(
                    new SliderSetting(
                            "Spread",
                            0.8,
                            0.1,
                            2.0,
                            0.1
                    )
            );

    private final SliderSetting speed =
            addSetting(
                    new SliderSetting(
                            "Speed",
                            0.05,
                            0.01,
                            0.20,
                            0.01
                    )
            );

    private final SliderSetting cooldownSetting =
            addSetting(
                    new SliderSetting(
                            "Cooldown",
                            2.0,
                            0.0,
                            20.0,
                            1.0
                    )
            );

    private final ColorSetting color =
            addSetting(
                    new ColorSetting(
                            "Color",
                            0xFFFFFFFF
                    )
            );


    private final BooleanSetting criticalOnly = addSetting(new BooleanSetting("Critical Only", false));
    private final BooleanSetting targetOnly = addSetting(new BooleanSetting("Target Only", false));
    private final SliderSetting size = addSetting(new SliderSetting("Size", 1.0, 0.5, 3.0, 0.1));
    private final SliderSetting lifetime = addSetting(new SliderSetting("Lifetime", 1.0, 0.1, 3.0, 0.1));
    private final BooleanSetting gravity = addSetting(new BooleanSetting("Gravity", true));
    private final BooleanSetting fade = addSetting(new BooleanSetting("Fade", true));
    private final BooleanSetting burst = addSetting(new BooleanSetting("Burst", true));
    private final BooleanSetting rainbow = addSetting(new BooleanSetting("Rainbow", false));
    private final ColorSetting secondaryColor = addSetting(new ColorSetting("Secondary Color", 0xFFFFD84D));

    public HitParticles() {

        super(
                "Hit Particles",
                ModuleCategory.COMBAT,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    @Override
    public void onTick() {

        if (cooldown > 0) {
            cooldown--;
        }
    }

    /**
     * Только визуальный эффект.
     *
     * Этот метод вызывается CombatEvents
     * после подтверждённого нанесения урона.
     */
    public void spawn(Entity entity) {

        if (!isEnabled()) {
            return;
        }

        if (entity == null) {
            return;
        }

        if (mc.world == null) {
            return;
        }

        if (mc.player == null) {
            return;
        }

        if (entity == mc.player) {
            return;
        }

        if (targetOnly.isEnabled() && mc.targetedEntity != entity) {
            return;
        }
        if (criticalOnly.isEnabled() && !isLikelyCritical()) {
            return;
        }

        if (cooldown > 0) {
            return;
        }

        ParticleManager particles =
                mc.particleManager;

        if (particles == null) {
            return;
        }

        double x =
                entity.getX();

        double y =
                entity.getY()
                        + entity.getHeight()
                        * 0.55;

        double z =
                entity.getZ();

        int particleCount = Math.max(1, (int) Math.round(count.getValue()));
        if (burst.isEnabled()) particleCount = Math.min(64, Math.round(particleCount * 1.5f));

        double particleSpread = Math.max(0.1, spread.getValue() * size.getValue());
        double particleSpeed = Math.max(0.005, speed.getValue() / Math.max(0.35, lifetime.getValue()));

        for (int i = 0;
             i < particleCount;
             i++) {

            double offsetX =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * particleSpread;

            double offsetY =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * particleSpread;

            double offsetZ =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * particleSpread;

            double velocityX =
                    offsetX * particleSpeed;

            double velocityY = gravity.isEnabled()
                    ? offsetY * particleSpeed + 0.02
                    : 0.0;

            double velocityZ =
                    offsetZ * particleSpeed;

            ParticleEffect effect = getParticleEffect(i, particleCount);
            particles.addParticle(
                    effect,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    velocityX,
                    velocityY,
                    velocityZ
            );
        }

        cooldown =
                Math.max(
                        0,
                        (int) Math.round(
                                cooldownSetting.getValue()
                        )
                );
    }

    private ParticleEffect getParticleEffect(int index, int total) {

        String value =
                type.getValue();

        if (value == null) {
            return ParticleTypes.CRIT;
        }

        switch (value.toUpperCase()) {

            case "DUST":
                if (rainbow.isEnabled()) {
                    float hue = (float) ((System.currentTimeMillis() * 0.0004 + index / (double) Math.max(1, total)) % 1.0);
                    return createDustFromRgb(java.awt.Color.HSBtoRGB(hue, 0.85f, 1.0f) & 0x00FFFFFF);
                }
                return createColoredDust(index % 2 == 1 && secondaryColor.getColor() != 0
                        ? secondaryColor.getColor()
                        : color.getColor());

            case "ENCHANT":
                return ParticleTypes.ENCHANT;

            case "FLAME":
                return ParticleTypes.FLAME;

            case "HEART":
                return ParticleTypes.HEART;

            case "FIREWORK":
                return ParticleTypes.FIREWORK;

            case "CRIT":
            default:
                return ParticleTypes.CRIT;
        }
    }

    private DustParticleEffect createColoredDust() {
        return createColoredDust(color.getColor());
    }

    private DustParticleEffect createColoredDust(int argb) {
        return createDustFromRgb(argb & 0x00FFFFFF);
    }

    private DustParticleEffect createDustFromRgb(int rgb) {
        float red = ((rgb >> 16) & 0xFF) / 255.0f;
        float green = ((rgb >> 8) & 0xFF) / 255.0f;
        float blue = (rgb & 0xFF) / 255.0f;
        return new DustParticleEffect(red, green, blue, (float) Math.max(0.5, Math.min(2.0, size.getValue())));
    }

    public StringSetting getTypeSetting() {
        return type;
    }

    public SliderSetting getCountSetting() {
        return count;
    }

    public SliderSetting getSpreadSetting() {
        return spread;
    }

    public SliderSetting getSpeedSetting() {
        return speed;
    }

    public SliderSetting getCooldownSetting() {
        return cooldownSetting;
    }

    public ColorSetting getColorSetting() {
        return color;
    }

    public int getColor() {
        return color.getColor();
    }

    public void setColor(int value) {
        color.setColor(value);
    }

    public int getRed() {
        return color.getRed();
    }

    public int getGreen() {
        return color.getGreen();
    }

    public int getBlue() {
        return color.getBlue();
    }

    public int getAlpha() {
        return color.getAlpha();
    }

    private boolean isLikelyCritical() {
        if (mc.player == null) {
            return false;
        }
        return !mc.player.isOnGround()
                && mc.player.fallDistance > 0.0f
                && !mc.player.isClimbing()
                && !mc.player.isTouchingWater()
                && !mc.player.hasVehicle()
                && !mc.player.isSprinting();
    }

}