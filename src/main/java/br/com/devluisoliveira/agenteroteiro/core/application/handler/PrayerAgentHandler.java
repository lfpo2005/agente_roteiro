package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.application.service.OpenAIService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.PrayerStyleService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PromptTemplateService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.*;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
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

import static br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrayerAgentHandler implements AgentHandler {

    private final PrayerStyleService prayerStyleService;

    private static final String PROMPT_TEMPLATE_PATH = "prompts/prompt_prayer_specialist.txt";

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.PRAYER;
    }

    @Override
    public String preparePrompt(Map<String, Object> request) {
        try {
            log.info("[PrayerAgentHandler.preparePrompt] - Preparando prompt de oração");
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
            Map<ContentType, String> contentMap = extractContentSections(aiResponse); //Todo: Verificar se o método extractContentSections está correto

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
        log.info("[PrayerAgentHandler.customizeTemplate] - Personalizando template de oração");

        String customizedTemplate = template;


        String processId = getStringValue(request, "processId");
        String title = getStringValue(request, "title");
        String theme = getStringValue(request, "theme");
        String notes = getStringValue(request, "notes");
        String bibleVersion = getStringValue(request, "bibleVersion");
        String language = getStringValue(request, "language", "pt_BR");
        boolean isShort = request.containsKey("shortVideo") ? (Boolean) request.get("shortVideo") : false;
        boolean generateAudio = request.containsKey("generateAudio") ? (Boolean) request.get("generateAudio") : false;

        String targetAudience = getStringValue(request, "targetAudience");
        String personalizationName = getStringValue(request, "personalizationName");

        String targetDurationStr = getStringValue(request, "targetDuration");
        int targetDurationMinutes = 0;

        if (targetDurationStr != null && !targetDurationStr.isEmpty()) {
            try {
                int targetDurationSeconds = Integer.parseInt(targetDurationStr);
                targetDurationMinutes = targetDurationSeconds / 60;
            } catch (NumberFormatException e) {
                log.error("Erro ao converter targetDurationStr para inteiro: {}", e.getMessage());
            }
        }

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
                .replace("{bibleVersion}", nullSafe(bibleVersion).isEmpty() ? "NVI" : bibleVersion)
                .replace("{prayerType}", prayerType != null ? prayerType.getDisplayName() : "")
                .replace("{prayerStyle}", prayerStyle != null ? prayerStyle.getDisplayName() : "")
                .replace("{targetDuration}", nullSafe(String.valueOf(targetDurationMinutes)))
                .replace("{language}", nullSafe(language))
                .replace("{biblePassage}", nullSafe(biblePassage).isEmpty() ? "o que fizer sentido em relação ao tema" : biblePassage)
                .replace("{shortVideo}", isShort ? "Sim" : "Não")
                .replace("{generateAudio}", generateAudio ? "Sim" : "Não")
                .replace("{targetAudience}", (targetAudience == null || targetAudience.isEmpty()) ? "Cristãos" : targetAudience)
                .replace("{personalizationName}", nullSafe(personalizationName));

        // Obter e substituir as características do estilo e tipo de oração
        String prayerStyleChars = request.containsKey("prayerStyleCharacteristics")
                ? (String) request.get("prayerStyleCharacteristics")
                : prayerStyleService.getCombinedPrayerCharacteristics(prayerStyle, prayerType);
        customizedTemplate = customizedTemplate.replace("{prayerStyleCharacteristics}", prayerStyleChars);

        // Substituir contexto adicional
        customizedTemplate = customizedTemplate.replace("{additionalContext}", nullSafe(additionalContext));

        // Formatar tipos de conteúdo solicitados
        List<ContentType> contentTypes = getContentTypes(request);
        if (contentTypes.isEmpty()) {
            contentTypes = List.of(ContentType.TITLE, ContentType.DESCRIPTION, ContentType.SCRIPT, ContentType.TAGS);
        }
        String contentTypesFormatted = contentTypes.stream()
                .map(ContentType::getLabel)
                .collect(Collectors.joining("\n- "));
        if (!contentTypesFormatted.isEmpty()) {
            contentTypesFormatted = "- " + contentTypesFormatted;
        }
        customizedTemplate = customizedTemplate.replace("{contentTypesFormatted}", contentTypesFormatted);

        // Configurar seções do formato de saída
        StringBuilder formatOutput = new StringBuilder();
        if (shouldIncludeSection(contentTypes, ContentType.TITLE)) {
            formatOutput.append("### Título do Vídeo\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.DESCRIPTION)) {
            formatOutput.append("### Descrição do Vídeo\n[Descrição otimizada para plataformas de vídeo com 500-1000 caracteres]\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.TAGS)) {
            formatOutput.append("### Tags\n[5-10 hashtags relevantes para a oração]\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.SCRIPT)) {
            formatOutput.append("### Roteiro\n[Texto completo da oração seguindo a estrutura indicada]\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.THUMBNAIL_IDEA)) {
            formatOutput.append("### Imagem em destaque (Thumbnail)\n[3 ideias para thumbnail com elementos visuais e texto]\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.AUDIO_SCRIPT)) {
            formatOutput.append("### Script para Áudio\n[Versão da oração otimizada para narração em áudio]\n\n");
        }
        if (shouldIncludeSection(contentTypes, ContentType.SHORTS_IDEA)) {
            formatOutput.append("### Versão Curta\n[Versão curta da oração com 300-500 caracteres para vídeos breves]\n\n");
        }
        customizedTemplate = customizedTemplate.replace("{formatOutput}", formatOutput.toString());

        String durationInstructions = "";
        String durationTypeString = targetDurationStr;


        DurationType durationType = null;
        try {
            durationType = DurationType.valueOf(durationTypeString);
            log.info("[PrayerAgentHandler.customizeTemplate] - Duração extraída do enum: {}", durationType);
        } catch (IllegalArgumentException e) {
            try {
                int seconds = Integer.parseInt(durationTypeString);
                log.info("[PrayerAgentHandler.customizeTemplate] - Tentando encontrar enum por segundos: {}", seconds);

                for (DurationType type : DurationType.values()) {
                    if (type.getDurationInSeconds() == seconds) {
                        durationType = type;
                        break;
                    }
                }

                if (durationType == null) {
                    log.warn("[PrayerAgentHandler.customizeTemplate] - Não foi encontrado enum para {} segundos. Usando valor padrão.", seconds);
                    // Valor padrão se não encontrar correspondência
                    durationType = DurationType.MINUTES_5;
                }
            } catch (NumberFormatException numberEx) {
                log.warn("[PrayerAgentHandler.customizeTemplate] - Valor '{}' não é um nome de enum válido nem um número. Usando valor padrão.", durationTypeString);
                // Se nem for um número, use o valor padrão
                durationType = DurationType.MINUTES_5;
            }
        }
        String importantNote = " **IMPORTATE** ESSE VALOR É SÓ DA ORAÇÃO";

        log.info("[PrayerAgentHandler.customizeTemplate] - Duração extraída: {}", durationType);
        if (durationType != null) {
            switch (durationType) {
                case SECONDS_30:
                    durationInstructions = "Tamanho da oração: 300-500 caracteres (30 segundos)" + importantNote;
                    break;
                case SECONDS_60:
                    durationInstructions = "Tamanho da oração: 500-800 caracteres (1 minuto)" + importantNote;
                    break;
                case MINUTES_3:
                    durationInstructions = "Tamanho da oração: 800-1.200 caracteres (3 minutos)" + importantNote;
                    break;
                case MINUTES_5:
                    durationInstructions = "Tamanho da oração: 1.800-2.200 caracteres (5 minutos)" + importantNote;
                    break;
                case MINUTES_10:
                    durationInstructions = "Tamanho da oração: 3.500-4.000 caracteres (10 minutos)" + importantNote;
                    break;
                case MINUTES_15:
                    durationInstructions = "Tamanho da oração: 3.500-4.000 caracteres (15 minutos)" + importantNote;
                    break;
                case MINUTES_20:
                    durationInstructions = "Tamanho da oração: 6.000-7.000 caracteres (20 minutos)" + importantNote;
                    break;
                case MINUTES_25:
                    durationInstructions = "Tamanho da oração: 8.000-9.000 caracteres (25 minutos)" + importantNote;
                    break;
                case MINUTES_30:
                    durationInstructions = "Tamanho da oração: 9.000-10.000 caracteres (30 minutos)" + importantNote;
                    break;
                default:
                    durationInstructions = "Tamanho da oração: 5.000-6.000 caracteres (15-30 minutos)" + importantNote;
                    break;
            }
        } else {
            durationInstructions = "Tamanho da oração: 1.800-2.200 caracteres (5 minutos)" + importantNote;
        }
        // Você pode optar por inserir essas instruções em um placeholder específico no template,
        // ou simplesmente anexá-las ao final.
        customizedTemplate += "\n\n" + durationInstructions;

        customizedTemplate = customizedTemplate.replace("{processId}", nullSafe(processId))
                .replace("{durationInstructions}", durationInstructions);

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