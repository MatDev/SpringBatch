import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';
import { API } from '../constants/api.constant';
import {
  Template,
  TemplateRequest
} from '../models/template.model';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

  private readonly api = inject(ApiService);

  private readonly endpoint =
    `${API.CONFIG_API}${API.VERSION}/templates`;

  getAll(onlyActive = false): Observable<Template[]> {

    return this.api.get<Template[]>(
      this.endpoint,
      {
        onlyActive
      }
    );

  }

  getById(id: number): Observable<Template> {

    return this.api.get<Template>(
      `${this.endpoint}/${id}`
    );

  }

  getComplete(id: number): Observable<Template> {

    return this.api.get<Template>(
      `${this.endpoint}/${id}/complete`
    );

  }

  create(
    request: TemplateRequest
  ): Observable<Template> {

    return this.api.post<Template>(
      this.endpoint,
      request
    );

  }

  update(
    id: number,
    request: TemplateRequest
  ): Observable<Template> {

    return this.api.put<Template>(
      `${this.endpoint}/${id}`,
      request
    );

  }

  delete(id: number): Observable<void> {

    return this.api.delete<void>(
      `${this.endpoint}/${id}`
    );

  }

}
