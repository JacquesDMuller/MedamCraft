package com.medamcraft.civilization.events;

import java.util.ArrayList;
import java.util.List;

public class CivilizationEvent {
    private String type;
    private String description;
    private int duration;
    private List<EventEffect> effects;
    private List<EventEffect> completionEffects;
    
    public CivilizationEvent(String type, String description, int duration) {
        this.type = type;
        this.description = description;
        this.duration = duration;
        this.effects = new ArrayList<>();
        this.completionEffects = new ArrayList<>();
    }
    
    public void addEffect(EventEffect effect) {
        effects.add(effect);
    }
    
    public void addCompletionEffect(EventEffect effect) {
        completionEffects.add(effect);
    }
    
    public void update() {
        duration--;
        applyEffects();
    }
    
    private void applyEffects() {
        effects.forEach(effect -> effect.apply());
    }
    
    public boolean isCompleted() {
        return duration <= 0;
    }
    
    public List<EventEffect> getCompletionEffects() {
        return completionEffects;
    }
    
    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }
} 