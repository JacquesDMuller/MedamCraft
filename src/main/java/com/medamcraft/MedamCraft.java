package com.medamcraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medamcraft.civilization.Civilization;
import com.medamcraft.commands.MedamCraftCommands;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;

public class MedamCraft implements ModInitializer {
    public static final String MOD_ID = "medamcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static Civilization testCivilization;

    @Override
    public void onInitialize() {
        LOGGER.info("Inicializando MedamCraft...");
        
        // Registra comandos
        MedamCraftCommands.register();
        
        // Cria uma civilização de teste
        testCivilization = new Civilization("Civilização Teste");
        
        // Registra evento de carregamento do mundo
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
                testCivilization.initializeWorld(world);
            }
        });
        
        // Compartilha a civilização com o cliente (apenas se estiver no ambiente cliente)
        if (isClient()) {
            initializeClient();
        }
    }

    private boolean isClient() {
        try {
            Class.forName("net.minecraft.client.MinecraftClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void initializeClient() {
        try {
            Class.forName("com.medamcraft.MedamCraftClient")
                .getMethod("setTestCivilization", Civilization.class)
                .invoke(null, testCivilization);
        } catch (Exception e) {
            LOGGER.error("Erro ao inicializar cliente", e);
        }
    }

    public static Civilization getTestCivilization() {
        return testCivilization;
    }
} 