package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.StoicContentMapper;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.StoicContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoicContentService implements StoicContentPortIn {

    private final StoicContentMapper stoicContentMapper;
    private final PhilosopherStyleService philosopherStyleService;
    private final AgentGenerationService agentGenerationService;

    public ContentGenerationResponse generateContent(User user, StoicContentGenerationRequest request) {
        log.info("[StoicContentService.generateContent] - Iniciando a geração de conteúdo");

        validateRequest(request);

        String philosopherStyle = philosopherStyleService.getPhilosopherStyle(request.getPhilosopherName());
        request.setPhilosopherStyle(philosopherStyle);

        var aiResponse = agentGenerationService.startGeneration((Map<String, Object>) request);

        // Processar resposta
        return processResponse(String.valueOf(aiResponse)); // não definido ainda
    }

    private ContentGenerationResponse processResponse(String aiResponse) {
        // Lógica para processar resposta
        return null;
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
