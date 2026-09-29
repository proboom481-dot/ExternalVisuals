package com.externalvisuals.setting;
public class SliderSetting extends Setting<Double> {
    public final double min, max, step;
    public SliderSetting(String name, double value, double min, double max, double step) {
        super(name, value); this.min=min; this.max=max; this.step=step;
    }
    public void add(double amount) {
        double v = Math.max(min, Math.min(max, value + amount));
        value = Math.round(v / step) * step;
    }
}
