package com.medamcraft.civilization.systems;

public enum ResourceType {
    FOOD("Comida", 10f, 50f),
    WOOD("Madeira", 5f, 100f),
    STONE("Pedra", 8f, 100f),
    IRON("Ferro", 15f, 50f),
    GOLD("Ouro", 50f, 10f),
    COAL("Carvão", 12f, 50f),
    CLOTH("Tecido", 20f, 30f),
    POTTERY("Cerâmica", 25f, 20f);
    
    private final String name;
    private final float basePrice;
    private final float criticalLevel;
    
    ResourceType(String name, float basePrice, float criticalLevel) {
        this.name = name;
        this.basePrice = basePrice;
        this.criticalLevel = criticalLevel;
    }
    
    public String getName() { return name; }
    public float getBasePrice() { return basePrice; }
    public float getCriticalLevel() { return criticalLevel; }
} 