package com.externalvisuals.render;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.module.ModuleManager;
import com.externalvisuals.modules.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import java.util.*;

public final class VisualEngine {
    private static final Map<Integer, Float> HEALTH = new HashMap<>();
    private static final Set<Integer> SEEN = new HashSet<>();
    private VisualEngine() {}
    public static void init(ModuleManager modules) {
        HudRenderCallback.EVENT.register(VisualEngine::hud);
        WorldRenderEvents.AFTER_ENTITIES.register(VisualEngine::world);
    }
    public static void tick(MinecraftClient mc) {
        if(mc.world==null) return;
        for(Entity e: mc.world.getEntities()) if(e instanceof LivingEntity && e!=mc.player) {
            LivingEntity le=(LivingEntity)e; float now=le.getHealth(); Float old=HEALTH.put(le.getEntityId(),now);
            if(old!=null && now<old-0.01f) onDamage(le,old-now,mc.player!=null && mc.player.fallDistance>0.0f);
            if(now<=0 && old!=null && old>0) { KillEffects k=ExternalVisuals.MODULES.get(KillEffects.class); if(k!=null&&k.isEnabled())k.play(le); }
        }
    }
    private static net.minecraft.world.World mcWorld(Entity e){return e.world;}
    private static void onDamage(LivingEntity e,float damage,boolean crit){
        HitParticles hp=ExternalVisuals.MODULES.get(HitParticles.class); if(hp!=null&&hp.isEnabled())hp.spawn(e,crit); CriticalEffects ce=ExternalVisuals.MODULES.get(CriticalEffects.class); if(crit&&ce!=null&&ce.isEnabled()&&mcWorld(e)!=null){for(int i=0;i<(int)(8*ce.strength.getValue());i++)mcWorld(e).addParticle(net.minecraft.particle.ParticleTypes.END_ROD,e.getX(),e.getY()+e.getHeight()*.5,e.getZ(),(mcWorld(e).random.nextDouble()-.5)*.2,.08,(mcWorld(e).random.nextDouble()-.5)*.2);}
        DamageNumbers dn=ExternalVisuals.MODULES.get(DamageNumbers.class); if(dn!=null&&dn.isEnabled())dn.add(e,damage,crit);
        Hitmarker hm=ExternalVisuals.MODULES.get(Hitmarker.class); if(hm!=null&&hm.isEnabled()&&e instanceof net.minecraft.entity.player.PlayerEntity)hm.trigger();
    }
    private static void hud(MatrixStack m,float delta){
        MinecraftClient mc=MinecraftClient.getInstance(); if(mc.player==null||mc.world==null)return;
        int w=mc.getWindow().getScaledWidth(),h=mc.getWindow().getScaledHeight();
        LowHP low=ExternalVisuals.MODULES.get(LowHP.class);
        if(low!=null&&low.isEnabled()) {float hp=mc.player.getHealth()+mc.player.getAbsorptionAmount(); if(hp<low.threshold.getValue()){float d=1-Math.max(0,hp)/(float)low.threshold.getValue();int a=(int)(d*low.pulse.getValue()*180*(.65+.35*Math.sin(System.currentTimeMillis()*.012)));int c=(a<<24)|0xAA1020;int b=Math.max(10,(int)(14+d*24));DrawableHelper.fill(m,0,0,w,b,c);DrawableHelper.fill(m,0,h-b,w,h,c);DrawableHelper.fill(m,0,0,b,h,c);DrawableHelper.fill(m,w-b,0,w,h,c);}}
        CustomCrosshair ch=ExternalVisuals.MODULES.get(CustomCrosshair.class); Hitmarker hm=ExternalVisuals.MODULES.get(Hitmarker.class);
        if(ch!=null&&ch.isEnabled()){int cx=w/2,cy=h/2,size=ch.size.getValue().intValue(),gap=ch.gap.getValue().intValue();int col=(hm!=null&&hm.isEnabled()&&hm.until>System.currentTimeMillis())?0xFFFF4D5A:0xFFFFFFFF;String style=ch.style.getValue();if(style.equals("DOT")){DrawableHelper.fill(m,cx-2,cy-2,cx+3,cy+3,col);}else if(style.equals("CIRCLE")){for(int i=0;i<16;i++){double a=i*Math.PI*2/16;int x=cx+(int)(Math.cos(a)*size),y=cy+(int)(Math.sin(a)*size);DrawableHelper.fill(m,x,y,x+2,y+2,col);}}else{DrawableHelper.fill(m,cx-size,cy-1,cx-gap,cy+2,col);DrawableHelper.fill(m,cx+gap,cy-1,cx+size,cy+2,col);DrawableHelper.fill(m,cx-1,cy-size,cx+2,cy-gap,col);DrawableHelper.fill(m,cx-1,cy+gap,cx+2,cy+size,col);}}
        if(hm!=null&&hm.isEnabled()&&hm.until>System.currentTimeMillis()){int cx=w/2,cy=h/2,s=hm.size.getValue().intValue();for(int i=0;i<s;i++){DrawableHelper.fill(m,cx-s+i,cy-s+i,cx-s+i+2,cy-s+i+2,0xFFFF4655);DrawableHelper.fill(m,cx+s-i,cy-s+i,cx+s-i+2,cy-s+i+2,0xFFFF4655);DrawableHelper.fill(m,cx-s+i,cy+s-i,cx-s+i+2,cy+s-i+2,0xFFFF4655);DrawableHelper.fill(m,cx+s-i,cy+s-i,cx+s-i+2,cy+s-i+2,0xFFFF4655);}}
        TargetHUD th=ExternalVisuals.MODULES.get(TargetHUD.class);if(th!=null&&th.isEnabled()&&mc.targetedEntity instanceof LivingEntity){LivingEntity e=(LivingEntity)mc.targetedEntity;int x=16,y=h-78,width=th.width.getValue().intValue();DrawableHelper.fill(m,x,y,x+width,y+58,((int)th.alpha.getValue().doubleValue()<<24)|0x10121A);String name=e.getDisplayName().getString();mc.textRenderer.draw(m,name,x+10,y+8,0xFFF4EFFF);float ratio=Math.max(0,Math.min(1,e.getHealth()/Math.max(1,e.getMaxHealth())));DrawableHelper.fill(m,x+10,y+27,x+width-10,y+31,0xFF242634);DrawableHelper.fill(m,x+10,y+27,x+10+(int)((width-20)*ratio),y+31,0xFFB55CFF);mc.textRenderer.draw(m,String.format(java.util.Locale.US,"%.1f / %.1f",e.getHealth(),e.getMaxHealth()),x+10,y+38,0xFFBDB7C9);mc.textRenderer.draw(m,String.format(java.util.Locale.US,"%.1fm",mc.player.distanceTo(e)),x+width-45,y+8,0xFF8F8A98);}
        ArmorPotionHUD ap=ExternalVisuals.MODULES.get(ArmorPotionHUD.class);if(ap!=null&&ap.isEnabled()){int x=w-18,y=h-20;for(net.minecraft.item.ItemStack st:mc.player.getInventory().armor){if(st.isEmpty())continue;String n=st.getItem().getName().getString();String t=n.length()>8?n.substring(0,8):n;mc.textRenderer.draw(m,t,x-mc.textRenderer.getWidth(t),y,0xFFD7D0E2);y-=12;}if(ap.potions.isEnabled()){int py=20;for(net.minecraft.entity.effect.StatusEffectInstance fx:mc.player.getActiveStatusEffects().values()){String t=fx.getEffectType().getName().getString();mc.textRenderer.draw(m,t.substring(t.lastIndexOf('.')+1),8,py,0xFFB9A8FF);py+=11;if(py>h/2)break;}}}
    }

    private static void world(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx){
        MinecraftClient mc=MinecraftClient.getInstance(); if(mc.player==null||mc.world==null)return; MatrixStack m=ctx.matrixStack(); Camera cam=ctx.camera(); VertexConsumerProvider consumers=ctx.consumers();
        DamageNumbers dn=ExternalVisuals.MODULES.get(DamageNumbers.class); if(dn!=null&&dn.isEnabled()&&!dn.entries.isEmpty()) renderDamage(m,cam,consumers,dn);
        Trajectory t=ExternalVisuals.MODULES.get(Trajectory.class); if(t!=null&&t.isEnabled()) renderTrajectory(m,cam,consumers,t,mc);
    }
    private static void renderDamage(MatrixStack m,Camera cam,VertexConsumerProvider cp,DamageNumbers dn){
        if(cp==null)return; Vec3d c=cam.getPos(); for(DamageNumbers.Entry e:dn.entries){float fade=1f-(System.currentTimeMillis()-e.time)/(float)(dn.lifetime.getValue()*50);if(fade<=0)continue;String s=(e.crit?"✦ ":"")+Math.round(e.damage);m.push();m.translate(e.pos.x-c.x,e.pos.y-c.y,e.pos.z-c.z);m.multiply(cam.getRotation());m.scale(-.025f*dn.scale.getValue().floatValue(),-.025f*dn.scale.getValue().floatValue(),.025f*dn.scale.getValue().floatValue());int color=((int)(255*fade)<<24)|(e.crit?0xFF4040:0xFFD84A);int tw=MinecraftClient.getInstance().textRenderer.getWidth(s);MinecraftClient.getInstance().textRenderer.draw(s,-tw/2f,0,color,true,m.peek().getModel(),cp,true,0,15728880);m.pop();}
    }
    private static void renderTrajectory(MatrixStack m,Camera cam,VertexConsumerProvider cp,Trajectory t,MinecraftClient mc){
        if(cp==null)return; if(!(mc.player.getMainHandStack().getItem()==Items.BOW||mc.player.getMainHandStack().getItem()==Items.SNOWBALL||mc.player.getMainHandStack().getItem()==Items.ENDER_PEARL||mc.player.getMainHandStack().getItem()==Items.SPLASH_POTION))return;
        VertexConsumer v=cp.getBuffer(RenderLayer.getLines());Vec3d p=mc.player.getCameraPosVec(1f);Vec3d vel=mc.player.getRotationVec(1f).normalize().multiply(.9);double g=.03;for(int i=0;i<t.length.getValue().intValue();i++){Vec3d n=p.add(vel);Vec3d a=p.subtract(cam.getPos()),b=n.subtract(cam.getPos());v.vertex(m.peek().getModel(),(float)a.x,(float)a.y,(float)a.z).color(0.75f,0.25f,1f,.85f).next();v.vertex(m.peek().getModel(),(float)b.x,(float)b.y,(float)b.z).color(0.75f,0.25f,1f,.25f).next();p=n;vel=vel.add(0,-g,0).multiply(.99);}
    }
}
