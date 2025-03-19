package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resumo de conteúdo, usado na listagem de conteúdos do usuário
 * Contém apenas os metadados básicos, sem os textos completos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSummaryDto {

    private UUID contentId;
    private String title;
    private AgentType agentType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    private boolean hasAudio;
    private boolean hasScript;
    private boolean hasShortVersion;

    /**
     * Dias restantes até a expiração (7 dias a partir da criação)
     */
    private long daysUntilExpiration;

    /**
     * Tamanho estimado em KB dos arquivos gerados
     */
    private long estimatedSizeKb;
}