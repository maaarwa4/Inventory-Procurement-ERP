// Nom du fichier : src/app/app.component.ts

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // <-- L'IMPORTATION LA PLUS IMPORTANTE

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet // <-- IL FAUT LE METTRE ICI POUR QUE ÇA MARCHE
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  title = 'frontend';
}

