package com.medamcraft.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import com.medamcraft.civilization.ai.CivilizationAI;
import com.medamcraft.civilization.ai.Decision;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;

public class AIDecisionScreen extends Screen {
    private final CivilizationAI ai;
    private List<Decision> displayedDecisions;
    
    public AIDecisionScreen(CivilizationAI ai) {
        super(Text.literal("Decisões da IA"));
        this.ai = ai;
        this.displayedDecisions = new ArrayList<>();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Adiciona botões para interagir com decisões da IA
        addDrawableChild(ButtonWidget.builder(Text.literal("Aceitar Todas as Decisões"), 
            button -> acceptAllDecisions())
            .dimensions(width / 2 - 100, height - 30, 200, 20)
            .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        
        // Renderiza lista de decisões pendentes
        int y = 30;
        for (Decision decision : displayedDecisions) {
            context.drawTextWithShadow(textRenderer, decision.getTitle(), 20, y, 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, decision.getDescription(), 20, y + 15, 0xAAAAAA);
            y += 40;
        }
    }
    
    private void acceptAllDecisions() {
        // Implementa a aceitação de todas as decisões pendentes
    }
} 