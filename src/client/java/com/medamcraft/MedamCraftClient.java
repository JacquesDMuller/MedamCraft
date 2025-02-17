package com.medamcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import com.medamcraft.civilization.Civilization;
import com.medamcraft.client.gui.CivilizationScreen;

public class MedamCraftClient implements ClientModInitializer {
    private static KeyBinding civilizationKey;
    private static Civilization testCivilization;
    
    @Override
    public void onInitializeClient() {
        // Registra a tecla M para abrir a interface
        civilizationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.medamcraft.civilization",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.medamcraft.general"
        ));
        
        // Registra o evento de tecla
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (civilizationKey.wasPressed()) {
                if (testCivilization != null) {
                    client.setScreen(new CivilizationScreen(testCivilization));
                }
            }
        });
    }
    
    public static void setTestCivilization(Civilization civilization) {
        testCivilization = civilization;
    }
} 