package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.application.service.ContentDownloadService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.ContentPersistenceService;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentSummaryDto;
import br.com.devluisoliveira.agenteroteiro.shared.utils.SecurityUtil;
import br.com.devluisoliveira.agenteroteiro.shared.validation.CustomOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Meus Conteúdos", description = "Endpoints para gerenciar conteúdos do usuário autenticado")
@RequestMapping("/my-content")
@PreAuthorize("hasAnyRole('USER')")
public class UserContentController {

    private final ContentPersistenceService contentPersistenceService;
    private final ContentDownloadService contentDownloadService;
    private final SecurityUtil securityUtil;

    @CustomOperation(summary = "Listar resumo dos meus conteúdos")
    @ApiResponse(responseCode = "200", description = "Lista paginada de resumos de conteúdos do usuário",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    @GetMapping
    public ResponseEntity<Page<ContentSummaryDto>> getMyContentSummary(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        User currentUser = securityUtil.getLoggedInUser();
        log.info("[UserContentController.getMyContentSummary] - Buscando resumos de conteúdos para usuário: {}",
                currentUser.getUsername());

        // Retorna apenas um resumo dos conteúdos (metadados básicos)
        Page<ContentSummaryDto> contentSummary = contentPersistenceService.getUserContentSummary(currentUser, pageable);
        return ResponseEntity.ok(contentSummary);
    }

    @CustomOperation(summary = "Obter detalhes de um conteúdo específico")
    @ApiResponse(responseCode = "200", description = "Detalhes completos do conteúdo",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGeneration.class))})
    @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado")
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentGeneration> getMyContentById(@PathVariable UUID contentId) {
        User currentUser = securityUtil.getLoggedInUser();
        log.info("[UserContentController.getMyContentById] - Buscando conteúdo ID: {} para usuário: {}",
                contentId, currentUser.getUsername());

        return contentPersistenceService.getUserContentById(contentId, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CustomOperation(summary = "Baixar pacote de conteúdo (textos e áudio)")
    @ApiResponse(responseCode = "200", description = "Pacote de conteúdo disponível para download")
    @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado")
    @GetMapping("/{contentId}/download")
    public ResponseEntity<Resource> downloadContentPackage(@PathVariable UUID contentId) {
        User currentUser = securityUtil.getLoggedInUser();
        log.info("[UserContentController.downloadContentPackage] - Solicitando download de pacote para conteúdo ID: {} do usuário: {}",
                contentId, currentUser.getUsername());

        try {
            // Verificar se o conteúdo pertence ao usuário
            Optional<ContentGeneration> contentOpt = contentPersistenceService.getUserContentById(contentId, currentUser);
            if (contentOpt.isEmpty()) {
                log.warn("[UserContentController.downloadContentPackage] - Conteúdo não pertence ao usuário ou não existe");
                return ResponseEntity.notFound().build();
            }

            ContentGeneration content = contentOpt.get();

            // Gerar o pacote de download
            Resource contentPackage = contentDownloadService.generateContentPackage(contentId);

            // Gerar nome de arquivo baseado no título do conteúdo
            String filename = contentDownloadService.generatePackageFilename(content);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(contentPackage);

        } catch (Exception e) {
            log.error("[UserContentController.downloadContentPackage] - Erro ao gerar pacote de download: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @CustomOperation(summary = "Excluir um conteúdo")
    @ApiResponse(responseCode = "204", description = "Conteúdo excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Conteúdo não encontrado")
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteMyContent(@PathVariable UUID contentId) {
        User currentUser = securityUtil.getLoggedInUser();
        log.info("[UserContentController.deleteMyContent] - Excluindo conteúdo ID: {} para usuário: {}",
                contentId, currentUser.getUsername());

        boolean deleted = contentPersistenceService.deleteUserContent(contentId, currentUser);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CustomOperation(summary = "Contar meus conteúdos")
    @ApiResponse(responseCode = "200", description = "Total de conteúdos do usuário")
    @GetMapping("/count")
    public ResponseEntity<Long> countMyContent() {
        User currentUser = securityUtil.getLoggedInUser();
        Long count = contentPersistenceService.countUserContent(currentUser);
        return ResponseEntity.ok(count);
    }
}