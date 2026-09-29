package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

public final class HudVisuals {
    private HudVisuals() {}

    public static void register() {
        HudRenderCallback.EVENT.register(HudVisuals::render);
    }

    private static boolean on(String n) {
        for (Module m : ExternalVisuals.MODULES.getModules())
            if (m.getName().equals(n)) return m.isEnabled();
        return false;
    }

    private static void render(MatrixStack m, float delta) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.player==null) return;
        int w=mc.getWindow().getScaledWidth(), h=mc.getWindow().getScaledHeight();
        if(on("Armor HUD")) armor(m,mc,w,h);
        if(on("Potion HUD")) potions(m,mc,w,h);
        if(on("Target HUD") && mc.targetedEntity != null) target(m,mc,w,h);
        if(on("Trajectory")) trajectory(m,mc,w,h);
        if(on("Kill Effects") && KillEffects.active()) killFlash(m,w,h);
        if(on("Target HUD")) targetPanel(m,mc,w,h);
    }

    private static void panel(MatrixStack m,int x,int y,int w,int h) {
        DrawableHelper.fill(m,x,y,x+w,y+h,0xC9121016);
        DrawableHelper.fill(m,x,y,x+w,y+2,0xFFD08AFF);
    }

    private static void armor(MatrixStack m,MinecraftClient mc,int w,int h) {
        int x=w/2-78,y=h-42;
        panel(m,x-5,y-5,166,34);
        for(int i=3;i>=0;i--){
            ItemStack s=mc.player.inventory.armor.get(i);
            if(!s.isEmpty()) mc.getItemRenderer().renderGuiItemIcon(s,x+(3-i)*38,y,0);
        }
        mc.textRenderer.draw(m,new LiteralText("§7Armor"),x+2,y+20,0xFFFFFFFF);
    }

    private static void potions(MatrixStack m,MinecraftClient mc,int w,int h) {
        int x=8,y=8,row=0;
        for(StatusEffectInstance e:mc.player.getActiveStatusEffects().values()){
            if(row>=5) break;
            String name=e.getEffectType().getName().getString();
            String line="§d"+name+" §7"+(e.getAmplifier()+1)+" §f"+e.getDuration()/20+"s";
            mc.textRenderer.draw(m,new LiteralText(line),x,y+row*12,0xFFFFFFFF);
            row++;
        }
    }

    private static void target(MatrixStack m,MinecraftClient mc,int w,int h) {
        panel(m,8,h/2-34,150,60);
        String name=mc.targetedEntity.getName().getString();
        mc.textRenderer.draw(m,new LiteralText("§dTARGET"),18,h/2-25,0xFFFFFFFF);
        mc.textRenderer.draw(m,new LiteralText("§f"+name),18,h/2-10,0xFFFFFFFF);
        mc.textRenderer.draw(m,new LiteralText("§7Distance: §f"+
                String.format("%.1f",mc.player.distanceTo(mc.targetedEntity))),18,h/2+5,0xFFFFFFFF);
    }

    private static void targetPanel(MatrixStack m, MinecraftClient mc, int w, int h) {
        if (mc.targetedEntity == null || !(mc.targetedEntity instanceof net.minecraft.entity.LivingEntity)) return;
        net.minecraft.entity.LivingEntity e=(net.minecraft.entity.LivingEntity)mc.targetedEntity;
        int x=w-185, y=70;
        fill(m,x,y,x+170,y+58,0xE80A080F);
        fill(m,x,y,x+3,y+58,0xFFD08AFF);
        drawText(m,textRenderer,new net.minecraft.text.LiteralText("§f"+e.getDisplayName().getString()),x+12,y+10,0xFFFFFFFF);
        float pct=Math.max(0f,Math.min(1f,e.getHealth()/Math.max(1f,e.getMaxHealth())));
        fill(m,x+12,y+31,x+158,y+36,0xFF3A233E);
        fill(m,x+12,y+31,x+12+(int)(146*pct),y+36,0xFFD08AFF);
        drawText(m,textRenderer,new net.minecraft.text.LiteralText("§7"+String.format("%.1f",e.getHealth())+" / "+String.format("%.1f",e.getMaxHealth())),x+12,y+42,0xFFFFFFFF);
    }

    private static void killFlash(MatrixStack m,int w,int h) {
        DrawableHelper.fill(m,0,0,w,2,0xB0D08AFF);
        DrawableHelper.fill(m,0,h-2,w,h,0xB0D08AFF);
    }

    private static void trajectory(MatrixStack m,MinecraftClient mc,int w,int h) {
        int cx=w/2,cy=h/2+16;
        for(int i=0;i<8;i++){
            int x=cx+(i-3)*7, y=cy+i*i/2;
            DrawableHelper.fill(m,x,y,x+2,y+2,0xFFD08AFF);
        }
    }
}
