package br.com.devluisoliveira.agenteroteiro.persistence.repository;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentGenerationRepository extends JpaRepository<ContentGeneration, UUID> {
}
