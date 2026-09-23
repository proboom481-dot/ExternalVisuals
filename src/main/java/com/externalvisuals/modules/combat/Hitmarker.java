package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public class Hitmarker extends Module {

    private int timer;

    private final BooleanSetting sound =
            addSetting(
                    new BooleanSetting(
                            "Sound",
                            true
                    )
            );

    private final SliderSetting size =
            addSetting(
                    new SliderSetting(
                            "Size",
                            8.0,
                            2.0,
                            20.0,
                            1.0
                    )
            );

    private final SliderSetting thickness =
            addSetting(
                    new SliderSetting(
                            "Thickness",
                            2.0,
                            1.0,
                            5.0,
                            1.0
                    )
            );

    private final SliderSetting duration =
            addSetting(
                    new SliderSetting(
                            "Duration",
                            8.0,
                            1.0,
                            30.0,
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

    public Hitmarker() {

        super(
                "Hitmarker",
                ModuleCategory.COMBAT,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    /**
     * Вызывается только после подтверждённого
     * нанесения урона.
     */
    public void trigger() {

        if (!isEnabled()) {
            return;
        }

        timer = getDuration();

        /*
         * Звук пока оставляем как настройку.
         * Сам звук можно подключить отдельно,
         * не смешивая его с системой определения попадания.
         */
    }

    @Override
    public void onTick() {

        if (!isEnabled()) {
            timer = 0;
            return;
        }

        if (timer > 0) {
            timer--;
        }
    }

    public boolean isVisible() {

        return isEnabled()
                && timer > 0;
    }

    /**
     * Прогресс от 1.0 в момент попадания
     * до 0.0 перед исчезновением.
     */
    public float getProgress() {

        int max =
                getDuration();

        if (max <= 0) {
            return 0.0f;
        }

        return Math.max(
                0.0f,
                Math.min(
                        1.0f,
                        timer / (float) max
                )
        );
    }

    /**
     * Более плавный fade.
     */
    public float getFade() {

        float progress =
                getProgress();

        return progress * progress;
    }

    public int getRemainingTicks() {

        return timer;
    }

    public int getSize() {

        return Math.max(
                2,
                (int) Math.round(
                        size.getValue()
                )
        );
    }

    public void setSize(int value) {

        size.setSliderValue(
                value
        );
    }

    public int getThickness() {

        return Math.max(
                1,
                (int) Math.round(
                        thickness.getValue()
                )
        );
    }

    public void setThickness(int value) {

        thickness.setSliderValue(
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

    public boolean isSoundEnabled() {

        return sound.isEnabled();
    }

    public void setSoundEnabled(
            boolean enabled
    ) {

        sound.setEnabled(
                enabled
        );
    }

    public BooleanSetting getSoundSetting() {

        return sound;
    }

    public SliderSetting getSizeSetting() {

        return size;
    }

    public SliderSetting getThicknessSetting() {

        return thickness;
    }

    public SliderSetting getDurationSetting() {

        return duration;
    }

    public ColorSetting getColorSetting() {

        return color;
    }
}