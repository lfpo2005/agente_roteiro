package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.handler.AgentHandler;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentGenerationService {

    // Injetar outros serviços necessários
    private final PromptTemplateService promptTemplateService;


    private final OpenAIService openAIService;
//    private final AnthropicService anthropicService;
//    private final MistralService mistralService;
    // Outros serviços necessários

    // Registro de handlers para diferentes tipos de agentes
    private final Map<AgentType, AgentHandler> handlers;

    // Construtor para injetar automaticamente todos os handlers
    public AgentGenerationService(List<AgentHandler> handlerList,
                                  PromptTemplateService promptTemplateService,
                                  OpenAIService openAIService /* outros serviços */) {
        this.promptTemplateService = promptTemplateService;
        this.openAIService = openAIService;
        this.handlers = new HashMap<>();
        handlerList.forEach(handler ->
                handlers.put(handler.getSupportedAgentType(), handler));
        // Inicializar outros serviços
    }

    public ContentGenerationResponse startGeneration(Map<String, Object> request) {
        log.info("[AgentGenerationService.startGeneration] - Iniciando processo de geração de conteúdo");
        // 1. Selecionar o handler apropriado
        AgentType agentType = (AgentType) request.getOrDefault("agentType", AgentType.GENERIC);
        AgentHandler handler = handlers.getOrDefault(agentType, handlers.get(AgentType.GENERIC));

        // 2. Preparar o prompt usando o handler específico
        String prompt = handler.preparePrompt(request);

        // 3. Selecionar qual API de IA usar (pode ter lógica de failover)
        // String aiResponse = callAiService(prompt, request);

        // 4. Processar a resposta com o handler específico
        return handler.processResponse(null, request);
    }

//    private String callAiService(String prompt, ContentGenerationRequest request) {
//        // Lógica para selecionar qual API usar com base em disponibilidade,
//        // custo, capacidades específicas ou preferências do usuário
//        try {
//            // Primeiro tente o serviço principal
//            return openAIService.generateContent(prompt);
//        } catch (Exception e) {
//            log.warn("Erro ao chamar OpenAI, tentando serviço alternativo: {}", e.getMessage());
//            try {
//                // Failover para segunda opção
//                return anthropicService.generateContent(prompt);
//            } catch (Exception e2) {
//                log.warn("Erro ao chamar Anthropic, tentando serviço final: {}", e2.getMessage());
//                // Última opção
//                return mistralService.generateContent(prompt);
//            }
//        }
//    }

// VOU IMPLEMENTAR AQUI AINDA ESTOU REFINANDO O PROCESSO NÃO PRECIA IMPLEMENTAR AINDA

    /*public Object startGeneration(ContentGenerationRequest request) {

        // Gerar conteúdo para cada tipo solicitado
        Map<ContentType, String> generatedContent = new HashMap<>();

        for (ContentType contentType : request.getContentTypes()) {
            // Personalizar o prompt base para o tipo de conteúdo específico
            String customizedPrompt = customizePromptForContentType(basePrompt, contentType, request);

            // Chamar o serviço de IA para gerar o conteúdo
            String content = aiProviderService.generateContent(customizedPrompt);

            // Armazenar o resultado
            generatedContent.put(contentType, content);

            log.info("Conteúdo do tipo {} gerado com sucesso para processo {}",
                    contentType, request.getProcessId());
        }

        // Gerar áudio se solicitado
        String audioUrl = null;
        if (request.getGenerateAudio() && generatedContent.containsKey(ContentType.AUDIO_SCRIPT)) {
            audioUrl = audioGenerationService.generateAudio(
                    generatedContent.get(ContentType.AUDIO_SCRIPT),
                    request.getVoiceType(),
                    request.getLanguage()
            );
        }

        // Montar e retornar a resposta
        ContentGenerationResponse response = new ContentGenerationResponse();
        response.setProcessId(request.getProcessId());
        response.setGeneratedContent(generatedContent);
        response.setAudioUrl(audioUrl);

        return response;
    }*/

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
