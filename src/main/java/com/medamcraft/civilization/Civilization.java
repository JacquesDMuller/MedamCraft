package com.medamcraft.civilization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.medamcraft.civilization.events.EventSystem;
import com.medamcraft.civilization.ai.CivilizationAI;
import com.medamcraft.civilization.systems.CultureSystem;
import com.medamcraft.civilization.systems.EconomicSystem;
import com.medamcraft.civilization.systems.ReligionSystem;
import com.medamcraft.civilization.population.Population;
import com.medamcraft.civilization.village.VillageSystem;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Civilization {
    private String name;
    private CultureSystem cultureSystem;
    private EconomicSystem economicSystem;
    private ReligionSystem religionSystem;
    private List<Population> populations;
    private Map<String, Float> resources;
    private EventSystem eventSystem;
    private CivilizationAI ai;
    private VillageSystem villageSystem;
    
    // Novos atributos
    private float stability; // Estabilidade geral (0-1)
    private float development; // Nível de desenvolvimento (0-1)
    private int age; // Idade em dias do Minecraft
    private Location capital; // Localização da capital
    private Set<Location> territory; // Território controlado
    
    private static final Logger LOGGER = LoggerFactory.getLogger("Civilization");
    
    public Civilization(String name) {
        this.name = name;
        this.populations = new ArrayList<>();
        this.resources = new HashMap<>();
        this.territory = new HashSet<>();
        this.stability = 0.5f;
        this.development = 0.1f;
        this.age = 0;
        
        initializeSystems();
        this.eventSystem = new EventSystem();
        registerEventListeners();
        this.ai = new CivilizationAI(this);
        this.villageSystem = new VillageSystem(null); // Será definido quando o mundo for carregado
    }
    
    private void initializeSystems() {
        this.cultureSystem = new CultureSystem(this);
        this.economicSystem = new EconomicSystem(this);
        this.religionSystem = new ReligionSystem(this);
    }
    
    private void registerEventListeners() {
        eventSystem.registerListener(event -> {
            if (event.getType().startsWith("CULTURAL")) {
                cultureSystem.handleEvent(event);
            } else if (event.getType().startsWith("ECONOMIC")) {
                economicSystem.handleEvent(event);
            } else if (event.getType().startsWith("RELIGIOUS")) {
                religionSystem.handleEvent(event);
            }
        });
    }
    
    public void update() {
        // Atualiza todos os sistemas
        age++;
        
        // Atualiza sistemas principais
        cultureSystem.update();
        economicSystem.update();
        religionSystem.update();
        
        // Atualiza populações
        updatePopulations();
        
        // Processa eventos
        eventSystem.update();
        
        // Atualiza IA
        ai.update();
        
        // Atualiza métricas gerais
        updateStability();
        updateDevelopment();
        
        // Atualiza sistema de vilas
        villageSystem.tick();
    }
    
    private void updatePopulations() {
        for (Population pop : populations) {
            pop.update();
        }
    }
    
    private void updateStability() {
        float culturalStability = cultureSystem.getStability();
        float economicStability = economicSystem.getStability();
        float religiousHarmony = religionSystem.getHarmony();
        float populationHappiness = getAveragePopulationHappiness();
        
        stability = (culturalStability + economicStability + religiousHarmony + populationHappiness) / 4;
    }
    
    private void updateDevelopment() {
        float culturalProgress = cultureSystem.getDevelopment();
        float economicProgress = economicSystem.getDevelopment();
        float populationGrowth = getPopulationGrowthRate();
        
        development = (culturalProgress + economicProgress + populationGrowth) / 3;
    }
    
    private float getAveragePopulationHappiness() {
        if (populations.isEmpty()) return 0.5f;
        return (float) populations.stream()
            .mapToDouble(pop -> pop.getAverageHappiness())
            .average()
            .orElse(0.5);
    }
    
    private float getPopulationGrowthRate() {
        if (populations.isEmpty()) return 0;
        return (float) populations.stream()
            .mapToDouble(pop -> pop.getGrowthRate())
            .average()
            .orElse(0);
    }
    
    // Getters e setters
    public String getName() { return name; }
    public float getStability() { return stability; }
    public float getDevelopment() { return development; }
    public int getAge() { return age; }
    public CultureSystem getCultureSystem() { return cultureSystem; }
    public EconomicSystem getEconomicSystem() { return economicSystem; }
    public ReligionSystem getReligionSystem() { return religionSystem; }
    public List<Population> getPopulations() { return populations; }
    public Map<String, Float> getResources() { return resources; }
    public EventSystem getEventSystem() {
        return eventSystem;
    }
    
    public void initializeWorld(ServerWorld world) {
        this.villageSystem = new VillageSystem(world);
        LOGGER.info("Sistema de vilas inicializado para civilização: " + name);
    }
    
    public VillageSystem getVillageSystem() {
        return villageSystem;
    }
}

class Location {
    private final int x, y, z;
    private final String dimension;
    
    public Location(int x, int y, int z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getDimension() { return dimension; }
} 