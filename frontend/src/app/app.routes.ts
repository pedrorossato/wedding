import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';
import { HomeComponent } from './features/home/home.component';
import { InviteComponent } from './features/invite/invite.component';
import { LoginComponent } from './features/admin/auth/login.component';
import { DashboardComponent } from './features/admin/dashboard/dashboard.component';
import { EventAdminComponent } from './features/admin/event/event.component';
import { GuestAdminComponent } from './features/admin/guests/guest.component';
import { GiftAdminComponent } from './features/admin/gifts-admin/gift.component';
import { GalleryAdminComponent } from './features/admin/gallery/gallery.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'invite/:uuid',
    component: InviteComponent,
  },
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
      { path: 'event', component: EventAdminComponent },
      { path: 'guests', component: GuestAdminComponent },
      { path: 'gifts', component: GiftAdminComponent },
      { path: 'gallery', component: GalleryAdminComponent },
    ],
  },
];
