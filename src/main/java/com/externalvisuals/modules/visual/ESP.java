package com.externalvisuals.modules.visual;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public final class ESP extends Module {

    private final SliderSetting range = addSetting(new SliderSetting("Range", 64.0, 4.0, 128.0, 1.0));
    private final ColorSetting color = addSetting(new ColorSetting("Color", 0xFF9B5CFF));
    private final ColorSetting targetColor = addSetting(new ColorSetting("Target Color", 0xFFFF4FA3));
    private final BooleanSetting players = addSetting(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = addSetting(new BooleanSetting("Mobs", true));
    private final BooleanSetting otherEntities = addSetting(new BooleanSetting("Other Entities", false));
    private final BooleanSetting visibleOnly = addSetting(new BooleanSetting("Visible Only", false));
    private final BooleanSetting boxes = addSetting(new BooleanSetting("Boxes", true));
    private final StringSetting boxMode = addSetting(new StringSetting("Box Mode", "FULL", "FULL", "CORNERS"));
    private final BooleanSetting outline = addSetting(new BooleanSetting("Outline", true));
    private final BooleanSetting names = addSetting(new BooleanSetting("Names", true));
    private final BooleanSetting distance = addSetting(new BooleanSetting("Distance", true));
    private final BooleanSetting health = addSetting(new BooleanSetting("Health", true));
    private final BooleanSetting healthBar = addSetting(new BooleanSetting("Health Bar", true));
    private final BooleanSetting armor = addSetting(new BooleanSetting("Armor", true));
    private final BooleanSetting item = addSetting(new BooleanSetting("Held Item", true));
    private final BooleanSetting tracers = addSetting(new BooleanSetting("Tracers", true));
    private final BooleanSetting trail = addSetting(new BooleanSetting("Trail", true));
    private final SliderSetting trailLength = addSetting(new SliderSetting("Trail Length", 18.0, 4.0, 60.0, 1.0));
    private final SliderSetting trailFade = addSetting(new SliderSetting("Trail Fade", 1.0, 0.1, 2.0, 0.1));
    private final BooleanSetting targetHighlight = addSetting(new BooleanSetting("Target Highlight", true));
    private final BooleanSetting targetOnly = addSetting(new BooleanSetting("Target Only", false));


    private final BooleanSetting fill = addSetting(new BooleanSetting("Fill", true));
    private final SliderSetting fillAlpha = addSetting(new SliderSetting("Fill Alpha", 0.10, 0.0, 0.5, 0.01));
    private final SliderSetting cornerLength = addSetting(new SliderSetting("Corner Length", 0.32, 0.10, 0.50, 0.01));
    private final SliderSetting tracerAlpha = addSetting(new SliderSetting("Tracer Alpha", 0.75, 0.10, 1.0, 0.05));
    private final BooleanSetting pulseTarget = addSetting(new BooleanSetting("Pulse Target", true));
    private final BooleanSetting healthColor = addSetting(new BooleanSetting("Health Color", false));
    private final BooleanSetting showSneaking = addSetting(new BooleanSetting("Show Sneaking", true));
    private final BooleanSetting showInvisible = addSetting(new BooleanSetting("Show Invisible", true));
    private final BooleanSetting invisibleAlert = addSetting(new BooleanSetting("Invisible Alert", true));
    private final BooleanSetting fadeByDistance = addSetting(new BooleanSetting("Fade By Distance", true));
    private final BooleanSetting glow = addSetting(new BooleanSetting("Glow", true));
    private final BooleanSetting tracerFromCrosshair = addSetting(new BooleanSetting("Tracer From Crosshair", false));
    private final BooleanSetting trailVertical = addSetting(new BooleanSetting("Trail Vertical", true));
    private final SliderSetting targetPulseSpeed = addSetting(new SliderSetting("Target Pulse Speed", 8.0, 1.0, 20.0, 0.5));
    private final BooleanSetting infoBackground = addSetting(new BooleanSetting("Info Background", true));
    private final BooleanSetting compactInfo = addSetting(new BooleanSetting("Compact Info", false));
    private final SliderSetting infoOffset = addSetting(new SliderSetting("Info Offset", 0.4, 0.0, 1.5, 0.05));
    private final BooleanSetting totemCount = addSetting(new BooleanSetting("Totem Count", true));
    private final BooleanSetting totemPlayersOnly = addSetting(new BooleanSetting("Totem Players Only", true));
    private final BooleanSetting totemLabel = addSetting(new BooleanSetting("Totem Label", true));
    private final BooleanSetting targetTotemOnly = addSetting(new BooleanSetting("Target Totem Only", false));
    private final BooleanSetting safeMode = addSetting(new BooleanSetting("Safe Mode", true));
    private final SliderSetting renderBudget = addSetting(new SliderSetting("Render Budget", 96, 8, 256, 1));
    private final BooleanSetting cacheBoxes = addSetting(new BooleanSetting("Cache Boxes", true));
    private final BooleanSetting adaptiveDetail = addSetting(new BooleanSetting("Adaptive Detail", true));
    private final SliderSetting lowFpsDetail = addSetting(new SliderSetting("Low FPS Detail", 0.5, 0.1, 1.0, 0.05));
    private final BooleanSetting skipFarText = addSetting(new BooleanSetting("Skip Far Text", true));
    private final SliderSetting textRange = addSetting(new SliderSetting("Text Range", 48, 4, 128, 1));

    // Pulse-style target polish
    private final BooleanSetting pulseTargetBox = addSetting(new BooleanSetting("Pulse Target Box", true));
    private final SliderSetting pulseTargetSpeed = addSetting(new SliderSetting("Pulse Speed", 7.0, 1.0, 20.0, 0.5));
    private final SliderSetting pulseTargetAlpha = addSetting(new SliderSetting("Pulse Alpha", 0.30, 0.05, 0.80, 0.05));
    private final BooleanSetting targetRing = addSetting(new BooleanSetting("Target Ring", true));
    private final SliderSetting targetRingRadius = addSetting(new SliderSetting("Target Ring Radius", 0.72, 0.35, 1.6, 0.05));
    private final SliderSetting targetRingHeight = addSetting(new SliderSetting("Target Ring Height", 0.06, 0.0, 1.2, 0.05));
    private final SliderSetting targetRingSegments = addSetting(new SliderSetting("Target Ring Segments", 28, 8, 64, 1));
    private final BooleanSetting targetBeam = addSetting(new BooleanSetting("Target Beam", false));
    private final SliderSetting targetBeamAlpha = addSetting(new SliderSetting("Target Beam Alpha", 0.45, 0.10, 1.0, 0.05));
    private final BooleanSetting armorDetails = addSetting(new BooleanSetting("Armor Details", true));
    private final BooleanSetting armorDurability = addSetting(new BooleanSetting("Armor Durability", true));
    private final BooleanSetting targetInfo = addSetting(new BooleanSetting("Target Info", true));

    public ESP() {
        super("ESP", ModuleCategory.VISUALS, 0);
    }

    public double getRange() { return Math.max(1.0, range.getValue()); }
    public int getColor() { return color.getColor(); }
    public int getTargetColor() { return targetColor.getColor(); }
    public ColorSetting getColorSetting() { return color; }
    public ColorSetting getTargetColorSetting() { return targetColor; }
    public boolean showPlayers() { return players.isEnabled(); }
    public boolean showMobs() { return mobs.isEnabled(); }
    public boolean showOtherEntities() { return otherEntities.isEnabled(); }
    public boolean isVisibleOnly() { return visibleOnly.isEnabled(); }
    public boolean showBoxes() { return boxes.isEnabled(); }
    public boolean isOutline() { return outline.isEnabled(); }
    public boolean isCorners() { return boxMode.is("CORNERS"); }
    public boolean showNames() { return names.isEnabled(); }
    public boolean showDistance() { return distance.isEnabled(); }
    public boolean showHealth() { return health.isEnabled(); }
    public boolean showHealthBar() { return healthBar.isEnabled(); }
    public boolean showArmor() { return armor.isEnabled(); }
    public boolean showItem() { return item.isEnabled(); }
    public boolean showTracers() { return tracers.isEnabled(); }
    public boolean showTrail() { return trail.isEnabled(); }
    public int getTrailLength() { return Math.max(2, trailLength.getValue().intValue()); }
    public float getTrailFade() { return Math.max(0.1f, trailFade.getValue().floatValue()); }
    public boolean showTargetHighlight() { return targetHighlight.isEnabled(); }
    public boolean isTargetOnly() { return targetOnly.isEnabled(); }
    public boolean isFillEnabled() { return fill.isEnabled(); }
    public float getFillAlpha() { return Math.max(0.0f, Math.min(0.5f, fillAlpha.getValue().floatValue())); }
    public float getTracerAlpha() { return Math.max(0.05f, Math.min(1.0f, tracerAlpha.getValue().floatValue())); }
    public boolean isPulseTarget() { return pulseTarget.isEnabled(); }
    public boolean isHealthColor() { return healthColor.isEnabled(); }
    public boolean showSneaking() { return showSneaking.isEnabled(); }
    public boolean showInvisible() { return showInvisible.isEnabled(); }
    public boolean isInvisibleAlertEnabled() { return invisibleAlert.isEnabled(); }
    public boolean fadeByDistance() { return fadeByDistance.isEnabled(); }
    public boolean isGlowEnabled() { return glow.isEnabled(); }
    public boolean isTracerFromCrosshair() { return tracerFromCrosshair.isEnabled(); }
    public boolean isTrailVertical() { return trailVertical.isEnabled(); }
    public float getTargetPulseSpeed() { return targetPulseSpeed.getValue().floatValue(); }
    public boolean isInfoBackgroundEnabled() { return infoBackground.isEnabled(); }
    public boolean isCompactInfo() { return compactInfo.isEnabled(); }
    public float getInfoOffset() { return infoOffset.getValue().floatValue(); }
    public float getCornerLength() { return Math.max(0.10f, Math.min(0.50f, cornerLength.getValue().floatValue())); }
    public boolean showTotemCount() { return totemCount.isEnabled(); }
    public boolean isTotemPlayersOnly() { return totemPlayersOnly.isEnabled(); }
    public boolean isTotemLabel() { return totemLabel.isEnabled(); }
    public boolean isTargetTotemOnly() { return targetTotemOnly.isEnabled(); }
    public boolean isSafeMode() { return safeMode.isEnabled(); }
    public int getRenderBudget() { return Math.max(1, renderBudget.getValue().intValue()); }
    public boolean isAdaptiveDetail() { return adaptiveDetail.isEnabled(); }
    public float getLowFpsDetail() { return lowFpsDetail.getValue().floatValue(); }
    public boolean isSkipFarText() { return skipFarText.isEnabled(); }
    public double getTextRange() { return textRange.getValue(); }
    public boolean isPulseTargetBoxEnabled() { return pulseTargetBox.isEnabled(); }
    public float getPulseTargetSpeed() { return pulseTargetSpeed.getValue().floatValue(); }
    public float getPulseTargetAlpha() { return pulseTargetAlpha.getValue().floatValue(); }
    public boolean isTargetRingEnabled() { return targetRing.isEnabled(); }
    public float getTargetRingRadius() { return targetRingRadius.getValue().floatValue(); }
    public float getTargetRingHeight() { return targetRingHeight.getValue().floatValue(); }
    public int getTargetRingSegments() { return targetRingSegments.getValue().intValue(); }
    public boolean isTargetBeamEnabled() { return targetBeam.isEnabled(); }
    public float getTargetBeamAlpha() { return targetBeamAlpha.getValue().floatValue(); }
    public boolean isArmorDetailsEnabled() { return armorDetails.isEnabled(); }
    public boolean isArmorDurabilityEnabled() { return armorDurability.isEnabled(); }
    public boolean isTargetInfoEnabled() { return targetInfo.isEnabled(); }

    public boolean shouldRender(Entity entity) {
        if (!isEnabled() || entity == null || mc.world == null || mc.player == null || entity == mc.player) return false;
        double maxRange = getRange();
        if (mc.player.squaredDistanceTo(entity) > maxRange * maxRange) return false;
        if (!showSneaking() && entity.isSneaking()) return false;
        if (!showInvisible() && entity.isInvisible()) return false;
        if (isVisibleOnly() && !mc.player.canSee(entity)) return false;
        if (isTargetOnly() && mc.targetedEntity != entity) return false;
        if (entity instanceof PlayerEntity) return showPlayers();
        if (entity instanceof LivingEntity) return showMobs();
        return showOtherEntities();
    }

    public int getRed() { return (getColor() >> 16) & 255; }
    public int getGreen() { return (getColor() >> 8) & 255; }
    public int getBlue() { return getColor() & 255; }
    public int getAlpha() { return (getColor() >>> 24) & 255; }

    public SliderSetting getRangeSetting() { return range; }
    public ColorSetting getTargetColorSettingValue() { return targetColor; }
    public BooleanSetting getPlayersSetting() { return players; }
    public BooleanSetting getMobsSetting() { return mobs; }
    public BooleanSetting getOtherEntitiesSetting() { return otherEntities; }
    public BooleanSetting getVisibleOnlySetting() { return visibleOnly; }
    public BooleanSetting getBoxesSetting() { return boxes; }
    public StringSetting getBoxModeSetting() { return boxMode; }
    public BooleanSetting getOutlineSetting() { return outline; }
    public BooleanSetting getNamesSetting() { return names; }
    public BooleanSetting getDistanceSetting() { return distance; }
    public BooleanSetting getHealthSetting() { return health; }
    public BooleanSetting getHealthBarSetting() { return healthBar; }
    public BooleanSetting getArmorSetting() { return armor; }
    public BooleanSetting getItemSetting() { return item; }
    public BooleanSetting getTracersSetting() { return tracers; }
    public BooleanSetting getTrailSetting() { return trail; }
    public SliderSetting getTrailLengthSetting() { return trailLength; }
    public SliderSetting getTrailFadeSetting() { return trailFade; }
    public BooleanSetting getTargetHighlightSetting() { return targetHighlight; }
    public BooleanSetting getTargetOnlySetting() { return targetOnly; }
}
