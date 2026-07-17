import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface GuestResponse {
  id: number;
  name: string;
  uuid: string;
  confirmed: boolean | null;
  confirmedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class GuestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/admin/guests`;

  listAll() {
    return this.http.get<GuestResponse[]>(this.baseUrl);
  }

  getById(id: number) {
    return this.http.get<GuestResponse>(`${this.baseUrl}/${id}`);
  }

  create(name: string) {
    return this.http.post<GuestResponse>(this.baseUrl, { name });
  }

  update(id: number, name: string) {
    return this.http.put<GuestResponse>(`${this.baseUrl}/${id}`, { name });
  }

  delete(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
