import { Routes } from '@angular/router';
import {TemplateFormComponent} from './features/templates/pages/template-form/template-form.component';
import { LayoutComponent } from './layout/layout/layout.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { TemplateListComponent } from './features/templates/pages/template-list/template-list.component';
import { TemplateFieldListComponent } from './features/template-fields/pages/template-field-list/template-field-list.component';
import { TemplateFieldFormComponent } from './features/template-fields/pages/template-field-form/template-field-form.component';
import { DestinationListComponent } from './features/destinations/pages/destination-list/destination-list.component';
import { DestinationFormComponent } from './features/destinations/pages/destination-form/destination-form.component';
import { JobExecutionComponent } from './features/jobs/pages/job-execution/job-execution.component';
import { HistoryListComponent } from './features/history/pages/history-list/history-list.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
        title: 'Dashboard'
      },
      {
        path: 'templates',
        component: TemplateListComponent,
        title: 'Templates'
      },
      {
        path: 'templates/new',
        component: TemplateFormComponent,
        title: 'Nuevo Template'
      },
      {
        path: 'template-fields',
        component: TemplateFieldListComponent,
        title: 'Template Fields'
      },
      {
        path: 'template-fields/new',
        component: TemplateFieldFormComponent,
        title: 'Nuevo Field'
      },
      {
        path: 'destinations',
        component: DestinationListComponent,
        title: 'Destinations'
      },
      {
        path: 'destinations/new',
        component: DestinationFormComponent,
        title: 'Nuevo Destination'
      },
      {
        path: 'jobs',
        component: JobExecutionComponent,
        title: 'Jobs'
      },
      {
        path: 'history',
        component: HistoryListComponent,
        title: 'History'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
