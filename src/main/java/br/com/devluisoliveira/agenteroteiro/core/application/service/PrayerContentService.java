package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.GenericGeneraMapper;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.PrayerContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.ContentGenerationPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço para geração de conteúdo de orações
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrayerContentService implements PrayerContentPortIn {

    private final GenericGeneraMapper genericGeneraMapper;
    private final PrayerStyleService prayerStyleService;
    private final AgentGenerationService agentGenerationService;
    private final ContentGenerationPortOut contentGenerationPortOut;
    private final ElevenLabsService audioService;
    private final PromptTemplateService promptTemplateService;
    private final OpenAIService openAIService;

    /**
     * Gera conteúdo de oração baseado na requisição
     * @param user Usuário que solicitou
     * @param request Requisição com detalhes da oração
     * @return Resposta com o conteúdo gerado
     */
    public ContentGenerationResponse generateContent(User user, PrayerContentGenerationRequest request) {
        log.info("[PrayerContentService.generateContent] - Iniciando geração de conteúdo de oração para usuário: {}, tema: {}, estilo: {}",
                user.getUsername(), request.getTheme(), request.getPrayerStyle());

        try {
            // Validar a solicitação
            validateRequest(request);

            // Garantir que temos um processId
            if (request.getProcessId() == null || request.getProcessId().isEmpty()) {
                request.setProcessId(UUID.randomUUID().toString());
            }

            // Definir tipo de agente
            request.setAgentType(AgentType.PRAYER);

            // Converter a request para Map para processamento pelo AgentGenerationService
            Map<String, Object> requestMap = convertToMap(request);

            // Chamar o serviço centralizado para geração de conteúdo
            ContentGenerationResponse response = agentGenerationService.startGeneration(requestMap);

            // Personalizar o título se necessário (adicionar emojis, hashtags etc.)
            response = enhancePrayerResponse(response, request);

            // Gerar áudio se solicitado
            if (Boolean.TRUE.equals(request.getGenerateAudio()) && response.getText() != null && !response.getText().isEmpty()) {
                try {
                    log.info("[PrayerContentService.generateContent] - Gerando áudio para oração");
                    byte[] audioData = audioService.generateSpeech(response.getText());

                    // Converter para Base64 ou outra representação adequada
                    String audioBase64 = "data:audio/mp3;base64," + java.util.Base64.getEncoder().encodeToString(audioData);
                    response.setAudio(audioBase64);

                    log.info("[PrayerContentService.generateContent] - Áudio gerado com sucesso");
                } catch (Exception e) {
                    log.error("[PrayerContentService.generateContent] - Erro ao gerar áudio: {}", e.getMessage(), e);
                    // Não falhar todo o processo se apenas o áudio falhar
                }
            }

            // Persistir o resultado
            if (response != null && "COMPLETED".equals(response.getStatus())) {
                saveGeneratedContent(user, response);
                log.info("[PrayerContentService.generateContent] - Conteúdo de oração salvo com sucesso, ID: {}", response.getProcessId());
            }

            return response;
        } catch (Exception e) {
            log.error("[PrayerContentService.generateContent] - Erro ao gerar conteúdo de oração: {}", e.getMessage(), e);
            return ContentGenerationResponse.builder()
                    .processId(UUID.fromString(request.getProcessId()))
                    .status("ERROR")
                    .message("Erro ao gerar conteúdo de oração: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Gera uma versão curta de uma oração existente
     * @param user Usuário que solicitou
     * @param contentId ID do conteúdo original
     * @return Resposta com a versão curta gerada
     */
    public ContentGenerationResponse generateShortVersion(User user, UUID contentId) {
        log.info("[PrayerContentService.generateShortVersion] - Gerando versão curta da oração ID: {}", contentId);

        try {
            // Buscar o conteúdo original
            ContentGeneration original = contentGenerationPortOut.findById(contentId)
                    .orElseThrow(() -> new IllegalArgumentException("Conteúdo não encontrado: " + contentId));

            // Verificar se o conteúdo pertence ao usuário
            if (!original.getUser().getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Conteúdo não pertence ao usuário");
            }

            // Verificar se o conteúdo é do tipo PRAYER
            if (!AgentType.PRAYER.equals(original.getAgentType())) {
                throw new IllegalArgumentException("Conteúdo não é uma oração");
            }

            // Gerar prompt para versão curta
            String shortPrompt = PromptBuilder.buildShortPrompt(
                    original.getText(),
                    original.getTitle(),
                    "pt-BR" // Assumindo português, ajuste conforme necessário
            );

            // Chamar a API para gerar a versão curta
            String shortContent = openAIService.generateOracao(shortPrompt);

            // Criar resposta com a versão curta
            ContentGenerationResponse response = ContentGenerationResponse.builder()
                    .processId(UUID.randomUUID())
                    .title(original.getTitle() + " (Versão Curta)")
                    .agentType(AgentType.PRAYER)
                    .text(shortContent)
                    .description(original.getDescription())
                    .tags(original.getTags())
                    .status("COMPLETED")
                    .message("Versão curta gerada com sucesso")
                    .build();

            // Salvar como novo conteúdo
            ContentGeneration shortVersion = genericGeneraMapper.toEntity(response);
            shortVersion.setUser(user);
            shortVersion.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
            contentGenerationPortOut.saveContentGeneration(shortVersion);

            return response;
        } catch (Exception e) {
            log.error("[PrayerContentService.generateShortVersion] - Erro: {}", e.getMessage(), e);
            return ContentGenerationResponse.builder()
                    .processId(UUID.randomUUID())
                    .status("ERROR")
                    .message("Erro ao gerar versão curta: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Valida a requisição de geração de oração
     * @param request Requisição a ser validada
     */
    private void validateRequest(PrayerContentGenerationRequest request) {
        log.debug("[PrayerContentService.validateRequest] - Validando requisição");

        if (request.getTheme() == null || request.getTheme().trim().isEmpty()) {
            throw new IllegalArgumentException("O tema da oração é obrigatório");
        }

        if (request.getContentTypes() == null || request.getContentTypes().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um tipo de conteúdo deve ser selecionado");
        }

        log.debug("[PrayerContentService.validateRequest] - Requisição válida");
    }

    /**
     * Converte a requisição para um mapa para processamento
     * @param request Requisição a ser convertida
     * @return Mapa com os dados da requisição
     */
    private Map<String, Object> convertToMap(PrayerContentGenerationRequest request) {
        Map<String, Object> requestMap = new HashMap<>();

        // Mapear propriedades básicas
        requestMap.put("processId", request.getProcessId());
        requestMap.put("title", request.getTitle());
        requestMap.put("theme", request.getTheme());
        requestMap.put("notes", request.getNotes());
        requestMap.put("targetDuration", request.getTargetDuration());
        requestMap.put("language", request.getLanguage());
        requestMap.put("contentTypes", request.getContentTypes());
        requestMap.put("generateShortVersion", request.getGenerateShortVersion());
        requestMap.put("generateAudio", request.getGenerateAudio());
        requestMap.put("agentType", AgentType.PRAYER);

        // Mapear propriedades específicas de oração
        requestMap.put("prayerType", request.getPrayerType());
        requestMap.put("prayerStyle", request.getPrayerStyle());
        requestMap.put("biblePassage", request.getBiblePassage());
        requestMap.put("prayerTheme", request.getTheme()); // Redundante para clareza
        requestMap.put("targetAudience", request.getTargetAudience());
        requestMap.put("occasion", request.getOccasion());
        requestMap.put("personalizationName", request.getPersonalizationName());
        requestMap.put("bibleVersion", request.getBibleVersion());
        requestMap.put("includeInstructions", request.getIncludeInstructions());

        // Características de estilo e tipo
        if (request.getPrayerStyle() != null && request.getPrayerType() != null) {
            String characteristics = prayerStyleService.getCombinedPrayerCharacteristics(
                    request.getPrayerStyle(),
                    request.getPrayerType()
            );
            requestMap.put("prayerStyleCharacteristics", characteristics);
        }

        return requestMap;
    }

    /**
     * Melhora a resposta de oração com elementos adicionais
     * @param response Resposta original
     * @param request Requisição original
     * @return Resposta aprimorada
     */
    private ContentGenerationResponse enhancePrayerResponse(ContentGenerationResponse response, PrayerContentGenerationRequest request) {
        // Se não houver resposta ou não estiver completa, retornar sem modificações
        if (response == null || !"COMPLETED".equals(response.getStatus())) {
            return response;
        }

        // Melhorar o título com emojis apropriados se não tiver
        if (response.getTitle() != null && !response.getTitle().contains("🙏") && !response.getTitle().contains("✝️")) {
            String enhancedTitle = addPrayerEmojis(response.getTitle());
            response.setTitle(enhancedTitle);
        }

        // Adicionar hashtags se necessário
        if (response.getTags() == null || response.getTags().isEmpty()) {
            String tags = generatePrayerTags(request.getTheme(), request.getPrayerType());
            response.setTags(tags);
        }

        return response;
    }

    /**
     * Adiciona emojis relacionados a orações no título
     * @param title Título original
     * @return Título com emojis
     */
    private String addPrayerEmojis(String title) {
        // Verificar se já tem emojis
        if (title.matches(".*[\\p{Emoji}].*")) {
            return title;
        }

        // Emojis relacionados a orações
        String[] prayerEmojis = {"🙏", "✝️", "📖", "❤️", "✨", "🕊️", "🛐", "⛪", "🌟"};

        // Selecionar 1-2 emojis aleatórios
        int numEmojis = 1 + (int)(Math.random() * 2); // 1 ou 2
        StringBuilder enhancedTitle = new StringBuilder();

        // Adicionar emoji no início
        if (Math.random() > 0.5) {
            enhancedTitle.append(prayerEmojis[(int)(Math.random() * prayerEmojis.length)]).append(" ");
            numEmojis--;
        }

        // Adicionar o título
        enhancedTitle.append(title);

        // Adicionar emoji no final
        if (numEmojis > 0) {
            enhancedTitle.append(" ").append(prayerEmojis[(int)(Math.random() * prayerEmojis.length)]);
        }

        return enhancedTitle.toString();
    }

    /**
     * Gera hashtags apropriadas para a oração
     * @param theme Tema da oração
     * @param prayerType Tipo de oração
     * @return String com hashtags
     */
    private String generatePrayerTags(String theme, PrayerType prayerType) {
        StringBuilder tags = new StringBuilder();

        // Hashtags básicas
        tags.append("#oração #fé #espiritualidade");

        // Adicionar hashtag do tema
        if (theme != null && !theme.isEmpty()) {
            String themeTag = "#" + theme.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9áàâãéèêíìóòôõúùüçÁÀÂÃÉÈÊÍÌÓÒÔÕÚÙÜÇ]", "")
                    .replaceAll("\\s+", "");
            tags.append(" ").append(themeTag);
        }

        // Adicionar hashtag baseada no tipo de oração
        if (prayerType != null) {
            switch (prayerType) {
                case BIBLICAL_REFLECTION:
                    tags.append(" #reflexãoBíblica #estudo #revelação");
                    break;
                case DEVOTIONAL_INTIMACY:
                    tags.append(" #intimidade #adoração #devoção");
                    break;
                case FAITH_DECLARATION:
                    tags.append(" #declaraçãoDeFé #vitória #promessas");
                    break;
                case GRATITUDE_WORSHIP:
                    tags.append(" #gratidão #louvor #adoração");
                    break;
                case PASTORAL_COMFORT:
                    tags.append(" #consolo #esperança #cura");
                    break;
                case INTERCESSION:
                    tags.append(" #intercessão #súplica #clamor");
                    break;
                case REPENTANCE:
                    tags.append(" #arrependimento #perdão #restauração");
                    break;
                default:
                    tags.append(" #vida #paz #amor");
            }
        }

        return tags.toString();
    }

    /**
     * Salva o conteúdo gerado no banco de dados
     * @param user Usuário solicitante
     * @param response Resposta contendo o conteúdo
     */
    private void saveGeneratedContent(User user, ContentGenerationResponse response) {
        try {
            ContentGeneration contentGeneration = genericGeneraMapper.toEntity(response);
            contentGeneration.setUser(user);
            contentGenerationPortOut.saveContentGeneration(contentGeneration);
        } catch (Exception e) {
            log.error("[PrayerContentService.saveGeneratedContent] - Erro ao salvar conteúdo: {}", e.getMessage(), e);
            // Não propagar exceção, apenas logar, para não impedir o retorno da resposta ao usuário
        }
    }

    /**
     * Gera a descrição para o vídeo/áudio da oração
     * @param title Título da oração
     * @param text Texto da oração
     * @param language Idioma
     * @return Descrição otimizada para plataformas
     */
    public String generateDescription(String title, String text, String language) {
        log.info("[PrayerContentService.generateDescription] - Gerando descrição para oração");

        try {
            // Construir prompt para descrição
            String descriptionPrompt = PromptBuilder.buildDescriptionPrompt(title, text, language);

            // Gerar descrição
            String description = openAIService.generateDescription(descriptionPrompt);

            return description;
        } catch (Exception e) {
            log.error("[PrayerContentService.generateDescription] - Erro: {}", e.getMessage(), e);
            // Gerar uma descrição padrão em caso de erro
            return "🙏 " + title + "\n\n" +
                    "Esta oração especial vai tocar seu coração e renovar sua fé. " +
                    "Ouça com atenção para receber as bênçãos que Deus tem para você hoje.\n\n" +
                    "#oração #fé #espiritualidade";
        }
    }

    /**
     * Gera uma rotina de oração personalizada
     * @param user Usuário solicitante
     * @param religiousTradition Tradição religiosa
     * @param denomination Denominação específica
     * @param durationMinutes Duração em minutos
     * @param timeOfDay Momento do dia
     * @param intentions Intenções específicas
     * @param language Idioma
     * @return Resposta com a rotina de oração
     */
    public ContentGenerationResponse generatePrayerRoutine(
            User user,
            String religiousTradition,
            String denomination,
            Integer durationMinutes,
            String timeOfDay,
            String intentions,
            String language) {

        log.info("[PrayerContentService.generatePrayerRoutine] - Gerando rotina de oração personalizada");

        try {
            // Construir prompt para rotina
            String routinePrompt = PromptBuilder.buildPrayerRoutinePrompt(
                    religiousTradition,
                    denomination,
                    durationMinutes,
                    timeOfDay,
                    intentions,
                    language);

            // Gerar rotina
            String routineContent = openAIService.generateOracao(routinePrompt);

            // Construir título
            String title = "Rotina de Oração " +
                    (timeOfDay != null && !timeOfDay.isEmpty() ? "para " + timeOfDay : "Personalizada") +
                    (durationMinutes != null ? " (" + durationMinutes + " minutos)" : "");

            // Criar resposta
            ContentGenerationResponse response = ContentGenerationResponse.builder()
                    .processId(UUID.randomUUID())
                    .title(title)
                    .agentType(AgentType.PRAYER)
                    .text(routineContent)
                    .status("COMPLETED")
                    .message("Rotina de oração personalizada gerada com sucesso")
                    .build();

            // Salvar conteúdo
            saveGeneratedContent(user, response);

            return response;
        } catch (Exception e) {
            log.error("[PrayerContentService.generatePrayerRoutine] - Erro: {}", e.getMessage(), e);
            return ContentGenerationResponse.builder()
                    .processId(UUID.randomUUID())
                    .status("ERROR")
                    .message("Erro ao gerar rotina de oração: " + e.getMessage())
                    .build();
        }
    }
}