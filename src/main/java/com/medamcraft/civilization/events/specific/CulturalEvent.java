package com.medamcraft.civilization.events.specific;

import com.medamcraft.civilization.events.CivilizationEvent;
import com.medamcraft.civilization.events.EventEffect;

public class CulturalEvent extends CivilizationEvent {
    public CulturalEvent(String type, String description) {
        super(type, description, 20); // Duração padrão de 20 ticks
        
        // Adiciona efeitos específicos culturais
        addEffect(() -> {
            // Exemplo: Modifica valores culturais
        });
        
        addCompletionEffect(() -> {
            // Exemplo: Gera uma nova tradição
        });
    }
    
    public static CulturalEvent createFestivalEvent() {
        return new CulturalEvent(
            "FESTIVAL",
            "Um grande festival cultural está ocorrendo!"
        );
    }
    
    public static CulturalEvent createCulturalRevolutionEvent() {
        return new CulturalEvent(
            "CULTURAL_REVOLUTION",
            "Uma revolução cultural está transformando a sociedade!"
        );
    }
} 