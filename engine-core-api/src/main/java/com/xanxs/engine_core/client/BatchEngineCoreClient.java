package com.xanxs.engine_core.client;

// client/BatchEngineCoreClient.java
import com.xanxs.engine_core.dto.request.CoreJobRequest;
import com.xanxs.engine_core.dto.response.CoreJobResponse;
import com.xanxs.engine_core.dto.response.CoreJobStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "batch-engine-core",
        url = "${batch.engine.core.url}"
)
public interface BatchEngineCoreClient {

    @PostMapping("/api/v1/jobs/run")
    CoreJobResponse runJob(@RequestBody CoreJobRequest request);

    @GetMapping("/api/v1/jobs/{coreExecutionId}/status")
    CoreJobStatusResponse getStatus(@PathVariable String coreExecutionId);
}