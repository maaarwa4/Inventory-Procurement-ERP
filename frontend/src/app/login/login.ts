// Fichier: login/login.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.html', // ✅ Renommé
  styleUrls: ['./login.scss'], // ✅ Renommé
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';
  isLoading = false; // ✅ Ajout état de chargement

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService, 
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.authService.login(this.loginForm.value).subscribe({
        next: (res: any) => {
          console.log('Login success:', res); // ✅ Debug
          localStorage.setItem('token', res.token);
          this.router.navigate(['/products']); // ✅ Redirection vers products
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Login error:', err); // ✅ Debug
          this.errorMessage = err.error?.details || 'Email ou mot de passe incorrect';
          this.isLoading = false;
        }
      });
    } else {
      // ✅ Marquer tous les champs comme touched pour afficher les erreurs
      Object.keys(this.loginForm.controls).forEach(key => {
        this.loginForm.get(key)?.markAsTouched();
      });
    }
  }

  // ✅ Getters pour faciliter l'accès aux contrôles dans le template
  get email() { return this.loginForm.get('email'); }
  get password() { return this.loginForm.get('password'); }
}