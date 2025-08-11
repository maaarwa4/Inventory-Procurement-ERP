import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from '../../models/product';
import { ProductSearchCriteria } from '../../models/product-search-criteria';
import { PageResponse } from '../../models/page-response';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer tous les produits
  getProducts(page: number = 0, size: number = 12, search: string = ''): Observable<PageResponse<Product>> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      map(products => ({
        content: products,
        totalElements: products.length,
        totalPages: Math.ceil(products.length / size),
        size: size,
        number: page
      }))
    );
  }
  
  private generateRandomImageUrl(productName: string): string {
    const baseUrl = 'https://source.unsplash.com/random/300x200/?';
    return `${baseUrl}${encodeURIComponent(productName)}`;
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getDropdownData(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dropdown-data`);
  }

  // Méthode de recherche corrigée
  searchProducts(criteria: ProductSearchCriteria, page: number, size: number): Observable<PageResponse<Product>> {
    // Construire les paramètres de requête
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    // Nettoyer les critères - remplacer les chaînes vides par null
    const cleanedCriteria = {
      name: criteria.name?.trim() || null,
      brand: criteria.brand?.trim() || null,
      category: criteria.category?.trim() || null,
      priceMin: criteria.priceMin,
      priceMax: criteria.priceMax,
      storage: criteria.storage?.trim() || null
    };

    console.log('Sending search request:', cleanedCriteria, 'Page:', page, 'Size:', size);

    return this.http.post<PageResponse<Product>>(`${this.apiUrl}/search`, cleanedCriteria, { params });
  }
}