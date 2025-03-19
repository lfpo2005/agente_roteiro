package br.com.devluisoliveira.agenteroteiro.core.domain.entity;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.AgentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_CONTENT_GENERATION")
public class ContentGeneration {

    @Id
    private UUID contentId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgentType agentType;

    @Lob
    @Column(nullable = false)
    private String text;

    @Lob
    private String textShort;

    @Lob
    private String description;

    @Lob
    private String audio;

    @Column(length = 600)
    private String tags;

    @Lob
    @Column(name = "additional_content")
    private String additionalContent;

    @Column(name = "prompt_used", length = 1000)
    private String promptUsed;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}