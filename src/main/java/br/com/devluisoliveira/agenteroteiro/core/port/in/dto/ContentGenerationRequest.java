package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ContentGenerationRequest {

    private String processId;

    // Tipo de agente especializado a ser usado
    private AgentType agentType = AgentType.GENERIC;

    // Tipo(s) de conteúdo a ser gerado
    private List<ContentType> contentTypes = new ArrayList<>();

    // Informações básicas do vídeo
    private String title;
    private String theme;
    private String notes;
    private String videoTopic;
    private String targetAudience;
    private String toneStyle;
    private DurationType durationType = DurationType.MINUTES_10;

    // Parâmetros opcionais para personalização
    private String additionalContext;
    private Boolean includeCallToAction = true;
    private Boolean optimizeForSEO = true;
    private String language = "pt_BR";

    // Parâmetros para geração de áudio
    private Boolean generateAudio = false;
    private String voiceType; // Id da voz ou tipo (masculina/feminina...)
    private Boolean generateShortVersion = false;


    public Integer getTargetDuration() {
        return durationType != null ? durationType.getDurationInMinutes() : 10;
    }

    /**
     * Método de conveniência para compatibilidade retroativa
     * @param minutes Duração em minutos
     */
    public void setTargetDuration(Integer minutes) {
        if (minutes != null) {
            this.durationType = DurationType.findClosest(minutes);
        }
    }

    /**
     * Retorna o número estimado de palavras com base na duração
     * @return Número estimado de palavras
     */
    public int getEstimatedWordCount() {
        return durationType != null ? durationType.getEstimatedWordCount() : 1300; // 10 min default
    }

    /**
     * Retorna o número estimado de caracteres com base na duração
     * @return Número estimado de caracteres
     */
    public int getEstimatedCharacterCount() {
        return durationType != null ? durationType.getEstimatedCharacterCount() : 7500; // 10 min default
    }

}
