package com.medamcraft.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.medamcraft.MedamCraft;
import com.medamcraft.civilization.Civilization;
import net.minecraft.client.gui.DrawContext;
import com.medamcraft.civilization.systems.ResourceType;
import com.medamcraft.civilization.systems.EconomicSystem;
import net.minecraft.util.Formatting;

public class CivilizationScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(MedamCraft.MOD_ID, "textures/gui/civilization.png");
    private final Civilization civilization;
    private Tab currentTab = Tab.OVERVIEW;
    
    public CivilizationScreen(Civilization civilization) {
        super(Text.literal("Civilização"));
        this.civilization = civilization;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Adiciona botões de navegação
        int buttonWidth = 100;
        addDrawableChild(ButtonWidget.builder(Text.literal("Visão Geral"), button -> setTab(Tab.OVERVIEW))
            .dimensions(10, 10, buttonWidth, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(Text.literal("Cultura"), button -> setTab(Tab.CULTURE))
            .dimensions(10, 35, buttonWidth, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(Text.literal("Economia"), button -> setTab(Tab.ECONOMY))
            .dimensions(10, 60, buttonWidth, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(Text.literal("Religião"), button -> setTab(Tab.RELIGION))
            .dimensions(10, 85, buttonWidth, 20)
            .build());
            
        addDrawableChild(ButtonWidget.builder(Text.literal("População"), button -> setTab(Tab.POPULATION))
            .dimensions(10, 110, buttonWidth, 20)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        
        switch (currentTab) {
            case OVERVIEW:
                renderOverview(context);
                break;
            case CULTURE:
                renderCulture(context);
                break;
            case ECONOMY:
                renderEconomy(context);
                break;
            case RELIGION:
                renderReligion(context);
                break;
            case POPULATION:
                renderPopulation(context);
                break;
        }
    }
    
    private void renderOverview(DrawContext context) {
        context.drawTextWithShadow(textRenderer, "Visão Geral da Civilização", 120, 20, 0xFFFFFF);
    }
    
    private void renderCulture(DrawContext context) {
        context.drawTextWithShadow(textRenderer, "Sistema Cultural", 120, 20, 0xFFFFFF);
    }
    
    private void renderEconomy(DrawContext context) {
        EconomicSystem economy = civilization.getEconomicSystem();
        int x = 120;
        int y = 20;
        
        // Título
        context.drawTextWithShadow(textRenderer, "Sistema Econômico", x, y, 0xFFFFFF);
        y += 25;
        
        // Indicadores econômicos
        String gdpText = String.format("PIB: %.0f ⛃", economy.getGDP());
        context.drawTextWithShadow(textRenderer, gdpText, x, y, 0xFFFFFF);
        y += 15;
        
        String inflationText = String.format("Inflação: %.1f%%", economy.getInflation() * 100);
        int inflationColor = economy.getInflation() > 0.05f ? 0xFF0000 : 0x00FF00;
        context.drawTextWithShadow(textRenderer, inflationText, x, y, inflationColor);
        y += 15;
        
        String treasuryText = String.format("Tesouro: %.0f ⛃", economy.getTreasury());
        context.drawTextWithShadow(textRenderer, treasuryText, x, y, 0xFFFFFF);
        y += 25;
        
        // Recursos
        context.drawTextWithShadow(textRenderer, "Recursos:", x, y, 0xFFFFFF);
        y += 15;
        
        for (ResourceType type : ResourceType.values()) {
            float amount = economy.getResourceAmount(type);
            float price = economy.getResourcePrice(type);
            
            String resourceText = String.format("%s: %.0f (%.1f ⛃)", 
                type.getName(), amount, price);
            
            // Cor baseada no nível do recurso
            int color = amount < type.getCriticalLevel() ? 
                0xFF0000 : // Vermelho se abaixo do nível crítico
                0x00FF00;  // Verde se ok
                
            context.drawTextWithShadow(textRenderer, resourceText, x, y, color);
            y += 12;
        }
        
        // Botões de ação
        if (y + 25 < height) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Gerenciar Comércio"), 
                button -> openTradeScreen())
                .dimensions(x, y + 5, 120, 20)
                .build());
        }
    }
    
    private void renderReligion(DrawContext context) {
        context.drawTextWithShadow(textRenderer, "Sistema Religioso", 120, 20, 0xFFFFFF);
    }
    
    private void renderPopulation(DrawContext context) {
        context.drawTextWithShadow(textRenderer, "População", 120, 20, 0xFFFFFF);
    }
    
    private void setTab(Tab tab) {
        this.currentTab = tab;
    }
    
    private void openTradeScreen() {
        client.setScreen(new TradeScreen(this, civilization));
    }
    
    private enum Tab {
        OVERVIEW, CULTURE, ECONOMY, RELIGION, POPULATION
    }
} 