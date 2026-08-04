package com.xanxs.engine_core_job.domain.repository;


import com.xanxs.engine_core_job.domain.entity.CoreExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreExecutionRepository extends JpaRepository<CoreExecution, String> {
}