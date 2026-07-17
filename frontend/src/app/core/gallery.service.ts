import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface GalleryPhotoResponse {
  id: number;
  imageUrl: string;
  sortOrder: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class GalleryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/admin/gallery`;

  listAll() {
    return this.http.get<GalleryPhotoResponse[]>(this.baseUrl);
  }

  listPublic() {
    return this.http.get<GalleryPhotoResponse[]>(`${environment.apiUrl}/api/gallery`);
  }

  upload(files: File[]) {
    const fd = new FormData();
    for (const file of files) {
      fd.append('files', file);
    }
    return this.http.post<GalleryPhotoResponse[]>(this.baseUrl, fd);
  }

  reorder(orderedIds: number[]) {
    return this.http.put<GalleryPhotoResponse[]>(`${this.baseUrl}/reorder`, { orderedIds });
  }

  delete(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
