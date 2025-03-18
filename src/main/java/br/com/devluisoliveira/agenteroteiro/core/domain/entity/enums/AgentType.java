package br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums;


import lombok.Getter;

@Getter
public enum AgentType {
    GENERIC("Agente Genérico", "prompt_base_generico.txt"),
    STOICISM("Agente Estóico", "prompt_base_estoico.txt");

    private final String description;
    private final String promptTemplate;

    AgentType(String description, String promptTemplate) {
        this.description = description;
        this.promptTemplate = promptTemplate;
    }
}
