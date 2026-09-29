package com.externalvisuals.ui;

import com.externalvisuals.module.Module;
import com.externalvisuals.setting.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(new LiteralText(module.getName()));
        this.parent=parent; this.module=module;
    }

    @Override
    protected void init() {
        int y=82;
        for (Setting<?> s : module.getSettings()) {
            addButton(new ButtonWidget(width/2-135, y, 270, 25,
                    new LiteralText(label(s)),
                    b -> { change(s); rebuild(); }));
            y+=31;
        }
        addButton(new ButtonWidget(width/2-135, height-36, 270, 22,
                new LiteralText("§dBack"), b -> client.openScreen(parent)));
    }

    private String label(Setting<?> s) {
        return "§f"+s.getName()+" §8» §d"+String.valueOf(s.getValue());
    }

    private void change(Setting<?> s) {
        if (s instanceof BooleanSetting) ((BooleanSetting)s).toggle();
        else if (s instanceof ModeSetting) ((ModeSetting)s).next();
        else if (s instanceof SliderSetting) ((SliderSetting)s).add(0.1);
    }

    private void rebuild() {
        children.clear(); buttons.clear(); init();
    }

    @Override
    public void render(MatrixStack m,int mouseX,int mouseY,float delta) {
        fill(m,0,0,width,height,0xF2070709);
        fill(m,12,12,width-12,60,0xF2191322);
        fill(m,12,60,width-12,height-12,0xF20F0F13);
        drawCenteredText(m,textRenderer,new LiteralText("§d"+module.getName()+" §8• §fSettings"),
                width/2,31,0xFFFFFFFF);
        super.render(m,mouseX,mouseY,delta);
    }
}
