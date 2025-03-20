package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle;

import br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle.impl.StyleApplier;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação de StyleApplier para conteúdo de orações
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrayerStyleApplier implements StyleApplier {

    private final PrayerStyleService prayerStyleService;

    @Override
    public AgentType getSupportedAgentType() {
        return AgentType.PRAYER;
    }

    @Override
    public String applyStyle(String template, ContentGenerationRequest request) {
        if (request instanceof PrayerContentGenerationRequest) {
            PrayerContentGenerationRequest prayerRequest = (PrayerContentGenerationRequest) request;

            // Obter características combinadas de estilo e tipo de oração
            String prayerStyleCharacteristics = prayerStyleService.getPrayerStyle(
                    prayerRequest.getPrayerStyle(),
                    prayerRequest.getPrayerType());

            // Aplicar estilo no template
            template = template.replace("{prayerStyleCharacteristics}", prayerStyleCharacteristics);

            // Substituições adicionais específicas
            if (prayerRequest.getBiblePassage() != null) {
                template = template.replace("{biblePassage}", prayerRequest.getBiblePassage());
            } else {
                template = template.replace("{biblePassage}", "");
            }

            if (prayerRequest.getOccasion() != null) {
                template = template.replace("{occasion}", prayerRequest.getOccasion());
            } else {
                template = template.replace("{occasion}", "");
            }

            log.debug("Aplicado estilo de oração para: {}", prayerRequest.getPrayerStyle());
        } else {
            log.warn("Requisição não é do tipo PrayerContentGenerationRequest, aplicando estilo padrão");
            template = template.replace("{prayerStyleCharacteristics}",
                    "Estilo de oração padrão - balanceado e acessível");
            template = template.replace("{biblePassage}", "");
            template = template.replace("{occasion}", "");
        }

        return template;
    }
}
