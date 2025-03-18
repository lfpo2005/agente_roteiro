package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentGenerationService {

    // Injetar outros serviços necessários
    private final PromptTemplateService promptTemplateService;

// VOU IMPLEMENTAR AQUI AINDA ESTOU REFINANDO O PROCESSO NÃO PRECIA IMPLEMENTAR AINDA

//    public Object startGeneration(ContentGenerationRequest request) {
//
//        // Gerar conteúdo para cada tipo solicitado
//        Map<ContentType, String> generatedContent = new HashMap<>();
//
//        for (ContentType contentType : request.getContentTypes()) {
//            // Personalizar o prompt base para o tipo de conteúdo específico
//            String customizedPrompt = customizePromptForContentType(basePrompt, contentType, request);
//
//            // Chamar o serviço de IA para gerar o conteúdo
//            String content = aiProviderService.generateContent(customizedPrompt);
//
//            // Armazenar o resultado
//            generatedContent.put(contentType, content);
//
//            log.info("Conteúdo do tipo {} gerado com sucesso para processo {}",
//                    contentType, request.getProcessId());
//        }
//
//        // Gerar áudio se solicitado
//        String audioUrl = null;
//        if (request.getGenerateAudio() && generatedContent.containsKey(ContentType.AUDIO_SCRIPT)) {
//            audioUrl = audioGenerationService.generateAudio(
//                    generatedContent.get(ContentType.AUDIO_SCRIPT),
//                    request.getVoiceType(),
//                    request.getLanguage()
//            );
//        }
//
//        // Montar e retornar a resposta
//        ContentGenerationResponse response = new ContentGenerationResponse();
//        response.setProcessId(request.getProcessId());
//        response.setGeneratedContent(generatedContent);
//        response.setAudioUrl(audioUrl);
//
//        return response;
 //   }

    private String customizePromptForContentType(String basePrompt, ContentType contentType, ContentGenerationRequest request) {
        // Personalizar o prompt de acordo com o tipo de conteúdo
        StringBuilder customizedPrompt = new StringBuilder(basePrompt);

        customizedPrompt.append("\n\nVocê está gerando ").append(contentType.getLabel())
                .append(" para um vídeo sobre: ").append(request.getVideoTopic());

        if (request.getTargetAudience() != null) {
            customizedPrompt.append("\nPúblico-alvo: ").append(request.getTargetAudience());
        }

        if (request.getToneStyle() != null) {
            customizedPrompt.append("\nTom/estilo: ").append(request.getToneStyle());
        }

        // Adicionar instruções específicas para cada tipo de conteúdo
        switch (contentType) {
            case TITLE:
                customizedPrompt.append("\n\nCrie um título chamativo que gere curiosidade e seja otimizado para SEO.");
                break;
            case DESCRIPTION:
                customizedPrompt.append("\n\nCrie uma descrição completa que explique o vídeo, inclua palavras-chave relevantes");
                if (request.getIncludeCallToAction()) {
                    customizedPrompt.append(" e termine com uma call-to-action.");
                }
                break;
            case SCRIPT:
                customizedPrompt.append("\n\nEscreva um roteiro completo para um vídeo de aproximadamente ")
                        .append(request.getTargetDuration()).append(" minutos.");
                break;
            default:
                customizedPrompt.append("\n\nGere o conteúdo solicitado com base nas informações fornecidas.");
        }

        if (request.getAdditionalContext() != null) {
            customizedPrompt.append("\n\nInformações adicionais: ").append(request.getAdditionalContext());
        }

        return customizedPrompt.toString();
    }
}