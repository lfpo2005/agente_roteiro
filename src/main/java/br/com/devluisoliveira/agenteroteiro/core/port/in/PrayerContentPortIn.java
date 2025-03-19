package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;

import java.util.UUID;

public interface PrayerContentPortIn {

    /**
     * Gera conteúdo de oração baseado na requisição
     * @param user Usuário que solicitou
     * @param request Requisição com detalhes da oração
     * @return Resposta com o conteúdo gerado
     */
    ContentGenerationResponse generateContent(User user, PrayerContentGenerationRequest request);

    /**
     * Gera uma versão curta de uma oração existente
     * @param user Usuário que solicitou
     * @param contentId ID do conteúdo original
     * @return Resposta com a versão curta gerada
     */
    ContentGenerationResponse generateShortVersion(User user, UUID contentId);

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
    ContentGenerationResponse generatePrayerRoutine(
            User user,
            String religiousTradition,
            String denomination,
            Integer durationMinutes,
            String timeOfDay,
            String intentions,
            String language);
}