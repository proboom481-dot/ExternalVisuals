package com.externalvisuals.setting;

public class SliderSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(
            String name,
            double defaultValue,
            double min,
            double max,
            double step
    ) {
        super(
                name,
                clamp(defaultValue, min, max)
        );

        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public void setSliderValue(double value) {
        super.setValue(
                roundToStep(
                        clamp(
                                value,
                                min,
                                max
                        )
                )
        );
    }

    private static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(
                min,
                Math.min(
                        value,
                        max
                )
        );
    }

    private double roundToStep(double value) {

        if (step <= 0.0) {
            return value;
        }

        double steps =
                Math.round(
                        (value - min) / step
                );

        return clamp(
                min + steps * step,
                min,
                max
        );
    }

    public double getPercentage() {

        if (max <= min) {
            return 0.0;
        }

        return (
                getValue() - min
        ) / (
                max - min
        );
    }
}