import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // <-- 1. Importamos RouterModule
import { TransferenciaService } from '../../services/transferencia.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule], // <-- 2. Lo agregamos a los imports de Angular
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  transferencias: any[] = [];
  mensajeExito: string = '';

  constructor(
    private transferenciaService: TransferenciaService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarTransferencias();
  }

  cargarTransferencias() {
    this.transferenciaService.obtenerTodas().subscribe({
      next: (data) => this.transferencias = data,
      error: (err) => console.error('Error al cargar transferencias', err)
    });
  }

  anular(id: number) {
    if (confirm('¿Estás seguro de anular esta transferencia? Esto enviará un mensaje a RabbitMQ.')) {
      this.transferenciaService.anularTransferencia(id).subscribe({
        next: (resp) => {
          this.mensajeExito = resp;
          this.cargarTransferencias(); // Recargamos la tabla
          setTimeout(() => this.mensajeExito = '', 5000);
        },
        error: (err) => console.error('Error al anular', err)
      });
    }
  }

  // <-- 3. Agregamos el método para navegar
  irARabbitMQ() {
    this.router.navigate(['/admin/rabbitmq']);
  }

  cerrarSesion() {
    sessionStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }
}
