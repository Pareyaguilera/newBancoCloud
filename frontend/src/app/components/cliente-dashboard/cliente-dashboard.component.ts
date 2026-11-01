import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TransferenciaService } from '../../services/transferencia.service';

@Component({
  selector: 'app-cliente-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-dashboard.component.html',
  styleUrls: ['./cliente-dashboard.component.css']
})
export class ClienteDashboardComponent implements OnInit {
  transferencia = {
    cuentaOrigen: '',
    cuentaDestino: '',
    monto: null,
    usuarioSub: '' // <-- Agregamos el campo faltante
  };
  
  mensajeExito: string = '';
  mensajeError: string = '';
  cargando: boolean = false;

  constructor(
    private transferenciaService: TransferenciaService,
    private router: Router
  ) {}

  ngOnInit() {
    // Al iniciar la pantalla, extraemos el nombre de usuario del token de Cognito
    const token = sessionStorage.getItem('authToken');
    if (token) {
      const payloadBase64 = token.split('.')[1];
      const decodedPayload = JSON.parse(atob(payloadBase64));
      // Guardamos el sub o el username de Cognito
      this.transferencia.usuarioSub = decodedPayload['cognito:username'] || decodedPayload['sub'] || 'cliente-web';
    }
  }

  enviarTransferencia() {
    if (!this.transferencia.cuentaOrigen || !this.transferencia.cuentaDestino || !this.transferencia.monto) {
      this.mensajeError = 'Por favor, completa todos los campos.';
      return;
    }

    this.cargando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.transferenciaService.realizarTransferencia(this.transferencia).subscribe({
      next: (response) => {
        this.cargando = false;
        this.mensajeExito = '¡Transferencia enviada con éxito a la cola de RabbitMQ!';
        // Limpiamos los campos pero mantenemos el usuarioSub
        this.transferencia.cuentaOrigen = '';
        this.transferencia.cuentaDestino = '';
        this.transferencia.monto = null;
      },
      error: (err) => {
        this.cargando = false;
        console.error(err);
        this.mensajeError = 'Error al procesar la transferencia. Verifica la consola.';
      }
    });
  }

  cerrarSesion() {
    sessionStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }
}
