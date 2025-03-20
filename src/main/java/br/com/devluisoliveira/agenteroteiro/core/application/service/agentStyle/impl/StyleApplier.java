package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.impl;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;

/**
 * Interface para aplicação de estilos em templates
 * Seguindo o padrão Strategy para diferentes tipos de agentes
 */
public interface StyleApplier {

    /**
     * Retorna o tipo de agente suportado por este aplicador de estilo
     * @return Tipo de agente
     */
    AgentType getSupportedAgentType();

    /**
     * Aplica o estilo específico no template
     * @param template Template base a ser personalizado
     * @param request Requisição com dados para personalização
     * @return Template personalizado com estilo específico aplicado
     */
    String applyStyle(String template, ContentGenerationRequest request);
}
