package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ContentGenerationResponse {
    private UUID processId;
    private Map<ContentType, String> generatedContent;
    private String title;
    private AgentType agentType;
    private String text;
    private String textShort;
    private String description;
    private String audio;
    private String tags;

    private String status = "COMPLETED";
    private String message;
}
