package br.com.devluisoliveira.agenteroteiro.core.port.out;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de porta de saída para persistência e recuperação de conteúdo gerado
 */
public interface ContentGenerationPortOut {

    /**
     * Salva um conteúdo gerado na base de dados
     *
     * @param contentGeneration Entidade a ser salva
     * @return Entidade salva com ID gerado
     */
    ContentGeneration saveContentGeneration(ContentGeneration contentGeneration);

    /**
     * Busca um conteúdo pelo seu ID
     *
     * @param contentId ID do conteúdo
     * @return Optional contendo o conteúdo se encontrado
     */
    Optional<ContentGeneration> findById(UUID contentId);

    /**
     * Busca todos os conteúdos de um usuário específico
     *
     * @param user Usuário
     * @param pageable Informações de paginação
     * @return Página de conteúdos do usuário
     */
    Page<ContentGeneration> findByUser(User user, Pageable pageable);

    /**
     * Exclui um conteúdo pelo ID
     *
     * @param contentId ID do conteúdo a ser excluído
     */
    void delete(UUID contentId);

    /**
     * Exclui conteúdos criados antes de uma determinada data
     *
     * @param date Data limite
     * @return Número de registros excluídos
     */
    int deleteByCreatedAtBefore(LocalDateTime date);
}
