package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.application.service.PhilosopherStyleService;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StoicAgentHandler implements AgentHandler {

    private final PhilosopherStyleService philosopherStyleService;

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.STOICISM;
    }

    @Override
    public String preparePrompt(Map<String, Object> request) {
        // Lógica específica para preparar prompt estoico

        return null;
    }

    @Override
    public ContentGenerationResponse processResponse(String aiResponse, Map<String, Object> request) {
        // Lógica específica para processar resposta estoica

        return null;
    }
}
