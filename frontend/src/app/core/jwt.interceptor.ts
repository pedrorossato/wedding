import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!req.url.includes('/api/')) return next(req);

  const token = auth.getToken();
  if (!token) return next(req);

  if (!isTokenValid(token)) {
    auth.logout();
    router.navigate(['/admin/login']);
    return next(req);
  }

  const cloned = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });
  return next(cloned);
};

function isTokenValid(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}
