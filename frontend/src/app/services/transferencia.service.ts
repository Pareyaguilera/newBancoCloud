import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransferenciaService {
  private apiUrl = 'http://localhost:8080/api/transferencias'; 
  private rabbitApiUrl = 'http://localhost:8080/api/admin/rabbitmq';

  constructor(private http: HttpClient) {}

  realizarTransferencia(datosTransferencia: any): Observable<any> {
    const token = sessionStorage.getItem('authToken'); 
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post(this.apiUrl, datosTransferencia, { 
      headers: headers, 
      responseType: 'text' 
    });
  }

  obtenerTodas(estado?: string): Observable<any[]> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    
    let url = `${this.apiUrl}/admin/todas`;
    if (estado) {
      url += `?estado=${estado}`;
    }
    
    return this.http.get<any[]>(url, { headers });
  }

  anularTransferencia(id: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    
    return this.http.put(`${this.apiUrl}/admin/anular/${id}`, {}, { 
      headers: headers, 
      responseType: 'text' 
    });
  }

  procesarMensajeRabbitMQ(): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(`${this.rabbitApiUrl}/procesar-ack`, {}, { 
      headers, 
      responseType: 'text' 
    });
  }

  crearColaRabbit(nombreCola: string): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(`${this.rabbitApiUrl}/colas`, { name: nombreCola }, { 
      headers, 
      responseType: 'text' 
    });
  }

  eliminarColaRabbit(nombreCola: string): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.delete(`${this.rabbitApiUrl}/colas/${nombreCola}`, { 
      headers, 
      responseType: 'text' 
    });
  }

  // --- NUEVOS MÉTODOS PARA EXCHANGES Y BINDINGS (Requisito Rúbrica) ---

  crearExchangeRabbit(nombreExchange: string): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(`${this.rabbitApiUrl}/exchanges`, { name: nombreExchange }, { 
      headers, 
      responseType: 'text' 
    });
  }

  crearBindingRabbit(cola: string, exchange: string, routingKey: string): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.post(`${this.rabbitApiUrl}/bindings`, { cola, exchange, routingKey }, { 
      headers, 
      responseType: 'text' 
    });
  }
}
