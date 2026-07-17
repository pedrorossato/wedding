import { Component, inject, signal, OnInit, HostListener } from '@angular/core';
import { GalleryService, GalleryPhotoResponse } from '../../../core/gallery.service';
import { ButtonComponent } from '../../../shared/button/button.component';

@Component({
  selector: 'app-gallery-admin',
  imports: [ButtonComponent],
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.scss',
})
export class GalleryAdminComponent implements OnInit {
  private readonly galleryService = inject(GalleryService);

  readonly photos = signal<GalleryPhotoResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly dragActive = signal(false);
  readonly uploading = signal(false);
  readonly selectedFiles = signal<File[]>([]);

  readonly deletingId = signal<number | null>(null);

  ngOnInit() {
    this.loadPhotos();
  }

  loadPhotos() {
    this.loading.set(true);
    this.error.set('');
    this.galleryService.listAll().subscribe({
      next: (data) => {
        this.photos.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar galeria');
        this.loading.set(false);
      },
    });
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(false);
    const files = Array.from(event.dataTransfer?.files || []).filter(f => f.type.startsWith('image/'));
    if (files.length > 0) {
      this.uploadFiles(files);
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const files = Array.from(input.files).filter(f => f.type.startsWith('image/'));
      if (files.length > 0) {
        this.uploadFiles(files);
      }
    }
    input.value = '';
  }

  private uploadFiles(files: File[]) {
    this.uploading.set(true);
    this.error.set('');
    this.galleryService.upload(files).subscribe({
      next: (data) => {
        this.photos.set([...this.photos(), ...data]);
        this.uploading.set(false);
      },
      error: () => {
        this.error.set('Erro ao fazer upload');
        this.uploading.set(false);
      },
    });
  }

  moveUp(index: number) {
    if (index === 0) return;
    const list = [...this.photos()];
    [list[index - 1], list[index]] = [list[index], list[index - 1]];
    this.saveOrder(list);
  }

  moveDown(index: number) {
    if (index >= this.photos().length - 1) return;
    const list = [...this.photos()];
    [list[index], list[index + 1]] = [list[index + 1], list[index]];
    this.saveOrder(list);
  }

  private saveOrder(list: GalleryPhotoResponse[]) {
    const orderedIds = list.map(p => p.id);
    this.galleryService.reorder(orderedIds).subscribe({
      next: (data) => this.photos.set(data),
      error: () => this.error.set('Erro ao reordenar'),
    });
  }

  startDelete(id: number) {
    this.deletingId.set(id);
  }

  cancelDelete() {
    this.deletingId.set(null);
  }

  confirmDelete(id: number) {
    this.galleryService.delete(id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.photos.set(this.photos().filter(p => p.id !== id));
      },
      error: () => {
        this.error.set('Erro ao remover foto');
        this.deletingId.set(null);
      },
    });
  }
}
