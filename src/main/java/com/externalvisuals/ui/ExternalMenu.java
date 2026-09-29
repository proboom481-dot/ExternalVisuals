package com.externalvisuals.ui;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.config.ExternalConfig;
import com.externalvisuals.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class ExternalMenu extends Screen {
    private final Screen parent;
    private final List<Module> visible = new ArrayList<>();
    private int category = 1;
    private int clicks;
    private long firstClick;
    private String search = "";

    private static final String[] CATS = {"Combat", "Visuals", "Render", "Player", "Misc"};
    private static final String[] ICONS = {"⚔", "◆", "◈", "●", "⋯"};
    private static final int PURPLE = 0xFFD06CFF;
    private static final int WHITE = 0xFFF5F3F8;
    private static final int MUTED = 0xFF77747E;

    public ExternalMenu(Screen parent) {
        super(new LiteralText("ExternalVisuals"));
        this.parent = parent;
    }

    @Override protected void init() { rebuild(); }

    private void rebuild() {
        children.clear();
        buttons.clear();
        visible.clear();

        for (Module m : ExternalVisuals.MODULES.getModules()) {
            boolean catOk = category == 1 || "Visuals".equals(m.getCategory());
            if (catOk && (search.isEmpty() || m.getName().toLowerCase().contains(search.toLowerCase())))
                visible.add(m);
        }

        // Compact top controls
        addButton(new ButtonWidget(width - 178, 26, 72, 22,
                new LiteralText("§7Search"), b -> { search = search.isEmpty() ? " " : ""; rebuild(); }));
        addButton(new ButtonWidget(width - 100, 26, 72, 22,
                new LiteralText("§dClose"), b -> close()));

        // Five-click logo hotspot
        addButton(new ButtonWidget(width / 2 - 80, 18, 160, 28,
                new LiteralText("§d✦ §fEXTERNAL §dVISUALS"), b -> secretClick()));

        // Sidebar
        for (int i = 0; i < CATS.length; i++) {
            final int idx = i;
            addButton(new ButtonWidget(26, 132 + i * 48, 50, 38,
                    new LiteralText((idx == category ? "§d" : "§7") + ICONS[i]),
                    b -> { category = idx; rebuild(); }));
        }

        // Module rows, screenshot-style: large translucent row + right-side toggle.
        int startY = 126;
        int row = 0;
        for (Module module : visible) {
            if (row >= 7) break;
            final Module m = module;
            int y = startY + row * 64;
            addButton(new ButtonWidget(110, y, Math.max(190, width - 330), 48,
                    new LiteralText("§f" + m.getName()), b -> {
                        m.toggle();
                        ExternalConfig.save();
                        rebuild();
                    }));
            addButton(new ButtonWidget(width - 125, y + 9, 62, 30,
                    new LiteralText(m.isEnabled() ? "§f●" : "§8●"),
                    b -> { m.toggle(); ExternalConfig.save(); rebuild(); }));
            row++;
        }

        if (visible.isEmpty()) {
            addButton(new ButtonWidget(110, 126, 250, 42,
                    new LiteralText("§7No modules in this section"), b -> {}));
        }
    }

    private void secretClick() {
        long now = System.currentTimeMillis();
        if (now - firstClick > 2000) { firstClick = now; clicks = 0; }
        if (++clicks >= 5) {
            clicks = 0;
            client.openScreen(new SecretMenu(this));
        }
    }

    @Override
    public void render(MatrixStack m, int mouseX, int mouseY, float delta) {
        // Deep blurred-looking layers.
        fill(m, 0, 0, width, height, 0xF3050507);
        fill(m, 18, 18, width - 18, height - 18, 0xF20C0C11);

        // Header
        fill(m, 18, 18, width - 18, 78, 0xF2161320);
        fill(m, 18, 18, 21, height - 18, PURPLE);

        // Sidebar
        fill(m, 18, 78, 88, height - 18, 0xF20A0A0F);
        if (category >= 0)
            fill(m, 18, 132 + category * 48, 88, 170 + category * 48, 0xFF2A1838);
        fill(m, 18, 132 + category * 48, 21, 170 + category * 48, PURPLE);

        // Main content backdrop
        fill(m, 88, 78, width - 18, height - 18, 0xE90A0A0F);

        drawText(m, textRenderer, new LiteralText("§dExternalVisuals"), 36, 34, WHITE);
        drawText(m, textRenderer, new LiteralText("§8Visuals  /  §7" + CATS[category] + "  /  §fModules"),
                112, 92, MUTED);

        // Tiny status text, like the reference UI.
        drawText(m, textRenderer, new LiteralText("§7Fabric 1.16.5  •  §d" + visible.size() + " modules"),
                112, height - 34, MUTED);

        super.render(m, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ExternalConfig.save();
        if (client != null) client.openScreen(parent);
    }
}
