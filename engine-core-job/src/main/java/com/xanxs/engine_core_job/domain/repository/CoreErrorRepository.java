package com.xanxs.engine_core_job.domain.repository;


import com.xanxs.engine_core_job.domain.entity.CoreError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoreErrorRepository extends JpaRepository<CoreError, Long> {

    List<CoreError> findAllByExecutionIdOrderByLineNumberAsc(String executionId);
}
