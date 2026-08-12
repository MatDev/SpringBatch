import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';

import { MaterialModule } from '../../../../shared/material/material.module';

import {
  FileType,
  OnErrorBehavior,
  TemplateRequest
} from '../../../../core/models/template.model';

import {
  TemplateService
} from '../../../../core/services/template.service';

@Component({
  selector: 'app-template-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './template-form.component.html',
  styleUrl: './template-form.component.scss'
})
export class TemplateFormComponent {

  private readonly fb = inject(FormBuilder);

  private readonly router = inject(Router);

  private readonly templateService =
    inject(TemplateService);

  readonly fileTypes = Object.values(FileType);

  readonly errorBehaviors =
    Object.values(OnErrorBehavior);

  saving = false;

  errorMessage = '';

  readonly form = this.fb.nonNullable.group({

    name: [
      '',
      [
        Validators.required,
        Validators.maxLength(100)
      ]
    ],

    description: [
      '',
      [
        Validators.maxLength(500)
      ]
    ],

    fileType: [
      FileType.DELIMITED,
      Validators.required
    ],

    separator: [
      ',',
      [
        Validators.maxLength(10)
      ]
    ],

    encoding: [
      'UTF-8',
      [
        Validators.required,
        Validators.maxLength(50)
      ]
    ],

    skipEmptyLines: [
      true
    ],

    onError: [
      OnErrorBehavior.STOP,
      Validators.required
    ],

    active: [
      true
    ]

  });

  save(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;

    }

    this.saving = true;

    this.errorMessage = '';

    const request: TemplateRequest = {
      ...this.form.getRawValue()
    };

    this.templateService
      .create(request)
      .subscribe({

        next: () => {

          this.router.navigate([
            '/templates'
          ]);

        },

        error: (error) => {

          console.error(
            'Error creating template',
            error
          );

          this.errorMessage =
            'No fue posible crear el template.';

          this.saving = false;

        }

      });

  }

  cancel(): void {

    this.router.navigate([
      '/templates'
    ]);

  }

}