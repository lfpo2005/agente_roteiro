package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.application.service.OpenAIService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.PrayerStyleService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PromptTemplateService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrayerAgentHandler implements AgentHandler {

    private final PrayerStyleService prayerStyleService;
    private final OpenAIService openAIService;
    private final PromptTemplateService promptTemplateService;

    private static final String PROMPT_TEMPLATE_PATH = "prompts/prompt_prayer_specialist.txt";

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.PRAYER;
    }

    @Override
    public String preparePrompt(Map<String, Object> request) {
        try {
            // Carregar o template base
            String baseTemplate = loadTemplateFile();

            // Personalizar o template com os dados da requisição
            return customizeTemplate(baseTemplate, request);
        } catch (Exception e) {
            log.error("Erro ao preparar prompt de oração: {}", e.getMessage(), e);
            return "Houve um erro ao preparar o prompt. Por favor, gere uma oração sobre o tema fornecido.";
        }
    }

    @Override
    public ContentGenerationResponse processResponse(String aiResponse, Map<String, Object> request) {
        log.info("Processando resposta para conteúdo de oração");

        if (aiResponse == null || aiResponse.isEmpty()) {
            log.error("Resposta da IA está vazia ou nula");
            return createErrorResponse("Não foi possível gerar o conteúdo de oração");
        }

        try {
            // Extrair o processId
            String processId = getStringValue(request, "processId");
            UUID contentId = processId != null ? UUID.fromString(processId) : UUID.randomUUID();

            // Extrair título e tema
            String title = getStringValue(request, "title");
            String theme = getStringValue(request, "theme");

            // Mapear seções da resposta
            Map<ContentType, String> contentMap = extractContentSections(aiResponse);

            // Obter o tipo e estilo de oração
            PrayerType prayerType = getPrayerType(request);
            PrayerStyle prayerStyle = getPrayerStyle(request);
            String prayerTypeDisplayName = prayerType != null ? prayerType.getDisplayName() : "";
            String prayerStyleDisplayName = prayerStyle != null ? prayerStyle.getDisplayName() : "";

            // Construir título se não foi fornecido
            if (title == null || title.isEmpty()) {
                title = contentMap.getOrDefault(ContentType.TITLE, null);
                if (title == null || title.isEmpty()) {
                    title = "Oração " + prayerTypeDisplayName + " - " + theme;
                }
            }

            // Construir a resposta
            return ContentGenerationResponse.builder()
                    .processId(contentId)
                    .title(title)
                    .agentType(AgentType.PRAYER)
                    .text(contentMap.getOrDefault(ContentType.SCRIPT, ""))
                    .textShort(contentMap.getOrDefault(ContentType.SHORTS_IDEA, ""))
                    .description(contentMap.getOrDefault(ContentType.DESCRIPTION, ""))
                    .tags(contentMap.getOrDefault(ContentType.TAGS, ""))
                    .generatedContent(contentMap)
                    .status("COMPLETED")
                    .message("Oração gerada com sucesso no estilo " + prayerStyleDisplayName)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao processar resposta: {}", e.getMessage(), e);
            return createErrorResponse("Erro ao processar o conteúdo gerado: " + e.getMessage());
        }
    }

    private String loadTemplateFile() throws IOException {
        ClassPathResource resource = new ClassPathResource(PROMPT_TEMPLATE_PATH);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    private String customizeTemplate(String template, Map<String, Object> request) {
        // Clone o template para evitar alterações no original
        String customizedTemplate = template;

        // Obter valores básicos
        String processId = getStringValue(request, "processId");
        String title = getStringValue(request, "title");
        String theme = getStringValue(request, "theme");
        String notes = getStringValue(request, "notes");
        String targetDuration = request.containsKey("targetDuration") ? String.valueOf(request.get("targetDuration")) : "5";
        String language = getStringValue(request, "language", "pt_BR");

        // Obter valores específicos da oração
        PrayerType prayerType = getPrayerType(request);
        PrayerStyle prayerStyle = getPrayerStyle(request);
        String biblePassage = getStringValue(request, "biblePassage");
        String additionalContext = getStringValue(request, "additionalContext");

        // Substituir os placeholders básicos
        customizedTemplate = customizedTemplate.replace("{processId}", nullSafe(processId))
                .replace("{title}", nullSafe(title))
                .replace("{theme}", nullSafe(theme))
                .replace("{notes}", nullSafe(notes))
                .replace("{prayerType}", prayerType != null ? prayerType.getDisplayName() : "")
                .replace("{prayerStyle}", prayerStyle != null ? prayerStyle.getDisplayName() : "")
                .replace("{targetDuration}", nullSafe(targetDuration))
                .replace("{language}", nullSafe(language))
                .replace("{biblePassage}", nullSafe(biblePassage));

        // Obter e substituir as características do estilo e tipo de oração
        String prayerStyleChars = request.containsKey("prayerStyleCharacteristics")
                ? (String) request.get("prayerStyleCharacteristics")
                : prayerStyleService.getCombinedPrayerCharacteristics(prayerStyle, prayerType);

        customizedTemplate = customizedTemplate.replace("{prayerStyleCharacteristics}", prayerStyleChars);

        // Substituir contexto adicional
        customizedTemplate = customizedTemplate.replace("{additionalContext}", additionalContext);

        // Formatar tipos de conteúdo solicitados
        List<ContentType> contentTypes = getContentTypes(request);
        String contentTypesFormatted = contentTypes.stream()
                .map(ContentType::getLabel)
                .collect(Collectors.joining("\n- "));
        if (!contentTypesFormatted.isEmpty()) {
            contentTypesFormatted = "- " + contentTypesFormatted;
        }
        customizedTemplate = customizedTemplate.replace("{contentTypesFormatted}", contentTypesFormatted);

        // Configurar seções condicionais
        customizedTemplate = customizedTemplate.replace("{titleSection}",
                shouldIncludeSection(contentTypes, ContentType.TITLE) ? "### TÍTULO DA ORAÇÃO" : "");

        customizedTemplate = customizedTemplate.replace("{descriptionSection}",
                shouldIncludeSection(contentTypes, ContentType.DESCRIPTION) ?
                        "### DESCRIÇÃO\n[Descrição otimizada para plataformas de vídeo com 500-1000 caracteres]" : "");

        customizedTemplate = customizedTemplate.replace("{tagsSection}",
                shouldIncludeSection(contentTypes, ContentType.TAGS) ?
                        "### TAGS\n[5-10 hashtags relevantes para a oração]" : "");

        customizedTemplate = customizedTemplate.replace("{scriptSection}",
                shouldIncludeSection(contentTypes, ContentType.SCRIPT) ?
                        "### ORAÇÃO COMPLETA\n[Texto completo da oração seguindo a estrutura indicada]" : "");

        customizedTemplate = customizedTemplate.replace("{thumbnailSection}",
                shouldIncludeSection(contentTypes, ContentType.THUMBNAIL_IDEA) ?
                        "### IDEIA PARA THUMBNAIL\n[3 ideias para thumbnail com elementos visuais e texto]" : "");

        customizedTemplate = customizedTemplate.replace("{audioScriptSection}",
                shouldIncludeSection(contentTypes, ContentType.AUDIO_SCRIPT) ?
                        "### SCRIPT PARA ÁUDIO\n[Versão da oração otimizada para narração em áudio]" : "");

        customizedTemplate = customizedTemplate.replace("{shortVersionSection}",
                shouldIncludeSection(contentTypes, ContentType.SHORTS_IDEA) ?
                        "### VERSÃO CURTA\n[Versão curta da oração com 300-500 caracteres para vídeos breves]" : "");

        // Logar tamanho do prompt para monitoramento de tokens
        log.debug("[PrayerAgentHandler.customizeTemplate] - Prompt final com {} caracteres", customizedTemplate.length());

        return customizedTemplate;
    }

    private PrayerType getPrayerType(Map<String, Object> request) {
        Object typeObj = request.get("prayerType");
        if (typeObj instanceof PrayerType) {
            return (PrayerType) typeObj;
        }
        return null;
    }

    private PrayerStyle getPrayerStyle(Map<String, Object> request) {
        Object styleObj = request.get("prayerStyle");
        if (styleObj instanceof PrayerStyle) {
            return (PrayerStyle) styleObj;
        }
        return null;
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

    @SuppressWarnings("unchecked")
    private List<ContentType> getContentTypes(Map<String, Object> request) {
        if (request.containsKey("contentTypes")) {
            Object contentTypesObj = request.get("contentTypes");
            if (contentTypesObj instanceof List) {
                return (List<ContentType>) contentTypesObj;
            }
        }
        // Retornar tipos padrão se não encontrar
        return List.of(ContentType.TITLE, ContentType.DESCRIPTION, ContentType.SCRIPT, ContentType.TAGS);
    }

    private boolean shouldIncludeSection(List<ContentType> contentTypes, ContentType type) {
        return contentTypes.contains(type);
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private ContentGenerationResponse createErrorResponse(String errorMessage) {
        return ContentGenerationResponse.builder()
                .processId(UUID.randomUUID())
                .status("ERROR")
                .message(errorMessage)
                .build();
    }

    private Map<ContentType, String> extractContentSections(String aiResponse) {
        Map<ContentType, String> contentMap = new HashMap<>();

        // Mapeamento de cabeçalhos de seção para tipos de conteúdo
        Map<String, ContentType> sectionToType = new HashMap<>();
        sectionToType.put("TÍTULO DA ORAÇÃO", ContentType.TITLE);
        sectionToType.put("DESCRIÇÃO", ContentType.DESCRIPTION);
        sectionToType.put("TAGS", ContentType.TAGS);
        sectionToType.put("ORAÇÃO COMPLETA", ContentType.SCRIPT);
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
}