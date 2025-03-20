package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.GenericGeneraMapper;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.GenericGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericGenerationService implements GenericGenerationPortIn {

    private final AgentGenerationService agentGenerationService;
    private final ContentGenerationPortOut contentGenerationPortOut;
    private final GenericGeneraMapper genericGeneraMapper;

    @Override
    public ContentGenerationResponse generateContent(User user, ContentGenerationRequest request) {
        log.info("Iniciando geração de conteúdo para usuário {}, título {}",
                user.getUserId(), request.getTitle());

        // Validar a solicitação
        validateRequest(request);

        // Delegar a geração de conteúdo para o serviço especializado
        var response = agentGenerationService.startGeneration((Map<String, Object>) request);

        // Persistir o resultado
        ContentGeneration contentGeneration = genericGeneraMapper.toEntity(response);
        contentGeneration.setUser(user); // Associar ao usuário
        ContentGeneration savedContentGeneration = contentGenerationPortOut.saveContentGeneration(contentGeneration);

        log.info("Processo de geração concluído e salvo com sucesso");

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

    /**
     * Verifica o status de uma geração de conteúdo pelo ID do processo
     *
     * @param processId ID do processo a ser verificado
     * @param user Usuário que está solicitando a verificação
     * @return Resposta com o status atual da geração
     */
    public ContentGenerationResponse checkGenerationStatus(String processId, User user) {
        log.info("[ContentPersistenceService.checkGenerationStatus] - Verificando status para processId: {}, usuário: {}",
                processId, user.getUsername());

        try {
            UUID uuid = UUID.fromString(processId);

            // Verificar se existe uma geração com este ID
            Optional<ContentGeneration> contentOpt = contentGenerationPortOut.findById(uuid);

            if (contentOpt.isEmpty()) {
                log.warn("[ContentPersistenceService.checkGenerationStatus] - Conteúdo não encontrado para ID: {}", processId);
                return ContentGenerationResponse.builder()
                        .processId(uuid)
                        .status("NOT_FOUND")
                        .message("Geração não encontrada")
                        .build();
            }

            ContentGeneration content = contentOpt.get();

            // Verificar se o conteúdo pertence ao usuário solicitante
            if (!content.getUser().getUserId().equals(user.getUserId())) {
                log.warn("[ContentPersistenceService.checkGenerationStatus] - Usuário {} tentou acessar conteúdo de outro usuário",
                        user.getUsername());
                return ContentGenerationResponse.builder()
                        .processId(uuid)
                        .status("UNAUTHORIZED")
                        .message("Não autorizado a acessar esta geração")
                        .build();
            }

            // Construir a resposta com base no conteúdo encontrado
            ContentGenerationResponse response = ContentGenerationResponse.builder()
                    .processId(content.getContentId())
                    .title(content.getTitle())
                    .agentType(content.getAgentType())
                    .text(content.getText())
                    .textShort(content.getTextShort())
                    .description(content.getDescription())
                    .tags(content.getTags())
                    .audio(content.getAudio())
                    .promptUsed(content.getPromptUsed())
                    .status("COMPLETED") // Considerando que se está no banco de dados, está completo
                    .message("Geração encontrada")
                    .build();

            log.info("[ContentPersistenceService.checkGenerationStatus] - Status verificado com sucesso para ID: {}", processId);
            return response;

        } catch (IllegalArgumentException e) {
            log.error("[ContentPersistenceService.checkGenerationStatus] - UUID inválido: {}", processId, e);
            return ContentGenerationResponse.builder()
                    .processId(null)
                    .status("ERROR")
                    .message("ID de processo inválido: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("[ContentPersistenceService.checkGenerationStatus] - Erro ao verificar status: {}", e.getMessage(), e);
            return ContentGenerationResponse.builder()
                    .processId(UUID.fromString(processId))
                    .status("ERROR")
                    .message("Erro ao verificar status: " + e.getMessage())
                    .build();
        }
    }

}
