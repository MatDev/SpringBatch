package com.xanxs.engine_core_job.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bte_core_error")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private CoreExecution execution;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "line_content", columnDefinition = "TEXT")
    private String lineContent;

    @Column(name = "error_description", nullable = false, length = 500)
    private String errorDescription;

    @Column(name = "exception_detail", columnDefinition = "TEXT")
    private String exceptionDetail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}