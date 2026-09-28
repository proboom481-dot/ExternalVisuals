package com.externalvisuals.gui;

import com.externalvisuals.gui.components.GuiPanel;
import com.externalvisuals.gui.components.GuiSlider;
import com.externalvisuals.gui.components.GuiToggle;
import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.setting.BooleanSetting;
import com.externalvisuals.setting.ColorSetting;
import com.externalvisuals.setting.Setting;
import com.externalvisuals.setting.SliderSetting;
import com.externalvisuals.setting.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderer {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final ModuleManager moduleManager;

    private ModuleCategory selectedCategory =
            ModuleCategory.COMBAT;

    private Module selectedModule;

    private final List<GuiPanel> panels =
            new ArrayList<>();

    private final List<GuiToggle> toggles =
            new ArrayList<>();

    private final List<GuiSlider> sliders =
            new ArrayList<>();

    private int guiX;
    private int guiY;

    private int guiWidth;
    private int guiHeight;

    private int leftWidth;
    private int centerWidth;
    private int rightWidth;

    public GuiRenderer(
            ModuleManager moduleManager
    ) {
        this.moduleManager = moduleManager;

        rebuildLayout();
    }

    public void rebuildLayout() {

        int screenWidth =
                mc.getWindow().getScaledWidth();

        int screenHeight =
                mc.getWindow().getScaledHeight();

        guiWidth =
                Math.min(
                        900,
                        screenWidth - 30
                );

        guiHeight =
                Math.min(
                        520,
                        screenHeight - 30
                );

        guiWidth =
                Math.max(
                        500,
                        guiWidth
                );

        guiHeight =
                Math.max(
                        300,
                        guiHeight
                );

        guiX =
                (screenWidth - guiWidth) / 2;

        guiY =
                (screenHeight - guiHeight) / 2;

        leftWidth = 150;

        centerWidth =
                Math.max(
                        180,
                        (guiWidth - leftWidth) / 2
                );

        rightWidth =
                guiWidth
                        - leftWidth
                        - centerWidth;

        rebuildPanels();
    }

    private void rebuildPanels() {

        panels.clear();

        panels.add(
                new GuiPanel(
                        "Categories",
                        guiX,
                        guiY,
                        leftWidth,
                        guiHeight
                )
        );

        panels.add(
                new GuiPanel(
                        "Modules",
                        guiX + leftWidth,
                        guiY,
                        centerWidth,
                        guiHeight
                )
        );

        panels.add(
                new GuiPanel(
                        "Settings",
                        guiX
                                + leftWidth
                                + centerWidth,
                        guiY,
                        rightWidth,
                        guiHeight
                )
        );
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        rebuildLayout();

        renderBackground(
                matrices
        );

        for (GuiPanel panel :
                panels) {

            panel.render(
                    matrices,
                    mouseX,
                    mouseY
            );
        }

        renderCategories(
                matrices,
                mouseX,
                mouseY
        );

        renderModules(
                matrices,
                mouseX,
                mouseY
        );

        renderSettings(
                matrices,
                mouseX,
                mouseY
        );

        renderFooter(
                matrices,
                mouseX,
                mouseY
        );
    }

    private void renderBackground(
            MatrixStack matrices
    ) {

        fill(
                matrices,
                0,
                0,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                GuiColors.OVERLAY
        );

        fill(
                matrices,
                guiX - 2,
                guiY - 2,
                guiX + guiWidth + 2,
                guiY + guiHeight + 2,
                GuiColors.BORDER
        );
    }

    private void renderCategories(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        int startY =
                guiY + 34;

        int rowHeight = 30;

        for (ModuleCategory category :
                ModuleCategory.values()) {

            boolean selected =
                    category == selectedCategory;

            boolean hovered =
                    mouseX >= guiX + 6
                            && mouseX <=
                            guiX + leftWidth - 6
                            && mouseY >= startY
                            && mouseY <=
                            startY + rowHeight - 4;

            int background =
                    selected
                            ? GuiColors.SELECTED
                            : hovered
                            ? GuiColors.HOVER
                            : GuiColors.PANEL;

            fill(
                    matrices,
                    guiX + 6,
                    startY,
                    guiX + leftWidth - 6,
                    startY + rowHeight - 4,
                    background
            );

            if (selected) {

                fill(
                        matrices,
                        guiX + 6,
                        startY,
                        guiX + 9,
                        startY + rowHeight - 4,
                        GuiColors.ACCENT
                );
            }

            drawText(
                    matrices,
                    category.getDisplayName(),
                    guiX + 18,
                    startY + 7,
                    selected
                            ? GuiColors.TEXT
                            : GuiColors.TEXT_LIGHT
            );

            startY += rowHeight;
        }
    }

    private void renderModules(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        int startX =
                guiX + leftWidth + 8;

        int startY =
                guiY + 34;

        int rowHeight = 34;

        for (Module module :
                moduleManager.getModules()) {

            if (module.getCategory()
                    != selectedCategory) {

                continue;
            }

            boolean selected =
                    module == selectedModule;

            boolean hovered =
                    mouseX >= startX
                            && mouseX <=
                            guiX
                                    + leftWidth
                                    + centerWidth
                                    - 8
                            && mouseY >= startY
                            && mouseY <=
                            startY + rowHeight - 5;

            int background;

            if (selected) {

                background =
                        GuiColors.SELECTED;

            } else if (hovered) {

                background =
                        GuiColors.HOVER;

            } else {

                background =
                        GuiColors.PANEL;
            }

            fill(
                    matrices,
                    startX,
                    startY,
                    guiX
                            + leftWidth
                            + centerWidth
                            - 8,
                    startY + rowHeight - 5,
                    background
            );

            if (module.isEnabled()) {

                fill(
                        matrices,
                        startX,
                        startY,
                        startX + 3,
                        startY + rowHeight - 5,
                        GuiColors.ENABLED
                );
            }

            drawText(
                    matrices,
                    module.getName(),
                    startX + 10,
                    startY + 8,
                    module.isEnabled()
                            ? GuiColors.TEXT
                            : GuiColors.TEXT_MUTED
            );

            drawToggleIndicator(
                    matrices,
                    guiX
                            + leftWidth
                            + centerWidth
                            - 28,
                    startY + 8,
                    module.isEnabled()
            );

            startY += rowHeight;
        }
    }

    private void renderSettings(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        toggles.clear();
        sliders.clear();

        if (selectedModule == null) {

            drawText(
                    matrices,
                    "Select a module",
                    guiX
                            + leftWidth
                            + centerWidth
                            + 14,
                    guiY + 45,
                    GuiColors.TEXT_MUTED
            );

            return;
        }

        int startX =
                guiX
                        + leftWidth
                        + centerWidth
                        + 12;

        int contentWidth =
                rightWidth - 24;

        int currentY =
                guiY + 42;

        drawText(
                matrices,
                selectedModule.getName(),
                startX,
                currentY,
                GuiColors.ACCENT
        );

        currentY += 28;

        for (Setting<?> setting :
                selectedModule.getSettings()) {

            if (setting instanceof BooleanSetting) {

                BooleanSetting booleanSetting =
                        (BooleanSetting) setting;

                GuiToggle toggle =
                        new GuiToggle(
                                booleanSetting,
                                startX,
                                currentY,
                                contentWidth,
                                22
                        );

                toggle.render(
                        matrices,
                        mouseX,
                        mouseY
                );

                toggles.add(toggle);

                currentY += 32;

            } else if (setting instanceof SliderSetting) {

                SliderSetting sliderSetting =
                        (SliderSetting) setting;

                GuiSlider slider =
                        new GuiSlider(
                                sliderSetting,
                                startX,
                                currentY + 8,
                                contentWidth,
                                14
                        );

                slider.render(
                        matrices,
                        mouseX,
                        mouseY
                );

                sliders.add(slider);

                currentY += 48;

            } else if (setting instanceof StringSetting) {

                StringSetting stringSetting =
                        (StringSetting) setting;

                renderStringSetting(
                        matrices,
                        stringSetting,
                        startX,
                        currentY,
                        contentWidth,
                        mouseX,
                        mouseY
                );

                currentY += 38;

            } else if (setting instanceof ColorSetting) {

                ColorSetting colorSetting =
                        (ColorSetting) setting;

                renderColorSetting(
                        matrices,
                        colorSetting,
                        startX,
                        currentY,
                        contentWidth,
                        mouseX,
                        mouseY
                );

                currentY += 44;

            } else {

                drawText(
                        matrices,
                        setting.getName(),
                        startX,
                        currentY,
                        GuiColors.TEXT_MUTED
                );

                currentY += 30;
            }

            if (currentY >
                    guiY + guiHeight - 55) {

                break;
            }
        }
    }

    private void renderStringSetting(
            MatrixStack matrices,
            StringSetting setting,
            int x,
            int y,
            int width,
            int mouseX,
            int mouseY
    ) {

        drawText(
                matrices,
                setting.getName(),
                x,
                y,
                GuiColors.TEXT_LIGHT
        );

        String value =
                setting.getValue();

        int boxTop =
                y + 14;

        int boxRight =
                x + width;

        boolean hovered =
                mouseX >= x
                        && mouseX <= boxRight
                        && mouseY >= boxTop
                        && mouseY <= boxTop + 20;

        int background =
                hovered
                        ? GuiColors.HOVER
                        : GuiColors.INPUT;

        fill(
                matrices,
                x,
                boxTop,
                boxRight,
                boxTop + 20,
                background
        );

        drawText(
                matrices,
                value,
                x + 7,
                boxTop + 6,
                GuiColors.TEXT
        );

        drawText(
                matrices,
                "›",
                boxRight - 14,
                boxTop + 4,
                GuiColors.ACCENT
        );
    }

    private void renderColorSetting(
            MatrixStack matrices,
            ColorSetting setting,
            int x,
            int y,
            int width,
            int mouseX,
            int mouseY
    ) {

        drawText(
                matrices,
                setting.getName(),
                x,
                y,
                GuiColors.TEXT_LIGHT
        );

        int previewX =
                x + width - 32;

        int previewY =
                y - 2;

        fill(
                matrices,
                previewX - 1,
                previewY - 1,
                previewX + 25,
                previewY + 25,
                GuiColors.BORDER
        );

        fill(
                matrices,
                previewX,
                previewY,
                previewX + 24,
                previewY + 24,
                setting.getColor()
        );

        String hex =
                String.format(
                        "#%06X",
                        setting.getColor()
                                & 0xFFFFFF
                );

        drawText(
                matrices,
                hex,
                x,
                y + 18,
                GuiColors.TEXT_MUTED
        );
    }

    private void drawToggleIndicator(
            MatrixStack matrices,
            int x,
            int y,
            boolean enabled
    ) {

        int color =
                enabled
                        ? GuiColors.ENABLED
                        : GuiColors.DISABLED;

        fill(
                matrices,
                x,
                y,
                x + 14,
                y + 14,
                color
        );

        if (enabled) {

            fill(
                    matrices,
                    x + 3,
                    y + 3,
                    x + 11,
                    y + 11,
                    GuiColors.TEXT
            );
        }
    }

    private void renderFooter(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        int footerY =
                guiY + guiHeight - 34;

        fill(
                matrices,
                guiX,
                footerY,
                guiX + guiWidth,
                footerY + 1,
                GuiColors.DIVIDER
        );

        drawText(
                matrices,
                "ExternalVisuals",
                guiX + 10,
                footerY + 11,
                GuiColors.TEXT_MUTED
        );

        int exitWidth = 70;
        int exitHeight = 22;

        int exitX =
                guiX
                        + guiWidth
                        - exitWidth
                        - 10;

        int exitY =
                footerY
                        + 5;

        boolean hovered =
                mouseX >= exitX
                        && mouseX <=
                        exitX + exitWidth
                        && mouseY >= exitY
                        && mouseY <=
                        exitY + exitHeight;

        fill(
                matrices,
                exitX,
                exitY,
                exitX + exitWidth,
                exitY + exitHeight,
                hovered
                        ? GuiColors.BUTTON_HOVER
                        : GuiColors.BUTTON
        );

        drawText(
                matrices,
                "Exit",
                exitX + 24,
                exitY + 6,
                GuiColors.TEXT
        );

        String version =
                "1.2.0";

        TextRenderer renderer =
                mc.textRenderer;

        int versionWidth =
                renderer.getWidth(version);

        drawText(
                matrices,
                version,
                exitX
                        - versionWidth
                        - 12,
                footerY + 11,
                GuiColors.TEXT_MUTED
        );
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * EXIT BUTTON
         */
        int footerY =
                guiY + guiHeight - 34;

        int exitWidth = 70;
        int exitHeight = 22;

        int exitX =
                guiX
                        + guiWidth
                        - exitWidth
                        - 10;

        int exitY =
                footerY + 5;

        if (mouseX >= exitX
                && mouseX <= exitX + exitWidth
                && mouseY >= exitY
                && mouseY <= exitY + exitHeight) {

            if (button == 0
                    && mc != null) {

                mc.openScreen(null);

                return true;
            }
        }

        /*
         * CATEGORIES
         */
        int categoryY =
                guiY + 34;

        for (ModuleCategory category :
                ModuleCategory.values()) {

            if (mouseX >= guiX + 6
                    && mouseX <=
                    guiX + leftWidth - 6
                    && mouseY >= categoryY
                    && mouseY <=
                    categoryY + 26) {

                selectedCategory =
                        category;

                selectedModule = null;

                return true;
              }
            categoryY += 30;
        }

        /*
         * MODULES
         *
         * ЛКМ:
         * выбрать модуль + включить/выключить.
         *
         * ПКМ:
         * только выбрать модуль.
         */
        int moduleX =
                guiX + leftWidth + 8;

        int moduleY =
                guiY + 34;

        for (Module module :
                moduleManager.getModules()) {

            if (module.getCategory()
                    != selectedCategory) {

                continue;
            }

            if (mouseX >= moduleX
                    && mouseX <=
                    guiX
                            + leftWidth
                            + centerWidth
                            - 8
                    && mouseY >= moduleY
                    && mouseY <=
                    moduleY + 29) {

                if (button == 0) {

                    selectedModule =
                            module;

                    module.toggle();

                    return true;
                }

                if (button == 1) {

                    selectedModule =
                            module;

                    return true;
                }
            }

            moduleY += 34;
        }

        /*
         * BOOLEAN SETTINGS
         */
        for (GuiToggle toggle :
                toggles) {

            if (toggle.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        /*
         * SLIDER SETTINGS
         */
        for (GuiSlider slider :
                sliders) {

            if (slider.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        /*
         * STRING + COLOR SETTINGS
         */
        if (selectedModule != null) {

            int startX =
                    guiX
                            + leftWidth
                            + centerWidth
                            + 12;

            int contentWidth =
                    rightWidth - 24;

            int currentY =
                    guiY + 42;

            currentY += 28;

            for (Setting<?> setting :
                    selectedModule.getSettings()) {

                if (setting instanceof BooleanSetting) {

                    currentY += 32;

                } else if (setting instanceof SliderSetting) {

                    currentY += 48;

                } else if (setting instanceof StringSetting) {

                    StringSetting stringSetting =
                            (StringSetting) setting;

                    int boxTop =
                            currentY + 14;

                    int boxRight =
                            startX + contentWidth;

                    if (mouseX >= startX
                            && mouseX <= boxRight
                            && mouseY >= boxTop
                            && mouseY <=
                            boxTop + 20
                            && button == 0) {

                        stringSetting.cycle();

                        return true;
                    }

                    currentY += 38;

                } else if (setting instanceof ColorSetting) {

                    ColorSetting colorSetting =
                            (ColorSetting) setting;

                    int previewY =
                            currentY - 2;

                    if (mouseX >= startX
                            && mouseX <=
                            startX + contentWidth
                            && mouseY >= previewY
                            && mouseY <=
                            previewY + 25
                            && button == 0) {

                        cycleColor(
                                colorSetting
                        );

                        return true;
                    }

                    currentY += 44;

                } else {

                    currentY += 30;
                }

                if (currentY >
                        guiY + guiHeight - 55) {

                    break;
                }
            }
        }

        return false;
    }

    private void cycleColor(
            ColorSetting setting
    ) {

        int current =
                setting.getColor()
                        & 0xFFFFFF;

        int next;

        if (current == 0xFFFFFF) {

            next = 0xFF5555;

        } else if (current == 0xFF5555) {

            next = 0x55FF55;

        } else if (current == 0x55FF55) {

            next = 0x5555FF;

        } else if (current == 0x5555FF) {

            next = 0xFFFF55;

        } else if (current == 0xFFFF55) {

            next = 0x55FFFF;

        } else if (current == 0x55FFFF) {

            next = 0xFF55FF;

        } else {

            next = 0xFFFFFF;
        }

        int alpha =
                setting.getAlpha();

        setting.setColor(
                (alpha << 24)
                        | next
        );
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (GuiSlider slider :
                sliders) {

            if (slider.mouseDragged(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (GuiSlider slider :
                sliders) {

            if (slider.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            )) {

                return true;
            }
        }

        return false;
    }

    private void drawText(
            MatrixStack matrices,
            String text,
            int x,
            int y,
            int color
    ) {

        mc.textRenderer.drawWithShadow(
                matrices,
                text == null ? "" : text,
                x,
                y,
                color
        );
    }

    private void fill(
            MatrixStack matrices,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {

        net.minecraft.client.gui.DrawableHelper.fill(
                matrices,
                left,
                top,
                right,
                bottom,
                color
        );
    }

    public ModuleCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(
            ModuleCategory category
    ) {

        if (category == null) {
            return;
        }

        selectedCategory =
                category;

        selectedModule = null;
    }

    public Module getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(
            Module module
    ) {

        selectedModule =
                module;
    }

    public int getGuiX() {
        return guiX;
    }

    public int getGuiY() {
        return guiY;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    public int getGuiHeight() {
        return guiHeight;
    }
}