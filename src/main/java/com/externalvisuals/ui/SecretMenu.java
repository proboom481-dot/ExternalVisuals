package com.externalvisuals.ui;

import com.externalvisuals.config.ExternalConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class SecretMenu extends Screen {
    private final Screen parent;
    private int category;
    private boolean sessionInfo;
    private boolean debugInfo;
    private boolean movementInfo;
    private boolean worldInfo;

    private static final String[] CATEGORIES = {"Combat", "Movement", "World", "Misc"};

    public SecretMenu(Screen parent) {
        super(new LiteralText("Secret Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        children.clear();
        buttons.clear();

        for (int i=0;i<CATEGORIES.length;i++) {
            final int idx=i;
            addButton(new ButtonWidget(20, 82+i*32, 105, 24,
                    new LiteralText((idx==category?"§d● ":"§7○ ")+"§f"+CATEGORIES[i]),
                    b->{category=idx;rebuild();}));
        }

        // Safe, non-destructive utility placeholders. Gameplay modules can be added individually later.
        int y=88;
        if(category==0) {
            addButton(new ButtonWidget(145,y,245,28,new LiteralText("§fAttack Visualizer §7[OFF]"),b->{}));
            addButton(new ButtonWidget(145,y+38,245,28,new LiteralText("§fHitbox Overlay §7[OFF]"),b->{}));
        } else if(category==1) {
            addButton(new ButtonWidget(145,y,245,28,new LiteralText("§fMovement Info §7["+(movementInfo?"ON":"OFF")+"]"),b->{movementInfo=!movementInfo;rebuild();}));
            addButton(new ButtonWidget(145,y+38,245,28,new LiteralText("§fJump Indicator §7[OFF]"),b->{}));
        } else if(category==2) {
            addButton(new ButtonWidget(145,y,245,28,new LiteralText("§fBlock Info §7[OFF]"),b->{}));
            addButton(new ButtonWidget(145,y+38,245,28,new LiteralText("§fWorld Info §7["+(worldInfo?"ON":"OFF")+"]"),b->{worldInfo=!worldInfo;rebuild();}));
        } else {
            addButton(new ButtonWidget(145,y,245,28,new LiteralText("§fSession Info §7["+(sessionInfo?"ON":"OFF")+"]"),b->{sessionInfo=!sessionInfo;rebuild();}));
            addButton(new ButtonWidget(145,y+38,245,28,new LiteralText("§fDebug Overlay §7["+(debugInfo?"ON":"OFF")+"]"),b->{debugInfo=!debugInfo;rebuild();}));
        }

        addButton(new ButtonWidget(width/2-110,height-38,220,22,
                new LiteralText("§dBack to ExternalVisuals"),
                b->{ExternalConfig.save();client.openScreen(parent);}));
    }

    @Override
    public void render(MatrixStack m,int mouseX,int mouseY,float delta) {
        fill(m,0,0,width,height,0xF2050507);
        fill(m,12,12,width-12,60,0xF21B1224);
        fill(m,12,60,128,height-12,0xF20B0B0F);
        fill(m,128,60,width-12,height-12,0xF2101014);
        fill(m,12,12,15,height-12,0xFFD08AFF);

        drawText(m,textRenderer,new LiteralText("§dSECRET MENU"),28,27,0xFFFFFFFF);
        drawText(m,textRenderer,new LiteralText("§7Private module area"),28,43,0xFF88888F);
        drawText(m,textRenderer,new LiteralText("§f"+CATEGORIES[category]),145,68,0xFFFFFFFF);

        if(sessionInfo && client.player!=null) {
            fill(m,width-230,70,width-16,145,0xE90A0A0E);
            drawText(m,textRenderer,new LiteralText("§dSession"),width-214,82,0xFFFFFFFF);
            drawText(m,textRenderer,new LiteralText("§7FPS: §f"+client.getCurrentFps()),width-214,98,0xFFFFFFFF);
            drawText(m,textRenderer,new LiteralText("§7Pos: §f"+client.player.getBlockPos().toShortString()),width-214,114,0xFFFFFFFF);
            drawText(m,textRenderer,new LiteralText("§7Health: §f"+String.format("%.1f",client.player.getHealth())),width-214,130,0xFFFFFFFF);
        }
        if(movementInfo && client.player!=null) {
            drawText(m,textRenderer,new LiteralText("§dSpeed: §f"+String.format("%.2f",Math.sqrt(client.player.getVelocity().x*client.player.getVelocity().x+client.player.getVelocity().z*client.player.getVelocity().z))),
                    145,height-70,0xFFFFFFFF);
        }
        if(worldInfo && client.world!=null) {
            drawText(m,textRenderer,new LiteralText("§dWorld: §f"+client.world.getRegistryKey().getValue()),145,height-54,0xFFFFFFFF);
        }
        if(debugInfo) {
            drawText(m,textRenderer,new LiteralText("§8ExternalVisuals debug"),145,height-38,0xFFFFFFFF);
        }
        super.render(m,mouseX,mouseY,delta);
    }

    @Override
    public void close() {
        ExternalConfig.save();
        if(client!=null) client.openScreen(parent);
    }
}
