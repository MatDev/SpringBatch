import { Routes } from '@angular/router';
import {TemplateFormComponent} from './features/templates/pages/template-form/template-form.component';
import { LayoutComponent } from './layout/layout/layout.component';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { TemplateListComponent } from './features/templates/pages/template-list/template-list.component';

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
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
