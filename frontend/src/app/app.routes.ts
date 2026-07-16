import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';
import { LoginComponent } from './features/admin/auth/login.component';
import { DashboardComponent } from './features/admin/dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: 'admin/login',
    component: LoginComponent,
  },
  {
    path: 'admin',
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
    ],
  },
];
