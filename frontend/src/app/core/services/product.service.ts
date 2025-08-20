import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../../models/product';

export interface DropdownData {
  brands: string[];
  categories: string[];
  colors: string[];
  storageOptions: string[];
}

export interface SearchCriteria {
  name?: string;
  brand?: string;
  category?: string;
  priceMin?: number | null;
  priceMax?: number | null;
  storage?: string;
  model?: string;
}

export interface SearchRequest {
  criteria: SearchCriteria;
  page: number;
  size: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly baseUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  private getHttpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      })
    };
  }

  /** Récupérer tous les produits */
  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.baseUrl);
  }

  /** Récupérer un produit par son ID */
  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  /** Créer un produit */
  create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product, this.getHttpOptions());
  }

  /** Mettre à jour un produit */
  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product, this.getHttpOptions());
  }

  /** Supprimer un produit */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /** Récupérer les données pour les menus déroulants */
  getDropdownData(): Observable<DropdownData> {
    return this.http.get<DropdownData>(`${this.baseUrl}/dropdown-data`);
  }

  /** Recherche avec pagination */
  searchProducts(searchRequest: SearchRequest): Observable<PageResponse<Product>> {
    // On sécurise les valeurs pour éviter les erreurs si un champ est null ou undefined
    const safeRequest: SearchRequest = {
      criteria: {
        name: searchRequest.criteria?.name || '',
        brand: searchRequest.criteria?.brand || '',
        category: searchRequest.criteria?.category || '',
        priceMin: searchRequest.criteria?.priceMin ?? null,
        priceMax: searchRequest.criteria?.priceMax ?? null,
        storage: searchRequest.criteria?.storage || '',
        model: searchRequest.criteria?.model || ''
      },
      page: searchRequest.page ?? 0,
      size: searchRequest.size ?? 10
    };

    console.log('Making search request:', safeRequest);

    return this.http.post<PageResponse<Product>>(
      `${this.baseUrl}/search`,
      safeRequest,
      this.getHttpOptions()
    );
  }
}
