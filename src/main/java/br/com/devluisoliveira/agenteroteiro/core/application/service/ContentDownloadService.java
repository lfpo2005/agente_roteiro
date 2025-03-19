package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Serviço para geração e download de conteúdos em formato de pacote
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentDownloadService {

    private final ContentGenerationPortOut contentGenerationPortOut;
    private final SrtConverterService srtConverterService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    /**
     * Gera um pacote ZIP contendo todos os arquivos do conteúdo:
     * - Pasta de textos: descrição, roteiro, tags, SRT
     * - Pasta de áudio: arquivo de áudio (se disponível)
     *
     * @param contentId ID do conteúdo
     * @return Resource contendo o pacote ZIP e o nome do arquivo a ser usado
     * @throws Exception Se ocorrer um erro na geração do pacote
     */
    public Resource generateContentPackage(UUID contentId) throws Exception {
        log.info("[ContentDownloadService.generateContentPackage] - Gerando pacote para conteúdo ID: {}", contentId);

        ContentGeneration content = contentGenerationPortOut.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            // Criar pasta de textos
            createTextFiles(zipOut, content);

            // Criar pasta de áudio (se houver)
            if (content.getAudio() != null && !content.getAudio().isEmpty()) {
                createAudioFiles(zipOut, content);
            }

            zipOut.finish();
            zipOut.close();

            log.info("[ContentDownloadService.generateContentPackage] - Pacote gerado com sucesso, tamanho: {} bytes",
                    baos.size());

            return new ByteArrayResource(baos.toByteArray());
        }
    }

    /**
     * Gera o nome de arquivo para o pacote baseado no título do conteúdo
     *
     * @param content O conteúdo gerado
     * @return Nome de arquivo sanitizado
     */
    public String generatePackageFilename(ContentGeneration content) {
        // Sanitizar o título para uso seguro em nomes de arquivo
        String sanitizedTitle = content.getTitle()
                .replaceAll("[^a-zA-Z0-9áàâãéèêíìóòôõúùüçÁÀÂÃÉÈÊÍÌÓÒÔÕÚÙÜÇ\\s-]", "_") // Substitui caracteres especiais
                .replaceAll("\\s+", "_") // Substitui espaços por underscores
                .trim();

        // Limitar o tamanho do título no nome do arquivo (evitar nomes muito longos)
        if (sanitizedTitle.length() > 50) {
            sanitizedTitle = sanitizedTitle.substring(0, 50);
        }

        // Adicionar data de criação e ID parcial para garantir unicidade
        String createdDate = content.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String idPart = content.getContentId().toString().substring(0, 8);

        return sanitizedTitle + "_" + createdDate + "_" + idPart + ".zip";
    }

    /**
     * Cria os arquivos de texto no pacote ZIP
     */
    private void createTextFiles(ZipOutputStream zipOut, ContentGeneration content) throws IOException {
        String folderPrefix = "textos/";
        String createdDate = content.getCreatedAt().format(DATE_FORMATTER);

        // Arquivo de descrição
        if (content.getDescription() != null && !content.getDescription().isEmpty()) {
            addTextFile(zipOut, folderPrefix + "descricao.txt", content.getDescription());
        }

        // Arquivo de roteiro
        if (content.getText() != null && !content.getText().isEmpty()) {
            addTextFile(zipOut, folderPrefix + "roteiro.txt", content.getText());

            // Arquivo SRT
            String srtContent = srtConverterService.convertToSrt(content.getText());
            addTextFile(zipOut, folderPrefix + "roteiro.srt", srtContent);
        }

        // Arquivo de tags
        if (content.getTags() != null && !content.getTags().isEmpty()) {
            addTextFile(zipOut, folderPrefix + "tags.txt", content.getTags());
        }

        // Versão curta (se disponível)
        if (content.getTextShort() != null && !content.getTextShort().isEmpty()) {
            addTextFile(zipOut, folderPrefix + "versao_curta.txt", content.getTextShort());
        }

        // Arquivo de metadados
        StringBuilder metadata = new StringBuilder();
        metadata.append("Título: ").append(content.getTitle()).append("\n");
        metadata.append("Tipo de Agente: ").append(content.getAgentType()).append("\n");
        metadata.append("Data de Criação: ").append(createdDate).append("\n");

        addTextFile(zipOut, folderPrefix + "metadados.txt", metadata.toString());
    }

    /**
     * Cria os arquivos de áudio no pacote ZIP
     */
    private void createAudioFiles(ZipOutputStream zipOut, ContentGeneration content) throws IOException {
        String folderPrefix = "audio/";

        // Aqui assumimos que o áudio está em formato Base64
        // Em um cenário real, você pode precisar ajustar este código
        // dependendo de como o áudio é armazenado
        if (content.getAudio() != null && content.getAudio().startsWith("data:audio")) {
            String base64Audio = content.getAudio().split(",")[1];
            byte[] audioBytes = java.util.Base64.getDecoder().decode(base64Audio);

            addBinaryFile(zipOut, folderPrefix + "audio.mp3", audioBytes);
        } else if (content.getAudio() != null) {
            // Assumindo que é uma URL ou caminho para o áudio
            addTextFile(zipOut, folderPrefix + "audio_url.txt", content.getAudio());
        }
    }

    /**
     * Adiciona um arquivo de texto ao ZIP
     */
    private void addTextFile(ZipOutputStream zipOut, String filename, String content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zipOut.putNextEntry(zipEntry);
        zipOut.write(content.getBytes(StandardCharsets.UTF_8));
        zipOut.closeEntry();
    }

    /**
     * Adiciona um arquivo binário ao ZIP
     */
    private void addBinaryFile(ZipOutputStream zipOut, String filename, byte[] content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zipOut.putNextEntry(zipEntry);
        zipOut.write(content);
        zipOut.closeEntry();
    }
}