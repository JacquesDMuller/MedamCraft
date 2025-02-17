package com.medamcraft.civilization.events;

public class PopulationEvent extends CivilizationEvent {
    public PopulationEvent(String type, String description) {
        super(type, description, 20);
    }
} 