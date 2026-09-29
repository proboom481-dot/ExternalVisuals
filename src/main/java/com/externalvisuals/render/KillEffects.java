package com.externalvisuals.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public final class KillEffects {
    private static long until;
    private static double x,y,z;

    private KillEffects(){}

    public static void trigger(double x,double y,double z){
        KillEffects.x=x; KillEffects.y=y; KillEffects.z=z;
        until=System.currentTimeMillis()+700;
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.world!=null){
            for(int i=0;i<20;i++){
                double a=Math.PI*2*i/20.0;
                mc.world.addParticle(ParticleTypes.CRIT,
                        x+Math.cos(a)*0.35,y+0.25,z+Math.sin(a)*0.35,
                        Math.cos(a)*0.04,0.035,Math.sin(a)*0.04);
            }
        }
    }

    public static boolean active(){ return System.currentTimeMillis()<until; }
    public static double x(){return x;}
    public static double y(){return y;}
    public static double z(){return z;}
}
