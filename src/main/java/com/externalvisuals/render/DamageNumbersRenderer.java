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
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc == null
                    || mc.player == null
                    || mc.world == null
                    || ExternalVisuals.MODULE_MANAGER == null) {
                return;
            }

            DamageNumbers damageNumbers =
                    ExternalVisuals.MODULE_MANAGER.get(DamageNumbers.class);

            if (damageNumbers == null
                    || !damageNumbers.isEnabled()
                    || !damageNumbers.hasEntries()) {
                return;
            }

            VertexConsumerProvider consumers = context.consumers();

            if (consumers == null) {
                return;
            }

            try {
                render(
                        context.matrixStack(),
                        context.camera(),
                        consumers,
                        damageNumbers
                );
            } catch (Throwable throwable) {
                System.err.println(
                        "[ExternalVisuals] Damage Numbers renderer failed; disabling module."
                );
                throwable.printStackTrace();
                damageNumbers.setEnabled(false);
            }
        });
    }

    private static void render(
            MatrixStack matrices,
            Camera camera,
            VertexConsumerProvider consumers,
            DamageNumbers damageNumbers
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null || mc.textRenderer == null) {
            return;
        }

        TextRenderer textRenderer = mc.textRenderer;
        Vec3d cameraPos = camera.getPos();

        for (DamageNumbers.DamageEntry entry
                : damageNumbers.getEntries()) {

            if (entry == null) {
                continue;
            }

            Entity target = entry.getTarget();

            if (target == null || !target.isAlive()) {
                continue;
            }

            double x = target.getX() - cameraPos.x;
            double y = target.getY()
                    + target.getHeight()
                    + 0.45
                    + entry.getOffsetY();
            double z = target.getZ() - cameraPos.z;

            float fade = Math.max(
                    0.0f,
                    Math.min(1.0f, entry.getFade())
            );

            if (fade <= 0.001f) {
                continue;
            }

            int baseColor;

            if (damageNumbers.isCriticalColorEnabled()
                    && entry.getDamage()
                    >= damageNumbers.getCriticalThreshold()) {
                baseColor = damageNumbers.getCriticalHitColor();
            } else {
                baseColor = damageNumbers.getColor();
            }

            int alpha = Math.max(
                    0,
                    Math.min(
                            255,
                            Math.round(
                                    damageNumbers.getAlpha() * fade
                            )
                    )
            );

            if (alpha <= 0) {
                continue;
            }

            int color =
                    (alpha << 24)
                            | (baseColor & 0x00FFFFFF);

            String text = formatDamage(entry.getDamage());

            if (text.isEmpty()) {
                continue;
            }

            int width = textRenderer.getWidth(text);

            float scale =
                    Math.max(
                            0.0125f,
                            0.025f * damageNumbers.getSize()
                    );

            matrices.push();

            try {
                matrices.translate(x, y, z);
                matrices.multiply(camera.getRotation());
                matrices.scale(-scale, -scale, scale);

                float textX = -width / 2.0f;

                if (damageNumbers.isShadowEnabled()) {
                    int shadowAlpha =
                            Math.max(
                                    0,
                                    Math.min(
                                            255,
                                            Math.round(alpha * 0.55f)
                                    )
                            );

                    int shadowColor =
                            shadowAlpha << 24;

                    textRenderer.draw(
                            text,
                            textX + 1.0f,
                            1.0f,
                            shadowColor,
                            false,
                            matrices.peek().getModel(),
                            consumers,
                            true,
                            0,
                            15728880
                    );
                }

                textRenderer.draw(
                        text,
                        textX,
                        0.0f,
                        color,
                        false,
                        matrices.peek().getModel(),
                        consumers,
                        true,
                        0,
                        15728880
                );
            } finally {
                matrices.pop();
            }
        }

        /*
         * IMPORTANT:
         * context.consumers() belongs to the world renderer. Do not call
         * consumers.draw() here; vanilla will flush the shared provider at
         * the correct point. This prevents "Not filled all elements of the
         * vertex" crashes when ESP/TNT/text effects are enabled together.
         */
    }

    private static String formatDamage(float damage) {
        if (damage <= 0.0f) {
            return "0";
        }

        if (damage == (int) damage) {
            return Integer.toString((int) damage);
        }

        return String.format(
                Locale.US,
                "%.1f",
                damage
        );
    }
}
