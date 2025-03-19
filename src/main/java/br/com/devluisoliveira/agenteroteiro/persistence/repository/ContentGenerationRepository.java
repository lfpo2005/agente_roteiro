package br.com.devluisoliveira.agenteroteiro.persistence.repository;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repositório para acesso aos dados de conteúdo gerado
 */
@Repository
public interface ContentGenerationRepository extends JpaRepository<ContentGeneration, UUID> {

    /**
     * Busca conteúdos por usuário
     */
    Page<ContentGeneration> findByUser(User user, Pageable pageable);

    /**
     * Busca conteúdos por usuário ordenados por data de criação (mais recentes primeiro)
     */
    Page<ContentGeneration> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Exclui conteúdos criados antes de uma determinada data
     *
     * @param date Data limite
     * @return Número de registros excluídos
     */
    @Modifying
    @Query("DELETE FROM ContentGeneration c WHERE c.createdAt < :date")
    int deleteByCreatedAtBefore(@Param("date") LocalDateTime date);

    /**
     * Conta o número de conteúdos de um usuário
     */
    long countByUser(User user);
}