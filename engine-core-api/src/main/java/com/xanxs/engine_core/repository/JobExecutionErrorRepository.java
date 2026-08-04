package com.xanxs.engine_core.repository;

// repository/JobExecutionErrorRepository.java
import com.xanxs.engine_core.domain.entity.JobExecutionError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutionErrorRepository extends JpaRepository<JobExecutionError, Long> {

    List<JobExecutionError> findAllByJobExecutionIdOrderByLineNumberAsc(Long jobExecutionId);

    long countByJobExecutionId(Long jobExecutionId);
}