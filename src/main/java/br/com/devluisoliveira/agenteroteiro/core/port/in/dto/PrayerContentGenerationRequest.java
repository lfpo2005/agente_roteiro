package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrayerContentGenerationRequest extends ContentGenerationRequest {

    /**
     * Tipo de oração (ex: Reflexão Bíblica, Declaração de Fé, etc.)
     */
    private PrayerType prayerType;

    /**
     * Estilo de linguagem da oração (ex: Poético, Bíblico, Conversacional, etc.)
     */
    private PrayerStyle prayerStyle;

    /**
     * Passagem bíblica específica (opcional)
     */
    private String biblePassage;

    /**
     * Tema ou assunto principal da oração
     */
    private String prayerTheme;

    /**
     * Públic-alvo específico da oração (ex: jovens, famílias, líderes, etc.)
     */
    private String targetAudience;

    /**
     * Ocasião específica (ex: culto matutino, células, retiro, etc.)
     */
    private String occasion;

    /**
     * Nome de pessoa ou grupo para personalização (opcional)
     */
    private String personalizationName;

    /**
     * Versão bíblica preferida (ex: NVI, ARA, ACF, etc.)
     */
    private String bibleVersion = "NVI";

    /**
     * Se deve incluir instruções de uso/contextualização
     */
    private Boolean includeInstructions = true;

    /**
     * Duração preferida para a oração falada
     * Por padrão, orações são mais curtas que outros conteúdos
     */
    @Override
    public DurationType getDurationType() {
        DurationType type = super.getDurationType();
        // Se não foi definido explicitamente, usar MINUTES_3 como padrão para orações
        return type != null ? type : DurationType.MINUTES_3;
    }

    /**
     * Construtor para criar uma requisição com campos obrigatórios
     * @param prayerType Tipo de oração
     * @param prayerStyle Estilo da oração
     * @param prayerTheme Tema da oração
     */
    public PrayerContentGenerationRequest(PrayerType prayerType, PrayerStyle prayerStyle, String prayerTheme) {
        this.prayerType = prayerType;
        this.prayerStyle = prayerStyle;
        this.prayerTheme = prayerTheme;
    }
}
