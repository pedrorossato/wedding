import { Component, inject, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { GuestService, GuestResponse, PageResponse } from '../../../core/guest.service';
import { ButtonComponent } from '../../../shared/button/button.component';

@Component({
  selector: 'app-guests-admin',
  imports: [ReactiveFormsModule, ButtonComponent],
  templateUrl: './guest.component.html',
  styleUrl: './guest.component.scss',
})
export class GuestAdminComponent implements OnInit {
  private readonly guestService = inject(GuestService);
  private readonly fb = inject(FormBuilder);

  readonly guests = signal<GuestResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly page = signal(0);
  readonly size = signal(10);
  readonly sort = signal('name');
  readonly order = signal('asc');
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly pageSizes = [5, 10, 50, 100];

  readonly editingId = signal<number | null>(null);
  readonly editForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
  });

  readonly createForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
  });

  readonly deletingId = signal<number | null>(null);

  ngOnInit() {
    this.loadGuests();
  }

  loadGuests() {
    this.loading.set(true);
    this.error.set('');
    this.guestService.listAll(this.page(), this.size(), this.sort(), this.order()).subscribe({
      next: (data: PageResponse<GuestResponse>) => {
        this.guests.set(data.content);
        this.totalPages.set(data.totalPages);
        this.totalElements.set(data.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar convidados');
        this.loading.set(false);
      },
    });
  }

  setPage(newPage: number) {
    if (newPage < 0 || newPage >= this.totalPages()) return;
    this.page.set(newPage);
    this.loadGuests();
  }

  setSize(newSize: number) {
    this.size.set(newSize);
    this.page.set(0);
    this.loadGuests();
  }

  toggleSort(column: string) {
    if (this.sort() === column) {
      this.order.set(this.order() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sort.set(column);
      this.order.set('asc');
    }
    this.page.set(0);
    this.loadGuests();
  }

  sortIndicator(column: string): string {
    if (this.sort() !== column) return '↕';
    return this.order() === 'asc' ? '↑' : '↓';
  }

  onCreate() {
    if (this.createForm.invalid) return;
    const name = this.createForm.value.name!.trim();
    if (!name) return;

    this.guestService.create(name).subscribe({
      next: () => {
        this.createForm.reset();
        this.loadGuests();
      },
      error: () => this.error.set('Erro ao criar convidado'),
    });
  }

  startEdit(guest: GuestResponse) {
    this.editingId.set(guest.id);
    this.editForm.setValue({ name: guest.name });
  }

  cancelEdit() {
    this.editingId.set(null);
  }

  saveEdit(id: number) {
    if (this.editForm.invalid) return;
    const name = this.editForm.value.name!.trim();
    if (!name) return;

    this.guestService.update(id, name).subscribe({
      next: () => {
        this.editingId.set(null);
        this.loadGuests();
      },
      error: () => this.error.set('Erro ao atualizar convidado'),
    });
  }

  startDelete(id: number) {
    this.deletingId.set(id);
  }

  cancelDelete() {
    this.deletingId.set(null);
  }

  confirmDelete(id: number) {
    this.guestService.delete(id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.loadGuests();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao remover convidado');
        this.deletingId.set(null);
      },
    });
  }

  copyUuid(uuid: string, name: string) {
    navigator.clipboard.writeText(uuid).catch(() => {
      const el = document.createElement('textarea');
      el.value = uuid;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
    });
  }

  statusLabel(confirmed: boolean | null): string {
    if (confirmed === true) return 'Confirmado';
    if (confirmed === false) return 'Recusou';
    return 'Pendente';
  }

  statusClass(confirmed: boolean | null): string {
    if (confirmed === true) return 'guest-status--confirmed';
    if (confirmed === false) return 'guest-status--declined';
    return 'guest-status--pending';
  }
}