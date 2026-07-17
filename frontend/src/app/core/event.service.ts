import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface EventConfig {
  id: number | null;
  weddingDate: string;
  rsvpDeadline: string;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/admin/event`;

  getPublicConfig() {
    return this.http.get<EventConfig>(`${environment.apiUrl}/api/event`);
  }

  getConfig() {
    return this.http.get<EventConfig>(this.baseUrl);
  }

  update(data: { weddingDate: string; rsvpDeadline: string }) {
    return this.http.put<EventConfig>(this.baseUrl, data);
  }
}
