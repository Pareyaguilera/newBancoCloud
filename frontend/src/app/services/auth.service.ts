import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Apunta a la URL de tu backend local de Spring Boot
  private apiUrl = 'http://localhost:8080/api/auth/login'; 

  constructor(private http: HttpClient) {}

  login(credenciales: { username: string, password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, credenciales);
  }
}
