package com.externalvisuals.gui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.HashMap;
import java.util.Map;

public final class ModernGuiRenderer {

    private static final Identifier GLASS_TEXTURE =
            new Identifier("externalvisuals", "textures/gui/glass_noise.png");

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final ModuleManager moduleManager;

    private final GuiSearchBar searchBar;

    private final ModuleSettingsPanel settingsPanel;

    private ModuleCategory selectedCategory;

    private Module selectedModule;

    private float animation;
    private static float savedUiScale = AMOLEDTheme.DEFAULT_SCALE;

    private float uiScale = AMOLEDTheme.DEFAULT_SCALE;
    private int moduleScroll;

    /*
     * Animated module states.
     */
    private final Map<Module, Float> moduleHoverAnimations =
            new HashMap<>();

    private final Map<Module, Float> moduleEnabledAnimations =
            new HashMap<>();

    private final Map<ModuleCategory, Float> categoryAnimations =
            new HashMap<>();

    /*
     * Secret menu click sequence.
     */
    private int secretClicks;

    private long lastSecretClickTime;

    private static final int SECRET_CLICK_TARGET = 5;

    private static final long SECRET_CLICK_WINDOW =
            2000L;

    public ModernGuiRenderer(
            ModuleManager moduleManager
    ) {

        this.moduleManager = moduleManager;

        this.selectedCategory =
                ModuleCategory.VISUALS;

        this.uiScale = AMOLEDTheme.clampScale(savedUiScale);

        this.selectedModule = null;
        this.moduleScroll = 0;

        this.searchBar =
                new GuiSearchBar(
                        0,
                        0,
                        190,
                        24
                );

        this.settingsPanel =
                new ModuleSettingsPanel();

        this.animation = 0.0f;

        this.secretClicks = 0;

        this.lastSecretClickTime = 0L;
    }

    public void render(
            MatrixStack matrices,
            int mouseX,
            int mouseY
    ) {

        if (mc == null) {
            return;
        }

        if (moduleManager == null) {
            return;
        }

        animation =
                AMOLEDTheme.animate(
                        animation,
                        1.0f,
                        AMOLEDTheme.PANEL_SPEED
                );

        int screenWidth =
                mc.getWindow().getScaledWidth();

        int screenHeight =
                mc.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int guiMouseX = Math.round(centerX + (mouseX - centerX) / uiScale);
        int guiMouseY = Math.round(centerY + (mouseY - centerY) / uiScale);
        int logicalWidth = Math.max(320, Math.round(screenWidth / uiScale));
        int logicalHeight = Math.max(240, Math.round(screenHeight / uiScale));

        fill(matrices, 0, 0, screenWidth, screenHeight, AMOLEDTheme.BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        mc.getTextureManager().bindTexture(GLASS_TEXTURE);
        DrawableHelper.drawTexture(
                matrices, 0, 0, 0, 0, screenWidth, screenHeight, 768, 768
        );
        RenderSystem.disableBlend();

        matrices.push();
        matrices.translate(centerX, centerY, 0.0f);
        matrices.scale(uiScale, uiScale, 1.0f);
        matrices.translate(-centerX, -centerY, 0.0f);

        /*
         * Small iOS-style entrance motion: the panel settles into place
         * instead of appearing instantly.
         */
        float entranceOffset =
                (1.0f - animation) * 10.0f;

        matrices.translate(
                0.0f,
                entranceOffset,
                0.0f
        );

        int panelWidth =
                Math.min(
                        900,
                        logicalWidth - 40
                );

        int panelHeight =
                Math.min(
                        560,
                        logicalHeight - 40
                );

        panelWidth =
                Math.max(
                        520,
                        panelWidth
                );

        panelHeight =
                Math.max(
                        360,
                        panelHeight
                );

        int panelX =
                (logicalWidth - panelWidth) / 2;

        int panelY =
                (logicalHeight - panelHeight) / 2;

        /*
         * Subtle panel glow.
         */
        fill(
                matrices,
                panelX - 2,
                panelY - 2,
                panelX + panelWidth + 2,
                panelY + panelHeight + 2,
                AMOLEDTheme.GLOW
        );

        drawPanel(
                matrices,
                panelX,
                panelY,
                panelWidth,
                panelHeight
        );

        drawHeader(
                matrices,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                guiMouseX,
                guiMouseY
        );

        drawCategories(
                matrices,
                panelX,
                panelY,
                guiMouseX,
                guiMouseY
        );

        drawModules(
                matrices,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                guiMouseX,
                guiMouseY
        );

        if (settingsPanel.isOpen()) {

            int settingsWidth =
                    310;

            int settingsHeight =
                    panelHeight - 90;

            int settingsX =
                    panelX
                            + panelWidth
                            - settingsWidth
                            - 16;

            int settingsY =
                    panelY + 72;

            settingsPanel.setPosition(
                    settingsX,
                    settingsY
            );

            settingsPanel.setSize(
                    settingsWidth,
                    settingsHeight
            );

            settingsPanel.render(
                    matrices,
                    guiMouseX,
                    guiMouseY
            );
        }

        // Draw EXIT last so the settings panel can never cover it.
        drawExitButton(
                matrices,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                guiMouseX,
                guiMouseY
        );

        matrices.pop();
    }

    private void drawPanel(
            MatrixStack matrices,
            int x,
            int y,
            int width,
            int height
    ) {

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                AMOLEDTheme.PANEL
        );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 2,
                AMOLEDTheme.ACCENT
        );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                AMOLEDTheme.BORDER_LIGHT
        );

        fill(
                matrices,
                x,
                y + height - 1,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        fill(
                matrices,
                x,
                y,
                x + 1,
                y + height,
                AMOLEDTheme.BORDER
        );

        fill(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );
    }

    private void drawHeader(
            MatrixStack matrices,
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight,
            int mouseX,
            int mouseY
    ) {

        TextRenderer font =
                mc.textRenderer;

        int titleX =
                panelX + 22;

        int titleY =
                panelY + 18;

        boolean titleHovered =
                mouseX >= titleX - 4
                        && mouseX <= titleX + 120
                        && mouseY >= titleY - 6
                        && mouseY <= titleY + 14;

        int titleColor =
                titleHovered
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.TEXT;

        /*
         * Small title glow on hover.
         */
        if (titleHovered) {

            fill(
                    matrices,
                    titleX - 5,
                    titleY - 4,
                    titleX + 125,
                    titleY + 15,
                    AMOLEDTheme.GLOW
            );
        }

        drawItalicWithShadow(
                matrices,
                "ExternalVisuals",
                titleX,
                titleY,
                titleColor
        );

        drawItalicWithShadow(
                matrices,
                "PULSE UI  •  " + ExternalVisuals.VERSION,
                panelX + 22,
                panelY + 33,
                AMOLEDTheme.TEXT_MUTED
        );

        drawItalicWithShadow(
                matrices,
                "UI " + Math.round(uiScale * 100.0f) + "%",
                panelX + 120,
                panelY + 33,
                AMOLEDTheme.TEXT_MUTED
        );

        drawScaleControls(
                matrices,
                panelX + 18,
                panelY + panelHeight - 40,
                mouseX,
                mouseY
        );

        drawHudEditorButton(
                matrices,
                panelX,
                panelY,
                mouseX,
                mouseY
        );

        int searchWidth =
                190;

        int searchHeight =
                24;

        int searchX =
                panelX
                        + panelWidth
                        - searchWidth
                        - 20;

        int searchY =
                panelY + 17;

        searchBar.setPosition(
                searchX,
                searchY
        );

        searchBar.setSize(
                searchWidth,
                searchHeight
        );

        searchBar.render(
                matrices,
                mouseX,
                mouseY
        );

        String searchText =
                searchBar.getSearchText();

        if (!searchText.isEmpty()) {

            drawItalicWithShadow(
                    matrices,
                    searchText,
                    searchX + 10,
                    searchY + 8,
                    AMOLEDTheme.TEXT
            );

        } else {

            drawItalicWithShadow(
                    matrices,
                    "Search modules...",
                    searchX + 10,
                    searchY + 8,
                    AMOLEDTheme.TEXT_MUTED
            );
        }

        if (searchBar.hasText()) {

            drawItalicWithShadow(
                    matrices,
                    "x",
                    searchX + searchWidth - 18,
                    searchY + 8,
                    AMOLEDTheme.TEXT_MUTED
            );
        }

        fill(
                matrices,
                panelX + 18,
                panelY + 55,
                panelX + panelWidth - 18,
                panelY + 56,
                AMOLEDTheme.DIVIDER
        );
    }

    private void drawCategories(
            MatrixStack matrices,
            int panelX,
            int panelY,
            int mouseX,
            int mouseY
    ) {

        int navigationX =
                panelX + 18;

        int navigationY =
                panelY + 72;

        int navigationWidth =
                145;

        int itemHeight =
                34;

        ModuleCategory[] categories =
                ModuleCategory.values();

        for (int i = 0;
             i < categories.length;
             i++) {

            ModuleCategory category =
                    categories[i];

            int itemY =
                    navigationY
                            + i * (itemHeight + 4);

            boolean selected =
                    category == selectedCategory;

            boolean hovered =
                    mouseX >= navigationX
                            && mouseX <= navigationX
                            + navigationWidth
                            && mouseY >= itemY
                            && mouseY <= itemY
                            + itemHeight;

            float current =
                    getCategoryAnimation(
                            category
                    );

            float target =
                    selected
                            ? 1.0f
                            : hovered
                            ? 0.55f
                            : 0.0f;

            current =
                    AMOLEDTheme.animate(
                            current,
                            target,
                            AMOLEDTheme.HOVER_SPEED
                    );

            categoryAnimations.put(
                    category,
                    current
            );

            int background =
                    interpolateColor(
                            AMOLEDTheme.PANEL_DARK,
                            AMOLEDTheme.HOVER,
                            current
                    );

            if (selected) {

                background =
                        interpolateColor(
                                background,
                                AMOLEDTheme.SELECTED,
                                current
                        );
            }

            fill(
                    matrices,
                    navigationX,
                    itemY,
                    navigationX + navigationWidth,
                    itemY + itemHeight,
                    background
            );

            if (current > 0.001f) {

                int glowAlpha =
                        (int) (
                                45.0f
                                        * current
                        );

                fill(
                        matrices,
                        navigationX,
                        itemY,
                        navigationX + navigationWidth,
                        itemY + 1,
                        withAlpha(
                                AMOLEDTheme.ACCENT_LIGHT,
                                glowAlpha
                        )
                );
            }

            if (selected) {

                fill(
                        matrices,
                        navigationX,
                        itemY,
                        navigationX + 3,
                        itemY + itemHeight,
                        AMOLEDTheme.ACCENT
                );
            }

            drawItalicWithShadow(
                    matrices,
                    category.getDisplayName(),
                    navigationX + 12,
                    itemY + 11,
                    selected
                            ? AMOLEDTheme.TEXT
                            : AMOLEDTheme.TEXT_LIGHT
            );
        }
    }

    private void drawModules(
            MatrixStack matrices,
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight,
            int mouseX,
            int mouseY
    ) {

        int contentX =
                panelX + 185;

        int contentY =
                panelY + 72;

        int contentWidth =
                panelWidth - 205;

        int cardWidth =
                Math.max(
                        180,
                        (contentWidth - 14) / 2
                );

        int cardHeight =
                72;

        int visibleModules = 0;
        int totalRows = 0;

        String search =
                searchBar
                        .getSearchText()
                        .toLowerCase();

        for (Module module :
                moduleManager.getModules()) {

            if (module == null) {
                continue;
            }

            if (isSecretModule(module)) {
                continue;
            }

            if (
                    module.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (!search.isEmpty()) {

                String moduleName =
                        module.getName()
                                .toLowerCase();

                if (!moduleName.contains(
                        search
                )) {
                    continue;
                }
            }

            int logicalRow = visibleModules / 2;
            int column = visibleModules % 2;
            int visibleRow = logicalRow - moduleScroll;

            if (visibleRow < 0) {
                visibleModules++;
                continue;
            }

            if (visibleRow >= 5) {
                totalRows = Math.max(totalRows, logicalRow + 1);
                visibleModules++;
                continue;
            }

            int cardX =
                    contentX
                            + column
                            * (cardWidth + 14);

            int cardY =
                    contentY
                            + visibleRow
                            * (cardHeight + 12);

            drawModuleCard(
                    matrices,
                    module,
                    cardX,
                    cardY,
                    cardWidth,
                    cardHeight,
                    mouseX,
                    mouseY
            );

            visibleModules++;
            totalRows = Math.max(totalRows, logicalRow + 1);
        }

        int maxModuleScroll = Math.max(0, totalRows - 5);
        moduleScroll = Math.max(0, Math.min(moduleScroll, maxModuleScroll));

        if (maxModuleScroll > 0) {
            int trackX = panelX + panelWidth - 11;
            int trackTop = contentY;
            int trackBottom = contentY + 5 * (cardHeight + 12) - 12;
            int trackHeight = Math.max(1, trackBottom - trackTop);
            int thumbHeight = Math.max(26, trackHeight * 5 / Math.max(5, totalRows));
            int thumbTravel = Math.max(0, trackHeight - thumbHeight);
            int thumbY = trackTop + (int) Math.round(
                    thumbTravel * (moduleScroll / (double) maxModuleScroll)
            );

            fill(matrices, trackX, trackTop, trackX + 2, trackBottom, AMOLEDTheme.INPUT);
            fill(matrices, trackX - 1, thumbY, trackX + 3, thumbY + thumbHeight, AMOLEDTheme.ACCENT);
        }

        if (
                visibleModules == 0
                        && !search.isEmpty()
        ) {

            drawItalicWithShadow(
                    matrices,
                    "No modules found",
                    contentX + 18,
                    contentY + 18,
                    AMOLEDTheme.TEXT_MUTED
            );
        }
    }

    private void drawModuleCard(
            MatrixStack matrices,
            Module module,
            int x,
            int y,
            int width,
            int height,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + height;

        boolean enabled =
                module.isEnabled();

        float hoverProgress =
                getModuleHoverAnimation(
                        module
                );

        float enabledProgress =
                getModuleEnabledAnimation(
                        module
                );

        float hoverTarget =
                hovered
                        ? 1.0f
                        : 0.0f;

        float enabledTarget =
                enabled
                        ? 1.0f
                        : 0.0f;

        hoverProgress =
                AMOLEDTheme.animate(
                        hoverProgress,
                        hoverTarget,
                        AMOLEDTheme.HOVER_SPEED
                );

        enabledProgress =
                AMOLEDTheme.animate(
                        enabledProgress,
                        enabledTarget,
                        AMOLEDTheme.TOGGLE_SPEED
                );

        moduleHoverAnimations.put(
                module,
                hoverProgress
        );

        moduleEnabledAnimations.put(
                module,
                enabledProgress
        );

        /*
         * Outer purple glow.
         */
        if (enabledProgress > 0.01f) {

            int glowAlpha =
                    (int) (
                            28.0f
                                    * enabledProgress
                    );

            fill(
                    matrices,
                    x - 2,
                    y - 2,
                    x + width + 2,
                    y + height + 2,
                    withAlpha(
                            AMOLEDTheme.ACCENT,
                            glowAlpha
                    )
            );
        }

        /*
         * Hover glow.
         */
        if (hoverProgress > 0.01f) {

            int hoverAlpha =
                    (int) (
                            24.0f
                                    * hoverProgress
                    );

            fill(
                    matrices,
                    x - 1,
                    y - 1,
                    x + width + 1,
                    y + height + 1,
                    withAlpha(
                            AMOLEDTheme.ACCENT_LIGHT,
                            hoverAlpha
                    )
            );
        }

        int background =
                interpolateColor(
                        AMOLEDTheme.PANEL_LIGHT,
                        AMOLEDTheme.HOVER,
                        hoverProgress
                );

        background =
                interpolateColor(
                        background,
                        AMOLEDTheme.SELECTED,
                        enabledProgress
                );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                background
        );

        /*
         * Animated left accent.
         */
        if (enabledProgress > 0.001f) {

            fill(
                    matrices,
                    x,
                    y,
                    x + 3,
                    y + height,
                    withAlpha(
                            AMOLEDTheme.ACCENT,
                            (int) (
                                    255.0f
                                            * enabledProgress
                            )
                    )
            );
        }

        /*
         * Top highlight.
         */
        float highlight =
                Math.max(
                        hoverProgress,
                        enabledProgress
                );

                if (highlight > 0.001f) {

            fill(
                    matrices,
                    x,
                    y,
                    x + width,
                    y + 1,
                    withAlpha(
                            AMOLEDTheme.ACCENT_LIGHT,
                            (int) (
                                    70.0f
                                            * highlight
                            )
                    )
            );
        }

        /*
         * Borders.
         */
        int border =
                interpolateColor(
                        AMOLEDTheme.BORDER,
                        AMOLEDTheme.SELECTED_BORDER,
                        enabledProgress
                );

        border =
                interpolateColor(
                        border,
                        AMOLEDTheme.BORDER_LIGHT,
                        hoverProgress * 0.65f
                );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                border
        );

        fill(
                matrices,
                x,
                y + height - 1,
                x + width,
                y + height,
                AMOLEDTheme.BORDER
        );

        fill(
                matrices,
                x,
                y,
                x + 1,
                y + height,
                border
        );

        fill(
                matrices,
                x + width - 1,
                y,
                x + width,
                y + height,
                border
        );

        TextRenderer font =
                mc.textRenderer;

        /*
         * Small status indicator.
         */
        int indicatorColor =
                enabled
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.TEXT_MUTED;

        fill(
                matrices,
                x + 13,
                y + 12,
                x + 17,
                y + 16,
                indicatorColor
        );

        drawItalicWithShadow(
                matrices,
                module.getName(),
                x + 22,
                y + 10,
                enabled
                        ? AMOLEDTheme.TEXT
                        : AMOLEDTheme.TEXT_LIGHT
        );

        drawItalicWithShadow(
                matrices,
                enabled
                        ? "Enabled"
                        : "Disabled",
                x + 13,
                y + 31,
                enabled
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.TEXT_MUTED
        );

        /* Explicit settings button: it remains visible in the compact card. */
        int settingsWidth = 64;
        int settingsHeight = 18;
        int settingsX = x + width - settingsWidth - 10;
        int settingsY = y + height - settingsHeight - 8;
        boolean settingsHover = mouseX >= settingsX && mouseX <= settingsX + settingsWidth
                && mouseY >= settingsY && mouseY <= settingsY + settingsHeight;
        fill(matrices, settingsX, settingsY, settingsX + settingsWidth, settingsY + settingsHeight,
                settingsHover ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON);
        drawItalicWithShadow(matrices, "SETTINGS", settingsX + 7, settingsY + 5,
                settingsHover ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.TEXT_MUTED);

        /*
         * Animated toggle.
         */
        int toggleWidth =
                38;

        int toggleHeight =
                18;

        int toggleX =
                x + width
                        - toggleWidth
                        - 12;

        int toggleY =
                y + 13;

        int toggleColor =
                interpolateColor(
                        AMOLEDTheme.TOGGLE_OFF,
                        AMOLEDTheme.TOGGLE_ON,
                        enabledProgress
                );

        fill(
                matrices,
                toggleX,
                toggleY,
                toggleX + toggleWidth,
                toggleY + toggleHeight,
                toggleColor
        );

        int knobStart =
                toggleX + 3;

        int knobEnd =
                toggleX + 21;

        int knobX =
                Math.round(
                        knobStart
                                + (
                                knobEnd
                                        - knobStart
                        ) * enabledProgress
                );

        fill(
                matrices,
                knobX,
                toggleY + 3,
                knobX + 14,
                toggleY + 15,
                AMOLEDTheme.TOGGLE_KNOB
        );

        /*
         * Tiny active glow around toggle.
         */
        if (enabledProgress > 0.01f) {

            fill(
                    matrices,
                    toggleX - 1,
                    toggleY - 1,
                    toggleX + toggleWidth + 1,
                    toggleY,
                    withAlpha(
                            AMOLEDTheme.ACCENT_LIGHT,
                            (int) (
                                    60.0f
                                            * enabledProgress
                            )
                    )
            );
        }
    }

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (moduleManager == null) {
            return false;
        }

        int screenWidth =
                mc.getWindow()
                        .getScaledWidth();

        int screenHeight =
                mc.getWindow()
                        .getScaledHeight();

        int logicalWidth = Math.max(320, Math.round(screenWidth / uiScale));
        int logicalHeight = Math.max(240, Math.round(screenHeight / uiScale));

        int panelWidth =
                Math.min(
                        900,
                        logicalWidth - 40
                );

        int panelHeight =
                Math.min(
                        560,
                        logicalHeight - 40
                );

        panelWidth =
                Math.max(520, panelWidth);

        panelHeight =
                Math.max(360, panelHeight);

        int panelX =
                (logicalWidth - panelWidth) / 2;

        int panelY =
                (logicalHeight - panelHeight) / 2;

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        mouseX = centerX + (mouseX - centerX) / uiScale;
        mouseY = centerY + (mouseY - centerY) / uiScale;

        /*
         * EXIT must work even while settings are open.
         */
        if (button == 0
                && isExitHovered(
                mouseX,
                mouseY,
                panelX,
                panelY,
                panelWidth,
                panelHeight
        )) {

            mc.openScreen(null);
            return true;
        }

        if (settingsPanel.isOpen()) {

            if (
                    settingsPanel.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }

            if (button == 0) {
                return true;
            }
        }

        if (button == 0) {
            int searchX =
                    panelX
                            + panelWidth
                            - 190
                            - 20;

            int searchY =
                    panelY + 17;

            if (searchBar.hasText()
                    && mouseX >= searchX + 162
                    && mouseX <= searchX + 190
                    && mouseY >= searchY
                    && mouseY <= searchY + 24) {
                searchBar.clear();
                searchBar.setFocused(false);
                moduleScroll = 0;
                return true;
            }

            if (searchBar.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {
                return true;
            }
        }

        /*
         * Secret title click area.
         */
        if (button == 0) {

            int titleX =
                    panelX + 22;

            int titleY =
                    panelY + 18;

            if (
                    mouseX >= titleX - 4
                            && mouseX <= titleX + 120
                            && mouseY >= titleY - 6
                            && mouseY <= titleY + 14
            ) {

                handleSecretClick();

                return true;
            }
        }

        /*
         * HUD editor.
         */
        if (
                button == 0
                        && isHudEditorHovered(
                        mouseX,
                        mouseY,
                        panelX,
                        panelY
                )
        ) {
            mc.openScreen(
                    new HudEditorScreen()
            );
            return true;
        }

        /*
         * UI scale controls.
         */
        if (button == 0) {
            if (isScaleButtonHovered(mouseX, mouseY, panelX + 18, panelY + panelHeight - 40, 0)) {
                setUiScale(uiScale - 0.05f);
                return true;
            }

            if (isScaleButtonHovered(mouseX, mouseY, panelX + 18, panelY + panelHeight - 40, 1)) {
                setUiScale(AMOLEDTheme.DEFAULT_SCALE);
                return true;
            }

            if (isScaleButtonHovered(mouseX, mouseY, panelX + 18, panelY + panelHeight - 40, 2)) {
                setUiScale(uiScale + 0.05f);
                return true;
            }
        }

        /*
         * Categories.
         */
        if (button == 0) {

            int navigationX =
                    panelX + 18;

            int navigationY =
                    panelY + 72;

            int navigationWidth =
                    145;

            int itemHeight =
                    34;

            ModuleCategory[] categories =
                    ModuleCategory.values();

            for (int i = 0;
                 i < categories.length;
                 i++) {

                int itemY =
                        navigationY
                                + i
                                * (itemHeight + 4);

                if (
                        mouseX >= navigationX
                                && mouseX <= navigationX
                                + navigationWidth
                                && mouseY >= itemY
                                && mouseY <= itemY
                                + itemHeight
                ) {

                    selectedCategory =
                            categories[i];
                    moduleScroll = 0;

                    return true;
                }
            }
        }

        /*
         * Module interaction.
         */
        for (Module module :
                moduleManager.getModules()) {

            if (module == null) {
                continue;
            }

            if (isSecretModule(module)) {
                continue;
            }

            if (
                    module.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (!matchesSearch(
                    module
            )) {
                continue;
            }

            int contentX =
                    panelX + 185;

            int contentY =
                    panelY + 72;

            int contentWidth =
                    panelWidth - 205;

            int cardWidth =
                    Math.max(
                            180,
                            (contentWidth - 14) / 2
                    );

            int cardHeight =
                    72;

            int visibleIndex =
                    getVisibleModuleIndex(
                            module
                    );

            if (visibleIndex < 0) {
                continue;
            }

            int column =
                    visibleIndex % 2;

            int row =
                    visibleIndex / 2;

            int cardX =
                    contentX
                            + column
                            * (cardWidth + 14);

            int visibleRow = row - moduleScroll;

            if (visibleRow < 0 || visibleRow >= 5) {
                continue;
            }

            int cardY =
                    contentY
                            + visibleRow
                            * (cardHeight + 12);

            boolean inside =
                    mouseX >= cardX
                            && mouseX <= cardX + cardWidth
                            && mouseY >= cardY
                            && mouseY <= cardY + cardHeight;

            if (!inside) {
                continue;
            }

            selectedModule = module;

            int settingsWidth = 64;
            int settingsHeight = 18;
            int settingsX = cardX + cardWidth - settingsWidth - 10;
            int settingsY = cardY + cardHeight - settingsHeight - 8;
            boolean settingsHovered = mouseX >= settingsX && mouseX <= settingsX + settingsWidth
                    && mouseY >= settingsY && mouseY <= settingsY + settingsHeight;

            /* Explicit SETTINGS button opens the module panel. */
            if (button == 0 && settingsHovered) {

                settingsPanel.open(
                        module
                );

                return true;
            }

            /*
             * Left click elsewhere toggles.
             */
            if (button == 0) {

                module.toggle();

                return true;
            }

            /*
             * Right click still opens settings
             * as a convenient shortcut.
             */
            if (button == 1) {

                settingsPanel.open(
                        module
                );

                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        if (settingsPanel.isOpen()) {
            int centerX = mc.getWindow().getScaledWidth() / 2;
            int centerY = mc.getWindow().getScaledHeight() / 2;
            mouseX = centerX + (mouseX - centerX) / uiScale;
            mouseY = centerY + (mouseY - centerY) / uiScale;
            deltaX /= uiScale;
            deltaY /= uiScale;

            return settingsPanel.mouseDragged(
                    mouseX,
                    mouseY,
                    button,
                    deltaX,
                    deltaY
            );
        }

        return false;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (settingsPanel.isOpen()) {
            int centerX = mc.getWindow().getScaledWidth() / 2;
            int centerY = mc.getWindow().getScaledHeight() / 2;
            mouseX = centerX + (mouseX - centerX) / uiScale;
            mouseY = centerY + (mouseY - centerY) / uiScale;

            return settingsPanel.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            );
        }

        return false;
    }

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ) {
        int centerX = mc.getWindow().getScaledWidth() / 2;
        int centerY = mc.getWindow().getScaledHeight() / 2;
        mouseX = centerX + (mouseX - centerX) / uiScale;
        mouseY = centerY + (mouseY - centerY) / uiScale;

        if (settingsPanel.isOpen()) {
            return settingsPanel.mouseScrolled(mouseX, mouseY, amount);
        }

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        int logicalWidth = Math.max(320, Math.round(screenWidth / uiScale));
        int logicalHeight = Math.max(240, Math.round(screenHeight / uiScale));
        int panelWidth = Math.max(520, Math.min(900, logicalWidth - 40));
        int panelHeight = Math.max(360, Math.min(560, logicalHeight - 40));
        int panelX = (logicalWidth - panelWidth) / 2;
        int panelY = (logicalHeight - panelHeight) / 2;

        int contentX = panelX + 185;
        int contentY = panelY + 72;
        int contentRight = panelX + panelWidth - 18;
        int contentBottom = panelY + panelHeight - 18;

        if (mouseX >= contentX && mouseX <= contentRight
                && mouseY >= contentY && mouseY <= contentBottom) {
            moduleScroll += amount < 0.0 ? 1 : -1;
            moduleScroll = Math.max(0, Math.min(getMaxModuleScroll(), moduleScroll));
            return true;
        }

        return false;
    }

    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (keyCode == 45 || keyCode == 334) {
            setUiScale(uiScale - 0.05f);
            return true;
        }

        if (keyCode == 61 || keyCode == 335) {
            setUiScale(uiScale + 0.05f);
            return true;
        }

        if (settingsPanel.isOpen()) {

            if (keyCode == 256) {

                settingsPanel.close();

                return true;
            }

            if ((keyCode == 257 || keyCode == 335)
                    && settingsPanel.getModule() != null) {

                settingsPanel.getModule().toggle();
                return true;
            }

            if (
                    settingsPanel.keyPressed(
                            keyCode,
                            scanCode,
                            modifiers
                    )
            ) {
                return true;
            }

            return true;
        }

        if (
                searchBar.keyPressed(
                        keyCode,
                        scanCode,
                        modifiers
                )
        ) {
            moduleScroll = 0;
            return true;
        }

        /*
         * ENTER toggles the last selected module.
         * Numpad ENTER is supported as well.
         */
        if ((keyCode == 257 || keyCode == 335)
                && selectedModule != null) {

            selectedModule.toggle();

            return true;
        }

        return false;
    }

    public boolean charTyped(
            char chr,
            int modifiers
    ) {

        if (settingsPanel.isOpen()) {

            if (
                    settingsPanel.charTyped(
                            chr,
                            modifiers
                    )
            ) {
                return true;
            }

            return true;
        }

        boolean typed = searchBar.charTyped(
                chr,
                modifiers
        );
        if (typed) {
            moduleScroll = 0;
        }
        return typed;
    }

    private void drawScaleControls(
            MatrixStack matrices,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {
        int size = 18;
        int gap = 3;

        for (int i = 0; i < 3; i++) {
            int bx = x + i * (size + gap);
            boolean hovered = isScaleButtonHovered(mouseX, mouseY, x, y, i);

            fill(
                    matrices,
                    bx,
                    y,
                    bx + size,
                    y + size,
                    hovered ? AMOLEDTheme.BUTTON_HOVER : AMOLEDTheme.BUTTON
            );

            fill(
                    matrices,
                    bx,
                    y,
                    bx + size,
                    y + 1,
                    hovered ? AMOLEDTheme.ACCENT_LIGHT : AMOLEDTheme.BORDER
            );

            String label = i == 0 ? "-" : i == 1 ? "1" : "+";
            drawItalicWithShadow(
                    matrices,
                    label,
                    bx + 6,
                    y + 4,
                    hovered ? AMOLEDTheme.TEXT : AMOLEDTheme.TEXT_MUTED
            );
        }
    }

    private boolean isScaleButtonHovered(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int index
    ) {
        int size = 18;
        int gap = 3;
        int bx = x + index * (size + gap);

        return mouseX >= bx
                && mouseX <= bx + size
                && mouseY >= y
                && mouseY <= y + size;
    }

    private void drawHudEditorButton(
            MatrixStack matrices,
            int panelX,
            int panelY,
            int mouseX,
            int mouseY
    ) {
        int width = 94;
        int height = 22;

        int x = panelX + 210;
        int y = panelY + 17;

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + height;

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                hovered
                        ? AMOLEDTheme.BUTTON_HOVER
                        : AMOLEDTheme.BUTTON
        );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 2,
                hovered
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.BORDER
        );

        drawItalicWithShadow(
                matrices,
                "HUD EDITOR",
                x + 10,
                y + 7,
                hovered
                        ? AMOLEDTheme.TEXT
                        : AMOLEDTheme.TEXT_LIGHT
        );
    }

    private boolean isHudEditorHovered(
            double mouseX,
            double mouseY,
            int panelX,
            int panelY
    ) {
        int x = panelX + 210;
        int y = panelY + 17;
        int width = 94;
        int height = 22;

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    private void drawExitButton(
            MatrixStack matrices,
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight,
            int mouseX,
            int mouseY
    ) {

        int width = 74;
        int height = 22;

        int x =
                panelX + panelWidth - width - 18;

        int y =
                panelY + panelHeight - height - 14;

        boolean hovered =
                isExitHovered(
                        mouseX,
                        mouseY,
                        panelX,
                        panelY,
                        panelWidth,
                        panelHeight
                );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + height,
                hovered
                        ? AMOLEDTheme.BUTTON_HOVER
                        : AMOLEDTheme.BUTTON
        );

        fill(
                matrices,
                x,
                y,
                x + width,
                y + 1,
                hovered
                        ? AMOLEDTheme.ACCENT_LIGHT
                        : AMOLEDTheme.BORDER
        );

        drawItalicWithShadow(
                matrices,
                "EXIT",
                x + 22,
                y + 7,
                hovered
                        ? AMOLEDTheme.TEXT
                        : AMOLEDTheme.TEXT_LIGHT
        );
    }

    private boolean isExitHovered(
            double mouseX,
            double mouseY,
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight
    ) {

        int width = 74;
        int height = 22;

        int x =
                panelX + panelWidth - width - 18;

        int y =
                panelY + panelHeight - height - 14;

        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    private void handleSecretClick() {

        long now =
                System.currentTimeMillis();

        if (
                lastSecretClickTime == 0L
                        || now - lastSecretClickTime
                        > SECRET_CLICK_WINDOW
        ) {

            secretClicks = 0;
        }

        lastSecretClickTime =
                now;

        secretClicks++;

        if (
                secretClicks
                        >= SECRET_CLICK_TARGET
        ) {

            secretClicks = 0;

            lastSecretClickTime =
                    0L;

            if (mc != null) {

                mc.openScreen(
                        new SecretMenuScreen()
                );
            }
        }
    }

    private float getModuleHoverAnimation(
            Module module
    ) {

        Float value =
                moduleHoverAnimations.get(
                        module
                );

        if (value == null) {
            return 0.0f;
        }

        return value;
    }

    private float getModuleEnabledAnimation(
            Module module
    ) {

        Float value =
                moduleEnabledAnimations.get(
                        module
                );

        if (value == null) {

            return module.isEnabled()
                    ? 1.0f
                    : 0.0f;
        }

        return value;
    }

    private float getCategoryAnimation(
            ModuleCategory category
    ) {

        Float value =
                categoryAnimations.get(
                        category
                );

        if (value == null) {
            return 0.0f;
        }

        return value;
    }

    /**
     * Advanced combat/render modules are intentionally hidden from the normal
     * ClickGUI. They remain registered and are exposed by SecretMenuScreen.
     */
    public static boolean isSecretModule(Module module) {
        if (module == null) {
            return false;
        }

        String name = module.getName();
        return "ESP".equalsIgnoreCase(name)
                || "Aim Assist".equalsIgnoreCase(name);
    }

    private int getMaxModuleScroll() {
        int count = 0;
        for (Module module : moduleManager.getModules()) {
            if (module == null) {
                continue;
            }
            if (module.getCategory() != selectedCategory) {
                continue;
            }
            if (!matchesSearch(module)) {
                continue;
            }
            count++;
        }
        int rows = (count + 1) / 2;
        return Math.max(0, rows - 5);
    }

    private boolean matchesSearch(
            Module module
    ) {

        if (module == null || isSecretModule(module)) {
            return false;
        }

        String search =
                searchBar
                        .getSearchText()
                        .toLowerCase();

        if (search.isEmpty()) {
            return true;
        }

        return module.getName()
                .toLowerCase()
                .contains(search);
    }

    private int getVisibleModuleIndex(
            Module target
    ) {

        int index = 0;

        for (Module module :
                moduleManager.getModules()) {

            if (module == null) {
                continue;
            }

            if (
                    module.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (!matchesSearch(
                    module
            )) {
                continue;
            }

            if (module == target) {
                return index;
            }

            index++;
        }

        return -1;
    }

    public float getUiScale() {
        return uiScale;
    }

    public void setUiScale(float scale) {
        uiScale =
                AMOLEDTheme.clampScale(scale);
        savedUiScale = uiScale;
    }


    private void drawItalicWithShadow(
            MatrixStack matrices,
            String text,
            float x,
            float y,
            int color
    ) {

        if (text == null) {
            return;
        }

        LiteralText styled =
                new LiteralText(text);

        styled.setStyle(
                Style.EMPTY.withItalic(true)
        );

        mc.textRenderer.drawWithShadow(
                matrices,
                styled,
                x,
                y,
                color
        );
    }

    private static int interpolateColor(
            int first,
            int second,
            float progress
    ) {

        progress =
                Math.max(
                        0.0f,
                        Math.min(
                                1.0f,
                                progress
                        )
                );

        int firstA =
                (first >> 24) & 0xFF;

        int firstR =
                (first >> 16) & 0xFF;

        int firstG =
                (first >> 8) & 0xFF;

        int firstB =
                first & 0xFF;

        int secondA =
                (second >> 24) & 0xFF;

        int secondR =
                (second >> 16) & 0xFF;

        int secondG =
                (second >> 8) & 0xFF;

        int secondB =
                second & 0xFF;

        int alpha =
                Math.round(
                        firstA
                                + (
                                secondA - firstA
                        ) * progress
                );

        int red =
                Math.round(
                        firstR
                                + (
                                secondR - firstR
                        ) * progress
                );

        int green =
                Math.round(
                        firstG
                                + (
                                secondG - firstG
                        ) * progress
                );

        int blue =
                Math.round(
                        firstB
                                + (
                                secondB - firstB
                        ) * progress
                );

        return (alpha << 24)
                | (red << 16)
                | (green << 8)
                | blue;
    }

    private static int withAlpha(
            int color,
            int alpha
    ) {

        alpha =
                Math.max(
                        0,
                        Math.min(
                                255,
                                alpha
                        )
                );

        return (alpha << 24)
                | (color & 0x00FFFFFF);
    }

    private static void fill(
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
}

    