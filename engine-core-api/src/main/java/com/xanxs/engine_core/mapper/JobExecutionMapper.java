package com.xanxs.engine_core.mapper;




import com.xanxs.engine_core.domain.entity.JobExecution;
import com.xanxs.engine_core.domain.entity.JobExecutionError;
import com.xanxs.engine_core.dto.response.JobExecutionErrorResponse;
import com.xanxs.engine_core.dto.response.JobExecutionResponse;
import com.xanxs.engine_core.dto.response.JobReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobExecutionMapper {

    JobExecutionResponse toResponse(JobExecution entity);

    JobExecutionErrorResponse toErrorResponse(JobExecutionError entity);

    List<JobExecutionErrorResponse> toErrorResponseList(List<JobExecutionError> errors);

    // El reporte se arma en el servicio con lógica adicional
    // el mapper solo mapea los campos simples
    @Mapping(target = "executionId", source = "id")
    @Mapping(target = "durationSeconds", ignore = true)
    @Mapping(target = "successRate", ignore = true)
    @Mapping(target = "errors", ignore = true)
    JobReportResponse toReportResponse(JobExecution entity);
}