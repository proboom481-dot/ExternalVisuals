package com.externalvisuals.setting;

public class ColorSetting extends Setting<Integer> {

    private boolean rainbow;
    private float rainbowSpeed = 0.08f;

    public ColorSetting(
            String name,
            int defaultColor
    ) {
        super(name, defaultColor);
        this.rainbow = false;
    }

    /**
     * Returns the current display color. When Rainbow is enabled the hue
     * animates client-side without destroying the user's stored base color.
     */
    public int getColor() {
        if (!rainbow) {
            return getBaseColor();
        }

        float hue =
                (System.currentTimeMillis() * rainbowSpeed / 1000.0f)
                        % 1.0f;

        if (hue < 0.0f) {
            hue += 1.0f;
        }

        int rgb = hsbToRgb(hue, 0.88f, 1.0f);
        return (getBaseColor() & 0xFF000000) | (rgb & 0x00FFFFFF);
    }

    public int getBaseColor() {
        Integer value = getValue();
        return value == null ? 0xFFFFFFFF : value;
    }

    public void setColor(int color) {
        setValue(color);
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public float getRainbowSpeed() {
        return rainbowSpeed;
    }

    public void setRainbowSpeed(float speed) {
        rainbowSpeed = Math.max(
                0.01f,
                Math.min(1.0f, speed)
        );
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
                        | (getBaseGreen() << 8)
                        | getBaseBlue()
        );
    }

    public void setGreen(int green) {
        green = clamp(green);

        setColor(
                (getAlpha() << 24)
                        | (getBaseRed() << 16)
                        | (green << 8)
                        | getBaseBlue()
        );
    }

    public void setBlue(int blue) {
        blue = clamp(blue);

        setColor(
                (getAlpha() << 24)
                        | (getBaseRed() << 16)
                        | (getBaseGreen() << 8)
                        | blue
        );
    }

    public void setAlpha(int alpha) {
        alpha = clamp(alpha);

        setColor(
                (alpha << 24)
                        | (getBaseRed() << 16)
                        | (getBaseGreen() << 8)
                        | getBaseBlue()
        );
    }

    private int getBaseRed() {
        return (getBaseColor() >> 16) & 0xFF;
    }

    private int getBaseGreen() {
        return (getBaseColor() >> 8) & 0xFF;
    }

    private int getBaseBlue() {
        return getBaseColor() & 0xFF;
    }

    private int clamp(int value) {
        return Math.max(
                0,
                Math.min(255, value)
        );
    }

    private static int hsbToRgb(
            float hue,
            float saturation,
            float brightness
    ) {
        float h = (hue - (float) Math.floor(hue)) * 6.0f;
        int sector = (int) Math.floor(h);
        float fraction = h - sector;

        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * fraction);
        float t = brightness * (1.0f - saturation * (1.0f - fraction));

        float r;
        float g;
        float b;

        switch (sector) {
            case 0:
                r = brightness;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = brightness;
                b = p;
                break;
            case 2:
                r = p;
                g = brightness;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = brightness;
                break;
            case 4:
                r = t;
                g = p;
                b = brightness;
                break;
            default:
                r = brightness;
                g = p;
                b = q;
                break;
        }

        return ((int) (r * 255.0f) << 16)
                | ((int) (g * 255.0f) << 8)
                | (int) (b * 255.0f);
    }
}
