package com.externalvisuals.gui;

import com.externalvisuals.module.Module;
import com.externalvisuals.module.ModuleCategory;
import com.externalvisuals.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public final class ModernGuiRenderer {

    private final MinecraftClient mc =
            MinecraftClient.getInstance();

    private final ModuleManager moduleManager;

    private final GuiSearchBar searchBar;

    private final ModuleSettingsPanel settingsPanel;

    private ModuleCategory selectedCategory;

    private float animation;

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

        int panelWidth =
                Math.min(
                        900,
                        screenWidth - 40
                );

        int panelHeight =
                Math.min(
                        560,
                        screenHeight - 40
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
                (screenWidth - panelWidth) / 2;

        int panelY =
                (screenHeight - panelHeight) / 2;

        fill(
                matrices,
                0,
                0,
                screenWidth,
                screenHeight,
                AMOLEDTheme.BACKGROUND
        );

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
                mouseX,
                mouseY
        );

        drawCategories(
                matrices,
                panelX,
                panelY,
                mouseX,
                mouseY
        );

        drawModules(
                matrices,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                mouseX,
                mouseY
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
                    mouseX,
                    mouseY
            );
        }
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

        font.drawWithShadow(
                matrices,
                "ExternalVisuals",
                titleX,
                titleY,
                titleColor
        );

        font.drawWithShadow(
                matrices,
                "1.2.0",
                panelX + 22,
                panelY + 33,
                AMOLEDTheme.TEXT_MUTED
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

            font.drawWithShadow(
                    matrices,
                    searchText,
                    searchX + 10,
                    searchY + 8,
                    AMOLEDTheme.TEXT
            );

        } else {

            font.drawWithShadow(
                    matrices,
                    "Search modules...",
                    searchX + 10,
                    searchY + 8,
                    AMOLEDTheme.TEXT_MUTED
            );
        }

        if (searchBar.hasText()) {

            font.drawWithShadow(
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

            mc.textRenderer.drawWithShadow(
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

        int column = 0;
        int row = 0;

        int visibleModules = 0;

        String search =
                searchBar
                        .getSearchText()
                        .toLowerCase();

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

            int cardX =
                    contentX
                            + column
                            * (cardWidth + 14);

            int cardY =
                    contentY
                            + row
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

            column++;

            if (column >= 2) {
                column = 0;
                row++;
            }

            if (row >= 5) {
                break;
            }
        }

        if (
                visibleModules == 0
                        && !search.isEmpty()
        ) {

            mc.textRenderer.drawWithShadow(
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

        font.drawWithShadow(
                matrices,
                module.getName(),
                x + 22,
                y + 10,
                enabled
                        ? AMOLEDTheme.TEXT
                        : AMOLEDTheme.TEXT_LIGHT
        );

        font.drawWithShadow(
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

            if (
                    searchBar.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }
        }

        int screenWidth =
                mc.getWindow()
                        .getScaledWidth();

        int screenHeight =
                mc.getWindow()
                        .getScaledHeight();

        int panelWidth =
                Math.min(
                        900,
                        screenWidth - 40
                );

        int panelHeight =
                Math.min(
                        560,
                        screenHeight - 40
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
                (screenWidth - panelWidth) / 2;

        int panelY =
                (screenHeight - panelHeight) / 2;

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
         * Search clear.
         */
        if (
                button == 0
                        && searchBar.hasText()
        ) {

            int searchX =
                    panelX
                            + panelWidth
                            - 190
                            - 20;

            int searchY =
                    panelY + 17;

            if (
                    mouseX >= searchX + 165
                            && mouseX <= searchX + 190
                            && mouseY >= searchY
                            && mouseY <= searchY + 24
            ) {

                searchBar.clear();

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

            if (row >= 5) {
                continue;
            }

            int cardX =
                    contentX
                            + column
                            * (cardWidth + 14);

            int cardY =
                    contentY
                            + row
                            * (cardHeight + 12);

            boolean inside =
                    mouseX >= cardX
                            && mouseX <= cardX + cardWidth
                            && mouseY >= cardY
                            && mouseY <= cardY + cardHeight;

            if (!inside) {
                continue;
            }

            /*
             * Left click toggles.
             */
            if (button == 0) {

                module.toggle();

                return true;
            }

            /*
             * Right click opens settings.
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

            return settingsPanel.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            );
        }

        return false;
    }

    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (settingsPanel.isOpen()) {

            if (keyCode == 256) {

                settingsPanel.close();

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

        return searchBar.charTyped(
                chr,
                modifiers
        );
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

    private boolean matchesSearch(
            Module module
    ) {

        if (module == null) {
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

    