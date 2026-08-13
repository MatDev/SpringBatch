import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';
import { API } from '../constants/api.constant';
import {
  JobExecution,
  JobExecutionError,
  JobExecutionRequest,
  JobReport
} from '../models/job.model';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private readonly api = inject(ApiService);

  private readonly endpoint =
    `${API.ENGINE_API}${API.VERSION}/jobs`;

  execute(
    request: JobExecutionRequest
  ): Observable<JobExecution> {

    return this.api.post<JobExecution>(
      `${this.endpoint}/execute`,
      request
    );

  }

  getAll(status?: string): Observable<JobExecution[]> {

    return this.api.get<JobExecution[]>(
      this.endpoint,
      status
        ? {
          status
        }
        : undefined
    );

  }

  getById(id: number): Observable<JobExecution> {

    return this.api.get<JobExecution>(
      `${this.endpoint}/${id}`
    );

  }

  refreshStatus(id: number): Observable<JobExecution> {

    return this.api.patch<JobExecution>(
      `${this.endpoint}/${id}/refresh`,
      {}
    );

  }

  getErrors(id: number): Observable<JobExecutionError[]> {

    return this.api.get<JobExecutionError[]>(
      `${this.endpoint}/${id}/errors`
    );

  }

  getReport(id: number): Observable<JobReport> {

    return this.api.get<JobReport>(
      `${this.endpoint}/${id}/report`
    );

  }

}
