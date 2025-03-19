package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.PhilosopherType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
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

    private final PhilosopherStyleService philosopherStyleService;

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

    /**
     * Carrega e personaliza o template para conteúdo estoico
     */
    public String loadStoicPromptTemplate(StoicContentGenerationRequest request) {
        try {
            // Carregar template base
            String baseTemplate = loadTemplateFile(AgentType.STOICISM);

            // Obter estilo do filósofo escolhido
            String philosopherName = request.getPhilosopherName();
            String philosopherStyle = philosopherStyleService.getPhilosopherStyle(philosopherName);

            // Personalizar o template com o estilo do filósofo e outros dados
            baseTemplate = baseTemplate.replace("{philosopherStyle}", philosopherStyle);

            // Continuar com outras personalizações
            return personalizeTemplate(baseTemplate, request);

        } catch (Exception e) {
            log.error("Erro ao carregar template estoico: {}", e.getMessage(), e);
            return "# Erro ao carregar template\nPor favor, gere conteúdo estoico para YouTube sobre: " +
                    (request.getTitle() != null ? request.getTitle() : "tema não especificado") +
                    " no estilo do filósofo " + request.getPhilosopherName();
        }
    }

    /**
     * Carrega o arquivo de template para o tipo de agente especificado
     */
    private String loadTemplateFile(AgentType agentType) throws IOException {
        String templatePath = agentType.getPromptTemplate();
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
        replacements.put("{processId}", nullSafe(request.getProcessId()));
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
                        "[Aqui será gerada uma descrição completa com 1500-2000 caracteres, incluindo:\n" +
                        "- Breve introdução ao conteúdo do vídeo\n" +
                        "- Pontos principais abordados\n" +
                        "- Informações relevantes sobre o tema\n" +
                        "- Call-to-action para inscrição, likes e compartilhamento\n" +
                        "- Hashtags relevantes]" : "");

        replacements.put("{tagsSection}", shouldIncludeSection(request, ContentType.TAGS) ?
                "### TAGS\n" +
                        "[10-15 tags relevantes separadas por vírgula]" : "");

        replacements.put("{scriptSection}", shouldIncludeSection(request, ContentType.SCRIPT) ?
                "### ROTEIRO\n" +
                        "[Roteiro completo estruturado com:\n" +
                        "- Introdução cativante\n" +
                        "- Desenvolvimento em tópicos claros\n" +
                        "- Conclusão com resumo e call-to-action]" : "");

        replacements.put("{thumbnailSection}", shouldIncludeSection(request, ContentType.THUMBNAIL_IDEA) ?
                "### IDEIA PARA THUMBNAIL\n" +
                        "[3 ideias para thumbnail com elementos visuais e texto]" : "");

        replacements.put("{audioScriptSection}", shouldIncludeSection(request, ContentType.AUDIO_SCRIPT) ?
                "### SCRIPT PARA ÁUDIO\n" +
                        "[Script otimizado para narração em áudio]" : "");

        replacements.put("{shortVersionSection}", Boolean.TRUE.equals(request.getGenerateShortVersion()) ?
                "### VERSÃO CURTA\n" +
                        "[Versão reduzida de 60-90 segundos para Shorts]" : "");

        // Adicionar diretrizes específicas para o tipo de agente se necessário
        if (template.contains("{agentSpecificGuidelines}")) {
            replacements.put("{agentSpecificGuidelines}", getAgentSpecificGuidelines(request.getAgentType()));
        }

        // Aplicar todas as substituições
        String personalizedTemplate = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            personalizedTemplate = personalizedTemplate.replace(entry.getKey(), entry.getValue());
        }

        // Logar tamanho do template para monitoramento de uso de tokens
        log.debug("Template personalizado gerado com {} caracteres", personalizedTemplate.length());

        return personalizedTemplate;
    }

    /**
     * Personaliza o template especificamente para o caso de conteúdo estoico
     */
    private String personalizeStoicTemplate(String template, StoicContentGenerationRequest request) {
        // Personalizações básicas
        String result = personalizeTemplate(template, request);

        // Personalização específica para o filósofo
        if (request.getPhilosopher() != null) {
            String philosopherStyle = philosopherStyleService.getPhilosopherStyle(request.getPhilosopher());
            result = result.replace("{philosopherStyle}", philosopherStyle);
        } else if (request.getPhilosopherName() != null) {
            String philosopherStyle = philosopherStyleService.getPhilosopherStyle(request.getPhilosopherName());
            result = result.replace("{philosopherStyle}", philosopherStyle);
        } else {
            result = result.replace("{philosopherStyle}", "Estilo estoico genérico");
        }

        // Substituições adicionais específicas
        result = result.replace("{philosopher}", nullSafe(request.getPhilosopherName()));

        return result;
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
                return "- Use linguagem técnica precisa e acessível\n" +
                        "- Explique conceitos complexos com clareza\n" +
                        "- Compare produtos ou tecnologias quando relevante\n" +
                        "- Mantenha-se objetivo nas análises";
            case STOICISM:
                return "- Use terminologia específica da filosofia estoica\n" +
                        "- Seja entusiástico sobre os princípios e práticas estoicas\n" +
                        "- Inclua detalhes sobre exercícios e experiências estoicas\n" +
                        "- Considere diferentes níveis de compreensão e aplicação";
            default:
                return "";
        }
    }
}