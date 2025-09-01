import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../login/auth.service';
import { ProfileService, ChangePasswordRequest, UpdateEmailRequest } from './profile.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class ProfileComponent implements OnInit {
  changePasswordForm: FormGroup;
  updateEmailForm: FormGroup;
  
  currentUser = {
    email: '',
    lastLogin: new Date(),
    id: ''
  };
  
  isChangingPassword = false;
  isUpdatingEmail = false;
  successMessage = '';
  errorMessage = '';
  activeTab = 'overview'; // 'overview', 'password', 'email'

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private profileService: ProfileService,
    private router: Router
  ) {
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', [Validators.required, Validators.minLength(3)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });

    this.updateEmailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      currentPassword: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    const token = this.authService.getToken();
    if (token) {
      try {
        // Décoder le token pour obtenir l'email
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.currentUser.email = payload.sub;
        this.currentUser.lastLogin = new Date(payload.iat * 1000);
        this.updateEmailForm.patchValue({ email: this.currentUser.email });
      } catch (error) {
        console.error('Erreur décodage token:', error);
        this.logout();
      }
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    this.clearMessages();
  }

  onChangePassword() {
    if (this.changePasswordForm.valid) {
      this.isChangingPassword = true;
      this.clearMessages();

      const request: ChangePasswordRequest = {
        email: this.currentUser.email,
        currentPassword: this.changePasswordForm.value.currentPassword,
        newPassword: this.changePasswordForm.value.newPassword
      };
      
      this.profileService.changePassword(request).subscribe({
        next: (response) => {
          this.successMessage = 'Mot de passe modifié avec succès !';
          this.isChangingPassword = false;
          this.changePasswordForm.reset();
        },
        error: (error) => {
          this.errorMessage = error.error?.details || 'Erreur lors de la modification';
          this.isChangingPassword = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.changePasswordForm);
    }
  }

  onUpdateEmail() {
    if (this.updateEmailForm.valid) {
      this.isUpdatingEmail = true;
      this.clearMessages();

      const request: UpdateEmailRequest = {
        currentEmail: this.currentUser.email,
        newEmail: this.updateEmailForm.value.email,
        currentPassword: this.updateEmailForm.value.currentPassword
      };
      
      this.profileService.updateEmail(request).subscribe({
        next: (response) => {
          this.successMessage = 'Email modifié avec succès !';
          this.currentUser.email = request.newEmail;
          this.isUpdatingEmail = false;
          this.updateEmailForm.get('currentPassword')?.reset();
          // Mettre à jour l'email dans le formulaire
          this.updateEmailForm.patchValue({ email: this.currentUser.email });
        },
        error: (error) => {
          this.errorMessage = error.error?.details || 'Erreur lors de la modification';
          this.isUpdatingEmail = false;
        }
      });
    } else {
      this.markFormGroupTouched(this.updateEmailForm);
    }
  }

  logout() {
    if (confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      formGroup.get(key)?.markAsTouched();
    });
  }

  private clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  // Getters pour le template
  get currentPassword() { return this.changePasswordForm.get('currentPassword'); }
  get newPassword() { return this.changePasswordForm.get('newPassword'); }
  get confirmPassword() { return this.changePasswordForm.get('confirmPassword'); }
  get email() { return this.updateEmailForm.get('email'); }
  get emailCurrentPassword() { return this.updateEmailForm.get('currentPassword'); }
}