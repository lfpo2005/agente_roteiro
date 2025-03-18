package br.com.devluisoliveira.agenteroteiro.core.application.mapper;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class GenericGeneraMapper {


    public ContentGeneration toEntity(ContentGenerationResponse response) {
        ContentGeneration contentGeneration = new ContentGeneration();
        contentGeneration.setContentId(response.getProcessId());
        contentGeneration.setTitle(response.getTitle());
        contentGeneration.setAgentType(response.getAgentType());
        contentGeneration.setText(response.getText());
        contentGeneration.setTextShort(response.getTextShort());
        contentGeneration.setDescription(response.getDescription());
        contentGeneration.setAudio(response.getAudio());
        contentGeneration.setTags(response.getTags());
        contentGeneration.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return contentGeneration;
    }

    public ContentGenerationResponse toDto(ContentGeneration savedContentGeneration) {
        return ContentGenerationResponse.builder()
                .processId(savedContentGeneration.getContentId())
                .title(savedContentGeneration.getTitle())
                .agentType(savedContentGeneration.getAgentType())
                .text(savedContentGeneration.getText())
                .textShort(savedContentGeneration.getTextShort())
                .description(savedContentGeneration.getDescription())
                .audio(savedContentGeneration.getAudio())
                .tags(savedContentGeneration.getTags())
                .build();
    }
}
