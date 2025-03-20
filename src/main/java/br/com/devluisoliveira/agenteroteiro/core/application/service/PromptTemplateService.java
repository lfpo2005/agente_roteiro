package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.impl.StyleApplier;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serviço responsável por carregar e personalizar templates de prompt
 * para diferentes tipos de agentes usando o padrão Strategy
 */
@Service
@Slf4j
public class PromptTemplateService {

    private final Map<AgentType, StyleApplier> styleAppliers;

    /**
     * Construtor que inicializa o mapa de StyleAppliers usando injeção de dependência
     * @param appliers Lista de StyleAppliers disponíveis
     */
    public PromptTemplateService(List<StyleApplier> appliers) {
        this.styleAppliers = appliers.stream()
                .collect(Collectors.toMap(
                        StyleApplier::getSupportedAgentType,
                        Function.identity(),
                        (existing, replacement) -> existing // Em caso de conflito, manter o existente
                ));
        log.info("PromptTemplateService inicializado com {} StyleAppliers", appliers.size());
    }

    /**
     * Método principal para carregar e personalizar o template de acordo com o tipo de requisição
     */
    public String loadTemplateForRequest(ContentGenerationRequest request) {
        try {
            // Carregar o template base do arquivo
            String baseTemplate = loadTemplateFile(request.getAgentType());

            // Aplicar o estilo específico do agente (se disponível)
            StyleApplier styleApplier = styleAppliers.get(request.getAgentType());
            if (styleApplier != null) {
                baseTemplate = styleApplier.applyStyle(baseTemplate, request);
                log.debug("Aplicado estilo específico para agente: {}", request.getAgentType());
            }

            // Continuar com personalizações genéricas
            return personalizeTemplate(baseTemplate, request);

        } catch (Exception e) {
            log.error("Erro ao carregar template para o agente {}: {}",
                    request.getAgentType(), e.getMessage(), e);

            // Em caso de erro, retornar um template genérico
            return "# Erro ao carregar template\nPor favor, gere conteúdo para YouTube sobre: " +
                    (request.getTitle() != null ? request.getTitle() : "tema não especificado");
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
        String contentTypesFormatted = "";
        if (request.getContentTypes() != null && !request.getContentTypes().isEmpty()) {
            contentTypesFormatted = request.getContentTypes().stream()
                    .map(ContentType::getLabel)
                    .collect(Collectors.joining("\n- "));
            if (!contentTypesFormatted.isEmpty()) {
                contentTypesFormatted = "- " + contentTypesFormatted;
            }
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

    private boolean shouldIncludeSection(ContentGenerationRequest request, ContentType contentType) {
        return request.getContentTypes() != null && request.getContentTypes().contains(contentType);
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
            case PRAYER:
                return "- Use linguagem inspiradora e edificante\n" +
                        "- Mantenha uma abordagem respeitosa e reverente\n" +
                        "- Inclua elementos apropriados para o contexto espiritual\n" +
                        "- Considere o aspecto prático e aplicável da mensagem";
            default:
                return "";
        }
    }

    /**
     * Método de fallback para manter compatibilidade com código existente
     */
    public String loadPromptTemplate(ContentGenerationRequest request) {
        return loadTemplateForRequest(request);
    }

    /**
     * Método de fallback para manter compatibilidade com código existente
     */
    public String loadStoicPromptTemplate(StoicContentGenerationRequest request) {
        return loadTemplateForRequest(request);
    }

    /**
     * Carrega e personaliza o template para conteúdo de oração com parâmetros específicos
     * @param religiousTradition Tradição religiosa (ex: "Cristã", "Católica")
     * @param durationMinutes Duração em minutos
     * @param timeOfDay Momento do dia (ex: "Manhã", "Noite")
     * @param intentions Intenções específicas
     * @param language Idioma (ex: "pt-BR")
     * @return Template personalizado para geração de rotina de oração
     */
    public String loadPrayerPromptTemplate(
            String religiousTradition,
            Integer durationMinutes,
            String timeOfDay,
            String intentions,
            String language) {

        // Criar uma requisição temporária para manter compatibilidade
        PrayerContentGenerationRequest request = new PrayerContentGenerationRequest();
        request.setAgentType(AgentType.PRAYER);
        request.setLanguage(language);

        // Adicionar os parâmetros específicos como campos adicionais
        // Nota: Estes campos devem existir na classe PrayerContentGenerationRequest
        // Se não existirem, será necessário adicionar getters/setters ou usar um Map para dados adicionais

        // Exemplo assumindo que os campos existem:
        // request.setReligiousTradition(religiousTradition);
        // request.setTargetDuration(durationMinutes);
        // request.setTimeOfDay(timeOfDay);
        // request.setIntentions(intentions);

        // Se os campos não existirem, você pode usar um Map de contexto adicional:
        Map<String, Object> additionalContext = new HashMap<>();
        additionalContext.put("religiousTradition", religiousTradition);
        additionalContext.put("durationMinutes", durationMinutes);
        additionalContext.put("timeOfDay", timeOfDay);
        additionalContext.put("intentions", intentions);

        // Supondo que exista um método para definir contexto adicional
        // request.setAdditionalContextMap(additionalContext);

        // Ou converter para string e usar o campo existente
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Tradição religiosa: ").append(nullSafe(religiousTradition)).append("\n");
        contextBuilder.append("Duração: ").append(durationMinutes != null ? durationMinutes + " minutos" : "").append("\n");
        contextBuilder.append("Momento do dia: ").append(nullSafe(timeOfDay)).append("\n");
        contextBuilder.append("Intenções: ").append(nullSafe(intentions));
        request.setAdditionalContext(contextBuilder.toString());

        // Usar o método existente para carregar o template
        String template = loadTemplateForRequest(request);

        // Personalizar mais o template se necessário para este caso específico
        // Substituir placeholders específicos que podem não estar no método padrão
        template = template.replace("{religiousTradition}", nullSafe(religiousTradition));
        template = template.replace("{durationMinutes}", durationMinutes != null ? durationMinutes.toString() : "");
        template = template.replace("{timeOfDay}", nullSafe(timeOfDay));
        template = template.replace("{intentions}", nullSafe(intentions));

        return template;
    }

    public String buildShortPrompt(String originalText, String originalTitle, String language) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# Solicitação para versão curta de conteúdo\n\n");
        prompt.append("## Conteúdo original\n");
        prompt.append("Título: ").append(originalTitle).append("\n\n");

        // Limitar o tamanho do texto original se for muito grande
        String limitedText = originalText;
        if (originalText != null && originalText.length() > 2000) {
            limitedText = originalText.substring(0, 2000) + "... [texto truncado]";
        }
        prompt.append(limitedText).append("\n\n");

        prompt.append("## Instruções\n");
        prompt.append("Crie uma versão resumida e mais curta do conteúdo acima, mantendo os pontos principais e a essência do texto original.\n");
        prompt.append("A versão curta deve ser adequada para formatos como YouTube Shorts, Instagram ou TikTok.\n");
        prompt.append("Mantenha um tom similar ao original, mas com frases mais diretas e concisas.\n");

        // Instruções específicas por idioma
        if ("pt-BR".equals(language)) {
            prompt.append("\nEspecificações para Português do Brasil:\n");
            prompt.append("- Use linguagem coloquial brasileira\n");
            prompt.append("- Limite-se a aproximadamente 60-90 segundos de narração\n");
            prompt.append("- Inclua uma chamada para ação ao final\n");
        } else if (language != null && language.startsWith("en")) {
            prompt.append("\nEnglish specifications:\n");
            prompt.append("- Use conversational language\n");
            prompt.append("- Limit to approximately 60-90 seconds of narration\n");
            prompt.append("- Include a call to action at the end\n");
        }

        return prompt.toString();
    }

        public static String buildDescriptionPrompt(String title, String oracaoContent, String idioma) {
        log.debug("Construindo prompt para descrição: titulo={}, tamanho da oração={} caracteres, idioma={}",
                title, oracaoContent.length(), idioma);

        StringBuilder prompt = new StringBuilder();

        // Selecionar idioma para o prompt
        if ("pt".equalsIgnoreCase(idioma) || "pt-BR".equalsIgnoreCase(idioma)) {
            prompt.append("Crie uma descrição otimizada para YouTube e TikTok em português para o seguinte vídeo de oração:\n\n");
        } else if ("en".equalsIgnoreCase(idioma)) {
            prompt.append("Create an optimized description for YouTube and TikTok in English for the following prayer video:\n\n");
        } else if ("es-MX".equalsIgnoreCase(idioma)) {
            prompt.append("Crea una descripción optimizada para YouTube y TikTok en español latino/mexicano para el siguiente video de oración:\n\n");
            prompt.append("Utiliza expresiones, palabras y giros típicos del español de México y Latinoamérica. Evita términos o expresiones propias del español de España.\n\n");
        } else {
            // Padrão: espanhol
            prompt.append("Crea una descripción optimizada para YouTube y TikTok en español para el siguiente video de oración:\n\n");
        }

        prompt.append("Título: \"").append(title).append("\"\n\n");
        prompt.append("Conteúdo da oração:\n").append(oracaoContent).append("\n\n");

        prompt.append("Diretrizes para a descrição:\n");
        prompt.append("1. Escreva entre 500-1000 caracteres\n");
        prompt.append("2. Inclua 5-7 hashtags relevantes ao final\n");
        prompt.append("3. Adicione 3-5 frases inspiradoras relacionadas ao tema\n");
        prompt.append("4. Inclua um versículo bíblico principal\n");
        prompt.append("5. Adicione um call-to-action para inscrição/compartilhamento\n");
        prompt.append("6. Inclua 2-3 emojis estrategicamente colocados\n");
        prompt.append("7. Mencione os benefícios de ouvir esta oração\n\n");

        prompt.append("IMPORTANTE: A descrição deve estar no mesmo idioma da oração (");
        if ("pt".equalsIgnoreCase(idioma) || "pt-BR".equalsIgnoreCase(idioma)) {
            prompt.append("português");
        } else if ("en".equalsIgnoreCase(idioma)) {
            prompt.append("inglês");
        } else if ("es-MX".equalsIgnoreCase(idioma)) {
            prompt.append("español latino/mexicano");
        } else {
            prompt.append("español");
        }
        prompt.append(").\n\n");

        prompt.append("Responda APENAS com o texto da descrição, sem comentários adicionais.");

        return prompt.toString();
    }
}