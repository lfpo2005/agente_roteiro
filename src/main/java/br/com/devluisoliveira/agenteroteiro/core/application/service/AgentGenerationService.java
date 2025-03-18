package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.AgentGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.GenerationRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.GenerationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentGenerationService implements AgentGenerationPortIn {



    @Override
    public GenerationResponseDto initializeAgentPrayer(User user, GenerationRequestDto request) {
        return null;
    }
}
