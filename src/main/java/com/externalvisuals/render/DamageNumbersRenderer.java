package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.combat.DamageNumbers;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public final class DamageNumbersRenderer {

    private DamageNumbersRenderer() {
    }

    public static void init() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                context -> {

                    MinecraftClient mc =
                            MinecraftClient.getInstance();

                    if (mc == null) {
                        return;
                    }

                    if (mc.player == null) {
                        return;
                    }

                    if (mc.world == null) {
                        return;
                    }

                    if (ExternalVisuals.MODULE_MANAGER == null) {
                        return;
                    }

                    DamageNumbers damageNumbers =
                            ExternalVisuals.MODULE_MANAGER.get(
                                    DamageNumbers.class
                            );

                    if (damageNumbers == null) {
                        return;
                    }

                    if (!damageNumbers.isEnabled()) {
                        return;
                    }

                    if (!damageNumbers.hasEntries()) {
                        return;
                    }

                    render(
                            context.matrixStack(),
                            context.camera(),
                            damageNumbers
                    );
                }
        );
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            DamageNumbers damageNumbers
    ) {

        MinecraftClient mc =
                MinecraftClient.getInstance();

        if (mc.world == null) {
            return;
        }

        TextRenderer textRenderer =
                mc.textRenderer;

        Vec3d cameraPos =
                camera.getPos();

        VertexConsumerProvider.Immediate consumers =
                mc.getBufferBuilders()
                        .getEntityVertexConsumers();

        for (DamageNumbers.DamageEntry entry
                : damageNumbers.getEntries()) {

            if (entry == null) {
                continue;
            }

            Entity target =
                    entry.getTarget();

            if (target == null) {
                continue;
            }

            if (!target.isAlive()) {
                continue;
            }

            double x =
                    target.getX()
                            - cameraPos.x;

            double y =
                    target.getY()
                            + target.getHeight()
                            + 0.45
                            + entry.getOffsetY();

            double z =
                    target.getZ()
                            - cameraPos.z;

            float fade =
                    entry.getFade();

            fade =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    fade
                            )
                    );

            if (fade <= 0.001f) {
                continue;
            }

            int baseColor;

            if (damageNumbers.isCriticalColorEnabled()
                    && entry.getDamage()
                    >= damageNumbers.getCriticalThreshold()) {

                baseColor =
                        damageNumbers.getCriticalHitColor();

            } else {

                baseColor =
                        damageNumbers.getColor();
            }

            int alpha =
                    Math.round(
                            damageNumbers.getAlpha()
                                    * fade
                    );

            alpha =
                    Math.max(
                            0,
                            Math.min(
                                    255,
                                    alpha
                            )
                    );

            if (alpha <= 0) {
                continue;
            }

            int color =
                    (alpha << 24)
                            | (baseColor & 0x00FFFFFF);

            String text =
                    formatDamage(
                            entry.getDamage()
                    );

            int width =
                    textRenderer.getWidth(text);

            float scale =
                    0.025f
                            * damageNumbers.getSize();

            if (scale <= 0.0f) {
                scale = 0.025f;
            }

            matrices.push();

            matrices.translate(
                    x,
                    y,
                    z
            );

            matrices.multiply(
                    camera.getRotation()
            );

            /*
             * Minecraft world text orientation.
             */
            matrices.scale(
                    -scale,
                    -scale,
                    scale
            );

            float textX =
                    -width / 2.0f;

            /*
             * Shadow.
             */
            if (damageNumbers.isShadowEnabled()) {

                int shadowAlpha =
                        Math.round(
                                alpha * 0.55f
                        );

                shadowAlpha =
                        Math.max(
                                0,
                                Math.min(
                                        255,
                                        shadowAlpha
                                )
                        );

                int shadowColor =
                        (shadowAlpha << 24);

                textRenderer.draw(
                        text,
                        textX + 1.0f,
                        1.0f,
                        shadowColor,
                        false,
                        matrices.peek().getModel(),
                        consumers,
                        false,
                        0,
                        15728880
                );
            }

            /*
             * Основной текст.
             */
            textRenderer.draw(
                    text,
                    textX,
                    0.0f,
                    color,
                    false,
                    matrices.peek().getModel(),
                    consumers,
                    false,
                    0,
                    15728880
            );

            matrices.pop();
        }

        consumers.draw();
    }

    private static String formatDamage(
            float damage
    ) {

        if (damage <= 0.0f) {
            return "0";
        }

        if (damage == (int) damage) {

            return Integer.toString(
                    (int) damage
            );
        }

        return String.format(
                Locale.US,
                "%.1f",
                damage
        );
    }
}