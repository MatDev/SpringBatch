import { Component, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { MaterialModule } from '../../../../shared/material/material.module';

import {
  Template
} from '../../../../core/models/template.model';

import {
  TemplateService
} from '../../../../core/services/template.service';

@Component({
  selector: 'app-template-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MaterialModule
  ],
  templateUrl: './template-list.component.html',
  styleUrl: './template-list.component.scss'
})
export class TemplateListComponent implements OnInit {

  private readonly templateService = inject(TemplateService);

  templates: Template[] = [];

  loading = false;

  errorMessage = '';

  displayedColumns: string[] = [
    'name',
    'fileType',
    'encoding',
    'active',
    'actions'
  ];

  ngOnInit(): void {

    this.loading = true;

    const templateService = this.templateService as any;
    const loadTemplates =
      templateService.getTemplates?.bind(templateService) ??
      templateService.getAllTemplates?.bind(templateService) ??
      templateService.getAll?.bind(templateService) ??
      templateService.list?.bind(templateService);

    loadTemplates().subscribe({
      next: (data: Template[]) => {
        this.templates = data;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = 'Error al cargar los templates';
        this.loading = false;
      }
    });

  }

}
