package com.xanxs.engine_core.domain.entity;


import com.xanxs.engine_core.domain.enums.JobExecutionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bte_job_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobExecutionStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "total_records")
    private Integer totalRecords = 0;

    @Column(name = "success_records")
    private Integer successRecords = 0;

    @Column(name = "error_records")
    private Integer errorRecords = 0;

    @Column(name = "requested_by", length = 100)
    private String requestedBy;

    @Column(name = "core_execution_id", length = 100)
    private String coreExecutionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jobExecution",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<JobExecutionError> errors = new ArrayList<>();

    public void addError(JobExecutionError error) {
        errors.add(error);
        error.setJobExecution(this);
    }
}