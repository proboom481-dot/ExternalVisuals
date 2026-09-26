package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.config.ConfigManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public final class ClickGuiScreen extends Screen {

    private ModernGuiRenderer renderer;

    public ClickGuiScreen() {

        super(
                new LiteralText(
                        "ExternalVisuals"
                )
        );
    }

    @Override
    protected void init() {

        super.init();

        if (
                ExternalVisuals.MODULE_MANAGER
                        == null
        ) {

            renderer = null;

            return;
        }

        renderer =
                new ModernGuiRenderer(
                        ExternalVisuals.MODULE_MANAGER
                );
    }

    @Override
    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY,
            float delta
    ) {

        if (renderer != null) {

            renderer.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        super.render(
                matrices,
                mouseX,
                mouseY,
                delta
        );
    }

    @Override
    public void removed() {
        ConfigManager.save();
        super.removed();
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (
                renderer != null
                        && renderer.mouseClicked(
                        mouseX,
                        mouseY,
                        button
                )
        ) {

            return true;
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

        if (
                renderer != null
                        && renderer.mouseDragged(
                        mouseX,
                        mouseY,
                        button,
                        deltaX,
                        deltaY
                )
        ) {

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

        if (
                renderer != null
                        && renderer.mouseReleased(
                        mouseX,
                        mouseY,
                        button
                )
        ) {

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

        if (renderer != null
                && renderer.mouseScrolled(
                mouseX,
                mouseY,
                amount
        )) {
            return true;
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                amount
        );
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (renderer != null) {

            if (
                    renderer.keyPressed(
                            keyCode,
                            scanCode,
                            modifiers
                    )
            ) {

                return true;
            }
        }

        if (keyCode == 256) {

            if (client != null) {

                client.openScreen(
                        null
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
    public boolean charTyped(
            char chr,
            int modifiers
    ) {

        if (
                renderer != null
                        && renderer.charTyped(
                        chr,
                        modifiers
                )
        ) {

            return true;
        }

        return super.charTyped(
                chr,
                modifiers
        );
    }

    public ModernGuiRenderer getRenderer() {
        return renderer;
    }
}