import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { MaterialModule } from '../../../../shared/material/material.module';
import {
  Destination
} from '../../../../core/models/destination.model';
import { Template } from '../../../../core/models/template.model';
import { DestinationService } from '../../../../core/services/destination.service';
import { TemplateService } from '../../../../core/services/template.service';

@Component({
  selector: 'app-destination-list',
  standalone: true,
  imports: [
    RouterModule,
    MaterialModule
  ],
  templateUrl: './destination-list.component.html',
  styleUrl: './destination-list.component.scss'
})
export class DestinationListComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);

  private readonly templateService = inject(TemplateService);

  private readonly destinationService = inject(DestinationService);

  templates: Template[] = [];

  destinations: Destination[] = [];

  selectedTemplateId: number | null = null;

  loading = false;

  errorMessage = '';

  displayedColumns: string[] = [
    'name',
    'destinationType',
    'target',
    'active',
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
      this.destinations = [];
      return;
    }

    this.selectedTemplateId = templateId;
    this.loadDestinations(templateId);

  }

  getDestinationTarget(
    destination: Destination
  ): string {

    return destination.targetTable ??
      destination.targetCollection ??
      destination.targetTopic ??
      destination.targetPath ??
      destination.targetUrl ??
      '-';

  }

  deleteDestination(destinationId: number): void {

    if (this.selectedTemplateId === null) {
      this.errorMessage =
        'Debes seleccionar un template para eliminar destinos.';
      return;
    }

    this.destinationService
      .delete(this.selectedTemplateId, destinationId)
      .subscribe({
        next: () => {
          const templateId = this.selectedTemplateId;

          if (templateId === null) {
            this.errorMessage =
              'Debes seleccionar un template para recargar.';
            return;
          }

          this.loadDestinations(templateId);
        },
        error: (error) => {
          console.error(
            'Error deleting destination',
            error
          );
          this.errorMessage =
            'No fue posible eliminar el destino.';
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
            this.destinations = [];
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
          this.loadDestinations(selectedTemplate.id);
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

  private loadDestinations(templateId: number): void {

    this.loading = true;
    this.errorMessage = '';

    this.destinationService
      .getAll(templateId)
      .subscribe({
        next: (destinations) => {
          this.destinations = destinations;
          this.loading = false;
        },
        error: (error) => {
          console.error(
            'Error loading destinations',
            error
          );
          this.errorMessage =
            'No fue posible cargar los destinations.';
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
