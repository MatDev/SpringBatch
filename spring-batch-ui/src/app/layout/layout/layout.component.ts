import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { MaterialModule } from '../../shared/material/material.module';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { ToolbarComponent } from '../components/toolbar/toolbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    SidebarComponent,
    ToolbarComponent,
    MaterialModule
  ],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {}
