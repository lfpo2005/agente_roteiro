package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle;

import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.impl.StyleApplier;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação de StyleApplier para conteúdo estoico
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoicStyleApplier implements StyleApplier {

    private final PhilosopherStyleService philosopherStyleService;

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.STOICISM;
    }

    @Override
    public String applyStyle(String template, ContentGenerationRequest request) {
        if (request instanceof StoicContentGenerationRequest) {
            StoicContentGenerationRequest stoicRequest = (StoicContentGenerationRequest) request;

            // Obter estilo do filósofo escolhido
            String philosopherName = stoicRequest.getPhilosopherName();
            String philosopherStyle = philosopherStyleService.getPhilosopherStyle(philosopherName);

            // Aplicar estilo no template
            template = template.replace("{philosopherStyle}", philosopherStyle);

            // Substituições adicionais específicas
            template = template.replace("{philosopher}",
                    stoicRequest.getPhilosopherName() != null ? stoicRequest.getPhilosopherName() : "");

            log.debug("Aplicado estilo do filósofo: {}", philosopherName);
        } else {
            log.warn("Requisição não é do tipo StoicContentGenerationRequest, aplicando estilo padrão");
            template = template.replace("{philosopherStyle}", "Estilo estoico genérico");
            template = template.replace("{philosopher}", "");
        }

        return template;
    }
}