package br.com.devluisoliveira.agenteroteiro.persistence;

import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.persistence.repository.ContentGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentGenerationIntegrator implements ContentGenerationPortOut {

    private final ContentGenerationRepository contentGenerationRepository;

    @Override
    public ContentGeneration saveContentGeneration(ContentGeneration contentGeneration) {
       log.info("[ContentGenerationIntegrator.saveContentGeneration] - Iniciando a persistência de um novo conteúdo");
       try {
              return contentGenerationRepository.save(contentGeneration);
         } catch (Exception e) {
              log.error("[ContentGenerationIntegrator.saveContentGeneration] - erro ao salvar conteúdo", e);
              throw new RuntimeException("Erro ao salvar conteúdo: " + e.getMessage());
       }

    }
}
