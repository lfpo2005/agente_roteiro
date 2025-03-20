package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle;

import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.impl.StyleApplier;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação de StyleApplier para conteúdo genérico
 * Serve como fallback quando não há um StyleApplier específico para o tipo de agente
 */
@Service
@Slf4j
public class GenericStyleApplier implements StyleApplier {

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.GENERIC;
    }

    @Override
    public String applyStyle(String template, ContentGenerationRequest request) {
        log.debug("Aplicando estilo genérico para template");

        // O estilo genérico não tem customizações específicas,
        // mas podemos adicionar algumas diretrizes gerais

        // Substituir placeholders específicos do template genérico
        template = template.replace("{generalGuidelines}",
                "- Use linguagem clara e objetiva\n" +
                        "- Explique conceitos técnicos de forma acessível\n" +
                        "- Mantenha um tom informativo e profissional\n" +
                        "- Organize o conteúdo em seções lógicas\n" +
                        "- Inclua exemplos práticos quando relevante");

        return template;
    }
}