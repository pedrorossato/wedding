import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { EncryptionService } from '../../../core/encryption.service';
import { AuthService } from '../../../core/auth.service';
import { ButtonComponent } from '../../../shared/button/button.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, ButtonComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  private readonly encryption = new EncryptionService();

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  readonly error = signal('');
  readonly loading = signal(false);

  async onSubmit() {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.error.set('');

    try {
      const encryptedPassword = await this.encryption.encrypt(
        this.form.value.password!,
        environment.loginEncryptionKey,
      );

      this.http.post<{ token: string }>(
        `${environment.apiUrl}/api/auth/login`,
        { username: this.form.value.username, password: encryptedPassword },
      ).subscribe({
        next: (res) => {
          this.auth.setToken(res.token);
          this.router.navigate(['/admin/dashboard']);
        },
        error: () => {
          this.error.set('Usuário ou senha inválidos');
          this.loading.set(false);
        },
      });
    } catch {
      this.error.set('Erro ao criptografar senha');
      this.loading.set(false);
    }
  }
}
