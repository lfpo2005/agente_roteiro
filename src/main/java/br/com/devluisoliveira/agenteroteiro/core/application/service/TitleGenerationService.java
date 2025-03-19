//package br.com.devluisoliveira.agenteroteiro.core.application.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * Serviço para geração de títulos otimizados para conteúdos
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class TitleGenerationService {
//
//        private final OpenAIService openAIService;
//
//        /**
//         * Gera títulos para um conteúdo baseado nos parâmetros fornecidos
//         *
//         * @param tema         Tema principal do conteúdo
//         * @param estilo       Estilo do conteúdo
//         * @param idioma       Idioma (pt_BR, en, es_MX, etc)
//         * @param observacoes  Observações adicionais (opcional)
//         * @return Lista de títulos gerados
//         */
//        public List<String> generateTitles(String tema, String estilo, String idioma, String observacoes) {
//                log.info("Gerando títulos para tema: {}, estilo: {}, idioma: {}", tema, estilo, idioma);
//
//                boolean hasObservacoes = observacoes != null && !observacoes.trim().isEmpty();
//
//                // Construir prompt otimizado com suporte ao idioma
//                String prompt = PromptBuilder.buildTitlePrompt(
//                        tema,
//                        estilo,
//                        idioma,
//                        hasObservacoes,
//                        observacoes);
//
//                // Chamar OpenAI API
//                List<String> titles = openAIService.generateTitles(prompt);
//
//                log.info("Gerados {} títulos com sucesso", titles.size());
//
//                return titles;
//        }
//
//        /**
//         * Gera títulos simplificando a chamada (sem observações)
//         */
//        public List<String> generateTitles(String tema, String estilo, String idioma) {
//                return generateTitles(tema, estilo, idioma, null);
//        }
//
//        /**
//         * Seleciona o melhor título baseado em critérios predefinidos
//         *
//         * @param titles       Lista de títulos gerados
//         * @param tema         Tema do conteúdo
//         * @param estilo       Estilo do conteúdo
//         * @return O título selecionado
//         */
//        public String selectBestTitle(List<String> titles, String tema, String estilo) {
//                if (titles == null || titles.isEmpty()) {
//                        throw new IllegalArgumentException("Nenhum título foi gerado");
//                }
//
//                // Por enquanto, implementação simples que retorna o primeiro título
//                // Em uma implementação mais avançada, você pode adicionar critérios de seleção
//                return titles.get(0);
//        }
//
//        /**
//         * Gera e seleciona o melhor título em uma única operação
//         */
//        public String generateAndSelectBestTitle(String tema, String estilo, String idioma, String observacoes) {
//                List<String> titles = generateTitles(tema, estilo, idioma, observacoes);
//                return selectBestTitle(titles, tema, estilo);
//        }
//}