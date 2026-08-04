package com.xanxs.engine_core.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bte_job_execution_error")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id", nullable = false)
    private JobExecution jobExecution;

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