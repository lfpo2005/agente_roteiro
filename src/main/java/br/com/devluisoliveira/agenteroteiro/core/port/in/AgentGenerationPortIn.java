package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.GenerationResponseDto;

public interface AgentGenerationPortIn {

    GenerationResponseDto initializeAgentGeneric(User user, ContentGenerationRequest request);
}
