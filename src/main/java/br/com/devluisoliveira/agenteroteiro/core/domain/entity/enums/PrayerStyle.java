package br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums;

import lombok.Getter;

/**
 * Enum que define os estilos de linguagem e abordagem para orações
 */
@Getter
public enum PrayerStyle {

    POETIC(
            "Poético",
            "Estilo com linguagem lírica, metafórica e ricamente expressiva, semelhante aos Salmos.",
            "lírico, metafórico, expressivo, imagético, elaborado"),

    BIBLICAL(
            "Bíblico",
            "Estilo que incorpora extensivamente citações e linguagem bíblica, semelhante às orações apostólicas.",
            "escritural, apostólico, citações, tradicional, fundamentado"),

    CONTEMPLATIVE(
            "Contemplativo",
            "Estilo reflexivo, meditativo e introspectivo, focado na consciência da presença divina.",
            "meditativo, reflexivo, silencioso, profundo, consciente"),

    CONVERSATIONAL(
            "Conversacional",
            "Estilo informal e dialógico, como uma conversa íntima e autêntica com Deus.",
            "informal, dialógico, natural, cotidiano, simples"),

    DECLARATIVE(
            "Declarativo",
            "Estilo assertivo com declarações de fé, promessas e decretos espirituais.",
            "assertivo, profético, confiante, proclamativo, imperativo"),

    PASTORAL(
            "Pastoral",
            "Estilo acolhedor, reconfortante e orientador, com tom de cuidado e aconselhamento.",
            "acolhedor, cuidadoso, empático, reconfortante, orientador"),

    LITURGICAL(
            "Litúrgico",
            "Estilo formal e estruturado, inspirado nas tradições litúrgicas das igrejas.",
            "formal, tradicional, estruturado, cerimonial, reverente");

    private final String displayName;
    private final String description;
    private final String keywords;

    PrayerStyle(String displayName, String description, String keywords) {
        this.displayName = displayName;
        this.description = description;
        this.keywords = keywords;
    }

    /**
     * Encontra um estilo de oração pelo nome de exibição
     * @param displayName Nome de exibição
     * @return PrayerStyle correspondente ou null se não encontrado
     */
    public static PrayerStyle findByDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        for (PrayerStyle style : values()) {
            if (style.getDisplayName().equalsIgnoreCase(displayName.trim())) {
                return style;
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
     * Retorna todos os estilos de oração como um array de strings para exibição
     * @return Array de nomes de exibição
     */
    public static String[] getAllDisplayNames() {
        return java.util.Arrays.stream(values())
                .map(PrayerStyle::getDisplayName)
                .toArray(String[]::new);
    }
}
