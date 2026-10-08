// Nom du fichier : src/app/layouts/main-layout.component.ts

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// --- IMPORTATION DES OUTILS MANQUANTS ---
import { RouterModule } from '@angular/router';         // <-- L'outil pour <router-outlet> et routerLink
import { MatToolbarModule } from '@angular/material/toolbar'; // <-- L'outil pour <mat-toolbar>
import { MatButtonModule } from '@angular/material/button';   // <-- L'outil pour mat-button

@Component({
  selector: 'app-main-layout',
  standalone: true,
  // --- ON DONNE LES OUTILS AU COMPOSANT ICI ---
  imports: [
    CommonModule,
    RouterModule,         // <-- On l'ajoute ici
    MatToolbarModule,     // <-- On l'ajoute ici
    MatButtonModule       // <-- On l'ajoute ici
  ],
  templateUrl: './main-layout.html',
  styleUrls: ['./main-layout.css']
})
export class MainLayoutComponent {

}