package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.handler.AgentHandler;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentGenerationService {

    // Injetar outros serviços necessários
    private final PromptTemplateService promptTemplateService;
    private final OpenAIService openAIService;

    // Opcionalmente, você pode adicionar esses serviços quando implementá-los
    // private final ElevenLabsService audioGenerationService;
    // private final AnthropicService anthropicService;
    // private final MistralService mistralService;

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
    }

    /**
     * Inicia o processo de geração de conteúdo usando a estratégia de handlers
     */
    public ContentGenerationResponse startGeneration(Map<String, Object> requestMap) {
        log.info("[AgentGenerationService.startGeneration] - Iniciando processo de geração de conteúdo");

        try {
            // 1. Extrair o tipo de agente e selecionar o handler apropriado
            AgentType agentType = extractAgentType(requestMap);
            AgentHandler handler = handlers.getOrDefault(agentType, handlers.get(AgentType.GENERIC));

            if (handler == null) {
                log.error("[AgentGenerationService.startGeneration] - Nenhum handler encontrado para o tipo: {}", agentType);
                return createErrorResponse("Tipo de agente não suportado: " + agentType);
            }

            log.info("[AgentGenerationService.startGeneration] - Usando handler: {}", handler.getClass().getSimpleName());

            // 2. Preparar o prompt usando o handler específico
            String prompt = handler.preparePrompt(requestMap);

            // 3. Chamar a API de IA para gerar conteúdo
            String aiResponse = generateContent(prompt, requestMap);

            if (aiResponse == null || aiResponse.isEmpty()) {
                log.error("[AgentGenerationService.startGeneration] - Resposta vazia da IA");
                return createErrorResponse("Não foi possível gerar o conteúdo");
            }

            log.info("[AgentGenerationService.startGeneration] - Conteúdo gerado com sucesso, processando resposta");

            // 4. Processar a resposta usando o handler específico
            return handler.processResponse(aiResponse, requestMap);

        } catch (Exception e) {
            log.error("[AgentGenerationService.startGeneration] - Erro ao gerar conteúdo: {}", e.getMessage(), e);
            return createErrorResponse("Erro ao gerar conteúdo: " + e.getMessage());
        }
    }

    /**
     * Extrai o tipo de agente do mapa de requisição
     */
    private AgentType extractAgentType(Map<String, Object> requestMap) {
        Object agentTypeObj = requestMap.get("agentType");

        if (agentTypeObj instanceof AgentType) {
            return (AgentType) agentTypeObj;
        } else if (agentTypeObj instanceof String) {
            try {
                return AgentType.valueOf((String) agentTypeObj);
            } catch (IllegalArgumentException e) {
                log.warn("[AgentGenerationService.extractAgentType] - Tipo de agente inválido: {}", agentTypeObj);
            }
        }

        return AgentType.GENERIC;
    }

    /**
     * Gera conteúdo chamando o provedor de IA apropriado
     */
    private String generateContent(String prompt, Map<String, Object> requestMap) {
        log.info("[AgentGenerationService.generateContent] - Gerando conteúdo com prompt de {} caracteres", prompt.length());

        try {
            // Chama o provedor principal de IA (OpenAI)
            return openAIService.generateOracao(prompt);
        } catch (Exception e) {
            log.error("[AgentGenerationService.generateContent] - Erro ao chamar OpenAI: {}", e.getMessage(), e);

            // Implemente lógica de fallback para outros provedores aqui quando disponíveis
            // Por exemplo:
            // try {
            //     return anthropicService.generateContent(prompt);
            // } catch (Exception e2) {
            //     log.error("Erro ao chamar Anthropic: {}", e2.getMessage());
            //     return mistralService.generateContent(prompt);
            // }

            throw new RuntimeException("Falha ao gerar conteúdo: " + e.getMessage(), e);
        }
    }

    /**
     * Método alternativo para gerar conteúdo com uma abordagem detalhada por tipo de conteúdo
     */
    public ContentGenerationResponse generateDetailedContent(ContentGenerationRequest request) {
        log.info("[AgentGenerationService.generateDetailedContent] - Iniciando geração detalhada para processo: {}",
                request.getProcessId());

        try {
            // Carregar o template base para o tipo de agente
            String basePrompt = promptTemplateService.loadPromptTemplate(request);

            // Gerar conteúdo para cada tipo solicitado
            Map<ContentType, String> generatedContent = new HashMap<>();

            for (ContentType contentType : request.getContentTypes()) {
                // Personalizar o prompt base para o tipo de conteúdo específico
                String customizedPrompt = customizePromptForContentType(basePrompt, contentType, request);

                // Chamar o serviço de IA para gerar o conteúdo
                String content = openAIService.generateOracao(customizedPrompt);

                // Armazenar o resultado
                generatedContent.put(contentType, content);

                log.info("[AgentGenerationService.generateDetailedContent] - Conteúdo do tipo {} gerado com sucesso", contentType);
            }

            // Gerar áudio se solicitado
            String audioUrl = null;
            if (Boolean.TRUE.equals(request.getGenerateAudio()) && generatedContent.containsKey(ContentType.AUDIO_SCRIPT)) {
                // Quando ElevenLabsService estiver disponível
                // audioUrl = audioGenerationService.generateSpeech(generatedContent.get(ContentType.AUDIO_SCRIPT));
                log.info("[AgentGenerationService.generateDetailedContent] - Geração de áudio solicitada mas não implementada");
            }

            // Montar a resposta
            ContentGenerationResponse response = ContentGenerationResponse.builder()
                    .processId(UUID.fromString(request.getProcessId()))
                    .title(request.getTitle())
                    .agentType(request.getAgentType())
                    .text(generatedContent.getOrDefault(ContentType.SCRIPT, ""))
                    .description(generatedContent.getOrDefault(ContentType.DESCRIPTION, ""))
                    .tags(generatedContent.getOrDefault(ContentType.TAGS, ""))
                    .textShort(generatedContent.getOrDefault(ContentType.SHORTS_IDEA, ""))
                    .audio(audioUrl)
                    .generatedContent(generatedContent)
                    .status("COMPLETED")
                    .message("Conteúdo gerado com sucesso")
                    .build();

            log.info("[AgentGenerationService.generateDetailedContent] - Processo completo com sucesso");
            return response;

        } catch (Exception e) {
            log.error("[AgentGenerationService.generateDetailedContent] - Erro: {}", e.getMessage(), e);
            return createErrorResponse("Erro ao gerar conteúdo detalhado: " + e.getMessage());
        }
    }

    /**
     * Personaliza o prompt de acordo com o tipo de conteúdo
     */
    private String customizePromptForContentType(String basePrompt, ContentType contentType, ContentGenerationRequest request) {
        StringBuilder customizedPrompt = new StringBuilder(basePrompt);

        customizedPrompt.append("\n\nVocê está gerando ").append(contentType.getLabel());

        if (request.getVideoTopic() != null) {
            customizedPrompt.append(" para um vídeo sobre: ").append(request.getVideoTopic());
        } else if (request.getTheme() != null) {
            customizedPrompt.append(" para um vídeo sobre: ").append(request.getTheme());
        }

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
                if (Boolean.TRUE.equals(request.getIncludeCallToAction())) {
                    customizedPrompt.append(" e termine com uma call-to-action.");
                }
                break;
            case SCRIPT:
                customizedPrompt.append("\n\nEscreva um roteiro completo para um vídeo de aproximadamente ")
                        .append(request.getTargetDuration()).append(" minutos.");
                break;
            case TAGS:
                customizedPrompt.append("\n\nGere 10-15 tags relevantes para o vídeo, separadas por vírgulas, começando das mais específicas para as mais gerais.");
                break;
            case THUMBNAIL_IDEA:
                customizedPrompt.append("\n\nSugira 3 ideias de thumbnail que sejam visualmente atraentes e representem bem o conteúdo do vídeo.");
                break;
            case AUDIO_SCRIPT:
                customizedPrompt.append("\n\nEscreva um script otimizado para narração em áudio, com linguagem mais conversacional e fácil de pronunciar.");
                break;
            case SHORTS_IDEA:
                customizedPrompt.append("\n\nCrie uma versão curta de 60-90 segundos do conteúdo principal, mantendo os pontos mais importantes.");
                break;
            default:
                customizedPrompt.append("\n\nGere o conteúdo solicitado com base nas informações fornecidas.");
        }

        if (request.getAdditionalContext() != null) {
            customizedPrompt.append("\n\nInformações adicionais: ").append(request.getAdditionalContext());
        }

        return customizedPrompt.toString();
    }

    /**
     * Cria uma resposta de erro
     */
    private ContentGenerationResponse createErrorResponse(String errorMessage) {
        return ContentGenerationResponse.builder()
                .processId(UUID.randomUUID())
                .status("ERROR")
                .message(errorMessage)
                .build();
    }
}