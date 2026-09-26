package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import org.lwjgl.glfw.GLFW;

public class HitEffects extends Module {

    public enum Style {
        CRIT,
        SPARK,
        HEART,
        FLAME,
        STAR
    }

    private final StringSetting style =
            addSetting(
                    new StringSetting(
                            "Style",
                            "CRIT",
                            "CRIT",
                            "SPARK",
                            "HEART",
                            "FLAME",
                            "STAR"
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

    private final SliderSetting size =
            addSetting(
                    new SliderSetting(
                            "Size",
                            1.0,
                            0.25,
                            3.0,
                            0.25
                    )
            );

    private final SliderSetting duration =
            addSetting(
                    new SliderSetting(
                            "Duration",
                            12.0,
                            1.0,
                            40.0,
                            1.0
                    )
            );

    private final ColorSetting color =
            addSetting(
                    new ColorSetting(
                            "Color",
                            0xFF8B6CFF
                    )
            );


    private final BooleanSetting criticalOnly = addSetting(new BooleanSetting("Critical Only", false));
    private final BooleanSetting targetOnly = addSetting(new BooleanSetting("Target Only", false));
    private final SliderSetting burstScale = addSetting(new SliderSetting("Burst Scale", 1.0, 0.2, 3.0, 0.1));
    private final SliderSetting upward = addSetting(new SliderSetting("Upward", 0.05, 0.0, 0.30, 0.01));
    private final BooleanSetting fade = addSetting(new BooleanSetting("Fade", true));
    private final BooleanSetting flash = addSetting(new BooleanSetting("Flash", true));
    private final BooleanSetting ring = addSetting(new BooleanSetting("Ring", true));
    private final ColorSetting secondaryColor = addSetting(new ColorSetting("Secondary Color", 0xFFFFD84D));
    private final BooleanSetting rainbow = addSetting(new BooleanSetting("Rainbow", false));

    public HitEffects() {

        super(
                "Hit Effects",
                ModuleCategory.COMBAT,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    /**
     * Вызывается только после подтверждённого
     * нанесения урона.
     */
    public void trigger(Entity entity) {

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

        double x =
                entity.getX();

        double y =
                entity.getY()
                        + entity.getHeight()
                        * 0.55;

        double z =
                entity.getZ();

        int amount =
                Math.max(
                        1,
                        (int) Math.round(
                                count.getValue()
                        )
                );

        double effectSize =
                Math.max(
                        0.1,
                        size.getValue()
                );

        for (int i = 0;
             i < amount;
             i++) {

            double offsetX =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * effectSize;

            double offsetY =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * effectSize;

            double offsetZ =
                    (
                            mc.world.random.nextDouble()
                                    - 0.5
                    ) * effectSize;

            double velocityX =
                    offsetX * 0.05;

            double velocityY =
                    Math.abs(offsetY)
                            * 0.05
                            + 0.04;

            double velocityZ =
                    offsetZ * 0.05;

            switch (getStyle()) {

                case HEART:

                    mc.world.addParticle(
                            ParticleTypes.HEART,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            0.0,
                            0.05,
                            0.0
                    );

                    break;

                case FLAME:

                    mc.world.addParticle(
                            ParticleTypes.FLAME,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            velocityX,
                            velocityY,
                            velocityZ
                    );

                    break;

                case SPARK:

                    mc.world.addParticle(
                            ParticleTypes.ENCHANT,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            velocityX,
                            velocityY,
                            velocityZ
                    );

                    break;

                case STAR:

                    mc.world.addParticle(
                            ParticleTypes.FIREWORK,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            velocityX,
                            velocityY,
                            velocityZ
                    );

                    break;

                case CRIT:
                default:

                    mc.world.addParticle(
                            ParticleTypes.CRIT,
                            x + offsetX,
                            y + offsetY,
                            z + offsetZ,
                            velocityX,
                            velocityY,
                            velocityZ
                    );

                    break;
            }
        }
    }

    public Style getStyle() {

        try {

            return Style.valueOf(
                    style.getValue()
                            .toUpperCase()
            );

        } catch (Exception ignored) {

            return Style.CRIT;
        }
    }

    public void setStyle(
            Style value
    ) {

        if (value != null) {

            style.setValue(
                    value.name()
            );
        }
    }

    public int getCount() {

        return Math.max(
                1,
                (int) Math.round(
                        count.getValue()
                )
        );
    }

    public void setCount(int value) {

        count.setSliderValue(
                value
        );
    }

    public float getSize() {

        return size.getValue()
                .floatValue();
    }

    public void setSize(float value) {

        size.setSliderValue(
                value
        );
    }

    public int getDuration() {

        return Math.max(
                1,
                (int) Math.round(
                        duration.getValue()
                )
        );
    }

    public void setDuration(int value) {

        duration.setSliderValue(
                value
        );
    }

    public int getColor() {

        return color.getColor();
    }

    public void setColor(int value) {

        color.setColor(
                value
        );
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

    public StringSetting getStyleSetting() {
        return style;
    }

    public SliderSetting getCountSetting() {
        return count;
    }

    public SliderSetting getSizeSetting() {
        return size;
    }

    public SliderSetting getDurationSetting() {
        return duration;
    }

    public ColorSetting getColorSetting() {
        return color;
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