// page-response.ts (dans src/app/models/)
export interface PageResponse<T> {
    content: T[];          // Les données (liste d’éléments)
    totalElements: number; // Nombre total d'éléments
    totalPages: number;    // Nombre total de pages
    size: number;          // Taille de la page
    number: number;        // Numéro de page actuel
  }
  