package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.hud.ClockHud;
import com.externalvisuals.modules.hud.CoordinatesHud;
import com.externalvisuals.modules.hud.CpsHud;
import com.externalvisuals.modules.hud.FPSHud;
import com.externalvisuals.modules.hud.HudModule;
import com.externalvisuals.modules.hud.ItemCounterHud;
import com.externalvisuals.modules.hud.KeystrokesHud;
import com.externalvisuals.modules.hud.PingHud;
import com.externalvisuals.modules.hud.PotionHud;
import com.externalvisuals.modules.hud.ReachDisplayHud;
import com.externalvisuals.modules.hud.SessionHud;
import com.externalvisuals.modules.hud.TargetHud;
import com.externalvisuals.modules.hud.WatermarkHud;
import com.externalvisuals.modules.audio.Music;
import com.externalvisuals.modules.hud.SpeedHud;
import com.externalvisuals.modules.hud.ServerHud;
import com.externalvisuals.modules.hud.BiomeHud;
import com.externalvisuals.modules.hud.ArmorHud;
import com.externalvisuals.modules.hud.MemoryHud;
import com.externalvisuals.modules.hud.CompassHud;
import com.externalvisuals.modules.hud.EntityRadarHud;
import com.externalvisuals.modules.hud.TargetStatsHud;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class ExtraHudRenderer {

    private ExtraHudRenderer() {
    }

    public static void init() {
        HudRenderCallback.EVENT.register(
                ExtraHudRenderer::render
        );
    }

    private static void render(
            MatrixStack matrices,
            float tickDelta
    ) {
        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.player == null
                || mc.world == null
                || mc.currentScreen != null
                || ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(FPSHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(FPSHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(CoordinatesHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(CoordinatesHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(SessionHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(SessionHud.class))
        );

        WatermarkHud watermark = ExternalVisuals.MODULE_MANAGER.get(WatermarkHud.class);
        if (watermark != null && watermark.isDynamicIslandEnabled()) {
            renderDynamicIsland(matrices, mc, watermark);
        } else {
            renderTextHud(matrices, mc, watermark, getText(watermark));
        }

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(PingHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(PingHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(CpsHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(CpsHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(ClockHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(ClockHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(ItemCounterHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(ItemCounterHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(ReachDisplayHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(ReachDisplayHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(SpeedHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(SpeedHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(ServerHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(ServerHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(BiomeHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(BiomeHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(MemoryHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(MemoryHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(CompassHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(CompassHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(EntityRadarHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(EntityRadarHud.class))
        );

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(TargetStatsHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(TargetStatsHud.class))
        );

        renderPotionHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(PotionHud.class)
        );

        renderArmorHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(ArmorHud.class)
        );

        renderKeystrokes(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(KeystrokesHud.class)
        );

        renderTargetHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(TargetHud.class)
        );
    }

    private static String getText(HudModule hud) {
        if (hud instanceof FPSHud) {
            return ((FPSHud) hud).getText();
        }
        if (hud instanceof CoordinatesHud) {
            return ((CoordinatesHud) hud).getText();
        }
        if (hud instanceof SessionHud) {
            return ((SessionHud) hud).getText();
        }
        if (hud instanceof WatermarkHud) {
            return ((WatermarkHud) hud).getText();
        }
        if (hud instanceof PingHud) {
            return ((PingHud) hud).getText();
        }
        if (hud instanceof CpsHud) {
            return ((CpsHud) hud).getText();
        }
        if (hud instanceof ClockHud) {
            return ((ClockHud) hud).getText();
        }
        if (hud instanceof ItemCounterHud) {
            return ((ItemCounterHud) hud).getText();
        }
        if (hud instanceof ReachDisplayHud) {
            return ((ReachDisplayHud) hud).getText();
        }
        if (hud instanceof SpeedHud) {
            return ((SpeedHud) hud).getText();
        }
        if (hud instanceof ServerHud) {
            return ((ServerHud) hud).getText();
        }
        if (hud instanceof BiomeHud) {
            return ((BiomeHud) hud).getText();
        }
        if (hud instanceof MemoryHud) {
            return ((MemoryHud) hud).getText();
        }
        if (hud instanceof CompassHud) {
            return ((CompassHud) hud).getText();
        }
        if (hud instanceof EntityRadarHud) {
            return ((EntityRadarHud) hud).getText();
        }
        if (hud instanceof TargetStatsHud) {
            return ((TargetStatsHud) hud).getText();
        }
        return null;
    }

    private static void renderTextHud(
            MatrixStack matrices,
            MinecraftClient mc,
            HudModule hud,
            String text
    ) {
        if (hud == null || !hud.isEnabled()
                || text == null || text.isEmpty()) {
            return;
        }

        float scale = hud.getScale();

        matrices.push();

        try {
            matrices.translate(
                    hud.getX(),
                    hud.getY(),
                    0.0f
            );

            matrices.scale(
                    scale,
                    scale,
                    1.0f
            );

            int width =
                    mc.textRenderer.getWidth(text);

            int height = 14;

            drawHudBackground(
                    matrices,
                    hud,
                    width,
                    height
            );

            if (hud.isShadowEnabled()) {
                mc.textRenderer.drawWithShadow(
                        matrices,
                        text,
                        0.0f,
                        0.0f,
                        hud.getColor()
                );
            } else {
                mc.textRenderer.draw(
                        matrices,
                        text,
                        0.0f,
                        0.0f,
                        hud.getColor()
                );
            }
        } finally {
            matrices.pop();
        }
    }

    private static void drawHudBackground(
            MatrixStack matrices,
            HudModule hud,
            int width,
            int height
    ) {
        int padX = 6;
        int padY = 4;

        if (hud.isBackgroundEnabled()) {
            DrawableHelper.fill(
                    matrices,
                    -padX,
                    -padY,
                    width + padX,
                    height + padY,
                    0xB80A0A0F
            );
        }

        if (hud.isBorderEnabled()) {
            int borderColor =
                    hud.getColor();

            DrawableHelper.fill(
                    matrices,
                    -padX,
                    -padY,
                    -padX + 2,
                    height + padY,
                    borderColor
            );
        }
    }


    private static void renderDynamicIsland(MatrixStack matrices, MinecraftClient mc, WatermarkHud hud) {
        matrices.push();
        try {
            matrices.translate(hud.getX(), hud.getY(), 0.0f);
            matrices.scale(hud.getScale(), hud.getScale(), 1.0f);

            int width = hud.getIslandWidth();
            int height = hud.getIslandHeight();
            int accent = hud.getIslandAccent();
            int bg = "SOLID".equalsIgnoreCase(hud.getIslandStyle())
                    ? 0xE9101018 : 0xC70A0A12;

            DrawableHelper.fill(matrices, 0, 0, width, height, bg);
            DrawableHelper.fill(matrices, 0, 0, 3, height, accent);

            StringBuilder left = new StringBuilder("✦ ").append(ExternalVisuals.NAME);
            StringBuilder right = new StringBuilder();

            if (hud.isShowFps()) {
                String fps = parseFps(mc.fpsDebugString);
                if (!fps.isEmpty()) right.append(fps).append(" FPS");
            }

            if (hud.isShowPing() && mc.getNetworkHandler() != null && mc.player != null) {
                net.minecraft.client.network.PlayerListEntry entry =
                        mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (entry != null) {
                    if (right.length() > 0) right.append("  ");
                    right.append(entry.getLatency()).append("ms");
                }
            }

            Music music = ExternalVisuals.MODULE_MANAGER.get(Music.class);
            if (hud.isShowMusic() && music != null && music.isEnabled() && music.isPlaying()) {
                if (right.length() > 0) right.append("  ");
                right.append("♫ MUSIC");
            }

            int textColor = 0xFFF4F7FF;
            mc.textRenderer.drawWithShadow(matrices, left.toString(), 10.0f, 6.0f, textColor);
            int rightWidth = mc.textRenderer.getWidth(right.toString());
            mc.textRenderer.drawWithShadow(matrices, right.toString(), width - rightWidth - 10.0f, 6.0f, accent);

            if (hud.isShowModuleState()) {
                int enabled = 0;
                for (com.externalvisuals.module.Module module : ExternalVisuals.MODULE_MANAGER.getModules()) {
                    if (module.isEnabled()) enabled++;
                }
                String state = enabled + " active";
                mc.textRenderer.draw(matrices, state, 10.0f, height - 10.0f, 0xFF858FA8);
            }
        } finally {
            matrices.pop();
        }
    }

    private static String parseFps(String debug) {
        if (debug == null || debug.isEmpty()) return "";
        StringBuilder digits = new StringBuilder();
        boolean started = false;
        for (int i = 0; i < debug.length(); i++) {
            char c = debug.charAt(i);
            if (Character.isDigit(c)) {
                started = true;
                digits.append(c);
            } else if (started) {
                break;
            }
        }
        return digits.toString();
    }

    private static void renderPotionHud(
            MatrixStack matrices,
            MinecraftClient mc,
            PotionHud hud
    ) {
        if (hud == null || !hud.isEnabled()) {
            return;
        }

        String[] lines = hud.getLines();

        if (lines.length == 0) {
            return;
        }

        int width = 0;

        for (String line : lines) {
            width = Math.max(
                    width,
                    mc.textRenderer.getWidth(line)
            );
        }

        int height =
                22 + lines.length * 14;

        matrices.push();

        try {
            matrices.translate(
                    hud.getX(),
                    hud.getY(),
                    0.0f
            );

            matrices.scale(
                    hud.getScale(),
                    hud.getScale(),
                    1.0f
            );

            drawHudBackground(
                    matrices,
                    hud,
                    width + 12,
                    height
            );

            if (hud.isShadowEnabled()) {
                mc.textRenderer.drawWithShadow(
                        matrices,
                        "Effects",
                        6.0f,
                        4.0f,
                        hud.getColor()
                );
            } else {
                mc.textRenderer.draw(
                        matrices,
                        "Effects",
                        6.0f,
                        4.0f,
                        hud.getColor()
                );
            }

            int lineY = 18;

            for (String line : lines) {
                mc.textRenderer.drawWithShadow(
                        matrices,
                        line,
                        6.0f,
                        lineY,
                        0xFFD8D4E2
                );
                lineY += 14;
            }
        } finally {
            matrices.pop();
        }
    }

    private static void renderArmorHud(
            MatrixStack matrices,
            MinecraftClient mc,
            ArmorHud hud
    ) {
        if (hud == null || !hud.isEnabled()) return;
        String[] lines = hud.getLines();
        if (lines.length == 0) return;

        int width = 0;
        for (String line : lines) width = Math.max(width, mc.textRenderer.getWidth(line));
        int height = 22 + lines.length * 14;

        matrices.push();
        try {
            matrices.translate(hud.getX(), hud.getY(), 0.0f);
            matrices.scale(hud.getScale(), hud.getScale(), 1.0f);
            drawHudBackground(matrices, hud, width + 12, height);
            mc.textRenderer.drawWithShadow(matrices, "Armor", 6.0f, 4.0f, hud.getColor());
            int lineY = 18;
            for (String line : lines) {
                mc.textRenderer.drawWithShadow(matrices, line, 6.0f, lineY, 0xFFD8D4E2);
                lineY += 14;
            }
        } finally {
            matrices.pop();
        }
    }

    private static void renderKeystrokes(
            MatrixStack matrices,
            MinecraftClient mc,
            KeystrokesHud hud
    ) {
        if (hud == null || !hud.isEnabled()) {
            return;
        }

        int size = 20;
        int gap = 3;

        matrices.push();

        try {
            matrices.translate(
                    hud.getX(),
                    hud.getY(),
                    0.0f
            );

            matrices.scale(
                    hud.getScale(),
                    hud.getScale(),
                    1.0f
            );

            drawKey(
                    matrices,
                    mc,
                    "W",
                    size + gap,
                    0,
                    hud.forward(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "A",
                    0,
                    size + gap,
                    hud.left(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "S",
                    size + gap,
                    size + gap,
                    hud.back(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "D",
                    (size + gap) * 2,
                    size + gap,
                    hud.right(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "J",
                    0,
                    (size + gap) * 2,
                    hud.jump(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "L",
                    size + gap,
                    (size + gap) * 2,
                    hud.attack(),
                    hud
            );

            drawKey(
                    matrices,
                    mc,
                    "R",
                    (size + gap) * 2,
                    (size + gap) * 2,
                    hud.use(),
                    hud
            );
        } finally {
            matrices.pop();
        }
    }

    private static void drawKey(
            MatrixStack matrices,
            MinecraftClient mc,
            String text,
            int x,
            int y,
            boolean pressed,
            KeystrokesHud hud
    ) {
        int background =
                pressed
                        ? 0xFF342047
                        : 0xCC0A0A0F;

        if (hud.isBackgroundEnabled()) {
            DrawableHelper.fill(
                    matrices,
                    x,
                    y,
                    x + 20,
                    y + 20,
                    background
            );
        }

        if (hud.isBorderEnabled()) {
            DrawableHelper.fill(
                    matrices,
                    x,
                    y,
                    x + 20,
                    y + 2,
                    pressed
                            ? hud.getColor()
                            : 0x553A3A48
            );
        }

        int textWidth =
                mc.textRenderer.getWidth(text);

        mc.textRenderer.drawWithShadow(
                matrices,
                text,
                x + (20 - textWidth) / 2.0f,
                y + 6,
                pressed
                        ? hud.getColor()
                        : 0xFFD8D4E2
        );
    }

    private static void renderTargetHud(
            MatrixStack matrices,
            MinecraftClient mc,
            TargetHud hud
    ) {
        if (hud == null || !hud.isEnabled()) return;

        Entity entity = mc.targetedEntity;
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) entity;
        if (!target.isAlive() || mc.player.distanceTo(target) > hud.getRange()) return;

        String name = target.getDisplayName().getString();
        StringBuilder details = new StringBuilder();
        if (hud.isDistanceEnabled()) {
            details.append(String.format(java.util.Locale.US, "%.1fm", mc.player.distanceTo(target)));
        }
        if (hud.isArmorEnabled()) {
            int armorCount = 0;
            StringBuilder armor = new StringBuilder();
            for (net.minecraft.item.ItemStack stack : target.getArmorItems()) {
                if (!stack.isEmpty()) {
                    armorCount++;
                    if (hud.isArmorDetailsEnabled()) {
                        if (armor.length() > 0) armor.append(" ");
                        if (hud.isArmorDurabilityEnabled() && stack.isDamageable()) {
                            int max = Math.max(1, stack.getMaxDamage());
                            int remaining = Math.max(0, max - stack.getDamage());
                            armor.append(Math.round(remaining * 100.0f / max)).append("%");
                        } else {
                            String label = stack.getName().getString();
                            armor.append(label.length() > 7 ? label.substring(0, 7) : label);
                        }
                    }
                } else if (hud.isArmorDetailsEnabled()) {
                    if (armor.length() > 0) armor.append(" ");
                    armor.append("--");
                }
            }
            if (details.length() > 0) details.append("  •  ");
            details.append("Armor ").append(hud.isArmorDetailsEnabled() ? armor : armorCount + "/4");
        }
        if (hud.isHeldItemEnabled()) {
            net.minecraft.item.ItemStack held = target.getMainHandStack();
            if (!held.isEmpty()) {
                if (details.length() > 0) details.append("  •  ");
                details.append(held.getName().getString());
            }
        }

        String health = String.format(java.util.Locale.US, "%.1f / %.1f",
                target.getHealth(), target.getMaxHealth());
        int width = Math.max(170, Math.max(mc.textRenderer.getWidth(name) + 80,
                mc.textRenderer.getWidth(details.toString()) + 30));
        boolean showArmorBars = hud.isArmorEnabled() && hud.isArmorBarsEnabled();
        int height = hud.isHealthBarEnabled()
                ? (details.length() > 0 ? (showArmorBars ? 72 : 58) : 46)
                : (details.length() > 0 ? (showArmorBars ? 60 : 46) : 34);

        matrices.push();
        try {
            matrices.translate(hud.getX(), hud.getY(), 0.0f);
            matrices.scale(hud.getTargetScale(), hud.getTargetScale(), 1.0f);

            int accent = hud.getAccent();
            int background = hud.isBlurEnabled() ? 0xB80A0A10 : 0xE80A0A10;
            if (hud.isBackgroundEnabled()) {
                DrawableHelper.fill(matrices, 0, 0, width, height, background);
            }
            if (hud.isBorderEnabled()) {
                int borderColor = accent;
                if (hud.isPulseAccentEnabled()) {
                    float pulse = (float) (0.70 + 0.30 * (0.5 + 0.5 *
                            Math.sin(System.currentTimeMillis() * 0.006)));
                    int ar = Math.min(255, Math.round(((accent >> 16) & 255) * pulse));
                    int ag = Math.min(255, Math.round(((accent >> 8) & 255) * pulse));
                    int ab = Math.min(255, Math.round((accent & 255) * pulse));
                    borderColor = (accent & 0xFF000000) | (ar << 16) | (ag << 8) | ab;
                }
                DrawableHelper.fill(matrices, 0, 0, width, 2, borderColor);
            }

            int textColor = hud.getColor();
            float textY = 6.0f;
            if (hud.isHeadEnabled()) {
                // A compact target marker reserves the same visual space as a head icon
                // without relying on private renderer internals in 1.16.5.
                DrawableHelper.fill(matrices, 7, 7, 11, 11, accent);
                mc.textRenderer.drawWithShadow(matrices, name, 16.0f, textY, textColor);
            } else {
                mc.textRenderer.drawWithShadow(matrices, name, 10.0f, textY, textColor);
            }

            mc.textRenderer.drawWithShadow(matrices, health,
                    hud.isHeadEnabled() ? 16.0f : 10.0f, 19.0f, hud.getHealthColor());

            if (details.length() > 0) {
                mc.textRenderer.drawWithShadow(matrices, details.toString(),
                        10.0f, 32.0f, 0xFFD8D4E2);
            }

            if (showArmorBars) {
                renderArmorBars(matrices, target, 10, 45, width - 20);
            }

            if (hud.isHealthBarEnabled()) {
                float progress = Math.max(0.0f, Math.min(1.0f,
                        target.getHealth() / Math.max(0.1f, target.getMaxHealth())));
                int barY = height - 8;
                int barX = 10;
                int barWidth = width - 20;
                if (hud.isBackgroundEnabled()) {
                    DrawableHelper.fill(matrices, barX, barY, barX + barWidth, barY + 4, 0xFF24242C);
                }
                DrawableHelper.fill(matrices, barX, barY,
                        barX + Math.round(barWidth * progress), barY + 4, hud.getHealthColor());
            }
        } finally {
            matrices.pop();
        }
    }

    private static void renderArmorBars(MatrixStack matrices, LivingEntity target,
                                         int x, int y, int width) {
        int slotWidth = Math.max(12, (width - 9) / 4);
        int index = 0;
        for (net.minecraft.item.ItemStack stack : target.getArmorItems()) {
            int left = x + index * (slotWidth + 3);
            DrawableHelper.fill(matrices, left, y, left + slotWidth, y + 3, 0xFF252530);
            float progress = 0.0f;
            if (!stack.isEmpty()) {
                if (stack.isDamageable()) {
                    int max = Math.max(1, stack.getMaxDamage());
                    progress = Math.max(0.0f, Math.min(1.0f,
                            (max - stack.getDamage()) / (float) max));
                } else {
                    progress = 1.0f;
                }
            }
            int rr = Math.round((1.0f - progress) * 255.0f);
            int gg = Math.round(progress * 220.0f);
            int color = 0xFF000000 | (rr << 16) | (gg << 8) | 0x40;
            DrawableHelper.fill(matrices, left, y, left + Math.round(slotWidth * progress), y + 3, color);
            index++;
        }
    }

}
