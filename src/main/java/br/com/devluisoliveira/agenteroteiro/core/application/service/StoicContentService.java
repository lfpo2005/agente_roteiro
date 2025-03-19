package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.GenericGeneraMapper;
import br.com.devluisoliveira.agenteroteiro.core.application.mapper.StoicContentMapper;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PhilosopherType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.StoicContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoicContentService implements StoicContentPortIn {

    private final StoicContentMapper stoicContentMapper;
    private final GenericGeneraMapper genericGeneraMapper;
    private final PhilosopherStyleService philosopherStyleService;
    private final AgentGenerationService agentGenerationService;
    private final ContentGenerationPortOut contentGenerationPortOut;

    @Override
    public ContentGenerationResponse generateContent(User user, StoicContentGenerationRequest request) {
        log.info("[StoicContentService.generateContent] - Iniciando a geração de conteúdo estoico para usuário: {}, filósofo: {}",
                user.getUsername(), request.getPhilosopherName());

        try {
            // Validar a solicitação
            validateRequest(request);

            // Garantir que temos um processId
            if (request.getProcessId() == null || request.getProcessId().isEmpty()) {
                request.setProcessId(UUID.randomUUID().toString());
            }

            // Normalizar o nome do filósofo se necessário
            normalizePhilosopher(request);

            // Obter o estilo do filósofo
            String philosopherStyle = philosopherStyleService.getPhilosopherStyle(request.getPhilosopherName());
            request.setPhilosopherStyle(philosopherStyle);

            // Converter a request para Map para processamento pelo AgentGenerationService
            Map<String, Object> requestMap = stoicContentMapper.convertRequestToMap(request);

            // Chamar o serviço centralizado para geração de conteúdo
            ContentGenerationResponse response = agentGenerationService.startGeneration(requestMap);

            // Persistir o resultado
            if (response != null && "COMPLETED".equals(response.getStatus())) {
                saveGeneratedContent(user, response);
                log.info("[StoicContentService.generateContent] - Conteúdo salvo com sucesso, ID: {}", response.getProcessId());
            }

            return response;
        } catch (Exception e) {
            log.error("[StoicContentService.generateContent] - Erro ao gerar conteúdo estoico: {}", e.getMessage(), e);
            return ContentGenerationResponse.builder()
                    .processId(UUID.fromString(request.getProcessId()))
                    .status("ERROR")
                    .message("Erro ao gerar conteúdo estoico: " + e.getMessage())
                    .build();
        }
    }

    private void validateRequest(StoicContentGenerationRequest request) {
        log.debug("[StoicContentService.validateRequest] - Validando requisição");

        if (request.getPhilosopherName() == null || request.getPhilosopherName().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do filósofo estoico é obrigatório");
        }

        if (request.getTheme() == null || request.getTheme().trim().isEmpty()) {
            throw new IllegalArgumentException("O tema do conteúdo é obrigatório");
        }

        if (request.getContentTypes() == null || request.getContentTypes().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um tipo de conteúdo deve ser selecionado");
        }

        log.debug("[StoicContentService.validateRequest] - Requisição válida");
    }

    private void normalizePhilosopher(StoicContentGenerationRequest request) {
        String philosopherName = request.getPhilosopherName();

        // Tentar encontrar o filósofo pelo nome exato ou aproximado
        PhilosopherType philosopher = PhilosopherType.findByName(philosopherName);
        if (philosopher == null) {
            philosopher = PhilosopherType.findByApproximateName(philosopherName);
        }

        // Se encontrou um filósofo no enum, usar o nome normalizado
        if (philosopher != null) {
            log.info("[StoicContentService.normalizePhilosopher] - Filósofo normalizado de '{}' para '{}'",
                    philosopherName, philosopher.getName());
            request.setPhilosopher(philosopher);
            request.setPhilosopherName(philosopher.getName());
        }
    }

    private void saveGeneratedContent(User user, ContentGenerationResponse response) {
        try {
            ContentGeneration contentGeneration = genericGeneraMapper.toEntity(response);
            contentGeneration.setUser(user);
            contentGenerationPortOut.saveContentGeneration(contentGeneration);
        } catch (Exception e) {
            log.error("[StoicContentService.saveGeneratedContent] - Erro ao salvar conteúdo: {}", e.getMessage(), e);
            // Não propagar exceção, apenas logar, para não impedir o retorno da resposta ao usuário
        }
    }
}