package com.medamcraft.civilization.systems;

import java.util.HashMap;
import java.util.Map;
import com.medamcraft.civilization.events.CivilizationEvent;
import com.medamcraft.civilization.Civilization;

public class CultureSystem {
    private final Civilization civilization;
    private Map<String, Float> culturalValues;
    private Map<String, String> traditions;
    private float adaptabilityIndex;
    
    public CultureSystem(Civilization civilization) {
        this.civilization = civilization;
        this.culturalValues = new HashMap<>();
        this.traditions = new HashMap<>();
        this.adaptabilityIndex = 0.5f; // Valor inicial médio
        initializeDefaultValues();
    }
    
    private void initializeDefaultValues() {
        // Valores culturais básicos em uma escala de 0 a 1
        culturalValues.put("individualismo", 0.5f);
        culturalValues.put("hierarquia", 0.5f);
        culturalValues.put("inovacao", 0.5f);
        culturalValues.put("tradicionalismo", 0.5f);
        culturalValues.put("coletivismo", 0.5f);
    }
    
    public void update() {
        updateCulturalValues();
        checkForCulturalEvents();
    }
    
    private void updateCulturalValues() {
        // Atualiza valores culturais com base em eventos e influências
        for (Map.Entry<String, Float> value : culturalValues.entrySet()) {
            float randomInfluence = (float) (Math.random() * 0.1 - 0.05); // -0.05 a 0.05
            float newValue = value.getValue() + randomInfluence;
            culturalValues.put(value.getKey(), Math.max(0, Math.min(1, newValue)));
        }
    }
    
    private void checkForCulturalEvents() {
        // Verifica se mudanças culturais significativas ocorreram
        for (Map.Entry<String, Float> value : culturalValues.entrySet()) {
            if (value.getValue() > 0.8f || value.getValue() < 0.2f) {
                triggerCulturalEvent(value.getKey(), value.getValue());
            }
        }
    }
    
    private void triggerCulturalEvent(String aspect, float value) {
        // Gera eventos culturais baseados em valores extremos
        // Por exemplo, alto individualismo pode gerar novos tipos de construções
    }
    
    public void handleEvent(CivilizationEvent event) {
        // Implementação básica
    }
    
    public float getStability() {
        return adaptabilityIndex;
    }
    
    public float getDevelopment() {
        return culturalValues.values().stream()
            .reduce(0f, Float::sum) / culturalValues.size();
    }
} 