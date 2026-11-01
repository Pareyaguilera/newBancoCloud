import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMensaje: string = '';
  cargando: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  iniciarSesion() {
    if (!this.username || !this.password) {
      this.errorMensaje = 'Por favor, ingresa tu correo y contraseña.';
      return;
    }

    this.cargando = true;
    this.errorMensaje = '';

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response) => {
        this.cargando = false;
        // Cognito devuelve un token (por ejemplo, IdToken) dentro del resultado de autenticación
        const token = response.idToken || response.IdToken || response.accessToken;

        if (token) {
          sessionStorage.setItem('authToken', token);

          // Decodificar el token para verificar el rol/grupo
          const payloadBase64 = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payloadBase64));
          const grupos = decodedPayload['cognito:groups'] || [];
          const esAdmin = grupos.includes('Administrador') || this.username.toLowerCase().includes('admin');

          // Redirigir según el rol
          if (esAdmin) {
            this.router.navigate(['/admin/dashboard']);
          } else {
            this.router.navigate(['/cliente/dashboard']);
          }
        } else {
          this.errorMensaje = 'No se pudo obtener el token de acceso.';
        }
      },
      error: (err) => {
        this.cargando = false;
        console.error(err);
        this.errorMensaje = 'Credenciales incorrectas o error al conectar con el servidor.';
      }
    });
  }
}