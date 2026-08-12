import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '../../../shared/material/material.module';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterModule,
    MaterialModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

}
