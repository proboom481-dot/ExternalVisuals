package com.externalvisuals.gui;

public final class AMOLEDTheme {

    private AMOLEDTheme() {
    }

    /*
     * Основные AMOLED-цвета
     */

    public static final int BACKGROUND =
            0xF2000000;

    public static final int PANEL =
            0xE80A0A0F;

    public static final int PANEL_LIGHT =
            0xEE111118;

    public static final int PANEL_DARK =
            0xE6050508;

    public static final int BORDER =
            0x553A3A48;

    public static final int BORDER_LIGHT =
            0x884F4F63;

    public static final int DIVIDER =
            0x4430303A;

    /*
     * Фиолетовый акцент
     */

    public static final int ACCENT =
            0xFF9B5CFF;

    public static final int ACCENT_LIGHT =
            0xFFBE91FF;

    public static final int ACCENT_DARK =
            0xFF6D35C9;

    public static final int ACCENT_TRANSPARENT =
            0x559B5CFF;

    /*
     * Hover / selected
     */

    public static final int HOVER =
            0x3320202B;

    public static final int HOVER_ACCENT =
            0x443A245C;

    public static final int SELECTED =
            0x55301B4D;

    public static final int SELECTED_BORDER =
            0xAA9B5CFF;

    /*
     * Toggle
     */

    public static final int TOGGLE_OFF =
            0xFF292932;

    public static final int TOGGLE_ON =
            0xFF9B5CFF;

    public static final int TOGGLE_KNOB =
            0xFFFFFFFF;

    /*
     * Текст
     */

    public static final int TEXT =
            0xFFF5F3FA;

    public static final int TEXT_LIGHT =
            0xFFD8D4E2;

    public static final int TEXT_MUTED =
            0xFF85808F;

    public static final int TEXT_DISABLED =
            0xFF57535F;

    /*
     * Кнопки
     */

    public static final int BUTTON =
            0xFF18171F;

    public static final int BUTTON_HOVER =
            0xFF25212F;

    public static final int BUTTON_ACTIVE =
            0xFF342047;

    /*
     * Поля
     */

    public static final int INPUT =
            0xFF111017;

    public static final int INPUT_HOVER =
            0xFF191620;

    public static final int INPUT_BORDER =
            0x663A3544;

    /*
     * Glow
     */

    public static final int GLOW =
            0x339B5CFF;

    public static final int GLOW_STRONG =
            0x669B5CFF;

    /*
     * Анимация
     */

    public static final float HOVER_SPEED =
            0.18f;

    public static final float TOGGLE_SPEED =
            0.20f;

    public static final float SLIDER_SPEED =
            0.16f;

    public static final float PANEL_SPEED =
            0.14f;

    /*
     * Скругления.
     *
     * Пока хранятся как параметры темы.
     * Реальную отрисовку скруглений подключим
     * отдельным компонентом.
     */

    public static final float PANEL_RADIUS =
            10.0f;

    public static final float BUTTON_RADIUS =
            7.0f;

    public static final float TOGGLE_RADIUS =
            8.0f;

    public static final float INPUT_RADIUS =
            7.0f;

    /*
     * UI Scale
     */

    public static final float MIN_SCALE =
            0.50f;

    public static final float MAX_SCALE =
            1.50f;

    public static final float DEFAULT_SCALE =
            1.00f;

    public static float clampScale(
            float scale
    ) {

        if (Float.isNaN(scale)
                || Float.isInfinite(scale)) {

            return DEFAULT_SCALE;
        }

        return Math.max(
                MIN_SCALE,
                Math.min(
                        MAX_SCALE,
                        scale
                )
        );
    }

    /*
     * Плавное приближение значения.
     */

    public static float animate(
            float current,
            float target,
            float speed
    ) {

        if (Float.isNaN(current)
                || Float.isInfinite(current)) {

            return target;
        }

        if (Float.isNaN(target)
                || Float.isInfinite(target)) {

            return current;
        }

        if (Float.isNaN(speed)
                || Float.isInfinite(speed)) {

            speed = 0.15f;
        }

        speed =
                Math.max(
                        0.0f,
                        Math.min(
                                1.0f,
                                speed
                        )
                );

        return current
                + (target - current) * speed;
    }
}