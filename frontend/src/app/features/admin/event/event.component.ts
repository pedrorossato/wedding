import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventService } from '../../../core/event.service';
import { ButtonComponent } from '../../../shared/button/button.component';

@Component({
  selector: 'app-event-admin',
  imports: [ReactiveFormsModule, ButtonComponent],
  templateUrl: './event.component.html',
  styleUrl: './event.component.scss',
})
export class EventAdminComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly eventService = inject(EventService);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly success = signal(false);
  readonly error = signal('');
  formSubmitted = false;

  readonly form = this.fb.nonNullable.group({
    weddingDate: ['', Validators.required],
    rsvpDeadline: ['', Validators.required],
  });

  get weddingDateInvalid(): boolean {
    const ctrl = this.form.get('weddingDate');
    return !!(ctrl && ctrl.invalid && (ctrl.touched || this.formSubmitted));
  }

  get rsvpDeadlineInvalid(): boolean {
    const ctrl = this.form.get('rsvpDeadline');
    return !!(ctrl && ctrl.invalid && (ctrl.touched || this.formSubmitted));
  }

  ngOnInit() {
    this.eventService.getConfig().subscribe({
      next: (config) => {
        this.form.patchValue({
          weddingDate: toDatetimeLocal(config.weddingDate),
          rsvpDeadline: toDatetimeLocal(config.rsvpDeadline),
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar dados do evento');
        this.loading.set(false);
      },
    });
  }

  onSubmit() {
    this.formSubmitted = true;
    if (this.form.invalid) return;

    this.saving.set(true);
    this.success.set(false);
    this.error.set('');

    this.eventService.update({
      weddingDate: fromDatetimeLocal(this.form.value.weddingDate!),
      rsvpDeadline: fromDatetimeLocal(this.form.value.rsvpDeadline!),
    }).subscribe({
      next: () => {
        this.success.set(true);
        this.saving.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao salvar. Verifique as datas.');
        this.saving.set(false);
      },
    });
  }
}

function toDatetimeLocal(iso: string): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toISOString().slice(0, 16);
}

function fromDatetimeLocal(local: string): string {
  return new Date(local).toISOString();
}
