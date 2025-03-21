package br.com.devluisoliveira.agenteroteiro.core.application.utils;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.ContentType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe utilitária para processamento de Content Generation
 * Contém métodos compartilhados entre os diferentes handlers de agentes
 */
public class ContentGenerationUtils {

    /**
     * Determina se deve incluir a seção de versão curta baseado na requisição
     *
     * @param request Map com os parâmetros da requisição
     * @return true se deve incluir versão curta, false caso contrário
     */
    public static boolean shouldIncludeShortVersion(Map<String, Object> request) {
        // Extrair duração
        DurationType durationType = extractDurationType(request);

        // Vídeos já considerados curtos - não precisa de versão short
        if (durationType == DurationType.SECONDS_30 ||
                durationType == DurationType.SECONDS_60 ||
                durationType == DurationType.MINUTES_3) {
            return false;
        }

        // Verificar a flag de geração
        Object generateShortObj = request.get("generateShortVersion");
        if (generateShortObj instanceof Boolean) {
            return (Boolean) generateShortObj;
        }

        return false; // Padrão: não gerar se não estiver explicitamente solicitado
    }

    /**
     * Extrai o tipo de duração da requisição
     *
     * @param request Map com os parâmetros da requisição
     * @return DurationType extraído ou o padrão MINUTES_5
     */
    public static DurationType extractDurationType(Map<String, Object> request) {
        // Tentar obter do mapa de request
        Object durationTypeObj = request.get("durationType");

        if (durationTypeObj instanceof DurationType) {
            return (DurationType) durationTypeObj;
        }
        else if (durationTypeObj instanceof String &&
                durationTypeObj != null && !((String)durationTypeObj).isEmpty()) {
            try {
                return DurationType.valueOf((String)durationTypeObj);
            } catch (IllegalArgumentException e) {
                // Se não conseguir converter, usar o padrão
                return DurationType.MINUTES_5;
            }
        }

        // Tentar obter do targetDuration (minutos)
        Object targetDurationObj = request.get("targetDuration");
        if (targetDurationObj instanceof Integer) {
            int minutes = (Integer) targetDurationObj;
            return DurationType.findClosest(minutes);
        }

        // Padrão
        return DurationType.MINUTES_5;
    }

       public static Map<ContentType, String> extractContentSections(
            String aiResponse,
            Map<String, Object> request,
            Map<String, ContentType> sectionToTypeMap) {

        Map<ContentType, String> contentMap = new HashMap<>();

        // Adicionar seção de versão curta apenas se necessário
        Map<String, ContentType> finalSectionMap = new HashMap<>(sectionToTypeMap);
        boolean shouldIncludeShort = shouldIncludeShortVersion(request);

        // Se não deve incluir versão curta, remover a seção correspondente
        if (!shouldIncludeShort) {
            finalSectionMap.values().removeIf(contentType -> contentType == ContentType.SHORTS_IDEA);
        }

        // Regex para encontrar seções
        Pattern sectionPattern = Pattern.compile("###\\s+([^\\n]+)([\\s\\S]*?)(?=###|$)");
        Matcher matcher = sectionPattern.matcher(aiResponse);

        // Processar as seções encontradas
        while (matcher.find()) {
            String sectionTitle = matcher.group(1).trim();
            String sectionContent = matcher.group(2).trim();

            // Mapear para o tipo de conteúdo correspondente
            for (Map.Entry<String, ContentType> entry : finalSectionMap.entrySet()) {
                if (sectionTitle.contains(entry.getKey())) {
                    contentMap.put(entry.getValue(), sectionContent);
                    break;
                }
            }
        }

        return contentMap;
    }

    /**
     * Determina se um placeholder de seção curta deve ser incluído no template
     *
     * @param request Map com os parâmetros da requisição
     * @param shortVersionTemplate Template para a seção curta
     * @return Template se deve incluir, string vazia caso contrário
     */
    public static String getShortVersionSectionTemplate(Map<String, Object> request, String shortVersionTemplate) {
        return shouldIncludeShortVersion(request) ? shortVersionTemplate : "";
    }
}
