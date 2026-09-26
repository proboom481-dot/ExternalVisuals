package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.ESP;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ESPWorldRenderer {

    private static final Map<Integer, Deque<Vec3d>> TRAILS = new HashMap<>();
    private static net.minecraft.world.World lastWorld;

    private ESPWorldRenderer() {
    }

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc == null || mc.world == null || mc.player == null
                    || ExternalVisuals.MODULE_MANAGER == null) {
                return;
            }

            if (lastWorld != mc.world) {
                TRAILS.clear();
                lastWorld = mc.world;
            }

            ESP esp = ExternalVisuals.MODULE_MANAGER.get(ESP.class);
            if (esp == null || !esp.isEnabled()) {
                TRAILS.clear();
                return;
            }

            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) {
                return;
            }

            try {
                render(context.matrixStack(), context.camera(), consumers, esp);
            } catch (Throwable throwable) {
                System.err.println("[ExternalVisuals] ESP renderer recovered from a render error: " + throwable.getClass().getSimpleName());
            }
        });
    }

    /** Uses the public debug string instead of touching the private FPS field. */
    private static int estimateFps(MinecraftClient mc) {
        if (mc == null || mc.fpsDebugString == null) return 0;
        String value = mc.fpsDebugString;
        int fps = 0;
        boolean started = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') {
                started = true;
                fps = Math.min(1000, fps * 10 + (c - '0'));
            } else if (started) {
                break;
            }
        }
        return fps;
    }

    private static void render(MatrixStack matrices, Camera camera,
                               VertexConsumerProvider consumers, ESP esp) {
        MinecraftClient mc = MinecraftClient.getInstance();
        VertexConsumer lineConsumer = consumers.getBuffer(ESPNoDepthLayer.LINES);
        double cameraX = camera.getPos().x;
        double cameraY = camera.getPos().y;
        double cameraZ = camera.getPos().z;

        int renderedEntities = 0;
        int budget = esp.getRenderBudget();
        boolean lowFps = false;
        com.externalvisuals.modules.optimization.Optimization optimization =
                ExternalVisuals.MODULE_MANAGER.get(com.externalvisuals.modules.optimization.Optimization.class);
        if (optimization != null && optimization.isEnabled() && esp.isAdaptiveDetail()) {
            int fps = estimateFps(mc);
            lowFps = fps > 0 && fps < optimization.getLowFpsThreshold();
            if (lowFps) budget = Math.max(8, (int) (budget * optimization.getLowFpsTrailScale()));
            else if (fps > 0 && fps < 60) budget = Math.max(12, (int) (budget * 0.75f));
            budget = Math.min(budget, optimization.getEspEntityBudget());
        }
        for (Entity entity : mc.world.getEntities()) {
            if (renderedEntities >= budget) break;
            if (entity == null || !esp.shouldRender(entity)) {
                continue;
            }
            renderedEntities++;

            int baseColor = esp.getColor();
            if (esp.isHealthColor() && entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                float max = Math.max(0.001f, living.getMaxHealth());
                float ratio = Math.max(0.0f, Math.min(1.0f, living.getHealth() / max));
                int hr = Math.round((1.0f - ratio) * 255.0f);
                int hg = Math.round(ratio * 220.0f);
                baseColor = (baseColor & 0xFF000000) | (hr << 16) | (hg << 8) | 0x35;
            }
            boolean targeted = esp.showTargetHighlight() && mc.targetedEntity == entity;
            if (targeted) {
                baseColor = esp.getTargetColor();
            }

            float red = ((baseColor >> 16) & 255) / 255.0f;
            float green = ((baseColor >> 8) & 255) / 255.0f;
            float blue = (baseColor & 255) / 255.0f;
            float alpha = ((baseColor >>> 24) & 255) / 255.0f;
            if (esp.fadeByDistance() && mc.player != null) {
                double distance = mc.player.distanceTo(entity);
                float distanceFade = (float) Math.max(0.20, 1.0 - distance / Math.max(1.0, esp.getRange()));
                alpha *= distanceFade;
            }
            if (targeted && esp.isPulseTarget()) {
                alpha *= (float) (0.72 + 0.28 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 1000.0 * esp.getTargetPulseSpeed())));
            }

            Box box = entity.getBoundingBox().offset(-cameraX, -cameraY, -cameraZ);

            if (esp.showBoxes() && esp.isOutline()) {
                if (esp.isCorners()) {
                    drawCornerBox(matrices, lineConsumer, box, red, green, blue, alpha);
                } else {
                    WorldRenderer.drawBox(matrices, lineConsumer, box, red, green, blue, alpha);
                }
                if (esp.isGlowEnabled()) {
                    float glowAlpha = alpha * 0.28f;
                    Box glowBox = box.expand(0.015);
                    WorldRenderer.drawBox(matrices, lineConsumer, glowBox, red, green, blue, glowAlpha);
                }

                if (targeted && esp.isPulseTargetBoxEnabled()) {
                    float wave = (float) (0.5 + 0.5 * Math.sin(
                            System.currentTimeMillis() * 0.001 * esp.getPulseTargetSpeed()));
                    float pulseAlpha = Math.min(1.0f, alpha * (0.35f + wave * esp.getPulseTargetAlpha()));
                    WorldRenderer.drawBox(matrices, lineConsumer, box.expand(0.035 + wave * 0.035),
                            red, green, blue, pulseAlpha);
                }
            }

            if (targeted && esp.isTargetRingEnabled()) {
                drawTargetRing(matrices, lineConsumer, entity, cameraX, cameraY, cameraZ,
                        red, green, blue, Math.min(1.0f, alpha * 0.95f), esp);
            }

            if (esp.showHealthBar() && entity instanceof LivingEntity) {
                renderHealthBar(matrices, lineConsumer, (LivingEntity) entity,
                        cameraX, cameraY, cameraZ);
            }

            if (esp.showTracers()) {
                drawTracer(matrices, lineConsumer, cameraX, cameraY, cameraZ,
                        entity, red, green, blue, alpha * esp.getTracerAlpha(), esp.isTracerFromCrosshair());
            }
            if (targeted && esp.isTargetBeamEnabled()) {
                drawTracer(matrices, lineConsumer, cameraX, cameraY, cameraZ,
                        entity, red, green, blue, alpha * esp.getTargetBeamAlpha(), true);
            }

            if (esp.showTrail() && !lowFps) {
                updateTrail(entity, esp.getTrailLength());
                drawTrail(matrices, lineConsumer, cameraX, cameraY, cameraZ,
                        entity.getEntityId(), red, green, blue, alpha, esp.getTrailFade());
            }

            if (!lowFps && (esp.showNames() || esp.showDistance() || esp.showHealth()
                    || esp.showArmor() || esp.showItem() || esp.showTotemCount()
                    || esp.isTargetInfoEnabled())) {
                double textDistance = mc.player == null ? 0.0 : mc.player.distanceTo(entity);
                if (!esp.isSkipFarText() || textDistance <= esp.getTextRange()) {
                    try {
                        drawInfo(matrices, consumers, camera, entity, esp, baseColor);
                    } catch (Throwable ignored) {
                        // A malformed text element must not take down the ESP renderer.
                    }
                }
            }
        }

        cleanupTrails(mc, esp.getTrailLength());
        if (optimization != null && optimization.isEnabled()) {
            trimTrailEntities(optimization.getMaxTrailEntities());
            if (optimization.isMemoryGuardEnabled()) trimTrailMemory(optimization.getTrailBudget());
        }
    }

    private static void updateTrail(Entity entity, int maxLength) {
        Deque<Vec3d> points = TRAILS.get(entity.getEntityId());
        if (points == null) {
            points = new ArrayDeque<>();
            TRAILS.put(entity.getEntityId(), points);
        }

        ESP module = ExternalVisuals.MODULE_MANAGER.get(ESP.class);
        double trailY = (module != null && module.isTrailVertical()) ? entity.getHeight() * 0.5 : 0.0;
        Vec3d current = entity.getPos().add(0.0, trailY, 0.0);
        Vec3d last = points.peekLast();
        if (last == null || last.squaredDistanceTo(current) > 0.0004) {
            points.addLast(current);
        }

        while (points.size() > maxLength) {
            points.removeFirst();
        }
    }

    private static void drawTrail(MatrixStack matrices, VertexConsumer consumer,
                                  double cameraX, double cameraY, double cameraZ,
                                  int entityId, float red, float green, float blue,
                                  float alpha, float fade) {
        Deque<Vec3d> points = TRAILS.get(entityId);
        if (points == null || points.size() < 2) {
            return;
        }

        Vec3d previous = null;
        int index = 0;
        int total = Math.max(1, points.size() - 1);

        for (Vec3d point : points) {
            if (previous != null) {
                float progress = index / (float) total;
                float segmentAlpha = Math.min(1.0f, alpha * progress * fade);
                drawLine(matrices, consumer,
                        previous.x - cameraX, previous.y - cameraY, previous.z - cameraZ,
                        point.x - cameraX, point.y - cameraY, point.z - cameraZ,
                        red, green, blue, segmentAlpha);
            }
            previous = point;
            index++;
        }
    }

    private static void cleanupTrails(MinecraftClient mc, int maxLength) {
        if (TRAILS.size() < 256) {
            return;
        }
        Iterator<Integer> iterator = TRAILS.keySet().iterator();
        int removed = 0;
        while (iterator.hasNext() && removed < 64) {
            int id = iterator.next();
            Entity entity = mc.world.getEntityById(id);
            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                removed++;
            } else {
                Deque<Vec3d> points = TRAILS.get(id);
                while (points != null && points.size() > maxLength) {
                    points.removeFirst();
                }
            }
        }
    }

    private static void trimTrailEntities(int maxEntities) {
        if (TRAILS.size() <= maxEntities) return;
        Iterator<Integer> iterator = TRAILS.keySet().iterator();
        while (TRAILS.size() > maxEntities && iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    private static void trimTrailMemory(int pointBudget) {
        int total = 0;
        for (Deque<Vec3d> points : TRAILS.values()) total += points == null ? 0 : points.size();
        if (total <= pointBudget) return;
        Iterator<Map.Entry<Integer, Deque<Vec3d>>> iterator = TRAILS.entrySet().iterator();
        while (iterator.hasNext() && total > pointBudget) {
            Map.Entry<Integer, Deque<Vec3d>> entry = iterator.next();
            Deque<Vec3d> points = entry.getValue();
            while (points != null && !points.isEmpty() && total > pointBudget) {
                points.removeFirst();
                total--;
            }
            if (points == null || points.isEmpty()) iterator.remove();
        }
    }

    private static void drawCornerBox(MatrixStack matrices, VertexConsumer consumer,
                                      Box box, float r, float g, float b, float a) {
        double x1 = box.minX, x2 = box.maxX;
        double y1 = box.minY, y2 = box.maxY;
        double z1 = box.minZ, z2 = box.maxZ;
        double corner = 0.32;
        try {
            ESP module = ExternalVisuals.MODULE_MANAGER.get(ESP.class);
            if (module != null) corner = module.getCornerLength();
        } catch (Throwable ignored) { }
        double dx = (x2 - x1) * corner;
        double dy = (y2 - y1) * Math.min(0.5, corner * 0.75);
        double dz = (z2 - z1) * corner;

        // Bottom corners
        corner(matrices, consumer, x1, y1, z1, dx, dy, dz, r, g, b, a);
        corner(matrices, consumer, x2, y1, z1, -dx, dy, dz, r, g, b, a);
        corner(matrices, consumer, x1, y1, z2, dx, dy, -dz, r, g, b, a);
        corner(matrices, consumer, x2, y1, z2, -dx, dy, -dz, r, g, b, a);
        // Top corners
        corner(matrices, consumer, x1, y2, z1, dx, -dy, dz, r, g, b, a);
        corner(matrices, consumer, x2, y2, z1, -dx, -dy, dz, r, g, b, a);
        corner(matrices, consumer, x1, y2, z2, dx, -dy, -dz, r, g, b, a);
        corner(matrices, consumer, x2, y2, z2, -dx, -dy, -dz, r, g, b, a);
    }

    private static void corner(MatrixStack matrices, VertexConsumer consumer,
                               double x, double y, double z,
                               double dx, double dy, double dz,
                               float r, float g, float b, float a) {
        drawLine(matrices, consumer, x, y, z, x + dx, y, z, r, g, b, a);
        drawLine(matrices, consumer, x, y, z, x, y + dy, z, r, g, b, a);
        drawLine(matrices, consumer, x, y, z, x, y, z + dz, r, g, b, a);
    }

    private static void drawInfo(MatrixStack matrices, VertexConsumerProvider consumers,
                                 Camera camera, Entity entity, ESP esp, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;
        // Invisible entities get a dedicated high-contrast warning marker instead of
        // relying on the normal name/info overlay. This keeps the indicator readable
        // even when the entity model and regular ESP details are hard to see.
        if (entity.isInvisible() && esp.isInvisibleAlertEnabled()) {
            drawInvisibleAlert(matrices, consumers, camera, entity);
            return;
        }

        StringBuilder text = new StringBuilder();

        if (esp.showNames()) {
            text.append(entity.getDisplayName().getString());
        }
        if (esp.showHealth() && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            append(text, String.format(java.util.Locale.US, "%.1f HP", living.getHealth()));
        }
        if (esp.showDistance() && mc.player != null) {
            append(text, String.format(java.util.Locale.US, "%.1fm", mc.player.distanceTo(entity)));
        }
        if (esp.showArmor() && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            int armorCount = 0;
            StringBuilder armorLine = new StringBuilder();
            for (ItemStack stack : living.getArmorItems()) {
                if (stack.isEmpty()) {
                    if (esp.isArmorDetailsEnabled()) {
                        if (armorLine.length() > 0) armorLine.append(" ");
                        armorLine.append("--");
                    }
                    continue;
                }
                armorCount++;
                if (esp.isArmorDetailsEnabled()) {
                    if (armorLine.length() > 0) armorLine.append(" ");
                    if (esp.isArmorDurabilityEnabled() && stack.isDamageable()) {
                        int max = Math.max(1, stack.getMaxDamage());
                        int remaining = Math.max(0, max - stack.getDamage());
                        armorLine.append(Math.round(remaining * 100.0f / max)).append("%");
                    } else {
                        armorLine.append("1");
                    }
                }
            }
            if (esp.isArmorDetailsEnabled()) {
                append(text, "Armor " + armorLine);
            } else {
                append(text, "Armor " + armorCount + "/4");
            }
        }
        if (esp.isTargetInfoEnabled() && mc.targetedEntity == entity) {
            append(text, "TARGET");
        }
        if (esp.showItem() && entity instanceof LivingEntity) {
            ItemStack held = ((LivingEntity) entity).getMainHandStack();
            if (!held.isEmpty()) append(text, held.getName().getString());
        }
        if (esp.showTotemCount() && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if ((!esp.isTotemPlayersOnly() || player != null)
                    && (!esp.isTargetTotemOnly() || mc.targetedEntity == entity)) {
                int visibleTotems = 0;
                ItemStack main = player.getMainHandStack();
                ItemStack off = player.getOffHandStack();
                if (!main.isEmpty() && main.getItem() == net.minecraft.item.Items.TOTEM_OF_UNDYING) visibleTotems += main.getCount();
                if (!off.isEmpty() && off.getItem() == net.minecraft.item.Items.TOTEM_OF_UNDYING) visibleTotems += off.getCount();
                append(text, esp.isTotemLabel() ? "Totems " + visibleTotems : String.valueOf(visibleTotems));
            }
        }

        if (text.length() == 0) return;
        drawName(matrices, consumers, camera, entity, text.toString(), color, esp);
    }

    private static void append(StringBuilder builder, String value) {
        if (builder.length() > 0) builder.append("  •  ");
        builder.append(value);
    }

    private static void drawInvisibleAlert(MatrixStack matrices, VertexConsumerProvider consumers,
                                           Camera camera, Entity entity) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null) return;

        double x = entity.getX() - camera.getPos().x;
        double y = entity.getY() + entity.getHeight() + 0.62 - camera.getPos().y;
        double z = entity.getZ() - camera.getPos().z;

        matrices.push();
        try {
            matrices.translate(x, y, z);
            matrices.multiply(camera.getRotation());
            matrices.scale(-0.030f, -0.030f, 0.030f);

            final String marker = "!!!";
            int width = textRenderer.getWidth(marker);
            // Bright yellow panel + black exclamation marks: deliberately distinct
            // from the normal ESP palette so invisible targets are immediately clear.
            int background = 0xFFFFC107;
            int textColor = 0xFF111111;

            textRenderer.draw(marker, -width / 2.0f + 2.0f, 2.0f, 0xFF000000, false,
                    matrices.peek().getModel(), consumers, true, 0, 15728880);
            textRenderer.draw(marker, -width / 2.0f, 0.0f, textColor, true,
                    matrices.peek().getModel(), consumers, true, background, 15728880);
        } finally {
            matrices.pop();
        }
    }

    private static void drawName(MatrixStack matrices, VertexConsumerProvider consumers,
                                 Camera camera, Entity entity, String text, int color, ESP esp) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer == null || text.isEmpty()) return;

        double x = entity.getX() - camera.getPos().x;
        double y = entity.getY() + entity.getHeight() + 0.35 + esp.getInfoOffset() - camera.getPos().y;
        double z = entity.getZ() - camera.getPos().z;

        matrices.push();
        try {
            matrices.translate(x, y, z);
            matrices.multiply(camera.getRotation());
            matrices.scale(-0.025f, -0.025f, 0.025f);
            String drawText = text;
            if (esp.isCompactInfo() && drawText.length() > 28) {
                drawText = drawText.substring(0, 27) + "…";
            }
            int width = textRenderer.getWidth(drawText);
            if (esp.isInfoBackgroundEnabled()) {
                int bg = 0x78000000;
                textRenderer.draw(drawText, -width / 2.0f + 1.0f, 1.0f, bg, false,
                        matrices.peek().getModel(), consumers, true, 0, 15728880);
            }
            textRenderer.draw(drawText, -width / 2.0f, 0.0f, color, true,
                    matrices.peek().getModel(), consumers, true, 0, 15728880);
        } finally {
            matrices.pop();
        }
    }

    private static void drawTargetRing(MatrixStack matrices, VertexConsumer consumer,
                                         Entity entity, double cameraX, double cameraY, double cameraZ,
                                         float red, float green, float blue, float alpha, ESP esp) {
        int segments = Math.max(8, esp.getTargetRingSegments());
        double radius = Math.max(0.1, esp.getTargetRingRadius());
        double y = entity.getY() + esp.getTargetRingHeight() - cameraY;
        double cx = entity.getX() - cameraX;
        double cz = entity.getZ() - cameraZ;
        double time = System.currentTimeMillis() * 0.0015;
        Vec3d previous = null;

        for (int i = 0; i <= segments; i++) {
            double angle = time + (Math.PI * 2.0 * i / segments);
            Vec3d point = new Vec3d(
                    cx + Math.cos(angle) * radius,
                    y,
                    cz + Math.sin(angle) * radius
            );
            if (previous != null) {
                drawLine(matrices, consumer,
                        previous.x, previous.y, previous.z,
                        point.x, point.y, point.z,
                        red, green, blue, alpha);
            }
            previous = point;
        }
    }

    private static void drawTracer(MatrixStack matrices, VertexConsumer consumer,
                                   double cameraX, double cameraY, double cameraZ,
                                   Entity entity, float r, float g, float b, float a,
                                   boolean fromCrosshair) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double targetX = entity.getX() - cameraX;
        double targetY = entity.getY() + entity.getHeight() * 0.5 - cameraY;
        double targetZ = entity.getZ() - cameraZ;
        double startX = 0.0;
        double startY = 0.0;
        double startZ = 0.0;
        if (!fromCrosshair && mc.player != null) {
            startX = mc.player.getX() - cameraX;
            startY = mc.player.getY() + 0.8 - cameraY;
            startZ = mc.player.getZ() - cameraZ;
        }
        drawLine(matrices, consumer, startX, startY, startZ,
                targetX, targetY, targetZ, r, g, b, a);
    }

    private static void renderHealthBar(MatrixStack matrices, VertexConsumer consumer,
                                        LivingEntity entity, double cameraX,
                                        double cameraY, double cameraZ) {
        float maxHealth = entity.getMaxHealth();
        if (maxHealth <= 0.0f) return;
        float health = Math.max(0.0f, Math.min(entity.getHealth(), maxHealth));
        float progress = health / maxHealth;
        Box box = entity.getBoundingBox();
        double x = box.minX - cameraX - 0.08;
        double y = box.minY - cameraY;
        double z = box.minZ - cameraZ;
        double height = Math.max(0.05, box.maxY - box.minY);

        drawLine(matrices, consumer, x, y, z, x, y + height, z,
                0.08f, 0.08f, 0.08f, 0.85f);
        drawLine(matrices, consumer, x, y, z, x, y + height * progress, z,
                1.0f - progress, progress, 0.15f, 1.0f);
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float red, float green, float blue, float alpha) {
        consumer.vertex(matrices.peek().getModel(), (float) x1, (float) y1, (float) z1)
                .color(red, green, blue, alpha).next();
        consumer.vertex(matrices.peek().getModel(), (float) x2, (float) y2, (float) z2)
                .color(red, green, blue, alpha).next();
    }
}
