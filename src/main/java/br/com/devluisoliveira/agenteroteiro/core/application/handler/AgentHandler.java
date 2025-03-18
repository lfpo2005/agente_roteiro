package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;

import java.util.Map;

public interface AgentHandler {
    AgentType getSupportedAgentType();
    String preparePrompt(Map<String, Object> request);
    ContentGenerationResponse processResponse(String aiResponse, Map<String, Object> request);
}
