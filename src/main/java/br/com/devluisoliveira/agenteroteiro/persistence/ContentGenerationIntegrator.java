package br.com.devluisoliveira.agenteroteiro.persistence;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.out.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.persistence.repository.ContentGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação da porta de saída para persistência de conteúdo gerado
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContentGenerationIntegrator implements ContentGenerationPortOut {

    private final ContentGenerationRepository contentGenerationRepository;

    @Override
    @Transactional
    public ContentGeneration saveContentGeneration(ContentGeneration contentGeneration) {
        log.info("[ContentGenerationIntegrator.saveContentGeneration] - Iniciando a persistência de um novo conteúdo");
        try {
            return contentGenerationRepository.save(contentGeneration);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.saveContentGeneration] - Erro ao salvar conteúdo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar conteúdo: " + e.getMessage());
        }
    }

    @Override
    public Optional<ContentGeneration> findById(UUID contentId) {
        log.info("[ContentGenerationIntegrator.findById] - Buscando conteúdo por ID: {}", contentId);
        try {
            return contentGenerationRepository.findById(contentId);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.findById] - Erro ao buscar conteúdo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar conteúdo: " + e.getMessage());
        }
    }

    @Override
    public Page<ContentGeneration> findByUser(User user, Pageable pageable) {
        log.info("[ContentGenerationIntegrator.findByUser] - Buscando conteúdos do usuário: {}", user.getUsername());
        try {
            return contentGenerationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.findByUser] - Erro ao buscar conteúdos do usuário: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar conteúdos do usuário: " + e.getMessage());
        }
    }

    @Override
    public Long countByUser(User user) {
        log.info("[ContentGenerationIntegrator.countByUser] - Contando conteúdos do usuário: {}", user.getUsername());
        try {
            return contentGenerationRepository.countByUser(user);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.countByUser] - Erro ao contar conteúdos do usuário: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao contar conteúdos do usuário: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(UUID contentId) {
        log.info("[ContentGenerationIntegrator.delete] - Excluindo conteúdo ID: {}", contentId);
        try {
            contentGenerationRepository.deleteById(contentId);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.delete] - Erro ao excluir conteúdo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao excluir conteúdo: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int deleteByCreatedAtBefore(LocalDateTime date) {
        log.info("[ContentGenerationIntegrator.deleteByCreatedAtBefore] - Excluindo conteúdos anteriores a: {}", date);
        try {
            return contentGenerationRepository.deleteByCreatedAtBefore(date);
        } catch (Exception e) {
            log.error("[ContentGenerationIntegrator.deleteByCreatedAtBefore] - Erro ao excluir conteúdos antigos: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao excluir conteúdos antigos: " + e.getMessage());
        }
    }
}