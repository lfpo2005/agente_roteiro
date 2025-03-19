package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela persistência centralizada de conteúdos gerados
 * por diferentes agentes (genérico, estoico, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentPersistenceService {

    private final ContentGenerationPortOut contentGenerationPortOut;

    /**
     * Salva o conteúdo gerado associado a um usuário
     *
     * @param user Usuário que solicitou a geração
     * @param response Resposta contendo o conteúdo gerado
     * @return ContentGeneration objeto salvo
     */
    public ContentGeneration saveContent(User user, ContentGenerationResponse response) {
        log.info("[ContentPersistenceService.saveContent] - Iniciando persistência de conteúdo para usuário: {}",
                user.getUsername());

        try {
            // Criar entidade para persistência
            ContentGeneration contentGeneration = mapResponseToEntity(response);
            contentGeneration.setUser(user);

            // Persistir
            ContentGeneration savedContent = contentGenerationPortOut.saveContentGeneration(contentGeneration);

            log.info("[ContentPersistenceService.saveContent] - Conteúdo persistido com sucesso, ID: {}",
                    savedContent.getContentId());

            return savedContent;
        } catch (Exception e) {
            log.error("[ContentPersistenceService.saveContent] - Erro ao persistir conteúdo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar conteúdo gerado: " + e.getMessage(), e);
        }
    }

    /**
     * Converte a resposta da API para a entidade ContentGeneration
     */
    private ContentGeneration mapResponseToEntity(ContentGenerationResponse response) {
        ContentGeneration entity = new ContentGeneration();

        // Mapear campos básicos
        entity.setContentId(response.getProcessId() != null ? response.getProcessId() : UUID.randomUUID());
        entity.setTitle(response.getTitle());
        entity.setAgentType(response.getAgentType() != null ? response.getAgentType() : AgentType.GENERIC);
        entity.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        // Mapear conteúdo
        entity.setText(response.getText());
        entity.setTextShort(response.getTextShort());
        entity.setDescription(response.getDescription());
        entity.setTags(response.getTags());
        entity.setAudio(response.getAudio());

        // Armazenar conteúdo adicional como JSON se disponível
        if (response.getGeneratedContent() != null && !response.getGeneratedContent().isEmpty()) {
            entity.setAdditionalContent(convertMapToJsonString(response.getGeneratedContent()));
        }

        // Armazenar prompt usado (opcional)
        if (response.getPromptUsed() != null) {
            entity.setPromptUsed(response.getPromptUsed());
        }

        return entity;
    }

    /**
     * Converte o mapa de conteúdo para String JSON
     * Note: Se você tiver uma biblioteca JSON no projeto, pode usá-la aqui
     */
    private String convertMapToJsonString(Map<ContentType, String> contentMap) {
        if (contentMap == null || contentMap.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<ContentType, String> entry : contentMap.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            // Escape JSON strings
            String value = entry.getValue()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            json.append("\"").append(entry.getKey().name()).append("\":");
            json.append("\"").append(value).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Somente salva o conteúdo se o processamento foi bem-sucedido
     * e o status é "COMPLETED"
     */
    public ContentGeneration saveContentIfSuccessful(User user, ContentGenerationResponse response) {
        if (response != null && "COMPLETED".equals(response.getStatus())) {
            return saveContent(user, response);
        } else {
            log.warn("[ContentPersistenceService.saveContentIfSuccessful] - Conteúdo não salvo devido ao status: {}",
                    response != null ? response.getStatus() : "null");
            return null;
        }
    }

    /**
     * Retorna os metadados resumidos dos conteúdos do usuário (sem conteúdo completo)
     * para otimização da listagem
     */
    public Page<ContentSummaryDto> getUserContentSummary(User user, Pageable pageable) {
        log.info("[ContentPersistenceService.getUserContentSummary] - Buscando resumos de conteúdos para usuário: {}",
                user.getUsername());

        Page<ContentGeneration> userContent = contentGenerationPortOut.findByUser(user, pageable);

        // Converter para DTOs resumidos
        var contentSummaries = userContent.getContent().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());

        return new PageImpl<>(contentSummaries, pageable, userContent.getTotalElements());
    }

    /**
     * Converte uma entidade ContentGeneration para um DTO resumido
     */
    private ContentSummaryDto mapToSummaryDto(ContentGeneration content) {
        // Calcular dias até expiração (7 dias a partir da criação)
        LocalDateTime expirationDate = content.getCreatedAt().plusDays(7);
        long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDateTime.now(ZoneId.of("UTC")), expirationDate);

        // Estimar tamanho dos arquivos baseado no conteúdo
        long textSize = 0;
        if (content.getText() != null) textSize += content.getText().length();
        if (content.getTextShort() != null) textSize += content.getTextShort().length();
        if (content.getDescription() != null) textSize += content.getDescription().length();
        if (content.getTags() != null) textSize += content.getTags().length();

        // Converter para KB (estimativa)
        long estimatedSizeKb = textSize / 1024;

        // Se tiver áudio, adicionar estimativa
        boolean hasAudio = content.getAudio() != null && !content.getAudio().isEmpty();
        if (hasAudio) {
            estimatedSizeKb += 500; // Estimativa genérica para áudio
        }

        return ContentSummaryDto.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .agentType(content.getAgentType())
                .createdAt(content.getCreatedAt())
                .hasAudio(hasAudio)
                .hasScript(content.getText() != null && !content.getText().isEmpty())
                .hasShortVersion(content.getTextShort() != null && !content.getTextShort().isEmpty())
                .daysUntilExpiration(daysUntilExpiration)
                .estimatedSizeKb(estimatedSizeKb)
                .build();
    }

    /**
     * Busca um conteúdo específico pelo ID e verifica se pertence ao usuário
     */
    public Optional<ContentGeneration> getUserContentById(UUID contentId, User user) {
        log.info("[ContentPersistenceService.getUserContentById] - Buscando conteúdo ID: {} para usuário: {}",
                contentId, user.getUsername());

        Optional<ContentGeneration> contentOpt = contentGenerationPortOut.findById(contentId);

        // Verificar se o conteúdo pertence ao usuário
        if (contentOpt.isPresent() && contentOpt.get().getUser().getUserId().equals(user.getUserId())) {
            return contentOpt;
        }

        return Optional.empty();
    }

    /**
     * Verifica se um conteúdo pertence a um determinado usuário
     */
    public boolean contentBelongsToUser(UUID contentId, User user) {
        return getUserContentById(contentId, user).isPresent();
    }

    /**
     * Conta o número de conteúdos de um usuário
     */
    public Long countUserContent(User user) {
        return contentGenerationPortOut.countByUser(user);
    }

    /**
     * Exclui um conteúdo se ele pertencer ao usuário
     */
    public boolean deleteUserContent(UUID contentId, User user) {
        log.info("[ContentPersistenceService.deleteUserContent] - Excluindo conteúdo ID: {} para usuário: {}",
                contentId, user.getUsername());

        Optional<ContentGeneration> contentOpt = contentGenerationPortOut.findById(contentId);

        if (contentOpt.isPresent() && contentOpt.get().getUser().getUserId().equals(user.getUserId())) {
            contentGenerationPortOut.delete(contentId);
            return true;
        }

        return false;
    }

    /**
     * Exclui todos os conteúdos mais antigos que 7 dias
     * Este método será chamado pelo scheduler
     */
    public void deleteExpiredContent() {
        log.info("[ContentPersistenceService.deleteExpiredContent] - Excluindo conteúdos expirados");

        // Calcular data limite (7 dias atrás)
        LocalDateTime expirationDate = LocalDateTime.now(ZoneId.of("UTC")).minusDays(7);

        // Executar exclusão
        int deletedCount = contentGenerationPortOut.deleteByCreatedAtBefore(expirationDate);

        log.info("[ContentPersistenceService.deleteExpiredContent] - {} conteúdos expirados foram excluídos", deletedCount);
    }
}