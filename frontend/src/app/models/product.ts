// product.ts
export interface ProductBase {
  id?: number;
  name: string;
  description?: string;
  brand: string;
  category: string;
  price: number;
  stockQuantity: number;  // Nom original du backend
  imageUrl?: string;
  model?: string;
  color?: string;
  storage?: string;
  screenSize?: string;
  networkType?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProductFrontend {
  id?: number;
  name: string;
  description?: string;
  brand: string;
  category: string;
  price: number;
  stock_quantity: number;  // Version frontend
  image_url?: string;
  model?: string;
  color?: string;
  storage?: string;
  screen_size?: string;
  network_type?: string;
  created_at?: string;
  updated_at?: string;
}

export type Product = ProductBase; // Pour le backend
export type ProductAlias = ProductFrontend; // Pour le frontend

// page-response.ts
export interface PageResponse<T> {
  content: T[];          // Les données (liste d'éléments)
  totalElements: number; // Nombre total d'éléments
  totalPages: number;    // Nombre total de pages
  size: number;          // Taille de la page
  number: number;        // Numéro de page actuel
}

// product-search-criteria.ts
export interface ProductSearchCriteria {
  name?: string | null;
  brand?: string | null;
  category?: string | null;
  priceMin?: number | null;
  priceMax?: number | null;
  storage?: string | null;
}