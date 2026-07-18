import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

interface InviteData {
  name: string;
  uuid: string;
  confirmed: boolean | null;
  weddingDate: string;
  rsvpDeadline: string;
}

interface Popup {
  title: string;
  message: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-invite',
  imports: [CommonModule],
  templateUrl: './invite.component.html',
  styleUrl: './invite.component.scss',
})
export class InviteComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);

  readonly loading = signal(true);
  readonly invite = signal<InviteData | null>(null);
  readonly confirming = signal(false);
  readonly popup = signal<Popup | null>(null);

  ngOnInit() {
    const uuid = this.route.snapshot.paramMap.get('uuid');
    if (!uuid) {
      this.loading.set(false);
      this.popup.set({ type: 'error', title: 'Convite inválido', message: 'Este link de convite não é válido.' });
      return;
    }

    this.http.get<InviteData>(`${environment.apiUrl}/api/invite/${uuid}`).subscribe({
      next: (data) => {
        this.invite.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.popup.set({
          type: 'error',
          title: 'Convite não encontrado',
          message: err.error?.message || 'Não foi possível localizar este convite. Verifique o link.',
        });
      },
    });
  }

  confirm(choice: boolean) {
    const data = this.invite();
    if (!data) return;

    this.confirming.set(true);
    this.http
      .post<InviteData>(`${environment.apiUrl}/api/invite/${data.uuid}/confirm`, { confirmed: choice })
      .subscribe({
        next: (updated) => {
          this.confirming.set(false);
          this.invite.set(updated);
          const label = choice ? 'sua presença foi confirmada' : 'a ausência foi registrada';
          this.popup.set({
            type: 'success',
            title: choice ? 'Presença confirmada!' : 'Convite recusado',
            message: `Olá ${updated.name}, ${label}. Obrigado por responder!`,
          });
        },
        error: (err) => {
          this.confirming.set(false);
          this.popup.set({
            type: 'error',
            title: 'Erro',
            message: err.error?.message || 'Erro ao processar sua resposta.',
          });
        },
      });
  }

  closePopup() {
    this.popup.set(null);
  }

  formatDate(iso: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
  }

  formatTime(iso: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return `${String(d.getHours()).padStart(2, '0')}h${String(d.getMinutes()).padStart(2, '0')}`;
  }
}