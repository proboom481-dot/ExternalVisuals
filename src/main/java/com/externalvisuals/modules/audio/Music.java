package com.externalvisuals.modules.audio;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/** Local-resource music player. Put a licensed OGG at the documented resource path. */
public final class Music extends Module {
    public static final Identifier TRACK_ID = new Identifier("externalvisuals", "music/voruem_alcohol_slowed");
    private static final SoundEvent TRACK = new SoundEvent(TRACK_ID);

    private final BooleanSetting autoplay = addSetting(new BooleanSetting("Auto Play", false));
    private final BooleanSetting loop = addSetting(new BooleanSetting("Loop", true));
    private final SliderSetting volume = addSetting(new SliderSetting("Volume", 0.65, 0.0, 1.0, 0.01));
    private final SliderSetting pitch = addSetting(new SliderSetting("Pitch", 1.0, 0.5, 2.0, 0.01));
    private final BooleanSetting stream = addSetting(new BooleanSetting("Stream", true));
    private final BooleanSetting pauseOnMenu = addSetting(new BooleanSetting("Pause On Menu", true));
    private final BooleanSetting stopOnWorldLeave = addSetting(new BooleanSetting("Stop On World Leave", true));
    private final BooleanSetting fade = addSetting(new BooleanSetting("Fade", true));
    private final SliderSetting fadeSpeed = addSetting(new SliderSetting("Fade Speed", 0.05, 0.01, 0.2, 0.01));
    private final BooleanSetting remember = addSetting(new BooleanSetting("Remember Volume", true));
    private final StringSetting track = addSetting(new StringSetting("Track", "VORUEM_ALCOHOL_SLOWED", "VORUEM_ALCOHOL_SLOWED", "CUSTOM_RESOURCE"));
    private final BooleanSetting notify = addSetting(new BooleanSetting("Notify", true));
    private final BooleanSetting allowResourcePack = addSetting(new BooleanSetting("Allow Resource Pack", true));
    private final BooleanSetting autoResume = addSetting(new BooleanSetting("Auto Resume", true));
    private final BooleanSetting duckMaster = addSetting(new BooleanSetting("Duck Master", false));
    private final SliderSetting duckAmount = addSetting(new SliderSetting("Duck Amount", 0.5, 0.0, 1.0, 0.01));
    private final BooleanSetting visualizer = addSetting(new BooleanSetting("Visualizer", false));
    private final BooleanSetting beatPulse = addSetting(new BooleanSetting("Beat Pulse", false));
    private final SliderSetting beatStrength = addSetting(new SliderSetting("Beat Strength", 0.2, 0.0, 1.0, 0.01));
    private final BooleanSetting safeMode = addSetting(new BooleanSetting("Safe Mode", true));
    private boolean registered;
    private boolean playing;

    public Music() { super("Music", ModuleCategory.AUDIO, 0); }

    public static void initRegistry() {
        try {
            if (!Registry.SOUND_EVENT.containsId(TRACK_ID)) {
                Registry.register(Registry.SOUND_EVENT, TRACK_ID, TRACK);
            }
        } catch (Throwable ignored) {}
    }

    @Override public void onEnable() {
        registered = true;
        playing = false;
        if (autoplay.isEnabled()) play();
    }

    @Override public void onDisable() { stop(); }

    @Override public void onTick() {
        if (mc.world == null || mc.player == null) {
            if (stopOnWorldLeave.isEnabled()) stop();
            return;
        }
        if (pauseOnMenu.isEnabled() && mc.currentScreen != null) {
            if (playing) {
                stop();
            }
            return;
        }
        if (autoplay.isEnabled() && !playing) play();
    }

    public void play() {
        try {
            Identifier resource = new Identifier("externalvisuals", "sounds/music/voruem_alcohol_slowed.ogg");
            if (!mc.getResourceManager().containsResource(resource)) {
                // The track is intentionally not bundled without a licensed user-provided audio file.
                return;
            }
            mc.getSoundManager().play(new PositionedSoundInstance(
                    TRACK_ID,
                    net.minecraft.sound.SoundCategory.MUSIC,
                    volume.getValue().floatValue(),
                    pitch.getValue().floatValue(),
                    loop.isEnabled(),
                    0,
                    net.minecraft.client.sound.SoundInstance.AttenuationType.NONE,
                    0.0, 0.0, 0.0,
                    true
            ));
            playing = true;
        } catch (Throwable ignored) { playing = false; }
    }

    public void stop() {
        try { mc.getSoundManager().stopSounds(TRACK_ID, net.minecraft.sound.SoundCategory.MUSIC); } catch (Throwable ignored) {}
        playing = false;
    }

    public boolean isPlaying() { return playing; }
}
