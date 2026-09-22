package com.externalvisuals.setting;

public class ColorSetting extends Setting<Integer> {

    public ColorSetting(
            String name,
            int defaultColor
    ) {
        super(name, defaultColor);
    }

    public int getColor() {
        return getValue();
    }

    public void setColor(int color) {
        setValue(color);
    }

    public int getRed() {
        return (getColor() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (getColor() >> 8) & 0xFF;
    }

    public int getBlue() {
        return getColor() & 0xFF;
    }

    public int getAlpha() {
        return (getColor() >> 24) & 0xFF;
    }

    public void setRed(int red) {
        red = clamp(red);

        setColor(
                (getAlpha() << 24)
                        | (red << 16)
                        | (getGreen() << 8)
                        | getBlue()
        );
    }

    public void setGreen(int green) {
        green = clamp(green);

        setColor(
                (getAlpha() << 24)
                        | (getRed() << 16)
                        | (green << 8)
                        | getBlue()
        );
    }

    public void setBlue(int blue) {
        blue = clamp(blue);

        setColor(
                (getAlpha() << 24)
                        | (getRed() << 16)
                        | (getGreen() << 8)
                        | blue
        );
    }

    public void setAlpha(int alpha) {
        alpha = clamp(alpha);

        setColor(
                (alpha << 24)
                        | (getRed() << 16)
                        | (getGreen() << 8)
                        | getBlue()
        );
    }

    private int clamp(int value) {
        return Math.max(
                0,
                Math.min(
                        255,
                        value
                )
        );
    }
}