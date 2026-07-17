import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { GiftService, GiftResponse, GiftPurchaseInfo } from '../../../core/gift.service';
import { ButtonComponent } from '../../../shared/button/button.component';

@Component({
  selector: 'app-gifts-admin',
  imports: [ReactiveFormsModule, ButtonComponent, CurrencyPipe],
  templateUrl: './gift.component.html',
  styleUrl: './gift.component.scss',
})
export class GiftAdminComponent implements OnInit {
  private readonly giftService = inject(GiftService);
  private readonly fb = inject(FormBuilder);

  readonly gifts = signal<GiftResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly showForm = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly saving = signal(false);
  readonly formError = signal('');

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    value: [0, [Validators.required, Validators.min(0.01)]],
  });

  selectedFile: File | null = null;
  readonly previewUrl = signal<string | null>(null);

  readonly deletingId = signal<number | null>(null);

  readonly expandedId = signal<number | null>(null);
  readonly purchases = signal<GiftPurchaseInfo[]>([]);
  readonly loadingPurchases = signal(false);

  ngOnInit() {
    this.loadGifts();
  }

  loadGifts() {
    this.loading.set(true);
    this.error.set('');
    this.giftService.listAll().subscribe({
      next: (data) => {
        this.gifts.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar presentes');
        this.loading.set(false);
      },
    });
  }

  openCreate() {
    this.form.reset({ name: '', description: '', value: 0 });
    this.editingId.set(null);
    this.selectedFile = null;
    this.previewUrl.set(null);
    this.formError.set('');
    this.showForm.set(true);
  }

  openEdit(gift: GiftResponse) {
    this.form.setValue({
      name: gift.name,
      description: gift.description || '',
      value: gift.value,
    });
    this.editingId.set(gift.id);
    this.selectedFile = null;
    this.previewUrl.set(null);
    this.formError.set('');
    this.showForm.set(true);
  }

  closeForm() {
    this.showForm.set(false);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.previewUrl.set(reader.result as string);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit() {
    if (this.form.invalid) return;

    const fd = new FormData();
    fd.append('name', this.form.value.name!);
    fd.append('description', this.form.value.description!);
    fd.append('value', this.form.value.value!.toString());

    const editingId = this.editingId();
    const isCreate = editingId === null;

    if (this.selectedFile) {
      fd.append('image', this.selectedFile);
    } else if (!isCreate) {
      fd.append('image', new Blob([]), '');
    }

    this.saving.set(true);
    const request = isCreate
      ? this.giftService.create(fd)
      : this.giftService.update(editingId!, fd);

    request.subscribe({
      next: () => {
        this.showForm.set(false);
        this.saving.set(false);
        this.loadGifts();
      },
      error: (err) => {
        this.formError.set(err.error?.message || 'Erro ao salvar presente');
        this.saving.set(false);
      },
    });
  }

  startDelete(id: number) {
    this.deletingId.set(id);
  }

  cancelDelete() {
    this.deletingId.set(null);
  }

  confirmDelete(id: number) {
    this.giftService.delete(id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.loadGifts();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao remover presente');
        this.deletingId.set(null);
      },
    });
  }

  togglePurchases(giftId: number) {
    if (this.expandedId() === giftId) {
      this.expandedId.set(null);
      return;
    }

    this.expandedId.set(giftId);
    this.loadingPurchases.set(true);
    this.giftService.listPurchases(giftId).subscribe({
      next: (data) => {
        this.purchases.set(data);
        this.loadingPurchases.set(false);
      },
      error: () => {
        this.purchases.set([]);
        this.loadingPurchases.set(false);
      },
    });
  }
}
