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
    private DurationType durationType;

    // Parâmetros opcionais para personalização
    private String additionalContext;
    private Boolean includeCallToAction = true;
    private Boolean optimizeForSEO = true;
    private String language = "pt_BR";

    // Parâmetros para geração de áudio
    private Boolean generateAudio = false;
    private String voiceType; // Id da voz ou tipo (masculina/feminina...)
    private Boolean generateShortVersion = false;


    public boolean shouldGenerateShortVersion() {
        // Obter o tipo de duração atual
        DurationType duration = getDurationType();

        // Vídeos já considerados curtos - não precisa de versão short
        if (duration == DurationType.SECONDS_30 ||
                duration == DurationType.SECONDS_60 ||
                duration == DurationType.MINUTES_3) {
            return false;
        }

        // Para vídeos mais longos, gerar short apenas se a flag estiver habilitada
        return Boolean.TRUE.equals(getGenerateShortVersion());
    }

    public void setTargetDuration(Integer seconds) {
        if (seconds != null) {
            this.durationType = DurationType.findClosest(seconds);
        }
    }

    /**
     * Retorna o número estimado de palavras com base na duração
     * @return Número estimado de palavras
     */
    public int getEstimatedWordCount() {
        return getDurationType().getEstimatedWordCount();
    }

    /**
     * Retorna o número estimado de caracteres com base na duração
     * @return Número estimado de caracteres
     */
    public int getEstimatedCharacterCount() {
        return getDurationType().getEstimatedCharacterCount();
    }

    /**
     * Retorna a duração alvo em minutos
     */
    public Integer getTargetDuration() {
        return getDurationType().getDurationInMinutes();
    }
}