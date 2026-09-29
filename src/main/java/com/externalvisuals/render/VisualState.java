package com.externalvisuals.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class VisualState {
    public static final List<Particle> PARTICLES = new ArrayList<>();
    public static final List<Damage> DAMAGES = new ArrayList<>();
    public static long lastHitMarker;
    public static long lastCritical;
    public static float lowHpPulse;

    private VisualState() {}

    public static List<Particle> particles(){ return particles; }\n    public static List<Damage> damages(){ return damages; }

    public static void tick() {
        Iterator<Particle> pi = PARTICLES.iterator();
        while (pi.hasNext()) {
            Particle p = pi.next();
            p.age++;
            p.y += 0.025f;
            if (p.age > 24) pi.remove();
        }

        Iterator<Damage> di = DAMAGES.iterator();
        while (di.hasNext()) {
            Damage d = di.next();
            d.age++;
            d.y += 0.018f;
            if (d.age > 35) di.remove();
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            float hp = mc.player.getHealth();
            lowHpPulse = hp <= 8.0f ? (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() * 0.012)) : 0.0f;
        }
    }

    public static void hit(Entity e, float damage, boolean critical) {
        if (e == null) return;
        double x = e.getX(), y = e.getY() + e.getHeight() * 0.55, z = e.getZ();

        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2.0 * i / 8.0;
            PARTICLES.add(new Particle(x, y, z,
                    Math.cos(a) * 0.035, 0.045 + (i % 3) * 0.012, Math.sin(a) * 0.035));
        }
        DAMAGES.add(new Damage(x, y + 0.2, z, damage));
        lastHitMarker = System.currentTimeMillis();
        if (critical) lastCritical = System.currentTimeMillis();
    }

    public static class Particle {
        public double x,y,z,vx,vy,vz;
        public int age;
        public Particle(double x,double y,double z,double vx,double vy,double vz) {
            this.x=x;this.y=y;this.z=z;this.vx=vx;this.vy=vy;this.vz=vz;
        }
    }

    public static class Damage {
        public double x,y,z;
        public float value;
        public int age;
        public Damage(double x,double y,double z,float value) {
            this.x=x;this.y=y;this.z=z;this.value=value;
        }
    }
}
