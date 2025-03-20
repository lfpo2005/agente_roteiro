package br.com.devluisoliveira.agenteroteiro.core.application.service.agentStyle;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PhilosopherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para gerenciar estilos e características específicas de cada filósofo estoico.
 * Utilizado para personalizar os prompts e conteúdo gerado de acordo com o filósofo selecionado.
 */
@Service
@Slf4j
public class PhilosopherStyleService {

    private static final Map<String, String> PHILOSOPHER_STYLES = new HashMap<>();

    static {
        initializePhilosopherStyles();
    }

    /**
     * Inicializa os estilos e características de cada filósofo estoico
     */
    private static void initializePhilosopherStyles() {
        // Primeiros filósofos estoicos (período antigo)
        PHILOSOPHER_STYLES.put("Zenão de Cítio",
                "- Tom fundacional e didático\n" +
                        "- Linguagem direta e concisa\n" +
                        "- Referências frequentes ao conceito de 'viver de acordo com a natureza'\n" +
                        "- Ênfase na importância da virtude acima de tudo\n" +
                        "- Uso de analogias relacionadas à física e cosmologia");

        PHILOSOPHER_STYLES.put("Cleanthes",
                "- Estilo reverente e quase religioso\n" +
                        "- Referências frequentes à divindade e ordem cósmica\n" +
                        "- Uso de linguagem poética e hinos\n" +
                        "- Ênfase na aceitação do destino\n" +
                        "- Abordagem mais espiritual do estoicismo");

        PHILOSOPHER_STYLES.put("Crisipo de Solis",
                "- Estilo lógico e estruturado\n" +
                        "- Argumentação rigorosa e sistemática\n" +
                        "- Explicações detalhadas de conceitos estoicos\n" +
                        "- Uso de definições precisas e distinções conceituais\n" +
                        "- Abordagem enciclopédica dos temas");

        // Filósofos do período médio
        PHILOSOPHER_STYLES.put("Panécio de Rodes",
                "- Tom moderado e pragmático\n" +
                        "- Adaptação do estoicismo para a elite romana\n" +
                        "- Incorporação de elementos do platonismo e aristotelismo\n" +
                        "- Ênfase em deveres sociais e vida política\n" +
                        "- Abordagem mais flexível dos princípios estoicos");

        PHILOSOPHER_STYLES.put("Posidônio de Apameia",
                "- Estilo científico e investigativo\n" +
                        "- Integração de conhecimentos de geografia, história e astronomia\n" +
                        "- Interesse por explicações causais de fenômenos\n" +
                        "- Abordagem empírica e observacional\n" +
                        "- Ênfase na conexão entre cosmos e humanidade");

        // Filósofos do período romano
        PHILOSOPHER_STYLES.put("Sêneca",
                "- Tom equilibrado entre intelectual e acessível\n" +
                        "- Estilo elegante e retórico\n" +
                        "- Uso abundante de exemplos, analogias e histórias ilustrativas\n" +
                        "- Abordagem psicológica profunda das emoções e vícios\n" +
                        "- Ênfase em conselhos práticos para a vida cotidiana\n" +
                        "- Reconhecimento das dificuldades reais na prática da filosofia");

        PHILOSOPHER_STYLES.put("Musônio Rufo",
                "- Estilo direto e focado na aplicação prática\n" +
                        "- Ênfase na educação moral e formação de caráter\n" +
                        "- Foco em temas como casamento, família e papéis sociais\n" +
                        "- Defesa da igualdade filosófica para homens e mulheres\n" +
                        "- Abordagem ascética da vida");

        PHILOSOPHER_STYLES.put("Epicteto",
                "- Tom direto, por vezes contundente\n" +
                        "- Uso de diálogos hipotéticos e perguntas retóricas\n" +
                        "- Linguagem coloquial e acessível\n" +
                        "- Ênfase constante na distinção entre o que está e o que não está sob nosso controle\n" +
                        "- Abordagem prática e exercícios mentais concretos\n" +
                        "- Uso de analogias e exemplos cotidianos");

        PHILOSOPHER_STYLES.put("Marco Aurélio",
                "- Tom introspectivo e meditativo\n" +
                        "- Estilo de anotações pessoais e autorreprovação\n" +
                        "- Uso de máximas e lembretes curtos\n" +
                        "- Perspectiva cósmica e visão da interconexão de todas as coisas\n" +
                        "- Ênfase no dever e na responsabilidade social\n" +
                        "- Reflexão constante sobre a mortalidade e impermanência");

        // Outros filósofos
        PHILOSOPHER_STYLES.put("Hierocles",
                "- Foco nos círculos concêntricos de preocupação ética\n" +
                        "- Abordagem da ética familiar e relações interpessoais\n" +
                        "- Estilo sistemático e estruturado\n" +
                        "- Ênfase nas obrigações sociais\n" +
                        "- Linguagem acessível e prática");

        PHILOSOPHER_STYLES.put("Atenodoro de Tarsus",
                "- Estilo moderado e diplomático\n" +
                        "- Foco no autocontrole e gestão da raiva\n" +
                        "- Aplicação da filosofia à política\n" +
                        "- Abordagem pragmática dos princípios estoicos\n" +
                        "- Ênfase na tranquilidade como objetivo");

        PHILOSOPHER_STYLES.put("Aristo de Quios",
                "- Estilo radical e direto\n" +
                        "- Rejeição de áreas teóricas da filosofia\n" +
                        "- Ênfase na virtude e indiferença a tudo mais\n" +
                        "- Abordagem minimalista dos princípios estoicos\n" +
                        "- Crítica à educação convencional");

        PHILOSOPHER_STYLES.put("Diogenes de Babilônia",
                "- Estilo didático e organizado\n" +
                        "- Foco na lógica e teoria da linguagem\n" +
                        "- Abordagem rigorosa da moralidade\n" +
                        "- Integração da dialética na filosofia estoica\n" +
                        "- Importância da educação filosófica");

        PHILOSOPHER_STYLES.put("Antipatro de Tarso",
                "- Estilo argumentativo e polêmico\n" +
                        "- Defesa do estoicismo contra críticas céticas\n" +
                        "- Refinamento da teoria dos deveres\n" +
                        "- Abordagem casuística da ética\n" +
                        "- Análise de casos particulares para princípios gerais");

        // Filósofo padrão (caso um nome não seja encontrado)
        PHILOSOPHER_STYLES.put("DEFAULT",
                "- Tom equilibrado entre teórico e prático\n" +
                        "- Linguagem clara e direta\n" +
                        "- Ênfase nas virtudes cardeais estoicas\n" +
                        "- Foco na aplicação prática dos princípios\n" +
                        "- Abordagem acessível dos conceitos filosóficos");
    }

    /**
     * Retorna o estilo específico para o filósofo estoico selecionado
     * @param philosopher nome do filósofo
     * @return string contendo o estilo e características do filósofo
     */
    public String getPhilosopherStyle(String philosopher) {
        if (philosopher == null || philosopher.trim().isEmpty()) {
            return PHILOSOPHER_STYLES.get("DEFAULT");
        }

        // Verificar correspondência exata
        if (PHILOSOPHER_STYLES.containsKey(philosopher)) {
            return PHILOSOPHER_STYLES.get(philosopher);
        }

        // Tentar encontrar por nome aproximado
        String normalizedPhilosopherName = normalizeString(philosopher);
        for (Map.Entry<String, String> entry : PHILOSOPHER_STYLES.entrySet()) {
            if (normalizeString(entry.getKey()).contains(normalizedPhilosopherName) ||
                    normalizedPhilosopherName.contains(normalizeString(entry.getKey()))) {
                log.info("Encontrada correspondência aproximada para '{}': '{}'", philosopher, entry.getKey());
                return entry.getValue();
            }
        }

        // Se não encontrar correspondência, retornar o estilo padrão
        log.warn("Filósofo não encontrado: '{}'. Usando estilo padrão.", philosopher);
        return PHILOSOPHER_STYLES.get("DEFAULT");
    }

    /**
     * Retorna o estilo específico para o filósofo estoico selecionado pelo enum
     * @param philosopherType enum do tipo de filósofo
     * @return string contendo o estilo e características do filósofo
     */
    public String getPhilosopherStyle(PhilosopherType philosopherType) {
        if (philosopherType == null) {
            return PHILOSOPHER_STYLES.get("DEFAULT");
        }

        return getPhilosopherStyle(philosopherType.getName());
    }

    /**
     * Normaliza uma string para comparação insensível a acentos e maiúsculas/minúsculas
     */
    private String normalizeString(String input) {
        if (input == null) return "";

        return input.toLowerCase()
                .replaceAll("[áàâã]", "a")
                .replaceAll("[éèê]", "e")
                .replaceAll("[íì]", "i")
                .replaceAll("[óòôõ]", "o")
                .replaceAll("[úù]", "u")
                .replaceAll("[ç]", "c")
                .trim();
    }

    /**
     * Personaliza o template de prompt com o estilo do filósofo escolhido
     * @param template template base do prompt
     * @param philosopherName nome do filósofo
     * @return template personalizado com o estilo do filósofo
     */
    public String customizeTemplateForPhilosopher(String template, String philosopherName) {
        String philosopherStyle = getPhilosopherStyle(philosopherName);
        return template.replace("{philosopherStyle}", philosopherStyle);
    }

    /**
     * Personaliza o template de prompt com o estilo do filósofo escolhido
     * @param template template base do prompt
     * @param philosopherType enum do tipo de filósofo
     * @return template personalizado com o estilo do filósofo
     */
    public String customizeTemplateForPhilosopher(String template, PhilosopherType philosopherType) {
        String philosopherStyle = getPhilosopherStyle(philosopherType);
        return template.replace("{philosopherStyle}", philosopherStyle);
    }
}