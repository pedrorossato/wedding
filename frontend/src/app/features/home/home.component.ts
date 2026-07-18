import {
  Component,
  inject,
  signal,
  OnInit,
  afterNextRender,
  ElementRef,
  viewChildren,
  DestroyRef,
} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { interval } from 'rxjs';
import { EventService, EventConfig } from '../../core/event.service';
import { GalleryService, GalleryPhotoResponse } from '../../core/gallery.service';

interface Countdown {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

const MESES_PT = [
  'janeiro', 'fevereiro', 'março', 'abril', 'maio', 'junho',
  'julho', 'agosto', 'setembro', 'outubro', 'novembro', 'dezembro',
];

const DIAS_SEMANA_PT = [
  'domingo', 'segunda-feira', 'terça-feira', 'quarta-feira',
  'quinta-feira', 'sexta-feira', 'sábado',
];

@Component({
  selector: 'app-home',
  imports: [CommonModule, DatePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly galleryService = inject(GalleryService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly host = inject(ElementRef<HTMLElement>);

  readonly event = signal<EventConfig | null>(null);
  readonly photos = signal<GalleryPhotoResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal(false);

  readonly countdown = signal<Countdown>({ days: 0, hours: 0, minutes: 0, seconds: 0 });

  private readonly revealElements = viewChildren<ElementRef<HTMLElement>>('reveal');

  ngOnInit() {
    this.loadEvent();
    this.loadGallery();
  }

  private loadEvent() {
    this.eventService.getPublicConfig().subscribe({
      next: (data) => {
        this.event.set(data);
        this.startCountdown(data.weddingDate);
      },
      error: () => this.error.set(true),
    });
  }

  private loadGallery() {
    this.galleryService.listPublic().subscribe({
      next: (data) => {
        this.photos.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  private startCountdown(weddingDate: string) {
    const compute = () => {
      const target = new Date(weddingDate).getTime();
      const now = Date.now();
      const diff = Math.max(0, target - now);
      this.countdown.set({
        days: Math.floor(diff / 86400000),
        hours: Math.floor((diff % 86400000) / 3600000),
        minutes: Math.floor((diff % 3600000) / 60000),
        seconds: Math.floor((diff % 60000) / 1000),
      });
    };

    compute();

    if (typeof window !== 'undefined') {
      interval(1000)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(compute);
    }
  }

  get isWedded(): boolean {
    const data = this.event();
    if (!data) return false;
    return new Date(data.weddingDate).getTime() <= Date.now();
  }

  formatWeddingDate(): string {
    const data = this.event();
    if (!data) return '';
    const d = new Date(data.weddingDate);
    const dia = d.getDate();
    const mes = MESES_PT[d.getMonth()];
    const ano = d.getFullYear();
    return `${dia} de ${mes} de ${ano}`;
  }

  formatWeddingWeekday(): string {
    const data = this.event();
    if (!data) return '';
    const d = new Date(data.weddingDate);
    return DIAS_SEMANA_PT[d.getDay()];
  }

  formatWeddingTime(): string {
    const data = this.event();
    if (!data) return '';
    const d = new Date(data.weddingDate);
    return `${String(d.getHours()).padStart(2, '0')}h${String(d.getMinutes()).padStart(2, '0')}`;
  }

  readonly onScrollInit = afterNextRender(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target);
          }
        }
      },
      { threshold: 0.12 },
    );

    this.revealElements().forEach((el) => observer.observe(el.nativeElement));

    this.host.nativeElement.classList.add('home--ready');
  });
}