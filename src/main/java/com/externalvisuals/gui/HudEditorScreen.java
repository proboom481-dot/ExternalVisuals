package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.config.ConfigManager;
import com.externalvisuals.module.Module;
import com.externalvisuals.modules.hud.HudModule;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public final class HudEditorScreen extends Screen {

    private final List<HudModule> hudModules =
            new ArrayList<>();

    private HudModule dragging;

    private float dragOffsetX;
    private float dragOffsetY;

    private int grid = 4;

    public HudEditorScreen() {
        super(new LiteralText("ExternalVisuals HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();

        hudModules.clear();

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        for (Module module :
                ExternalVisuals.MODULE_MANAGER.getModules()) {

            if (module instanceof HudModule) {
                hudModules.add(
                        (HudModule) module
                );
            }
        }
    }

    @Override
    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY,
            float delta
    ) {

        /*
         * Background
         */
        DrawableHelper.fill(
                matrices,
                0,
                0,
                width,
                height,
                0xE9000008
        );

        /*
         * Grid
         */
        drawGrid(matrices);

        /*
         * Title
         */
        drawCenteredText(
                matrices,
                "HUD EDITOR",
                width / 2,
                18,
                AMOLEDTheme.TEXT
        );

        /*
         * Help text
         */
        drawCenteredText(
                matrices,
                "Drag components • Mouse wheel = scale • Right click = reset",
                width / 2,
                34,
                AMOLEDTheme.TEXT_MUTED
        );

        /*
         * HUD components
         */
        for (HudModule hud : hudModules) {
            renderHudComponent(
                    matrices,
                    hud,
                    mouseX,
                    mouseY
            );
        }

        /*
         * Bottom hint
         */
        drawCenteredText(
                matrices,
                "ESC — back",
                width / 2,
                height - 18,
                AMOLEDTheme.TEXT_MUTED
        );

        super.render(
                matrices,
                mouseX,
                mouseY,
                delta
        );
    }

    /**
     * Центрированный текст через TextRenderer.
     *
     * В Minecraft 1.16.5 DrawableHelper не содержит
     * нужного drawCenteredString overload, поэтому
     * рисуем напрямую через textRenderer.
     */
    private void drawCenteredText(
            MatrixStack matrices,
            String text,
            float centerX,
            float y,
            int color
    ) {
        float textWidth =
                textRenderer.getWidth(text);

        textRenderer.drawWithShadow(
                matrices,
                text,
                centerX - textWidth / 2.0f,
                y,
                color
        );
    }

    /**
     * Обычный текст с тенью.
     */
    private void drawText(
            MatrixStack matrices,
            String text,
            float x,
            float y,
            int color
    ) {
        textRenderer.drawWithShadow(
                matrices,
                text,
                x,
                y,
                color
        );
    }

    private void drawGrid(MatrixStack matrices) {

        for (int x = 0; x < width; x += 32) {

            DrawableHelper.fill(
                    matrices,
                    x,
                    0,
                    x + 1,
                    height,
                    0x182C2C38
            );
        }

        for (int y = 0; y < height; y += 32) {

            DrawableHelper.fill(
                    matrices,
                    0,
                    y,
                    width,
                    y + 1,
                    0x182C2C38
            );
        }
    }

    private void renderHudComponent(
            MatrixStack matrices,
            HudModule hud,
            int mouseX,
            int mouseY
    ) {

        int x =
                Math.round(hud.getX());

        int y =
                Math.round(hud.getY());

        int baseWidth =
                Math.max(
                        74,
                        textRenderer.getWidth(
                                hud.getName()
                        ) + 28
                );

        int baseHeight = 24;

        int drawWidth =
                Math.max(
                        30,
                        Math.round(
                                baseWidth
                                        * hud.getScale()
                        )
                );

        int drawHeight =
                Math.max(
                        18,
                        Math.round(
                                baseHeight
                                        * hud.getScale()
                        )
                );

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + drawWidth
                        && mouseY >= y
                        && mouseY <= y + drawHeight;

        /*
         * Background
         */
        int background;

        if (!hud.isEnabled()) {

            background =
                    0x66101015;

        } else if (hud.isBackgroundEnabled()) {

            background =
                    hovered
                            ? 0xE62A2038
                            : 0xD20A0A10;

        } else {

            background =
                    hovered
                            ? 0x99302040
                            : 0x66202028;
        }

        DrawableHelper.fill(
                matrices,
                x,
                y,
                x + drawWidth,
                y + drawHeight,
                background
        );

        /*
         * Border
         */
        if (hud.isBorderEnabled()) {

            int color =
                    hud.getColor();

            /*
             * Top
             */
            DrawableHelper.fill(
                    matrices,
                    x,
                    y,
                    x + drawWidth,
                    y + 2,
                    color
            );

            /*
             * Bottom
             */
            DrawableHelper.fill(
                    matrices,
                    x,
                    y + drawHeight - 2,
                    x + drawWidth,
                    y + drawHeight,
                    color
            );
        }

        /*
         * Label
         */
        String label =
                hud.getName()
                        + (hud.isEnabled()
                        ? ""
                        : " [OFF]");

        matrices.push();

        try {

            matrices.translate(
                    x + 8,
                    y + 7,
                    0.0f
            );

            matrices.scale(
                    hud.getScale(),
                    hud.getScale(),
                    1.0f
            );

            drawText(
                    matrices,
                    label,
                    0,
                    0,
                    hud.getColor()
            );

        } finally {

            matrices.pop();
        }

        /*
         * Position + scale information
         */
        if (hovered) {

            String info =
                    Math.round(hud.getX())
                            + ", "
                            + Math.round(hud.getY())
                            + "  "
                            + Math.round(
                                    hud.getScale()
                                            * 100.0f
                            )
                            + "%";

            drawText(
                    matrices,
                    info,
                    x + 8,
                    y + drawHeight + 3,
                    AMOLEDTheme.TEXT_MUTED
            );
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Left click — start dragging
         */
        if (button == 0) {

            for (HudModule hud : hudModules) {

                int x =
                        Math.round(hud.getX());

                int y =
                        Math.round(hud.getY());

                int w =
                        Math.max(
                                74,
                                Math.round(
                                        (
                                                textRenderer.getWidth(
                                                        hud.getName()
                                                ) + 28
                                        )
                                                * hud.getScale()
                                )
                        );

                int h =
                        Math.max(
                                18,
                                Math.round(
                                        24
                                                * hud.getScale()
                                )
                        );

                if (mouseX >= x
                        && mouseX <= x + w
                        && mouseY >= y
                        && mouseY <= y + h) {

                    dragging = hud;

                    dragOffsetX =
                            (float) mouseX
                                    - hud.getX();

                    dragOffsetY =
                            (float) mouseY
                                    - hud.getY();

                    return true;
                }
            }
        }

        /*
         * Right click — reset
         */
        if (button == 1) {

            for (HudModule hud : hudModules) {

                int x =
                        Math.round(hud.getX());

                int y =
                        Math.round(hud.getY());

                if (Math.abs(mouseX - x) <= 80
                        && Math.abs(mouseY - y) <= 40) {

                    resetPosition(hud);

                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        if (button == 0
                && dragging != null) {

            float newX =
                    (float) mouseX
                            - dragOffsetX;

            float newY =
                    (float) mouseY
                            - dragOffsetY;

            /*
             * Grid snapping
             */
            newX =
                    Math.round(
                            newX / grid
                    ) * grid;

            newY =
                    Math.round(
                            newY / grid
                    ) * grid;

            /*
             * Keep HUD inside screen
             */
            dragging.setX(
                    Math.max(
                            0.0f,
                            Math.min(
                                    width - 20.0f,
                                    newX
                            )
                    )
            );

            dragging.setY(
                    Math.max(
                            0.0f,
                            Math.min(
                                    height - 20.0f,
                                    newY
                            )
                    )
            );

            return true;
        }

        return super.mouseDragged(
                mouseX,
                mouseY,
                button,
                deltaX,
                deltaY
        );
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button == 0) {

            dragging = null;

            ConfigManager.save();

            return true;
        }

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ) {

        for (HudModule hud : hudModules) {

            int x =
                    Math.round(hud.getX());

            int y =
                    Math.round(hud.getY());

            if (Math.abs(mouseX - x) <= 100
                    && Math.abs(mouseY - y) <= 60) {

                float next =
                        hud.getScale()
                                + (
                                amount > 0.0
                                        ? 0.05f
                                        : -0.05f
                        );

                hud.setScale(next);

                ConfigManager.save();

                return true;
            }
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                amount
        );
    }

    private void resetPosition(
            HudModule hud
    ) {

        hud.setHudPosition(
                6.0f,
                6.0f
        );

        hud.setScale(
                1.0f
        );

        ConfigManager.save();
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        /*
         * ESC
         */
        if (keyCode == 256) {

            if (client != null) {

                client.openScreen(
                        new ClickGuiScreen()
                );
            }

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    @Override
    public void removed() {

        ConfigManager.save();

        super.removed();
    }
}