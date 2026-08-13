import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { MaterialModule } from '../../../../shared/material/material.module';
import { Template } from '../../../../core/models/template.model';
import { TemplateField } from '../../../../core/models/template-field.model';
import { TemplateService } from '../../../../core/services/template.service';
import { TemplateFieldService } from '../../../../core/services/template-field.service';

@Component({
  selector: 'app-template-field-list',
  standalone: true,
  imports: [
    RouterModule,
    MaterialModule
  ],
  templateUrl: './template-field-list.component.html',
  styleUrl: './template-field-list.component.scss'
})
export class TemplateFieldListComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);

  private readonly templateService = inject(TemplateService);

  private readonly templateFieldService = inject(TemplateFieldService);

  templates: Template[] = [];

  fields: TemplateField[] = [];

  selectedTemplateId: number | null = null;

  loading = false;

  errorMessage = '';

  displayedColumns: string[] = [
    'name',
    'fieldType',
    'position',
    'required',
    'actions'
  ];

  ngOnInit(): void {

    this.loadTemplates();

  }

  onTemplateChange(event: Event): void {

    const templateId = this.parseTemplateId(
      (event.target as HTMLSelectElement).value
    );

    if (templateId === null) {
      this.selectedTemplateId = null;
      this.fields = [];
      return;
    }

    this.selectedTemplateId = templateId;
    this.loadFields(templateId);

  }

  deleteField(fieldId: number): void {

    if (this.selectedTemplateId === null) {
      this.errorMessage =
        'Debes seleccionar un template para eliminar campos.';
      return;
    }

    this.templateFieldService
      .delete(this.selectedTemplateId, fieldId)
      .subscribe({
        next: () => {
          const templateId = this.selectedTemplateId;

          if (templateId === null) {
            this.errorMessage =
              'Debes seleccionar un template para recargar.';
            return;
          }

          this.loadFields(templateId);
        },
        error: (error) => {
          console.error(
            'Error deleting template field',
            error
          );
          this.errorMessage =
            'No fue posible eliminar el campo.';
        }
      });

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
            this.fields = [];
            return;
          }

          const queryTemplateId = this.parseTemplateId(
            this.route.snapshot.queryParamMap.get('templateId')
          );

          const selectedTemplate =
            templates.find(
              template => template.id === queryTemplateId
            ) ?? templates[0];

          this.selectedTemplateId = selectedTemplate.id;
          this.loadFields(selectedTemplate.id);
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

  private loadFields(templateId: number): void {

    this.loading = true;
    this.errorMessage = '';

    this.templateFieldService
      .getAll(templateId)
      .subscribe({
        next: (fields) => {
          this.fields = fields;
          this.loading = false;
        },
        error: (error) => {
          console.error(
            'Error loading template fields',
            error
          );
          this.errorMessage =
            'No fue posible cargar los fields.';
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
