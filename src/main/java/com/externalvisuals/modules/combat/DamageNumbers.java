package com.externalvisuals.modules.combat;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DamageNumbers extends Module {

    public static class DamageEntry {

        private final Entity target;
        private final float damage;

        private int age;
        private final int maxAge;

        private float offsetY;

        public DamageEntry(
                Entity target,
                float damage,
                int maxAge
        ) {
            this.target = target;
            this.damage = damage;
            this.maxAge = Math.max(1, maxAge);

            this.age = 0;
            this.offsetY = 0.0f;
        }

        public Entity getTarget() {
            return target;
        }

        public float getDamage() {
            return damage;
        }

        public int getAge() {
            return age;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public float getOffsetY() {
            return offsetY;
        }

        public void tick(float riseSpeed) {
            if (age < maxAge) {
                age++;
            }
            offsetY += Math.max(0.001f, riseSpeed);
        }

        public void tick() { tick(0.025f); }

        public boolean isAlive() {
            return age < maxAge;
        }

        public float getProgress() {
            if (maxAge <= 0) {
                return 0.0f;
            }

            return Math.max(
                    0.0f,
                    Math.min(
                            1.0f,
                            age / (float) maxAge
                    )
            );
        }

        public float getFade() {
            /*
             * Начало — полностью видно.
             * Конец — исчезает плавно.
             */
            float progress = getProgress();

            float fade =
                    1.0f - progress;

            return Math.max(
                    0.0f,
                    Math.min(
                            1.0f,
                            fade * fade
                    )
            );
        }
    }

    private final List<DamageEntry> entries =
            new ArrayList<DamageEntry>();

    /*
     * Сколько тиков живёт число.
     */
    private final SliderSetting duration =
            addSetting(
                    new SliderSetting(
                            "Duration",
                            25.0,
                            1.0,
                            100.0,
                            1.0
                    )
            );

    /*
     * Размер текста.
     */
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

    /*
     * Максимальное количество одновременно
     * отображаемых чисел.
     */
    private final SliderSetting maxEntries =
            addSetting(
                    new SliderSetting(
                            "Max Entries",
                            8.0,
                            1.0,
                            30.0,
                            1.0
                    )
            );

    /*
     * Показывать тень.
     */
    private final BooleanSetting shadow =
            addSetting(
                    new BooleanSetting(
                            "Shadow",
                            true
                    )
            );

    /*
     * Отдельный цвет для больших ударов.
     */
    private final BooleanSetting criticalColor =
            addSetting(
                    new BooleanSetting(
                            "Critical Color",
                            true
                    )
            );

    /*
     * Порог, начиная с которого удар считается большим.
     */
    private final SliderSetting criticalThreshold =
            addSetting(
                    new SliderSetting(
                            "Critical Threshold",
                            4.0,
                            0.5,
                            20.0,
                            0.5
                    )
            );

    private final ColorSetting color =
            addSetting(
                    new ColorSetting(
                            "Color",
                            0xFFFFFFFF
                    )
            );

    private final ColorSetting criticalHitColor =
            addSetting(
                    new ColorSetting(
                            "Critical Hit Color",
                            0xFFFF5555
                    )
            );


    private final BooleanSetting critScale = addSetting(new BooleanSetting("Crit Scale", true));
    private final SliderSetting riseSpeed = addSetting(new SliderSetting("Rise Speed", 0.025, 0.005, 0.08, 0.005));
    private final SliderSetting spread = addSetting(new SliderSetting("Spread", 0.20, 0.0, 1.0, 0.05));
    private final BooleanSetting outline = addSetting(new BooleanSetting("Outline", true));
    private final ColorSetting criticalNumberColor = addSetting(new ColorSetting("Critical Number Color", 0xFFFFD84D));
    private final BooleanSetting pulse = addSetting(new BooleanSetting("Pulse", true));
    private final SliderSetting pulseSpeed = addSetting(new SliderSetting("Pulse Speed", 4.0, 0.5, 10.0, 0.5));
    private final BooleanSetting compact = addSetting(new BooleanSetting("Compact", false));
    private final BooleanSetting showPlus = addSetting(new BooleanSetting("Show Plus", false));

    public DamageNumbers() {
        super(
                "Damage Numbers",
                ModuleCategory.COMBAT,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    /**
     * Добавляет новое число урона.
     */
    public void show(
            Entity target,
            float damage
    ) {

        if (!isEnabled()) {
            return;
        }

        if (target == null) {
            return;
        }

        if (damage <= 0.001f) {
            return;
        }

        int limit =
                getMaxEntries();

        /*
         * Не даём списку бесконечно расти.
         */
        while (entries.size() >= limit) {
            entries.remove(0);
        }

        entries.add(
                new DamageEntry(
                        target,
                        damage,
                        getDuration()
                )
        );
    }

    @Override
    public void onTick() {

        if (!isEnabled()) {
            entries.clear();
            return;
        }

        Iterator<DamageEntry> iterator =
                entries.iterator();

        while (iterator.hasNext()) {

            DamageEntry entry =
                    iterator.next();

            if (entry == null) {
                iterator.remove();
                continue;
            }

            Entity target =
                    entry.getTarget();

            if (target == null) {
                iterator.remove();
                continue;
            }

            /*
             * Не удаляем запись только потому, что цель умерла:
             * последний удар тоже должен успеть отрисоваться.
             * Запись всё равно будет удалена по таймеру.
             */
            entry.tick(riseSpeed.getValue().floatValue());

            if (!entry.isAlive()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onDisable() {
        entries.clear();
    }

    public List<DamageEntry> getEntries() {
        return Collections.unmodifiableList(
                entries
        );
    }

    public boolean hasEntries() {
        return isEnabled()
                && !entries.isEmpty();
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
        duration.setSliderValue(value);
    }

    public float getSize() {
        return Math.max(
                0.5f,
                size.getValue().floatValue()
        );
    }

    public void setSize(float value) {
        size.setSliderValue(value);
    }

    public int getMaxEntries() {
        return Math.max(
                1,
                (int) Math.round(
                        maxEntries.getValue()
                )
        );
    }

    public void setMaxEntries(int value) {
        maxEntries.setSliderValue(value);
    }

    public boolean isShadowEnabled() {
        return shadow.isEnabled();
    }

    public boolean isCriticalColorEnabled() {
        return criticalColor.isEnabled();
    }

    public double getCriticalThreshold() {
        return Math.max(
                0.5,
                criticalThreshold.getValue()
        );
    }

    public int getColor() {
        return color.getColor();
    }

    public void setColor(int value) {
        color.setColor(value);
    }

    public int getCriticalHitColor() {
        return criticalHitColor.getColor();
    }

    public void setCriticalHitColor(int value) {
        criticalHitColor.setColor(value);
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

    public BooleanSetting getShadowSetting() {
        return shadow;
    }

    public BooleanSetting getCriticalColorSetting() {
        return criticalColor;
    }

    public SliderSetting getDurationSetting() {
        return duration;
    }

    public SliderSetting getSizeSetting() {
        return size;
    }

    public SliderSetting getMaxEntriesSetting() {
        return maxEntries;
    }

    public SliderSetting getCriticalThresholdSetting() {
        return criticalThreshold;
    }

    public ColorSetting getColorSetting() {
        return color;
    }

    public ColorSetting getCriticalHitColorSetting() { return criticalHitColor; }
    public boolean isCritScaleEnabled() { return critScale.isEnabled(); }
    public float getRiseSpeed() { return riseSpeed.getValue().floatValue(); }
    public float getSpread() { return spread.getValue().floatValue(); }
    public boolean isOutlineEnabled() { return outline.isEnabled(); }
    public int getCriticalNumberColor() { return criticalNumberColor.getColor(); }
    public boolean isPulseEnabled() { return pulse.isEnabled(); }
    public float getPulseSpeed() { return pulseSpeed.getValue().floatValue(); }
    public boolean isCompact() { return compact.isEnabled(); }
    public boolean isShowPlus() { return showPlus.isEnabled(); }
}