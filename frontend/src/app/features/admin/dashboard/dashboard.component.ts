import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CurrencyPipe } from '@angular/common';
import { environment } from '../../../../environments/environment';

interface StatsResponse {
  totalGuests: number;
  confirmedGuests: number;
  totalGifts: number;
  giftedGifts: number;
  totalGiftedValue: number;
  totalPhotos: number;
}

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly http = inject(HttpClient);

  readonly stats = signal<StatsResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal(false);

  ngOnInit() {
    this.http.get<StatsResponse>(`${environment.apiUrl}/api/admin/stats`).subscribe({
      next: (data) => {
        this.stats.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }
}
