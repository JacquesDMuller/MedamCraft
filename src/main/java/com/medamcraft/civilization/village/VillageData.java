package com.medamcraft.civilization.village;

import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VillageData {
    private final UUID id;
    private final BlockPos center;
    private Set<BlockPos> buildings;
    private Set<UUID> villagers;
    private int level;
    private float prosperity;
    private int population;
    private Set<String> professions;
    
    public VillageData(BlockPos center) {
        this.id = UUID.randomUUID();
        this.center = center;
        this.buildings = new HashSet<>();
        this.villagers = new HashSet<>();
        this.level = 1;
        this.prosperity = 0.0f;
        this.population = 0;
        this.professions = new HashSet<>();
    }
    
    public void addBuilding(BlockPos pos) {
        buildings.add(pos);
    }
    
    public void addVillager(UUID villagerId, String profession) {
        villagers.add(villagerId);
        professions.add(profession);
        population++;
    }
    
    public void removeVillager(UUID villagerId) {
        villagers.remove(villagerId);
        population--;
        updateProfessions();
    }
    
    private void updateProfessions() {
        // Atualiza lista de profissões baseado nos villagers existentes
    }
    
    public boolean isInRange(BlockPos pos) {
        int range = 32 * level; // Raio aumenta com o nível
        return pos.isWithinDistance(center, range);
    }
    
    public void addProsperity(float value) {
        prosperity += value;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        float threshold = level * 1000.0f;
        if (prosperity >= threshold) {
            level++;
            prosperity -= threshold;
        }
    }
    
    // Getters
    public BlockPos getCenter() { return center; }
    public int getLevel() { return level; }
    public float getProsperity() { return prosperity; }
    public int getPopulation() { return population; }
    public Set<String> getProfessions() { return professions; }
    public Set<BlockPos> getBuildings() { return buildings; }
    public Set<UUID> getVillagers() {
        return villagers;
    }
} 