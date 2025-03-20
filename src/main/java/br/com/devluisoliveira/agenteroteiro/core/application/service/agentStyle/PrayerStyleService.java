package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para gerenciar características e estilos específicos de orações.
 * Utilizado para personalizar os prompts e conteúdo gerado de acordo com o tipo e estilo de oração selecionados.
 */
@Service
@Slf4j
public class PrayerStyleService {

    private static final Map<String, String> PRAYER_STYLE_CHARACTERISTICS = new HashMap<>();
    private static final Map<String, String> PRAYER_TYPE_CHARACTERISTICS = new HashMap<>();

    static {
        initializePrayerStyles();
        initializePrayerTypes();
    }

    /**
     * Inicializa as características de cada estilo de oração
     */
    private static void initializePrayerStyles() {
        // Estilos poéticos e líricos
        PRAYER_STYLE_CHARACTERISTICS.put("POETIC",
                "- Linguagem lírica e metafórica\n" +
                        "- Rico em imagens e simbolismo\n" +
                        "- Uso de estruturas poéticas como paralelismo\n" +
                        "- Expressões emocionais profundas\n" +
                        "- Tom elevado e contemplativo\n" +
                        "- Semelhante aos Salmos e literatura sapiencial");

        // Estilos baseados na Bíblia
        PRAYER_STYLE_CHARACTERISTICS.put("BIBLICAL",
                "- Abundante uso de citações bíblicas\n" +
                        "- Linguagem inspirada nos textos sagrados\n" +
                        "- Estrutura semelhante às orações apostólicas\n" +
                        "- Referências a personagens e narrativas bíblicas\n" +
                        "- Uso de terminologia teológica tradicional\n" +
                        "- Tom reverente e solene");

        // Estilos contemplativos
        PRAYER_STYLE_CHARACTERISTICS.put("CONTEMPLATIVE",
                "- Pausado e meditativo\n" +
                        "- Foco na presença divina\n" +
                        "- Uso de repetições significativas\n" +
                        "- Convites ao silêncio e quietude\n" +
                        "- Linguagem que estimula a reflexão interior\n" +
                        "- Conexão entre a experiência cotidiana e o divino");

        // Estilos conversacionais
        PRAYER_STYLE_CHARACTERISTICS.put("CONVERSATIONAL",
                "- Tom informal e pessoal\n" +
                        "- Diálogo íntimo com o divino\n" +
                        "- Linguagem contemporânea e acessível\n" +
                        "- Expressões autênticas e transparentes\n" +
                        "- Referências a situações cotidianas\n" +
                        "- Estilo que reflete uma amizade com Deus");

        // Estilos declarativos
        PRAYER_STYLE_CHARACTERISTICS.put("DECLARATIVE",
                "- Tom assertivo e confiante\n" +
                        "- Proclamações baseadas em promessas bíblicas\n" +
                        "- Uso de decretos espirituais\n" +
                        "- Linguagem de autoridade e convicção\n" +
                        "- Declarações no tempo presente\n" +
                        "- Afirmações de fé e vitória");

        // Estilos pastorais
        PRAYER_STYLE_CHARACTERISTICS.put("PASTORAL",
                "- Tom acolhedor e compassivo\n" +
                        "- Linguagem de cuidado e consolo\n" +
                        "- Atenção às necessidades emocionais\n" +
                        "- Abordagem curativa e terapêutica\n" +
                        "- Orientação espiritual prática\n" +
                        "- Equilíbrio entre verdade e graça");

        // Estilos litúrgicos
        PRAYER_STYLE_CHARACTERISTICS.put("LITURGICAL",
                "- Estrutura formal e tradicional\n" +
                        "- Elementos responsivos ou antifonais\n" +
                        "- Linguagem cerimonial e reverente\n" +
                        "- Uso de fórmulas tradicionais de oração\n" +
                        "- Referências ao calendário litúrgico\n" +
                        "- Tom solene e comunitário");

        // Estilo padrão (caso nenhum estilo específico seja selecionado)
        PRAYER_STYLE_CHARACTERISTICS.put("DEFAULT",
                "- Equilíbrio entre profundidade e acessibilidade\n" +
                        "- Linguagem clara e direta\n" +
                        "- Tom caloroso e inspirador\n" +
                        "- Fundamentação bíblica\n" +
                        "- Aplicações práticas para a vida\n" +
                        "- Relevância cultural contemporânea");
    }

    /**
     * Inicializa as características de cada tipo de oração
     */
    private static void initializePrayerTypes() {
        // Reflexão Bíblica
        PRAYER_TYPE_CHARACTERISTICS.put("BIBLICAL_REFLECTION",
                "- Baseada na análise de passagens bíblicas específicas\n" +
                        "- Foco na revelação de princípios espirituais\n" +
                        "- Aplicação prática das Escrituras\n" +
                        "- Contextualização histórica e teológica\n" +
                        "- Desenvolvimento de insights espirituais\n" +
                        "- Convite à transformação pessoal");

        // Intimidade Devocional
        PRAYER_TYPE_CHARACTERISTICS.put("DEVOTIONAL_INTIMACY",
                "- Foco no relacionamento pessoal com Deus\n" +
                        "- Expressões de amor e adoração\n" +
                        "- Linguagem de entrega e rendição\n" +
                        "- Busca por experiências profundas de comunhão\n" +
                        "- Expressão de desejos e anseios espirituais\n" +
                        "- Metáforas de proximidade e intimidade");

        // Declaração de Fé
        PRAYER_TYPE_CHARACTERISTICS.put("FAITH_DECLARATION",
                "- Baseada em promessas bíblicas específicas\n" +
                        "- Proclamações de vitória e conquista\n" +
                        "- Afirmações de identidade espiritual\n" +
                        "- Rejeição de mentiras e limitações\n" +
                        "- Decretos proféticos e visionários\n" +
                        "- Foco em resultados sobrenaturais");

        // Gratidão e Adoração
        PRAYER_TYPE_CHARACTERISTICS.put("GRATITUDE_WORSHIP",
                "- Expressões de agradecimento específicas\n" +
                        "- Reconhecimento dos atributos divinos\n" +
                        "- Celebração das obras e intervenções divinas\n" +
                        "- Exaltação e louvor\n" +
                        "- Foco na bondade e fidelidade de Deus\n" +
                        "- Linguagem de elevação e honra");

        // Consolo Pastoral
        PRAYER_TYPE_CHARACTERISTICS.put("PASTORAL_COMFORT",
                "- Linguagem de consolo e apoio\n" +
                        "- Abordagem de feridas emocionais e traumas\n" +
                        "- Promessas de restauração e cura\n" +
                        "- Reconhecimento da dor e sofrimento\n" +
                        "- Palavras de esperança em tempos difíceis\n" +
                        "- Orientação para momentos de crise");

        // Intercessão
        PRAYER_TYPE_CHARACTERISTICS.put("INTERCESSION",
                "- Foco em pessoas ou situações específicas\n" +
                        "- Petições detalhadas e estratégicas\n" +
                        "- Abordagem de questões familiares, comunitárias ou globais\n" +
                        "- Identificação com as necessidades dos outros\n" +
                        "- Convite à ação divina transformadora\n" +
                        "- Persistência e intensidade no clamor");

        // Arrependimento e Restauração
        PRAYER_TYPE_CHARACTERISTICS.put("REPENTANCE",
                "- Reconhecimento honesto do pecado\n" +
                        "- Expressões de contrição e arrependimento\n" +
                        "- Pedidos de perdão e purificação\n" +
                        "- Compromisso com mudança de comportamento\n" +
                        "- Busca por restauração de relacionamentos\n" +
                        "- Celebração da graça e misericórdia divina");

        // Tipo padrão (caso nenhum tipo específico seja selecionado)
        PRAYER_TYPE_CHARACTERISTICS.put("DEFAULT",
                "- Equilíbrio entre diversos elementos de oração\n" +
                        "- Invocação, gratidão, petição e adoração\n" +
                        "- Aplicação bíblica relevante\n" +
                        "- Conexão entre fé e vida prática\n" +
                        "- Expressão de necessidades e desejos\n" +
                        "- Foco na transformação espiritual");
    }

    /**
     * Retorna as características específicas para o estilo de oração selecionado
     * @param prayerStyle enum do estilo de oração
     * @return string contendo as características do estilo
     */
    public String getPrayerStyleCharacteristics(PrayerStyle prayerStyle) {
        if (prayerStyle == null) {
            return PRAYER_STYLE_CHARACTERISTICS.get("DEFAULT");
        }

        return PRAYER_STYLE_CHARACTERISTICS.getOrDefault(
                prayerStyle.name(),
                PRAYER_STYLE_CHARACTERISTICS.get("DEFAULT"));
    }

    /**
     * Retorna as características específicas para o tipo de oração selecionado
     * @param prayerType enum do tipo de oração
     * @return string contendo as características do tipo
     */
    public String getPrayerTypeCharacteristics(PrayerType prayerType) {
        if (prayerType == null) {
            return PRAYER_TYPE_CHARACTERISTICS.get("DEFAULT");
        }

        return PRAYER_TYPE_CHARACTERISTICS.getOrDefault(
                prayerType.name(),
                PRAYER_TYPE_CHARACTERISTICS.get("DEFAULT"));
    }

    /**
     * Método padronizado para obter o estilo de oração
     * Similar ao método getPhilosopherStyle do PhilosopherStyleService
     *
     * @param prayerStyle Estilo de oração
     * @return String com características do estilo
     */
    public String getPrayerStyle(PrayerStyle prayerStyle) {
        return getPrayerStyleCharacteristics(prayerStyle);
    }

    /**
     * Método padronizado para obter o estilo de oração combinado com o tipo
     *
     * @param prayerStyle Estilo de oração
     * @param prayerType Tipo de oração
     * @return String com características combinadas de estilo e tipo
     */
    public String getPrayerStyle(PrayerStyle prayerStyle, PrayerType prayerType) {
        return getCombinedPrayerCharacteristics(prayerStyle, prayerType);
    }

    /**
     * Combina características de estilo e tipo para criar um guia completo
     * @param prayerStyle estilo de oração selecionado
     * @param prayerType tipo de oração selecionado
     * @return string combinando características de estilo e tipo
     */
    public String getCombinedPrayerCharacteristics(PrayerStyle prayerStyle, PrayerType prayerType) {
        StringBuilder combined = new StringBuilder();

        combined.append("## CARACTERÍSTICAS DO ESTILO DE ORAÇÃO\n");
        combined.append(getPrayerStyleCharacteristics(prayerStyle));

        combined.append("\n\n## CARACTERÍSTICAS DO TIPO DE ORAÇÃO\n");
        combined.append(getPrayerTypeCharacteristics(prayerType));

        return combined.toString();
    }

    /**
     * Personaliza o template de prompt com as características do estilo e tipo de oração
     * @param template template base do prompt
     * @param prayerStyle estilo de oração
     * @param prayerType tipo de oração
     * @return template personalizado
     */
    public String customizeTemplateForPrayer(String template, PrayerStyle prayerStyle, PrayerType prayerType) {
        String characteristics = getCombinedPrayerCharacteristics(prayerStyle, prayerType);
        return template.replace("{prayerStyleCharacteristics}", characteristics);
    }
}