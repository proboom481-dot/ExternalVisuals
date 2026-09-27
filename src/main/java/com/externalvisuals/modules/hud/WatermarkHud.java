package com.externalvisuals.modules.hud;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;

public final class WatermarkHud extends HudModule {

    private final BooleanSetting dynamicIsland = addSetting(new BooleanSetting("Dynamic Island", true));
    private final BooleanSetting showFps = addSetting(new BooleanSetting("Show FPS", true));
    private final BooleanSetting showPing = addSetting(new BooleanSetting("Show Ping", true));
    private final BooleanSetting showMusic = addSetting(new BooleanSetting("Show Music", true));
    private final BooleanSetting showModuleState = addSetting(new BooleanSetting("Show Module State", true));
    private final SliderSetting islandWidth = addSetting(new SliderSetting("Island Width", 190, 120, 360, 5));
    private final SliderSetting islandHeight = addSetting(new SliderSetting("Island Height", 26, 18, 48, 1));
    private final ColorSetting islandAccent = addSetting(new ColorSetting("Island Accent", 0xFF42D9FF));
    private final StringSetting islandStyle = addSetting(new StringSetting("Island Style", "GLASS", "GLASS", "SOLID", "MINIMAL"));

    public WatermarkHud() {
        super("Watermark", 0, 6.0f, 110.0f);
    }

    public String getText() {
        return ExternalVisuals.NAME + "  " + ExternalVisuals.VERSION;
    }

    public boolean isDynamicIslandEnabled() { return dynamicIsland.isEnabled(); }
    public boolean isShowFps() { return showFps.isEnabled(); }
    public boolean isShowPing() { return showPing.isEnabled(); }
    public boolean isShowMusic() { return showMusic.isEnabled(); }
    public boolean isShowModuleState() { return showModuleState.isEnabled(); }
    public int getIslandWidth() { return islandWidth.getValue().intValue(); }
    public int getIslandHeight() { return islandHeight.getValue().intValue(); }
    public int getIslandAccent() { return islandAccent.getColor(); }
    public String getIslandStyle() { return islandStyle.getValue(); }
}
