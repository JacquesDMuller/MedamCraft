package com.medamcraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.medamcraft.MedamCraft;
import com.medamcraft.civilization.Civilization;
import com.medamcraft.civilization.village.VillageData;
import net.minecraft.util.math.Vec3d;

public class MedamCraftCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("medamcraft")
            .then(literal("status")
                .executes(context -> {
                    context.getSource().sendMessage(Text.literal("MedamCraft está funcionando!"));
                    return 1;
                }))
            .then(literal("test")
                .executes(context -> {
                    // Executa testes básicos
                    runTests(context.getSource());
                    return 1;
                }))
            .then(literal("villages")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    Civilization civ = MedamCraft.getTestCivilization();
                    
                    // Obtém informações das vilas próximas
                    Vec3d pos = source.getPosition();
                    BlockPos playerPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
                    List<VillageData> nearbyVillages = civ.getVillageSystem()
                        .getVillagesNear(playerPos);
                    
                    if (nearbyVillages.isEmpty()) {
                        source.sendFeedback(() -> 
                            Text.literal("Nenhuma vila encontrada nas proximidades"), false);
                    } else {
                        nearbyVillages.forEach(village -> {
                            source.sendFeedback(() -> Text.literal(String.format(
                                "Vila em %d, %d, %d:\n" +
                                "- Nível: %d\n" +
                                "- População: %d\n" +
                                "- Prosperidade: %.1f\n" +
                                "- Profissões: %s",
                                village.getCenter().getX(),
                                village.getCenter().getY(),
                                village.getCenter().getZ(),
                                village.getLevel(),
                                village.getPopulation(),
                                village.getProsperity(),
                                String.join(", ", village.getProfessions())
                            )), false);
                        });
                    }
                    return 1;
                })
            )
        );
    }

    private static void runTests(ServerCommandSource source) {
        source.sendMessage(Text.literal("Iniciando testes do MedamCraft..."));
        // Adicione testes aqui
        source.sendMessage(Text.literal("Testes concluídos!"));
    }
} 