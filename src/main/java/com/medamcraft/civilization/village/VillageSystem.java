package com.medamcraft.civilization.village;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;

public class VillageSystem {
    private final Map<BlockPos, VillageData> villages;
    private final ServerWorld world;
    
    public VillageSystem(ServerWorld world) {
        this.villages = new HashMap<>();
        this.world = world;
    }
    
    public void tick() {
        if (world == null) {
            System.out.println("VillageSystem: mundo é null!");
            return;
        }
        
        if (world.getTime() % 100 == 0) { // A cada 5 segundos
            System.out.println("VillageSystem: procurando por vilas...");
            System.out.println("Vilas atuais: " + villages.size());
            detectNewVillages();
            updateExistingVillages();
        }
    }
    
    private void detectNewVillages() {
        if (world == null) return; // Proteção contra null
        
        // Aumenta o raio de busca
        int searchRadius = 100;
        BlockPos playerPos = world.getPlayers().isEmpty() ? null : 
            world.getPlayers().get(0).getBlockPos();
        
        if (playerPos != null) {
            // Procura por villagers próximos ao jogador
            world.getEntitiesByType(
                net.minecraft.entity.EntityType.VILLAGER,
                new net.minecraft.util.math.Box(
                    playerPos.add(-searchRadius, -searchRadius, -searchRadius),
                    playerPos.add(searchRadius, searchRadius, searchRadius)
                ),
                villager -> true
            ).forEach(villager -> {
                BlockPos villagerPos = villager.getBlockPos();
                
                // Verifica se já existe uma vila aqui
                Optional<VillageData> existingVillage = findVillageForPosition(villagerPos);
                
                if (existingVillage.isEmpty()) {
                    // Cria nova vila e loga a criação
                    createNewVillage(villagerPos, (VillagerEntity)villager);
                    System.out.println("Nova vila criada em: " + villagerPos);
                }
            });
        }
    }
    
    private void createNewVillage(BlockPos center, VillagerEntity villager) {
        // Verifica se já existe uma vila próxima
        if (villages.containsKey(center)) return;
        
        VillageData village = new VillageData(center);
        
        // Adiciona o villager inicial
        village.addVillager(villager.getUuid(), 
            villager.getVillagerData().getProfession().toString());
        
        // Procura por outros villagers próximos
        int nearbyRadius = 32;
        world.getEntitiesByType(
            net.minecraft.entity.EntityType.VILLAGER,
            new net.minecraft.util.math.Box(
                center.add(-nearbyRadius, -nearbyRadius, -nearbyRadius),
                center.add(nearbyRadius, nearbyRadius, nearbyRadius)
            ),
            v -> v != villager
        ).forEach(v -> {
            village.addVillager(v.getUuid(), 
                ((VillagerEntity)v).getVillagerData().getProfession().toString());
        });
        
        // Detecta construções
        scanForBuildings(village);
        
        // Só registra a vila se tiver construções ou mais de um villager
        if (!village.getBuildings().isEmpty() || village.getPopulation() > 1) {
            villages.put(center, village);
            System.out.println("Vila registrada com " + village.getPopulation() + 
                " villagers e " + village.getBuildings().size() + " construções");
        }
    }
    
    private void scanForBuildings(VillageData village) {
        BlockPos center = village.getCenter();
        int range = 32;
        
        // Escaneia área em busca de construções
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -5; y <= 15; y++) {
                    BlockPos pos = center.add(x, y, z);
                    if (isBuilding(pos)) {
                        village.addBuilding(pos);
                    }
                }
            }
        }
    }
    
    private boolean isBuilding(BlockPos pos) {
        net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
        return block instanceof net.minecraft.block.BedBlock ||
               block instanceof net.minecraft.block.DoorBlock ||
               block instanceof net.minecraft.block.CraftingTableBlock ||
               block instanceof net.minecraft.block.FurnaceBlock ||
               block instanceof net.minecraft.block.ChestBlock ||
               block instanceof net.minecraft.block.TorchBlock ||
               isWorkstation(pos) ||
               // Verifica se tem um teto (bloco sólido acima)
               world.getBlockState(pos.up()).isSolidBlock(world, pos);
    }
    
    private boolean isWorkstation(BlockPos pos) {
        // Verifica se é uma workstation de villager manualmente
        net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
        return block instanceof net.minecraft.block.SmithingTableBlock ||
               block instanceof net.minecraft.block.BlastFurnaceBlock ||
               block instanceof net.minecraft.block.BrewingStandBlock ||
               block instanceof net.minecraft.block.CartographyTableBlock ||
               block instanceof net.minecraft.block.ComposterBlock ||
               block instanceof net.minecraft.block.FletchingTableBlock ||
               block instanceof net.minecraft.block.GrindstoneBlock ||
               block instanceof net.minecraft.block.LecternBlock ||
               block instanceof net.minecraft.block.LoomBlock ||
               block instanceof net.minecraft.block.StonecutterBlock;
    }
    
    private Optional<VillageData> findVillageForPosition(BlockPos pos) {
        return villages.values().stream()
            .filter(village -> village.isInRange(pos))
            .findFirst();
    }
    
    private void updateExistingVillages() {
        villages.values().forEach(village -> {
            // Atualiza população
            updateVillagePopulation(village);
            // Atualiza construções
            updateVillageBuildings(village);
        });
    }
    
    private void updateVillagePopulation(VillageData village) {
        // Verifica villagers vivos e suas profissões
        village.getVillagers().removeIf(villagerId -> 
            !isVillagerAlive(villagerId));
    }
    
    private boolean isVillagerAlive(UUID villagerId) {
        return world.getEntity(villagerId) != null;
    }
    
    private void updateVillageBuildings(VillageData village) {
        // Remove construções destruídas
        village.getBuildings().removeIf(pos -> !isBuilding(pos));
        // Procura por novas construções
        scanForBuildings(village);
    }
    
    // Métodos para interação com o sistema de comércio
    public void onTrade(BlockPos villagePos, float value) {
        VillageData village = villages.get(villagePos);
        if (village != null) {
            village.addProsperity(value);
        }
    }
    
    public List<VillageData> getVillagesNear(BlockPos pos) {
        return villages.values().stream()
            .filter(village -> village.isInRange(pos))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public void onPlayerEnterVillage(PlayerEntity player, VillageData village) {
        // Atualiza dados da vila quando um jogador entra
        scanForBuildings(village); // Atualiza construções
        updateVillagePopulation(village); // Atualiza população
        
        // Envia informações detalhadas ao jogador
        player.sendMessage(
            net.minecraft.text.Text.literal(String.format(
                "§6=== Vila ===\n" +
                "§7População: §f%d villagers\n" +
                "§7Prosperidade: §f%.1f\n" +
                "§7Profissões: §f%s",
                village.getPopulation(),
                village.getProsperity(),
                String.join(", ", village.getProfessions())
            )), 
            false
        );
    }
} 