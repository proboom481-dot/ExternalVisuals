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
import com.externalvisuals.modules.hud.SpeedHud;
import com.externalvisuals.modules.hud.ServerHud;
import com.externalvisuals.modules.hud.BiomeHud;
import com.externalvisuals.modules.hud.MemoryHud;
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

        renderTextHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(WatermarkHud.class),
                getText(ExternalVisuals.MODULE_MANAGER.get(WatermarkHud.class))
        );

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

        renderPotionHud(
                matrices,
                mc,
                ExternalVisuals.MODULE_MANAGER.get(PotionHud.class)
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
        if (hud == null || !hud.isEnabled()) {
            return;
        }

        Entity entity =
                mc.targetedEntity;

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity target =
                (LivingEntity) entity;

        if (!target.isAlive()
                || mc.player.distanceTo(target) > hud.getRange()) {
            return;
        }

        String name =
                target.getDisplayName()
                        .getString();

        String health =
                String.format(
                        "%.1f / %.1f",
                        target.getHealth(),
                        target.getMaxHealth()
                );

        int width =
                Math.max(
                        150,
                        mc.textRenderer.getWidth(name) + 74
                );

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

            int height = 42;

            drawHudBackground(
                    matrices,
                    hud,
                    width,
                    height
            );

            mc.textRenderer.drawWithShadow(
                    matrices,
                    name,
                    10.0f,
                    6.0f,
                    hud.getColor()
            );

            mc.textRenderer.drawWithShadow(
                    matrices,
                    health,
                    10.0f,
                    19.0f,
                    0xFFD8D4E2
            );

            float progress =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    target.getHealth()
                                            / Math.max(
                                                    0.1f,
                                                    target.getMaxHealth()
                                            )
                            )
                    );

            int barX = 10;
            int barY = 33;
            int barWidth = width - 20;

            if (hud.isBackgroundEnabled()) {
                DrawableHelper.fill(
                        matrices,
                        barX,
                        barY,
                        barX + barWidth,
                        barY + 4,
                        0xFF24242C
                );
            }

            DrawableHelper.fill(
                    matrices,
                    barX,
                    barY,
                    barX + Math.round(
                            barWidth * progress
                    ),
                    barY + 4,
                    hud.getColor()
            );
        } finally {
            matrices.pop();
        }
    }
}
