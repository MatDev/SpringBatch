import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';

import {
  TemplateField,
  TemplateFieldRequest
} from '../models/template-field.model';

import { API } from '../constants/api.constant';

@Injectable({
  providedIn: 'root'
})
export class TemplateFieldService {

  private readonly api = inject(ApiService);

  private readonly baseEndpoint =
    `${API.CONFIG_API}${API.VERSION}/templates`;

  getAll(templateId: number): Observable<TemplateField[]> {

    return this.api.get<TemplateField[]>(
      `${this.baseEndpoint}/${templateId}/fields`
    );

  }

  create(
    templateId: number,
    request: TemplateFieldRequest
  ): Observable<TemplateField> {

    return this.api.post<TemplateField>(
      `${this.baseEndpoint}/${templateId}/fields`,
      request
    );

  }

  update(
    templateId: number,
    fieldId: number,
    request: TemplateFieldRequest
  ): Observable<TemplateField> {

    return this.api.put<TemplateField>(
      `${this.baseEndpoint}/${templateId}/fields/${fieldId}`,
      request
    );

  }

  delete(
    templateId: number,
    fieldId: number
  ): Observable<void> {

    return this.api.delete<void>(
      `${this.baseEndpoint}/${templateId}/fields/${fieldId}`
    );

  }

}
