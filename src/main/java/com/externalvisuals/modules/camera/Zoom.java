package com.externalvisuals.modules.camera;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {

    private final SliderSetting zoomFov =
            addSetting(
                    new SliderSetting(
                            "Zoom FOV",
                            30.0,
                            5.0,
                            70.0,
                            1.0
                    )
            );

    private final BooleanSetting smooth =
            addSetting(
                    new BooleanSetting(
                            "Smooth",
                            true
                    )
            );

    private final SliderSetting smoothSpeed =
            addSetting(
                    new SliderSetting(
                            "Smooth Speed",
                            0.25,
                            0.05,
                            1.0,
                            0.05
                    )
            );

    private double previousFov;
    private double currentFov;

    public Zoom() {

        super(
                "Zoom",
                ModuleCategory.CAMERA,
                GLFW.GLFW_KEY_UNKNOWN
        );
    }

    @Override
    public void onEnable() {

        previousFov = mc.options.fov;

        currentFov = previousFov;
    }

    @Override
    public void onDisable() {

        if (smooth.isEnabled()) {

            currentFov =
                    previousFov;
        }

        mc.options.fov =
                previousFov;
    }

    @Override
    public void onTick() {

        if (!isEnabled()) {
            return;
        }

        double targetFov =
                zoomFov.getValue();

        if (!smooth.isEnabled()) {

            currentFov =
                    targetFov;

        } else {

            double speed =
                    smoothSpeed.getValue();

            currentFov +=
                    (
                            targetFov
                                    - currentFov
                    )
                            * speed;
        }

        mc.options.fov =
                (int) Math.round(
                        currentFov
                );
    }

    public double getZoomFov() {
        return zoomFov.getValue();
    }

    public boolean isSmooth() {
        return smooth.isEnabled();
    }

    public double getSmoothSpeed() {
        return smoothSpeed.getValue();
    }

    public SliderSetting getZoomFovSetting() {
        return zoomFov;
    }

    public BooleanSetting getSmoothSetting() {
        return smooth;
    }

    public SliderSetting getSmoothSpeedSetting() {
        return smoothSpeed;
    }
}