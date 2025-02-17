package com.medamcraft.civilization.events;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private List<CivilizationEvent> activeEvents;
    private List<EventListener> listeners;
    
    public EventSystem() {
        this.activeEvents = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }
    
    public void triggerEvent(CivilizationEvent event) {
        activeEvents.add(event);
        notifyListeners(event);
    }
    
    private void notifyListeners(CivilizationEvent event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
    
    public void update() {
        List<CivilizationEvent> completedEvents = new ArrayList<>();
        
        for (CivilizationEvent event : activeEvents) {
            event.update();
            if (event.isCompleted()) {
                completedEvents.add(event);
                handleEventCompletion(event);
            }
        }
        
        activeEvents.removeAll(completedEvents);
    }
    
    private void handleEventCompletion(CivilizationEvent event) {
        // Processa os efeitos finais do evento
        event.getCompletionEffects().forEach(effect -> effect.apply());
    }
} 