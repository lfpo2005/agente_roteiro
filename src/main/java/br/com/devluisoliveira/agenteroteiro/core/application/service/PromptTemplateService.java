package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptTemplateService {

    private static final Map<AgentType, String> TEMPLATE_FILES = new HashMap<>();

    static {
        TEMPLATE_FILES.put(AgentType.GENERIC, "prompts/prompt_base_generico.txt");
        TEMPLATE_FILES.put(AgentType.STOICISM, "prompts/prompt_base_estoicismo.txt");
    }

    /**
     * Carrega e personaliza o template de prompt para o tipo de agente especificado
     */
    public String loadPromptTemplate(ContentGenerationRequest request) {
        try {
            // Carregar o template base do arquivo
            String baseTemplate = loadTemplateFile(request.getAgentType());

            // Personalizar o template com os dados da requisição
            return personalizeTemplate(baseTemplate, request);

        } catch (Exception e) {
            log.error("Erro ao carregar template de prompt para o agente {}: {}",
                    request.getAgentType(), e.getMessage());
            // Em caso de erro, usar template genérico
            try {
                return loadTemplateFile(AgentType.GENERIC);
            } catch (Exception ex) {
                log.error("Erro ao carregar template genérico: {}", ex.getMessage());
                return "# Erro ao carregar template\nPor favor, gere conteúdo para YouTube sobre: " +
                        (request.getTitle() != null ? request.getTitle() : "tema não especificado");
            }
        }
    }


    public String loadStoicPromptTemplate(StoicContentGenerationRequest request, PhilosopherStyleService styleService) {
        // Carregar template base
        String baseTemplate = loadTemplateFile(AgentType.STOICISM);

        // Personalizar com estilo do filósofo específico
        String philosopherStyle = styleService.getPhilosopherStyle(request.getPhilosopherName());
        baseTemplate = baseTemplate.replace("{philosopherStyle}", philosopherStyle);

        // Continuar com outras personalizações
        return personalizeTemplate(baseTemplate, convertToStandardRequest(request));
    }

    /**
     * Carrega o arquivo de template para o tipo de agente especificado
     */
    private String loadTemplateFile(AgentType agentType) throws IOException {
        String templatePath = TEMPLATE_FILES.getOrDefault(agentType, TEMPLATE_FILES.get(AgentType.GENERIC));
        Resource resource = new ClassPathResource(templatePath);

        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    /**
     * Personaliza o template substituindo os placeholders pelos valores da requisição
     */
    private String personalizeTemplate(String template, ContentGenerationRequest request) {
        // Substituir placeholders básicos
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{processId}", request.getProcessId());
        replacements.put("{title}", nullSafe(request.getTitle()));
        replacements.put("{theme}", nullSafe(request.getTheme()));
        replacements.put("{notes}", nullSafe(request.getNotes()));
        replacements.put("{videoTopic}", nullSafe(request.getVideoTopic()));
        replacements.put("{targetAudience}", nullSafe(request.getTargetAudience()));
        replacements.put("{toneStyle}", nullSafe(request.getToneStyle()));
        replacements.put("{targetDuration}", request.getTargetDuration() != null ?
                request.getTargetDuration().toString() : "não especificado");
        replacements.put("{language}", nullSafe(request.getLanguage()));
        replacements.put("{additionalContext}", nullSafe(request.getAdditionalContext()));

        // Formatar a lista de tipos de conteúdo
        String contentTypesFormatted = request.getContentTypes().stream()
                .map(ContentType::getLabel)
                .collect(Collectors.joining("\n- "));
        if (!contentTypesFormatted.isEmpty()) {
            contentTypesFormatted = "- " + contentTypesFormatted;
        }
        replacements.put("{contentTypesFormatted}", contentTypesFormatted);

        // Preparar seções condicionais
        replacements.put("{titleSection}", shouldIncludeSection(request, ContentType.TITLE) ?
                "### TÍTULO DO VÍDEO" : "");

        replacements.put("{descriptionSection}", shouldIncludeSection(request, ContentType.DESCRIPTION) ?
                "### DESCRIÇÃO DO VÍDEO\n" +
                        "[Aqui será gerada uma descrição completa com 1500 caracteres no máximo, incluindo:\n" +
                        "- Titulo\n" +
                        "- Breve introdução ao conteúdo do vídeo\n" +
                        "- Pontos principais abordados\n" +
                        "- Informações relevantes sobre o tema\n" +
                        "- Call-to-action para inscrição, likes e compartilhamento\n" +
                        "- Hashtags relevantes]" : "");

        replacements.put("{tagsSection}", shouldIncludeSection(request, ContentType.TAGS) ?
                "### TAGS\n" +
                        "[Aqui serão listadas 10-15 tags relevantes para o vídeo, começando com as mais específicas]" : "");

        replacements.put("{scriptSection}", shouldIncludeSection(request, ContentType.SCRIPT) ?
                "### ROTEIRO\n" +
                        "[Aqui será gerado um roteiro completo com:\n" +
                        "- Introdução cativante\n" +
                        "- Desenvolvimento em tópicos claros\n" +
                        "- Conclusão com resumo e call-to-action\n" +
                        "- Indicações de pontos para visual/B-roll quando relevante]" : "");

        replacements.put("{thumbnailSection}", shouldIncludeSection(request, ContentType.THUMBNAIL_IDEA) ?
                "### IDEIA PARA THUMBNAIL\n" +
                        "[Aqui serão sugeridas 3 ideias diferentes para thumbnail, com elementos visuais e texto]" : "");

        replacements.put("{audioScriptSection}", shouldIncludeSection(request, ContentType.AUDIO_SCRIPT) ?
                "### SCRIPT PARA ÁUDIO\n" +
                        "[Aqui será gerado um script otimizado para narração em áudio, com linguagem mais conversacional e fácil de pronunciar]" : "");

        replacements.put("{shortVersionSection}", Boolean.TRUE.equals(request.getGenerateShortVersion()) ?
                "### VERSÃO CURTA\n" +
                        "[Aqui será gerada uma versão reduzida do conteúdo principal, mantendo os pontos essenciais]" : "");

        // Adicionar diretrizes específicas para o tipo de agente
        replacements.put("{agentSpecificGuidelines}", getAgentSpecificGuidelines(request.getAgentType()));

        // Aplicar todas as substituições
        String personalizedTemplate = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            personalizedTemplate = personalizedTemplate.replace(entry.getKey(), entry.getValue());
        }

        return personalizedTemplate;
    }

    private boolean shouldIncludeSection(ContentGenerationRequest request, ContentType contentType) {
        return request.getContentTypes().contains(contentType);
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private String getAgentSpecificGuidelines(AgentType agentType) {
        if (agentType == null) {
            return "";
        }

        switch (agentType) {
            case GENERIC:
                return "- Use precise and accessible technical language\n" +
                        "- Explain complex concepts clearly\n" +
                        "- Compare products or technologies when relevant\n" +
                        "- Stay objective in analyses";
            case STOICISM:
                return "- Use specific terms from the Stoicism philosophy\n" +
                        "- Be enthusiastic about Stoic principles and practices\n" +
                        "- Include details about Stoic exercises and experiences\n" +
                        "- Consider different levels of understanding and application";
            default:
                return "";
        }
    }
}

