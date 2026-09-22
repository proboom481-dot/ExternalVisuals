package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Hitbox extends Module {

    private final SliderSetting expand =
            addSetting(
                    new SliderSetting(
                            "Expand",
                            0.0,
                            0.0,
                            1.0,
                            0.05
                    )
            );

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
                            0xFFFF5555
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

    private final BooleanSetting throughWalls =
            addSetting(
                    new BooleanSetting(
                            "Through Walls",
                            false
                    )
            );

    private final BooleanSetting showEyeLine =
            addSetting(
                    new BooleanSetting(
                            "Eye Line",
                            false
                    )
            );

    private final SliderSetting eyeLineLength =
            addSetting(
                    new SliderSetting(
                            "Eye Line Length",
                            1.0,
                            0.1,
                            3.0,
                            0.1
                    )
            );

    public Hitbox() {

        super(
                "Hitbox",
                ModuleCategory.VISUALS,
                0
        );
    }

    public double getExpand() {

        return Math.max(
                0.0,
                expand.getValue()
        );
    }

    public double getRange() {

        return Math.max(
                1.0,
                range.getValue()
        );
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

    public boolean isThroughWalls() {

        return throughWalls.isEnabled();
    }

    public boolean isShowEyeLine() {

        return showEyeLine.isEnabled();
    }

    public double getEyeLineLength() {

        return eyeLineLength.getValue();
    }

    public boolean shouldRender(Entity entity) {

        if (!isEnabled()) {
            return false;
        }

        if (entity == null) {
            return false;
        }

        if (mc.player == null) {
            return false;
        }

        if (entity == mc.player) {
            return false;
        }

        double range =
                getRange();

        if (mc.player.squaredDistanceTo(entity)
                > range * range) {

            return false;
        }

        if (entity instanceof PlayerEntity) {

            return players.isEnabled();
        }

        if (entity instanceof LivingEntity) {

            return mobs.isEnabled();
        }

        return otherEntities.isEnabled();
    }

    public SliderSetting getExpandSetting() {

        return expand;
    }

    public SliderSetting getRangeSetting() {

        return range;
    }

    public ColorSetting getColorSetting() {

        return color;
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

    public BooleanSetting getThroughWallsSetting() {

        return throughWalls;
    }

    public BooleanSetting getShowEyeLineSetting() {

        return showEyeLine;
    }

    public SliderSetting getEyeLineLengthSetting() {

        return eyeLineLength;
    }
}