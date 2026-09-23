package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class ESP extends Module {

    private final SliderSetting range =
            addSetting(
                    new SliderSetting(
                            "Range",
                            64.0,
                            4.0,
                            128.0,
                            1.0
                    )
            );

    private final ColorSetting color =
            addSetting(
                    new ColorSetting(
                            "Color",
                            0xFF9B5CFF
                    )
            );

    private final BooleanSetting players =
            addSetting(
                    new BooleanSetting(
                            "Players",
                            true
                    )
            );

    private final BooleanSetting mobs =
            addSetting(
                    new BooleanSetting(
                            "Mobs",
                            true
                    )
            );

    private final BooleanSetting otherEntities =
            addSetting(
                    new BooleanSetting(
                            "Other Entities",
                            false
                    )
            );

    private final BooleanSetting boxes =
            addSetting(
                    new BooleanSetting(
                            "Boxes",
                            true
                    )
            );

    private final BooleanSetting names =
            addSetting(
                    new BooleanSetting(
                            "Names",
                            true
                    )
            );

    private final BooleanSetting health =
            addSetting(
                    new BooleanSetting(
                            "Health",
                            true
                    )
            );

    private final BooleanSetting tracers =
            addSetting(
                    new BooleanSetting(
                            "Tracers",
                            false
                    )
            );

    public ESP() {
        super(
                "ESP",
                ModuleCategory.VISUALS,
                0
        );
    }

    public double getRange() {

        return Math.max(
                1.0,
                range.getValue()
        );
    }

    public ColorSetting getColorSetting() {
        return color;
    }

    public int getColor() {
        return color.getColor();
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

    public boolean showPlayers() {
        return players.isEnabled();
    }

    public boolean showMobs() {
        return mobs.isEnabled();
    }

    public boolean showOtherEntities() {
        return otherEntities.isEnabled();
    }

    public boolean showBoxes() {
        return boxes.isEnabled();
    }

    public boolean showNames() {
        return names.isEnabled();
    }

    public boolean showHealth() {
        return health.isEnabled();
    }

    public boolean showTracers() {
        return tracers.isEnabled();
    }

    public boolean shouldRender(
            Entity entity
    ) {

        if (!isEnabled()) {
            return false;
        }

        if (entity == null) {
            return false;
        }

        if (mc.world == null) {
            return false;
        }

        if (mc.player == null) {
            return false;
        }

        if (entity == mc.player) {
            return false;
        }

        double maxRange =
                getRange();

        if (mc.player.squaredDistanceTo(entity)
                > maxRange * maxRange) {

            return false;
        }

        if (entity instanceof PlayerEntity) {
            return showPlayers();
        }

        if (entity instanceof LivingEntity) {
            return showMobs();
        }

        return showOtherEntities();
    }

    public SliderSetting getRangeSetting() {
        return range;
    }

    public BooleanSetting getPlayersSetting() {
        return players;
    }

    public BooleanSetting getMobsSetting() {
        return mobs;
    }

    public BooleanSetting getOtherEntitiesSetting() {
        return otherEntities;
    }

    public BooleanSetting getBoxesSetting() {
        return boxes;
    }

    public BooleanSetting getNamesSetting() {
        return names;
    }

    public BooleanSetting getHealthSetting() {
        return health;
    }

    public BooleanSetting getTracersSetting() {
        return tracers;
    }
}