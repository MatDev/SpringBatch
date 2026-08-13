import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { MaterialModule } from '../../../../shared/material/material.module';
import {
  JobExecution,
  JobExecutionStatus
} from '../../../../core/models/job.model';
import { Template } from '../../../../core/models/template.model';
import { JobService } from '../../../../core/services/job.service';
import { TemplateService } from '../../../../core/services/template.service';

@Component({
  selector: 'app-job-execution',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './job-execution.component.html',
  styleUrl: './job-execution.component.scss'
})
export class JobExecutionComponent implements OnInit {

  private readonly fb = inject(FormBuilder);

  private readonly jobService = inject(JobService);

  private readonly templateService = inject(TemplateService);

  templates: Template[] = [];

  jobs: JobExecution[] = [];

  loading = false;

  saving = false;

  errorMessage = '';

  selectedStatus:
    | JobExecutionStatus
    | 'ALL' = 'ALL';

  readonly statusFilters: Array<
    JobExecutionStatus | 'ALL'
  > = [
    'ALL',
    JobExecutionStatus.PENDING,
    JobExecutionStatus.RUNNING,
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

  readonly form = this.fb.group({

    jobName: [
      '',
      [
        Validators.required
      ]
    ],

    templateId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    filePath: [
      '',
      [
        Validators.required
      ]
    ],

    requestedBy: [
      ''
    ]

  });

  ngOnInit(): void {

    this.loadTemplates();
    this.loadJobs();

  }

  execute(): void {

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const rawValue = this.form.getRawValue();
    const templateId = rawValue.templateId ?? 0;

    if (templateId <= 0) {
      this.errorMessage = 'Debes seleccionar un template.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    this.jobService
      .execute({
        jobName: rawValue.jobName ?? '',
        templateId,
        filePath: rawValue.filePath ?? '',
        requestedBy: rawValue.requestedBy || null
      })
      .subscribe({
        next: () => {
          this.saving = false;
          this.loadJobs();
        },
        error: (error) => {
          console.error(
            'Error executing job',
            error
          );
          this.errorMessage =
            'No fue posible ejecutar el job.';
          this.saving = false;
        }
      });

  }

  refreshStatus(id: number): void {

    this.jobService
      .refreshStatus(id)
      .subscribe({
        next: () => {
          this.loadJobs();
        },
        error: (error) => {
          console.error(
            'Error refreshing job status',
            error
          );
          this.errorMessage =
            'No fue posible refrescar el estado.';
        }
      });

  }

  onStatusChange(event: Event): void {

    this.selectedStatus = (event.target as HTMLSelectElement)
      .value as JobExecutionStatus | 'ALL';

    this.loadJobs();

  }

  private loadTemplates(): void {

    this.templateService
      .getAll()
      .subscribe({
        next: (templates) => {
          this.templates = templates;

          if (templates.length > 0) {
            this.form.patchValue({
              templateId: templates[0].id
            });
          }
        },
        error: (error) => {
          console.error(
            'Error loading templates',
            error
          );
          this.errorMessage =
            'No fue posible cargar los templates.';
        }
      });

  }

  private loadJobs(): void {

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
            'Error loading jobs',
            error
          );
          this.errorMessage =
            'No fue posible cargar los jobs.';
          this.loading = false;
        }
      });

  }

}
