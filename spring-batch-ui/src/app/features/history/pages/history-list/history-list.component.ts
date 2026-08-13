import { Component, OnInit, inject } from '@angular/core';

import { MaterialModule } from '../../../../shared/material/material.module';
import {
  JobExecution,
  JobExecutionStatus,
  JobReport
} from '../../../../core/models/job.model';
import { JobService } from '../../../../core/services/job.service';

@Component({
  selector: 'app-history-list',
  standalone: true,
  imports: [
    MaterialModule
  ],
  templateUrl: './history-list.component.html',
  styleUrl: './history-list.component.scss'
})
export class HistoryListComponent implements OnInit {

  private readonly jobService = inject(JobService);

  jobs: JobExecution[] = [];

  selectedReport: JobReport | null = null;

  loading = false;

  loadingReport = false;

  errorMessage = '';

  selectedStatus:
    | JobExecutionStatus
    | 'ALL' = JobExecutionStatus.COMPLETED;

  readonly statusFilters: Array<
    JobExecutionStatus | 'ALL'
  > = [
    'ALL',
    JobExecutionStatus.COMPLETED,
    JobExecutionStatus.FAILED,
    JobExecutionStatus.STOPPED
  ];

  displayedColumns: string[] = [
    'id',
    'jobName',
    'templateId',
    'status',
    'startedAt',
    'finishedAt',
    'actions'
  ];

  ngOnInit(): void {

    this.loadHistory();

  }

  onStatusChange(event: Event): void {

    this.selectedStatus = (event.target as HTMLSelectElement)
      .value as JobExecutionStatus | 'ALL';

    this.selectedReport = null;
    this.loadHistory();

  }

  loadReport(executionId: number): void {

    this.loadingReport = true;

    this.jobService
      .getReport(executionId)
      .subscribe({
        next: (report) => {
          this.selectedReport = report;
          this.loadingReport = false;
        },
        error: (error) => {
          console.error(
            'Error loading report',
            error
          );
          this.errorMessage =
            'No fue posible cargar el reporte.';
          this.loadingReport = false;
        }
      });

  }

  private loadHistory(): void {

    this.loading = true;
    this.errorMessage = '';

    const status =
      this.selectedStatus === 'ALL'
        ? undefined
        : this.selectedStatus;

    this.jobService
      .getAll(status)
      .subscribe({
        next: (jobs) => {
          this.jobs = jobs;
          this.loading = false;
        },
        error: (error) => {
          console.error(
            'Error loading history',
            error
          );
          this.errorMessage =
            'No fue posible cargar el historial.';
          this.loading = false;
        }
      });

  }

}
