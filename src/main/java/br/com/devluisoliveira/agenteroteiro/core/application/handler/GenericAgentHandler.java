package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.application.service.OpenAIService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PromptTemplateService;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenericAgentHandler implements AgentHandler {

    private final PromptTemplateService promptTemplateService;
    private final OpenAIService openAIService;

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.GENERIC;
    }

    @Override
    public String preparePrompt(Map<String, Object> request) {
        log.info("[GenericAgentHandler.preparePrompt] - Preparando prompt para geração de conteúdo genérico");

        try {
            // Converter o Map para ContentGenerationRequest para usar o PromptTemplateService
            String processId = getStringValue(request, "processId");
            String title = getStringValue(request, "title");
            String theme = getStringValue(request, "theme");
            String notes = getStringValue(request, "notes");
            String videoTopic = getStringValue(request, "videoTopic", theme); // Fallback para theme
            String targetAudience = getStringValue(request, "targetAudience");
            String toneStyle = getStringValue(request, "toneStyle");
            Integer targetDuration = getIntegerValue(request, "targetDuration", 10);
            String language = getStringValue(request, "language", "pt_BR");
            Boolean includeCallToAction = getBooleanValue(request, "includeCallToAction", true);
            Boolean optimizeForSEO = getBooleanValue(request, "optimizeForSEO", true);
            String additionalContext = getStringValue(request, "additionalContext");

            // Construir prompt personalizado
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("# Assistente Especializado em Geração de Conteúdo\n\n");
            promptBuilder.append("Você é um especialista em criar conteúdo para YouTube. Sua tarefa é gerar conteúdo de alta qualidade baseado nas seguintes informações:\n\n");

            // Informações básicas
            if (title != null && !title.isEmpty()) {
                promptBuilder.append("## TÍTULO\n").append(title).append("\n\n");
            }

            if (theme != null && !theme.isEmpty()) {
                promptBuilder.append("## TEMA\n").append(theme).append("\n\n");
            }

            if (videoTopic != null && !videoTopic.isEmpty()) {
                promptBuilder.append("## TÓPICO DO VÍDEO\n").append(videoTopic).append("\n\n");
            }

            if (notes != null && !notes.isEmpty()) {
                promptBuilder.append("## NOTAS\n").append(notes).append("\n\n");
            }

            if (targetAudience != null && !targetAudience.isEmpty()) {
                promptBuilder.append("## PÚBLICO-ALVO\n").append(targetAudience).append("\n\n");
            }

            if (toneStyle != null && !toneStyle.isEmpty()) {
                promptBuilder.append("## TOM/ESTILO\n").append(toneStyle).append("\n\n");
            }

            promptBuilder.append("## DURAÇÃO ALVO\n").append(targetDuration).append(" minutos\n\n");
            promptBuilder.append("## IDIOMA\n").append(language).append("\n\n");

            // Conteúdo adicional
            if (additionalContext != null && !additionalContext.isEmpty()) {
                promptBuilder.append("## CONTEXTO ADICIONAL\n").append(additionalContext).append("\n\n");
            }

            // Diretrizes
            promptBuilder.append("## DIRETRIZES\n");
            promptBuilder.append("- Crie conteúdo envolvente e adequado ao público-alvo\n");
            promptBuilder.append("- Estruture o conteúdo de forma lógica e fácil de seguir\n");

            if (optimizeForSEO) {
                promptBuilder.append("- Otimize o conteúdo para SEO\n");
            }

            if (includeCallToAction) {
                promptBuilder.append("- Inclua call-to-action relevantes\n");
            }

            promptBuilder.append("\n## ESTRUTURA DO CONTEÚDO\n");

            // Seções a gerar
            if (hasSection(request, ContentType.TITLE)) {
                promptBuilder.append("\n### TÍTULO DO VÍDEO\n");
                promptBuilder.append("Crie um título chamativo que gere curiosidade e seja otimizado para SEO.\n");
            }

            if (hasSection(request, ContentType.DESCRIPTION)) {
                promptBuilder.append("\n### DESCRIÇÃO DO VÍDEO\n");
                promptBuilder.append("Crie uma descrição completa com 1500-2000 caracteres, incluindo palavras-chave relevantes");
                if (includeCallToAction) {
                    promptBuilder.append(" e call-to-action");
                }
                promptBuilder.append(".\n");
            }

            if (hasSection(request, ContentType.TAGS)) {
                promptBuilder.append("\n### TAGS\n");
                promptBuilder.append("Liste 10-15 tags relevantes para o vídeo, separadas por vírgula.\n");
            }

            if (hasSection(request, ContentType.SCRIPT)) {
                promptBuilder.append("\n### ROTEIRO\n");
                promptBuilder.append("Escreva um roteiro completo para um vídeo de aproximadamente ")
                        .append(targetDuration)
                        .append(" minutos, com introdução cativante, desenvolvimento em tópicos claros e conclusão com resumo");

                if (includeCallToAction) {
                    promptBuilder.append(" e call-to-action");
                }
                promptBuilder.append(".\n");
            }

            if (hasSection(request, ContentType.THUMBNAIL_IDEA)) {
                promptBuilder.append("\n### IDEIA PARA THUMBNAIL\n");
                promptBuilder.append("Sugira 3 ideias diferentes para thumbnail, com elementos visuais e texto.\n");
            }

            if (hasSection(request, ContentType.AUDIO_SCRIPT)) {
                promptBuilder.append("\n### SCRIPT PARA ÁUDIO\n");
                promptBuilder.append("Escreva um script otimizado para narração em áudio, com linguagem mais conversacional e fácil de pronunciar.\n");
            }

            if (hasSection(request, ContentType.SHORTS_IDEA)) {
                promptBuilder.append("\n### VERSÃO CURTA\n");
                promptBuilder.append("Crie uma versão reduzida do conteúdo principal de 60-90 segundos, mantendo os pontos essenciais.\n");
            }

            log.info("[GenericAgentHandler.preparePrompt] - Prompt gerado com sucesso: {} caracteres", promptBuilder.length());
            return promptBuilder.toString();

        } catch (Exception e) {
            log.error("[GenericAgentHandler.preparePrompt] - Erro ao preparar prompt: {}", e.getMessage(), e);
            return "Houve um erro ao preparar o prompt. Por favor, gere conteúdo para YouTube baseado nas informações disponíveis.";
        }
    }

    @Override
    public ContentGenerationResponse processResponse(String aiResponse, Map<String, Object> request) {
        log.info("[GenericAgentHandler.processResponse] - Processando resposta para conteúdo genérico");

        if (aiResponse == null || aiResponse.isEmpty()) {
            log.error("[GenericAgentHandler.processResponse] - Resposta da IA está vazia ou nula");
            return createErrorResponse("Não foi possível gerar o conteúdo");
        }

        try {
            // Extrair o processId
            String processId = getStringValue(request, "processId");
            UUID contentId = processId != null ? UUID.fromString(processId) : UUID.randomUUID();

            // Extrair título
            String title = getStringValue(request, "title");

            // Mapear seções da resposta
            Map<ContentType, String> contentMap = extractContentSections(aiResponse);

            // Construir a resposta
            return ContentGenerationResponse.builder()
                    .processId(contentId)
                    .title(title != null ? title : extractTitle(aiResponse))
                    .agentType(AgentType.GENERIC)
                    .text(contentMap.getOrDefault(ContentType.SCRIPT, ""))
                    .textShort(contentMap.getOrDefault(ContentType.SHORTS_IDEA, ""))
                    .description(contentMap.getOrDefault(ContentType.DESCRIPTION, ""))
                    .tags(contentMap.getOrDefault(ContentType.TAGS, ""))
                    .generatedContent(contentMap)
                    .status("COMPLETED")
                    .message("Conteúdo gerado com sucesso")
                    .build();

        } catch (Exception e) {
            log.error("[GenericAgentHandler.processResponse] - Erro ao processar resposta: {}", e.getMessage(), e);
            return createErrorResponse("Erro ao processar o conteúdo gerado: " + e.getMessage());
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        return getStringValue(map, key, null);
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            return value != null ? value.toString() : defaultValue;
        }
        return defaultValue;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key, Integer defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    private Boolean getBooleanValue(Map<String, Object> map, String key, Boolean defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private boolean hasSection(Map<String, Object> request, ContentType contentType) {
        if (request.containsKey("contentTypes")) {
            Object contentTypesObj = request.get("contentTypes");
            if (contentTypesObj instanceof Iterable) {
                for (Object type : (Iterable<?>) contentTypesObj) {
                    if (contentType.equals(type) || contentType.name().equals(type.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Map<ContentType, String> extractContentSections(String aiResponse) {
        Map<ContentType, String> contentMap = new HashMap<>();

        // Mapeamento de cabeçalhos de seção para tipos de conteúdo
        Map<String, ContentType> sectionToType = new HashMap<>();
        sectionToType.put("TÍTULO DO VÍDEO", ContentType.TITLE);
        sectionToType.put("DESCRIÇÃO DO VÍDEO", ContentType.DESCRIPTION);
        sectionToType.put("TAGS", ContentType.TAGS);
        sectionToType.put("ROTEIRO", ContentType.SCRIPT);
        sectionToType.put("IDEIA PARA THUMBNAIL", ContentType.THUMBNAIL_IDEA);
        sectionToType.put("SCRIPT PARA ÁUDIO", ContentType.AUDIO_SCRIPT);
        sectionToType.put("VERSÃO CURTA", ContentType.SHORTS_IDEA);

        // Regex para encontrar seções no formato "### NOME DA SEÇÃO"
        Pattern sectionPattern = Pattern.compile("###\\s+([^\\n]+)([\\s\\S]*?)(?=###|$)");
        Matcher matcher = sectionPattern.matcher(aiResponse);

        // Extrair cada seção
        while (matcher.find()) {
            String sectionTitle = matcher.group(1).trim();
            String sectionContent = matcher.group(2).trim();

            // Mapear para o tipo de conteúdo correspondente
            for (Map.Entry<String, ContentType> entry : sectionToType.entrySet()) {
                if (sectionTitle.contains(entry.getKey())) {
                    contentMap.put(entry.getValue(), sectionContent);
                    break;
                }
            }
        }

        return contentMap;
    }

    private String extractTitle(String aiResponse) {
        // Tentar extrair o título da resposta
        Pattern titlePattern = Pattern.compile("###\\s+TÍTULO DO VÍDEO\\s*([^\\n]+)");
        Matcher matcher = titlePattern.matcher(aiResponse);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // Alternativa: buscar a primeira linha que pareça um título
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#") && line.length() < 100) {
                return line;
            }
        }

        return "Conteúdo para YouTube";
    }

    private ContentGenerationResponse createErrorResponse(String errorMessage) {
        return ContentGenerationResponse.builder()
                .processId(UUID.randomUUID())
                .status("ERROR")
                .message(errorMessage)
                .build();
    }
}
