package br.com.devluisoliveira.agenteroteiro.core.application.mapper;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoicContentMapper {



    public ContentGenerationRequest convertToStandardRequest(StoicContentGenerationRequest stoicRequest) {
        ContentGenerationRequest request = new ContentGenerationRequest();

        request.setProcessId(stoicRequest.getProcessId());
        request.setAgentType(stoicRequest.getAgentType());
        request.setContentTypes(stoicRequest.getContentTypes());
        request.setTitle(stoicRequest.getTitle());
        request.setTheme(stoicRequest.getTheme());
        request.setNotes(stoicRequest.getNotes());
        request.setTargetAudience(stoicRequest.getTargetAudience());
        request.setToneStyle(stoicRequest.getToneStyle());
        request.setTargetDuration(stoicRequest.getTargetDuration());
        request.setLanguage(stoicRequest.getLanguage());
        request.setGenerateAudio(stoicRequest.getGenerateAudio());
        request.setVoiceType(stoicRequest.getVoiceType());
        request.setGenerateShortVersion(stoicRequest.getGenerateShortVersion());
        request.setIncludeCallToAction(stoicRequest.getIncludeCallToAction());
        request.setOptimizeForSEO(stoicRequest.getOptimizeForSEO());

        // Adiciona informações específicas do estoicismo ao contexto adicional
        StringBuilder additionalContext = new StringBuilder();

        // Adiciona o nome do filósofo
        additionalContext.append("Filósofo Estoico: ").append(stoicRequest.getPhilosopherName()).append("\n\n");

        // Adiciona conceito estoico específico se fornecido
        if (stoicRequest.getStoicConcept() != null && !stoicRequest.getStoicConcept().isEmpty()) {
            additionalContext.append("Conceito Estoico: ").append(stoicRequest.getStoicConcept()).append("\n\n");
        }

        // Adiciona aplicação prática se fornecida
        if (stoicRequest.getPracticalApplication() != null && !stoicRequest.getPracticalApplication().isEmpty()) {
            additionalContext.append("Aplicação Prática: ").append(stoicRequest.getPracticalApplication()).append("\n\n");
        }

        // Adiciona qualquer contexto adicional fornecido
        if (stoicRequest.getAdditionalContext() != null && !stoicRequest.getAdditionalContext().isEmpty()) {
            additionalContext.append("Contexto Adicional: ").append(stoicRequest.getAdditionalContext());
        }

        request.setAdditionalContext(additionalContext.toString());

        // Se não tiver solicitado tipos de conteúdo específicos, adiciona os padrões para vídeos de 15 minutos
        if (request.getContentTypes() == null || request.getContentTypes().isEmpty()) {
            request.setContentTypes(List.of(
                    ContentType.TITLE,
                    ContentType.DESCRIPTION,
                    ContentType.TAGS,
                    ContentType.SCRIPT
            ));
        }

        // Se for um vídeo mais longo (30 min), assumir que é mais abrangente
        if (request.getTargetDuration() != null && request.getTargetDuration() >= 30) {
            // Se ainda não estiver incluído, adicionar THUMBNAIL_IDEA para vídeos longos
            if (!request.getContentTypes().contains(ContentType.THUMBNAIL_IDEA)) {
                request.getContentTypes().add(ContentType.THUMBNAIL_IDEA);
            }
        }

        return request;
    }
}
