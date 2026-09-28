package com.externalvisuals.gui;

public class GuiMouseHandler {

    private final GuiRenderer renderer;

    public GuiMouseHandler(
            GuiRenderer renderer
    ) {
        this.renderer = renderer;
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (renderer == null) {
            return false;
        }

        return renderer.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (renderer == null) {
            return false;
        }

        return renderer.mouseDragged(
                mouseX,
                mouseY,
                button
        );
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (renderer == null) {
            return false;
        }

        return renderer.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }
}