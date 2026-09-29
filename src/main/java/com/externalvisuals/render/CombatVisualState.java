package com.externalvisuals.render;

public final class CombatVisualState {
    public static long hitUntil;
    public static long criticalUntil;
    public static long killUntil;
    public static String targetName = "";
    public static float targetHealth;
    public static float targetMaxHealth;
    public static double targetDistance;

    private CombatVisualState() {}

    public static void hit(String name, float health, float maxHealth, double distance, boolean critical) {
        targetName = name;
        targetHealth = health;
        targetMaxHealth = maxHealth;
        targetDistance = distance;
        hitUntil = System.currentTimeMillis() + 250;
        if (critical) criticalUntil = System.currentTimeMillis() + 500;
    }

    public static void kill(String name) {
        targetName = name;
        killUntil = System.currentTimeMillis() + 800;
    }
}
