package com.externalvisuals.setting;

public class StringSetting extends Setting<String> {

    private final String[] options;

    public StringSetting(
            String name,
            String defaultValue,
            String... options
    ) {
        super(name, defaultValue);
        this.options = options;
    }

    public String[] getOptions() {
        return options;
    }

    public void cycle() {

        if (options.length == 0) {
            return;
        }

        int current = -1;

        for (int i = 0; i < options.length; i++) {

            if (options[i].equalsIgnoreCase(getValue())) {
                current = i;
                break;
            }
        }

        int next = current + 1;

        if (next >= options.length) {
            next = 0;
        }

        setValue(options[next]);
    }

    public boolean is(String option) {

        return getValue()
                .equalsIgnoreCase(option);
    }
}