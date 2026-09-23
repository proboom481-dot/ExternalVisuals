package com.externalvisuals.modules.world;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.TntEntity;
import org.lwjgl.glfw.GLFW;

public class TntTimer extends Module {

    private final SliderSetting size =
            addSetting(
                    new SliderSetting(
                            "Size",
                            1.0,
                            0.5,
                            3.0,
                            0.1
                    )
            );

    private final SliderSetting range =
            addSetting(
                    new SliderSetting(
                            "Range",
                            32.0,
                            4.0,
                            64.0,
                            1.0
                    )
            );

    private final BooleanSetting showSeconds =
            addSetting(
                    new BooleanSetting(
                            "Show Seconds",
                            true
                    )
            );

    private final BooleanSetting showTicks =
            addSetting(
                    new BooleanSetting(
                            "Show Ticks",
                            false
                    )
            );

    private final BooleanSetting dynamicColor =
            addSetting(
                    new BooleanSetting(
                            "Dynamic Color",
                            true
                    )
            );

    private final ColorSetting normalColor =
            addSetting(
                    new ColorSetting(
                            "Normal Color",
                            0xFFFFFFFF
                    )
            );

    private final ColorSetting dangerColor =
            addSetting(
                    new ColorSetting(
                            "Danger Color",
                            0xFFFF3030
                    )
            );

    private final ColorSetting warningColor =
            addSetting(
                    new ColorSetting(
                            "Warning Color",
                            0xFFFFAA30
                    )
            );

    private final SliderSetting dangerTime =
            addSetting(
                    new SliderSetting(
                            "Danger Time",
                            1.0,
                            0.1,
                            3.0,
                            0.1
                    )
            );

    private final SliderSetting warningTime =
            addSetting(
                    new SliderSetting(
                            "Warning Time",
                            2.0,
                            0.5,
                            5.0,
                            0.1
                    )
            );

    private final StringSetting format =
            addSetting(
                    new StringSetting(
                            "Format",
                            "SECONDS",
                            "SECONDS",
                            "TICKS",
                            "BOTH"
                    )
            );

    public TntTimer() {

        super(
                "TNT Timer",
                ModuleCategory.WORLD,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    /*
     * Проверяем, должна ли конкретная TNT
     * отображаться.
     */
    public boolean shouldRender(TntEntity tnt) {

        if (!isEnabled()) {
            return false;
        }

        if (tnt == null) {
            return false;
        }

        if (mc.player == null) {
            return false;
        }

        double maxRange =
                Math.max(
                        1.0,
                        range.getValue()
                );

        double distanceSquared =
                mc.player.squaredDistanceTo(tnt);

        return distanceSquared
                <= maxRange * maxRange;
    }

    /*
     * Получаем оставшееся время
     * в игровых тиках.
     */
    public int getFuseTicks(TntEntity tnt) {

        if (tnt == null) {
            return 0;
        }

        return Math.max(
                0,
                tnt.getFuse()
        );
    }

    /*
     * Получаем оставшееся время
     * в секундах.
     */
    public float getSeconds(TntEntity tnt) {

        return getFuseTicks(tnt)
                / 20.0f;
    }

    /*
     * Текст над TNT.
     */
    public String getText(TntEntity tnt) {

        if (tnt == null) {
            return "";
        }

        int ticks = getFuseTicks(tnt);
        float seconds = getSeconds(tnt);

        String selectedFormat = format.getValue();

        if (selectedFormat == null) {
            selectedFormat = "SECONDS";
        }

        if ("TICKS".equalsIgnoreCase(selectedFormat)) {
            return ticks + "t";
        }

        if ("BOTH".equalsIgnoreCase(selectedFormat)) {
            return String.format(
                    java.util.Locale.US,
                    "%.1fs %dt",
                    seconds,
                    ticks
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.1fs",
                seconds
        );
    }

    /*
     * Цвет таймера.
     *
     * Дальше:
     * normal -> warning -> danger.
     */
    public int getColor(TntEntity tnt) {

        if (tnt == null) {
            return normalColor.getColor();
        }

        if (!dynamicColor.isEnabled()) {
            return normalColor.getColor();
        }

        float seconds =
                getSeconds(tnt);

        double danger =
                dangerTime.getValue();

        double warning =
                warningTime.getValue();

        if (seconds <= danger) {

            return dangerColor.getColor();
        }

        if (seconds <= warning) {

            return warningColor.getColor();
        }

        return normalColor.getColor();
    }

    /*
     * Размер.
     */
    public float getSize() {

        return size.getValue()
                .floatValue();
    }

    public void setSize(float value) {

        size.setSliderValue(
                value
        );
    }

    /*
     * Дальность.
     */
    public double getRange() {

        return range.getValue();
    }

    public void setRange(double value) {

        range.setSliderValue(
                value
        );
    }

    /*
     * Настройки отображения.
     */
    public boolean isShowSeconds() {

        return showSeconds.isEnabled();
    }

    public boolean isShowTicks() {

        return showTicks.isEnabled();
    }

    public boolean isDynamicColor() {

        return dynamicColor.isEnabled();
    }

    public double getDangerTime() {

        return dangerTime.getValue();
    }

    public double getWarningTime() {

        return warningTime.getValue();
    }

    public String getFormat() {

        return format.getValue();
    }

    /*
     * Цвета.
     */
    public int getNormalColor() {

        return normalColor.getColor();
    }

    public int getDangerColor() {

        return dangerColor.getColor();
    }

    public int getWarningColor() {

        return warningColor.getColor();
    }

    /*
     * Settings getters.
     */
    public SliderSetting getSizeSetting() {

        return size;
    }

    public SliderSetting getRangeSetting() {

        return range;
    }

    public BooleanSetting getShowSecondsSetting() {

        return showSeconds;
    }

    public BooleanSetting getShowTicksSetting() {

        return showTicks;
    }

    public BooleanSetting getDynamicColorSetting() {

        return dynamicColor;
    }

    public ColorSetting getNormalColorSetting() {

        return normalColor;
    }

    public ColorSetting getDangerColorSetting() {

        return dangerColor;
    }

    public ColorSetting getWarningColorSetting() {

        return warningColor;
    }

    public SliderSetting getDangerTimeSetting() {

        return dangerTime;
    }

    public SliderSetting getWarningTimeSetting() {

        return warningTime;
    }

    public StringSetting getFormatSetting() {

        return format;
    }
}