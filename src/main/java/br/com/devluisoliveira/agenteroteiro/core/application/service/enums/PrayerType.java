package br.com.devluisoliveira.agenteroteiro.core.application.service.enums;

import lombok.Getter;

/**
 * Enum que define os tipos de oração disponíveis para geração de conteúdo
 */
@Getter
public enum PrayerType {

    BIBLICAL_REFLECTION(
            "Reflexão Bíblica Profunda",
            "Oração baseada em uma análise profunda de passagens bíblicas, com aplicações práticas e revelações espirituais.",
            "reflexão, bíblia, estudo, revelação, aplicação"),

    DEVOTIONAL_INTIMACY(
            "Intimidade Devocional",
            "Oração íntima e pessoal, focada no relacionamento profundo com Deus, expressando amor, adoração e entrega.",
            "intimidade, relacionamento, devocional, comunhão, presença"),

    FAITH_DECLARATION(
            "Declaração de Fé e Vitória",
            "Oração declarativa, baseada em promessas bíblicas, proclamando vitória, cura, provisão e conquistas espirituais.",
            "declaração, vitória, conquista, promessas, decretos"),

    GRATITUDE_WORSHIP(
            "Gratidão e Adoração",
            "Oração centrada no louvor, gratidão e exaltação a Deus, reconhecendo Seus atributos e obras.",
            "gratidão, adoração, louvor, exaltação, agradecimento"),

    PASTORAL_COMFORT(
            "Consolo Pastoral",
            "Oração de conforto, cuidado e direção, trazendo esperança em tempos difíceis e orientação espiritual.",
            "consolo, cuidado, direção, esperança, cura emocional"),

    INTERCESSION(
            "Intercessão",
            "Oração em favor de outras pessoas, grupos, nações ou situações específicas, clamando pela intervenção divina.",
            "intercessão, súplica, mediação, clamor, intermediação"),

    REPENTANCE(
            "Arrependimento e Restauração",
            "Oração de confissão, arrependimento e busca por perdão e restauração espiritual.",
            "arrependimento, confissão, perdão, restauração, purificação");

    private final String displayName;
    private final String description;
    private final String keywords;

    PrayerType(String displayName, String description, String keywords) {
        this.displayName = displayName;
        this.description = description;
        this.keywords = keywords;
    }

    /**
     * Encontra um tipo de oração pelo nome de exibição
     * @param displayName Nome de exibição
     * @return PrayerType correspondente ou null se não encontrado
     */
    public static PrayerType findByDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        for (PrayerType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName.trim())) {
                return type;
            }
        }

        return null;
    }

    /**
     * Verifica se o tipo de oração contém determinada palavra-chave
     * @param keyword Palavra-chave a ser verificada
     * @return True se contém a palavra-chave
     */
    public boolean hasKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        return this.keywords.toLowerCase().contains(keyword.toLowerCase().trim());
    }

    /**
     * Retorna todos os tipos de oração como um array de strings para exibição
     * @return Array de nomes de exibição
     */
    public static String[] getAllDisplayNames() {
        return java.util.Arrays.stream(values())
                .map(PrayerType::getDisplayName)
                .toArray(String[]::new);
    }
}