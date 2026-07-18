import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

@Injectable({ providedIn: 'root' })
export class GuestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/admin/guests`;

  listAll(page: number, size: number, sort: string, order: string) {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort)
      .set('order', order);
    return this.http.get<PageResponse<GuestResponse>>(this.baseUrl, { params });
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
