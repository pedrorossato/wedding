import { Component, input } from '@angular/core';

@Component({
  selector: 'app-button, a[app-button], button[app-button]',
  imports: [],
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss',
  host: {
    '[class.btn-primary]': 'variant() === "primary"',
    '[class.btn-outline]': 'variant() === "outline"',
    '[class.btn-ghost]': 'variant() === "ghost"',
    '[class.btn-accent]': 'variant() === "accent"',
    '[class.btn-sm]': 'size() === "sm"',
    '[class.btn-lg]': 'size() === "lg"',
    '[class.btn-full]': 'full()',
    '[attr.disabled]': 'disabled() ? "" : null',
  },
})
export class ButtonComponent {
  readonly variant = input<'primary' | 'outline' | 'ghost' | 'accent'>('primary');
  readonly size = input<'sm' | 'md' | 'lg'>('md');
  readonly full = input(false);
  readonly disabled = input(false);
}
