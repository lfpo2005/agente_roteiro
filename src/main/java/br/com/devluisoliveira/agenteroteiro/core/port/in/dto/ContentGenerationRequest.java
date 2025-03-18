package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
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
    private Integer targetDuration;

    // Parâmetros opcionais para personalização
    private String additionalContext;
    private Boolean includeCallToAction = true;
    private Boolean optimizeForSEO = true;
    private String language = "pt_BR";

    // Parâmetros para geração de áudio
    private Boolean generateAudio = false;
    private String voiceType; // Id da voz ou tipo (masculina/feminina...)
    private Boolean generateShortVersion = false;

}
