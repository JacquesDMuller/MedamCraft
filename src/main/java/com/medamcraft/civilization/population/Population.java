package com.medamcraft.civilization.population;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.medamcraft.civilization.events.CivilizationEvent;

public class Population {
    private UUID id;
    private String name;
    private int size;
    private Map<String, Float> traits;
    private Map<String, Float> needs;
    private Map<String, Float> happiness;
    
    public Population(String name, int initialSize) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.size = initialSize;
        this.traits = new HashMap<>();
        this.needs = new HashMap<>();
        this.happiness = new HashMap<>();
        initializePopulation();
    }
    
    private void initializePopulation() {
        // Inicializa traços da população
        traits.put("adaptability", 0.5f);
        traits.put("creativity", 0.5f);
        traits.put("spirituality", 0.5f);
        traits.put("productivity", 0.5f);
        
        // Inicializa necessidades básicas
        needs.put("food", 1.0f);
        needs.put("shelter", 1.0f);
        needs.put("safety", 1.0f);
        needs.put("social", 1.0f);
        
        // Inicializa níveis de felicidade
        happiness.put("cultural", 0.5f);
        happiness.put("economic", 0.5f);
        happiness.put("religious", 0.5f);
    }
    
    public void update() {
        updateNeeds();
        updateHappiness();
        updatePopulationSize();
        generateEvents();
    }
    
    private void updateNeeds() {
        for (Map.Entry<String, Float> need : needs.entrySet()) {
            float consumption = calculateConsumption(need.getKey());
            float newValue = need.getValue() - consumption;
            needs.put(need.getKey(), Math.max(0, Math.min(1, newValue)));
        }
    }
    
    private float calculateConsumption(String needType) {
        return 0.01f * size; // Implementação básica - consumo por pessoa
    }
    
    private void updateHappiness() {
        // Atualiza felicidade baseada em necessidades atendidas
        float avgNeeds = needs.values().stream().reduce(0f, Float::sum) / needs.size();
        for (Map.Entry<String, Float> happy : happiness.entrySet()) {
            float newValue = (happy.getValue() + avgNeeds) / 2;
            happiness.put(happy.getKey(), newValue);
        }
    }
    
    private void updatePopulationSize() {
        float avgHappiness = happiness.values().stream().reduce(0f, Float::sum) / happiness.size();
        if (avgHappiness > 0.7f) {
            size += Math.max(1, size * 0.01); // Crescimento de 1%
        } else if (avgHappiness < 0.3f) {
            size -= Math.max(1, size * 0.01); // Declínio de 1%
        }
    }
    
    private void generateEvents() {
        checkForPopulationEvents();
    }
    
    private void checkForPopulationEvents() {
        // Gera eventos baseados no estado da população
        if (size > 1000 && needs.get("food") < 0.2f) {
            triggerEvent(new CivilizationEvent("FOOD_CRISIS", "A população está passando fome!", 20));
        }
    }

    private void triggerEvent(CivilizationEvent event) {
        // Implementar lógica para disparar eventos
    }
    
    public float getAverageHappiness() {
        return happiness.values().stream()
            .reduce(0f, Float::sum) / happiness.size();
    }
    
    public float getGrowthRate() {
        float avgHappiness = getAverageHappiness();
        return avgHappiness > 0.7f ? 0.01f : -0.01f;
    }
    
    public int getSize() {
        return size;
    }
} 