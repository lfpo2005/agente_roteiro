package br.com.devluisoliveira.agenteroteiro.core.port;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.GenerationRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.GenerationResponseDto;

public interface AgentGenerationPortIn {

    GenerationResponseDto initializeAgentPrayer(User user, GenerationRequestDto request);
}
