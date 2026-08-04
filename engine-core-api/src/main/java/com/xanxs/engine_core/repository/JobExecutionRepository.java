package com.xanxs.engine_core.repository;


import com.xanxs.engine_core.domain.entity.JobExecution;
import com.xanxs.engine_core.domain.enums.JobExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findAllByOrderByCreatedAtDesc();

    List<JobExecution> findAllByStatusOrderByCreatedAtDesc(JobExecutionStatus status);

    List<JobExecution> findAllByTemplateIdOrderByCreatedAtDesc(Long templateId);

    // Carga con errores
    @Query("""
        SELECT j FROM JobExecution j
        LEFT JOIN FETCH j.errors
        WHERE j.id = :id
    """)
    Optional<JobExecution> findByIdWithErrors(Long id);
}
