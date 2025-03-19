package br.com.devluisoliveira.agenteroteiro.core.application.handler;

import br.com.devluisoliveira.agenteroteiro.core.application.service.OpenAIService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PhilosopherStyleService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PromptTemplateService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
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
public class StoicAgentHandler implements AgentHandler {

    private final PhilosopherStyleService philosopherStyleService;
    private final OpenAIService openAIService;
    private final PromptTemplateService promptTemplateService;

    private static final String PROMPT_TEMPLATE_PATH = "prompts/prompt_estoicism_specialist.txt";

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.STOICISM;
    }

    @Override
    public String preparePrompt(Map<String, Object> request) {
        try {
            // Carregar o template base
            String baseTemplate = loadTemplateFile();

            // Personalizar o template com os dados da requisição
            return customizeTemplate(baseTemplate, request);
        } catch (Exception e) {
            log.error("Erro ao preparar prompt estoico: {}", e.getMessage(), e);
            return "Houve um erro ao preparar o prompt estoico. Por favor, gere conteúdo sobre filosofia estoica.";
        }
    }

    @Override
    public ContentGenerationResponse processResponse(String aiResponse, Map<String, Object> request) {
        log.info("Processando resposta para conteúdo estoico");

        if (aiResponse == null || aiResponse.isEmpty()) {
            log.error("Resposta da IA está vazia ou nula");
            return createErrorResponse("Não foi possível gerar o conteúdo estoico");
        }

        try {
            // Extrair o processId
            String processId = getStringValue(request, "processId");
            UUID contentId = processId != null ? UUID.fromString(processId) : UUID.randomUUID();

            // Extrair título
            String title = getStringValue(request, "title");

            // Extrair filósofo
            String philosopherName = getStringValue(request, "philosopherName");

            // Mapear seções da resposta
            Map<ContentType, String> contentMap = extractContentSections(aiResponse);

            // Construir a resposta
            return ContentGenerationResponse.builder()
                    .processId(contentId)
                    .title(title != null ? title : extractTitle(aiResponse))
                    .agentType(AgentType.STOICISM)
                    .text(contentMap.getOrDefault(ContentType.SCRIPT, ""))
                    .textShort(contentMap.getOrDefault(ContentType.SHORTS_IDEA, ""))
                    .description(contentMap.getOrDefault(ContentType.DESCRIPTION, ""))
                    .tags(contentMap.getOrDefault(ContentType.TAGS, ""))
                    .generatedContent(contentMap)
                    .status("COMPLETED")
                    .message("Conteúdo estoico gerado com sucesso para o filósofo " + philosopherName)
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
        String targetDuration = request.containsKey("targetDuration") ? String.valueOf(request.get("targetDuration")) : "15";
        String language = getStringValue(request, "language", "pt_BR");

        // Obter valores específicos do estoico
        String philosopherName = getStringValue(request, "philosopherName");
        String stoicConcept = getStringValue(request, "stoicConcept");
        String practicalApplication = getStringValue(request, "practicalApplication");
        String additionalContext = getStringValue(request, "additionalContext");

        // Substituir os placeholders básicos
        customizedTemplate = customizedTemplate.replace("{processId}", nullSafe(processId))
                .replace("{title}", nullSafe(title))
                .replace("{theme}", nullSafe(theme))
                .replace("{notes}", nullSafe(notes))
                .replace("{philosopher}", nullSafe(philosopherName))
                .replace("{targetDuration}", nullSafe(targetDuration))
                .replace("{language}", nullSafe(language));

        // Obter e substituir o estilo do filósofo
        // Aqui está a otimização: em vez de incluir detalhes redundantes no template,
        // injetamos apenas o estilo específico do filósofo escolhido
        String philosopherStyle = philosopherStyleService.getPhilosopherStyle(philosopherName);
        customizedTemplate = customizedTemplate.replace("{philosopherStyle}", philosopherStyle);

        // Construir contexto adicional
        StringBuilder contextBuilder = new StringBuilder();
        if (stoicConcept != null && !stoicConcept.isEmpty()) {
            contextBuilder.append("Conceito Estoico: ").append(stoicConcept).append("\n\n");
        }
        if (practicalApplication != null && !practicalApplication.isEmpty()) {
            contextBuilder.append("Aplicação Prática: ").append(practicalApplication).append("\n\n");
        }
        if (additionalContext != null && !additionalContext.isEmpty()) {
            contextBuilder.append(additionalContext);
        }
        customizedTemplate = customizedTemplate.replace("{additionalContext}", contextBuilder.toString());

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
                shouldIncludeSection(contentTypes, ContentType.TITLE) ? "### TÍTULO DO VÍDEO" : "");

        customizedTemplate = customizedTemplate.replace("{descriptionSection}",
                shouldIncludeSection(contentTypes, ContentType.DESCRIPTION) ?
                        "### DESCRIÇÃO DO VÍDEO\n[Descrição otimizada para SEO com 1500-2000 caracteres]" : "");

        customizedTemplate = customizedTemplate.replace("{tagsSection}",
                shouldIncludeSection(contentTypes, ContentType.TAGS) ?
                        "### TAGS\n[10-15 tags relevantes separadas por vírgula]" : "");

        customizedTemplate = customizedTemplate.replace("{scriptSection}",
                shouldIncludeSection(contentTypes, ContentType.SCRIPT) ?
                        "### ROTEIRO\n[Roteiro completo estruturado em introdução, desenvolvimento e conclusão]" : "");

        customizedTemplate = customizedTemplate.replace("{thumbnailSection}",
                shouldIncludeSection(contentTypes, ContentType.THUMBNAIL_IDEA) ?
                        "### IDEIA PARA THUMBNAIL\n[3 ideias para thumbnail com descrição visual]" : "");

        customizedTemplate = customizedTemplate.replace("{audioScriptSection}",
                shouldIncludeSection(contentTypes, ContentType.AUDIO_SCRIPT) ?
                        "### SCRIPT PARA ÁUDIO\n[Versão do roteiro otimizada para narração]" : "");

        customizedTemplate = customizedTemplate.replace("{shortVersionSection}",
                shouldIncludeSection(contentTypes, ContentType.SHORTS_IDEA) ?
                        "### VERSÃO CURTA\n[Versão condensada de 60-90 segundos para Shorts]" : "");

        // Logar tamanho do prompt para monitoramento de tokens
        log.debug("[StoicAgentHandler.customizeTemplate] - Prompt final com {} caracteres", customizedTemplate.length());

        return customizedTemplate;
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

        return "Conteúdo Estoico";
    }
}