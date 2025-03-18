package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.GenericGeneraMapper;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.GenericGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericGenerationService implements GenericGenerationPortIn {

    private final AgentGenerationService agentGenerationService;
    private final ContentGenerationPortOut contentGenerationPortOut;
    private final GenericGeneraMapper genericGeneraMapper;


    @Override
    public ContentGenerationResponse generateContent(User user, ContentGenerationRequest request) {
        log.info("[AgentGenerationService.initializeAgentGeneric] - Iniciando a geração de conteúdo para o usuário {}, titulo {}",
                user.getUserId(), request.getTitle());

        // Validar a solicitação
        validateRequest(request);

        log.info("[AgentGenerationService.initializeAgentGeneric] - Solicitação validada com sucesso");
        // Iniciar o processo de geração de conteúdo
        ContentGenerationResponse process = (ContentGenerationResponse) agentGenerationService.startGeneration(request);

        ContentGeneration contentGeneration = genericGeneraMapper.toEntity(process);
        ContentGeneration savedContentGeneration = contentGenerationPortOut.saveContentGeneration(contentGeneration);

        log.info("[AgentGenerationService.initializeAgentGeneric] - Processo de geração Salvo com sucesso");


        return genericGeneraMapper.toDto(savedContentGeneration);
    }

    private void validateRequest(ContentGenerationRequest request) {
        // Implementar validações
        if (request.getVideoTopic() == null || request.getVideoTopic().isEmpty()) {
            throw new IllegalArgumentException("O tópico do vídeo é obrigatório");
        }

        if (request.getContentTypes() == null || request.getContentTypes().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um tipo de conteúdo deve ser selecionado");
        }

        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("O título do vídeo é obrigatório");
        }
    }

}
