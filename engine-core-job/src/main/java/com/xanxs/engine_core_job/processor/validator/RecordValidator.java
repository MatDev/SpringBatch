package com.xanxs.engine_core_job.processor.validator;

import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;

public interface RecordValidator {
    void validate(ProcessedRecord record, TemplateConfig template);
}
