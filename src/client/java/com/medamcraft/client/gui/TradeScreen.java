package com.medamcraft.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import com.medamcraft.civilization.Civilization;
import com.medamcraft.civilization.systems.ResourceType;
import com.medamcraft.civilization.systems.Trade;
import java.util.ArrayList;
import java.util.List;

public class TradeScreen extends Screen {
    private final Screen parent;
    private final Civilization civilization;
    private ResourceType selectedResource;
    private Trade.TradeType tradeType = Trade.TradeType.IMPORT;
    private float amount = 0;
    
    public TradeScreen(Screen parent, Civilization civilization) {
        super(Text.literal("Comércio"));
        this.parent = parent;
        this.civilization = civilization;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = width / 2;
        int y = 20;
        
        // Botões de tipo de comércio
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Importar"), 
            button -> tradeType = Trade.TradeType.IMPORT)
            .dimensions(centerX - 100, y, 90, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Exportar"), 
            button -> tradeType = Trade.TradeType.EXPORT)
            .dimensions(centerX + 10, y, 90, 20)
            .build());
            
        y += 30;
        
        // Botões de recursos
        int resourceX = centerX - 150;
        for (ResourceType type : ResourceType.values()) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal(type.getName()), 
                button -> selectedResource = type)
                .dimensions(resourceX, y, 80, 20)
                .build());
                
            if ((resourceX += 100) > centerX + 150) {
                resourceX = centerX - 150;
                y += 25;
            }
        }
        
        y += 40;
        
        // Controles de quantidade
        addDrawableChild(ButtonWidget.builder(
            Text.literal("-10"), 
            button -> adjustAmount(-10))
            .dimensions(centerX - 130, y, 40, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(
            Text.literal("-1"), 
            button -> adjustAmount(-1))
            .dimensions(centerX - 80, y, 40, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(
            Text.literal("+1"), 
            button -> adjustAmount(1))
            .dimensions(centerX + 40, y, 40, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(
            Text.literal("+10"), 
            button -> adjustAmount(10))
            .dimensions(centerX + 90, y, 40, 20)
            .build());
            
        y += 40;
        
        // Botão de confirmar
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Confirmar Transação"), 
            button -> executeTrade())
            .dimensions(centerX - 100, y, 200, 20)
            .build());
            
        y += 25;
        
        // Botão de voltar
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Voltar"), 
            button -> client.setScreen(parent))
            .dimensions(centerX - 50, y, 100, 20)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        
        int centerX = width / 2;
        int y = height - 100;
        
        // Mostra tesouro atual
        String treasuryText = String.format("Tesouro: %.0f ⛃", 
            civilization.getEconomicSystem().getTreasury());
        context.drawTextWithShadow(textRenderer, treasuryText, 10, 10, 0xFFFFFF);
        
        // Renderiza informações da transação
        if (selectedResource != null) {
            float currentAmount = civilization.getEconomicSystem()
                .getResourceAmount(selectedResource);
            float price = civilization.getEconomicSystem()
                .getResourcePrice(selectedResource);
            float totalCost = price * amount;
            
            // Informações do recurso
            List<String> info = new ArrayList<>();
            info.add(String.format("Recurso: %s", selectedResource.getName()));
            info.add(String.format("Estoque Atual: %.0f", currentAmount));
            info.add(String.format("Preço Unitário: %.1f ⛃", price));
            info.add(String.format("Quantidade: %.0f", amount));
            info.add(String.format("Custo Total: %.1f ⛃", totalCost));
            
            // Verifica se pode realizar a transação
            boolean canTrade = true;
            String errorMessage = "";
            
            if (tradeType == Trade.TradeType.IMPORT) {
                if (totalCost > civilization.getEconomicSystem().getTreasury()) {
                    canTrade = false;
                    errorMessage = "§cEsmeraldas insuficientes!";
                }
            } else {
                if (amount > currentAmount) {
                    canTrade = false;
                    errorMessage = "§cRecursos insuficientes!";
                }
            }
            
            // Renderiza as informações
            y = 150;
            for (String line : info) {
                context.drawTextWithShadow(textRenderer, line, 
                    centerX - 100, y, 0xFFFFFF);
                y += 15;
            }
            
            if (!canTrade) {
                context.drawTextWithShadow(textRenderer, errorMessage, 
                    centerX - 100, y, 0xFF0000);
            }
        }
    }
    
    private void adjustAmount(float delta) {
        amount = Math.max(0, amount + delta);
    }
    
    private void executeTrade() {
        if (selectedResource != null && amount > 0) {
            // Implementar lógica de comércio
            civilization.getEconomicSystem().executeTrade(
                new Trade(selectedResource, amount, 
                    civilization.getEconomicSystem().getResourcePrice(selectedResource),
                    tradeType, 20)
            );
            client.setScreen(parent);
        }
    }
} 