import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';
import { API } from '../constants/api.constant';
import {
  Destination,
  DestinationRequest
} from '../models/destination.model';

@Injectable({
  providedIn: 'root'
})
export class DestinationService {

  private readonly api = inject(ApiService);

  private readonly baseEndpoint =
    `${API.CONFIG_API}${API.VERSION}/templates`;

  getAll(templateId: number): Observable<Destination[]> {

    return this.api.get<Destination[]>(
      `${this.baseEndpoint}/${templateId}/destinations`
    );

  }

  create(
    templateId: number,
    request: DestinationRequest
  ): Observable<Destination> {

    return this.api.post<Destination>(
      `${this.baseEndpoint}/${templateId}/destinations`,
      request
    );

  }

  update(
    templateId: number,
    destinationId: number,
    request: DestinationRequest
  ): Observable<Destination> {

    return this.api.put<Destination>(
      `${this.baseEndpoint}/${templateId}/destinations/${destinationId}`,
      request
    );

  }

  delete(
    templateId: number,
    destinationId: number
  ): Observable<void> {

    return this.api.delete<void>(
      `${this.baseEndpoint}/${templateId}/destinations/${destinationId}`
    );

  }

}
