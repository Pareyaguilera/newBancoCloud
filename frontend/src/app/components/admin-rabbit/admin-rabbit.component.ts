import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransferenciaService } from '../../services/transferencia.service';

@Component({
  selector: 'app-admin-rabbit',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-rabbit.component.html',
  styleUrls: ['./admin-rabbit.component.css']
})
export class AdminRabbitComponent {
  mensajeConsola: string[] = [];
  procesando: boolean = false;
  
  // Variables para Colas
  nombreColaInput: string = ''; 

  // Variables para Exchanges y Bindings
  nombreExchangeInput: string = '';
  bindingColaInput: string = '';
  bindingExchangeInput: string = '';
  bindingRoutingKeyInput: string = '';

  constructor(private transferenciaService: TransferenciaService) {}

  ejecutarAckManual() {
    this.procesando = true;
    this.transferenciaService.procesarMensajeRabbitMQ().subscribe({
      next: (resp) => {
        this.procesando = false;
        this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ${resp}`);
      },
      error: (err) => {
        this.procesando = false;
        this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] Error en servidor.`);
      }
    });
  }

  crearCola() {
    if (!this.nombreColaInput.trim()) return;
    this.transferenciaService.crearColaRabbit(this.nombreColaInput).subscribe({
      next: (resp) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ÉXITO: ${resp}`),
      error: (err) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ERROR: No se pudo crear la cola.`)
    });
  }

  eliminarCola() {
    if (!this.nombreColaInput.trim()) return;
    this.transferenciaService.eliminarColaRabbit(this.nombreColaInput).subscribe({
      next: (resp) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ÉXITO: ${resp}`),
      error: (err) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ERROR: ${err.error}`)
    });
  }

  // --- NUEVOS MÉTODOS PARA EXCHANGES Y BINDINGS ---

  crearExchange() {
    if (!this.nombreExchangeInput.trim()) return;
    this.transferenciaService.crearExchangeRabbit(this.nombreExchangeInput).subscribe({
      next: (resp) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ÉXITO: ${resp}`),
      error: (err) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ERROR al crear exchange.`)
    });
  }

  crearBinding() {
    if (!this.bindingColaInput.trim() || !this.bindingExchangeInput.trim() || !this.bindingRoutingKeyInput.trim()) return;
    this.transferenciaService.crearBindingRabbit(this.bindingColaInput, this.bindingExchangeInput, this.bindingRoutingKeyInput).subscribe({
      next: (resp) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ÉXITO: ${resp}`),
      error: (err) => this.mensajeConsola.unshift(`[${new Date().toLocaleTimeString()}] ERROR al crear binding.`)
    });
  }
}
