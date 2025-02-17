package com.medamcraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.medamcraft.MedamCraft;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    private BlockPos lastCheckedPosition = null;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        
        if (!player.getWorld().isClient) {
            BlockPos currentPos = player.getBlockPos();
            
            // Verifica a cada 20 ticks (1 segundo) ou quando muda de posição
            if (lastCheckedPosition == null || 
                !lastCheckedPosition.equals(currentPos) && 
                player.age % 20 == 0) {
                
                lastCheckedPosition = currentPos;
                checkVillageEntry(player, currentPos);
            }
        }
    }
    
    private void checkVillageEntry(PlayerEntity player, BlockPos pos) {
        MedamCraft.getTestCivilization().getVillageSystem()
            .getVillagesNear(pos)
            .forEach(village -> {
                if (village.isInRange(pos)) {
                    // Notifica o jogador
                    player.sendMessage(
                        net.minecraft.text.Text.literal(
                            String.format("Você entrou em uma vila (Nível %d)", 
                                village.getLevel())
                        ), 
                        true
                    );
                    
                    // Atualiza informações da vila
                    MedamCraft.getTestCivilization().getVillageSystem()
                        .onPlayerEnterVillage(player, village);
                }
            });
    }
} 