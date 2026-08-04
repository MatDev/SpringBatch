package com.xanxs.engine_core_job.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bte_core_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreExecution {

    @Id
    @Column(length = 100)
    private String id;
    // UUID generado al crear la ejecución

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false, length = 20)
    private String status;
    // RUNNING | COMPLETED | FAILED | STOPPED

    @Column(name = "total_records")
    private Integer totalRecords = 0;

    @Column(name = "success_records")
    private Integer successRecords = 0;

    @Column(name = "error_records")
    private Integer errorRecords = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @OneToMany(mappedBy = "execution",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<CoreError> errors = new ArrayList<>();

    public void addError(CoreError error) {
        errors.add(error);
        error.setExecution(this);
    }
}