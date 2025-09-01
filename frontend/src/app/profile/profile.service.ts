import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

// Interface corrigée - sans le 's' à la fin
export interface ChangePasswordRequest {
  email: string;
  currentPassword: string;
  newPassword: string;
}

export interface UpdateEmailRequest {
  currentEmail: string;
  newEmail: string;
  currentPassword: string;
}

export interface ProfileResponse {
  message: string;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private baseUrl = 'http://localhost:8080/api/profile';

  constructor(private http: HttpClient) {}

  changePassword(request: ChangePasswordRequest): Observable<ProfileResponse> {
    return this.http.post<ProfileResponse>(`${this.baseUrl}/change-password`, request)
      .pipe(catchError(this.handleError));
  }

  updateEmail(request: UpdateEmailRequest): Observable<ProfileResponse> {
    return this.http.post<ProfileResponse>(`${this.baseUrl}/update-email`, request)
      .pipe(catchError(this.handleError));
  }

  deleteAccount(email: string, password: string): Observable<ProfileResponse> {
    return this.http.post<ProfileResponse>(`${this.baseUrl}/delete-account`, { 
      email, 
      password 
    }).pipe(catchError(this.handleError));
  }

  private handleError(error: any) {
    console.error('Profile service error:', error);
    let errorMessage = 'Une erreur est survenue';
    
    if (error.error?.error) {
      errorMessage = error.error.error;
    } else if (error.status === 401) {
      errorMessage = 'Mot de passe incorrect';
    } else if (error.status === 409) {
      errorMessage = 'Cet email est déjà utilisé';
    } else if (error.status === 404) {
      errorMessage = 'Utilisateur non trouvé';
    }
    
    return throwError(() => ({ error: { details: errorMessage } }));
  }
}