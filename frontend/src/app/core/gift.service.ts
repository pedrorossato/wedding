import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface GiftResponse {
  id: number;
  name: string;
  description: string | null;
  value: number;
  imageUrl: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface GiftPurchaseInfo {
  id: number;
  guestName: string;
  paid: boolean;
  paymentIntentId: string | null;
}

@Injectable({ providedIn: 'root' })
export class GiftService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/admin/gifts`;

  listAll() {
    return this.http.get<GiftResponse[]>(this.baseUrl);
  }

  getById(id: number) {
    return this.http.get<GiftResponse>(`${this.baseUrl}/${id}`);
  }

  create(formData: FormData) {
    return this.http.post<GiftResponse>(this.baseUrl, formData);
  }

  update(id: number, formData: FormData) {
    return this.http.put<GiftResponse>(`${this.baseUrl}/${id}`, formData);
  }

  delete(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  listPurchases(id: number) {
    return this.http.get<GiftPurchaseInfo[]>(`${this.baseUrl}/${id}/purchases`);
  }
}
