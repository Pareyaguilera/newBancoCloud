import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { ClienteDashboardComponent } from './components/cliente-dashboard/cliente-dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AdminRabbitComponent } from './components/admin-rabbit/admin-rabbit.component'; // <-- Importación agregada

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'cliente/dashboard', component: ClienteDashboardComponent },
  { path: 'admin/dashboard', component: AdminDashboardComponent },
  { path: 'admin/rabbitmq', component: AdminRabbitComponent }, // <-- Nueva ruta agregada
  { path: '**', redirectTo: 'login' } 
];