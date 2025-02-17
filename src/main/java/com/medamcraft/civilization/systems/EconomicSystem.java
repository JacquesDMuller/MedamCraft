package com.medamcraft.civilization.systems;

import com.medamcraft.civilization.Civilization;
import com.medamcraft.civilization.events.CivilizationEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class EconomicSystem {
    private final Civilization civilization;
    
    // Recursos básicos
    private Map<ResourceType, Float> resources;
    private Map<ResourceType, Float> production;
    private Map<ResourceType, Float> consumption;
    private Map<ResourceType, Float> prices;
    
    // Economia
    private float gdp; // Produto Interno Bruto
    private float inflation; // Taxa de inflação
    private float taxRate; // Taxa de impostos
    private float treasury; // Tesouro
    private List<Trade> activeTrades; // Trocas comerciais ativas
    
    public EconomicSystem(Civilization civilization) {
        this.civilization = civilization;
        this.resources = new HashMap<>();
        this.production = new HashMap<>();
        this.consumption = new HashMap<>();
        this.prices = new HashMap<>();
        this.activeTrades = new ArrayList<>();
        
        initializeEconomy();
    }
    
    private void initializeEconomy() {
        // Inicializa recursos básicos
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 100f);
            production.put(type, 1f);
            consumption.put(type, 0.5f);
            prices.put(type, type.getBasePrice());
        }
        
        gdp = 1000f;
        inflation = 0.02f;
        taxRate = 0.1f;
        treasury = 500f;
    }
    
    public void update() {
        updateResources();
        updatePrices();
        updateEconomy();
        processTrades();
        collectTaxes();
    }
    
    private void updateResources() {
        for (ResourceType type : ResourceType.values()) {
            // Produção
            float produced = production.get(type) * getProductionMultiplier(type);
            
            // Consumo
            float consumed = consumption.get(type) * getConsumptionMultiplier(type);
            
            // Atualiza estoque
            float current = resources.get(type);
            float newAmount = current + produced - consumed;
            resources.put(type, Math.max(0, newAmount));
            
            // Verifica escassez
            if (newAmount < type.getCriticalLevel()) {
                triggerShortageEvent(type);
            }
        }
    }
    
    private float getProductionMultiplier(ResourceType type) {
        float stability = civilization.getStability();
        float development = civilization.getDevelopment();
        return 1.0f + (stability * 0.5f) + (development * 0.5f);
    }
    
    private float getConsumptionMultiplier(ResourceType type) {
        int populationSize = civilization.getPopulations().stream()
            .mapToInt(pop -> pop.getSize())
            .sum();
        return 1.0f + (populationSize / 1000f);
    }
    
    private void updatePrices() {
        for (ResourceType type : ResourceType.values()) {
            float supply = resources.get(type);
            float demand = consumption.get(type);
            float currentPrice = prices.get(type);
            
            // Ajusta preço baseado em oferta e demanda
            float newPrice = currentPrice * (1 + ((demand - supply) / supply) * 0.1f);
            newPrice = Math.max(type.getBasePrice() * 0.5f, 
                              Math.min(type.getBasePrice() * 2f, newPrice));
            
            prices.put(type, newPrice);
        }
    }
    
    private void updateEconomy() {
        // Calcula novo GDP
        float newGdp = calculateGDP();
        float gdpGrowth = (newGdp - gdp) / gdp;
        gdp = newGdp;
        
        // Atualiza inflação
        inflation = Math.max(0.01f, Math.min(0.5f, 
            inflation + (gdpGrowth * 0.1f) + (treasury / 10000f)));
    }
    
    private float calculateGDP() {
        float totalValue = 0f;
        for (ResourceType type : ResourceType.values()) {
            totalValue += resources.get(type) * prices.get(type);
        }
        return totalValue;
    }
    
    private void processTrades() {
        List<Trade> completedTrades = new ArrayList<>();
        
        for (Trade trade : activeTrades) {
            if (trade.process()) {
                completedTrades.add(trade);
            }
        }
        
        activeTrades.removeAll(completedTrades);
    }
    
    private void collectTaxes() {
        float taxIncome = gdp * taxRate;
        treasury += taxIncome;
    }
    
    private void triggerShortageEvent(ResourceType type) {
        civilization.getEventSystem().triggerEvent(
            new CivilizationEvent(
                "ECONOMIC_SHORTAGE",
                "Escassez de " + type.getName(),
                20
            )
        );
    }
    
    // Getters
    public float getStability() {
        float resourceStability = calculateResourceStability();
        float economicGrowth = (gdp > 0) ? Math.min(1, gdp / 10000f) : 0;
        return (resourceStability + economicGrowth) / 2;
    }
    
    public float getDevelopment() {
        return Math.min(1, gdp / 50000f);
    }
    
    private float calculateResourceStability() {
        float stability = 1.0f;
        for (ResourceType type : ResourceType.values()) {
            if (resources.get(type) < type.getCriticalLevel()) {
                stability *= 0.8f;
            }
        }
        return stability;
    }
    
    public float getGDP() { return gdp; }
    public float getInflation() { return inflation; }
    public float getTreasury() { return treasury; }

    public float getResourceAmount(ResourceType type) {
        return resources.getOrDefault(type, 0f);
    }

    public float getResourcePrice(ResourceType type) {
        return prices.getOrDefault(type, type.getBasePrice());
    }

    public void executeTrade(Trade trade) {
        ResourceType resource = trade.getResource();
        float amount = trade.getAmount();
        float totalCost = amount * trade.getPrice();
        
        if (trade.getType() == Trade.TradeType.IMPORT) {
            // Verifica se tem esmeraldas suficientes
            if (treasury >= totalCost) {
                treasury -= totalCost;
                float currentAmount = resources.getOrDefault(resource, 0f);
                resources.put(resource, currentAmount + amount);
                
                // Notifica o evento
                civilization.getEventSystem().triggerEvent(
                    new CivilizationEvent(
                        "ECONOMIC_IMPORT",
                        "Importou " + amount + " de " + resource.getName(),
                        10
                    )
                );
            }
        } else { // EXPORT
            // Verifica se tem recursos suficientes
            float currentAmount = resources.getOrDefault(resource, 0f);
            if (currentAmount >= amount) {
                resources.put(resource, currentAmount - amount);
                treasury += totalCost;
                
                // Notifica o evento
                civilization.getEventSystem().triggerEvent(
                    new CivilizationEvent(
                        "ECONOMIC_EXPORT",
                        "Exportou " + amount + " de " + resource.getName(),
                        10
                    )
                );
            }
        }
    }

    public void handleEvent(CivilizationEvent event) {
        if (event.getType().startsWith("ECONOMIC_SHORTAGE")) {
            // Reduz estabilidade durante escassez
            inflation += 0.01f;
        }
    }
} 