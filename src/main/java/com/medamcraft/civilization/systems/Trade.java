package com.medamcraft.civilization.systems;

public class Trade {
    private final ResourceType resource;
    private final float amount;
    private final float price;
    private final TradeType type;
    private int duration;
    
    public Trade(ResourceType resource, float amount, float price, TradeType type, int duration) {
        this.resource = resource;
        this.amount = amount;
        this.price = price;
        this.type = type;
        this.duration = duration;
    }
    
    public boolean process() {
        duration--;
        return duration <= 0;
    }
    
    public enum TradeType {
        IMPORT,
        EXPORT
    }
    
    // Getters
    public ResourceType getResource() { return resource; }
    public float getAmount() { return amount; }
    public float getPrice() { return price; }
    public TradeType getType() { return type; }
} 