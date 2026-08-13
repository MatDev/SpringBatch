import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MaterialModule } from '../../../../shared/material/material.module';
import {
  DestinationType
} from '../../../../core/models/destination.model';
import { Template } from '../../../../core/models/template.model';
import { DestinationService } from '../../../../core/services/destination.service';
import { TemplateService } from '../../../../core/services/template.service';

@Component({
  selector: 'app-destination-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './destination-form.component.html',
  styleUrl: './destination-form.component.scss'
})
export class DestinationFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);

  private readonly route = inject(ActivatedRoute);

  private readonly router = inject(Router);

  private readonly templateService = inject(TemplateService);

  private readonly destinationService = inject(DestinationService);

  readonly destinationTypes =
    Object.values(DestinationType);

  templates: Template[] = [];

  loading = false;

  saving = false;

  errorMessage = '';

  readonly form = this.fb.group({

    templateId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    destinationType: [
      DestinationType.POSTGRESQL,
      Validators.required
    ],

    name: [
      '',
      [
        Validators.required,
        Validators.maxLength(100)
      ]
    ],

    targetTable: [
      ''
    ],

    targetCollection: [
      ''
    ],

    targetTopic: [
      ''
    ],

    targetPath: [
      ''
    ],

    targetUrl: [
      ''
    ],

    active: [
      true
    ]

  });

  ngOnInit(): void {

    this.loadTemplates();

  }

  save(): void {

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

    this.destinationService
      .create(
        templateId,
        {
          destinationType:
            rawValue.destinationType ?? DestinationType.POSTGRESQL,
          name: rawValue.name ?? '',
          targetTable: rawValue.targetTable || null,
          targetCollection: rawValue.targetCollection || null,
          targetTopic: rawValue.targetTopic || null,
          targetPath: rawValue.targetPath || null,
          targetUrl: rawValue.targetUrl || null,
          active: rawValue.active ?? true
        }
      )
      .subscribe({
        next: () => {
          this.router.navigate(
            ['/destinations'],
            {
              queryParams: {
                templateId
              }
            }
          );
        },
        error: (error) => {
          console.error(
            'Error creating destination',
            error
          );
          this.errorMessage =
            'No fue posible crear el destination.';
          this.saving = false;
        }
      });

  }

  cancel(): void {

    this.router.navigate(['/destinations']);

  }

  private loadTemplates(): void {

    this.loading = true;
    this.errorMessage = '';

    this.templateService
      .getAll()
      .subscribe({
        next: (templates) => {
          this.templates = templates;
          this.loading = false;

          if (templates.length === 0) {
            return;
          }

          const queryTemplateId = this.parseTemplateId(
            this.route.snapshot.queryParamMap.get('templateId')
          );

          const selectedTemplate =
            templates.find(
              template => template.id === queryTemplateId
            ) ?? templates[0];

          this.form.patchValue({
            templateId: selectedTemplate.id
          });
        },
        error: (error) => {
          console.error(
            'Error loading templates',
            error
          );
          this.errorMessage =
            'No fue posible cargar los templates.';
          this.loading = false;
        }
      });

  }

  private parseTemplateId(
    value: string | null
  ): number | null {

    if (!value) {
      return null;
    }

    const parsed = Number(value);

    if (!Number.isInteger(parsed) || parsed <= 0) {
      return null;
    }

    return parsed;

  }

}
