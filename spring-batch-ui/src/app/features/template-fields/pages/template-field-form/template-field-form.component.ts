import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MaterialModule } from '../../../../shared/material/material.module';
import { FieldType } from '../../../../core/models/template-field.model';
import { Template } from '../../../../core/models/template.model';
import { TemplateService } from '../../../../core/services/template.service';
import { TemplateFieldService } from '../../../../core/services/template-field.service';

@Component({
  selector: 'app-template-field-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './template-field-form.component.html',
  styleUrl: './template-field-form.component.scss'
})
export class TemplateFieldFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);

  private readonly route = inject(ActivatedRoute);

  private readonly router = inject(Router);

  private readonly templateService = inject(TemplateService);

  private readonly templateFieldService = inject(TemplateFieldService);

  readonly fieldTypes = Object.values(FieldType);

  templates: Template[] = [];

  saving = false;

  loading = false;

  errorMessage = '';

  readonly form = this.fb.group({

    templateId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    name: [
      '',
      [
        Validators.required,
        Validators.maxLength(100)
      ]
    ],

    fieldType: [
      FieldType.STRING,
      Validators.required
    ],

    position: [
      1,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    length: [
      null as number | null,
      [
        Validators.min(1)
      ]
    ],

    required: [
      true
    ],

    defaultValue: [
      ''
    ],

    orderIndex: [
      null as number | null,
      [
        Validators.min(0)
      ]
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

    this.templateFieldService
      .create(
        templateId,
        {
          name: rawValue.name ?? '',
          fieldType: rawValue.fieldType ?? FieldType.STRING,
          position: rawValue.position ?? 1,
          length: rawValue.length ?? null,
          required: rawValue.required ?? true,
          defaultValue: rawValue.defaultValue || null,
          orderIndex: rawValue.orderIndex ?? null
        }
      )
      .subscribe({
        next: () => {
          this.router.navigate(
            ['/template-fields'],
            {
              queryParams: {
                templateId
              }
            }
          );
        },
        error: (error) => {
          console.error(
            'Error creating template field',
            error
          );
          this.errorMessage =
            'No fue posible crear el field.';
          this.saving = false;
        }
      });

  }

  cancel(): void {

    this.router.navigate(['/template-fields']);

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
